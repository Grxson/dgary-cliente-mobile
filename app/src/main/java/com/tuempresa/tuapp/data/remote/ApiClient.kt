package com.tuempresa.tuapp.data.remote

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Retrofit centralizado.
 * Configurado para conectarse a la PC en la red local (192.168.1.12)
 */
object ApiClient {

    private const val BASE_URL = "http://192.168.1.12:8000/"

    val apiService: ApiService by lazy {
        Log.d("ApiClient", "🌐 Inicializando Retrofit con BASE_URL=$BASE_URL")
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Log.d("ApiClient", "✅ Retrofit inicializado correctamente")
        retrofit.create(ApiService::class.java)
    }
}
