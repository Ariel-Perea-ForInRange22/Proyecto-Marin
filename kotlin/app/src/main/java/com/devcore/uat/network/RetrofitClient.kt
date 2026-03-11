package com.devcore.uat.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANTE: En el emulador de Android Studio 10.0.2.2 apunta a localhost.
    // Si usas un dispositivo físico en la misma red Wi-Fi, cambia esta IP por tu IP local.
    private const val BASE_URL = "http://172.31.205.19:8000/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        instance.create(ApiService::class.java)
    }
}
