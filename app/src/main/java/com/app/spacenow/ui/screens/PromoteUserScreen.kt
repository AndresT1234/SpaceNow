package com.app.spacenow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.spacenow.data.model.User
import com.app.spacenow.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.*

@Composable
fun PromoteUserScreen(authViewModel: AuthViewModel) {
    var users by remember { mutableStateOf(listOf<User>()) }
    val errorMessage by authViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.viewModelScope.launch {
            try {
                val snapshot = authViewModel.db.collection("users").get().await()
                users = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                authViewModel._errorMessage.value = "Error al cargar usuarios."
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

        Spacer(modifier = Modifier.height(10.dp))

        Title()

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(users) { user ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(user.name, modifier = Modifier.weight(1f))
                    if (user.rol != "admin") {
                        Button(onClick = { authViewModel.promoteToAdmin(user.id) }) {
                            Text("Promover a Admin")
                        }
                    } else {
                        Text("Administrador", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun Title() {
    Text(
        text = "Promover Users a Admins",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}
