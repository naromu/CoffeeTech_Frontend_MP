// ResultHealthCheckViewModel.kt
package com.example.coffetech.viewmodel.healthcheck

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.coffetech.model.*
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class DetectionResult(
    val imagenNumero: Int,
    val prediccion: String,
    val recomendacion: String,
    val predictionId: Int // Nuevo campo
)


class ResultHealthCheckViewModel : ViewModel() {

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _detectionResults = MutableStateFlow<List<DetectionResult>>(emptyList())
    val detectionResults: StateFlow<List<DetectionResult>> = _detectionResults

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    /**
     * Función para realizar la llamada a la API según el tipo de chequeo
     */
    fun fetchDetectionResults(
        culturalWorksName: String,
        culturalWorkTasksId: Int,
        imagesBase64: List<String>,
        context: Context
    ) {
        _isLoading.value = true
        _errorMessage.value = ""

        // Obtener el sessionToken usando SharedPreferencesHelper
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

        val detectionRequest = DetectionRequest(
            cultural_work_tasks_id = culturalWorkTasksId,
            images = imagesBase64.map { DetectionImage(image_base64 = it) }
        )

        val call: Call<*> = when (culturalWorksName) {
            "Chequeo de Salud" -> RetrofitInstance.api.detectDiseaseDeficiency(sessionToken, detectionRequest)
            "Chequeo de estado de maduración" -> RetrofitInstance.api.detectMaturity(sessionToken, detectionRequest)
            else -> {
                _isLoading.value = false
                _errorMessage.value = "Tipo de chequeo no reconocido."
                Toast.makeText(
                    context,
                    "Tipo de chequeo no reconocido.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        when (culturalWorksName) {
            "Chequeo de Salud" -> {
                (call as Call<DiseaseDeficiencyResponse>).enqueue(object : Callback<DiseaseDeficiencyResponse> {
                    override fun onResponse(
                        call: Call<DiseaseDeficiencyResponse>,
                        response: Response<DiseaseDeficiencyResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            val body = response.body()
                            body?.let {
                                if (it.status == "success") {
                                    val results = it.data.map { detection ->
                                        DetectionResult(
                                            imagenNumero = detection.imagen_numero,
                                            prediccion = detection.prediccion,
                                            recomendacion = detection.recomendacion,
                                            predictionId = detection.prediction_id // Asegúrate de que este campo existe en tu modelo

                                        )
                                    }
                                    _detectionResults.value = results
                                } else {
                                    _errorMessage.value = it.message
                                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                }
                            } ?: run {
                                _errorMessage.value = "Respuesta vacía del servidor."
                                Toast.makeText(context, "Respuesta vacía del servidor.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            // Manejar errores de respuesta del servidor
                            val errorBody = response.errorBody()?.string()
                            errorBody?.let {
                                try {
                                    val errorJson = JSONObject(it)
                                    val errorMsg = if (errorJson.has("message")) {
                                        errorJson.getString("message")
                                    } else {
                                        "Error desconocido en la respuesta del servidor."
                                    }
                                    _errorMessage.value = errorMsg
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    _errorMessage.value = "Error al procesar la respuesta del servidor."
                                    Toast.makeText(
                                        context,
                                        "Error al procesar la respuesta del servidor.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } ?: run {
                                _errorMessage.value = "Respuesta vacía del servidor."
                                Toast.makeText(context, "Respuesta vacía del servidor.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<DiseaseDeficiencyResponse>, t: Throwable) {
                        _isLoading.value = false
                        _errorMessage.value = "Error de conexión"
                        Toast.makeText(
                            context,
                            "Error de conexión",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }
            "Chequeo de estado de maduración" -> {
                (call as Call<MaturityResponse>).enqueue(object : Callback<MaturityResponse> {
                    override fun onResponse(
                        call: Call<MaturityResponse>,
                        response: Response<MaturityResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            val body = response.body()
                            body?.let {
                                if (it.status == "success") {
                                    val results = it.data.detalles_por_imagen.map { detection ->
                                        DetectionResult(
                                            imagenNumero = detection.imagen_numero,
                                            prediccion = detection.prediccion,
                                            recomendacion = detection.recomendacion,
                                            predictionId = detection.prediction_id // Asegúrate de que este campo existe en tu modelo

                                        )
                                    }
                                    _detectionResults.value = results
                                } else {
                                    _errorMessage.value = it.message
                                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                }
                            } ?: run {
                                _errorMessage.value = "Respuesta vacía del servidor."
                                Toast.makeText(context, "Respuesta vacía del servidor.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            // Manejar errores de respuesta del servidor
                            val errorBody = response.errorBody()?.string()
                            errorBody?.let {
                                try {
                                    val errorJson = JSONObject(it)
                                    val errorMsg = if (errorJson.has("message")) {
                                        errorJson.getString("message")
                                    } else {
                                        "Error desconocido en la respuesta del servidor."
                                    }
                                    _errorMessage.value = errorMsg
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    _errorMessage.value = "Error al procesar la respuesta del servidor."
                                    Toast.makeText(
                                        context,
                                        "Error al procesar la respuesta del servidor.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } ?: run {
                                _errorMessage.value = "Respuesta vacía del servidor."
                                Toast.makeText(context, "Respuesta vacía del servidor.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<MaturityResponse>, t: Throwable) {
                        _isLoading.value = false
                        _errorMessage.value = "Error de conexión"
                        Toast.makeText(
                            context,
                            "Error de conexión: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }
        }
    }

    /**
     * Función para simular el guardado de resultados
     */
// ResultHealthCheckViewModel.kt

    fun acceptPredictions(context: Context, onComplete: () -> Unit) {
        _isLoading.value = true
        _errorMessage.value = ""
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

        val predictionIds = _detectionResults.value.map { it.predictionId }
        val requestBody = PredictionIdsRequest(prediction_ids = predictionIds)

        RetrofitInstance.api.acceptDetection(sessionToken, requestBody)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.status == "success") {
                            onComplete()
                        } else {
                            _errorMessage.value = body?.message ?: "Error desconocido al aceptar las predicciones."
                            Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        _errorMessage.value = "Error al aceptar las predicciones."
                        Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión"
                    Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
                }
            })
    }


    /**
     * Función para simular el descarte de resultados
     */
    // ResultHealthCheckViewModel.kt

    fun discardPredictions(context: Context, onComplete: () -> Unit) {
        _isLoading.value = true
        _errorMessage.value = ""
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

        val predictionIds = _detectionResults.value.map { it.predictionId }
        val requestBody = PredictionIdsRequest(prediction_ids = predictionIds)

        RetrofitInstance.api.discardDetection(sessionToken, requestBody)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.status == "success") {
                            onComplete()
                        } else {
                            _errorMessage.value = body?.message ?: "Error desconocido al descartar las predicciones."
                            Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        _errorMessage.value = "Error al descartar las predicciones."
                        Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión"
                    Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
                }
            })
    }

}
