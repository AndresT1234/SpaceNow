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
    
    // Validar Número de Teléfono
    fun validatePhoneNumber(phone: String): String? {
        val trimmedPhone = phone.trim()
        return when {
            trimmedPhone.isEmpty() -> "El número de teléfono es obligatorio."
            !trimmedPhone.matches(Regex("^\\d{10}$")) -> "Debe ser un número de 10 dígitos."
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

    // Validar Confirmación de Contraseña
    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Debe confirmar la contraseña."
            password != confirmPassword -> "Las contraseñas no coinciden."
            else -> null
        }
    }    
}