package com.example.coffetech.viewmodel.auth.others

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.UpdateProfileRequest
import com.example.coffetech.model.UpdateProfileResponse
import com.example.coffetech.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun performUpdateProfile(
    context: Context,
    newName: String,
    email: String,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit,
    onSuccess: () -> Unit
) {
    val sharedPreferencesHelper = SharedPreferencesHelper(context)
    val sessionToken = sharedPreferencesHelper.getSessionToken()

    if (sessionToken == null) {
        val error = "No se encontró el token de sesión."
        onError(error)
        Toast.makeText(context, "Error: $error Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
        return
    }

    val updateRequest = UpdateProfileRequest(new_name = newName)
    onLoading(true)

    AuthRetrofitInstance.api.updateProfile(updateRequest, sessionToken).enqueue(object : Callback<UpdateProfileResponse> {
        override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
            onLoading(false)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody?.status == "success") {
                    sharedPreferencesHelper.saveSessionData(sessionToken, newName, email)
                    Toast.makeText(context, "Perfil actualizado exitosamente.", Toast.LENGTH_LONG).show()
                    onSuccess()
                } else {
                    val message = responseBody?.message ?: "Error desconocido al actualizar el perfil."
                    onError(message)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            } else {
                val message = "Error al actualizar el perfil."
                onError(message)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                Log.e("performUpdateProfile", "Error del servidor: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
            val message = "Error de conexión"
            onError(message)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            Log.e("performUpdateProfile", "Fallo: ${t.message}")
        }
    })
}
