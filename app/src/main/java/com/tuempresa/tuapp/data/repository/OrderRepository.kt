package com.tuempresa.tuapp.data.repository

import android.content.Context
import android.util.Log
import com.tuempresa.tuapp.data.local.SessionManager
import com.tuempresa.tuapp.data.remote.ApiClient
import com.tuempresa.tuapp.data.remote.dto.ApiEnvelope
import com.tuempresa.tuapp.data.remote.dto.CartItemDto
import com.tuempresa.tuapp.data.remote.dto.OrderCreateRequestDto
import com.tuempresa.tuapp.data.remote.dto.OrderDetailFullDto
import com.tuempresa.tuapp.data.remote.dto.OrderListDto
import com.tuempresa.tuapp.data.remote.dto.OrderPreviewResponseDto
import com.tuempresa.tuapp.data.remote.dto.OrderResponseDto

class OrderRepository(context: Context) {

    private val sessionManager = SessionManager(context)
    private val apiService = ApiClient.apiService

    private fun bearerToken(): String? {
        val token = sessionManager.getToken()
        return token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
    }

    private fun shouldRetryWithoutCoupon(statusCode: Int, errorBody: String): Boolean {
        if (statusCode != 422) return false

        val normalized = errorBody.lowercase()
        return normalized.contains("validation.exists") ||
            normalized.contains("coupon_id") ||
            normalized.contains("cupón no disponible")
    }

    suspend fun previewOrder(
        items: List<CartItemDto>,
        couponId: String? = null,
        notes: String? = null
    ): OrderPreviewResponseDto? {
        val token = bearerToken() ?: return null

        return try {
            val request = OrderCreateRequestDto(items = items, couponId = couponId, notes = notes)
            val response = apiService.previewOrder(token, request)

            if (response.isSuccessful) {
                response.body()?.data
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()

                if (couponId != null && shouldRetryWithoutCoupon(response.code(), errorBody)) {
                    Log.w("OrderRepository", "Preview retry without coupon due to incompatible backend contract")
                    val fallbackResponse = apiService.previewOrder(
                        token,
                        request.copy(couponId = null)
                    )

                    if (fallbackResponse.isSuccessful) {
                        return fallbackResponse.body()?.data
                    }

                    Log.e(
                        "OrderRepository",
                        "Preview fallback failed: ${fallbackResponse.code()} ${fallbackResponse.errorBody()?.string()}"
                    )
                    return null
                }

                Log.e("OrderRepository", "Preview failed: ${response.code()} $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Preview exception: ${e.message}", e)
            null
        }
    }

    suspend fun createOrder(
        items: List<CartItemDto>,
        couponId: String? = null,
        notes: String? = null,
        address: String? = null,
        destinationLat: Double? = null,
        destinationLng: Double? = null
    ): OrderResponseDto? {
        val token = bearerToken() ?: return null

        return try {
            val request = OrderCreateRequestDto(
                items = items,
                couponId = couponId,
                notes = notes,
                address = address,
                destinationLat = destinationLat,
                destinationLng = destinationLng
            )
            val response = apiService.createOrder(token, request)

            if (response.isSuccessful) {
                response.body()?.data
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()

                if (couponId != null && shouldRetryWithoutCoupon(response.code(), errorBody)) {
                    Log.w("OrderRepository", "Create retry without coupon due to incompatible backend contract")
                    val fallbackResponse = apiService.createOrder(
                        token,
                        request.copy(couponId = null)
                    )

                    if (fallbackResponse.isSuccessful) {
                        return fallbackResponse.body()?.data
                    }

                    Log.e(
                        "OrderRepository",
                        "Create fallback failed: ${fallbackResponse.code()} ${fallbackResponse.errorBody()?.string()}"
                    )
                    return null
                }

                Log.e("OrderRepository", "Create failed: ${response.code()} $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Create exception: ${e.message}", e)
            null
        }
    }

    suspend fun getOrders(page: Int = 1, status: String? = null): ApiEnvelope<List<OrderListDto>>? {
        val token = bearerToken() ?: return null

        return try {
            val response = apiService.getOrders(token, page, status)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("OrderRepository", "List failed: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "List exception: ${e.message}", e)
            null
        }
    }

    suspend fun getOrder(id: String): OrderDetailFullDto? {
        val token = bearerToken() ?: return null

        return try {
            val response = apiService.getOrder(token, id)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("OrderRepository", "Detail failed: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Detail exception: ${e.message}", e)
            null
        }
    }

    suspend fun reorder(id: String): OrderResponseDto? {
        val token = bearerToken() ?: return null

        return try {
            val response = apiService.reorderOrder(token, id)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("OrderRepository", "Reorder failed: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Reorder exception: ${e.message}", e)
            null
        }
    }

    suspend fun cancelOrder(id: String): OrderDetailFullDto? {
        val token = bearerToken() ?: return null

        return try {
            val response = apiService.cancelOrder(token, id)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("OrderRepository", "Cancel failed: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Cancel exception: ${e.message}", e)
            null
        }
    }
}
