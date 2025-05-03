package com.app.spacenow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.spacenow.data.model.Reservation
import com.app.spacenow.data.model.Space
import com.app.spacenow.ui.components.PrimaryButton
import com.app.spacenow.ui.components.TextFieldInput
import com.app.spacenow.ui.viewmodels.ReservationViewModel
import com.app.spacenow.ui.viewmodels.SpaceViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.app.TimePickerDialog
import android.app.DatePickerDialog as AndroidDatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    isAdmin: Boolean,
    space: Space? = null,
    existingReservation: Reservation? = null,
    reservationViewModel: ReservationViewModel = viewModel(),
    spaceViewModel: SpaceViewModel = viewModel()
) {
    // Estados para el formulario
    var name by remember { mutableStateOf(space?.name ?: "") }
    var description by remember { mutableStateOf(space?.description ?: "") }
    var capacity by remember { mutableStateOf(space?.capacity?.toString() ?: "") }
    var showSpaceDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Estados del ViewModel según el modo
    val selectedDate by reservationViewModel.selectedDate.collectAsState()
    val selectedSpace by reservationViewModel.selectedSpace.collectAsState()
    val errorMessage by if (isAdmin) spaceViewModel.errorMessage.collectAsState() else reservationViewModel.errorMessage.collectAsState()

    // Inicializar datos si es una edición
    LaunchedEffect(space, existingReservation) {
        if (isAdmin) {
            space?.let { 
                name = it.name
                description = it.description
                capacity = it.capacity.toString()
            }
        } else {
            space?.let { reservationViewModel.setSelectedSpace(it) }
            existingReservation?.let { 
                reservationViewModel.setSelectedSpace(Space(
                    id = it.spaceId,
                    name = it.spaceName,
                    description = "",
                    capacity = 0,
                    available = true,
                    imageResource = 0
                ))
                reservationViewModel.setSelectedDate(it.dateTime)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when {
                            isAdmin && space != null -> "Editar Espacio"
                            isAdmin -> "Nuevo Espacio"
                            existingReservation != null -> "Modificar Reserva"
                            else -> "Nueva Reserva"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding -> 
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isAdmin) {
                // Formulario de Espacio
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Nombre del espacio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = "Descripción") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text(text = "Capacidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Formulario de Reserva
                Text(
                    text = "¿Qué espacio quieres reservar?",
                    style = MaterialTheme.typography.titleMedium
                )

                if (space == null) {
                    SpaceSelector(
                        selectedSpace = selectedSpace,
                        onSpaceSelected = { reservationViewModel.setSelectedSpace(it) },
                        showDropdown = showSpaceDropdown,
                        onDropdownDismiss = { showSpaceDropdown = false },
                        onDropdownShow = { showSpaceDropdown = true }
                    )
                } else {
                    Text(
                        text = "Espacio: ${space.name}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    text = "¿Cuándo lo quieres usar?",
                    style = MaterialTheme.typography.titleMedium
                )

                DateTimeSelector(
                    selectedDate = selectedDate,
                    onDateTimeSelected = { date ->
                        reservationViewModel.setSelectedDate(date)
                    },
                    context = context
                )
            }

            // Mostrar error si existe
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (space != null && isAdmin) {
                    // Botón eliminar para espacios existentes
                    Button(
                        onClick = {
                            if (spaceViewModel.deleteSpace(space.id)) {
                                navController.navigateUp()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Eliminar")
                    }
                }

                PrimaryButton(
                    text = when {
                        isAdmin && space != null -> "Actualizar"
                        isAdmin -> "Crear"
                        existingReservation != null -> "Modificar"
                        else -> "Reservar"
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val success = when {
                            isAdmin && space != null -> {
                                spaceViewModel.updateSpace(
                                    space,
                                    name,
                                    description,
                                    capacity.toIntOrNull() ?: 0
                                )
                            }
                            isAdmin -> {
                                spaceViewModel.createSpace(
                                    name,
                                    description,
                                    capacity.toIntOrNull() ?: 0
                                )
                            }
                            existingReservation != null -> {
                                reservationViewModel.updateReservation(existingReservation)
                            }
                            else -> {
                                reservationViewModel.createReservation("current_user")
                            }
                        }
                        if (success) {
                            navController.navigateUp()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpaceSelector(
    selectedSpace: Space?,
    onSpaceSelected: (Space) -> Unit,
    showDropdown: Boolean,
    onDropdownDismiss: () -> Unit,
    onDropdownShow: () -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = showDropdown,
        onExpandedChange = { if (it) onDropdownShow() else onDropdownDismiss() }
    ) {
        OutlinedTextField(
            value = selectedSpace?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Seleccionar espacio") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdown)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = showDropdown,
            onDismissRequest = onDropdownDismiss
        ) {
            // TODO: Reemplazar con lista real de espacios
            listOf(
                Space("1", "Salón Social", "Desc", 50, true, 0),
                Space("2", "Zona BBQ", "Desc", 20, true, 0),
                Space("3", "Gimnasio", "Desc", 15, true, 0)
            ).forEach { space ->
                DropdownMenuItem(
                    text = { Text(space.name) },
                    onClick = {
                        onSpaceSelected(space)
                        onDropdownDismiss()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimeSelector(
    selectedDate: Date?,
    onDateTimeSelected: (Date) -> Unit,
    context: android.content.Context
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()
    selectedDate?.let { calendar.time = it }
    
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            AndroidDatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            onDateTimeSelected(calendar.time)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate?.let { dateFormatter.format(it) } ?: "Seleccionar fecha y hora",
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null
            )
        }
    }
}