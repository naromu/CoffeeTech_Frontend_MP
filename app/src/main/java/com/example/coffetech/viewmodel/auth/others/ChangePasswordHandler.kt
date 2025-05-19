package com.example.coffetech.viewmodel.auth.others

import android.content.Context
import android.widget.Toast
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.ChangePasswordRequest
import com.example.coffetech.model.ChangePasswordResponse
import com.example.coffetech.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun performChangePassword(
    context: Context,
    currentPassword: String,
    newPassword: String,
    onLoading: (Boolean) -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val sharedPreferencesHelper = SharedPreferencesHelper(context)
    val sessionToken = sharedPreferencesHelper.getSessionToken()

    if (sessionToken == null) {
        onError("No se encontró el token de sesión.")
        Toast.makeText(context, "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
        return
    }

    val changePasswordRequest = ChangePasswordRequest(currentPassword, newPassword)

    onLoading(true)

    AuthRetrofitInstance.api.changePassword(changePasswordRequest, sessionToken)
        .enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                onLoading(false)
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res?.status == "success") {
                        Toast.makeText(context, "Contraseña cambiada exitosamente.", Toast.LENGTH_LONG).show()
                        onSuccess()
                    } else {
                        val msg = res?.message ?: "Error desconocido."
                        onError(msg)
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    onError("Error al cambiar la contraseña.")
                    Toast.makeText(context, "Error al cambiar la contraseña.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                onLoading(false)
                val msg = "Error de conexión"
                onError(msg)
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        })
}
