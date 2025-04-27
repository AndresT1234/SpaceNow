package com.app.spacenow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.spacenow.ui.utils.ValidationUtils
import com.app.spacenow.ui.components.PrimaryButton
import com.app.spacenow.ui.components.TextFieldInput

@Composable
fun RegisterScreen(navController: NavController) {
    // Variables de estado
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Errores de validación
    var nameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Title()

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de Nombre
        NameInput(value = name, onValueChange = {
            name = it
            nameError = ValidationUtils.validateNameOrLastName(it)
        }, nameError = nameError)

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Apellido
        LastNameInput(value = lastName, onValueChange = {
            lastName = it
            lastNameError = ValidationUtils.validateNameOrLastName(it)
        }, lastNameError = lastNameError)

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Correo Electrónico
        EmailInput(value = email, onValueChange = {
            email = it
            emailError = ValidationUtils.validateEmail(it)
        }, emailError = emailError)

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Número de Teléfono
        PhoneNumberInput(value = phoneNumber, onValueChange = {
            phoneNumber = it
            phoneError = ValidationUtils.validatePhoneNumber(it)
        }, phoneError = phoneError)

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Contraseña
        PasswordInput(value = password, onValueChange = {
            password = it
            passwordError = ValidationUtils.validatePassword(it)
        }, passwordError = passwordError, passwordVisible = passwordVisible)

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Confirmar Contraseña
        ConfirmPasswordInput(value = confirmPassword, onValueChange = {
            confirmPassword = it
            confirmPasswordError = ValidationUtils.validateConfirmPassword(password, it)
        }, confirmPasswordError = confirmPasswordError, passwordVisible = passwordVisible)

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Registro
        PrimaryButton(
            text = "Registro",
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validación final
                nameError = ValidationUtils.validateNameOrLastName(name)
                lastNameError = ValidationUtils.validateNameOrLastName(lastName)
                emailError = ValidationUtils.validateEmail(email)
                phoneError = ValidationUtils.validatePhoneNumber(phoneNumber)
                passwordError = ValidationUtils.validatePassword(password)
                confirmPasswordError = ValidationUtils.validateConfirmPassword(password, confirmPassword)

                // Verificar si no hay errores
                if (nameError == null && lastNameError == null && emailError == null &&
                    phoneError == null && passwordError == null && confirmPasswordError == null) {

                    // authViewModel.register(name, lastName, email, phoneNumber, password)
                    // navController.navigate("dashboard") {
                    //    popUpTo("register") { inclusive = true }
                    //}
                }
            }
        )
    }
}

@Composable
private fun Title() {
    Text(
        text = "Registro de Usuario",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun NameInput(value: String, onValueChange: (String) -> Unit, nameError: String?) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Nombre",
        keyboardType = KeyboardType.Text,
        modifier = Modifier.fillMaxWidth()
    )
    nameError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LastNameInput(value: String, onValueChange: (String) -> Unit, lastNameError: String?) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Apellido",
        keyboardType = KeyboardType.Text,
        modifier = Modifier.fillMaxWidth()
    )
    lastNameError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun EmailInput(value: String, onValueChange: (String) -> Unit, emailError: String?) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Correo Electrónico",
        keyboardType = KeyboardType.Email,
        modifier = Modifier.fillMaxWidth()
    )
    emailError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun PhoneNumberInput(value: String, onValueChange: (String) -> Unit, phoneError: String?) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Número de Teléfono",
        keyboardType = KeyboardType.Phone,
        modifier = Modifier.fillMaxWidth()
    )
    phoneError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun PasswordInput(value: String, onValueChange: (String) -> Unit, passwordError: String?, passwordVisible: Boolean) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Contraseña",
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    passwordError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ConfirmPasswordInput(value: String, onValueChange: (String) -> Unit, confirmPasswordError: String?, passwordVisible: Boolean) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Confirmar Contraseña",
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    confirmPasswordError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


