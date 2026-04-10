package com.tuempresa.tuapp.domain.model

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val imageUrl: String? = null
)