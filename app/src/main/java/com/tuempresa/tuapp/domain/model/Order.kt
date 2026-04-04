package com.tuempresa.tuapp.domain.model

data class Order(
    val id: String,
    val ubicacion: String,
    val tiempoEntrega: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val descuento: Double,
    val total: Double,
    val cuponAplicado: String? = null,
    val ahorro: Double = 0.0
)

