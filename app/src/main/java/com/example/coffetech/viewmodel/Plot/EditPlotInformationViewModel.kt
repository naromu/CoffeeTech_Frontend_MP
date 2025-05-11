// En com.example.coffetech.viewmodel.Plot.EditPlotInformationViewModel

package com.example.coffetech.viewmodel.Plot

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.model.*
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * ViewModel responsible for managing the state and logic of editing plot information,
 * including plot name and coffee variety updates.
 */
class EditPlotInformationViewModel : ViewModel() {

    private val TAG = "EditPlotInfoViewModel"

    // Estados para los datos
    private val _plotId = MutableStateFlow(0)
    val plotId: StateFlow<Int> = _plotId.asStateFlow()

    private val _plotName = MutableStateFlow("")
    val plotName: StateFlow<String> = _plotName.asStateFlow()

    private val _selectedVariety = MutableStateFlow("")
    val selectedVariety: StateFlow<String> = _selectedVariety.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _plotCoffeeVariety = MutableStateFlow<List<String>>(emptyList())
    val plotCoffeeVariety: StateFlow<List<String>> = _plotCoffeeVariety.asStateFlow()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    private val _coffeeVarietyList = MutableStateFlow<List<CoffeeVariety>>(emptyList())
    val coffeeVarietyList: StateFlow<List<CoffeeVariety>> = _coffeeVarietyList.asStateFlow()

    private val _selectedVarietyId = MutableStateFlow<Int?>(null)
    val selectedVarietyId: StateFlow<Int?> = _selectedVarietyId.asStateFlow()


    fun onPlotNameChange(newName: String) {
        if (_plotName.value != newName) {
            _plotName.value = newName
            _hasChanges.value = true
        }
    }

    fun onVarietyChange(newVariety: String) {
        if (_selectedVariety.value != newVariety) {
            _selectedVariety.value = newVariety
            val selected = _coffeeVarietyList.value.find { it.name == newVariety }
            _selectedVarietyId.value = selected?.coffee_variety_id
            _hasChanges.value = true
        }
    }


    /**
     * Initializes the ViewModel with the plot's current details.
     *
     * @param plotId The ID of the plot to be edited.
     * @param plotName The current name of the plot.
     * @param selectedVariety The current coffee variety of the plot.
     * @param context The current context, needed for accessing SharedPreferences.
     */
    fun initialize(plotId: Int, plotName: String, selectedVariety: String, context: Context) {
        // Solo inicializar si el plotId actual es diferente al recibido
        if (_plotId.value != plotId) {
            _plotId.value = plotId
        }

        // Actualizar plotName y selectedVariety si son diferentes
        if (_plotName.value != plotName) {
            _plotName.value = plotName
        }

        if (_selectedVariety.value != selectedVariety) {
            _selectedVariety.value = selectedVariety
        }

        // Cargar variedades de café
        loadCoffeeVarieties(context)
    }

    /**
     * Loads the available coffee varieties from SharedPreferences or other data sources.
     *
     * @param context The current context, needed for accessing SharedPreferences.
     */
    fun loadCoffeeVarieties(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sharedPreferencesHelper = SharedPreferencesHelper(context)
                val varieties = sharedPreferencesHelper.getCoffeeVarieties() ?: listOf(
                    CoffeeVariety(1, "Caturra"),
                    CoffeeVariety(2, "Bourbon")
                )
                _coffeeVarietyList.value = varieties              // ← agrega este StateFlow
                _plotCoffeeVariety.value = varieties.map { it.name }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar variedades de café: ${e.message}"
                Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Saves the changes made to the plot's information and updates the server.
     *
     * @param plotId The ID of the plot to be updated.
     * @param navController The NavController for navigation after successful update.
     * @param onSuccess Callback function to execute upon successful update.
     * @param onError Callback function to execute upon encountering an error.
     */
    fun saveChanges(
        plotId: Int,
        navController: NavController,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Validar entradas
        if (_plotName.value.isBlank() || _selectedVariety.value.isBlank()) {
            _errorMessage.value = "Todos los campos son obligatorios."
            Toast.makeText(navController.context, _errorMessage.value, Toast.LENGTH_LONG).show()

            return
        }

        // Obtener el session_token de SharedPreferences
        val sharedPreferencesHelper = SharedPreferencesHelper(navController.context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()
        if (sessionToken.isNullOrEmpty()) {
            _errorMessage.value = "Token de sesión no encontrado. Por favor, inicia sesión nuevamente."
            Toast.makeText(navController.context, _errorMessage.value, Toast.LENGTH_LONG).show()

            return
        }

        // Crear el objeto de solicitud
        val updateRequest = UpdatePlotGeneralInfoRequest(
            plot_id = plotId,
            name = _plotName.value,
            coffee_variety_id = _selectedVarietyId.value ?: 0 // ← usa el ID
        )

        _isLoading.value = true
        _errorMessage.value = ""

        // Realizar la llamada a la API
        FarmInstance.api.updatePlotGeneralInfo(sessionToken, updateRequest)
            .enqueue(object : Callback<UpdatePlotGeneralInfoResponse> {
                override fun onResponse(
                    call: Call<UpdatePlotGeneralInfoResponse>,
                    response: Response<UpdatePlotGeneralInfoResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.status == "success") {
                            Toast.makeText(navController.context, "Lote actualizado exitosamente.", Toast.LENGTH_LONG).show()
                            _hasChanges.value = false
                            onSuccess()
                        } else if (responseBody?.status == "error") {
                            val errorMsg = responseBody.message ?: "Error desconocido."
                            _errorMessage.value = errorMsg
                            Toast.makeText(navController.context, errorMsg, Toast.LENGTH_LONG).show()

                        } else {
                            _errorMessage.value = "Respuesta inesperada del servidor."
                            Toast.makeText(navController.context, "Respuesta inesperada del servidor.", Toast.LENGTH_LONG).show()

                        }
                    } else {
                        // Manejar errores de respuesta no exitosa
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = if (errorBody != null) {
                            JSONObject(errorBody).optString("message", "Error desconocido.")
                        } else {
                            "Error desconocido."
                        }
                        _errorMessage.value = errorMsg
                        Toast.makeText(navController.context, errorMsg, Toast.LENGTH_LONG).show()

                    }
                }

                override fun onFailure(call: Call<UpdatePlotGeneralInfoResponse>, t: Throwable) {
                    _isLoading.value = false
                    val connectionErrorMsg = "Error de conexión"
                    _errorMessage.value = connectionErrorMsg
                    Toast.makeText(navController.context, connectionErrorMsg, Toast.LENGTH_LONG).show()
                    //onError(connectionErrorMsg)
                }
            })
    }

    fun deletePlot(context: Context, plotId: Int, navController: NavController) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()

        if (sessionToken.isNullOrEmpty()) {
            _errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(context, "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            return
        }

        _isLoading.value = true

        FarmInstance.api.deletePlot(plotId, sessionToken).enqueue(object : Callback<CreateFarmResponse> {
            override fun onResponse(call: Call<CreateFarmResponse>, response: Response<CreateFarmResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Lote eliminado correctamente.", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                    navController.popBackStack()
                } else {
                    _errorMessage.value = "Error al eliminar el lote."
                    Toast.makeText(context, "Error al eliminar el lote.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<CreateFarmResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error de conexión"
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }


}
