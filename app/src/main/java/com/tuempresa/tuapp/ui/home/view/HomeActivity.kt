package com.tuempresa.tuapp.ui.home.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.usecase.GetProductsUseCase
import com.tuempresa.tuapp.ui.cupones.view.CuponesActivity
import com.tuempresa.tuapp.ui.home.adapter.ProductAdapter
import com.tuempresa.tuapp.ui.home.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var btnDelivery: MaterialButton
    private lateinit var btnCupones: MaterialButton
    private lateinit var rvProducts: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: HomeViewModel

    // Category buttons
    private lateinit var categoryNieves: ImageView
    private lateinit var categoryPLeche: ImageView
    private lateinit var categoryPAgua: ImageView
    private lateinit var categoryAguas: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeViews()
        setupViewModel()
        setupListeners()
        observeProducts()
    }

    private fun initializeViews() {
        btnDelivery = findViewById(R.id.btn_delivery)
        btnCupones = findViewById(R.id.btn_cupones)
        rvProducts = findViewById(R.id.rv_products)

        categoryNieves = findViewById(R.id.category_nieves)
        categoryPLeche = findViewById(R.id.category_p_leche)
        categoryPAgua = findViewById(R.id.category_p_agua)
        categoryAguas = findViewById(R.id.category_aguas)

        // Configurar RecyclerView de productos con LinearLayoutManager
        rvProducts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        productAdapter = ProductAdapter(emptyList())
        rvProducts.adapter = productAdapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(GetProductsUseCase()) as T
            }
        }).get(HomeViewModel::class.java)
    }

    private fun setupListeners() {
        btnDelivery.setOnClickListener {
            btnDelivery.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.BLACK
            )
            btnCupones.backgroundTintList = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.dgary_green, null)
            )
        }

        btnCupones.setOnClickListener {
            btnCupones.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.BLACK
            )
            btnDelivery.backgroundTintList = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.dgary_green, null)
            )
            // Navegar a la pantalla de cupones
            startActivity(Intent(this, CuponesActivity::class.java))
        }

        // ...existing code...
        categoryNieves.setOnClickListener {
            viewModel.filterByCategory("Nieves")
        }

        categoryPLeche.setOnClickListener {
            viewModel.filterByCategory("P. Leche")
        }

        categoryPAgua.setOnClickListener {
            viewModel.filterByCategory("P. Agua")
        }

        categoryAguas.setOnClickListener {
            viewModel.filterByCategory("Aguas")
        }
    }

    private fun observeProducts() {
        viewModel.products.observe(this) { products ->
            productAdapter = ProductAdapter(products)
            rvProducts.adapter = productAdapter
        }
    }
}



