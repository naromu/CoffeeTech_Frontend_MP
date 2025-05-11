package com.example.coffetech.model

import retrofit2.http.GET
import retrofit2.http.Query


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