package com.example.coffetech.viewmodel.auth.others

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.VerifyRequest
import com.example.coffetech.model.VerifyResponse
import com.example.coffetech.routes.Routes
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun performVerifyAccount(
    token: String,
    context: Context,
    navController: NavController,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)

    val verifyRequest = VerifyRequest(token = token)

    AuthRetrofitInstance.api.verifyUser(verifyRequest).enqueue(object : Callback<VerifyResponse> {
        override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
            onLoading(false)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody?.status == "success") {
                    Toast.makeText(context, responseBody.message, Toast.LENGTH_LONG).show()
                    navController.navigate(Routes.LoginView) {
                        popUpTo(Routes.VerifyAccountView) { inclusive = true }
                    }
                } else {
                    onError(responseBody?.message ?: "Error desconocido del servidor")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                errorBody?.let {
                    try {
                        val errorJson = JSONObject(it)
                        val message = errorJson.optString("message", "Error desconocido al verificar cuenta")
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al procesar la respuesta del servidor", Toast.LENGTH_LONG).show()
                    }
                } ?: run {
                    val unknownError = "Error desconocido al registrar usuario"
                    Toast.makeText(context, unknownError, Toast.LENGTH_LONG).show()
                    onError(unknownError)
                }
            }
        }

        override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
            onLoading(false)
            val message = "Fallo en la conexión: ${t.message}"
            onError(message)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    })
}
