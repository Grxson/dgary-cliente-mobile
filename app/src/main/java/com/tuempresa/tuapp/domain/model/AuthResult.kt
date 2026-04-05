package com.tuempresa.tuapp.domain.model

sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val message: String = "Operación exitosa") : AuthResult()
    data class Error(val message: String) : AuthResult()
}
