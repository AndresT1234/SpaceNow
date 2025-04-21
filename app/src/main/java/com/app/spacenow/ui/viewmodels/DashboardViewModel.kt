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
                        name = "Salón Social",
                        description = "Amplio espacio para eventos sociales y reuniones",
                        capacity = 50,
                        available = true,
                        imageUrl = "https://via.placeholder.com/400x300/2196F3/FFFFFF?text=Salon+Social"
                    ),
                    Space(
                        id = "2",
                        name = "Zona BBQ",
                        description = "Área equipada para asados y reuniones al aire libre",
                        capacity = 20,
                        available = true,
                        imageUrl = "https://via.placeholder.com/400x300/4CAF50/FFFFFF?text=Zona+BBQ"
                    ),
                    Space(
                        id = "3",
                        name = "Gimnasio",
                        description = "Espacio con equipos modernos para ejercicio",
                        capacity = 15,
                        available = true,
                        imageUrl = "https://via.placeholder.com/400x300/FFC107/FFFFFF?text=Gimnasio"
                    ),
                    Space(
                        id = "4",
                        name = "Sauna",
                        description = "Área de relajación y bienestar",
                        capacity = 6,
                        available = true,
                        imageUrl = "https://via.placeholder.com/400x300/FF5722/FFFFFF?text=Sauna"
                    ),
                    Space(
                        id = "5",
                        name = "Cancha de Tenis",
                        description = "Cancha profesional para práctica y competición",
                        capacity = 4,
                        available = true,
                        imageUrl = "https://via.placeholder.com/400x300/9C27B0/FFFFFF?text=Cancha+Tenis"
                    ),
                    Space(
                        id = "6",
                        name = "Cancha Sintética",
                        description = "Campo de fútbol con césped artificial",
                        capacity = 14,
                        available = true,
                        imageUrl = "https://via.placeholder.com/400x300/009688/FFFFFF?text=Cancha+Sintetica"
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