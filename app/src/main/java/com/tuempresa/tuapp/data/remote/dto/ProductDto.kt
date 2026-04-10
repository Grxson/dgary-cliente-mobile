package com.tuempresa.tuapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    val id: Int,
    val name: String,
    @SerializedName("category_id")
    val categoryId: Int? = null,
    val category: String? = null,
    val price: Double,
    val stock: Double? = null,
    val points: Int? = null,
    val status: Boolean? = null,
    val estimatedTime: String? = null,
    val imageUrl: String? = null
)
