package com.example.coffetech.viewmodel.farm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.navigation.NavController
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.coffetech.model.CreateFarmRequest
import com.example.coffetech.model.CreateFarmResponse
import com.example.coffetech.model.FarmInstance
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.UnitMeasure
import com.example.coffetech.model.UpdateFarmRequest
import com.example.coffetech.model.UpdateFarmResponse
import com.example.coffetech.utils.SharedPreferencesHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * ViewModel responsible for managing the state and logic of editing an existing farm.
 */
class FarmEditViewModel : ViewModel() {
    // Estados para los datos de la finca
    private val _farmName = MutableStateFlow("")
    val farmName: StateFlow<String> = _farmName.asStateFlow()

    private val _farmArea = MutableStateFlow("")
    val farmArea: StateFlow<String> = _farmArea.asStateFlow()

    private val _selectedUnitName = MutableStateFlow("Seleccione una opción")
    val selectedUnitName: StateFlow<String> = _selectedUnitName.asStateFlow()

    private val _selectedUnitId = MutableStateFlow<Int?>(null)

    private val _areaUnitsList = MutableStateFlow<List<UnitMeasure>>(emptyList())
    val areaUnitsList: StateFlow<List<UnitMeasure>> = _areaUnitsList


    // Estado para la lista de unidades de medida
    private val _areaUnits = MutableStateFlow<List<String>>(emptyList())
    val areaUnits: StateFlow<List<String>> = _areaUnits.asStateFlow()

    // Estado de error y de carga
    var errorMessage = MutableStateFlow("")
        private set
    var isLoading = MutableStateFlow(false)
        private set

    // Estado para rastrear si hay cambios pendientes
    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    // Guardar valores iniciales para comparación
    private var initialFarmName = ""
    private var initialFarmArea = ""
    private var initialSelectedUnit = ""

    /**
     * Initializes the ViewModel with the current farm details.
     *
     * @param farmName The current name of the farm.
     * @param farmArea The current area of the farm.
     * @param selectedUnit The current unit of measure of the farm.
     */
    fun initializeValues(farmName: String, farmArea: String, selectedUnit: String) {
        initialFarmName = farmName
        initialFarmArea = farmArea.toDoubleOrNull()?.toInt()?.toString() ?: "0" // Convertir a entero y luego a String
        initialSelectedUnit = selectedUnit

        // Establecer los valores iniciales en los estados del ViewModel
        _farmName.value = farmName
        _farmArea.value = farmArea
        _selectedUnitName.value = selectedUnit

        val selectedUnitObject = _areaUnitsList.value.find { it.name == selectedUnit }
        _selectedUnitId.value = selectedUnitObject?.area_unit_id
    }

    /**
     * Updates the farm name when the user modifies it.
     *
     * @param newName The new farm name entered by the user.
     */
    fun onFarmNameChange(newName: String) {
        _farmName.value = newName
        checkForChanges()
    }
    /**
     * Updates the farm area when the user modifies it.
     *
     * @param newArea The new farm area entered by the user.
     */
    fun onFarmAreaChange(newArea: String) {
        _farmArea.value = newArea
        checkForChanges()
    }
    /**
     * Updates the selected unit of measure when the user selects a new unit.
     *
     * @param newUnit The new unit of measure selected by the user.
     */
    fun onUnitChange(newUnitName: String) {
        _selectedUnitName.value = newUnitName
        val selectedUnit = _areaUnitsList.value.find { it.name == newUnitName }

        if (selectedUnit != null) {
            _selectedUnitId.value = selectedUnit.area_unit_id
            Log.d("CreateFarm", "Unidad seleccionada: ${selectedUnit.name} (ID: ${selectedUnit.area_unit_id})")
        } else {
            Log.e("CreateFarm", "No se encontró la unidad con nombre: $newUnitName")
            _selectedUnitId.value = null
        }
    }
    /**
     * Checks if any changes have been made to the farm details.
     */
    private fun checkForChanges() {
        _hasChanges.value = _farmName.value != initialFarmName ||
                _farmArea.value != initialFarmArea ||
                _selectedUnitName.value != initialSelectedUnit
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
            _areaUnitsList.value = units
            val areaUnitNames = units.map { it.name }
            _areaUnits.value = areaUnitNames

            // Añade este log para verificar los datos cargados
            Log.d("CreateFarm", "Unidades cargadas: ${units.joinToString { "${it.name} (ID: ${it.area_unit_id})" }}")
        } else {
            Log.e("CreateFarm", "No se encontraron unidades de medida")
        }

        // Después de cargar las unidades, asegura que el ID esté alineado al nombre por defecto
        val selectedUnitObject = units?.find { it.name == _selectedUnitName.value }
        _selectedUnitId.value = selectedUnitObject?.area_unit_id

    }

    /**
     * Validates the input fields for editing the farm.
     *
     * @return `true` if the inputs are valid, `false` otherwise.
     */
    private fun validateInputs(): Boolean {
        if (_farmName.value.isBlank()) {
            errorMessage.value = "El nombre de la finca no puede estar vacío."
            return false
        }

        val area = _farmArea.value.toDoubleOrNull()
        if (area == null || area <= 0 || area > 10000) {
            errorMessage.value = "El área debe ser un número mayor a 0 y menor a 10000."
            return false
        }

        return true
    }
    /**
     * Initiates the process of updating the farm details.
     *
     * @param farmId The ID of the farm to be updated.
     * @param navController The NavController for navigation.
     * @param context The current context, needed for displaying toasts.
     */
    fun updateFarmDetails(
        farmId: Int,
        navController: NavController,
        context: Context
    ) {
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

        // Crear el objeto de solicitud para actualizar la finca
        val updateFarmRequest = UpdateFarmRequest(
            farm_id = farmId,
            name = _farmName.value,
            area = _farmArea.value.toDouble(),
            area_unit_id = _selectedUnitId.value ?: throw IllegalStateException("Debe seleccionar una unidad")
        )

        // Realizar la solicitud al servidor
        FarmInstance.api.updateFarm(sessionToken, updateFarmRequest).enqueue(object :
            Callback<UpdateFarmResponse> {
            override fun onResponse(call: Call<UpdateFarmResponse>, response: Response<UpdateFarmResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            Toast.makeText(context, "Finca actualizada exitosamente", Toast.LENGTH_LONG).show()
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
                                "Error desconocido al actualizar la finca."
                            }
                            this@FarmEditViewModel.errorMessage.value = errorMessage
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            this@FarmEditViewModel.errorMessage.value = "Error al procesar la respuesta del servidor."
                            Toast.makeText(context, "Error al procesar la respuesta del servidor.", Toast.LENGTH_LONG).show()
                        }
                    } ?: run {
                        this@FarmEditViewModel.errorMessage.value = "Respuesta vacía del servidor."
                        Toast.makeText(context, "Respuesta vacía del servidor.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateFarmResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Error de conexión"
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }


}
