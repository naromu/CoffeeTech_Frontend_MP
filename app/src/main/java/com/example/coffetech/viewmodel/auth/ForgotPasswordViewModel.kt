// ForgotPasswordViewModel.kt (ViewModel)

package com.example.coffetech.viewmodel.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.ForgotPasswordRequest
import com.example.coffetech.model.ForgotPasswordResponse
import com.example.coffetech.routes.Routes
import com.example.coffetech.viewmodel.auth.others.performForgotPasswordRequest
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel for managing the state and logic of the forgot password flow.
 * This ViewModel handles user input validation, sending a forgot password request, and navigation.
 */
class ForgotPasswordViewModel : ViewModel() {

    // State variables
    var email = mutableStateOf("")
        private set

    var isEmailValid = mutableStateOf(true)
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false) // State to handle loading status
        private set

    /**
     * Updates the value of the email and validates its format.
     * If the email is invalid, an error message is displayed.
     *
     * @param newEmail The new email entered by the user.
     */
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
        isEmailValid.value = isValidEmail(newEmail)
        if (isEmailValid.value) {
            errorMessage.value = ""
        } else {
            errorMessage.value = "Correo electrónico no válido"
        }
    }

    /**
     * Sends a forgot password request to the backend API if the email is valid.
     * If the request is successful, navigates to the confirm token view.
     * Displays error messages in case of network issues or invalid responses.
     *
     * @param navController The [NavController] used for navigation between screens.
     * @param context The [Context] used for displaying Toast messages.
     */
    fun sendForgotPasswordRequest(navController: NavController, context: Context) {
        if (!isEmailValid.value) {
            errorMessage.value = "Correo electrónico no válido"
            return
        }

        isLoading.value = true // Start loading

        performForgotPasswordRequest(
            email = email.value,
            context = context,
            navController = navController,
            onLoading = { isLoading.value = it },
            onError = { errorMessage.value = it }
        )

    }

    /**
     * Validates the format of the given email address.
     *
     * @param email The email address to validate.
     * @return `true` if the email format is valid, `false` otherwise.
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return emailRegex.matches(email)
    }
}
