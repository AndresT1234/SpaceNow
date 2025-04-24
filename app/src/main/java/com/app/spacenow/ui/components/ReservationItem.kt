package com.app.spacenow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.spacenow.data.model.Reservation
import java.text.SimpleDateFormat
import java.util.*

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