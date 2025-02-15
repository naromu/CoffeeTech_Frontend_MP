// CulturalWorkTaskViewModel.kt
package com.example.coffetech.viewmodel.cultural

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffetech.model.CulturalWorkTask
import com.example.coffetech.model.ListCulturalWorkTasksResponse
import com.example.coffetech.model.RetrofitInstance
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class CulturalWorkTaskViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<CulturalWorkTask>>(emptyList())
    val tasks: StateFlow<List<CulturalWorkTask>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery: StateFlow<TextFieldValue> = _searchQuery.asStateFlow()

    // Nuevos estados para los filtros
    private val _statusFilter = MutableStateFlow("Todos")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _orderFilter = MutableStateFlow("Más reciente")
    val orderFilter: StateFlow<String> = _orderFilter.asStateFlow()

    // Estado para las tareas filtradas
    private val _filteredTasks = MutableStateFlow<List<CulturalWorkTask>>(emptyList())
    val filteredTasks: StateFlow<List<CulturalWorkTask>> = _filteredTasks.asStateFlow()

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }


    fun onSearchQueryChanged(query: TextFieldValue) {
        _searchQuery.value = query
        applyFiltersAndSorting()
    }

    // Funciones para actualizar los filtros
    fun selectStatusFilter(filter: String) {
        _statusFilter.value = filter
        applyFiltersAndSorting()
    }

    fun selectOrderFilter(order: String) {
        _orderFilter.value = order
        applyFiltersAndSorting()
    }

    // Aplicar filtros y ordenamiento
    private fun applyFiltersAndSorting() {
        var filteredList = _tasks.value

        // Filtrar por estado
        if (_statusFilter.value != "Todos") {
            filteredList = filteredList.filter { it.status == _statusFilter.value }
        }

        // Aplicar ordenamiento
        filteredList = when (_orderFilter.value) {
            "Más antiguo" -> filteredList.sortedBy { it.task_date }
            "Más reciente" -> filteredList.sortedByDescending { it.task_date }
            else -> filteredList
        }

        // Aplicar búsqueda
        val query = _searchQuery.value.text
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.collaborator_name.contains(query, ignoreCase = true)
            }
        }
        _filteredTasks.value = filteredList
    }

    /**
     * Carga las tareas desde el API.
     *
     * @param plotId ID del plot para el cual se obtendrán las tareas.
     * @param sessionToken Token de sesión para autenticación.
     */
    fun loadTasks(plotId: Int, sessionToken: String) {
        Log.d("CulturalWorkTaskViewModel", "Iniciando carga de tareas para plotId: $plotId")
        _isLoading.value = true
        _errorMessage.value = ""

        RetrofitInstance.api.listCulturalWorkTasks(plotId, sessionToken)
            .enqueue(object : Callback<ListCulturalWorkTasksResponse> {
                override fun onResponse(
                    call: Call<ListCulturalWorkTasksResponse>,
                    response: Response<ListCulturalWorkTasksResponse>
                ) {
                    Log.d("CulturalWorkTaskViewModel", "Respuesta recibida con código: ${response.code()}")
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("CulturalWorkTaskViewModel", "Cuerpo de la respuesta: $responseBody")
                        if (responseBody?.status == "success") {
                            _tasks.value = responseBody.data.tasks
                            Log.d("CulturalWorkTaskViewModel", "Tareas cargadas: ${_tasks.value.size}")
                            applyFiltersAndSorting()
                        } else {
                            _errorMessage.value = responseBody?.message ?: "Error desconocido."
                            Log.e("CulturalWorkTaskViewModel", "Error de la API: ${responseBody?.message}")
                        }
                    } else {
                        _errorMessage.value = "Error en la respuesta del servidor: ${response.code()}"
                        Log.e("CulturalWorkTaskViewModel", "Error en la respuesta del servidor: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ListCulturalWorkTasksResponse>, t: Throwable) {
                    Log.e("CulturalWorkTaskViewModel", "Fallo en la llamada API: ${t.message}", t)
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión"
                }
            })
    }
}