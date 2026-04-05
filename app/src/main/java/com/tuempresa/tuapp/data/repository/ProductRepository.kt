package com.tuempresa.tuapp.data.repository

import android.content.Context
import android.util.Log
import com.tuempresa.tuapp.data.local.SessionManager
import com.tuempresa.tuapp.data.remote.ApiClient
import com.tuempresa.tuapp.domain.model.Product

class ProductRepository(context: Context) {

    private val sessionManager = SessionManager(context)
    private val apiService = ApiClient.apiService

    suspend fun getProducts(): List<Product> {
        Log.d("ProductRepository", "📦 PRODUCTOS - Iniciando carga...")
        
        val token = sessionManager.getToken()
        Log.d("ProductRepository", "📦 PRODUCTOS - Token recuperado: ${token?.take(10)}... (null: ${token == null})")
        
        if (token.isNullOrBlank()) {
            Log.w("ProductRepository", "📦 PRODUCTOS - Sin token, retornando lista vacía ⚠️")
            return emptyList()
        }

        return try {
            Log.d("ProductRepository", "📦 PRODUCTOS - Llamando a API con Bearer token...")
            val response = apiService.getProducts("Bearer $token")
            Log.d("ProductRepository", "📦 PRODUCTOS - Respuesta recibida: ${response.code()}")

            if (!response.isSuccessful) {
                Log.e("ProductRepository", "📦 PRODUCTOS - Error HTTP ${response.code()}")
                return emptyList()
            }

            val payload = response.body()?.data
            Log.d("ProductRepository", "📦 PRODUCTOS - Productos recibidos: ${payload?.size ?: 0}")

            payload?.map {
                Product(
                    id = it.id.toString(),
                    name = it.name,
                    category = it.category ?: "Sin categoría",
                    price = it.price,
                    estimatedTime = it.estimatedTime ?: "10-25 min Estimación",
                    imageUrl = it.imageUrl
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("ProductRepository", "📦 PRODUCTOS - Excepción: ${e.message}", e)
            emptyList()
        }
    }
}
