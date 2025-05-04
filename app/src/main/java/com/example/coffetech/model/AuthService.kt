package com.example.coffetech.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query



/**
 * Data class representing the registration request payload.
 *
 * @property name The user's name.
 * @property email The user's email.
 * @property password The user's password.
 * @property passwordConfirmation Confirmation of the user's password.
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val passwordConfirmation: String
)

/**
 * Data class representing the registration response from the server.
 *
 * @property status The status of the registration request ("success" or "error").
 * @property message The message associated with the registration response.
 * @property data Optional additional data.
 */
data class RegisterResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

/**
 * Data class representing the login request payload.
 *
 * @property email The user's email.
 * @property password The user's password.
 * @property fcm_token The user's token cloud message firebase
 */
data class LoginRequest(
    val email: String,
    val password: String,
    val fcm_token: String? = null
)


/**
 * Data class representing the login response from the server.
 *
 * @property status The status of the login request.
 * @property message The message associated with the login response.
 * @property data Token data if login is successful.
 */
data class LoginResponse(
    val status: String,
    val message: String,
    val data: TokenData? = null
)

/**
 * Data class representing the token and user data.
 *
 * @property session_token The session token for the authenticated user.
 * @property name The name of the authenticated user.
 */
data class TokenData(
    val session_token: String,
    val name: String
)

/**
 * Data class representing the verification request payload.
 *
 * @property token The token for verifying the user's account.
 */
data class VerifyRequest(
    val token: String
)

/**
 * Data class representing the verification response from the server.
 *
 * @property status The status of the verification request.
 * @property message The message associated with the verification response.
 * @property data Optional additional data.
 */
data class VerifyResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

/**
 * Data class representing the forgot password request payload.
 *
 * @property email The user's email for password recovery.
 */
data class ForgotPasswordRequest(
    val email: String
)

/**
 * Data class representing the forgot password response from the server.
 *
 * @property status The status of the forgot password request.
 * @property message The message associated with the forgot password response.
 * @property data Optional additional data.
 */
data class ForgotPasswordResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

/**
 * Data class representing the reset password request payload.
 *
 * @property token The token for resetting the user's password.
 * @property new_password The new password for the user.
 * @property confirm_password Confirmation of the new password.
 */
data class ResetPasswordRequest(
    val token: String,
    val new_password: String,
    val confirm_password: String
)

/**
 * Data class representing the reset password response from the server.
 *
 * @property status The status of the reset password request.
 * @property message The message associated with the reset password response.
 * @property data Optional additional data.
 */
data class ResetPasswordResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

/**
 * Data class representing the logout request payload.
 *
 * @property session_token The session token to be invalidated.
 */
data class LogoutRequest(
    val session_token: String
)

/**
 * Data class representing the logout response from the server.
 *
 * @property status The status of the logout request.
 * @property message The message associated with the logout response.
 * @property data Optional additional data.
 */
data class LogoutResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

/**
 * Data class representing the update profile request payload.
 *
 * @property new_name The new name for the user's profile.
 */
data class UpdateProfileRequest(
    val new_name: String
)

/**
 * Data class representing the update profile response from the server.
 *
 * @property status The status of the profile update request.
 * @property message The message associated with the profile update response.
 */
data class UpdateProfileResponse(
    val status: String,
    val message: String
)

/**
 * Data class representing the change password request payload.
 *
 * @property current_password The current password of the user.
 * @property new_password The new password for the user.
 */
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String
)

/**
 * Data class representing the change password response from the server.
 *
 * @property status The status of the change password request.
 * @property message The message associated with the change password response.
 */
data class ChangePasswordResponse(
    val status: String,
    val message: String
)


interface AuthService {
    /**
     * Registers a new user.
     *
     * @param request The request payload containing user registration data.
     * @return A [Call] object for the registration response.
     */
    @POST("/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    /**
     * Logs in a user.
     *
     * @param request The request payload containing user login data.
     * @return A [Call] object for the login response.
     */
    @POST("/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    /**
     * Verifies a user's account using a token.
     *
     * @param request The request payload containing the verification token.
     * @return A [Call] object for the verification response.
     */
    @POST("/auth/verify-email")
    fun verifyUser(@Body request: VerifyRequest): Call<VerifyResponse>

    /**
     * Initiates the forgot password process for a user.
     *
     * @param request The request payload containing the user's email.
     * @return A [Call] object for the forgot password response.
     */
    @POST("/auth/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    /**
     * Verifies the forgot password token.
     *
     * @param request The request payload containing the verification token.
     * @return A [Call] object for the verification response.
     */
    @POST("/auth/verify-reset-token")
    fun confirmForgotPassword(@Body request: VerifyRequest): Call<VerifyResponse>

    /**
     * Resets the user's password.
     *
     * @param request The request payload containing the token and new password.
     * @return A [Call] object for the reset password response.
     */
    @POST("/auth/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<ResetPasswordResponse>

    /**
     * Logs out a user.
     *
     * @param request The request payload containing the session token.
     * @return A [Call] object for the logout response.
     */
    @POST("/auth/logout")
    fun logoutUser(@Body request: LogoutRequest): Call<LogoutResponse>

    /**
     * Updates a user's profile.
     *
     * @param request The request payload containing updated profile information.
     * @param sessionToken The session token of the user making the request.
     * @return A [Call] object for the profile update response.
     */
    @POST("/auth/update-profile")
    fun updateProfile(
        @Body request: UpdateProfileRequest,
        @Query("session_token") sessionToken: String
    ): Call<UpdateProfileResponse>

    /**
     * Changes the user's password.
     *
     * @param request The request payload containing the current and new passwords.
     * @param sessionToken The session token of the user making the request.
     * @return A [Call] object for the change password response.
     */
    @PUT("/auth/change-password")
    fun changePassword(
        @Body request: ChangePasswordRequest,
        @Query("session_token") sessionToken: String
    ): Call<ChangePasswordResponse>

    /**
     * Retrieves a list of roles available to users.
     *
     * @return A [Call] object for the roles response.
     */
    @GET("/roles/list-roles")
    fun getRoles(): Call<ApiResponse<List<Role>>>


}
