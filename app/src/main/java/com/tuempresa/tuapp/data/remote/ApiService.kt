package com.tuempresa.tuapp.data.remote

import com.tuempresa.tuapp.data.remote.dto.ApiEnvelope
import com.tuempresa.tuapp.data.remote.dto.AuthDataDto
import com.tuempresa.tuapp.data.remote.dto.LoginRequestDto
import com.tuempresa.tuapp.data.remote.dto.ProductDto
import com.tuempresa.tuapp.data.remote.dto.RegisterRequestDto
import com.tuempresa.tuapp.data.remote.dto.PaymentIntentResponseDto
import com.tuempresa.tuapp.data.remote.dto.ConfirmPaymentRequestDto
import com.tuempresa.tuapp.data.remote.dto.ConfirmPaymentResponseDto
import com.tuempresa.tuapp.data.remote.dto.PaymentStatusResponseDto
import com.tuempresa.tuapp.data.remote.dto.PaymentMethodDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiEnvelope<AuthDataDto>>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<ApiEnvelope<AuthDataDto>>

    @GET("api/v1/products")
    suspend fun getProducts(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<List<ProductDto>>>

    // Payment endpoints
    @POST("api/v1/orders/{orderId}/payment/intent")
    suspend fun createPaymentIntent(
        @Path("orderId") orderId: String,
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<PaymentIntentResponseDto>>

    @POST("api/v1/orders/{orderId}/payment/confirm")
    suspend fun confirmPayment(
        @Path("orderId") orderId: String,
        @Body request: ConfirmPaymentRequestDto,
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<ConfirmPaymentResponseDto>>

    @GET("api/v1/orders/{orderId}/payment/status")
    suspend fun getPaymentStatus(
        @Path("orderId") orderId: String,
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<PaymentStatusResponseDto>>

    @GET("api/v1/payment-methods")
    suspend fun getPaymentMethods(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<List<PaymentMethodDto>>>
}
