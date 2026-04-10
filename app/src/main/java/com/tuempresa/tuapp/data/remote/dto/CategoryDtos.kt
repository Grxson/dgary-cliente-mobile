package com.tuempresa.tuapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val products: List<ProductDto> = emptyList()
)
