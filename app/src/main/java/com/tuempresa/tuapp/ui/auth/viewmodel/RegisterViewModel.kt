package com.tuempresa.tuapp.ui.auth.viewmodel

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

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val result = registerUseCase.execute(name, email, password)
            _authResult.value = result
        }
    }
}
