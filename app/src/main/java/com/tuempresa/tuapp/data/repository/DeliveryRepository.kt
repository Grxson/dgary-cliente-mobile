package com.tuempresa.tuapp.data.repository

import android.content.Context
import android.util.Log
import com.tuempresa.tuapp.data.local.SessionManager
import com.tuempresa.tuapp.data.remote.ApiClient
import com.tuempresa.tuapp.data.remote.dto.DeliveryDto
import com.tuempresa.tuapp.data.remote.dto.OrderTrackingDto

class DeliveryRepository(context: Context) {

    private val sessionManager = SessionManager(context)
    private val apiService = ApiClient.apiService

    private fun bearerToken(): String? {
        val token = sessionManager.getToken()
        return token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
    }

    suspend fun getDeliveries(): List<DeliveryDto> {
        val token = bearerToken() ?: return emptyList()

        return try {
            val response = apiService.getDeliveries(token)
            if (response.isSuccessful) {
                response.body()?.data.orEmpty()
            } else {
                Log.e("DeliveryRepository", "Get deliveries failed: ${response.code()} ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Get deliveries exception: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getOrderDelivery(orderId: String): DeliveryDto? {
        val token = bearerToken() ?: return null

        return try {
            val response = apiService.getOrderDelivery(orderId = orderId, bearerToken = token)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("DeliveryRepository", "Get order delivery failed: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Get order delivery exception: ${e.message}", e)
            null
        }
    }

    suspend fun getOrderTracking(orderId: String): OrderTrackingDto? {
        val token = bearerToken() ?: return null

        return try {
            val response = apiService.getOrderTracking(orderId = orderId, bearerToken = token)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("DeliveryRepository", "Get order tracking failed: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("DeliveryRepository", "Get order tracking exception: ${e.message}", e)
            null
        }
    }
}
