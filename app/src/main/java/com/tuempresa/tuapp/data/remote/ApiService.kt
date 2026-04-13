package com.tuempresa.tuapp.data.remote

import com.tuempresa.tuapp.data.remote.dto.ApiEnvelope
import com.tuempresa.tuapp.data.remote.dto.AuthDataDto
import com.tuempresa.tuapp.data.remote.dto.CategoryDto
import com.tuempresa.tuapp.data.remote.dto.CustomerCouponDto
import com.tuempresa.tuapp.data.remote.dto.LoginRequestDto
import com.tuempresa.tuapp.data.remote.dto.ProductDto
import com.tuempresa.tuapp.data.remote.dto.RegisterRequestDto
import com.tuempresa.tuapp.data.remote.dto.CustomerDto
import com.tuempresa.tuapp.data.remote.dto.UpdateProfileRequestDto
import com.tuempresa.tuapp.data.remote.dto.PaymentIntentResponseDto
import com.tuempresa.tuapp.data.remote.dto.ConfirmPaymentRequestDto
import com.tuempresa.tuapp.data.remote.dto.ConfirmPaymentResponseDto
import com.tuempresa.tuapp.data.remote.dto.PaymentStatusResponseDto
import com.tuempresa.tuapp.data.remote.dto.PaymentMethodDto
import com.tuempresa.tuapp.data.remote.dto.UseSavedCardRequestDto
import com.tuempresa.tuapp.data.remote.dto.OrderCreateRequestDto
import com.tuempresa.tuapp.data.remote.dto.OrderDetailFullDto
import com.tuempresa.tuapp.data.remote.dto.OrderListDto
import com.tuempresa.tuapp.data.remote.dto.OrderPreviewResponseDto
import com.tuempresa.tuapp.data.remote.dto.OrderResponseDto
import com.tuempresa.tuapp.data.remote.dto.OrderTrackingDto
import com.tuempresa.tuapp.data.remote.dto.DeliveryDto
import com.tuempresa.tuapp.data.remote.dto.ValidateCouponRequestDto
import com.tuempresa.tuapp.data.remote.dto.ValidateCouponResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiEnvelope<AuthDataDto>>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<ApiEnvelope<AuthDataDto>>

    @GET("api/v1/products")
    suspend fun getProducts(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<List<ProductDto>>>

    @GET("api/v1/products/{id}")
    suspend fun getProductById(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String
    ): Response<ApiEnvelope<ProductDto>>

    @GET("api/v1/categories")
    suspend fun getCategories(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<List<CategoryDto>>>

    @GET("api/v1/categories/{id}")
    suspend fun getCategoryById(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String
    ): Response<ApiEnvelope<CategoryDto>>

    @GET("api/v1/auth/me")
    suspend fun me(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<CustomerDto>>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<Unit>>

    @PUT("api/v1/auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateProfileRequestDto
    ): Response<ApiEnvelope<CustomerDto>>

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

    @POST("api/v1/orders/{orderId}/payment/use-saved")
    suspend fun payWithSavedCard(
        @Path("orderId") orderId: String,
        @Body request: UseSavedCardRequestDto,
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

    @POST("api/v1/payment-methods/cleanup")
    suspend fun cleanupPaymentMethods(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<Unit>>

    @GET("api/v1/deliveries")
    suspend fun getDeliveries(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<List<DeliveryDto>>>

    @GET("api/v1/orders/{orderId}/delivery")
    suspend fun getOrderDelivery(
        @Path("orderId") orderId: String,
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<DeliveryDto>>

    @GET("api/v1/orders/{orderId}/tracking")
    suspend fun getOrderTracking(
        @Path("orderId") orderId: String,
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<OrderTrackingDto>>

    @GET("api/v1/coupons")
    suspend fun getCoupons(
        @Header("Authorization") bearerToken: String
    ): Response<ApiEnvelope<List<CustomerCouponDto>>>

    @POST("api/v1/coupons/validate")
    suspend fun validateCoupon(
        @Header("Authorization") bearerToken: String,
        @Body request: ValidateCouponRequestDto
    ): Response<ApiEnvelope<ValidateCouponResponseDto>>

    // Orders
    @POST("api/v1/orders/preview")
    suspend fun previewOrder(
        @Header("Authorization") bearerToken: String,
        @Body request: OrderCreateRequestDto
    ): Response<ApiEnvelope<OrderPreviewResponseDto>>

    @POST("api/v1/orders")
    suspend fun createOrder(
        @Header("Authorization") bearerToken: String,
        @Body request: OrderCreateRequestDto
    ): Response<ApiEnvelope<OrderResponseDto>>

    @GET("api/v1/orders")
    suspend fun getOrders(
        @Header("Authorization") bearerToken: String,
        @Query("page") page: Int = 1,
        @Query("status") status: String? = null
    ): Response<ApiEnvelope<List<OrderListDto>>>

    @GET("api/v1/orders/{id}")
    suspend fun getOrder(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String
    ): Response<ApiEnvelope<OrderDetailFullDto>>

    @POST("api/v1/orders/{id}/reorder")
    suspend fun reorderOrder(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String
    ): Response<ApiEnvelope<OrderResponseDto>>

    @DELETE("api/v1/orders/{id}")
    suspend fun cancelOrder(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String
    ): Response<ApiEnvelope<OrderDetailFullDto>>
}
