package com.tuempresa.tuapp.domain.usecase

import android.content.Context
import com.tuempresa.tuapp.data.repository.ProductRepository
import com.tuempresa.tuapp.domain.model.Product

class GetProductsUseCase(context: Context) {

    private val repository = ProductRepository(context)

    suspend fun execute(): List<Product> {
        return repository.getProducts()
    }
}

