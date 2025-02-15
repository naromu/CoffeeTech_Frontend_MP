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
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPasswordViewModel : ViewModel() {

    var password = mutableStateOf("")
        private set

    var confirmPassword = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    // Actualiza el valor del campo de contraseña
    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    // Actualiza el valor del campo de confirmación de contraseña
    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    // Valida la contraseña ingresada y si coincide con la confirmación
// Modificación de validatePassword para devolver una lista de errores
    private fun validatePassword(password: String, confirmPassword: String): List<String> {
        val errors = mutableListOf<String>()
        val specialCharacterPattern = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")
        val uppercasePattern = Regex(".*[A-Z].*")

        if (password != confirmPassword) {
            errors.add("Las contraseñas no coinciden")
        }

        if (password.length < 8) {
            errors.add("La contraseña debe tener al menos 8 caracteres")
        }

        if (!specialCharacterPattern.containsMatchIn(password)) {
            errors.add("La contraseña debe contener al menos un carácter especial")
        }

        if (!uppercasePattern.containsMatchIn(password)) {
            errors.add("La contraseña debe contener al menos una letra mayúscula")
        }

        return errors
    }


    // Método para registrar al usuario
    fun registerUser(navController: NavController, context: Context, name: String, email: String) {
        // Validación de campos vacíos
        if (name.isBlank() || email.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank()) {
            errorMessage.value = "Todos los campos son obligatorios"
            return
        }

        // Validación de la contraseña
        val passwordErrors = validatePassword(password.value, confirmPassword.value)
        if (passwordErrors.isNotEmpty()) {
            // Unimos todos los errores en un solo mensaje
            errorMessage.value = passwordErrors.joinToString(separator = "\n")
            return
        }
        errorMessage.value = "" // Borrar mensaje de error previo
        isLoading.value = true // Set loading state to true

        // Usa directamente name y email como cadenas de texto
        val registerRequest = RegisterRequest(name, email, password.value, confirmPassword.value)

        // Make the registration request to the backend
        RetrofitInstance.api.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                isLoading.value = false // Stop loading

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()

                            // Navigate to the account verification screen
                            navController.navigate(Routes.VerifyAccountView) {
                                popUpTo(Routes.RegisterView) { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        val errorJson = JSONObject(it)
                        val errorMessage = errorJson.getString("message")
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } ?: run {
                        val unknownErrorMessage = "Error desconocido al registrar usuario"
                        Toast.makeText(context, unknownErrorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                isLoading.value = false // Stop loading
                val failureMessage = "Fallo en la conexión: ${t.message}"
                Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

}
