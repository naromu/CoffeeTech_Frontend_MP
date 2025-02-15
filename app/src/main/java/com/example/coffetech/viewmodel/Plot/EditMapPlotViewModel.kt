package com.example.coffetech.viewmodel.Plot

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.coffetech.model.*
import com.example.coffetech.utils.SharedPreferencesHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
/**
 * ViewModel responsible for managing the state and logic of editing a plot's map location,
 * including handling location permissions and updating plot coordinates.
 */
class EditMapPlotViewModel : ViewModel() {

    private val _latitude = MutableStateFlow(0.0)
    val latitude: StateFlow<Double> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(0.0)
    val longitude: StateFlow<Double> = _longitude.asStateFlow()

    private val _altitude = MutableStateFlow<Double?>(null)
    val altitude: StateFlow<Double?> = _altitude.asStateFlow()

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    private val _isAltitudeLoading = MutableStateFlow(false)
    val isAltitudeLoading: StateFlow<Boolean> = _isAltitudeLoading.asStateFlow()

    private val openElevationService: OpenElevationService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-elevation.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(OpenElevationService::class.java)
    }

    // Nuevas variables para rastrear la ubicación inicial y cambios
    private var _initialLatitude: Double = 0.0
    private var _initialLongitude: Double = 0.0

    private val _hasLocationChanged = MutableStateFlow(false)
    val hasLocationChanged: StateFlow<Boolean> = _hasLocationChanged.asStateFlow()
    /**
     * Checks whether the location permission has been granted.
     *
     * @param context The current context, needed to access permission status.
     * @return `true` if location permission is granted, `false` otherwise.
     */
    fun checkLocationPermission(context: Context): Boolean {
        val permissionState = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val granted = permissionState == PackageManager.PERMISSION_GRANTED
        _locationPermissionGranted.value = granted
        return granted
    }
    /**
     * Updates the state of location permission based on user action.
     *
     * @param granted `true` if permission is granted, `false` otherwise.
     */
    fun updateLocationPermissionStatus(granted: Boolean) {
        _locationPermissionGranted.value = granted
    }
    /**
     * Updates the plot's location and fetches the corresponding altitude.
     *
     * @param latLng The new location of the plot.
     */
    fun onLocationChange(latLng: LatLng) {
        _latitude.value = latLng.latitude
        _longitude.value = latLng.longitude

        // Determinar si la ubicación ha cambiado respecto a la inicial
        _hasLocationChanged.value = (latLng.latitude != _initialLatitude) || (latLng.longitude != _initialLongitude)

        // Obtener altitud usando la API de Open Elevation
        fetchAltitude(latLng.latitude, latLng.longitude)
    }
    /**
     * Fetches the altitude for the given latitude and longitude with retry logic.
     *
     * @param latitude The latitude of the plot.
     * @param longitude The longitude of the plot.
     */
    private fun fetchAltitude(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _isAltitudeLoading.value = true
            val maxAttempts = 3
            var attempts = 0
            var success = false
            var currentLatLng = LatLng(latitude, longitude)

            while (attempts < maxAttempts && !success) {
                try {
                    _errorMessage.value = ""
                    val locations = "${currentLatLng.latitude},${currentLatLng.longitude}"
                    val elevationResponse = openElevationService.getElevation(locations)

                    if (elevationResponse.results.isNotEmpty()) {
                        _altitude.value = elevationResponse.results[0].elevation
                        success = true
                    } else {
                        _errorMessage.value = "Reintentando..."
                        currentLatLng = LatLng(
                            currentLatLng.latitude + 0.0001,
                            currentLatLng.longitude + 0.0001
                        )
                    }
                } catch (e: Exception) {
                    attempts++
                    if (attempts < maxAttempts) {
                        currentLatLng = LatLng(
                            currentLatLng.latitude + 0.0001,
                            currentLatLng.longitude + 0.0001
                        )
                        delay(1000) // Esperar 1 segundo antes de reintentar
                    } else {
                        _errorMessage.value = "Error al obtener la altitud después de $attempts intentos. Intente en otra ubicación o más tarde."
                    }
                }
            }

            if (!success && _errorMessage.value.isEmpty()) {
                _errorMessage.value = "No se pudo obtener la altitud."
            }

            _isAltitudeLoading.value = false
        }
    }
    /**
     * Sets the initial location of the plot.
     *
     * @param latitude The initial latitude of the plot.
     * @param longitude The initial longitude of the plot.
     * @param altitude The initial altitude of the plot.
     */
    fun setInitialLocation(latitude: Double, longitude: Double, altitude: Double) {
        _latitude.value = latitude
        _longitude.value = longitude
        _altitude.value = altitude

        // Establecer ubicación inicial
        _initialLatitude = latitude
        _initialLongitude = longitude

        // Inicialmente, no hay cambios
        _hasLocationChanged.value = false
    }

    /**
     * Updates the plot's location on the server by sending the updated coordinates.
     *
     * @param context The current context, needed for accessing SharedPreferences.
     * @param plotId The ID of the plot to be updated.
     */
    fun onUpdatePlotLocation(context: Context, plotId: Int) {
        _isLoading.value = true
        _errorMessage.value = ""

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken() ?: run {
            _errorMessage.value = "No se encontró el token de sesión."
            _isLoading.value = false
            return
        }

        val request = UpdatePlotLocationRequest(
            plot_id = plotId,
            latitude = _latitude.value.toString(),
            longitude = _longitude.value.toString(),
            altitude = _altitude.value?.toString() ?: "0.0"
        )

        RetrofitInstance.api.updatePlotLocation(sessionToken, request).enqueue(object :
            Callback<UpdatePlotLocationResponse> {
            override fun onResponse(
                call: Call<UpdatePlotLocationResponse>,
                response: Response<UpdatePlotLocationResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        _updateSuccess.value = true
                        // Resetear el estado de cambios después de una actualización exitosa
                        _hasLocationChanged.value = false
                        // Actualizar la ubicación inicial a la nueva ubicación guardada
                        _initialLatitude = _latitude.value
                        _initialLongitude = _longitude.value
                    } else {
                        _errorMessage.value = responseBody?.message ?: "Error desconocido."
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = errorBody ?: "Error al actualizar la ubicación."
                }
            }

            override fun onFailure(call: Call<UpdatePlotLocationResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error de conexión"
            }
        })
    }
}
