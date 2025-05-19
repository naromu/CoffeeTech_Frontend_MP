// VerifyAccountViewModel.kt

package com.example.coffetech.viewmodel.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.VerifyRequest
import com.example.coffetech.model.VerifyResponse
import com.example.coffetech.routes.Routes
import com.example.coffetech.viewmodel.auth.others.performVerifyAccount
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
        if (token.value.isBlank()) {
            errorMessage.value = "El token es obligatorio"
            return
        }

        performVerifyAccount(
            token = token.value,
            context = context,
            navController = navController,
            onLoading = { isLoading.value = it },
            onError = { errorMessage.value = it }
        )
    }
}
