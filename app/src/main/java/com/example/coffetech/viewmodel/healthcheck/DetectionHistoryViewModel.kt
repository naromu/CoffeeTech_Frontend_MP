// viewmodel/healthcheck/DetectionHistoryViewModel.kt
package com.example.coffetech.viewmodel.healthcheck

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.coffetech.model.Detection
import com.example.coffetech.model.ListDetectionsRequest
import com.example.coffetech.model.ListDetectionsResponse
import com.example.coffetech.model.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetectionHistoryViewModel : ViewModel() {

    // Lista original de detecciones (sin filtrar)
    private val _allDetections = mutableListOf<Detection>()

    // Lista de detecciones filtrada por la búsqueda
    private val _detections = MutableStateFlow<List<Detection>>(emptyList())
    val detections: StateFlow<List<Detection>> = _detections.asStateFlow()

    // Estado de búsqueda usando TextFieldValue
    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery: StateFlow<TextFieldValue> = _searchQuery.asStateFlow()

    // Estado para los filtros (puedes agregar más filtros si es necesario)
    private val _statusFilter = MutableStateFlow("Todos")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    /**
     * Carga las detecciones desde el API.
     *
     * @param plotId ID del lote para el cual se obtendrán las detecciones.
     * @param sessionToken Token de sesión para autenticación.
     */
    fun loadDetections(plotId: Int, sessionToken: String) {
        Log.d("DetectionHistoryViewModel", "Iniciando carga de detecciones para plotId: $plotId")
        _isLoading.value = true
        _errorMessage.value = ""

        val request = ListDetectionsRequest(plot_id = plotId)

        RetrofitInstance.api.listDetections(sessionToken, request)
            .enqueue(object : Callback<ListDetectionsResponse> {
                override fun onResponse(
                    call: Call<ListDetectionsResponse>,
                    response: Response<ListDetectionsResponse>
                ) {
                    Log.d("DetectionHistoryViewModel", "Respuesta recibida con código: ${response.code()}")
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("DetectionHistoryViewModel", "Cuerpo de la respuesta: $responseBody")
                        if (responseBody?.status == "success") {
                            _allDetections.clear()
                            _allDetections.addAll(responseBody.data.detections)
                            applyFiltersAndSearch()
                            Log.d("DetectionHistoryViewModel", "Detecciones cargadas: ${_allDetections.size}")
                        } else {
                            _errorMessage.value = responseBody?.message ?: "Error desconocido."
                            Log.e("DetectionHistoryViewModel", "Error de la API: ${responseBody?.message}")
                        }
                    } else {
                        _errorMessage.value = "Error en la respuesta del servidor: ${response.code()}"
                        Log.e("DetectionHistoryViewModel", "Error en la respuesta del servidor: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ListDetectionsResponse>, t: Throwable) {
                    Log.e("DetectionHistoryViewModel", "Fallo en la llamada API: ${t.message}", t)
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión"
                }
            })
    }

    /**
     * Actualiza la consulta de búsqueda y aplica los filtros.
     */
    fun onSearchQueryChanged(query: TextFieldValue) {
        _searchQuery.value = query
        applyFiltersAndSearch()
    }

    /**
     * Aplica los filtros y la búsqueda a la lista de detecciones.
     */
    private fun applyFiltersAndSearch() {
        var filteredList = _allDetections.toList()

        // Filtrar por estado si aplica (ajusta según tus necesidades)
        if (_statusFilter.value != "Todos") {
            filteredList = filteredList.filter { it.result == _statusFilter.value }
        }

        // Aplicar búsqueda por nombre del colaborador
        val query = _searchQuery.value.text.lowercase()
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.collaborator_name.lowercase().contains(query)
            }
        }

        _detections.value = filteredList
    }

    /**
     * Función para actualizar el filtro de estado, si decides implementarlo en la UI.
     */
    fun selectStatusFilter(filter: String) {
        _statusFilter.value = filter
        applyFiltersAndSearch()
    }
}
