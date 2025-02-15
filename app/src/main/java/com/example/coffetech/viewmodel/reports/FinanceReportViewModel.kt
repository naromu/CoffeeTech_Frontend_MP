package com.example.coffetech.viewmodel.reports

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.FinancialReportData
import com.example.coffetech.model.FinancialReportRequest
import com.example.coffetech.model.FinancialReportResponse

import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log // Importación añadida para Logcat

class FinanceReportViewModel : ViewModel() {

    private val TAG = "FinanceReportViewModel" // Tag para los logs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _reportData = MutableStateFlow<FinancialReportData?>(null)
    val reportData: StateFlow<FinancialReportData?> = _reportData.asStateFlow()

    private val _username = MutableStateFlow("Usuario")
    val username: StateFlow<String> = _username

    fun getFinancialReport(
        context: Context,
        plotIds: List<Int>,
        fechaInicio: String,
        fechaFin: String,
        includeTransactionHistory: Boolean // Parámetro adicional

    ) {
        Log.d(TAG, "Obteniendo reporte financiero con plotIds=$plotIds, fechaInicio=$fechaInicio, fechaFin=$fechaFin, includeTransactionHistory=$includeTransactionHistory")
        _isLoading.value = true
        _errorMessage.value = null

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
        // Obtener y actualizar el username
        val username = sharedPreferencesHelper.getUserName()
        Log.d(TAG, "Nombre de usuario obtenido: $username")
        _username.value = username // Aquí actualizamos el StateFlow

        Log.d(TAG, "Token de sesión obtenido: $sessionToken")

        val request = FinancialReportRequest(
            plot_ids = plotIds,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            include_transaction_history = includeTransactionHistory // Incluir en la solicitud
        )

        Log.d(TAG, "Realizando llamada a la API con request: $request")

        RetrofitInstance.api.getFinancialReport(sessionToken, request)
            .enqueue(object : Callback<FinancialReportResponse> {
                override fun onResponse(
                    call: Call<FinancialReportResponse>,
                    response: Response<FinancialReportResponse>
                ) {
                    Log.d(TAG, "Respuesta de la API recibida con código: ${response.code()}")
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d(TAG, "Respuesta de la API exitosa: $responseBody")
                        responseBody?.let {
                            if (it.status == "success") {
                                _reportData.value = it.data
                                Log.d(TAG, "Datos del reporte actualizados: ${it.data}")
                            } else {
                                _errorMessage.value = it.message ?: "Error desconocido al generar el reporte."
                                Log.e(TAG, "Error en la respuesta de la API: ${it.message}")
                            }
                        } ?: run {
                            _errorMessage.value = "No se pudo generar el reporte."
                            Log.e(TAG, "Respuesta de la API es nula")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Respuesta de la API no exitosa: $errorBody")
                        errorBody?.let {
                            try {
                                val errorJson = JSONObject(it)
                                _errorMessage.value = errorJson.optString(
                                    "message",
                                    "Error desconocido al generar el reporte."
                                )
                                Log.e(TAG, "Mensaje de error de la API: ${errorJson.optString("message")}")
                            } catch (e: Exception) {
                                _errorMessage.value = "Error al procesar la respuesta del servidor."
                                Log.e(TAG, "Excepción al procesar el errorBody: ${e.message}")
                            }
                        } ?: run {
                            _errorMessage.value = "Error al generar el reporte: respuesta vacía del servidor."
                            Log.e(TAG, "Respuesta de la API sin cuerpo de error")
                        }
                    }
                }

                override fun onFailure(call: Call<FinancialReportResponse>, t: Throwable) {
                    Log.e(TAG, "Fallo en la llamada a la API: ${t.message}")
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión"
                }
            })
    }

    fun generarRecomendaciones(): List<LoteRecommendation> {
        Log.d(TAG, "Generando recomendaciones basadas en los datos del reporte")
        val recomendaciones = mutableListOf<LoteRecommendation>()

        reportData.value?.plot_financials?.forEach { plot ->
            val ingresos = plot.ingresos
            val gastos = plot.gastos
            val balance = plot.balance

            val rendimiento = when {
                balance > 0.2 * ingresos -> "Excelente rendimiento"
                balance > 0.1 * ingresos -> "Buen rendimiento"
                else -> "Rendimiento bajo"
            }

            Log.d(TAG, "Rendimiento para lote ${plot.plot_name}: $rendimiento")

            val recomendacionesLote = mutableListOf<String>()

            when (rendimiento) {
                "Excelente rendimiento" -> {
                    recomendacionesLote.add("Recomendable mantener o incrementar la inversión.")
                }
                "Buen rendimiento" -> {
                    recomendacionesLote.add("Oportunidades para optimizar gastos en categorías específicas.")
                }
                "Rendimiento bajo" -> {
                    recomendacionesLote.add("Se recomienda revisar gastos y explorar estrategias para aumentar ingresos.")
                }
            }

            // Añadir recomendaciones específicas para optimizar gastos
            if (rendimiento != "Excelente rendimiento") {
                plot.gastos_por_categoria.forEach { categoria ->
                    when (categoria.category_name) {
                        "Pagos a colaboradores" -> {
                            recomendacionesLote.add("Revisar y optimizar los pagos a colaboradores.")
                        }
                        "Fertilizantes" -> {
                            recomendacionesLote.add("Reducir el uso de fertilizantes o buscar alternativas más económicas.")
                        }
                        "Plaguicidas/herbicidas" -> {
                            recomendacionesLote.add("Evaluar el uso de plaguicidas/herbicidas y buscar alternativas sostenibles.")
                        }
                        "Otros" -> {
                            recomendacionesLote.add("Analizar los gastos en 'Otros' y buscar posibles optimizaciones.")
                        }
                    }
                }
            }

            // Recomendaciones para incrementar ingresos
            if (rendimiento == "Rendimiento bajo") {
                plot.ingresos_por_categoria.forEach { categoria ->
                    when (categoria.category_name) {
                        "Venta de café" -> {
                            recomendacionesLote.add("Explorar nuevos mercados para la venta de café o mejorar la calidad del producto.")
                        }
                        "Otros" -> {
                            recomendacionesLote.add("Diversificar las fuentes de ingresos, como añadir otros productos agrícolas.")
                        }
                    }
                }
            }

            recomendaciones.add(
                LoteRecommendation(
                    loteNombre = plot.plot_name,
                    rendimiento = rendimiento,
                    recomendaciones = recomendacionesLote
                )
            )
            Log.d(TAG, "Recomendaciones para lote ${plot.plot_name}: $recomendacionesLote")
        }

        Log.d(TAG, "Recomendaciones generadas: $recomendaciones")
        return recomendaciones
    }



}
data class LoteRecommendation(
    val loteNombre: String,
    val rendimiento: String,
    val recomendaciones: List<String>
)