package com.app.spacenow.ui.utils

object ValidationUtils {

    // Validar Nombre y Apellido
    fun validateNameOrLastName(input: String): String? {
        val trimmedInput = input.trim()
        return when {
            trimmedInput.isEmpty() -> "Este campo es obligatorio."
            trimmedInput.length < 2 -> "Debe tener al menos 2 caracteres."
            trimmedInput.length > 50 -> "Debe tener menos de 50 caracteres."
            !trimmedInput.matches(Regex("^[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ\\s]+$")) -> "Solo se permiten letras y espacios."
            else -> null
        }
    }

    // Validar Correo Electrónico
    fun validateEmail(email: String): String? {
        val trimmedEmail = email.trim()
        return when {
            trimmedEmail.isEmpty() -> "El correo es obligatorio."
            !trimmedEmail.contains("@") -> "El correo debe contener '@'."
            !trimmedEmail.matches(Regex("^[\\w._%+-]+@[\\w.-]+\\.(com|co)$")) -> "El correo debe terminar en '.com' o '.co'."
            else -> null
        }
    }

    // Validar Contraseña
    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "La contraseña es obligatoria."
            password.length < 8 -> "Debe tener al menos 8 caracteres."
            !password.matches(Regex(".*[A-Z].*")) -> "Debe contener al menos una letra mayúscula."
            !password.matches(Regex(".*[a-z].*")) -> "Debe contener al menos una letra minúscula."
            !password.matches(Regex(".*\\d.*")) -> "Debe contener al menos un número."
            !password.matches(Regex(".*[!@#\$%^&*].*")) -> "Debe contener al menos un símbolo (!@#\$%^&*)."
            else -> null
        }
    }
}