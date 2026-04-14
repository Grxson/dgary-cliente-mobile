package com.tuempresa.tuapp.data.repository

import android.content.Context
import android.util.Log
import com.tuempresa.tuapp.data.local.SessionManager
import com.tuempresa.tuapp.data.remote.ApiClient
import com.tuempresa.tuapp.data.remote.dto.CouponDto
import com.tuempresa.tuapp.data.remote.dto.ValidateCouponRequestDto
import com.tuempresa.tuapp.data.remote.dto.ValidateCouponResponseDto

class CouponRepository(context: Context) {

    private val sessionManager = SessionManager(context)
    private val apiService = ApiClient.apiService

    private fun bearerToken(): String? {
        val token = sessionManager.getToken()
        return token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
    }

    suspend fun getCoupons(): List<CouponDto> {
        val token = bearerToken() ?: return emptyList()

        return try {
            val response = apiService.getCoupons(token)
            if (response.isSuccessful) {
                response.body()?.data.orEmpty()
            } else {
                Log.e("CouponRepository", "Get coupons failed: ${response.code()} ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CouponRepository", "Get coupons exception: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun validateCoupon(couponCode: String, subtotal: Double): ValidateCouponResponseDto? {
        val token = bearerToken() ?: return null

        return try {
            val response = apiService.validateCoupon(
                bearerToken = token,
                request = ValidateCouponRequestDto(couponId = couponCode, subtotal = subtotal)
            )
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("CouponRepository", "Validate coupon failed: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CouponRepository", "Validate coupon exception: ${e.message}", e)
            null
        }
    }
}
