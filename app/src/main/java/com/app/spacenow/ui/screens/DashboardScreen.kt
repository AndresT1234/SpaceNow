package com.app.spacenow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.app.spacenow.R
import com.app.spacenow.ui.components.AdminReservationItem
import com.app.spacenow.ui.components.ReservationItem
import com.app.spacenow.ui.components.SpaceCard
import com.app.spacenow.ui.components.StatisticsChart
import com.app.spacenow.ui.theme.AvailableGreen
import com.app.spacenow.ui.viewmodels.AuthViewModel
import com.app.spacenow.ui.viewmodels.DashboardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.updateAdminStatus(authViewModel)
    }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate("auth") {
                popUpTo("dashboard") { inclusive = true }
            }
        }
    }

    // Dashboard state collectors
    val spaces by dashboardViewModel.spaces.collectAsState()
    val reservations by dashboardViewModel.reservations.collectAsState()
    val allActiveReservations by dashboardViewModel.allActiveReservations.collectAsState()
    val spaceStatistics by dashboardViewModel.spaceStatistics.collectAsState()
    val isLoading by dashboardViewModel.isLoading.collectAsState()
    val isAdmin by dashboardViewModel.isAdmin.collectAsState()

    // Drawer and scrolling
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Dialog & selection
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(if (isAdmin) "active" else "spaces") }

    // Scroll anchors
    val spacesRef = remember { mutableStateOf(0) }
    val reservedRef = remember { mutableStateOf(0) }
    val activeRef = remember { mutableStateOf(0) }
    val statsRef = remember { mutableStateOf(0) }

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
                                listState.scrollToItem(activeRef.value)
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Estadísticas") },
                        selected = selectedItem == "stats",
                        onClick = {
                            scope.launch {
                                selectedItem = "stats"
                                drawerState.close()
                                listState.scrollToItem(statsRef.value)
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Promover Usuarios") },
                        selected = selectedItem == "promote",
                        onClick = {
                            scope.launch {
                                selectedItem = "promote"
                                drawerState.close()
                                navController.navigate("promote-users")
                            }
                        }
                    )

                } else {
                    NavigationDrawerItem(
                        label = { Text("Nueva Reserva") },
                        selected = false,
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("form?mode=reservation")
                            }
                        }
                    )
                    
                    NavigationDrawerItem(
                        label = { Text("Espacios disponibles") },
                        selected = selectedItem == "spaces",
                        onClick = {
                            scope.launch {
                                selectedItem = "spaces"
                                drawerState.close()
                                listState.scrollToItem(spacesRef.value)
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Mis Reservas") },
                        selected = selectedItem == "reservations",
                        onClick = {
                            scope.launch {
                                selectedItem = "reservations"
                                drawerState.close()
                                listState.scrollToItem(reservedRef.value)
                            }
                        }
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
                    onClick = { showLogoutDialog = true }
                )

                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = { Text("Confirmación", style = TextStyle(fontSize = 20.sp)) },
                        text = { Text("¿Deseas cerrar sesión?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showLogoutDialog = false
                                scope.launch {
                                    drawerState.close()
                                    authViewModel.logout()
                                }
                            }) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_logo),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("SpaceNow", fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        if (isAdmin) {
                            IconButton(onClick = { 
                                navController.navigate("form?mode=space") 
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Agregar espacio")
                            }
                        }
                    }
                )
            },
            // Boton flotante de acción (FAB) para agregar reservas
            /* 
            floatingActionButton = {
                if (!isAdmin) {
                    FloatingActionButton(
                        onClick = { navController.navigate("form?mode=reservation") }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva Reserva")
                    }
                }
            }
            */
        ) { padding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isAdmin) {
                    item {
                        Text(
                            text = "Reservas activas",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { activeRef.value = 0 }
                        )
                    }
                    items(allActiveReservations) { res ->
                        AdminReservationItem(reservation = res)
                    }
                    item {
                        Text(
                            text = "Estadísticas",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { statsRef.value = allActiveReservations.size + 1 }
                        )
                    }
                    item { StatisticsChart(spaceStatistics) }
                    item {
                        Text(
                            text = "Espacios disponibles",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { spacesRef.value = 0 }
                        )
                    }





                    items(spaces) { space ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    navController.navigate("form?spaceId=${space.id}&mode=space")
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = space.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Capacidad: ${space.capacity} personas",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (space.available) {
                                        Surface(
                                            color = AvailableGreen,
                                            shape = MaterialTheme.shapes.small,
                                            modifier = Modifier.padding(top = 8.dp)
                                        ) {
                                            Text(
                                                text = "Disponible",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { 
                                        navController.navigate("form?spaceId=${space.id}&mode=space")
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar espacio")
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Espacios disponibles",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { spacesRef.value = 0 }
                        )
                    }
                    if (isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        items(spaces.chunked(2)) { row ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { space ->
                                    SpaceCard(
                                        space = space,
                                        onSpaceClick = { 
                                            navController.navigate("form?spaceId=${space.id}&mode=reservation")
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    item {
                        Text(
                            text = "Mis Reservas",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { reservedRef.value = spaces.size + 2 }
                        )
                    }
                    items(reservations) { res ->
                        ReservationItem(
                            reservation = res,
                            onEditClick = { 
                                navController.navigate("form?reservationId=${res.id}&mode=reservation")
                            },
                            onDeleteClick = { dashboardViewModel.deleteReservation(res.id) }
                        )
                    }
                }
            }
        }
    }
}


