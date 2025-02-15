package com.example.coffetech.viewmodel.farm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.navigation.NavController

import android.content.Context
import android.widget.Toast
import com.example.coffetech.model.CreateFarmRequest
import com.example.coffetech.model.CreateFarmResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel responsible for managing the state and logic of creating a new farm.
 */
class CreateFarmViewModel : ViewModel() {

    // Estados para los datos
    private val _farmName = MutableStateFlow("")
    val farmName: StateFlow<String> = _farmName.asStateFlow()

    private val _farmArea = MutableStateFlow("")
    val farmArea: StateFlow<String> = _farmArea.asStateFlow()

    private val _areaUnits = MutableStateFlow<List<String>>(emptyList())
    val areaUnits: StateFlow<List<String>> = _areaUnits.asStateFlow()

    private val _selectedUnit = MutableStateFlow("Seleccione una opción")
    val selectedUnit: StateFlow<String> = _selectedUnit.asStateFlow()

    var errorMessage = MutableStateFlow("")
        private set
    var isLoading = MutableStateFlow(false)
        private set


    /**
     * Updates the farm name when the user modifies it.
     *
     * @param newName The new farm name entered by the user.
     */
    fun onFarmNameChange(newName: String) {
        _farmName.value = newName
    }
    /**
     * Updates the farm area when the user modifies it.
     *
     * @param newArea The new farm area entered by the user.
     */
    fun onFarmAreaChange(newArea: String) {
        _farmArea.value = newArea
    }
    /**
     * Updates the selected unit of measure when the user selects a new unit.
     *
     * @param newUnit The new unit of measure selected by the user.
     */
    fun onUnitChange(newUnit: String) {
        _selectedUnit.value = newUnit
    }
    /**
     * Loads the available unit measures from SharedPreferences.
     *
     * @param context The current context, needed to access SharedPreferences.
     */
    fun loadUnitMeasuresFromSharedPreferences(context: Context) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val units = sharedPreferencesHelper.getUnitMeasures()
        if (!units.isNullOrEmpty()) {
            val areaUnitsList = units.filter { it.unit_of_measure_type.name == "Área" }
                .map { it.name }
            _areaUnits.value = areaUnitsList
        }
    }
    /**
     * Validates the input fields for creating a new farm.
     *
     * @return `true` if the inputs are valid, `false` otherwise.
     */
    private fun validateInputs(): Boolean {
        if (_farmName.value.isBlank()) {
            errorMessage.value = "El nombre de la finca no puede estar vacío."
            return false
        }

        val areaString = _farmArea.value
        val area = areaString.toIntOrNull()
        if (area == null) {
            errorMessage.value = "El área debe ser un número entero válido."
            return false
        }
        if (area <= 0 || area > 10000) {
            errorMessage.value = "El área debe ser mayor a 0 y menor o igual a 10,000."
            return false
        }

        // Validación de la unidad seleccionada
        if (_selectedUnit.value == "Seleccione una opción") {
            errorMessage.value = "Debe seleccionar una la unidad de medida."
            return false
        }

        return true
    }
    /**
     * Initiates the process of creating a new farm.
     *
     * @param navController The NavController for navigation.
     * @param context The current context, needed for displaying toasts.
     */
    fun onCreate(navController: NavController, context: Context) {
        if (!validateInputs()) {
            return
        }

        errorMessage.value = ""
        isLoading.value = true

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken() ?: run {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(context, "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            isLoading.value = false
            return
        }

        // Crear el objeto de solicitud
        val createFarmRequest = CreateFarmRequest(
            name = _farmName.value,
            area = _farmArea.value.toDouble(),
            unitMeasure = _selectedUnit.value
        )

        // Realizar la solicitud al servidor
        RetrofitInstance.api.createFarm(sessionToken, createFarmRequest).enqueue(object :
            Callback<CreateFarmResponse> {
            override fun onResponse(call: Call<CreateFarmResponse>, response: Response<CreateFarmResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            Toast.makeText(context, "Finca creada exitosamente", Toast.LENGTH_LONG).show()
                            navController.popBackStack() // Regresar a la pantalla anterior
                        } else {
                            errorMessage.value = it.message
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMessage = if (errorJson.has("message")) {
                                errorJson.getString("message")
                            } else {
                                "Error desconocido al crear la finca."
                            }
                            this@CreateFarmViewModel.errorMessage.value = errorMessage
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            this@CreateFarmViewModel.errorMessage.value = "Error al procesar la respuesta del servidor."
                            Toast.makeText(context, "Error al procesar la respuesta del servidor.", Toast.LENGTH_LONG).show()
                        }
                    } ?: run {
                        this@CreateFarmViewModel.errorMessage.value = "Respuesta vacía del servidor."
                        Toast.makeText(context, "Respuesta vacía del servidor.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CreateFarmResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Error de conexión"
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }


}


