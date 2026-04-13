package com.tuempresa.tuapp.ui.carrito.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.repository.OrderRepository
import com.tuempresa.tuapp.ui.carrito.adapter.CartAdapter
import com.tuempresa.tuapp.ui.carrito.viewmodel.CartViewModel
import com.tuempresa.tuapp.ui.home.view.HomeActivity
import com.tuempresa.tuapp.ui.account.view.AccountActivity
import com.tuempresa.tuapp.ui.account.view.PaymentMethodActivity
import com.tuempresa.tuapp.ui.order.view.LocationSelectionActivity
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CarritoActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvEmptyCart: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDiscount: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: MaterialButton
    private lateinit var btnInicio: LinearLayout
    private lateinit var btnCarrito: LinearLayout
    private lateinit var btnMiCuenta: LinearLayout

    private lateinit var cartAdapter: CartAdapter
    private var lastShownError: String? = null

    private val locationSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            val data = result.data ?: return@registerForActivityResult
            val address = data.getStringExtra(LocationSelectionActivity.EXTRA_SELECTED_ADDRESS)
            val lat = data.getDoubleExtra(LocationSelectionActivity.EXTRA_SELECTED_LAT, Double.NaN)
            val lng = data.getDoubleExtra(LocationSelectionActivity.EXTRA_SELECTED_LNG, Double.NaN)

            if (address.isNullOrBlank() || lat.isNaN() || lng.isNaN()) {
                Toast.makeText(this, "Debes confirmar una ubicación válida", Toast.LENGTH_LONG).show()
                return@registerForActivityResult
            }

            proceedCheckout(address, lat, lng)
        }

    private val viewModel: CartViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CartViewModel(OrderRepository(this@CarritoActivity)) as T
                }
            }
        )[CartViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        initializeViews()
        setupListeners()
        observeState()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_cart)
        rvCartItems = findViewById(R.id.rv_cart_items)
        tvEmptyCart = findViewById(R.id.tv_empty_cart)
        tvSubtotal = findViewById(R.id.tv_cart_subtotal)
        tvDiscount = findViewById(R.id.tv_cart_discount)
        tvTotal = findViewById(R.id.tv_cart_total)
        btnCheckout = findViewById(R.id.btn_checkout)
        btnInicio = findViewById(R.id.nav_inicio)
        btnCarrito = findViewById(R.id.nav_carrito)
        btnMiCuenta = findViewById(R.id.nav_mi_cuenta)

        rvCartItems.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(emptyList(), { item ->
            viewModel.increaseQuantity(item.productId)
        }, { item ->
            viewModel.decreaseQuantity(item.productId)
        }, { item ->
            viewModel.removeItem(item.productId)
        })
        rvCartItems.adapter = cartAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnCheckout.setOnClickListener {
            locationSelectionLauncher.launch(Intent(this, LocationSelectionActivity::class.java))
        }

        btnInicio.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnCarrito.setOnClickListener {
            // Ya estamos en Carrito
        }

        btnMiCuenta.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    private fun proceedCheckout(address: String, lat: Double, lng: Double) {
        viewModel.checkout(
            address = address,
            destinationLat = lat,
            destinationLng = lng
        ) { orderId ->
            val intent = Intent(this, PaymentMethodActivity::class.java)
            intent.putExtra("order_id", orderId)
            startActivity(intent)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                cartAdapter.updateItems(state.items)
                tvEmptyCart.visibility = if (state.items.isEmpty()) View.VISIBLE else View.GONE
                rvCartItems.visibility = if (state.items.isEmpty()) View.GONE else View.VISIBLE

                val subtotal = state.preview?.subtotal ?: state.items.sumOf { it.price * it.quantity }
                val discount = state.preview?.discountAmount ?: 0.0
                val total = state.preview?.total ?: (subtotal - discount)

                tvSubtotal.text = getString(R.string.cart_subtotal_value, toMxn(subtotal))
                tvDiscount.text = getString(R.string.cart_discount_value, toMxn(discount))
                tvTotal.text = getString(R.string.cart_total_value, toMxn(total))

                btnCheckout.isEnabled = !state.isSubmitting && state.items.isNotEmpty()
                btnCheckout.text = if (state.isSubmitting) getString(R.string.cart_processing) else getString(R.string.cart_checkout)

                if (!state.error.isNullOrBlank() && state.error != lastShownError) {
                    lastShownError = state.error
                    Toast.makeText(this@CarritoActivity, state.error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun toMxn(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        formatter.currency = java.util.Currency.getInstance("MXN")
        return formatter.format(value)
    }
}


