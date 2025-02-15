// SendDectectionViewModel.kt
package com.example.coffetech.viewmodel.healthcheck

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import java.io.File
import androidx.core.content.FileProvider

class SendDectectionViewModel : ViewModel() {

    companion object {
        private const val TAG = "SendDectectionViewModel"
        const val MAX_PHOTOS_PER_TASK = 10
    }

    // Estado para almacenar las fotos por tarea
    private val _taskPhotos = MutableStateFlow<Map<Int, List<Uri>>>(emptyMap())
    val taskPhotos: StateFlow<Map<Int, List<Uri>>> = _taskPhotos.asStateFlow()

    // Estado para manejar la tarea actualmente seleccionada para tomar fotos o importar
    private val _currentTaskId = MutableStateFlow<Int?>(null)
    val currentTaskId: StateFlow<Int?> = _currentTaskId.asStateFlow()

    // Estado para almacenar la URI de la imagen capturada o importada
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    // Estado para manejo de permisos
    private val _showPermissionRationale = MutableStateFlow(false)
    val showPermissionRationale: StateFlow<Boolean> = _showPermissionRationale.asStateFlow()

    // Estado para manejar la carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    // Función para crear un archivo de imagen
    fun createImageFile(context: Context): File {
        val imageFileName = "JPEG_${System.currentTimeMillis()}_"
        val storageDir = context.cacheDir // Puedes cambiar a externalFilesDir si prefieres
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    // Función para iniciar la captura o importación de fotos para una tarea
    fun startPhotoAction(taskId: Int) {
        Log.d(TAG, "Iniciando acción de foto para la tarea ID: $taskId")

        val currentPhotos = _taskPhotos.value[taskId] ?: emptyList()
        if (currentPhotos.size >= MAX_PHOTOS_PER_TASK) {
            Log.d(TAG, "Límite de fotos alcanzado para la tarea ID: $taskId")
            // Emitir un evento para mostrar un mensaje en la Vista si es necesario
            return
        }

        _currentTaskId.value = taskId
    }
    // En SendDectectionViewModel.kt
    fun endPhotoAction() {
        _currentTaskId.value = null
    }

    // Función para manejar el resultado de la captura o importación de la foto
    // En SendDectectionViewModel.kt
    fun onPictureTaken(success: Boolean) {
        if (success && _imageUri.value != null && _currentTaskId.value != null) {
            val taskId = _currentTaskId.value!!
            val updatedPhotos = _taskPhotos.value.toMutableMap()
            val currentPhotos = updatedPhotos[taskId] ?: emptyList()
            val photos = currentPhotos + _imageUri.value!!
            updatedPhotos[taskId] = photos
            _taskPhotos.value = updatedPhotos.toMap()
        }
        // Eliminar o comentar esta línea
        // _currentTaskId.value = null
        _imageUri.value = null
    }


    // Función para establecer la URI de la imagen
    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    // Funciones para manejar el permiso de cámara
    fun showPermissionRationale(show: Boolean) {
        _showPermissionRationale.value = show
    }

    // Función para verificar si se ha alcanzado el límite de fotos
    fun hasReachedPhotoLimit(taskId: Int): Boolean {
        val currentPhotos = _taskPhotos.value[taskId] ?: emptyList()
        return currentPhotos.size >= MAX_PHOTOS_PER_TASK
    }
}
