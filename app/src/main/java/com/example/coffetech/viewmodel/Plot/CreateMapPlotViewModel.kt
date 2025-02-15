package com.example.coffetech.viewmodel.Plot

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.WindowInsetsAnimation
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.room.util.copy
import com.example.coffetech.Routes.Routes
import com.example.coffetech.model.CreateFarmResponse
import com.example.coffetech.model.CreatePlotRequest
import com.example.coffetech.model.OpenElevationService
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * ViewModel responsible for managing the state and logic of plot-related operations,
 * including location handling and plot creation.
 */
class PlotViewModel : ViewModel() {

    // Estado para la ubicación seleccionada en el mapa (LatLng)
    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location.asStateFlow()

    // Estado para la altitud
    private val _altitude = MutableStateFlow<Double?>(null)
    val altitude: StateFlow<Double?> = _altitude.asStateFlow()

    // Estado para los permisos de localización
    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-elevation.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(OpenElevationService::class.java)

    private val _attemptNumber = MutableStateFlow(0)
    val attemptNumber: StateFlow<Int> = _attemptNumber.asStateFlow()

    // Estado para los mensajes de error o validación
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    // Estado para controlar si se está guardando
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isFormSubmitted = mutableStateOf(false)
    val isFormSubmitted: State<Boolean> = _isFormSubmitted

    private val _isAltitudeLoading = MutableStateFlow(false)
    val isAltitudeLoading: StateFlow<Boolean> = _isAltitudeLoading.asStateFlow()

    private val _latitude = MutableStateFlow("")
    val latitude: StateFlow<String> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow("")
    val longitude: StateFlow<String> = _longitude.asStateFlow()

    /**
     * Updates the user's selected location on the map.
     *
     * @param latLng The new location selected by the user.
     */
    fun onLocationChange(latLng: LatLng) {
        _location.value = latLng
        _latitude.value = latLng.latitude.toString()
        _longitude.value = latLng.longitude.toString()

        viewModelScope.launch {
            _isAltitudeLoading.value = true // Iniciar el estado de carga
            val elevation = fetchElevation(latLng)
            _altitude.value = elevation
            _isAltitudeLoading.value = false // Finalizar el estado de carga
        }
    }

