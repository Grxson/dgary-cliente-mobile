package com.tuempresa.tuapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartItemDto(
    @SerializedName("product_id")
    val productId: Int,
    val quantity: Int
)

data class OrderCreateRequestDto(
    val items: List<CartItemDto>,
    @SerializedName("coupon_id")
    val couponId: String? = null,
    val notes: String? = null
)

data class CouponInfoDto(
    val code: String? = null,
    val description: String? = null,
    @SerializedName("applied_discount")
    val appliedDiscount: Double? = null
)

data class OrderPreviewResponseDto(
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    @SerializedName("discount_amount")
    val discountAmount: Double = 0.0,
    @SerializedName("discount_percent")
    val discountPercent: Double = 0.0,
    @SerializedName("delivery_fee")
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0,
    @SerializedName("coupon_info")
    val couponInfo: CouponInfoDto? = null,
    @SerializedName("items_count")
    val itemsCount: Int = 0
)

data class ProductDetailDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val price: Double = 0.0,
    val image: String? = null
)

data class OrderDetailDto(
    val id: Int,
    @SerializedName("product_id")
    val productId: Int,
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    val subtotal: Double,
    val product: ProductDetailDto? = null
)

data class PaymentDto(
    val id: Int,
    val amount: Double = 0.0,
    val status: String? = null,
    @SerializedName("payment_method")
    val paymentMethod: String? = null,
    @SerializedName("stripe_client_secret")
    val stripeClientSecret: String? = null
)

data class CustomerAddressDto(
    val address: String? = null
)

data class OrderResponseDto(
    val id: Int,
    val status: String? = null,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    @SerializedName("delivery_fee")
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    @SerializedName("payment_id")
    val paymentId: Int? = null,
    @SerializedName("payment_intent_id")
    val paymentIntentId: String? = null,
    @SerializedName("client_secret")
    val clientSecret: String? = null
)

data class OrderListDto(
    val id: Int,
    val status: String? = null,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    @SerializedName("delivery_fee")
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    @SerializedName("created_at")
    val createdAt: String? = null,
    val details: List<OrderDetailDto> = emptyList()
)

data class OrderDetailFullDto(
    val id: Int,
    val status: String? = null,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    @SerializedName("delivery_fee")
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val notes: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    val details: List<OrderDetailDto> = emptyList(),
    val payments: List<PaymentDto> = emptyList(),
    val address: CustomerAddressDto? = null
)

