package com.tuempresa.tuapp.data.remote

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Retrofit centralizado.
 * Conectado al backend de producción en Railway
 */
object ApiClient {

    private const val BASE_URL = "https://dgaryweb-production.up.railway.app/"

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
