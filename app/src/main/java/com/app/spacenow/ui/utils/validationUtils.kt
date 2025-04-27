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
}