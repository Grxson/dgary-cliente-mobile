package com.tuempresa.tuapp.ui.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.tuapp.data.local.CartStore
import com.tuempresa.tuapp.data.local.SelectedCoupon
import com.tuempresa.tuapp.data.local.SelectedCouponStore
import com.tuempresa.tuapp.data.remote.dto.CartItemDto
import com.tuempresa.tuapp.data.remote.dto.OrderPreviewResponseDto
import com.tuempresa.tuapp.data.remote.dto.OrderResponseDto
import com.tuempresa.tuapp.data.repository.OrderRepository
import com.tuempresa.tuapp.domain.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class CartState(
    val items: List<CartItem> = emptyList(),
    val preview: OrderPreviewResponseDto? = null,
    val selectedCoupon: SelectedCoupon? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val createdOrder: OrderResponseDto? = null
)

class CartViewModel(
    private val repository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(CartStore.items, SelectedCouponStore.selectedCoupon) { items, coupon ->
                items to coupon
            }.collect { (items, coupon) ->
                _state.value = _state.value.copy(items = items, selectedCoupon = coupon)
                refreshPreview()
            }
        }
    }

    fun addProduct(product: CartItem) {
        CartStore.updateQuantity(product.productId, product.quantity + 1)
    }

    fun increaseQuantity(productId: Int) {
        val item = _state.value.items.firstOrNull { it.productId == productId } ?: return
        CartStore.updateQuantity(productId, item.quantity + 1)
    }

    fun decreaseQuantity(productId: Int) {
        val item = _state.value.items.firstOrNull { it.productId == productId } ?: return
        CartStore.updateQuantity(productId, item.quantity - 1)
    }

    fun removeItem(productId: Int) {
        CartStore.remove(productId)
    }

    fun clearCart() {
        CartStore.clear()
        SelectedCouponStore.clear()
        _state.value = CartState()
    }

    fun refreshPreview() {
        val items = _state.value.items
        if (items.isEmpty()) {
            _state.value = _state.value.copy(preview = null, error = null)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val preview = repository.previewOrder(
                items = items.map { it.toDto() },
                couponId = _state.value.selectedCoupon?.code
            )

            val fallbackPreview = if (preview == null) {
                buildLocalPreview(items)
            } else {
                null
            }

            _state.value = _state.value.copy(
                preview = preview ?: fallbackPreview,
                isLoading = false,
                error = if (preview == null && fallbackPreview == null) {
                    "No se pudo calcular el resumen"
                } else {
                    null
                }
            )
        }
    }

    fun checkout(
        address: String,
        destinationLat: Double,
        destinationLng: Double,
        onSuccess: (String) -> Unit = {}
    ) {
        val items = _state.value.items
        if (items.isEmpty()) {
            _state.value = _state.value.copy(error = "Tu carrito está vacío")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            val order = repository.createOrder(
                items = items.map { it.toDto() },
                couponId = _state.value.selectedCoupon?.code,
                address = address,
                destinationLat = destinationLat,
                destinationLng = destinationLng
            )
            _state.value = _state.value.copy(isSubmitting = false, createdOrder = order)

            if (order != null) {
                onSuccess(order.id.toString())
            } else {
                _state.value = _state.value.copy(error = "No se pudo crear la orden")
            }
        }
    }

    private fun CartItem.toDto(): CartItemDto {
        return CartItemDto(
            productId = productId,
            quantity = quantity
        )
    }

    private fun buildLocalPreview(items: List<CartItem>): OrderPreviewResponseDto? {
        val coupon = _state.value.selectedCoupon ?: return null
        val subtotal = items.sumOf { it.price * it.quantity }
        val discount = coupon.discountAmount.coerceAtLeast(0.0).coerceAtMost(subtotal)
        val total = (subtotal - discount).coerceAtLeast(0.0)

        return OrderPreviewResponseDto(
            subtotal = subtotal,
            tax = 0.0,
            discountAmount = discount,
            discountPercent = 0.0,
            deliveryFee = 0.0,
            total = total,
            couponInfo = com.tuempresa.tuapp.data.remote.dto.CouponInfoDto(
                code = coupon.code,
                description = coupon.title,
                appliedDiscount = discount
            ),
            itemsCount = items.size
        )
    }
}