package com.example.coffetech.viewmodel.auth.others

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.ForgotPasswordRequest
import com.example.coffetech.model.ForgotPasswordResponse
import com.example.coffetech.routes.Routes
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun performForgotPasswordRequest(
    email: String,
    context: Context,
    navController: NavController,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)

    val forgotPasswordRequest = ForgotPasswordRequest(email)

    AuthRetrofitInstance.api.forgotPassword(forgotPasswordRequest)
        .enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(
                call: Call<ForgotPasswordResponse>,
                response: Response<ForgotPasswordResponse>
            ) {
                onLoading(false)

                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status == "success") {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            navController.navigate(Routes.ConfirmTokenForgotPasswordView)
                        } else {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        errorBody?.let {
                            val json = JSONObject(it)
                            json.optString("message", "Error desconocido al enviar email")
                        } ?: "Error desconocido al enviar email"
                    } catch (e: Exception) {
                        "Error al procesar la respuesta del servidor"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                onLoading(false)
                val error = "Error de red: ${t.localizedMessage}"
                onError(error)
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        })
}
