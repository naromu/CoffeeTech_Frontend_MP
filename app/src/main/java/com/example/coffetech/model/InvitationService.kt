package com.example.coffetech.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


data class CreateInvitationRequest(
    val email: String,
    val suggested_role_id: Int,
    val farm_id: Int
)

data class CreateInvitationResponse(
    val status: String,
    val message: String
)


interface InvitationService {
    @POST("/invitations/create-invitation")
    fun createInvitation(
        @Query("session_token") sessionToken: String,
        @Body request: CreateInvitationRequest
    ): Call<CreateInvitationResponse>

    @POST("/invitations/respond-invitation/{invitation_id}")
    fun respondInvitation(
        @Path("invitation_id") invitationId: Int,
        @Query("action") action: String,
        @Query("session_token") sessionToken: String
    ): Call<ApiResponse<Any>>


}