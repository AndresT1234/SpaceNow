package com.app.spacenow.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.spacenow.data.model.Space
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpaceViewModel : ViewModel() {
    private val _selectedSpace = MutableStateFlow<Space?>(null)
    val selectedSpace: StateFlow<Space?> = _selectedSpace.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setSelectedSpace(space: Space) {
        _selectedSpace.value = space
    }

    fun createSpace(name: String, description: String, capacity: Int): Boolean {
        if (name.isBlank() || description.isBlank() || capacity <= 0) {
            _errorMessage.value = "Por favor complete todos los campos correctamente"
            return false
        }

        // Aquí iría la lógica para crear el espacio en el backend
        val space = Space(
            id = System.currentTimeMillis().toString(), // Temporal, debería venir del backend
            name = name,
            description = description,
            capacity = capacity,
            available = true,
            imageResource = 0 // Por ahora sin imagen
        )

        return true
    }

    fun updateSpace(space: Space, name: String, description: String, capacity: Int): Boolean {
        if (name.isBlank() || description.isBlank() || capacity <= 0) {
            _errorMessage.value = "Por favor complete todos los campos correctamente"
            return false
        }

        // Aquí iría la lógica para actualizar el espacio en el backend
        val updatedSpace = space.copy(
            name = name,
            description = description,
            capacity = capacity
        )

        return true
    }

    fun deleteSpace(spaceId: String): Boolean {
        // Aquí iría la lógica para eliminar el espacio en el backend
        return true
    }

    fun clearForm() {
        _selectedSpace.value = null
        _errorMessage.value = null
    }
}