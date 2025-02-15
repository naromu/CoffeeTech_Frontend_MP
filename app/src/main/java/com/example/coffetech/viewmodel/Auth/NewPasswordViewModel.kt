// NewPasswordViewModel.kt

package com.example.coffetech.viewmodel.Auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.ResetPasswordRequest
import com.example.coffetech.model.ResetPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * ViewModel for managing the state and logic of resetting the password in the forgot password flow.
 * This ViewModel handles user input validation, performing the password reset request, and navigation.
 */
class NewPasswordViewModel : ViewModel() {

    // State variables for managing password, confirm password, error messages, and loading status
    var password = mutableStateOf("")
        private set

    var confirmPassword = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    /**
     * Updates the password value when the user inputs a new password.
     * Clears any existing error message.
     *
     * @param newPassword The new password entered by the user.
     */
    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
        clearErrorMessage()
    }

    /**
     * Updates the confirm password value when the user inputs a new confirmation password.
     * Clears any existing error message.
     *
     * @param newConfirmPassword The new confirm password entered by the user.
     */
    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
        clearErrorMessage()
    }

    /**
     * Clears the current error message.
     */
    private fun clearErrorMessage() {
        errorMessage.value = ""
    }

    /**
     * Performs the password reset by sending a request to the backend API.
     * Validates the passwords and sends the request if the inputs are valid.
     * If the request is successful, navigates to the login screen. Otherwise, it displays an error.
     *
     * @param navController The [NavController] used for navigation between screens.
     * @param context The [Context] used for displaying Toast messages.
     * @param token The token used to authenticate the password reset request.
     */
    fun resetPassword(navController: NavController, context: Context, token: String) {
        Log.d("NewPasswordViewModel", "Token recibido en resetPassword: $token")

        // Validate that both password fields are not blank
        if (password.value.isBlank() || confirmPassword.value.isBlank()) {
            errorMessage.value = "Ambos campos son obligatorios"
            return
        }

        // Validate the passwords
        val (isValidPassword, passwordMessage) = validatePassword(password.value, confirmPassword.value)
        if (!isValidPassword) {
            errorMessage.value = passwordMessage
        } else {
            errorMessage.value = ""

            // Create the password reset request object
            val resetPasswordRequest = ResetPasswordRequest(
                token = token,
                new_password = password.value,
                confirm_password = confirmPassword.value
            )

            isLoading.value = true // Indicate that the loading process has started

            // Make the network request to reset the password
            RetrofitInstance.api.resetPassword(resetPasswordRequest).enqueue(object : Callback<ResetPasswordResponse> {
                override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                    isLoading.value = false

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            if (it.status == "success") {
                                Toast.makeText(context, "Contraseña restablecida exitosamente", Toast.LENGTH_SHORT).show()
                                navController.navigate(Routes.LoginView)
                            } else {
                                errorMessage.value = it.message ?: "Error desconocido del servidor"
                            }
                        }
                    } else {
                        errorMessage.value = "Error desconocido al restablecer la contraseña"
                    }
                }

                override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                    errorMessage.value = "Fallo en la conexión: ${t.message}"
                    Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    /**
     * Validates that the password and confirm password meet security requirements and match.
     *
     * @param password The password entered by the user.
     * @param confirmPassword The confirm password entered by the user.
     * @return A pair where the first value indicates if the validation was successful, and the second value contains an error message if applicable.
     */
    private fun validatePassword(password: String, confirmPassword: String): Pair<Boolean, String> {
        val specialCharacterPattern = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")
        val uppercasePattern = Regex(".*[A-Z].*")

        return when {
            password != confirmPassword -> Pair(false, "Las contraseñas no coinciden")
            password.length < 8 -> Pair(false, "La contraseña debe tener al menos 8 caracteres")
            !specialCharacterPattern.containsMatchIn(password) -> Pair(false, "La contraseña debe contener al menos un carácter especial")
            !uppercasePattern.containsMatchIn(password) -> Pair(false, "La contraseña debe contener al menos una letra mayúscula")
            else -> Pair(true, "Contraseña válida")
        }
    }

    /**
     * Validates if the password meets a stricter security standard.
     * This function checks for an uppercase letter, a number, a special character, and a minimum length of 8 characters.
     *
     * @param password The password to validate.
     * @return `true` if the password meets the security standard, `false` otherwise.
     */
    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{8,}$")
        return passwordRegex.matches(password)
    }
}
