package com.app.spacenow.ui.screens

// Android imports
import android.app.TimePickerDialog
import android.app.DatePickerDialog as AndroidDatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

// Compose imports
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// Project imports
import com.app.spacenow.data.model.Reservation
import com.app.spacenow.data.model.Space
import com.app.spacenow.ui.components.PrimaryButton
import com.app.spacenow.ui.viewmodels.ReservationViewModel
import com.app.spacenow.ui.viewmodels.SpaceViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

/**
 * Form screen for creating or editing spaces and reservations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    isAdmin: Boolean,
    space: Space? = null,
    existingReservation: Reservation? = null,
    reservationViewModel: ReservationViewModel = viewModel(),
    spaceViewModel: SpaceViewModel = viewModel(),
    spaces: List<Space>,
) {
    // Form state
    var name by remember { mutableStateOf(space?.name ?: "") }
    var description by remember { mutableStateOf(space?.description ?: "") }
    var capacity by remember { mutableStateOf(space?.capacity?.toString() ?: "") }
    var showSpaceDropdown by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // ViewModel state
    val selectedDate by reservationViewModel.selectedDate.collectAsState()
    val selectedSpace by reservationViewModel.selectedSpace.collectAsState()
    val errorMessage by if (isAdmin) spaceViewModel.errorMessage.collectAsState() else reservationViewModel.errorMessage.collectAsState()

    // Initialize data if editing
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
                // Space form fields
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

                // Image picker component
                ImagePickerSection(
                    selectedImageUri = selectedImageUri,
                    onImageSelected = { uri ->
                        selectedImageUri = uri
                    }
                )
            } else {
                // Reservation form fields
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
                        onDropdownShow = { showSpaceDropdown = true },
                        spaces = spaces
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

            // Error message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (space != null && isAdmin) {
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
                                    capacity.toIntOrNull() ?: 0,
                                    selectedImageUri
                                )
                            }
                            isAdmin -> {
                                spaceViewModel.createSpace(
                                    name,
                                    description,
                                    capacity.toIntOrNull() ?: 0,
                                    selectedImageUri
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

/**
 * Component for selecting and previewing an image
 */
@Composable
fun ImagePickerSection(
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Imagen del espacio",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Image preview
        Box(
            modifier = Modifier
                .size(200.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                // Show selected image
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Show placeholder
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "No image selected",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to open image picker
        Button(
            onClick = { launcher.launch("image/*") }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(
                text = if (selectedImageUri == null) "Seleccionar imagen" else "Cambiar imagen"
            )
        }
    }
}

/**
 * Dropdown menu for selecting a space
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpaceSelector(
    selectedSpace: Space?,
    onSpaceSelected: (Space) -> Unit,
    showDropdown: Boolean,
    onDropdownDismiss: () -> Unit,
    onDropdownShow: () -> Unit,
    spaces: List<Space>
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
            spaces.forEach { space ->
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

/**
 * Component for selecting date and time
 */
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