// RegisterViewModel.kt (ViewModel)

package com.example.coffetech.viewmodel.Auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.model.RegisterRequest
import com.example.coffetech.model.RegisterResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * ViewModel for managing the state and logic of the user registration flow.
 * This ViewModel handles user input validation, performing the registration request, and navigation.
 */
class RegisterViewModel : ViewModel() {

    // State variables for managing user input, error messages, and loading status
    var name = mutableStateOf("")
        private set

    var email = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    /**
     * Updates the name value when the user inputs a new name.
     *
     * @param newName The new name entered by the user.
     */
    fun onNameChange(newName: String) {
        name.value = newName
    }

    /**
     * Updates the email value when the user inputs a new email.
     *
     * @param newEmail The new email entered by the user.
     */
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    /**
     * Function to handle the next button click. This function first validates the email.
     * If the email is valid, it navigates to the next view. Otherwise, it shows an error message.
     *
     * @param navController The [NavController] used for navigation between screens.
     * @param context The [Context] used for displaying Toast messages or other UI interactions.
     */
    fun nextButton(navController: NavController, context: Context) {
        if (!validateEmail(email.value)) {
            errorMessage.value = "Correo electrónico no válido"
            return
        }

        // Si el correo es válido, limpia el mensaje de error y navega a la siguiente pantalla
        errorMessage.value = ""
        navController.navigate("${Routes.RegisterPasswordView}/${name.value}/${email.value}")
    }

    /**
     * Validates the email format using Android's email pattern matcher.
     *
     * @param email The email to validate.
     * @return `true` if the email is valid, `false` otherwise.
     */
    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
