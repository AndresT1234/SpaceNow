package com.app.spacenow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.spacenow.ui.screens.*
import com.app.spacenow.ui.theme.SpaceNowTheme
import com.app.spacenow.ui.viewmodels.*

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val reservationViewModel: ReservationViewModel by viewModels()
    private val spaceViewModel: SpaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permitir que reservationViewModel acceda a dashboardViewModel
        reservationViewModel.dashboardViewModel = dashboardViewModel
        spaceViewModel.dashboardViewModel = dashboardViewModel

        setContent {
            val navController = rememberNavController()
            val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

            // Collect additional state
            val spaces by dashboardViewModel.spaces.collectAsState()
            val reservations by dashboardViewModel.reservations.collectAsState()
            val isAdmin by dashboardViewModel.isAdmin.collectAsState()

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

                        composable("register") {
                            RegisterScreen(
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

                        composable("promote-users") {
                            PromoteUserScreen(
                                authViewModel = authViewModel
                            )
                        }

                        composable(
                            route = "form?spaceId={spaceId}&reservationId={reservationId}&mode={mode}",
                            arguments = listOf(
                                navArgument("spaceId") {
                                    type = NavType.StringType
                                    nullable = true
                                },
                                navArgument("reservationId") {
                                    type = NavType.StringType
                                    nullable = true
                                },
                                navArgument("mode") {
                                    type = NavType.StringType
                                    defaultValue = "reservation"
                                }
                            )
                        ) { backStackEntry ->
                            val spaceId = backStackEntry.arguments?.getString("spaceId")
                            val reservationId = backStackEntry.arguments?.getString("reservationId")
                            val mode = backStackEntry.arguments?.getString("mode") ?: "reservation"

                            val spaces by dashboardViewModel.spaces.collectAsState()

                            val space = spaceId?.let { id ->
                                spaces.find { it.id == id }
                            }

                            val reservation = reservationId?.let { id ->
                                reservations.find { it.id == id }
                            }

                            FormScreen(
                                navController = navController,
                                isAdmin = mode == "space",
                                space = space,
                                existingReservation = reservation,
                                reservationViewModel = reservationViewModel,
                                spaceViewModel = spaceViewModel,
                                spaces = spaces
                            )
                        }
                    }
                }
            }
        }
    }
}