package com.tuempresa.tuapp.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.tuapp.domain.model.Product
import com.tuempresa.tuapp.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.launch

class HomeViewModel(private val getProductsUseCase: GetProductsUseCase) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private var allProducts: List<Product> = emptyList()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val productList = getProductsUseCase.execute()
            allProducts = productList
            _products.value = productList
        }
    }

    fun filterByCategory(category: String) {
        _products.value = allProducts.filter { it.category == category }
    }

    fun loadAllProducts() {
        _products.value = allProducts
    }
}

