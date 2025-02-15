// AuthInterceptor.kt
package com.example.coffetech.utils

import android.util.Log
import com.example.coffetech.model.ApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.Response // Asegúrate de que esta es la clase correcta

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Leer el cuerpo de la respuesta
        val responseBody = response.body?.string()

        if (responseBody != null) {
            try {
                // Asumiendo que todas las respuestas siguen el formato ApiResponse
                val gson = Gson()
                val apiResponse = gson.fromJson(responseBody, ApiResponse::class.java)

                if (apiResponse.message == "Credenciales expiradas, cerrando sesión.") {
                    // Emitir el evento de logout de manera asíncrona
                    CoroutineScope(Dispatchers.IO).launch {
                        GlobalEventBus.emitLogout()
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Error al parsear la respuesta: ${e.message}")
                // Manejar excepciones de parseo si es necesario
            }
        }

        // Reconstruir el cuerpo de la respuesta para que pueda ser consumido por Retrofit
        val newResponseBody = responseBody?.toResponseBody(response.body?.contentType())

        return response.newBuilder().body(newResponseBody).build()
    }
}
