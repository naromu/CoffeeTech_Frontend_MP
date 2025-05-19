// ConfirmTokenForgotPasswordViewModel.kt (ViewModel)

package com.example.coffetech.viewmodel.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.VerifyRequest
import com.example.coffetech.model.VerifyResponse
import com.example.coffetech.routes.Routes
import com.example.coffetech.viewmodel.auth.others.performConfirmToken
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

        performConfirmToken(
            token = token.value,
            context = context,
            navController = navController,
            onLoading = { isLoading.value = it },
            onError = { errorMessage.value = it }
        )
    }
}
