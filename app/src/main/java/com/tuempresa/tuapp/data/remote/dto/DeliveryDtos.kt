package com.tuempresa.tuapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DeliveryDto(
    val id: Int,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("user_id")
    val userId: Int,
    val address: String? = null,
    val status: String? = null,
    val notes: String? = null,
    val total: Double = 0.0,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
