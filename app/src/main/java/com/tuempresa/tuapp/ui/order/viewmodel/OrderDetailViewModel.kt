package com.tuempresa.tuapp.ui.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.tuapp.data.repository.OrderRepository
import com.tuempresa.tuapp.data.remote.dto.OrderDetailFullDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderDetailState(
    val order: OrderDetailFullDto? = null,
    val isLoading: Boolean = false,
    val isReordering: Boolean = false,
    val isCancelling: Boolean = false,
    val error: String? = null
)

class OrderDetailViewModel(
    private val repository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailState())
    val state: StateFlow<OrderDetailState> = _state.asStateFlow()

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val order = repository.getOrder(orderId)
            _state.value = _state.value.copy(
                order = order,
                isLoading = false,
                error = if (order == null) "No se pudo cargar la orden" else null
            )
        }
    }

    fun reorder(orderId: String, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isReordering = true, error = null)
            val response = repository.reorder(orderId)
            _state.value = _state.value.copy(isReordering = false)

            if (response != null) {
                onSuccess(response.id.toString())
            } else {
                _state.value = _state.value.copy(error = "No se pudo reordenar")
            }
        }
    }

    fun cancelOrder(orderId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCancelling = true, error = null)
            val cancelled = repository.cancelOrder(orderId)
            _state.value = _state.value.copy(isCancelling = false)

            if (cancelled != null) {
                _state.value = _state.value.copy(order = cancelled)
                onSuccess()
            } else {
                _state.value = _state.value.copy(error = "No se pudo cancelar la orden")
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
