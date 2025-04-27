package com.app.spacenow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.spacenow.ui.utils.ValidationUtils

@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Errores
    var nameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Nombre
        TextField(
            value = name,
            onValueChange = {
                name = it
                nameError = ValidationUtils.validateNameOrLastName(it)
            },
            label = { Text("Nombre") },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Apellido
        TextField(
            value = lastName,
            onValueChange = {
                lastName = it
                lastNameError = ValidationUtils.validateNameOrLastName(it)
            },
            label = { Text("Apellido") },
            isError = lastNameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (lastNameError != null) Text(lastNameError!!, color = MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Correo
        TextField(
            value = email,
            onValueChange = {
                email = it
                emailError = ValidationUtils.validateEmail(it)
            },
            label = { Text("Correo Electrónico") },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        
        if (emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Contraseña
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Confirmar Contraseña
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Registro
        Button(
            onClick = {
                nameError = ValidationUtils.validateNameOrLastName(name)
                lastNameError = ValidationUtils.validateNameOrLastName(lastName)
                emailError = ValidationUtils.validateEmail(email)

                if (nameError == null && lastNameError == null) {
                    // TODO: Implementar lógica de registro
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
    }
}

