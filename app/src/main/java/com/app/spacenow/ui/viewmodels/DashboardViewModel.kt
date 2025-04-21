package com.app.spacenow.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.spacenow.data.model.Space
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val _spaces = MutableStateFlow<List<Space>>(emptyList())
    val spaces: StateFlow<List<Space>> = _spaces.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadMockSpaces()
    }

    private fun loadMockSpaces() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val mockSpaces = listOf(
                    Space(
                        id = "1",
                        name = "Sala de Reuniones A",
                        description = "Espaciosa sala con capacidad para 10 personas",
                        capacity = 10,
                        available = true,
                        imageUrl = "https://via.placeholder.com/400x300/2196F3/FFFFFF?text=Sala+A"
                    ),
                    Space(
                        id = "2",
                        name = "Sala de Conferencias",
                        description = "Sala equipada con proyector y sistema de audio",
                        capacity = 20,
                        available = true,
                        imageUrl = "https://via.placeholder.com/400x300/4CAF50/FFFFFF?text=Sala+B"
                    ),
                    Space(
                        id = "3",
                        name = "Espacio Colaborativo",
                        description = "Área abierta para trabajo en equipo",
                        capacity = 15,
                        available = false,
                        imageUrl = "https://via.placeholder.com/400x300/FFC107/FFFFFF?text=Sala+C"
                    )
                )
                _spaces.value = mockSpaces
            } catch (e: Exception) {
                // Manejar el error si es necesario
                _spaces.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}