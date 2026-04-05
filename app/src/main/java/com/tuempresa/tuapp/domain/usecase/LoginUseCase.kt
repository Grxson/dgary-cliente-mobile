package com.tuempresa.tuapp.domain.usecase

import android.content.Context
import com.tuempresa.tuapp.data.repository.AuthRepository
import com.tuempresa.tuapp.domain.model.AuthResult

/**
 * LoginUseCase: valida el input y delega la autenticación al backend.
 */
class LoginUseCase(context: Context) {

    private val authRepository = AuthRepository(context)

    suspend fun execute(email: String, password: String): AuthResult {
        // Validación básica
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Email y contraseña requeridos")
        }

        if (password.length < 8) {
            return AuthResult.Error("Contraseña debe tener al menos 8 caracteres")
        }

        // Delegar autenticación al backend
        return authRepository.login(email, password)
    }
}

