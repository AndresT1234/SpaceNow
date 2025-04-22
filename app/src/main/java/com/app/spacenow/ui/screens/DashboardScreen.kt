package com.app.spacenow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.spacenow.R
import com.app.spacenow.data.model.Space
import com.app.spacenow.data.model.Reservation
import com.app.spacenow.ui.theme.AvailableGreen
import com.app.spacenow.ui.viewmodels.DashboardViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                if (isAdmin) {
                    // Menú para administradores
                    NavigationDrawerItem(
                        label = { Text("Reservas activas") },
                        selected = false,
                        onClick = { /* TODO: Implementar navegación */ }
                    )
                    NavigationDrawerItem(
                        label = { Text("Frecuencia de reservas") },
                        selected = false,
                        onClick = { /* TODO: Implementar navegación */ }
                    )
                } else {
                    // Menú para usuarios normales
                    NavigationDrawerItem(
                        label = { Text("Espacios disponibles") },
                        selected = true,
                        onClick = { /* Estamos en esta vista */ }
                    )
                    NavigationDrawerItem(
                        label = { Text("Mis espacios reservados") },
                        selected = false,
                        onClick = { /* TODO: Implementar navegación */ }
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
                    onClick = { /* TODO: Implementar cierre de sesión */ }
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
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(allActiveReservations) { reservation -> 
                        AdminReservationItem(reservation = reservation)
                    }

                    item {
                        Text(
                            text = "Estadísticas de reservas",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(vertical = 8.dp)
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
                            modifier = Modifier.padding(vertical = 8.dp)
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
                                // Si solo hay un elemento en la última fila, añadir un espaciador
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
                            modifier = Modifier.padding(vertical = 8.dp)
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

@Composable
fun AdminReservationItem(reservation: Reservation) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reservation.spaceName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Fecha: ${dateFormatter.format(reservation.dateTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Usuario: ${reservation.userId}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun StatisticsChart(spaceStatistics: Map<String, Int>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            spaceStatistics.forEach { (spaceName, count) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = spaceName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    LinearProgressIndicator(
                        progress = count.toFloat() / spaceStatistics.values.maxOrNull()!!.toFloat(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .height(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReservationItem(
    reservation: Reservation,
    onEditClick: (Reservation) -> Unit,
    onDeleteClick: (Reservation) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reservation.spaceName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Fecha: ${dateFormatter.format(reservation.dateTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                IconButton(onClick = { onEditClick(reservation) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Modificar reserva")
                }
                IconButton(onClick = { onDeleteClick(reservation) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar reserva",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun SpaceCard(
    space: Space,
    onSpaceClick: (Space) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSpaceClick(space) }
    ) {
        Column {
            AsyncImage(
                model = space.imageResource,
                contentDescription = space.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = space.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = space.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Capacidad: ${space.capacity} personas",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
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
        }
    }
}