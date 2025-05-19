package com.example.coffetech.viewmodel.auth.others

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.RegisterRequest
import com.example.coffetech.model.RegisterResponse
import com.example.coffetech.routes.Routes
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun performRegisterUser(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    context: Context,
    navController: NavController,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    val registerRequest = RegisterRequest(name, email, password, confirmPassword)

    onLoading(true)

    AuthRetrofitInstance.api.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
            onLoading(false)

            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let {
                    if (it.status == "success") {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        navController.navigate(Routes.VerifyAccountView) {
                            popUpTo(Routes.RegisterView) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        onError(it.message ?: "Error desconocido del servidor")
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string()
                errorBody?.let {
                    val errorJson = JSONObject(it)
                    val errorMessage = errorJson.optString("message", "Error desconocido al registrar usuario")
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    onError(errorMessage)
                } ?: run {
                    val unknown = "Error desconocido al registrar usuario"
                    Toast.makeText(context, unknown, Toast.LENGTH_LONG).show()
                    onError(unknown)
                }
            }
        }

        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
            onLoading(false)
            val failureMessage = "Fallo en la conexión: ${t.message}"
            Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show()
            onError(failureMessage)
        }
    })
}
