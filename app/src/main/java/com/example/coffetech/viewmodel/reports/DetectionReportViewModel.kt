// DetectionReportViewModel.kt
package com.example.coffetech.viewmodel.reports

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffetech.model.DetectionHistory
import com.example.coffetech.model.DetectionHistoryRequest
import com.example.coffetech.model.DetectionHistoryResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetectionReportViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _detectionData = MutableStateFlow<List<DetectionHistory>?>(null)
    val detectionData: StateFlow<List<DetectionHistory>?> = _detectionData


    private val _nutrientDeficiencies = MutableStateFlow<List<NutrientDeficiency>>(emptyList())
    val nutrientDeficiencies: StateFlow<List<NutrientDeficiency>> = _nutrientDeficiencies

    private val _healthStatuses = MutableStateFlow<List<HealthStatus>>(emptyList())
    val healthStatuses: StateFlow<List<HealthStatus>> = _healthStatuses

    private val _maturityStatusPerDate = MutableStateFlow<List<MaturityStatusPerDate>>(emptyList())
    val maturityStatusPerDate: StateFlow<List<MaturityStatusPerDate>> = _maturityStatusPerDate

    // Nuevo StateFlow para el nombre del usuario
    private val _username = MutableStateFlow("Usuario")
    val username: StateFlow<String> = _username

    fun processMaturityStatus(detections: List<DetectionHistory>) {
        // Filtrar detecciones de "Chequeo de estado de maduración"
        val maturityDetections = detections.filter { it.cultural_work == "Chequeo de estado de maduración" }

        // Definir las clases de interés
        val classes = listOf("Verde", "Pintón", "Maduro", "Sobremaduro", "No hay granos")

        // Agrupar por fecha
        val groupedByDate = maturityDetections.groupBy { it.date }

        // Procesar cada grupo para sumar los conteos
        val maturityStatusList = groupedByDate.map { (date, detectionsOnDate) ->
            val counts = mutableMapOf<String, Int>()
            detectionsOnDate.forEach { detection ->
                // Parsear la cadena de detección
                val parts = detection.detection.split(",").map { it.trim() }
                parts.forEach { part ->
                    // Validar el formato esperado para evitar IndexOutOfBoundsException
                    val keyValue = part.split("=")
                    if (keyValue.size == 2) { // Validar que haya exactamente 2 elementos
                        val className = keyValue[0].trim()
                        val count = keyValue[1].trim().toIntOrNull() ?: 0
                        counts[className] = counts.getOrDefault(className, 0) + count
                    } else {
                        Log.e(TAG, "Formato inesperado en detección: $part")
                    }
                }
            }
            // Asegurar que todas las clases estén presentes, incluso con 0
            val completeCounts = classes.associateWith { className -> counts[className] ?: 0 }
            MaturityStatusPerDate(date, completeCounts)
        }

        _maturityStatusPerDate.value = maturityStatusList.sortedBy { it.date }
    }

    fun processDetectionData(detections: List<DetectionHistory>) {
        // Procesar deficiencias de nutrientes
        val deficiencies = detections
            .filter { it.cultural_work == "Chequeo de Salud" && it.detection.contains("N", ignoreCase = true) }
            .groupBy { it.lote_name }
            .map { (lote, detectionsLote) ->
                val nitrogenN = detectionsLote.count { it.detection.contains("Deficiencia de nitrógeno", ignoreCase = true) }
                val phosphorusP = detectionsLote.count { it.detection.contains("Deficiencia de fósforo", ignoreCase = true) }
                val potassiumK = detectionsLote.count { it.detection.contains("Deficiencia de potasio", ignoreCase = true) }
                NutrientDeficiency(
                    loteName = lote,
                    nitrogenN = nitrogenN,
                    phosphorusP = phosphorusP,
                    potassiumK = potassiumK
                )
            }
        _nutrientDeficiencies.value = deficiencies

        // Procesar estados de salud
        val healthStatusList = detections
            .filter { it.cultural_work == "Chequeo de Salud" }
            .groupBy { it.lote_name }
            .map { (lote, detectionsLote) ->
                val cercospora = detectionsLote.count { it.detection.equals("Cercospora", ignoreCase = true) }
                val ferrugem = detectionsLote.count { it.detection.equals("Mancha de hierro", ignoreCase = true) }
                val leafRust = detectionsLote.count { it.detection.equals("Roya", ignoreCase = true) }
                val hojaSana = detectionsLote.count { it.detection.equals("Hoja_sana", ignoreCase = true) }
                HealthStatus(
                    loteName = lote,
                    cercospora = cercospora,
                    ferrugem = ferrugem,
                    leafRust = leafRust,
                    hojaSana = hojaSana
                )
            }
        _healthStatuses.value = healthStatusList
    }

    // Actualiza tu función onResponse para procesar los datos
    fun onDetectionDataReceived(detections: List<DetectionHistory>, username: String) {
        _detectionData.value = detections
        processDetectionData(detections)
        processMaturityStatus(detections)
        _username.value = username

    }

    fun getDetectionHistory(
        context: Context,
        plotIds: List<Int>,
        fechaInicio: String,
        fechaFin: String,
    ) {

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken() ?: run {
            Log.e(TAG, "No se encontró el token de sesión.")
            _errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(
                context,
                "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            _isLoading.value = false
            return
        }
        val username = sharedPreferencesHelper.getUserName()

        _isLoading.value = true
        _errorMessage.value = null

        val request = DetectionHistoryRequest(
            plotIds = plotIds,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )

        RetrofitInstance.api.getDetectionHistory(sessionToken, request)
            .enqueue(object : Callback<DetectionHistoryResponse> {
                override fun onResponse(
                    call: Call<DetectionHistoryResponse>,
                    response: Response<DetectionHistoryResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.status == "success") {
                            onDetectionDataReceived(body.data.detections, username)
                        } else {
                            _errorMessage.value = body?.message ?: "Error desconocido."
                        }
                    } else {
                        _errorMessage.value = "Error de servidor: ${response.code()}"
                    }
                }



                override fun onFailure(call: Call<DetectionHistoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = t.message ?: "Fallo al conectar con el servidor."
                    Log.e("DetectionReportViewModel", "Error: ${t.message}")
                }
            })
    }

}

data class NutrientDeficiency(
    val loteName: String,
    val nitrogenN: Int,
    val phosphorusP: Int,
    val potassiumK: Int
)
data class HealthStatus(
    val loteName: String,
    val cercospora: Int,
    val ferrugem: Int,
    val leafRust: Int,
    val hojaSana: Int
)

data class MaturityStatusPerDate(
    val date: String,
    val counts: Map<String, Int>
)
