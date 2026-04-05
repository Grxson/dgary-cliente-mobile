package com.tuempresa.tuapp.data.remote.dto

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String
)

data class CustomerDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val points: Int? = 0
)

data class AuthDataDto(
    val customer: CustomerDto,
    val token: String,
    val expires_at: String? = null
)
