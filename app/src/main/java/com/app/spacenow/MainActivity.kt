package com.app.spacenow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.spacenow.ui.screens.AuthScreen
import com.app.spacenow.ui.screens.DashboardScreen
import com.app.spacenow.ui.screens.RegisterScreen
import com.app.spacenow.ui.screens.PromoteUserScreen
import com.app.spacenow.ui.theme.SpaceNowTheme
import com.app.spacenow.ui.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
            val navController = rememberNavController()

            SpaceNowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (isAuthenticated) "dashboard" else "auth"
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
                                authViewModel = authViewModel
                            )
                        }

                        composable("promote-users") {
                            PromoteUserScreen(
                                authViewModel = authViewModel
                            )
                        }

                        // Rutas adicionales (stubs para futuras pantallas)
                        composable("active-reservations") {
                            Text("Reservas Activas")
                        }
                        composable("reservation-stats") {
                            Text("Estadísticas de Reservas")
                        }
                        composable("my-reservations") {
                            Text("Mis Espacios Reservados")
                        }

                        composable("register") {
                            RegisterScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
