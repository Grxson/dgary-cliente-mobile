package com.tuempresa.tuapp.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.tuapp.domain.model.AuthResult
import com.tuempresa.tuapp.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    fun register(name: String, email: String, phone: String, password: String) {
        Log.d("RegisterViewModel", "👤 register() llamado con email=$email")
        viewModelScope.launch {
            Log.d("RegisterViewModel", "🔄 viewModelScope.launch ejecutado")
            _authResult.value = AuthResult.Loading
            Log.d("RegisterViewModel", "📡 Llamando a registerUseCase.execute()...")
            try {
                val result = registerUseCase.execute(name, email, phone, password)
                Log.d("RegisterViewModel", "📨 Resultado recibido: $result")
                _authResult.value = result
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "❌ EXCEPCIÓN en register: ${e.message}", e)
                _authResult.value = AuthResult.Error(e.message ?: "Unknown error")
            }
        }
    }
}
