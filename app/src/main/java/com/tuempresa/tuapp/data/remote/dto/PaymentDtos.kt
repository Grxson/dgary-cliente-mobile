package com.tuempresa.tuapp.data.remote.dto

data class PaymentIntentRequestDto(
    val order_id: String
)

data class PaymentIntentResponseDto(
    val payment_id: String,
    val stripe_payment_intent_id: String,
    val client_secret: String,
    val amount: Long,
    val currency: String,
    val is_setup_intent: Boolean? = false // TRUE para agregar tarjeta, FALSE para pagar
)

data class ConfirmPaymentRequestDto(
    val payment_intent_id: String
)

data class ConfirmPaymentResponseDto(
    val payment_id: String,
    val status: String,
    val order_status: String,
    val amount_paid: Double
)

data class PaymentStatusResponseDto(
    val order_id: String,
    val payment_id: String?,
    val payment_status: String,
    val amount: Double,
    val order_status: String,
    val paid_at: String?
)
