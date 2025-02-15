package com.example.coffetech.model

import com.example.coffetech.utils.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://prueba-production-1b78.up.railway.app"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor()) // Agregar el AuthInterceptor
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Usar el cliente con el interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}