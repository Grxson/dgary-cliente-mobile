package com.tuempresa.tuapp.domain.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val estimatedTime: String,
    val imageUrl: String? = null
)

data class Category(
    val id: String,
    val name: String,
    val iconRes: Int
)

