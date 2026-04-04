package com.tuempresa.tuapp.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuempresa.tuapp.domain.model.Product
import com.tuempresa.tuapp.domain.usecase.GetProductsUseCase

class HomeViewModel(private val getProductsUseCase: GetProductsUseCase) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    init {
        loadProducts()
    }

    private fun loadProducts() {
        val productList = getProductsUseCase.execute()
        _products.value = productList
    }

    fun filterByCategory(category: String) {
        val allProducts = getProductsUseCase.execute()
        _products.value = allProducts.filter { it.category == category }
    }

    fun loadAllProducts() {
        _products.value = getProductsUseCase.execute()
    }
}

