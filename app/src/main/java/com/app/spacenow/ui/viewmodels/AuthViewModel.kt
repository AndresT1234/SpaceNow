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
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null && user.isEmailVerified) {
                    _isAuthenticated.value = true
                    _errorMessage.value = "Has iniciado sesión correctamente."
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
    }
}
