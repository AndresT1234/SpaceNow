package com.app.spacenow.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.spacenow.data.model.Space
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.app.spacenow.R

class SpaceViewModel : ViewModel() {
    private val _selectedSpace = MutableStateFlow<Space?>(null)
    val selectedSpace: StateFlow<Space?> = _selectedSpace.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Reference to DashboardViewModel
    var dashboardViewModel: DashboardViewModel? = null

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

            // Handle image processing if needed
            val imageResource = imageUri?.let {
                // In a real app, process and upload the image
                // For now, just use a placeholder image resource
                R.drawable.salon_social // Default image or placeholder
            } ?: R.drawable.salon_social // Default image if none selected

            // Create the space object
            val space = Space(
                id = System.currentTimeMillis().toString(),
                name = name,
                description = description,
                capacity = capacity,
                available = true,
                imageResource = imageResource
            )

            // Add the new space to the DashboardViewModel
            dashboardViewModel?.addSpace(space)

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
                // Process new image
                R.drawable.salon_social // Replace with actual image processing
            } else {
                // Keep existing image
                space.imageResource
            }

            // Create updated space
            val updatedSpace = space.copy(
                name = name,
                description = description,
                capacity = capacity,
                imageResource = imageResource
            )

            // Update the space in DashboardViewModel
            dashboardViewModel?.updateSpace(updatedSpace)

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

            // Delete from DashboardViewModel
            dashboardViewModel?.deleteSpace(spaceId)

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