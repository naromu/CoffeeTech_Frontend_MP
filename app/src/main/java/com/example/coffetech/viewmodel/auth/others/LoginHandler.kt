package com.example.coffetech.viewmodel.auth.others

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.LoginRequest
import com.example.coffetech.model.LoginResponse
import com.example.coffetech.routes.Routes
import com.example.coffetech.utils.SharedPreferencesHelper
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun performLogin(
    email: String,
    password: String,
    context: Context,
    navController: NavController,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            onLoading(false)
            Log.w("LoginHandler", "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }

        val fcmToken = task.result
        val loginRequest = LoginRequest(email, password, fcmToken)

        onLoading(true)

        AuthRetrofitInstance.api.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                onLoading(false)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            val token = it.data?.session_token
                            val name = it.data?.name ?: "Usuario"
                            val sharedPreferencesHelper = SharedPreferencesHelper(context)

                            token?.let {
                                sharedPreferencesHelper.saveSessionData(token, name, email)
                                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()

                                navController.navigate(Routes.StartView) {
                                    popUpTo(Routes.LoginView) { inclusive = true }
                                }
                            } ?: run {
                                onError("No se recibió el token de sesión")
                            }
                        } else {
                            if (it.message == "Debes verificar tu correo antes de iniciar sesión") {
                                navController.navigate(Routes.VerifyAccountView) {
                                    popUpTo(Routes.LoginView) { inclusive = true }
                                }
                            }
                            onError(it.message ?: "Error desconocido")
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val json = JSONObject(it)
                            onError(json.optString("message", "Error desconocido al iniciar sesión"))
                        } catch (e: Exception) {
                            onError("Error al procesar la respuesta del servidor")
                        }
                    } ?: onError("Error desconocido al iniciar sesión")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onLoading(false)
                onError("Fallo en la conexión: ${t.message}")
            }
        })
    }
}
