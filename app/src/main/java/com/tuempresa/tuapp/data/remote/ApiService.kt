package com.tuempresa.tuapp.data.remote

import com.tuempresa.tuapp.data.remote.dto.ApiEnvelope
import com.tuempresa.tuapp.data.remote.dto.AuthDataDto
import com.tuempresa.tuapp.data.remote.dto.LoginRequestDto
import com.tuempresa.tuapp.data.remote.dto.ProductDto
import com.tuempresa.tuapp.data.remote.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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
}
