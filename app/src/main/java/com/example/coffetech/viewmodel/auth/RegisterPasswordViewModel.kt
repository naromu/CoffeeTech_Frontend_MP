package com.example.coffetech.viewmodel.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.RegisterRequest
import com.example.coffetech.model.RegisterResponse
import com.example.coffetech.routes.Routes
import com.example.coffetech.viewmodel.auth.others.performRegisterUser
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
        if (name.isBlank() || email.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank()) {
            errorMessage.value = "Todos los campos son obligatorios"
            return
        }

        val passwordErrors = validatePassword(password.value, confirmPassword.value)
        if (passwordErrors.isNotEmpty()) {
            errorMessage.value = passwordErrors.joinToString("\n")
            return
        }

        errorMessage.value = ""

        performRegisterUser(
            name = name,
            email = email,
            password = password.value,
            confirmPassword = confirmPassword.value,
            context = context,
            navController = navController,
            onLoading = { isLoading.value = it },
            onError = { errorMessage.value = it }
        )
    }

}
