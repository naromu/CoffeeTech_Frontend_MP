package com.example.coffetech.model

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Data classes for API requests and responses

/**
 * Generic response type for API responses that can handle different data types.
 *
 * @param T The type of data expected in the response.
 * @property status The status of the API response.
 * @property message The message associated with the API response.
 * @property data The data returned by the API, if applicable.
 */
data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null
)



//Transaction








//Reports
data class FinancialReportResponse(
    val status: String,
    val message: String,
    val data: FinancialReportData
)

data class FinancialReportData(
    val finca_nombre: String,
    val lotes_incluidos: List<String>,
    val periodo: String,
    val plot_financials: List<PlotFinancial>,
    val farm_summary: FarmSummary,
    val transaction_history: List<TransactionHistory>? // Campo opcional para historial de transacciones

)

data class TransactionHistory(
    val date: String,
    val plot_name: String,
    val farm_name: String,
    val transaction_type: String,
    val transaction_category: String,
    val creator_name: String,
    val value: Long
)

data class PlotFinancial(
    val plot_id: Int,
    val plot_name: String,
    val ingresos: Long,
    val gastos: Long,
    val balance: Long,
    val ingresos_por_categoria: List<CategoryAmount>,
    val gastos_por_categoria: List<CategoryAmount>
)

data class FarmSummary(
    val total_ingresos: Long,
    val total_gastos: Long,
    val balance_financiero: Long,
    val ingresos_por_categoria: List<CategoryAmount>,
    val gastos_por_categoria: List<CategoryAmount>
)

data class CategoryAmount(
    val category_name: String,
    val monto: Long
)
data class FinancialReportRequest(
    val plot_ids: List<Int>,
    val fechaInicio: String, // Formato "yyyy-MM-dd"
    val fechaFin: String   ,  // Formato "yyyy-MM-dd"
    val include_transaction_history: Boolean // Nuevo parámetro para incluir historial de transacciones


)

data class DetectionHistoryRequest(
    @SerializedName("plot_ids") val plotIds: List<Int>,
    @SerializedName("fechaInicio") val fechaInicio: String, // Formato "yyyy-MM-dd"
    @SerializedName("fechaFin") val fechaFin: String // Formato "yyyy-MM-dd"
)

data class DetectionHistoryResponse(
    val status: String,
    val message: String,
    val data: DetectionHistoryData
)

data class DetectionHistoryData(
    val detections: List<DetectionHistory>
)

data class DetectionHistory(
    val date: String,
    val person_name: String,
    val detection: String,
    val recommendation: String,
    val cultural_work: String,
    val lote_name: String,
    val farm_name: String
)





// API service interface for interacting with backend services

/**
 * Retrofit API service interface for interacting with backend services.
 */

interface ApiService {


    //Reports
    @POST("/reports/financial-report")
    fun getFinancialReport(
        @Query("session_token") sessionToken: String,
        @Body request: FinancialReportRequest
    ): Call<FinancialReportResponse>

    @POST("/reports/detection-report")
    fun getDetectionHistory(
        @Query("session_token") sessionToken: String,
        @Body request: DetectionHistoryRequest
    ): Call<DetectionHistoryResponse>




}




