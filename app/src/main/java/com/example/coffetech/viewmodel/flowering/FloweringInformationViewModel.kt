package com.example.coffetech.viewmodel.flowering

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.coffetech.model.Flowering
import com.example.coffetech.model.GetActiveFloweringsResponse
import com.example.coffetech.model.GetFloweringHistoryResponse
import com.example.coffetech.model.Plot
import com.example.coffetech.model.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FloweringInformationViewModel: ViewModel() {

    private val _filteredFlowerings = MutableStateFlow<List<Flowering>>(emptyList())

    // Estados para manejar las floraciones activas
    private val _allFlowerings = MutableStateFlow<List<Flowering>>(emptyList())
    val flowerings: StateFlow<List<Flowering>> = _filteredFlowerings.asStateFlow()


    // Estados para manejar el historial de floraciones
    private val _floweringHistory = MutableStateFlow<List<Flowering>>(emptyList())
    val floweringHistory: StateFlow<List<Flowering>> = _floweringHistory.asStateFlow()

    // Estados para el dropdown de nombres de floraciones
    private val _flowerings_name = MutableStateFlow<List<String>>(emptyList())
    val flowerings_name: StateFlow<List<String>> = _flowerings_name.asStateFlow()

    // Estados de UI
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    // Estado para el nombre del lote
    private val _plotName = MutableStateFlow("")
    val plotName: StateFlow<String> = _plotName.asStateFlow()

    // Estado de búsqueda usando TextFieldValue
    private val _searchQuery = mutableStateOf(TextFieldValue(""))
    val searchQuery = _searchQuery

    // Otros estados existentes
    private val _selectedFloweringName = mutableStateOf<String?>(null)
    val selectedFloweringName = _selectedFloweringName

    private val _isDropdownExpanded = mutableStateOf(false)
    val isDropdownExpanded = _isDropdownExpanded

    // Estados para el filtro de tipo de floración en el historial
    private val _historyTypeFilter = MutableStateFlow("Todos los tipos")
    val historyTypeFilter: StateFlow<String> = _historyTypeFilter.asStateFlow()

    // Estados para el ordenamiento del historial de floraciones
    private val _historyOrderFilter = MutableStateFlow("Más reciente")
    val historyOrderFilter: StateFlow<String> = _historyOrderFilter.asStateFlow()

    // Estado para el historial de floraciones filtrado y ordenado
    private val _filteredFloweringHistory = MutableStateFlow<List<Flowering>>(emptyList())
    val filteredFloweringHistory: StateFlow<List<Flowering>> = _filteredFloweringHistory.asStateFlow()


    /**
     * Función para cargar las floraciones activas desde el endpoint.
     */
    fun loadActiveFlowerings(plotId: Int, sessionToken: String) {
        _isLoading.value = true
        RetrofitInstance.api.getActiveFlowerings(plotId, sessionToken)
            .enqueue(object : Callback<GetActiveFloweringsResponse> {
                override fun onResponse(
                    call: Call<GetActiveFloweringsResponse>,
                    response: Response<GetActiveFloweringsResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.status == "success") {
                            responseBody.data?.flowerings?.let { floweringsList ->
                                _allFlowerings.value = floweringsList
                                _flowerings_name.value = floweringsList.map { it.flowering_type_name }.distinct()
                                filterFlowerings()
                            }
                        } else {
                            val errorMsg = responseBody?.message ?: "Error desconocido al obtener las floraciones."
                            _errorMessage.value = errorMsg
                            Log.e("FloweringInfoViewModel", errorMsg)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            try {
                                val errorJson = JSONObject(it)
                                val errorMsg = if (errorJson.has("message")) {
                                    errorJson.getString("message")
                                } else {
                                    "Error desconocido al obtener las floraciones."
                                }
                                _errorMessage.value = errorMsg
                                Log.e("FloweringInfoViewModel", errorMsg)
                            } catch (e: Exception) {
                                _errorMessage.value = "Error al procesar la respuesta del servidor."
                                Log.e("FloweringInfoViewModel", "JSON parsing error", e)
                            }
                        } ?: run {
                            _errorMessage.value = "Error al cargar las floraciones: respuesta vacía del servidor."
                            Log.e("FloweringInfoViewModel", _errorMessage.value)
                        }
                    }
                }

                override fun onFailure(call: Call<GetActiveFloweringsResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión al obtener las floraciones."
                    Log.e("FloweringInfoViewModel", "API call failed para obtener las floraciones: ${t.message}", t)
                }
            })
    }

    /**
     * Función para cargar el historial de floraciones desde el nuevo endpoint.
     */
    fun loadFloweringHistory(plotId: Int, sessionToken: String) {
        RetrofitInstance.api.getFloweringHistory(plotId, sessionToken)
            .enqueue(object : Callback<GetFloweringHistoryResponse> {
                override fun onResponse(
                    call: Call<GetFloweringHistoryResponse>,
                    response: Response<GetFloweringHistoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.status == "success") {
                            responseBody.data?.flowerings?.let { historyList ->
                                _floweringHistory.value = historyList
                                applyFiltersAndSorting() // Aplicar filtros y ordenamiento después de cargar

                            }
                        } else {
                            val errorMsg = responseBody?.message ?: "Error desconocido al obtener el historial de floraciones."
                            _errorMessage.value = errorMsg
                            Log.e("FloweringInfoViewModel", errorMsg)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            try {
                                val errorJson = JSONObject(it)
                                val errorMsg = if (errorJson.has("message")) {
                                    errorJson.getString("message")
                                } else {
                                    "Error desconocido al obtener el historial de floraciones."
                                }
                                _errorMessage.value = errorMsg
                                Log.e("FloweringInfoViewModel", errorMsg)
                            } catch (e: Exception) {
                                _errorMessage.value = "Error al procesar la respuesta del servidor."
                                Log.e("FloweringInfoViewModel", "JSON parsing error", e)
                            }
                        } ?: run {
                            _errorMessage.value = "Error al cargar el historial de floraciones: respuesta vacía del servidor."
                            Log.e("FloweringInfoViewModel", _errorMessage.value)
                        }
                    }
                }

                override fun onFailure(call: Call<GetFloweringHistoryResponse>, t: Throwable) {
                    _errorMessage.value = "Error de conexión al obtener el historial de floraciones."
                    Log.e("FloweringInfoViewModel", "API call failed para obtener el historial de floraciones: ${t.message}", t)
                }
            })
    }

    /**
     * Función para actualizar el filtro de tipo de floración en el historial.
     */
    fun updateHistoryTypeFilter(newFilter: String) {
        _historyTypeFilter.value = newFilter
        applyFiltersAndSorting()
    }
    /**
     * Maneja la selección del filtro de tipo de floración.
     */
    fun selectHistoryTypeFilter(filter: String) {
        updateHistoryTypeFilter(filter)
    }

    /**
     * Maneja la selección del ordenamiento del historial de floraciones.
     */
    fun selectHistoryOrderFilter(order: String) {
        updateHistoryOrderFilter(order)
    }

    /**
     * Función para actualizar el ordenamiento del historial de floraciones.
     */
    fun updateHistoryOrderFilter(newOrder: String) {
        _historyOrderFilter.value = newOrder
        applyFiltersAndSorting()
    }

    /**
     * Función para aplicar los filtros y el ordenamiento al historial de floraciones.
     */
    private fun applyFiltersAndSorting() {
        var filteredList = _floweringHistory.value

        // Aplicar filtro por tipo
        when (_historyTypeFilter.value) {
            "Principal" -> {
                filteredList = filteredList.filter { it.flowering_type_name == "Principal" }
            }
            "Mitaca" -> {
                filteredList = filteredList.filter { it.flowering_type_name == "Mitaca" }
            }
            // "Todos los tipos" no filtra nada
        }

        // Aplicar ordenamiento
        filteredList = when (_historyOrderFilter.value) {
            "Más antiguo" -> {
                filteredList.sortedBy { it.harvest_date }
            }
            "Más reciente" -> {
                filteredList.sortedByDescending { it.harvest_date }
            }
            else -> filteredList // "Ordenar por" no aplica ningún orden específico
        }

        _filteredFloweringHistory.value = filteredList
    }


    /**
     * Maneja el cambio de la consulta de búsqueda.
     */
    fun onSearchQueryChanged(query: TextFieldValue) {
        _searchQuery.value = query
        filterFlowerings()
    }

    /**
     * Filtra las floraciones basadas en la consulta de búsqueda.
     */
    private fun filterFlowerings() {
        val queryText = _searchQuery.value.text
        if (queryText.isEmpty()) {
            _filteredFlowerings.value = _allFlowerings.value
        } else {
            _filteredFlowerings.value = _allFlowerings.value.filter { flowering ->
                flowering.flowering_type_name.contains(queryText, ignoreCase = true)
            }
        }
    }

    /**
     * Maneja la selección de un nombre de floración desde el dropdown.
     */
    fun selectFloweringName(floweringName: String?) {
        _selectedFloweringName.value = floweringName
        filterFloweringsByFloweringName()
    }

    /**
     * Filtra las floraciones basadas en el nombre seleccionado del dropdown.
     */
    private fun filterFloweringsByFloweringName() {
        val selectedName = _selectedFloweringName.value
        _filteredFlowerings.value = if (selectedName.isNullOrEmpty()) {
            if (_searchQuery.value.text.isEmpty()) {
                _allFlowerings.value
            } else {
                _allFlowerings.value.filter { it.flowering_type_name.contains(_searchQuery.value.text, ignoreCase = true) }
            }
        } else {
            _allFlowerings.value.filter { it.flowering_type_name == selectedName }
        }
    }

    /**
     * Maneja la expansión del dropdown.
     */
    fun setDropdownExpanded(isExpanded: Boolean) {
        _isDropdownExpanded.value = isExpanded
    }

    /**
     * Establece un mensaje de error para ser mostrado en la UI.
     */
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
        Log.e("FloweringInfoViewModel", "Error set: $message")
    }

}