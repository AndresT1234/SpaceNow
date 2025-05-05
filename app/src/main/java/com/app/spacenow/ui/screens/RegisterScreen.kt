package com.app.spacenow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.app.spacenow.ui.utils.ValidationUtils
import com.app.spacenow.ui.components.PrimaryButton
import com.app.spacenow.ui.components.TextFieldInput
import com.app.spacenow.ui.components.PasswordVisibilityToggle
import com.app.spacenow.ui.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
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

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate("dashboard") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Title()

        Spacer(modifier = Modifier.height(32.dp))

        NameInput(value = name, onValueChange = {
            name = it
            nameError = ValidationUtils.validateNameOrLastName(it)
        }, nameError = nameError)

        Spacer(modifier = Modifier.height(8.dp))

        LastNameInput(value = lastName, onValueChange = {
            lastName = it
            lastNameError = ValidationUtils.validateNameOrLastName(it)
        }, lastNameError = lastNameError)

        Spacer(modifier = Modifier.height(8.dp))

        EmailInput(value = email, onValueChange = {
            email = it
            emailError = ValidationUtils.validateEmail(it)
        }, emailError = emailError)

        Spacer(modifier = Modifier.height(8.dp))

        PhoneNumberInput(value = phoneNumber, onValueChange = {
            phoneNumber = it
            phoneError = ValidationUtils.validatePhoneNumber(it)
        }, phoneError = phoneError)

        Spacer(modifier = Modifier.height(8.dp))

        PasswordInput(value = password, onValueChange = {
            password = it
            passwordError = ValidationUtils.validatePassword(it)
        },
        passwordError = passwordError, 
        passwordVisible = passwordVisible, 
        onVisibilityToggle = { passwordVisible = !passwordVisible })

        Spacer(modifier = Modifier.height(8.dp))

        ConfirmPasswordInput(value = confirmPassword, onValueChange = {
            confirmPassword = it
            confirmPasswordError = ValidationUtils.validateConfirmPassword(password, it)
        },
        confirmPasswordError = confirmPasswordError, 
        passwordVisible = passwordVisible,
        onVisibilityToggle = { passwordVisible = !passwordVisible})

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Registrar",
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validar campos
                nameError = ValidationUtils.validateNameOrLastName(name)
                lastNameError = ValidationUtils.validateNameOrLastName(lastName)
                emailError = ValidationUtils.validateEmail(email)
                phoneError = ValidationUtils.validatePhoneNumber(phoneNumber)
                passwordError = ValidationUtils.validatePassword(password)
                confirmPasswordError = ValidationUtils.validateConfirmPassword(password, confirmPassword)

                if (nameError == null && lastNameError == null && emailError == null &&
                    phoneError == null && passwordError == null && confirmPasswordError == null) {

                    // Llamar al ViewModel para registrarse
                    authViewModel.register(name, lastName, email, phoneNumber, password)
                    navController.navigate("auth") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        /*
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
         */
    }
}

@Composable
private fun Title() {
    Text(
        text = "Registro de Usuario",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

// Inputs
@Composable
private fun NameInput(value: String, onValueChange: (String) -> Unit, nameError: String?) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Nombre",
        keyboardType = KeyboardType.Text,
        modifier = Modifier.fillMaxWidth()
    )
    nameError?.let { ErrorText(it) }
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
    lastNameError?.let { ErrorText(it) }
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
    emailError?.let { ErrorText(it) }
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
    phoneError?.let { ErrorText(it) }
}

@Composable
private fun PasswordInput(value: String, onValueChange: (String) -> Unit, passwordError: String?, passwordVisible: Boolean,
                            onVisibilityToggle: () -> Unit ) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Contraseña",
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            PasswordVisibilityToggle(passwordVisible, onVisibilityToggle) // Icono para mostrar/ocultar contraseña
        },
        modifier = Modifier.fillMaxWidth()
    )
    passwordError?.let { ErrorText(it) }
}

@Composable
private fun ConfirmPasswordInput(value: String, onValueChange: (String) -> Unit, confirmPasswordError: String?, passwordVisible: Boolean,
                                  onVisibilityToggle: () -> Unit) {
    TextFieldInput(
        value = value,
        onValueChange = onValueChange,
        label = "Confirmar Contraseña",
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            PasswordVisibilityToggle(passwordVisible, onVisibilityToggle) // Icono para mostrar/ocultar contraseña
        },
        modifier = Modifier.fillMaxWidth()
    )
    confirmPasswordError?.let { ErrorText(it) }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}


