package com.app.spacenow.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class UserRole {
    USER, ADMIN
}

data class UserData(
    val isAuthenticated: Boolean = false,
    val role: UserRole = UserRole.USER
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _userRole = MutableStateFlow("user")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    fun setUserRole(role: String) {
        _userRole.value = role
    }

    fun promoteToAdmin(userId: String) {
        if (_userData.value.role != UserRole.ADMIN) {
            _errorMessage.value = "Permiso denegado. Solo los administradores pueden promover usuarios."
            return
        }

        viewModelScope.launch {
            try {
                db.collection("users").document(userId).update("rol", "admin").await()
                _errorMessage.value = "El usuario ha sido promovido a administrador."
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al promover al usuario."
            }
        }
    }

    private suspend fun getUserRoleFromFirestore(userId: String): UserRole {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            val role = snapshot.getString("rol")
            if (role == "admin") UserRole.ADMIN else UserRole.USER
        } catch (e: Exception) {
            UserRole.USER
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null && user.isEmailVerified) {
                    val role = getUserRoleFromFirestore(user.uid)
                    _isAuthenticated.value = true
                    _userData.value = UserData(isAuthenticated = true, role = role)
                    _userRole.value = if (role == UserRole.ADMIN) "admin" else "user"
                    _errorMessage.value = "Has iniciado sesión correctamente."

                } else {
                    _errorMessage.value = "Verifica tu correo electrónico antes de iniciar sesión."
                    auth.signOut()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Credenciales no válidas. Verifica tu correo y contraseña."
            }
        }
    }

    fun register(name: String, lastName: String, email: String, phoneNumber: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: return@launch

                val userMap = hashMapOf(
                    "rol" to "user",
                    "name" to name,
                    "lastName" to lastName,
                    "email" to email,
                    "phoneNumber" to phoneNumber
                )

                db.collection("users").document(userId).set(userMap).await()
                auth.currentUser?.sendEmailVerification()?.await()

                _isAuthenticated.value = false
                _userData.value = UserData(isAuthenticated = false, role = UserRole.USER)
                _userRole.value = "user"
                _errorMessage.value = "Usuario registrado. Por favor verifica tu correo electrónico."
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al registrar usuario."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggingOut.value = true
            try {
                auth.signOut()
                _isAuthenticated.value = false
                _userData.value = UserData(isAuthenticated = false, role = UserRole.USER)
                _userRole.value = "user"
                _errorMessage.value = "Has cerrado sesión correctamente."
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al cerrar sesión."
            } finally {
                _isLoggingOut.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}