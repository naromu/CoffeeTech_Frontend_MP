package com.example.coffetech.viewmodel.farm

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.GetFarmResponse
import com.example.coffetech.model.ListPlotsResponse
import com.example.coffetech.model.Plot
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.text.input.TextFieldValue
/**
 * ViewModel responsible for managing the state and logic of displaying farm information.
 */
class FarmInformationViewModel : ViewModel() {

    // Estados de nombre, área, y otros datos de la finca
    private val _farmName = MutableStateFlow("")
    val farmName: StateFlow<String> = _farmName.asStateFlow()

    private val _farmArea = MutableStateFlow(0.0)
    val farmArea: StateFlow<Double> = _farmArea.asStateFlow()

    private val _unitOfMeasure = MutableStateFlow("")
    val unitOfMeasure: StateFlow<String> = _unitOfMeasure.asStateFlow()

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _selectedRole = MutableStateFlow("")
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    private val _permissions = MutableStateFlow<List<String>>(emptyList())
    val permissions: StateFlow<List<String>> = _permissions.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _collaboratorName = MutableStateFlow("Colaborador de Ejemplo")
    val collaboratorName: StateFlow<String> = _collaboratorName.asStateFlow()

    // Lista completa de lotes sin filtrar
    private val _allLotes = MutableStateFlow<List<Plot>>(emptyList())

    // Estado de búsqueda usando TextFieldValue
    private val _searchQuery = mutableStateOf(TextFieldValue(""))
    val searchQuery = _searchQuery

    // Lista de lotes filtrados según la búsqueda
    private val _filteredLotes = MutableStateFlow<List<Plot>>(emptyList())
    val lotes: StateFlow<List<Plot>> = _filteredLotes.asStateFlow()

    /**
     * Handles changes to the search query for filtering plots.
     *
     * @param query The new search query entered by the user.
     */
    fun onSearchQueryChanged(query: TextFieldValue) {
        _searchQuery.value = query
        Log.d("FarmInfoViewModel", "Search query changed: ${query.text}")
        filterLotes() // Filtra los lotes cada vez que cambia la consulta de búsqueda
    }

    /**
     * Filters the list of plots based on the search query.
     */
    private fun filterLotes() {
        val queryText = _searchQuery.value.text
        if (queryText.isEmpty()) {
            _filteredLotes.value = _allLotes.value
        } else {
            _filteredLotes.value = _allLotes.value.filter { lote ->
                lote.name.contains(queryText, ignoreCase = true)
            }
        }
    }
    /**
     * Navigates to the FarmEditView with the specified farm details.
     *
     * @param navController The NavController for navigation.
     * @param farmId The ID of the farm to edit.
     * @param farmName The name of the farm to edit.
     * @param farmArea The area of the farm to edit.
     * @param unitOfMeasure The unit of measure of the farm to edit.
     */
    fun onEditFarm(navController: NavController, farmId: Int, farmName: String, farmArea: Double, unitOfMeasure: String) {
        navController.navigate("FarmEditView/$farmId/$farmName/$farmArea/$unitOfMeasure")
    }
    /**
     * Navigates to the CollaboratorView to add a new collaborator.
     *
     * @param navController The NavController for navigation.
     */
    fun onAddCollaborator(navController: NavController) {
        Log.d("FarmInfoViewModel", "Navigating to CollaboratorView")
        navController.navigate("CollaboratorView")
    }
    /**
     * Navigates to the AddLoteView to add a new plot.
     *
     * @param navController The NavController for navigation.
     */
    fun onAddLote(navController: NavController) {
        Log.d("FarmInfoViewModel", "Navigating to AddLoteView")
        navController.navigate("AddLoteView")
    }

    /**
     * Checks if the user has a specific permission.
     *
     * @param permission The permission to check.
     * @return `true` if the user has the specified permission, `false` otherwise.
     */
    fun hasPermission(permission: String): Boolean {
        return _permissions.value.contains(permission)
    }

