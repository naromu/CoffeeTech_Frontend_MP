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


data class CreateInvitationRequest(
    val email: String,
    val suggested_role: String,
    val farm_id: Int
)

data class CreateInvitationResponse(
    val status: String,
    val message: String
)

data class Notification(
    val message: String,
    val date: String,
    val notification_type: String,
    val farm_id: Int,
    val reminder_time: String?,
    val notifications_id: Int,
    val user_id: Int,
    val invitation_id: Int,
    val notification_type_id: Int?,
    val status: String
)

data class NotificationResponse(
    val status: String,
    val message: String,
    val data: Any
)

/**
 * Data class representing the list collaborators response from the server.
 *
 * @property status The status of the list collaborators request.
 * @property message The message associated with the list collaborators response.
 * @property data The list of collaborators returned by the server.
 */
data class ListCollaboratorResponse(
    val status: String,
    val message: String,
    val data: List<CollaboratorResponse>
)

/**
 * Data class representing an individual farm's details.
 *
 * @property farm_id The ID of the farm.
 * @property name The name of the collaborator.
 * @property email The email of the collaborator.
 * @property role The role associated with the collaborator.
 */

data class CollaboratorResponse(
    val user_id: Int,
    val name: String,
    val email: String,
    val role: String
)

// Data class for editing collaborator request
data class EditCollaboratorRequest(
    val collaborator_user_id: Int,
    val new_role: String
)

// Data class for editing collaborator response
data class EditCollaboratorResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)


//MODULE FLOWERINGS
data class Flowering(
    val flowering_id: Int,
    val plot_id: Int,
    val flowering_date: String,
    val harvest_date: String? = null,
    val status: String,
    val flowering_type_name: String,
)

data class ListFloweringsResponse(
    val status: String,
    val message: String,
    val data: FloweringsData
)

data class GetFloweringHistoryResponse(
    val status: String,
    val message: String,
    val data: FloweringHistoryData
)

data class FloweringHistoryData(
    val flowerings: List<Flowering>
)


data class FloweringDataWrapper(
    val flowering: Flowering
)


data class GetActiveFloweringsResponse(
    val status: String,
    val message: String,
    val data: FloweringsData
)

data class FloweringsData(
    val flowerings: List<Flowering>
)

data class CreateFloweringRequest(
    val plot_id: Int,
    val flowering_type_name: String,
    val flowering_date: String, // Formato "YYYY-MM-DD"
    val harvest_date: String? = null // Opcional
)


data class CreateFloweringResponse(
    val status: String,
    val message: String,
    val data: FloweringDataWrapper? = null
)

data class UpdateFloweringRequest(
    val flowering_id: Int,
    val harvest_date: String
)

data class DeleteFloweringResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

data class UpdateFloweringResponse(
    val status: String,
    val message: String,
    val data: FloweringDataWrapper? = null
)

data class Task(
    val task: String,
    val start_date: String,
    val end_date: String,
    val programar: String // Agregado el campo 'programar'
)

data class RecommendationsData(
    val recommendations: Recommendation
)

data class Recommendation(
    val flowering_id: Int,
    val flowering_type_name: String,
    val flowering_date: String,
    val current_date: String,
    val tasks: List<Task>
)

data class GetRecommendationsResponse(
    val status: String,
    val message: String,
    val data: RecommendationsData
)

//CulturalWorksTask

data class CulturalWorkTask(
    val cultural_work_task_id: Int,
    val cultural_works_name: String,
    val owner_name: String,
    val collaborator_user_id: Int,
    val collaborator_name: String,
    val status: String,
    val task_date: String // Formato "yyyy-MM-dd"
)

data class CulturalWorkTasksData(
    val tasks: List<CulturalWorkTask>
)

data class ListCulturalWorkTasksResponse(
    val status: String,
    val message: String,
    val data: CulturalWorkTasksData
)

// Data class para un colaborador
data class Collaborator(
    val user_id: Int,
    val name: String
)

// Data class para la respuesta de colaboradores
data class CollaboratorsData(
    val collaborators: List<Collaborator>
)

data class CollaboratorsResponse(
    val status: String,
    val message: String,
    val data: CollaboratorsData
)

