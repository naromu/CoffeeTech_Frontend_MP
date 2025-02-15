package com.example.coffetech.viewmodel.Auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.ChangePasswordRequest
import com.example.coffetech.model.ChangePasswordResponse
import com.example.coffetech.model.ResetPasswordRequest
import com.example.coffetech.model.ResetPasswordResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel responsible for managing the state and logic of the change password view.
 */
class ChangePasswordViewModel : ViewModel() {
    var currentPassword = mutableStateOf("")
        private set

    var newPassword = mutableStateOf("")
        private set

    var confirmPassword = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")

    var isLoading = mutableStateOf(false)

    var isPasswordChanged = mutableStateOf(false) // Mantiene el estado del cambio de contraseña

    /**
     * Actualiza la contraseña actual y realiza la validación en tiempo real.
     */
    fun onCurrentPasswordChange(newValue: String) {
        currentPassword.value = newValue
        validatePasswordRequirements()
    }

    /**
     * Actualiza la nueva contraseña y realiza la validación en tiempo real.
     */
    fun onNewPasswordChange(newValue: String) {
        newPassword.value = newValue
        validatePasswordRequirements()
    }

    /**
     * Actualiza la confirmación de la nueva contraseña y realiza la validación en tiempo real.
     */
    fun onConfirmPasswordChange(newValue: String) {
        confirmPassword.value = newValue
        validatePasswordRequirements()
    }

    /**
     * Valida que las contraseñas cumplan con los requisitos de seguridad.
     *
     * @return `true` si la contraseña es válida, `false` de lo contrario.
     */
    fun validatePasswordRequirements(): Boolean {
        val (isValid, message) = validatePassword(
            currentPassword.value,
            newPassword.value,
            confirmPassword.value
        )

        return if (isValid) {
            errorMessage.value = ""
            true
        } else {
            errorMessage.value = message
            false
        }
    }

    /**
     * Valida si las contraseñas ingresadas cumplen con las siguientes condiciones de seguridad:
     * - La nueva contraseña es diferente a la contraseña actual.
     * - La nueva contraseña coincide con la confirmación.
     * - La contraseña tiene al menos 8 caracteres.
     * - La contraseña contiene al menos un carácter especial.
     * - La contraseña contiene al menos una letra mayúscula.
     *
     * @param currentPassword La contraseña actual del usuario.
     * @param newPassword La nueva contraseña ingresada por el usuario.
     * @param confirmPassword La confirmación de la nueva contraseña.
     * @return Un [Pair] donde el primer valor es `true` si la contraseña es válida, y el segundo es un mensaje de error si es inválida.
     */
    private fun validatePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Pair<Boolean, String> {
        val specialCharacterPattern = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")
        val uppercasePattern = Regex(".*[A-Z].*")

        return when {
            newPassword == currentPassword -> Pair(
                false,
                "La nueva contraseña debe ser diferente a la contraseña actual"
            )
            newPassword != confirmPassword -> Pair(false, "Las contraseñas no coinciden")
            newPassword.length < 8 -> Pair(false, "La contraseña debe tener al menos 8 caracteres")
            !specialCharacterPattern.containsMatchIn(newPassword) -> Pair(
                false,
                "La contraseña debe contener al menos un carácter especial"
            )
            !uppercasePattern.containsMatchIn(newPassword) -> Pair(
                false,
                "La contraseña debe contener al menos una letra mayúscula"
            )
            else -> Pair(true, "Contraseña válida")
        }
    }

    /**
     * Inicia el proceso de cambio de contraseña realizando una llamada a la API.
     *
     * @param context El contexto actual, necesario para mostrar toasts.
     */
    fun changePassword(context: Context) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()

        if (sessionToken == null) {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(
                context,
                "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Crea la solicitud con la contraseña actual y la nueva contraseña
        val changePasswordRequest = ChangePasswordRequest(
            current_password = currentPassword.value,
            new_password = newPassword.value
        )
        isLoading.value = true
        // Realiza la llamada al backend usando el método PUT
        RetrofitInstance.api.changePassword(changePasswordRequest, sessionToken)
            .enqueue(object : Callback<ChangePasswordResponse> {
                override fun onResponse(
                    call: Call<ChangePasswordResponse>,
                    response: Response<ChangePasswordResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.status == "success") {
                            // Si la contraseña se cambió con éxito
                            isPasswordChanged.value = true
                            Toast.makeText(
                                context,
                                "Contraseña cambiada exitosamente.",
                                Toast.LENGTH_LONG
                            ).show()

                        } else if (responseBody?.status == "error") {
                            // Si hubo un error en el cambio de contraseña (credenciales incorrectas, etc.)
                            val errorMsg = responseBody.message ?: "Error desconocido."
                            errorMessage.value = errorMsg
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        } else {
                            // Manejar cualquier otra respuesta inesperada
                            errorMessage.value = "Respuesta inesperada del servidor."
                            Toast.makeText(
                                context,
                                "Respuesta inesperada del servidor.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        // Manejar el caso en el que la respuesta no fue exitosa (error del servidor)
                        errorMessage.value = "Error al cambiar la contraseña."
                        Toast.makeText(context, "Error al cambiar la contraseña.", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    // Manejar errores de conexión o fallos de la solicitud
                    val connectionErrorMsg = "Error de conexión"
                    errorMessage.value = connectionErrorMsg
                    Toast.makeText(context, connectionErrorMsg, Toast.LENGTH_LONG).show()
                }
            })
    }
}