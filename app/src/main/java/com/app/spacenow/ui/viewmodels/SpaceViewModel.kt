package com.app.spacenow.ui.viewmodels

import android.net.Uri
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


    private val _spaces = MutableStateFlow<List<Space>>(emptyList())
    val spaces: StateFlow<List<Space>> = _spaces.asStateFlow()

    fun setSelectedSpace(space: Space) {
        _selectedSpace.value = space
    }

    fun createSpace(name: String, description: String, capacity: Int, imageUri: Uri? = null): Boolean {
        if (name.isBlank() || description.isBlank() || capacity <= 0) {
            _errorMessage.value = "Por favor complete todos los campos correctamente"
            return false
        }

        try {
            _isLoading.value = true

            val imageResource = imageUri?.let { 0 } ?: 0

            // Create the space object
            val space = Space(
                id = System.currentTimeMillis().toString(), // Temporary ID, should come from backend
                name = name,
                description = description,
                capacity = capacity,
                available = true,
                imageResource = imageResource
            )


            val currentSpaces = _spaces.value.toMutableList()
            currentSpaces.add(space)
            _spaces.value = currentSpaces

            return true
        } catch (e: Exception) {
            _errorMessage.value = "Error al crear el espacio: ${e.message}"
            return false
        } finally {
            _isLoading.value = false
        }
    }

    fun updateSpace(space: Space, name: String, description: String, capacity: Int, imageUri: Uri? = null): Boolean {
        if (name.isBlank() || description.isBlank() || capacity <= 0) {
            _errorMessage.value = "Por favor complete todos los campos correctamente"
            return false
        }

        try {
            _isLoading.value = true

            // Handle image update if provided
            val imageResource = if (imageUri != null) {

                0
            } else {
                space.imageResource
            }

            // Create updated space with new details
            val updatedSpace = space.copy(
                name = name,
                description = description,
                capacity = capacity,
                imageResource = imageResource
            )

            val currentSpaces = _spaces.value.toMutableList()
            val index = currentSpaces.indexOfFirst { it.id == space.id }
            if (index != -1) {
                currentSpaces[index] = updatedSpace
                _spaces.value = currentSpaces
            }

            return true
        } catch (e: Exception) {
            _errorMessage.value = "Error al actualizar el espacio: ${e.message}"
            return false
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteSpace(spaceId: String): Boolean {
        try {
            _isLoading.value = true

            val currentSpaces = _spaces.value.toMutableList()
            currentSpaces.removeIf { it.id == spaceId }
            _spaces.value = currentSpaces

            return true
        } catch (e: Exception) {
            _errorMessage.value = "Error al eliminar el espacio: ${e.message}"
            return false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearForm() {
        _selectedSpace.value = null
        _errorMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}