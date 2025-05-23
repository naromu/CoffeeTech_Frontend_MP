package com.example.coffetech.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class NotificationResponse(
    val status: String,
    val message: String,
    val data: List<Notification>
)


data class Notification(
    val notification_id: Int,
    val message: String,
    val notification_date: String,
    val invitation_id: Int,
    val notification_type: String,
    val notification_state: String
)


interface NotificationService {
    @GET("/notification/get-notification")
    fun getNotifications(
        @Query("session_token") sessionToken: String
    ): Call<NotificationResponse>
}