package com.app.spacenow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.spacenow.ui.screens.DashboardScreen
import com.app.spacenow.ui.screens.RegisterScreen
import com.app.spacenow.ui.theme.SpaceNowTheme

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
                    
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard"
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                navController = navController,
                                onSpaceClick = { /* TODO: Implementar navegación al detalle */ }
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