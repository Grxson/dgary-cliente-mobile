package com.tuempresa.tuapp.ui.home.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.local.CartStore
import com.tuempresa.tuapp.data.repository.ProductRepository
import com.tuempresa.tuapp.domain.usecase.GetProductsUseCase
import com.tuempresa.tuapp.ui.account.view.AccountActivity
import com.tuempresa.tuapp.ui.carrito.view.CarritoActivity
import com.tuempresa.tuapp.ui.cupones.view.CuponesActivity
import com.tuempresa.tuapp.ui.home.adapter.ProductAdapter
import com.tuempresa.tuapp.ui.product.view.ProductDetailActivity
import com.tuempresa.tuapp.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

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
    private lateinit var productRepository: ProductRepository

    private var categoryNievesName: String = "Nieves"
    private var categoryLecheName: String = "Productos de Leche"
    private var categoryAguaName: String = "Productos de Agua"
    private var categoryAguasName: String = "Bebidas"

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

        productRepository = ProductRepository(this)

        initializeViews()
        setupViewModel()
        setupListeners()
        observeProducts()
        loadCategoriesFromApi()
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
        productAdapter = ProductAdapter(
            products = emptyList(),
            onAddToCart = { product ->
                CartStore.add(product)
                startActivity(Intent(this, CarritoActivity::class.java))
            },
            onProductClick = { product ->
                startActivity(
                    Intent(this, ProductDetailActivity::class.java)
                        .putExtra("product_id", product.id)
                )
            }
        )
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
            viewModel.filterByCategory(categoryNievesName)
        }

        categoryPLeche.setOnClickListener {
            viewModel.filterByCategory(categoryLecheName)
        }

        categoryPAgua.setOnClickListener {
            viewModel.filterByCategory(categoryAguaName)
        }

        categoryAguas.setOnClickListener {
            viewModel.filterByCategory(categoryAguasName)
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
            productAdapter = ProductAdapter(
                products = products,
                onAddToCart = { product ->
                    CartStore.add(product)
                    startActivity(Intent(this, CarritoActivity::class.java))
                },
                onProductClick = { product ->
                    startActivity(
                        Intent(this, ProductDetailActivity::class.java)
                            .putExtra("product_id", product.id)
                    )
                }
            )
            rvProducts.adapter = productAdapter
        }
    }

    private fun loadCategoriesFromApi() {
        lifecycleScope.launch {
            val categories = productRepository.getCategories()
            if (categories.isEmpty()) {
                return@launch
            }

            categoryNievesName = categories.firstOrNull { it.name.contains("nieve", ignoreCase = true) }?.name
                ?: categoryNievesName
            categoryLecheName = categories.firstOrNull { it.name.contains("leche", ignoreCase = true) }?.name
                ?: categoryLecheName
            categoryAguaName = categories.firstOrNull {
                it.name.contains("agua", ignoreCase = true) && !it.name.contains("bebida", ignoreCase = true)
            }?.name ?: categoryAguaName
            categoryAguasName = categories.firstOrNull {
                it.name.contains("bebida", ignoreCase = true) || it.name.contains("aguas", ignoreCase = true)
            }?.name ?: categoryAguasName
        }
    }
}



