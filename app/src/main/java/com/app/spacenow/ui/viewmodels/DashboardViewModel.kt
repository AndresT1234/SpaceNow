package com.app.spacenow.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.spacenow.R
import com.app.spacenow.data.model.Space
import com.app.spacenow.data.model.Reservation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class DashboardViewModel : ViewModel() {

    private val _spaces = MutableStateFlow<List<Space>>(emptyList())
    val spaces: StateFlow<List<Space>> = _spaces.asStateFlow()

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _allActiveReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val allActiveReservations: StateFlow<List<Reservation>> = _allActiveReservations.asStateFlow()

    private val _spaceStatistics = MutableStateFlow<Map<String, Int>>(emptyMap())
    val spaceStatistics: StateFlow<Map<String, Int>> = _spaceStatistics.asStateFlow()

    fun setUserRole(isAdmin: Boolean) {
        _isAdmin.value = isAdmin
    }

    init {
        viewModelScope.launch {
            _isAdmin.collect { isAdmin ->
                if (isAdmin) {
                    loadAllActiveReservations()
                    calculateSpaceStatistics()
                }
            }
        }
        loadMockSpaces()
        loadMockReservations()
    }

    fun updateAdminStatus(authViewModel: AuthViewModel) {
        viewModelScope.launch {
            authViewModel.userRole.collect { role ->
                _isAdmin.value = role == "admin"
            }
        }
    }

    private fun loadMockSpaces() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val mockSpaces = listOf(
                    Space("1", "Salón Social", "Amplio espacio para eventos sociales y reuniones", 50, true, R.drawable.salon_social),
                    Space("2", "Zona BBQ", "Área equipada para asados y reuniones al aire libre", 20, true, R.drawable.zona_bbq),
                    Space("3", "Gimnasio", "Espacio con equipos modernos para ejercicio", 15, true, R.drawable.gimnasio),
                    Space("4", "Sauna", "Área de relajación y bienestar", 6, true, R.drawable.sauna),
                    Space("5", "Cancha de Tenis", "Cancha profesional para práctica y competición", 4, true, R.drawable.cancha_tenis),
                    Space("6", "Cancha Sintética", "Campo de fútbol con césped artificial", 14, true, R.drawable.cancha_sintetica)
                )
                _spaces.value = mockSpaces
            } catch (e: Exception) {
                _spaces.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadMockReservations() {
        viewModelScope.launch {
            val mockReservations = listOf(
                Reservation("1", "1", "Salón Social", "current_user", Date(System.currentTimeMillis() + 86400000)),
                Reservation("2", "2", "Zona BBQ", "current_user", Date(System.currentTimeMillis() + 172800000))
            )
            _reservations.value = mockReservations
        }
    }

    private fun loadAllActiveReservations() {
        viewModelScope.launch {
            val mockAllReservations = listOf(
                Reservation("3", "3", "Gimnasio", "other_user1", Date(System.currentTimeMillis() + 86400000)),
                Reservation("4", "1", "Salón Social", "other_user2", Date(System.currentTimeMillis() + 172800000))
            )
            _allActiveReservations.value = mockAllReservations
        }
    }

    private fun calculateSpaceStatistics() {
        viewModelScope.launch {
            val stats = mutableMapOf<String, Int>()
            (_reservations.value + _allActiveReservations.value).forEach { reservation ->
                stats[reservation.spaceName] = (stats[reservation.spaceName] ?: 0) + 1
            }
            _spaceStatistics.value = stats
        }
    }

    fun deleteReservation(reservationId: String) {
        val updatedList = _reservations.value.toMutableList().apply {
            removeIf { it.id == reservationId }
        }
        _reservations.value = updatedList
    }

    fun modifyReservation(reservationId: String, newDateTime: Date) {
        val updatedList = _reservations.value.toMutableList()
        val index = updatedList.indexOfFirst { it.id == reservationId }
        if (index != -1) {
            updatedList[index] = updatedList[index].copy(dateTime = newDateTime)
            _reservations.value = updatedList
        }
    }

    fun addReservation(reservation: Reservation) {
        val updatedList = _reservations.value.toMutableList().apply {
            add(reservation)
        }
        _reservations.value = updatedList

        if (_isAdmin.value) {
            calculateSpaceStatistics()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Limpiar recursos si es necesario
    }
}