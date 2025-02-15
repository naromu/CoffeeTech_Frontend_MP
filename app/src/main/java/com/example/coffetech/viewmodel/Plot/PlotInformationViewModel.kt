// En com.example.coffetech.viewmodel.Plot.PlotInformationViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.GetPlotResponse
import com.example.coffetech.model.Plot
import com.example.coffetech.model.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel responsible for managing the state and logic of displaying plot information,
 * including fetching plot details and handling navigation actions.
 */
class PlotInformationViewModel : ViewModel() {
    private val TAG = "PlotInformationViewModel"

    // Estados existentes
    private val _plotName = MutableStateFlow("")
    val plotName: StateFlow<String> = _plotName.asStateFlow()

    private val _plotCoffeeVariety = MutableStateFlow("")
    val plotCoffeeVariety: StateFlow<String> = _plotCoffeeVariety.asStateFlow()

    private val _selectedVariety = MutableStateFlow("")
    val selectedVariety: StateFlow<String> = _selectedVariety.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _faseName = MutableStateFlow("")
    val faseName: StateFlow<String> = _faseName.asStateFlow()

    private val _initialDate = MutableStateFlow("")
    val initialDate: StateFlow<String> = _initialDate.asStateFlow()

    private val _endDate = MutableStateFlow("")
    val endDate: StateFlow<String> = _endDate.asStateFlow()

    private val _coordinatesUbication = MutableStateFlow("")
    val coordinatesUbication: StateFlow<String> = _coordinatesUbication.asStateFlow()

    // Nuevo estado para el plot
    private val _plot = MutableStateFlow<Plot?>(null)
    val plot: StateFlow<Plot?> = _plot.asStateFlow()

    /**
     * Navigates to the EditPlotInformationView with the specified plot details.
     *
     * @param navController The NavController for navigation.
     * @param plotId The ID of the plot to edit.
     * @param plotName The name of the plot to edit.
     * @param selectedVariety The coffee variety of the plot to edit.
     */
    fun onEditPlot(
        navController: NavController,
        plotId: Int,
        plotName: String,
        selectedVariety: String
    ) {
        Log.d(TAG, "Navegando a EditPlotInformationView con plotId: $plotId, plotName: $plotName, selectedVariety: $selectedVariety")

        navController.navigate("EditPlotInformationView/$plotId/$plotName/$selectedVariety")
    }
    /**
     * Navigates to the EditFaseView with the specified phase details.
     *
     * @param navController The NavController for navigation.
     * @param faseName The name of the phase to edit.
     * @param initialDate The initial date of the phase.
     * @param endDate The end date of the phase.
     */
    fun onEditFase(navController: NavController, faseName: String, initialDate: String, endDate: String) {
        navController.navigate("PlotEditView/$faseName/$initialDate/$endDate")
    }
    /**
     * Navigates to the EditUbicationView with the specified coordinates.
     *
     * @param navController The NavController for navigation.
     * @param coordinatesUbication The coordinates of the plot's location.
     */
    fun onEditUbication(navController: NavController, coordinatesUbication: String) {
        navController.navigate("PlotEditView/$coordinatesUbication")
    }
    /**
     * Navigates to the FloracionesView for managing plot flowering events.
     *
     * @param navController The NavController for navigation.
     * @param plotId The ID of the plot for which to manage flowering events.
     */
    fun onFloracionesClick(navController: NavController, plotId: Int) {
        navController.navigate("FloracionesView/$plotId")
    }

    /**
     * Sets an error message to be displayed in the UI.
     *
     * @param message The error message to set.
     */    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    /**
     * Fetches the plot details from the server using the provided plot ID and session token.
     *
     * @param plotId The ID of the plot to fetch.
     * @param sessionToken The session token for authorization.
     */    fun getPlot(plotId: Int, sessionToken: String) {
        _isLoading.value = true
        _errorMessage.value = ""  // Limpiar cualquier mensaje de error anterior

        RetrofitInstance.api.getPlot(plotId, sessionToken).enqueue(object : Callback<GetPlotResponse> {
            override fun onResponse(call: Call<GetPlotResponse>, response: Response<GetPlotResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            _plot.value = it.data.plot
                            _plotName.value = it.data.plot.name
                            _plotCoffeeVariety.value = it.data.plot.coffee_variety_name
                            _coordinatesUbication.value = "${it.data.plot.latitude},${it.data.plot.longitude}"
                            // Actualiza otros estados según sea necesario
                        } else {
                            _errorMessage.value = it.message
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
                                "Error desconocido al obtener el lote."
                            }
                            _errorMessage.value = errorMessage
                        } catch (e: Exception) {
                            _errorMessage.value = "Error al procesar la respuesta del servidor."
                        }
                    } ?: run {
                        _errorMessage.value = "Respuesta vacía del servidor."
                    }
                }
            }

            override fun onFailure(call: Call<GetPlotResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error de conexión"
            }
        })
    }

}
