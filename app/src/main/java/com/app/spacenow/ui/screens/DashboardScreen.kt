package com.app.spacenow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.app.spacenow.R
import com.app.spacenow.data.model.Space
import com.app.spacenow.ui.viewmodels.DashboardViewModel
import com.app.spacenow.ui.components.*
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    navController: NavHostController,
    onSpaceClick: (Space) -> Unit
) {
    val spaces by viewModel.spaces.collectAsState()
    val reservations by viewModel.reservations.collectAsState()
    val allActiveReservations by viewModel.allActiveReservations.collectAsState()
    val spaceStatistics by viewModel.spaceStatistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // Referencias para el scroll
    val availableSpacesRef = remember { mutableStateOf(0) }
    val myReservationsRef = remember { mutableStateOf(0) }
    val activeReservationsRef = remember { mutableStateOf(0) }
    val statisticsRef = remember { mutableStateOf(0) }

    var selectedItem by remember { mutableStateOf("spaces") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                if (isAdmin) {
                    NavigationDrawerItem(
                        label = { Text("Reservas activas") },
                        selected = selectedItem == "active",
                        onClick = {
                            scope.launch {
                                selectedItem = "active"
                                drawerState.close()
                                lazyListState.scrollToItem(activeReservationsRef.value)
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    NavigationDrawerItem(
                        label = { Text("Frecuencia de reservas") },
                        selected = selectedItem == "stats",
                        onClick = {
                            scope.launch {
                                selectedItem = "stats"
                                drawerState.close()
                                lazyListState.scrollToItem(statisticsRef.value)
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                } else {
                    NavigationDrawerItem(
                        label = { Text("Espacios disponibles") },
                        selected = selectedItem == "spaces",
                        onClick = {
                            scope.launch {
                                selectedItem = "spaces"
                                drawerState.close()
                                lazyListState.scrollToItem(availableSpacesRef.value)
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    NavigationDrawerItem(
                        label = { Text("Mis espacios reservados") },
                        selected = selectedItem == "reservations",
                        onClick = {
                            scope.launch {
                                selectedItem = "reservations"
                                drawerState.close()
                                lazyListState.scrollToItem(myReservationsRef.value)
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.error,
                        unselectedTextColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            // TODO: Implementar lógica de cierre de sesión
                        }
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = "SpaceNow Logo",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SpaceNow")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isAdmin) {
                    // Admin Dashboard Content
                    item {
                        Text(
                            text = "Reservas activas",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { coordinates ->
                                    activeReservationsRef.value = 0
                                }
                        )
                    }

                    items(allActiveReservations) { reservation -> 
                        AdminReservationItem(reservation = reservation)
                    }

                    item {
                        Text(
                            text = "Estadísticas de reservas",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { coordinates ->
                                    statisticsRef.value = allActiveReservations.size + 1
                                }
                        )
                    }

                    item {
                        StatisticsChart(spaceStatistics = spaceStatistics)
                    }
                } else {
                    // Regular User Dashboard Content
                    item {
                        Text(
                            text = "Espacios disponibles",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { coordinates ->
                                    availableSpacesRef.value = 0
                                }
                        )
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        items(spaces.chunked(2)) { spaceRow ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                spaceRow.forEach { space ->
                                    SpaceCard(
                                        space = space,
                                        onSpaceClick = onSpaceClick,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (spaceRow.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Mis espacios reservados",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { coordinates ->
                                    myReservationsRef.value = spaces.size + 2
                                }
                        )
                    }

                    items(reservations) { reservation ->
                        ReservationItem(
                            reservation = reservation,
                            onEditClick = { viewModel.modifyReservation(it.id, Date()) },
                            onDeleteClick = { viewModel.deleteReservation(it.id) }
                        )
                    }
                }
            }
        }
    }
}