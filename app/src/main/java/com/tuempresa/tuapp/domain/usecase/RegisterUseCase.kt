package com.tuempresa.tuapp.domain.usecase

import android.content.Context
import com.tuempresa.tuapp.data.repository.AuthRepository
import com.tuempresa.tuapp.domain.model.AuthResult

class RegisterUseCase(context: Context) {

    private val authRepository = AuthRepository(context)

    suspend fun execute(name: String, email: String, phone: String, password: String): AuthResult {
        if (name.isBlank()) {
            return AuthResult.Error("Nombre requerido")
        }
        if (email.isBlank()) {
            return AuthResult.Error("Email requerido")
        }
        if (phone.isBlank()) {
            return AuthResult.Error("Teléfono requerido")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Contraseña requerida")
        }
        if (password.length < 8) {
            return AuthResult.Error("Contraseña debe tener al menos 8 caracteres")
        }

        // En esta etapa usamos dirección por defecto hasta implementar direcciones guardadas.
        return authRepository.register(
            name = name,
            email = email,
            password = password,
            phone = phone,
            address = "Sin dirección"
        )
    }
}