    /**
     * Checks whether the location permission has been granted.
     *
     * @param context The current context, needed to access permission status.
     * @return `true` if location permission is granted, `false` otherwise.
     */
    fun checkLocationPermission(context: Context): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        _locationPermissionGranted.value = isGranted
        return isGranted
    }

    /**
     * Updates the state of location permission based on user action.
     *
     * @param isGranted `true` if permission is granted, `false` otherwise.
     */
    fun updateLocationPermissionStatus(isGranted: Boolean) {
        _locationPermissionGranted.value = isGranted
    }
    /**
     * Submits the plot creation form.
     */
    fun onSubmit() {
        _isFormSubmitted.value = true
    }
    /**
     * Sets an error message to be displayed in the UI.
     *
     * @param message The error message to set.
     */
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
    /**
     * Clears any existing error messages.
     */
    fun clearErrorMessage() {
        _errorMessage.value = ""
    }
    /**
     * Saves the plot data after successful validation.
     */
    fun savePlotData() {
        // Lógica para guardar los datos
    }
    /**
     * Fetches the user's current location.
     *
     * @param context The current context, needed for accessing location services.
     */
    fun fetchLocation(context: Context) {
        viewModelScope.launch {
            setErrorMessage("")
            _isLoading.value = true
            try {
                Log.d("PlotViewModel", "Intentando obtener la ubicación...")
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val lastLocation = fusedLocationClient.lastLocation.await()
                if (lastLocation != null) {
                    Log.d("PlotViewModel", "Última ubicación obtenida: $lastLocation")
                    val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                    onLocationChange(latLng)
                } else {
                    Log.d("PlotViewModel", "Última ubicación es null, obteniendo ubicación actual...")
                    val cancellationTokenSource = CancellationTokenSource()
                    val currentLocation = fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token
                    ).await()
                    if (currentLocation != null) {
                        Log.d("PlotViewModel", "Ubicación actual obtenida: $currentLocation")
                        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                        onLocationChange(latLng)
                    } else {
                        Log.e("PlotViewModel", "No se pudo obtener la ubicación actual.")
                        setErrorMessage("No se pudo obtener la ubicación actual.")
                    }
                }
            } catch (e: SecurityException) {
                Log.e("PlotViewModel", "Error de permisos de ubicación: ${e.message}")
                setErrorMessage("Error de permisos de ubicación.")
            } catch (e: Exception) {
                Log.e("PlotViewModel", "Error al obtener la ubicación: ${e.message}")
                setErrorMessage("Error al obtener la ubicación: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetches the elevation for a given location with retry logic.
     *
     * @param latLng The location for which to fetch elevation.
     * @return The elevation value if successful, `null` otherwise.
     */
    suspend fun fetchElevation(latLng: LatLng): Double? {
        val maxAttempts = 3
        var attempts = 0
        var elevation: Double? = null
        var currentLatLng = latLng

        while (attempts < maxAttempts && elevation == null) {
            attempts++
            _attemptNumber.value = attempts // Actualizar el número de intento
            try {
                // Resetear el mensaje de error antes de cada intento
                _errorMessage.value = ""
                Log.d("PlotViewModel", "Intento $attempts de $maxAttempts para obtener la altitud.")

                // Realizar la solicitud de elevación
                val response = service.getElevation("${currentLatLng.latitude},${currentLatLng.longitude}")

                if (response.results.isNotEmpty()) {
                    elevation = response.results[0].elevation
                    Log.d("PlotViewModel", "Altitud obtenida: $elevation")
                } else {
                    Log.e("PlotViewModel", "No se encontraron resultados para la altitud en ($currentLatLng).")
                    _errorMessage.value = "Intento $attempts de $maxAttempts: No se encontraron resultados para la altitud en la ubicación actual."

                    // Ajustar latLng ligeramente para el próximo intento
                    currentLatLng = LatLng(
                        currentLatLng.latitude + 0.0001,
                        currentLatLng.longitude + 0.0001
                    )
                }
            } catch (e: Exception) {
                Log.e("PlotViewModel", "Error al obtener la altitud en el intento $attempts: ${e.message}")

                // Mensaje de error con intento fallido
                _errorMessage.value = "Error en el intento $attempts de $maxAttempts, latitud: ${currentLatLng.latitude}, longitud: ${currentLatLng.longitude} : ${e.message}"

                // Ajustar latLng ligeramente antes del siguiente intento
                currentLatLng = LatLng(
                    currentLatLng.latitude + 0.000001,
                    currentLatLng.longitude + 0.000001
                )

                // Esperar antes de reintentar para evitar sobrecargar la API
                delay(2000) // Esperar 2 segundos antes de reintentar
            }
        }

        if (elevation == null) {
            // Asignar un mensaje de error después de agotar los intentos
            _errorMessage.value = "Error al obtener la altitud después de $attempts intentos. Intente en otra ubicación o más tarde."
        }

        return elevation
    }


    /**
     * Creates a new plot by sending the plot data to the server.
     *
     * @param navController The NavController for navigation after plot creation.
     * @param context The current context, needed for displaying toasts.
     * @param farmId The ID of the farm to which the plot belongs.
     * @param plotName The name of the new plot.
     * @param coffeeVarietyName The variety of coffee planted in the plot.
     */
    fun onCreatePlot(navController: NavController, context: Context, farmId: Int, plotName: String, coffeeVarietyName: String) {
        if (latitude.value.isBlank() || longitude.value.isBlank() || plotName.isBlank()) {
            _errorMessage.value = "Todos los campos deben estar completos."
            Toast.makeText(context, "Error: Todos los campos deben estar completos.", Toast.LENGTH_LONG).show()
            return
        }

        _isLoading.value = true

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken() ?: run {
            _errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(context, "Error: No se encontró el token de sesión.", Toast.LENGTH_LONG).show()
            _isLoading.value = false
            return
        }

        val request = CreatePlotRequest(
            name = plotName,
            coffee_variety_name = coffeeVarietyName,
            latitude = latitude.value,
            longitude = longitude.value,
            altitude = altitude.value.toString(),
            farm_id = farmId
        )

        RetrofitInstance.api.createPlot(sessionToken, request).enqueue(object : retrofit2.Callback<CreateFarmResponse> {
            override fun onResponse(call: Call<CreateFarmResponse>, response: retrofit2.Response<CreateFarmResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        Toast.makeText(context, "Lote creado exitosamente", Toast.LENGTH_LONG).show()
                        navController.navigate("farmInformationView/$farmId") {
                            popUpTo("farmInformationView/$farmId") { inclusive = true }
                        }
                    } else if (responseBody?.status == "error") {
                        val errorMsg = responseBody.message ?: "Error desconocido."
                        _errorMessage.value = errorMsg
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    } else {
                        _errorMessage.value = "Respuesta inesperada del servidor."
                        Toast.makeText(context, "Respuesta inesperada del servidor.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    _errorMessage.value = "Error al crear el lote."
                    Toast.makeText(context, "Error al crear el lote.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<CreateFarmResponse>, t: Throwable) {
                _isLoading.value = false
                val connectionErrorMsg = "Error de conexión"
                _errorMessage.value = connectionErrorMsg
                Toast.makeText(context, connectionErrorMsg, Toast.LENGTH_LONG).show()
            }
        })
    }



}
