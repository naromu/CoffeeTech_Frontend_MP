// VerifyAccountViewModel.kt

package com.example.coffetech.viewmodel.Auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.VerifyRequest
import com.example.coffetech.model.VerifyResponse
import com.example.coffetech.utils.SharedPreferencesHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel for managing the state and logic of the account verification flow.
 * This ViewModel handles user input for the verification token, sending the verification request, and navigation.
 */
class VerifyAccountViewModel : ViewModel() {

    // State variables for managing the token, error messages, and loading status
    var token = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    /**
     * Updates the token value when the user inputs a new token.
     * Clears any error message if the token is not blank.
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
     * Sends the verification request to the backend API with the provided token.
     * If successful, navigates to the login screen. If the token is invalid, it shows an error message.
     *
     * @param navController The [NavController] used for navigation between screens.
     * @param context The [Context] used for displaying Toast messages.
     */
    fun verifyUser(navController: NavController, context: Context) {
        // Ensure the token is not blank
        if (token.value.isBlank()) {
            errorMessage.value = "El token es obligatorio"
            return
        }

        val verifyRequest = VerifyRequest(token = token.value)
        isLoading.value = true // Set loading state to true

        // Make the network request to verify the token
        RetrofitInstance.api.verifyUser(verifyRequest).enqueue(object : Callback<VerifyResponse> {
            override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
                isLoading.value = false // Disable loading state

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()

                            // Navigate to the login screen after successful verification
                            navController.navigate(Routes.LoginView) {
                                popUpTo(Routes.VerifyAccountView) { inclusive = true } // Remove VerifyAccountView from the back stack
                            }
                        } else {
                            errorMessage.value = it.message ?: "Error desconocido del servidor"
                        }
                    }
                } else {
                    // Handle error response from the server
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        val errorJson = JSONObject(it)
                        val errorMessage = errorJson.getString("message")
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } ?: run {
                        errorMessage.value = "Error desconocido al registrar usuario"
                        Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                // Handle network failure
                errorMessage.value = "Fallo en la conexión: ${t.message}"
                Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
            }
        })
    }
}
