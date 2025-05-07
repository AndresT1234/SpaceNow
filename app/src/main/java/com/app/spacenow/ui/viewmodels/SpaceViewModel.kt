package com.app.spacenow.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.spacenow.R
import com.app.spacenow.data.model.Space
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.app.spacenow.ui.utils.*

class SpaceViewModel : ViewModel() {
    private val _selectedSpace = MutableStateFlow<Space?>(null)
    val selectedSpace: StateFlow<Space?> = _selectedSpace.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Reference to DashboardViewModel
    var dashboardViewModel: DashboardViewModel? = null

    // Necesitamos una referencia al contexto para manejar URIs y archivos
    private var appContext: Context? = null

    // Método para establecer el contexto (llamar desde la inicialización)
    fun setContext(context: Context) {
        this.appContext = context.applicationContext
    }

    fun setSelectedSpace(space: Space) {
        _selectedSpace.value = space
    }

    fun createSpace(name: String, description: String, capacity: Int, tempImageUri: Uri?): Boolean {
        if (name.isBlank() || description.isBlank() || capacity <= 0) {
            _errorMessage.value = "Por favor complete todos los campos correctamente"
            return false
        }

        try {
            _isLoading.value = true

            // Verificar que tenemos contexto
            if (appContext == null) {
                _errorMessage.value = "Error: Contexto de la aplicación no disponible"
                return false
            }

            // Guardar la imagen de forma permanente
            val permanentImageUri = saveImageToAppStorage(appContext!!, tempImageUri)

            Log.d("SpaceViewModel", "Creando espacio con URI: $tempImageUri")
            Log.d("SpaceViewModel", "URI permanente: $permanentImageUri")

            // Crear el objeto space con la URI permanente
            val space = Space(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                capacity = capacity,
                available = true,
                imageResource = R.drawable.salon_social, // Imagen por defecto como fallback
                imageUri = permanentImageUri?.toString() // URI permanente para la imagen
            )

            // Añadir el nuevo espacio al DashboardViewModel
            dashboardViewModel?.addSpace(space)

            return true
        } catch (e: Exception) {
            Log.e("SpaceViewModel", "Error al crear espacio", e)
            _errorMessage.value = "Error al crear el espacio: ${e.message}"
            return false
        } finally {
            _isLoading.value = false
        }
    }

    fun updateSpace(space: Space, name: String, description: String, capacity: Int, tempImageUri: Uri? = null): Boolean {
        if (name.isBlank() || description.isBlank() || capacity <= 0) {
            _errorMessage.value = "Por favor complete todos los campos correctamente"
            return false
        }

        try {
            _isLoading.value = true

            // Verificar que tenemos contexto
            if (appContext == null) {
                _errorMessage.value = "Error: Contexto de la aplicación no disponible"
                return false
            }

            // Procesar la imagen solo si se proporciona una nueva
            val permanentImageUri = if (tempImageUri != null) {
                saveImageToAppStorage(appContext!!, tempImageUri)
            } else {
                // Mantener la URI existente
                space.imageUri?.let { Uri.parse(it) }
            }

            // Crear el espacio actualizado
            val updatedSpace = space.copy(
                name = name,
                description = description,
                capacity = capacity,
                imageResource = space.imageResource,
                imageUri = permanentImageUri?.toString() ?: space.imageUri
            )

            // Actualizar el espacio en DashboardViewModel
            dashboardViewModel?.updateSpace(updatedSpace)

            return true
        } catch (e: Exception) {
            Log.e("SpaceViewModel", "Error al actualizar espacio", e)
            _errorMessage.value = "Error al actualizar el espacio: ${e.message}"
            return false
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteSpace(spaceId: String): Boolean {
        try {
            _isLoading.value = true

            // Eliminar del DashboardViewModel
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