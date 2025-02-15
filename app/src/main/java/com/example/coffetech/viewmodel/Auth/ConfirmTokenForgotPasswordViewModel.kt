// ConfirmTokenForgotPasswordViewModel.kt (ViewModel)

package com.example.coffetech.viewmodel.Auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.VerifyRequest
import com.example.coffetech.model.VerifyResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// ConfirmTokenForgotPasswordViewModel.kt (ViewModel)

/**
 * ViewModel for managing the state and logic for confirming the token in the forgot password flow.
 * This ViewModel handles the process of verifying the token and navigating to the new password screen.
 */
class ConfirmTokenForgotPasswordViewModel : ViewModel() {

    // State variables
    var token = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    /**
     * Updates the value of the token and clears any error message if the token is not blank.
     *
     * @param newToken The new token entered by the user.
     */
    fun onTokenChange(newToken: String) {
        token.value = newToken
        if (newToken.isNotBlank()) {
            errorMessage.value = ""
        }
    }

    /**
     * Confirms the token provided by the user and verifies it with the backend.
     * If successful, navigates to the NewPasswordView with the provided token.
     *
     * @param navController The [NavController] used for navigation between screens.
     * @param context The [Context] used for displaying Toast messages.
     */
    fun confirmToken(navController: NavController, context: Context) {
        if (token.value.isBlank()) {
            errorMessage.value = "El token es obligatorio"
            return
        }

        val verifyRequest = VerifyRequest(token = token.value)

        isLoading.value = true // Indicate that the loading process has started

        // Make a network call to verify the token
        RetrofitInstance.api.confirmForgotPassword(verifyRequest).enqueue(object : Callback<VerifyResponse> {
            override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
                isLoading.value = false // End the loading process

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()

                            Log.d("ConfirmTokenViewModel", "Navigating to NewPasswordView with token: ${token.value}")

                            // Navigate to the NewPasswordView with the token
                            navController.navigate("${Routes.NewPasswordView}/${token.value}")
                        } else {
                            errorMessage.value = it.message ?: "Error desconocido del servidor"
                        }
                    }
                } else {
                    // Handle error response from the server
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMessage = if (errorJson.has("message")) {
                                errorJson.getString("message")
                            } else {
                                "Error desconocido al confirmar el token"
                            }
                            this@ConfirmTokenForgotPasswordViewModel.errorMessage.value = errorMessage
                        } catch (e: Exception) {
                            errorMessage.value = "Error desconocido al confirmar el token"
                        }
                    } ?: run {
                        errorMessage.value = "Error desconocido al confirmar el token"
                    }
                }
            }

            override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                // Handle failure due to connection issues
                errorMessage.value = "Fallo en la conexión: ${t.message}"
                Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
            }
        })
    }
}
