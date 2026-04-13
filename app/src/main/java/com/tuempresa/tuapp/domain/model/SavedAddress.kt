package com.tuempresa.tuapp.domain.model

data class SavedAddress(
    val id: String,
    val titulo: String,
    val direccion: String,
    val detalles: String? = null,
    val esPrincipal: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)

