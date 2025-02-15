package com.example.coffetech.viewmodel.Auth

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.model.LoginRequest
import com.example.coffetech.model.LoginResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * ViewModel for managing the state and logic of the login flow.
 * This ViewModel handles user input validation, performing the login request, and navigation.
 */
class LoginViewModel() : ViewModel(), Parcelable {

    // State variables for managing email, password, error messages, and loading status
    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    /**
     * Parcelable constructor for restoring the ViewModel state if needed.
     */
    constructor(parcel: Parcel) : this()

    /**
     * Updates the email value when the user inputs a new email.
     *
     * @param newEmail The new email entered by the user.
     */
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    /**
     * Updates the password value when the user inputs a new password.
     *
     * @param newPassword The new password entered by the user.
     */
    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginViewModel> {
        override fun createFromParcel(parcel: Parcel): LoginViewModel {
            return LoginViewModel(parcel)
        }

        override fun newArray(size: Int): Array<LoginViewModel?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * Performs the login process by sending a login request to the server.
     * It first validates the email format and ensures both email and password are not blank.
     * If the login is successful, the session token, name, and email are stored in SharedPreferences,
     * and the user is navigated to the StartView. In case of an error, it handles error messages and logs them.
     *
     * @param navController The [NavController] used for navigating between screens.
     * @param context The [Context] used to show Toast messages and access SharedPreferences.
     */
    fun loginUser(navController: NavController, context: Context) {
        // Validar si los campos de email o contraseña están vacíos
        if (email.value.isBlank() || password.value.isBlank()) {
            errorMessage.value = "El correo y la contraseña son obligatorios"
            Log.e("LoginViewModel", "Los campos de email o contraseña están vacíos")
            return
        }

        // Validar el formato del correo electrónico
        val isValidEmail = validateEmail(email.value)
        if (!isValidEmail) {
            errorMessage.value = "Correo electrónico no válido"
            Log.e("LoginViewModel", "El formato del correo electrónico es inválido")
            return
        }

        // Limpiar cualquier mensaje de error anterior
        errorMessage.value = ""

        // Establecer el estado de carga en verdadero
        isLoading.value = true

        // Obtener el token de FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("LoginViewModel", "Fetching FCM registration token failed", task.exception)
                isLoading.value = false
                return@addOnCompleteListener
            }

            // Obtener el token de registro de FCM
            val fcmToken = task.result
            Log.d("LoginViewModel", "FCM Token: $fcmToken")

            // Crear la solicitud de inicio de sesión con el FCM token incluido
            val loginRequest = LoginRequest(email = email.value, password = password.value, fcm_token = fcmToken)

            Log.d("LoginViewModel", "Iniciando solicitud de inicio de sesión con email: ${email.value} y FCM Token: $fcmToken")

            // Enviar la solicitud de inicio de sesión al servidor
            RetrofitInstance.api.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    isLoading.value = false
                    Log.d("LoginViewModel", "Respuesta del servidor recibida")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            if (it.status == "success") {
                                // Procesar la respuesta exitosa
                                val token = it.data?.session_token
                                val name = it.data?.name ?: "Usuario"
                                val email = email.value

                                token?.let {
                                    val sharedPreferencesHelper = SharedPreferencesHelper(context)
                                    sharedPreferencesHelper.saveSessionData(token, name, email)

                                    Log.d("LoginViewModel", "Datos guardados correctamente: token=$token, name=$name")

                                    // Navegar a la vista de inicio
                                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
                                    navController.navigate(Routes.StartView) {
                                        popUpTo(Routes.LoginView) { inclusive = true }
                                    }
                                } ?: run {
                                    Log.e("LoginViewModel", "El token no fue recibido en la respuesta")
                                    Toast.makeText(context, "No se recibió el token de sesión", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                if (it.message == "Debes verificar tu correo antes de iniciar sesión") {
                                    // Navegar a la pantalla de verificación
                                    navController.navigate("${Routes.VerifyAccountView}") {
                                        popUpTo(Routes.LoginView) { inclusive = true }
                                    }
                                }
                                Log.e("LoginViewModel", "Inicio de sesión fallido: ${it.message}")
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        // Manejar la respuesta de error del servidor
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            try {
                                val errorJson = JSONObject(it)
                                val errorMessage = if (errorJson.has("message")) {
                                    errorJson.getString("message")
                                } else {
                                    "Error desconocido al iniciar sesión"
                                }
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Log.e("LoginViewModel", "Error al procesar la respuesta del servidor: ${e.message}")
                                Toast.makeText(context, "Error al procesar la respuesta del servidor", Toast.LENGTH_LONG).show()
                            }
                        } ?: run {
                            Log.e("LoginViewModel", "Respuesta vacía del servidor")
                            val unknownErrorMessage = "Error desconocido al iniciar sesión"
                            Toast.makeText(context, unknownErrorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    isLoading.value = false
                    Log.e("LoginViewModel", "Fallo en la conexión: ${t.message}")
                    Toast.makeText(context, "Fallo en la conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }


    /**
     * Validates the email format using Android's email address pattern matcher.
     *
     * @param email The email address to validate.
     * @return `true` if the email format is valid, `false` otherwise.
     */
    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

