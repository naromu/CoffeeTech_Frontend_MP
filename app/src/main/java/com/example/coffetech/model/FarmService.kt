package com.example.coffetech.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Data class representing the create farm request payload.
 *
 * @property name The name of the farm.
 * @property area The area of the farm.
 * @property area_unit_id The unit of measurement for the farm's area.
 */
data class CreateFarmRequest(
    val name: String,
    val area: Double,
    val area_unit_id: Int
)

/**
 * Data class representing the create farm response from the server.
 *
 * @property status The status of the create farm request.
 * @property message The message associated with the create farm response.
 * @property data Optional additional data.
 */
data class CreateFarmResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

/**
 * Data class representing the list farms response from the server.
 *
 * @property status The status of the list farms request.
 * @property message The message associated with the list farms response.
 * @property data The list of farms returned by the server.
 */
data class ListFarmResponse(
    val status: String,
    val message: String,
    val data: FarmDataResponse
)

/**
 * Data class representing the farm data in the list farms response.
 *
 * @property farms The list of farms returned by the server.
 */
data class FarmDataResponse(
    val farms: List<FarmResponse>
)

/**
 * Data class representing an individual farm's details.
 *
 * @property farm_id The ID of the farm.
 * @property name The name of the farm.
 * @property area The area of the farm.
 * @property unit_of_measure The unit of measurement for the farm's area.
 * @property status The status of the farm.
 * @property role The role associated with the user for this farm.
 */
data class FarmResponse(
    val farm_id: Int,
    val name: String,
    val area: Double,
    val area_unit_id: Int,
    val area_unit: String,
    val farm_state_id: Int,
    val farm_state: String,
    val user_role_id: Int,
    val role: String
)

data class GetFarmResponse(
    val status: String,
    val message: String,
    val data: FarmDataWrapper
)

data class FarmDataWrapper(
    val farm: FarmResponse
)


data class UpdateFarmRequest(
    val farm_id: Int,
    val name: String,
    val area: Double,
    val area_unit_id: Int
)


data class UpdateFarmResponse(
    val status: String,
    val message: String,
    val data: FarmResponse
)

// Definir la estructura de Permiso
data class Permission(
    val permission_id: Int,
    val name: String,
    val description: String
)

// Actualización de Role para incluir la lista de permisos
data class Role(
    val role_id: Int,
    val name: String,
    val permissions: List<Permission> // Lista de permisos asociados al rol
)


data class UnitMeasure(
    val area_unit_id: Int,
    val name: String,
    val abbreviation: String,
)

data class CoffeeVariety(
    val coffee_variety_id: Int,
    val name: String
)

//plot

data class CreatePlotRequest(
    val name: String,
    val coffee_variety_name: String,
    val latitude: String,
    val longitude: String,
    val altitude: String,
    val farm_id: Int
)


data class Plot(
    val plot_id: Int,
    val name: String,
    val coffee_variety_name: String,
    val latitude: String,
    val longitude: String,
    val altitude: String,
    val area: Double,
    val area_unit: String
)

data class ListPlotsResponse(
    val status: String,
    val message: String,
    val data: PlotsData
)

data class PlotsData(
    val plots: List<Plot>
)


data class GetPlotResponse(
    val status: String,
    val message: String,
    val data: PlotDataWrapper
)

data class PlotDataWrapper(
    val plot: Plot
)

/**
 * Solicitud para actualizar la información general de un lote.
 *
 * @property plot_id El ID del lote a actualizar.
 * @property name El nuevo nombre del lote.
 * @property coffee_variety_name La nueva variedad de café del lote.
 */
data class UpdatePlotGeneralInfoRequest(
    val plot_id: Int,
    val name: String,
    val coffee_variety_name: String
)

/**
 * Respuesta de la API para la actualización de la información general de un lote.
 *
 * @property status El estado de la operación ("success" o "error").
 * @property message El mensaje asociado a la respuesta.
 * @property data Los datos actualizados del lote.
 */
data class UpdatePlotGeneralInfoResponse(
    val status: String,
    val message: String,
    val data: Plot? = null
)
// UpdatePlotLocationRequest.kt

data class UpdatePlotLocationRequest(
    val plot_id: Int,
    val latitude: String,
    val longitude: String,
    val altitude: String
)


// UpdatePlotLocationResponse.kt
data class UpdatePlotLocationResponse(
    val status: String,
    val message: String,
    val data: PlotLocationData
)

data class PlotLocationData(
    val plot_id: Int,
    val latitude: String,
    val longitude: String,
    val altitude: String
)


interface FarmService {

    @POST("/farm/create-farm")
    fun createFarm(
        @Query("session_token") sessionToken: String,
        @Body request: CreateFarmRequest
    ): Call<CreateFarmResponse>

    /**
     * Lists all farms associated with the user's account.
     *
     * @param sessionToken The session token of the user making the request.
     * @return A [Call] object for the list farm response.
     */
    @POST("/farm/list-farm")
    fun listFarms(
        @Query("session_token") sessionToken: String
    ): Call<ListFarmResponse>


    // Método corregido para obtener los detalles de la finca
    @GET("/farm/get-farm/{farm_id}")
    fun getFarm(
        @Path("farm_id") farmId: Int,
        @Query("session_token") sessionToken: String
    ): Call<GetFarmResponse>


    @POST("/farm/update-farm")
    fun updateFarm(
        @Query("session_token") sessionToken: String,
        @Body request: UpdateFarmRequest
    ): Call<UpdateFarmResponse>


    //plots

    @POST("/plots/create-plot")
    fun createPlot(
        @Query("session_token") sessionToken: String,
        @Body request: CreatePlotRequest
    ): Call<CreateFarmResponse>

    @GET("/plots/list-plots/{farm_id}")
    fun listPlots(
        @Path("farm_id") farmId: Int,
        @Query("session_token") sessionToken: String
    ): Call<ListPlotsResponse>

    @GET("/plots/get-plot/{plot_id}")
    fun getPlot(
        @Path("plot_id") plotId: Int,
        @Query("session_token") sessionToken: String
    ): Call<GetPlotResponse>

    @POST("/plots/update-plot-general-info")
    fun updatePlotGeneralInfo(
        @Query("session_token") sessionToken: String,
        @Body request: UpdatePlotGeneralInfoRequest
    ): Call<UpdatePlotGeneralInfoResponse>

    @POST("/plots/update-plot-location")
    fun updatePlotLocation(
        @Query("session_token") sessionToken: String,
        @Body request: UpdatePlotLocationRequest
    ): Call<UpdatePlotLocationResponse>

    //utiliadades

    /**
     * Retrieves a list of unit measures.
     *
     * @return A [Call] object for the unit measures response.
     */
    @GET("/utils/area-units")
    fun getUnitMeasures(): Call<ApiResponse<List<UnitMeasure>>>

    /**
     * Creates a new farm.
     *
     * @param sessionToken The session token of the user making the request.
     * @param request The request payload containing farm details.
     * @return A [Call] object for the create farm response.
     */

    @GET("/utils/list-coffee-varieties")
    fun getCoffeeVarieties(): Call<ApiResponse<List<CoffeeVariety>>>

}