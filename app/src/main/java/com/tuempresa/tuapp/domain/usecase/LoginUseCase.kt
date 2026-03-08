package com.tuempresa.tuapp.domain.usecase

import com.tuempresa.tuapp.domain.model.AuthResult

/**
 * LoginUseCase: lógica de dominio para login
 *
 * Por el momento maneja lógica local
 * Credenciales de prueba: demo@demo.com / 1234
 */
class LoginUseCase {

    suspend fun execute(email: String, password: String): AuthResult {
        // Validación básica
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Email y contraseña requeridos")
        }

        if (password.length < 4) {
            return AuthResult.Error("Contraseña debe tener al menos 4 caracteres")
        }

        // Lógica de prueba local
        return if (email == "demo@demo.com" && password == "1234") {
            AuthResult.Success
        } else {
            AuthResult.Error("Credenciales incorrectas")
        }
    }
}

