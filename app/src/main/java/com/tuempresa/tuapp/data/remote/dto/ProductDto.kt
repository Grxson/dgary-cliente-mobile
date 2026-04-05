package com.tuempresa.tuapp.data.remote.dto

data class ProductDto(
    val id: Int,
    val name: String,
    val category: String? = null,
    val price: Double,
    val estimatedTime: String? = null,
    val imageUrl: String? = null
)
