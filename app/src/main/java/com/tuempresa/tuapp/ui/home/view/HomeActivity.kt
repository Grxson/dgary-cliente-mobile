package com.tuempresa.tuapp.ui.home.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.usecase.GetProductsUseCase
import com.tuempresa.tuapp.ui.account.view.AccountActivity
import com.tuempresa.tuapp.ui.carrito.view.CarritoActivity
import com.tuempresa.tuapp.ui.cupones.view.CuponesActivity
import com.tuempresa.tuapp.ui.home.adapter.ProductAdapter
import com.tuempresa.tuapp.ui.home.viewmodel.HomeViewModel

// ViewModelFactory para crear instancias de HomeViewModel
class HomeViewModelFactory(private val getProductsUseCase: GetProductsUseCase) :
    ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return modelClass.cast(HomeViewModel(getProductsUseCase))
                ?: throw IllegalStateException("No se pudo crear HomeViewModel")
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

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

    // Navigation buttons
    private lateinit var navInicio: LinearLayout
    private lateinit var navCarrito: LinearLayout
    private lateinit var navMiCuenta: LinearLayout

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

        navInicio = findViewById(R.id.nav_inicio)
        navCarrito = findViewById(R.id.nav_carrito)
        navMiCuenta = findViewById(R.id.nav_mi_cuenta)

        // Configurar RecyclerView de productos con LinearLayoutManager
        rvProducts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        productAdapter = ProductAdapter(emptyList())
        rvProducts.adapter = productAdapter
    }

     private fun setupViewModel() {
         val factory = HomeViewModelFactory(GetProductsUseCase(applicationContext))
         viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
     }

    private fun setupListeners() {
        btnDelivery.setOnClickListener {
            btnDelivery.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.BLACK
            )
            btnCupones.backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.dgary_green)
            )
        }

        btnCupones.setOnClickListener {
            btnCupones.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.BLACK
            )
            btnDelivery.backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.dgary_green)
            )
            // Navegar a la pantalla de cupones
            startActivity(Intent(this, CuponesActivity::class.java))
        }

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

        btnDelivery.setOnLongClickListener {
            // Atajo para volver a cargar todo el catálogo desde API.
            viewModel.loadAllProducts()
            true
        }

        // Navigation listeners
        navInicio.setOnClickListener {
            // Ya estamos en Inicio
        }

        navCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }

        navMiCuenta.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    private fun observeProducts() {
        viewModel.products.observe(this) { products ->
            productAdapter = ProductAdapter(products)
            rvProducts.adapter = productAdapter
        }
    }
}



