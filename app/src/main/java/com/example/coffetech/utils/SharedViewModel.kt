// SharedViewModel.kt
package com.example.coffetech.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {

    private val _culturalWorkTaskId = MutableStateFlow<Int?>(null)
    val culturalWorkTaskId: StateFlow<Int?> = _culturalWorkTaskId

    private val _culturalWorksName = MutableStateFlow<String?>(null)
    val culturalWorksName: StateFlow<String?> = _culturalWorksName

    private val _imagesBase64 = MutableStateFlow<List<String>>(emptyList())
    val imagesBase64: StateFlow<List<String>> = _imagesBase64

    // Funciones para establecer los valores
    fun setCulturalWorkTaskId(id: Int) {
        _culturalWorkTaskId.value = id
    }

    fun setCulturalWorksName(name: String) {
        _culturalWorksName.value = name
    }

    fun setImagesBase64(images: List<String>) {
        _imagesBase64.value = images
    }

    // Función para limpiar los datos después de usarlos, si es necesario
    fun clearData() {
        _culturalWorkTaskId.value = null
        _culturalWorksName.value = null
        _imagesBase64.value = emptyList()
    }
}
