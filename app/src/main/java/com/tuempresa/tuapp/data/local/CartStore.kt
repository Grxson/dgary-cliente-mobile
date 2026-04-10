package com.tuempresa.tuapp.data.local

import com.tuempresa.tuapp.domain.model.CartItem
import com.tuempresa.tuapp.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CartStore {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun add(product: Product) {
        val productId = product.id.toIntOrNull() ?: return
        val currentItems = _items.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            val currentItem = currentItems[existingIndex]
            currentItems[existingIndex] = currentItem.copy(quantity = currentItem.quantity + 1)
        } else {
            currentItems.add(
                CartItem(
                    productId = productId,
                    name = product.name,
                    price = product.price,
                    quantity = 1,
                    imageUrl = product.imageUrl
                )
            )
        }

        _items.value = currentItems
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            remove(productId)
            return
        }

        _items.value = _items.value.map {
            if (it.productId == productId) it.copy(quantity = quantity) else it
        }
    }

    fun remove(productId: Int) {
        _items.value = _items.value.filterNot { it.productId == productId }
    }

    fun clear() {
        _items.value = emptyList()
    }
}