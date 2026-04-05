package com.tuempresa.tuapp.data.repository

import android.content.Context
import android.util.Log
import com.tuempresa.tuapp.data.local.SessionManager
import com.tuempresa.tuapp.data.remote.ApiClient
import com.tuempresa.tuapp.data.remote.dto.LoginRequestDto
import com.tuempresa.tuapp.data.remote.dto.RegisterRequestDto
import com.tuempresa.tuapp.domain.model.AuthResult
import org.json.JSONObject

class AuthRepository(context: Context) {

    private val sessionManager = SessionManager(context)
    private val apiService = ApiClient.apiService

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            Log.d("AuthRepository", "🔐 LOGIN - Llamando a API con email: $email")
            val response = apiService.login(LoginRequestDto(email, password))
            Log.d("AuthRepository", "🔐 LOGIN - Respuesta recibida: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("AuthRepository", "🔐 LOGIN - Body: $body")
                val token = body?.data?.token
                Log.d("AuthRepository", "🔐 LOGIN - Token extraído: ${token?.take(10)}...")
                if (!token.isNullOrBlank()) {
                    sessionManager.saveToken(token)
                    Log.d("AuthRepository", "🔐 LOGIN - Token guardado en SharedPreferences ✅")
                } else {
                    Log.w("AuthRepository", "🔐 LOGIN - Token es null o blanco ⚠️")
                }
                AuthResult.Success(message = body?.message ?: "Sesión iniciada")
            } else {
                val errorMessage = "No se pudo iniciar sesión (${response.code()})"
                Log.e("AuthRepository", "🔐 LOGIN - Error: $errorMessage")
                AuthResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "🔐 LOGIN - Excepción: ${e.message}", e)
            AuthResult.Error("Error de red: ${e.message ?: "desconocido"}")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ): AuthResult {
        return try {
            val response = apiService.register(
                RegisterRequestDto(
                    name = name,
                    email = email,
                    password = password,
                    phone = phone,
                    address = address
                )
            )

            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.data?.token
                if (!token.isNullOrBlank()) {
                    sessionManager.saveToken(token)
                }
                AuthResult.Success(message = body?.message ?: "Cuenta creada")
            } else {
                val errorBody = response.errorBody()?.string()
                val backendMessage = parseBackendError(errorBody)
                val errorMessage = backendMessage ?: "No se pudo registrar (${response.code()})"
                Log.e("AuthRepository", "🔐 REGISTER - Error ${response.code()}: $errorMessage")
                AuthResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "🔐 REGISTER - Excepción: ${e.message}", e)
            AuthResult.Error("Error de red: ${e.message ?: "desconocido"}")
        }
    }

    private fun parseBackendError(rawBody: String?): String? {
        if (rawBody.isNullOrBlank()) return null
        return try {
            val json = JSONObject(rawBody)
            if (json.has("errors")) {
                val errors = json.getJSONObject("errors")
                val keys = errors.keys()
                val messages = mutableListOf<String>()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val arr = errors.optJSONArray(key)
                    if (arr != null && arr.length() > 0) {
                        messages.add(arr.getString(0))
                    }
                }
                if (messages.isNotEmpty()) {
                    messages.joinToString("\n")
                } else {
                    json.optString("message").takeIf { it.isNotBlank() }
                }
            } else {
                json.optString("message").takeIf { it.isNotBlank() }
            }
        } catch (_: Exception) {
            null
        }
    }
}
