package com.tuempresa.tuapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * DTO que representa una tarjeta guardada del usuario
 */
data class PaymentMethodDto(
    @SerializedName("payment_id")
    val payment_id: String,
    
    @SerializedName("stripe_charge_id")
    val stripe_charge_id: String?,
    
    @SerializedName("card_brand")
    val card_brand: String, // Visa, Mastercard, etc.
    
    @SerializedName("card_last_four")
    val card_last_four: String, // Últimos 4 dígitos
    
    @SerializedName("card_display")
    val card_display: String, // "Visa •••• 4242"
    
    @SerializedName("first_used")
    val first_used: String?, // ISO 8601 timestamp
    
    @SerializedName("last_used")
    val last_used: String?, // ISO 8601 timestamp
    
    @SerializedName("usage_count")
    val usage_count: Int // Cuántas veces se usó esta tarjeta
)
