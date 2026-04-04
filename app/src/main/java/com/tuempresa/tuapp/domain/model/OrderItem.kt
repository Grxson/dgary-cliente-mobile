package com.tuempresa.tuapp.domain.model

data class OrderItem(
    val id: String,
    val nombre: String,
    val cantidad: Int,
    val precio: Double,
    val preparaciones: List<Preparacion> = emptyList()
)

