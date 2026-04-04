package com.tuempresa.tuapp.domain.usecase

import com.tuempresa.tuapp.domain.model.Product

class GetProductsUseCase {
    fun execute(): List<Product> {
        // Retornar datos de prueba
        return listOf(
            Product(
                id = "1",
                name = "Dgary Nieves",
                category = "Nieves",
                price = 20.00,
                estimatedTime = "10-25 min Estimación"
            ),
            Product(
                id = "2",
                name = "Dgary Paletas Leche",
                category = "P. Leche",
                price = 10.10,
                estimatedTime = "10-25 min Estimación"
            ),
            Product(
                id = "3",
                name = "Dgary Paletas Agua",
                category = "P. Agua",
                price = 10.10,
                estimatedTime = "10-25 min Estimación"
            ),
            Product(
                id = "4",
                name = "Dgary Aguas",
                category = "Aguas",
                price = 10.10,
                estimatedTime = "10-25 min Estimación"
            )
        )
    }
}

