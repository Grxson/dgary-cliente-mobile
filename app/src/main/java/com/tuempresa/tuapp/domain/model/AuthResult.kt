package com.tuempresa.tuapp.domain.model

sealed class AuthResult {
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
