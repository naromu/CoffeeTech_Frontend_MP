// GeneralCulturalWorkTaskViewModel.kt
package com.example.coffetech.viewmodel.CulturalWorkTask

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffetech.model.GeneralCulturalWorkTask
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GeneralCulturalWorkTaskViewModel : ViewModel() {

    // Estado para la lista de tareas culturales generales
    private val _tasks = MutableStateFlow<List<GeneralCulturalWorkTask>>(emptyList())
    val tasks: StateFlow<List<GeneralCulturalWorkTask>> = _tasks

    // Estado para manejar mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Estado para manejar la carga de tareas
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estados para los filtros
    private val _selectedFarm = MutableStateFlow("Todas las fincas")
    val selectedFarm: StateFlow<String> = _selectedFarm.asStateFlow()

    private val _selectedPlot = MutableStateFlow("Todos los lotes")
    val selectedPlot: StateFlow<String> = _selectedPlot.asStateFlow()

    private val _selectedOrder = MutableStateFlow("Más reciente")
    val selectedOrder: StateFlow<String> = _selectedOrder.asStateFlow()

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery: StateFlow<TextFieldValue> = _searchQuery.asStateFlow()

    // Estados para los dropdowns de fincas y lotes
    private val _farmOptions = MutableStateFlow<List<String>>(emptyList())
    val farmOptions: StateFlow<List<String>> = _farmOptions.asStateFlow()

    private val _plotOptions = MutableStateFlow<List<String>>(emptyList())
    val plotOptions: StateFlow<List<String>> = _plotOptions.asStateFlow()

    // Estado para las tareas filtradas
    private val _filteredTasks = MutableStateFlow<List<GeneralCulturalWorkTask>>(emptyList())
    val filteredTasks: StateFlow<List<GeneralCulturalWorkTask>> = _filteredTasks.asStateFlow()

    fun addTestTasks(tasks: List<GeneralCulturalWorkTask>) {
        _tasks.value = tasks
        extractFilterOptions()
        applyFiltersAndSorting()
    }

    /**
     * Función para cargar tareas desde el endpoint.
     *
     * @param context El contexto para acceder a SharedPreferences.
     */
    fun loadTasks(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: ""
                if (sessionToken.isEmpty()) {
                    _errorMessage.value = "Token de sesión no encontrado. Por favor, inicia sesión nuevamente."
                    return@launch
                }

                val response = RetrofitInstance.api.getMyCulturalWorkTasks(sessionToken)
                if (response.status == "success") {
                    _tasks.value = response.data.tasks
                    extractFilterOptions()
                    applyFiltersAndSorting()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error cargando las tareas culturales: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para extraer opciones únicas de fincas y lotes
    private fun extractFilterOptions() {
        val farms = _tasks.value.map { it.farm_name }.distinct().sorted()
        _farmOptions.value = listOf("Todas las fincas") + farms

        val plots = _tasks.value.map { it.plot_name }.distinct().sorted()
        _plotOptions.value = listOf("Todos los lotes") + plots
    }

    // Funciones para actualizar filtros y búsqueda
    fun selectFarm(farm: String) {
        _selectedFarm.value = farm
        applyFiltersAndSorting()
    }

    fun selectPlot(plot: String) {
        _selectedPlot.value = plot
        applyFiltersAndSorting()
    }

    fun selectOrder(order: String) {
        _selectedOrder.value = order
        applyFiltersAndSorting()
    }

    fun onSearchQueryChanged(query: TextFieldValue) {
        _searchQuery.value = query
        applyFiltersAndSorting()
    }

    // Aplicar filtros y ordenamiento
    private fun applyFiltersAndSorting() {
        var filteredList = _tasks.value

        // Filtrar por finca
        if (_selectedFarm.value != "Todas las fincas") {
            filteredList = filteredList.filter { it.farm_name == _selectedFarm.value }
        }

        // Filtrar por lote
        if (_selectedPlot.value != "Todos los lotes") {
            filteredList = filteredList.filter { it.plot_name == _selectedPlot.value }
        }

        // Aplicar ordenamiento
        filteredList = when (_selectedOrder.value) {
            "Más antiguo" -> filteredList.sortedBy { it.task_date }
            "Más reciente" -> filteredList.sortedByDescending { it.task_date }
            else -> filteredList
        }

        // Aplicar búsqueda
        val query = _searchQuery.value.text
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.cultural_works_name.contains(query, ignoreCase = true)
            }
        }

        _filteredTasks.value = filteredList
    }
}