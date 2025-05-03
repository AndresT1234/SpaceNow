package com.app.spacenow.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.spacenow.data.model.Reservation
import com.app.spacenow.data.model.ReservationStatus
import com.app.spacenow.data.model.Space
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ReservationViewModel : ViewModel() {
    private val _selectedSpace = MutableStateFlow<Space?>(null)
    val selectedSpace: StateFlow<Space?> = _selectedSpace.asStateFlow()

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    lateinit var dashboardViewModel: DashboardViewModel

    fun setSelectedSpace(space: Space) {
        _selectedSpace.value = space
    }

    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    fun createReservation(userId: String): Boolean {
        val space = _selectedSpace.value
        val date = _selectedDate.value

        if (space == null || date == null) {
            _errorMessage.value = "Por favor complete todos los campos"
            return false
        }

        val reservation = Reservation(
            id = System.currentTimeMillis().toString(),
            spaceId = space.id,
            spaceName = space.name,
            userId = userId,
            dateTime = date,
            status = ReservationStatus.PENDING
        )

        // Actualizar la lista de reservas en el DashboardViewModel
        if (::dashboardViewModel.isInitialized) {
            dashboardViewModel.addReservation(reservation)
        }

        clearForm()
        return true
    }

    fun updateReservation(reservation: Reservation): Boolean {
        val date = _selectedDate.value ?: return false

        // Aquí iría la lógica para actualizar la reserva en el backend
        return true
    }

    fun clearForm() {
        _selectedSpace.value = null
        _selectedDate.value = null
        _errorMessage.value = null
    }
}