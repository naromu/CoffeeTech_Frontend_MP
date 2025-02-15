package com.example.coffetech.viewmodel.flowering

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.CreateFloweringRequest
import com.example.coffetech.model.CreateFloweringResponse
import com.example.coffetech.model.RetrofitInstance
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

class AddFloweringViewModel: ViewModel() {
    // Estados para los datos
    private val _flowering_date = MutableStateFlow("")
    val flowering_date: StateFlow<String> = _flowering_date.asStateFlow()

    private val _harvest_date = MutableStateFlow("")
    val harvest_date: StateFlow<String> = _harvest_date.asStateFlow()

    private val _floweringName = MutableStateFlow<List<String>>(emptyList())
    val floweringName: StateFlow<List<String>> = _floweringName.asStateFlow()

    private val _selectedFloweringName = MutableStateFlow("Seleccione un tipo de floración")
    val selectedFloweringName: StateFlow<String> = _selectedFloweringName.asStateFlow()

    var errorMessage = MutableStateFlow("")
        private set
    var isLoading = MutableStateFlow(false)
        private set

    // Estado para indicar si el formulario fue enviado
    var isFormSubmitted = MutableStateFlow(false)
        private set


    private var previousFloweringDate = ""
    private var previousHarvestDate = ""
    private var previousFloweringName = ""

    /**
     * Función para actualizar la fecha de floración.
     */
    fun onFloweringDateChange(newDate: String) {
        _flowering_date.value = newDate
        resetErrorMessage()

    }

    /**
     * Función para actualizar el tipo de floración seleccionado.
     */
    fun onFloweringNameChange(newFloweringName: String) {
        _selectedFloweringName.value = newFloweringName
        resetErrorMessage()

    }

    /**
     * Función para actualizar la fecha de cosecha.
     */
    fun onHarvestDateChange(newDate: String) {
        _harvest_date.value = newDate
        resetErrorMessage()

    }
    // Dentro de AddFloweringViewModel
    fun clearHarvestDate() {
        _harvest_date.value = ""
        resetErrorMessage()
    }


    /**
     * Función para resetear el mensaje de error.
     */
    private fun resetErrorMessage() {
        if (_flowering_date.value != previousFloweringDate ||
            _harvest_date.value != previousHarvestDate ||
            _selectedFloweringName.value != previousFloweringName
        ) {

            // Actualizar los valores anteriores
            previousFloweringDate = _flowering_date.value
            previousHarvestDate = _harvest_date.value
            previousFloweringName = _selectedFloweringName.value

            // Resetear mensaje de error si ha habido un cambio
            if (errorMessage.value.isNotEmpty()) {
                errorMessage.value = ""
                isFormSubmitted.value = false
            }
        }
    }

        /**
         * Función para cargar los tipos de floración disponibles.
         * Puedes modificar esta función para obtener los datos desde una fuente dinámica si es necesario.
         */
        fun loadFloweringTypes() {
            // Ejemplo estático, puedes reemplazarlo con una llamada a la API si es necesario
            _floweringName.value = listOf("Principal", "Mitaca")
        }

        /**
         * Función para validar los inputs del formulario.
         */
        fun validateInputs(): Boolean {
            isFormSubmitted.value = true

            if (_flowering_date.value.isBlank()) {
                errorMessage.value = "La fecha de floración no puede estar vacía."
                return false
            }

            if (_selectedFloweringName.value == "Seleccione un tipo de floración") {
                errorMessage.value = "Debe seleccionar un tipo de floración válido."
                return false
            }

            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("America/Bogota")

                val floweringDate = dateFormat.parse(_flowering_date.value)
                val currentDate = Calendar.getInstance(TimeZone.getTimeZone("America/Bogota")).time

                if (floweringDate.after(currentDate)) {
                    errorMessage.value = "La fecha de floración no puede ser posterior a la fecha actual."
                    return false
                }

                if (_harvest_date.value.isNotBlank()) {
                    val harvestDate = dateFormat.parse(_harvest_date.value)

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
                        errorMessage.value = "La fecha de cosecha debe ser al menos 24 semanas después de la fecha de floración."
                        return false
                    }
                }
            } catch (e: Exception) {
                errorMessage.value = "Formato de fecha inválido."
                return false
            }

            errorMessage.value = "" // Limpiar el mensaje de error si no hay problemas
            return true
        }

        /**
         * Función para crear una nueva floración.
         */
        fun onCreate(navController: NavController, context: Context, plotId: Int) {
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
            val createFloweringRequest = CreateFloweringRequest(
                plot_id = plotId,
                flowering_type_name = _selectedFloweringName.value,
                flowering_date = _flowering_date.value,
                harvest_date = if (_harvest_date.value.isBlank()) null else _harvest_date.value
            )

            // Realizar la solicitud al servidor
            RetrofitInstance.api.createFlowering(sessionToken, createFloweringRequest)
                .enqueue(object :
                    Callback<CreateFloweringResponse> {
                    override fun onResponse(
                        call: Call<CreateFloweringResponse>,
                        response: Response<CreateFloweringResponse>
                    ) {
                        isLoading.value = false
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            responseBody?.let {
                                if (it.status == "success") {
                                    Toast.makeText(
                                        context,
                                        "Floración creada exitosamente.",
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
                                        "Error desconocido al crear la floración."
                                    }
                                    this@AddFloweringViewModel.errorMessage.value = errorMsg
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    this@AddFloweringViewModel.errorMessage.value =
                                        "Error al procesar la respuesta del servidor."
                                    Toast.makeText(
                                        context,
                                        "Error al procesar la respuesta del servidor.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } ?: run {
                                this@AddFloweringViewModel.errorMessage.value =
                                    "Respuesta vacía del servidor."
                                Toast.makeText(
                                    context,
                                    "Respuesta vacía del servidor.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<CreateFloweringResponse>, t: Throwable) {
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
    }


