package com.app.spacenow.ui.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.regex.Pattern
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    fun login(email: String, password: String) {
        validateCredentials(email, password)
    }

    private fun validateCredentials(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Todos los campos son obligatorios."
            _isAuthenticated.value = false
            return
        }

        if (!emailPattern.matcher(email).matches()) {
            _errorMessage.value = "Correo electrónico inválido."
            _isAuthenticated.value = false
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres."
            _isAuthenticated.value = false
            return
        }

        _errorMessage.value = null
        _isAuthenticated.value = true
    }

    fun register(name: String, lastName: String, email: String, phoneNumber: String, password: String) {
        validateRegistration(name, lastName, email, phoneNumber, password)
    }

    private fun validateRegistration(name: String, lastName: String, email: String, phoneNumber: String, password: String) {
        if (name.isBlank() || lastName.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank()) {
            _errorMessage.value = "Todos los campos son obligatorios."
            _isAuthenticated.value = false
            return
        }

        if (!emailPattern.matcher(email).matches()) {
            _errorMessage.value = "Correo electrónico inválido."
            _isAuthenticated.value = false
            return
        }

        if (!phoneNumber.matches(Regex("^\\d{10}$"))) {
            _errorMessage.value = "Número de teléfono inválido."
            _isAuthenticated.value = false
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres."
            _isAuthenticated.value = false
            return
        }

        _errorMessage.value = null
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
        _errorMessage.value = null
    }
}
