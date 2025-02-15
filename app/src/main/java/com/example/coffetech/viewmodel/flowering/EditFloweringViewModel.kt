package com.example.coffetech.viewmodel.flowering

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.DeleteFloweringResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.UpdateFloweringRequest
import com.example.coffetech.model.UpdateFloweringResponse
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class EditFloweringViewModel: ViewModel() {

    private val _flowering_date = MutableStateFlow("")
    val flowering_date: StateFlow<String> = _flowering_date.asStateFlow()

    private val _harvest_date = MutableStateFlow("")
    val harvest_date: StateFlow<String> = _harvest_date.asStateFlow()

    private val _floweringName = MutableStateFlow<List<String>>(emptyList())
    val floweringName: StateFlow<List<String>> = _floweringName.asStateFlow()

    private val _selectedFloweringName = MutableStateFlow("Seleccione una floración")
    val selectedFloweringName: StateFlow<String> = _selectedFloweringName.asStateFlow()

    var errorMessage = MutableStateFlow("")
        private set
    var isLoading = MutableStateFlow(false)
        private set

    // Estado para rastrear si hay cambios pendientes
    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    // Almacenar flowering_id para futuras peticiones
    private var floweringId: Int = 0

    /**
     * Inicializar el ViewModel con los datos existentes de la floración.
     */
    fun initialize(
        floweringId: Int,
        floweringTypeName: String,
        floweringDate: String,
        harvestDate: String
    ) {
        this.floweringId = floweringId
        _selectedFloweringName.value = floweringTypeName
        _flowering_date.value = floweringDate
        _harvest_date.value = harvestDate.ifEmpty { "" } // Manejar vacío
        // Guardar valores iniciales para comparación
        initialHarvestDate = harvestDate
    }

    // Guardar valor inicial de harvestDate para comparación
    private var initialHarvestDate: String = ""

    /**
     * Función para actualizar la fecha de cosecha.
     */
    fun onHarvestDateChange(newDate: String) {
        if (_harvest_date.value != newDate) {
            _harvest_date.value = newDate
            _hasChanges.value = newDate != initialHarvestDate
            errorMessage.value = ""
        }
    }

    /**
     * Función para limpiar la fecha de cosecha.
     */
    fun clearHarvestDate() {
        if (_harvest_date.value.isNotEmpty()) {
            _harvest_date.value = ""
            _hasChanges.value = true

        }
    }

    /**
     * Función para cargar los tipos de floración disponibles.
     */
    fun loadFloweringTypes() {
        // Ejemplo estático, puedes reemplazarlo con una llamada a la API si es necesario
        _floweringName.value = listOf("Principal", "Mitaca")
    }

    /**
     * Función para validar los inputs del formulario.
     */
    private fun validateInputs(): Boolean {
        if (_harvest_date.value.isNotBlank()) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val floweringDate = dateFormat.parse(_flowering_date.value)
                val harvestDate = dateFormat.parse(_harvest_date.value)

                dateFormat.timeZone = TimeZone.getTimeZone("America/Bogota")
                val currentDate = Calendar.getInstance(TimeZone.getTimeZone("America/Bogota")).time


                if (harvestDate.before(floweringDate)) {
                    errorMessage.value = "La fecha de cosecha no puede ser antes de la fecha de floración."
                    return false
                }
                if (harvestDate.after(currentDate)) {
                    errorMessage.value = "La fecha de cosecha no puede ser posterior a la fecha actual."
                    return false
                }

                val diffInMillis = harvestDate.time - floweringDate.time
                val diffInWeeks = diffInMillis / (7 * 24 * 60 * 60 * 1000)
                if (diffInWeeks < 24) {
                    errorMessage.value =
                        "La fecha de cosecha debe ser al menos 24 semanas después de la fecha de floración."
                    return false
                }
            } catch (e: Exception) {
                errorMessage.value = "Formato de fecha inválido."
                return false
            }
        }

        errorMessage.value = "" // Limpiar el mensaje de error si no hay problemas
        return true
    }

    /**
     * Función para editar la floración.
     */
    fun editFlowering(context: Context, navController: NavController) {
        if (!validateInputs()) {
            return
        }

        errorMessage.value = ""
        isLoading.value = true

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken() ?: run {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(
                context,
                "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            isLoading.value = false
            return
        }

        // Crear el objeto de solicitud
        val updateFloweringRequest = UpdateFloweringRequest(
            flowering_id = floweringId,
            harvest_date = _harvest_date.value
        )

        // Realizar la solicitud al servidor
        RetrofitInstance.api.updateFlowering(sessionToken, updateFloweringRequest)
            .enqueue(object : Callback<UpdateFloweringResponse> {
                override fun onResponse(
                    call: Call<UpdateFloweringResponse>,
                    response: Response<UpdateFloweringResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            if (it.status == "success") {
                                Toast.makeText(
                                    context,
                                    it.message,
                                    Toast.LENGTH_LONG
                                ).show()
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
                                val errorMsg = if (errorJson.has("message")) {
                                    errorJson.getString("message")
                                } else {
                                    "Error desconocido al actualizar la floración."
                                }
                                this@EditFloweringViewModel.errorMessage.value = errorMsg
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                this@EditFloweringViewModel.errorMessage.value =
                                    "Error al procesar la respuesta del servidor."
                                Toast.makeText(
                                    context,
                                    "Error al procesar la respuesta del servidor.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } ?: run {
                            this@EditFloweringViewModel.errorMessage.value =
                                "Respuesta vacía del servidor."
                            Toast.makeText(
                                context,
                                "Respuesta vacía del servidor.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UpdateFloweringResponse>, t: Throwable) {
                    isLoading.value = false
                    errorMessage.value = "Error de conexión"
                    Toast.makeText(
                        context,
                        "Error de conexión",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /**
     * Función para eliminar la floración.
     */
    fun deleteFlowering(context: Context, navController: NavController) {
        errorMessage.value = ""
        isLoading.value = true

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken() ?: run {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(
                context,
                "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            isLoading.value = false
            return
        }

        // Realizar la solicitud al servidor
        RetrofitInstance.api.deleteFlowering(floweringId, sessionToken)
            .enqueue(object : Callback<DeleteFloweringResponse> {
                override fun onResponse(
                    call: Call<DeleteFloweringResponse>,
                    response: Response<DeleteFloweringResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            if (it.status == "success") {
                                Toast.makeText(
                                    context,
                                    it.message,
                                    Toast.LENGTH_LONG
                                ).show()
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
                                val errorMsg = if (errorJson.has("message")) {
                                    errorJson.getString("message")
                                } else {
                                    "Error desconocido al eliminar la floración."
                                }
                                this@EditFloweringViewModel.errorMessage.value = errorMsg
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                this@EditFloweringViewModel.errorMessage.value =
                                    "Error al procesar la respuesta del servidor."
                                Toast.makeText(
                                    context,
                                    "Error al procesar la respuesta del servidor.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } ?: run {
                            this@EditFloweringViewModel.errorMessage.value =
                                "Respuesta vacía del servidor."
                            Toast.makeText(
                                context,
                                "Respuesta vacía del servidor.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<DeleteFloweringResponse>, t: Throwable) {
                    isLoading.value = false
                    errorMessage.value = "Error de conexión"
                    Toast.makeText(
                        context,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}