// FormFinanceReportViewModel.kt
package com.example.coffetech.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.model.ListPlotsResponse
import com.example.coffetech.model.Plot
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class FormFinanceReportViewModel : ViewModel() {

    // Definir la zona horaria y formato de fecha
    private val colombiaTimeZone = TimeZone.getTimeZone("America/Bogota")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = colombiaTimeZone
    }
    private val currentDate: Calendar = Calendar.getInstance(colombiaTimeZone)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _allPlots = MutableStateFlow<List<Plot>>(emptyList())
    val allPlots: StateFlow<List<Plot>> = _allPlots.asStateFlow()

    private val _selectedPlotIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedPlotIds: StateFlow<List<Int>> = _selectedPlotIds.asStateFlow()

    private val _startDate = MutableStateFlow<String?>(null)
    val startDate: StateFlow<String?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<String?>(null)
    val endDate: StateFlow<String?> = _endDate.asStateFlow()

    val isFormValid: StateFlow<Boolean> = combine(
        _selectedPlotIds,
        _startDate,
        _endDate
    ) { plots, start, end ->
        plots.isNotEmpty() &&
                !start.isNullOrBlank() &&
                !end.isNullOrBlank() &&
                isDateNotInFuture(start) &&
                isDateNotInFuture(end)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun loadPlots(farmId: Int, context: Context) {
        _isLoading.value = true
        _errorMessage.value = null

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken() ?: run {
            _errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(
                context,
                "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            _isLoading.value = false
            return
        }

        RetrofitInstance.api.listPlots(farmId, sessionToken).enqueue(object :
            Callback<ListPlotsResponse> {
            override fun onResponse(call: Call<ListPlotsResponse>, response: Response<ListPlotsResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            _allPlots.value = it.data.plots
                        } else {
                            _errorMessage.value = it.message ?: "Error desconocido al cargar los lotes."
                        }
                    } ?: run {
                        _errorMessage.value = "No se encontraron lotes."
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            _errorMessage.value = errorJson.optString("message", "Error desconocido al cargar los lotes.")
                        } catch (e: Exception) {
                            _errorMessage.value = "Error al procesar la respuesta del servidor."
                        }
                    } ?: run {
                        _errorMessage.value = "Error al cargar los lotes: respuesta vacía del servidor."
                    }
                }
            }

            override fun onFailure(call: Call<ListPlotsResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error de conexión"
            }
        })
    }

    fun togglePlotSelection(plotId: Int) {
        val current = _selectedPlotIds.value.toMutableList()
        if (current.contains(plotId)) {
            current.remove(plotId)
        } else {
            current.add(plotId)
        }
        _selectedPlotIds.value = current
    }

    fun setSelectedPlotIds(ids: List<Int>) {
        _selectedPlotIds.value = ids
    }

    fun selectAllPlots() {
        _allPlots.value.let { plots ->
            _selectedPlotIds.value = plots.map { it.plot_id }
        }
    }

    fun deselectAllPlots() {
        _selectedPlotIds.value = emptyList()
    }

    fun updateStartDate(date: String) {
        if (isDateNotInFuture(date)) {
            _startDate.value = date
            _errorMessage.value = null
        } else {
            _errorMessage.value = "La fecha de inicio no puede ser en el futuro."
        }
    }

    fun updateEndDate(date: String) {
        if (isDateNotInFuture(date)) {
            _endDate.value = date
            _errorMessage.value = null
        } else {
            _errorMessage.value = "La fecha de fin no puede ser en el futuro."
        }
    }

    private fun isDateNotInFuture(date: String): Boolean {
        return try {
            val selectedDate = dateFormat.parse(date) ?: return false
            selectedDate <= currentDate.time
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Método para manejar el envío del formulario
     */
    fun onSubmit(navController: NavController, includeTransactionHistory: Boolean) {
        if (!isFormValid.value) {
            _errorMessage.value = "Por favor, completa todos los campos correctamente."
            return
        }

        val plotIds = _selectedPlotIds.value
        val startDate = _startDate.value
        val endDate = _endDate.value

        if (plotIds.isNotEmpty() && !startDate.isNullOrBlank() && !endDate.isNullOrBlank()) {
            // Construir una ruta con los parámetros
            val plotIdsParam = plotIds.joinToString(",")
            val historyParam = if (includeTransactionHistory) "1" else "0"

            val route = "financeReport/$plotIdsParam/$startDate/$endDate/$historyParam"
            navController.navigate(route)
        } else {
            _errorMessage.value = "Por favor, completa todos los campos."
        }
    }
}
