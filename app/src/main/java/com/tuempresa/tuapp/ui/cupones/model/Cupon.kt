package com.tuempresa.tuapp.ui.cupones.model

data class Cupon(
    val id: Int,
    val code: String,
    val titulo: String,
    val descripcion: String,
    val discountAmount: Double,
    val descuento: String,
    val botonTexto: String
)

