package com.tuempresa.tuapp.ui.order.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.tuapp.data.repository.OrderRepository
import com.tuempresa.tuapp.data.remote.dto.OrderListDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrdersListState(
    val orders: List<OrderListDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrdersListViewModel(context: Context) : ViewModel() {

    private val repository = OrderRepository(context)

    private val _state = MutableStateFlow(OrdersListState())
    val state: StateFlow<OrdersListState> = _state.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val response = repository.getOrders()
            val orders = response?.data.orEmpty()
            _state.value = _state.value.copy(
                orders = orders,
                isLoading = false,
                error = if (response == null) "No se pudieron cargar las órdenes" else null
            )
        }
    }
}