// Data class para la solicitud de creación de tarea cultural
data class CreateCulturalWorkTaskRequest(
    val cultural_works_name: String,
    val plot_id: Int,
    val reminder_owner: Boolean,
    val reminder_collaborator: Boolean,
    val collaborator_user_id: Int,
    val task_date: String // Formato "yyyy-MM-dd"
)

// Data class para la respuesta de creación de tarea cultural
data class CreateCulturalWorkTaskResponseData(
    val cultural_work_tasks_id: Int,
    val cultural_works_id: Int,
    val plot_id: Int,
    val status: String,
    val reminder_owner: Boolean,
    val reminder_collaborator: Boolean,
    val collaborator_user_id: Int,
    val owner_user_id: Int,
    val created_at: String,
    val task_date: String
)

data class CreateCulturalWorkTaskResponse(
    val status: String,
    val message: String,
    val data: CreateCulturalWorkTaskResponseData
)


data class GeneralCulturalWorkTask(
    val cultural_work_task_id: Int,
    val cultural_works_name: String,
    val collaborator_id: Int,
    val collaborator_name: String,
    val owner_name: String,
    val status: String,
    val task_date: String, // Formato "yyyy-MM-dd"
    val farm_name: String,
    val plot_name: String
)

data class GeneralCulturalWorkTasksData(
    val tasks: List<GeneralCulturalWorkTask>
)

data class GeneralListCulturalWorkTasksResponse(
    val status: String,
    val message: String,
    val data: GeneralCulturalWorkTasksData
)

// Data class para la solicitud de actualización de tarea cultural
data class UpdateCulturalWorkTaskRequest(
    val cultural_work_task_id: Int,
    val cultural_works_name: String,
    val collaborator_user_id: Int,
    val task_date: String // Formato "yyyy-MM-dd"
)

// Data class para la respuesta de actualización de tarea cultural
data class UpdateCulturalWorkTaskResponse(
    val status: String,
    val message: String,
    val data: UpdateCulturalWorkTaskData
)

data class UpdateCulturalWorkTaskData(
    val cultural_work_task_id: Int,
    val cultural_works_name: String,
    val collaborator_user_id: Int,
    val task_date: String,
    val status: String
)

// Data class para la solicitud de eliminación de tarea cultural
data class DeleteCulturalWorkTaskRequest(
    val cultural_work_task_id: Int
)

// Data class para la respuesta de eliminación de tarea cultural
data class DeleteCulturalWorkTaskResponse(
    val status: String,
    val message: String,
    val data: DeleteCulturalWorkTaskData
)

data class DeleteCulturalWorkTaskData(
    val cultural_work_task_id: Int
)

//Transaction

data class Transaction(
    val transaction_id: Int,
    val plot_id: Int,
    val transaction_type_name: String, // "Ingreso" o "Gasto"
    val transaction_category_name: String, // Nuevo campo
    val description: String?,
    val value: Long,
    val transaction_date: String, // Formato "yyyy-MM-dd"
    val status: String
)

data class TransactionData(
    val transactions: List<Transaction>
)

data class ListTransactionsResponse(
    val status: String,
    val message: String,
    val data: TransactionData
)

data class CreateTransactionRequest(
    val plot_id: Int,
    val transaction_type_name: String, // "Ingreso" o "Gasto"
    val transaction_category_name: String, // Nuevo campo
    val description: String,
    val value: Long,
    val transaction_date: String // Formato "yyyy-MM-dd"
)


data class CreateTransactionResponse(
    val status: String,
    val message: String,
    val data: Transaction // Cambiado de TransactionData a Transaction
)

data class EditTransactionRequest(
    val transaction_id: Int,
    val transaction_type_name: String, // "Ingreso" o "Gasto"
    val transaction_category_name: String, // Nuevo campo
    val description: String,
    val value: Long,
    val transaction_date: String // Formato "yyyy-MM-dd"
)

data class EditTransactionResponse(
    val status: String,
    val message: String,
    val data: Transaction // Similar a CreateTransactionResponse
)

data class TransactionDeleteRequest(
    val transaction_id: Int
)
data class TransactionDeleteResponse(
    val status: String,
    val message: String,
    val data: TransactionData?
)

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

// Solicitud para detección
data class DetectionRequest(
    val cultural_work_tasks_id: Int,
    val images: List<DetectionImage>
)

data class DetectionImage(
    val image_base64: String
)

