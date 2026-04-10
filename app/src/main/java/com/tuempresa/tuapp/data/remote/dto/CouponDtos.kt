package com.tuempresa.tuapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CouponMetaDto(
    val id: Int? = null,
    val name: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean? = null,
    @SerializedName("expires_at")
    val expiresAt: String? = null
)

data class CustomerCouponDto(
    val id: Int,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("coupon_id")
    val couponId: Int,
    val status: Boolean,
    val discount: Int,
    val coupon: CouponMetaDto? = null
)

data class ValidateCouponRequestDto(
    @SerializedName("coupon_id")
    val couponId: Int,
    val subtotal: Double
)

data class ValidateCouponResponseDto(
    @SerializedName("coupon_id")
    val couponId: Int,
    @SerializedName("coupon_name")
    val couponName: String? = null,
    @SerializedName("discount_percent")
    val discountPercent: Int = 0,
    val subtotal: Double = 0.0,
    @SerializedName("discount_amount")
    val discountAmount: Double = 0.0,
    val total: Double = 0.0
)
