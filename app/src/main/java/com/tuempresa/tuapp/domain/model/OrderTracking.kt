package com.tuempresa.tuapp.domain.model

data class OrderTracking(
    val id: String,
    val estado: String, // "Preparando", "En camino", "Entregado"
    val horaLlegada: String, // "10:15"
    val horaEntrega: String, // "10:40"
    val direccion: String,
    val items: List<String>,
    val total: Double,
    val progreso: Float // 0.0 a 1.0
)

