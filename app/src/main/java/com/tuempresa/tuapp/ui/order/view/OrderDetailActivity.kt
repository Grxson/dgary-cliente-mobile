package com.tuempresa.tuapp.ui.order.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.remote.dto.OrderDetailFullDto
import com.tuempresa.tuapp.data.repository.OrderRepository
import com.tuempresa.tuapp.domain.model.Order
import com.tuempresa.tuapp.domain.model.OrderItem
import com.tuempresa.tuapp.domain.model.Preparacion
import com.tuempresa.tuapp.ui.account.view.PaymentMethodActivity
import com.tuempresa.tuapp.ui.order.adapter.OrderItemsAdapter
import com.tuempresa.tuapp.ui.order.adapter.PreparacionAdapter
import com.tuempresa.tuapp.ui.order.viewmodel.OrderDetailViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvUbicacion: TextView
    private lateinit var tvTiempoEntrega: TextView
    private lateinit var rvOrdenes: RecyclerView
    private lateinit var rvPreparaciones: RecyclerView
    private lateinit var btnAnadirArticulos: LinearLayout
    private lateinit var tvCupon: TextView
    private lateinit var tvAhorro: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDescuento: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnSeguir: MaterialButton
    private lateinit var ivMetodoPago: ImageView
    private lateinit var btnUbicacion: LinearLayout
    private lateinit var btnMetodoPago: LinearLayout
    private lateinit var progressBar: View

    private val viewModel: OrderDetailViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return OrderDetailViewModel(OrderRepository(this@OrderDetailActivity)) as T
                }
            }
        )[OrderDetailViewModel::class.java]
    }

    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        initializeViews()
        setupListeners()
        observeState()
        loadOrderData()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_order_detail)
        tvUbicacion = findViewById(R.id.tv_ubicacion)
        tvTiempoEntrega = findViewById(R.id.tv_tiempo_entrega)
        rvOrdenes = findViewById(R.id.rv_order_items)
        rvPreparaciones = findViewById(R.id.rv_preparaciones)
        btnAnadirArticulos = findViewById(R.id.btn_anadir_articulos)
        tvCupon = findViewById(R.id.tv_cupon)
        tvAhorro = findViewById(R.id.tv_ahorro)
        tvSubtotal = findViewById(R.id.tv_subtotal)
        tvDescuento = findViewById(R.id.tv_descuento)
        tvTotal = findViewById(R.id.tv_total)
        btnSeguir = findViewById(R.id.btn_seguir)
        ivMetodoPago = findViewById(R.id.iv_metodo_pago)
        btnUbicacion = findViewById(R.id.btn_ubicacion)
        btnMetodoPago = findViewById(R.id.btn_metodo_pago)
        progressBar = findViewById(R.id.progress_bar)

        rvOrdenes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvPreparaciones.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnAnadirArticulos.setOnClickListener { finish() }

        btnUbicacion.setOnClickListener {
            startActivity(Intent(this, LocationSelectionActivity::class.java))
        }

        btnMetodoPago.setOnClickListener {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            intent.putExtra("order_id", orderId)
            startActivity(intent)
        }

        ivMetodoPago.setOnClickListener {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            intent.putExtra("order_id", orderId)
            startActivity(intent)
        }

        tvUbicacion.setOnClickListener {
            startActivity(Intent(this, LocationSelectionActivity::class.java))
        }

        btnSeguir.setOnClickListener {
            if (orderId.isNotBlank()) {
                startActivity(
                    Intent(this, OrderTrackingActivity::class.java)
                        .putExtra("order_id", orderId)
                )
            } else {
                finish()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                btnSeguir.isEnabled = !state.isReordering && !state.isCancelling

                state.order?.let { renderOrder(it) }

                state.error?.let {
                    tvTiempoEntrega.text = it
                    viewModel.clearError()
                }
            }
        }
    }

    private fun loadOrderData() {
        orderId = intent.getStringExtra("order_id")
            ?: intent.getIntExtra("order_id", 0).takeIf { it > 0 }?.toString()
            ?: ""

        if (orderId.isBlank()) {
            tvTiempoEntrega.text = getString(R.string.order_delivery_time_value)
            return
        }

        viewModel.loadOrder(orderId)
    }

    private fun renderOrder(order: OrderDetailFullDto) {
        val mappedItems = order.details.mapIndexed { index, detail ->
            OrderItem(
                id = detail.id.toString(),
                nombre = detail.product?.name ?: "Producto ${index + 1}",
                cantidad = detail.quantity,
                precio = detail.unitPrice,
                preparaciones = emptyList()
            )
        }

        val domainOrder = Order(
            id = order.id.toString(),
            ubicacion = order.address?.address ?: getString(R.string.order_location_value),
            tiempoEntrega = order.status ?: getString(R.string.order_delivery_time_value),
            items = mappedItems,
            subtotal = order.subtotal,
            descuento = order.discount,
            total = order.total,
            cuponAplicado = if (order.discount > 0) getString(R.string.order_cupon) else null,
            ahorro = order.discount
        )

        tvUbicacion.text = domainOrder.ubicacion
        tvTiempoEntrega.text = domainOrder.tiempoEntrega

        rvOrdenes.adapter = OrderItemsAdapter(domainOrder.items)
        rvPreparaciones.adapter = PreparacionAdapter(domainOrder.items.flatMap { it.preparaciones })

        if (domainOrder.cuponAplicado != null) {
            tvCupon.text = domainOrder.cuponAplicado
            tvCupon.visibility = View.VISIBLE
            tvAhorro.text = getString(
                R.string.order_coupon_saving,
                String.format(Locale.US, "%.2f", domainOrder.ahorro)
            )
            tvAhorro.visibility = View.VISIBLE
        } else {
            tvCupon.visibility = View.GONE
            tvAhorro.visibility = View.GONE
        }

        tvSubtotal.text = String.format(Locale.US, "$%.2f", domainOrder.subtotal)
        tvDescuento.text = String.format(Locale.US, "-$%.2f", domainOrder.descuento)
        tvTotal.text = String.format(Locale.US, "$%.2f", domainOrder.total)
        btnSeguir.text = getString(R.string.order_tracking_open)
    }
}