    /**
     * Loads the permissions for the selected role from SharedPreferences.
     *
     * @param context The current context, needed to access SharedPreferences.
     * @param selectedRoleName The name of the role whose permissions are to be loaded.
     */
    fun loadRolePermissions(context: Context, selectedRoleName: String) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val roles = sharedPreferencesHelper.getRoles()

        // Buscar el rol seleccionado y obtener sus permisos
        roles?.find { it.name == selectedRoleName }?.let { role ->
            _permissions.value = role.permissions.map { it.name }
        }
    }

    /**
     * Loads the farm data from the backend via an API call.
     *
     * @param farmId The ID of the farm to load data for.
     * @param sessionToken The session token for authorization.
     * @param context The current context, needed for displaying toasts.
     */
    fun loadFarmData(farmId: Int, sessionToken: String, context: Context) {
        if (sessionToken.isEmpty()) {
            setErrorMessage("Session token is missing. Please log in.")
            Log.e("FarmInfoViewModel", "Session token is missing. Aborting API call.")
            return
        }

        Log.d("FarmInfoViewModel", "Starting API call to fetch farm data for farmId: $farmId with sessionToken: $sessionToken")
        _isLoading.value = true

        RetrofitInstance.api.getFarm(farmId, sessionToken).enqueue(object : Callback<GetFarmResponse> {
            override fun onResponse(call: Call<GetFarmResponse>, response: Response<GetFarmResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        responseBody.data?.farm?.let { farm ->
                            _farmName.value = farm.name
                            _farmArea.value = farm.area
                            _unitOfMeasure.value = farm.unit_of_measure
                            _selectedRole.value = farm.role
                            _status.value = farm.status

                            // Cargar permisos basados en el rol
                            loadRolePermissions(context, farm.role)

                            // Verificar permisos antes de intentar cargar los lotes
                            if (hasPermission("read_plots")) {
                                loadPlots(farmId, sessionToken)
                            }
                        } ?: run {
                            _errorMessage.value = "Error: No se encontraron datos de la finca."
                            Toast.makeText(context, "Error: No se encontraron datos de la finca.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val errorMsg = responseBody?.message ?: "Error desconocido."
                        _errorMessage.value = errorMsg
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    _errorMessage.value = "Error al obtener los datos de la finca."
                    Toast.makeText(context, "Error al obtener los datos de la finca.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GetFarmResponse>, t: Throwable) {
                _isLoading.value = false
                val connectionErrorMsg = "Error de conexión"
                _errorMessage.value = connectionErrorMsg
                Toast.makeText(context, connectionErrorMsg, Toast.LENGTH_LONG).show()
                Log.e("FarmInfoViewModel", "API call failed for farmId: $farmId, Error: ${t.message}", t)
            }
        })
    }


    /**
     * Sets an error message to be displayed in the UI.
     *
     * @param message The error message to set.
     */
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
        Log.e("FarmInfoViewModel", "Error set: $message")
    }

    /**
     * Loads the list of plots from the backend and applies initial filtering.
     *
     * @param farmId The ID of the farm whose plots are being loaded.
     * @param sessionToken The session token for authorization.
     */
    fun loadPlots(farmId: Int, sessionToken: String) {
        _isLoading.value = true
        _errorMessage.value = ""  // Limpiar cualquier mensaje de error anterior

        RetrofitInstance.api.listPlots(farmId, sessionToken).enqueue(object : Callback<ListPlotsResponse> {
            override fun onResponse(call: Call<ListPlotsResponse>, response: Response<ListPlotsResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            _allLotes.value = it.data.plots // Almacena todos los lotes
                            filterLotes() // Aplica el filtro inicial (sin búsqueda)
                        } else if (it.status == "error") {
                            val errorMsg = it.message ?: "Error desconocido al cargar los lotes."
                            _errorMessage.value = errorMsg
                        }
                    } ?: run {
                        _errorMessage.value = "No se encontraron lotes."
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMsg = if (errorJson.has("message")) {
                                errorJson.getString("message")
                            } else {
                                "Error desconocido al cargar los lotes."
                            }
                            _errorMessage.value = errorMsg
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
}
