package com.app.spacenow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility // Icono
import androidx.compose.material.icons.filled.VisibilityOff // Icono
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
@Composable
fun PasswordVisibilityToggle(
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    IconButton(onClick = onToggleVisibility) {
        Icon(
            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
        )
    }
}