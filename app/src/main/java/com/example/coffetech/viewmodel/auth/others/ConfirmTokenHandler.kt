package com.example.coffetech.viewmodel.auth.others


import android.content.Context
import android.util.Log
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

fun performConfirmToken(
    token: String,
    context: Context,
    navController: NavController,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    val request = VerifyRequest(token = token)

    onLoading(true)

    AuthRetrofitInstance.api.confirmForgotPassword(request).enqueue(object : Callback<VerifyResponse> {
        override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
            onLoading(false)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == "success") {
                    Toast.makeText(context, body.message, Toast.LENGTH_LONG).show()
                    Log.d("ConfirmTokenHandler", "Navigating to NewPasswordView with token: $token")
                    navController.navigate("${Routes.NewPasswordView}/$token")
                } else {
                    onError(body?.message ?: "Error desconocido del servidor")
                }
            } else {
                val errorMsg = try {
                    val json = JSONObject(response.errorBody()?.string() ?: "")
                    json.optString("message", "Error desconocido al confirmar el token")
                } catch (e: Exception) {
                    "Error desconocido al confirmar el token"
                }
                onError(errorMsg)
            }
        }

        override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
            onLoading(false)
            val error = "Fallo en la conexión: ${t.message}"
            onError(error)
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    })
}
