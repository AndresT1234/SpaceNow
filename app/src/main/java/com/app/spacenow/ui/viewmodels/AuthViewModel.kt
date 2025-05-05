package com.app.spacenow.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    fun promoteToAdmin(userId: String) {
        if (_userRole.value != "admin") {
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

    fun fetchUserRole(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").document(userId).get().await()
                val role = snapshot.getString("rol")
                _userRole.value = role
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener el rol del usuario."
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null && user.isEmailVerified) {
                    _isAuthenticated.value = true
                    _errorMessage.value = "Haas iniciado sesión coreectamente."

                    fetchUserRole(user.uid)
                } else {
                    _errorMessage.value = "Por favor verifica tu correo electrónico antes de iniciar sesión."
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

                // ENVIAR CORREO DE VERIFICACIÓN
                auth.currentUser?.sendEmailVerification()?.await()

                _isAuthenticated.value = false // Aún no puede entrar hasta verificar
                _errorMessage.value = "Usuario registrado. Por favor verifica tu correo electrónico."
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al registrar usuario."
            }
        }
    }

    fun logout() {
        auth.signOut()
        _isAuthenticated.value = false
        _errorMessage.value = "Has cerrado sesión correctamente."
    }
}
