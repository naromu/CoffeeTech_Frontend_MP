package com.example.coffetech.viewmodel.auth.others

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.ResetPasswordRequest
import com.example.coffetech.model.ResetPasswordResponse
import com.example.coffetech.routes.Routes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun performResetPassword(
    token: String,
    newPassword: String,
    confirmPassword: String,
    context: Context,
    navController: NavController,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)

    val resetRequest = ResetPasswordRequest(
        token = token,
        new_password = newPassword,
        confirm_password = confirmPassword
    )

    AuthRetrofitInstance.api.resetPassword(resetRequest).enqueue(object : Callback<ResetPasswordResponse> {
        override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
            onLoading(false)

            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let {
                    if (it.status == "success") {
                        Toast.makeText(context, "Contraseña restablecida exitosamente", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.LoginView)
                    } else {
                        onError(it.message ?: "Error desconocido del servidor")
                    }
                }
            } else {
                onError("Error desconocido al restablecer la contraseña")
            }
        }

        override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
            onLoading(false)
            val error = "Fallo en la conexión: ${t.message}"
            onError(error)
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    })
}
