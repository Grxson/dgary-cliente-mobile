package com.tuempresa.tuapp.data.remote.dto

/**
 * Contrato base usado por la API Laravel.
 */
data class ApiEnvelope<T>(
    val data: T? = null,
    val message: String? = null,
    val errors: Map<String, List<String>>? = null,
    val meta: MetaDto? = null
)

data class MetaDto(
    val total: Int? = null,
    val per_page: Int? = null,
    val current_page: Int? = null,
    val last_page: Int? = null
)
