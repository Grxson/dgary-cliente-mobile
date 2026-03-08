package com.tuempresa.tuapp.domain.usecase

import com.tuempresa.tuapp.domain.model.AuthResult

class RegisterUseCase {

    suspend fun execute(name: String, email: String, password: String): AuthResult {
        if (name.isBlank()) {
            return AuthResult.Error("Nombre requerido")
        }
        if (email.isBlank()) {
            return AuthResult.Error("Email requerido")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Contraseña requerida")
        }
        if (password.length < 4) {
            return AuthResult.Error("Contraseña debe tener al menos 4 caracteres")
        }
        return AuthResult.Success
    }
}