// Respuesta para detección de enfermedades y deficiencias
data class DiseaseDeficiencyResponse(
    val status: String,
    val message: String,
    val data: List<DiseaseDeficiencyDetection>
)

data class DiseaseDeficiencyDetection(
    val prediction_id: Int,
    val imagen_numero: Int,
    val prediccion: String,
    val recomendacion: String,
    val modelo_utilizado: String,
    val confianza: Double
)

// Respuesta para detección de madurez
data class MaturityResponse(
    val status: String,
    val message: String,
    val data: MaturityData
)

data class MaturityData(
    val detalles_por_imagen: List<MaturityDetection>
)

data class MaturityDetection(
    val prediction_id: Int,
    val imagen_numero: Int,
    val prediccion: String,
    val recomendacion: String
)


data class PredictionIdsRequest(
    val prediction_ids: List<Int>
)

data class GenericResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)


//HEALTHCHECK

// Solicitud para listar detecciones
data class ListDetectionsRequest(
    val plot_id: Int
)

// Respuesta para listar detecciones
data class ListDetectionsResponse(
    val status: String,
    val message: String,
    val data: DetectionsData
)

data class DetectionsData(
    val detections: List<Detection>
)

data class Detection(
    val detection_id: Int,
    val collaborator_name: String,
    val date: String,
    val result: String,
    val recommendation: String
)

// API service interface for interacting with backend services

/**
 * Retrofit API service interface for interacting with backend services.
 */

interface ApiService {

    @POST("/invitation/create-invitation")
    fun createInvitation(
        @Query("session_token") sessionToken: String,
        @Body request: CreateInvitationRequest
    ): Call<CreateInvitationResponse>

    @POST("/invitation/respond-invitation/{invitation_id}")
    fun respondInvitation(
        @Path("invitation_id") invitationId: Int,
        @Query("action") action: String,
        @Query("session_token") sessionToken: String
    ): Call<ApiResponse<Any>>

    /**
     * Lists all collaborators associated with the user's account.
     *
     * @param sessionToken The session token of the user making the request.
     * @return A [Call] object for the list farm response.
     */
    @GET("/collaborators/list-collaborators")
    fun listCollaborators(
        @Query("farm_id") farmId: Int,
        @Query("session_token") sessionToken: String
    ): Call<ListCollaboratorResponse>

    @POST("/collaborators/edit-collaborator-role")
    fun editCollaboratorRole(
        @Query("farm_id") farmId: Int,
        @Query("session_token") sessionToken: String,
        @Body request: EditCollaboratorRequest
    ): Call<EditCollaboratorResponse>

    @POST("/collaborators/delete-collaborator")
    fun deleteCollaborator(
        @Query("farm_id") farmId: Int,
        @Query("session_token") sessionToken: String,
        @Body requestBody: Map<String, Int>
    ): Call<Void>


    @GET("/notification/get-notification")
    fun getNotifications(
        @Query("session_token") sessionToken: String
    ): Call<NotificationResponse>


    //Transaction
    @GET("/transaction/list-transactions/{plot_id}")
    fun listTransactions(
        @Path("plot_id") plotId: Int,
        @Query("session_token") sessionToken: String
    ): Call<ListTransactionsResponse>

    @POST("/transaction/create-transaction")
    fun createTransaction(
        @Query("session_token") sessionToken: String,
        @Body request: CreateTransactionRequest
    ): Call<CreateTransactionResponse>

    @POST("/transaction/edit-transaction")
    fun editTransaction(
        @Query("session_token") sessionToken: String,
        @Body request: EditTransactionRequest
    ): Call<EditTransactionResponse>

    @POST("/transaction/delete-transaction")
    fun deleteTransaction(
        @Query("session_token") sessionToken: String,
        @Body request: TransactionDeleteRequest
    ): Call<TransactionDeleteResponse>

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


// OpenElevationResponse.kt

data class OpenElevationResponse(
    val results: List<ElevationResult>
)

data class ElevationResult(
    val elevation: Double,
    val location: Location
)

data class Location(
    val latitude: Double,
    val longitude: Double
)


// OpenElevationService.kt

interface OpenElevationService {
    @GET("/api/v1/lookup")
    suspend fun getElevation(
        @Query("locations") locations: String
    ): OpenElevationResponse
}

