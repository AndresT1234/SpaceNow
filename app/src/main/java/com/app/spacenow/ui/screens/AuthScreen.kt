package com.app.spacenow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.spacenow.ui.components.PrimaryButton
import com.app.spacenow.ui.components.TextFieldInput
import com.app.spacenow.ui.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate("dashboard") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title()

        Spacer(modifier = Modifier.height(32.dp))

        EmailInput(email) { email = it }

        Spacer(modifier = Modifier.height(16.dp))

        PasswordInput(password) { password = it }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Iniciar Sesión",
            modifier = Modifier.fillMaxWidth(),
            onClick = { authViewModel.login(email, password) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PrimaryButton(
            text = "Registrarse",
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate("register") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            ErrorMessage(it)
        }
    }
}

@Composable
private fun Title() {
    Text(
        text = "Bienvenido a SpaceNow",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun EmailInput(value: String, onValueChange: (String) -> Unit) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Correo electrónico",
        keyboardType = KeyboardType.Email,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordInput(value: String, onValueChange: (String) -> Unit) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Contraseña",
        keyboardType = KeyboardType.Password,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}


