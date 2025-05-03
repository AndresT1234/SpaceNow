package com.app.spacenow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.spacenow.ui.screens.*
import com.app.spacenow.ui.theme.SpaceNowTheme
import com.app.spacenow.ui.viewmodels.AuthViewModel
import com.app.spacenow.ui.viewmodels.DashboardViewModel
import com.app.spacenow.ui.viewmodels.ReservationViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val reservationViewModel: ReservationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
            val navController = rememberNavController()
            
            // Collect states
            val spaces by dashboardViewModel.spaces.collectAsState()
            val reservations by dashboardViewModel.reservations.collectAsState()

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
                                dashboardViewModel = dashboardViewModel,
                                authViewModel = authViewModel
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }

                        composable(
                            route = "reservation-form?spaceId={spaceId}&reservationId={reservationId}",
                            arguments = listOf(
                                navArgument("spaceId") { 
                                    type = NavType.StringType
                                    nullable = true 
                                },
                                navArgument("reservationId") { 
                                    type = NavType.StringType
                                    nullable = true 
                                }
                            )
                        ) { backStackEntry ->
                            val spaceId = backStackEntry.arguments?.getString("spaceId")
                            val reservationId = backStackEntry.arguments?.getString("reservationId")
                            
                            val space = spaceId?.let { id ->
                                spaces.find { it.id == id }
                            }
                            
                            val reservation = reservationId?.let { id ->
                                reservations.find { it.id == id }
                            }

                            ReservationFormScreen(
                                navController = navController,
                                space = space,
                                existingReservation = reservation,
                                reservationViewModel = reservationViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
