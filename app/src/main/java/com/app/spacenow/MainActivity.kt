package com.app.spacenow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.spacenow.ui.screens.AuthScreen
import com.app.spacenow.ui.screens.DashboardScreen
import com.app.spacenow.ui.screens.RegisterScreen
import com.app.spacenow.ui.theme.SpaceNowTheme
import com.app.spacenow.ui.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpaceNowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "auth"
                    ) {
                        composable("auth") {
                            AuthScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                navController = navController,
                                onSpaceClick = { /* TODO */ }
                            )
                        }
                        composable("active-reservations") {
                            // TODO: Implementar pantalla de reservas activas
                            Text("Reservas Activas")
                        }
                        composable("reservation-stats") {
                            // TODO: Implementar pantalla de estadísticas
                            Text("Estadísticas de Reservas")
                        }
                        composable("my-reservations") {
                            // TODO: Implementar pantalla de mis reservas
                            Text("Mis Espacios Reservados")
                        }
                        // Pagina de Registro
                        composable("register") {
                            RegisterScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}