package com.tuempresa.tuapp.ui.order.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.repository.OrderRepository
import com.tuempresa.tuapp.data.repository.DeliveryRepository
import com.tuempresa.tuapp.data.remote.dto.OrderDetailFullDto
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

class OrderTrackingActivity : AppCompatActivity() {

    private lateinit var btnClose: ImageView
    private lateinit var tvEstado: TextView
    private lateinit var tvHoraLlegada: TextView
    private lateinit var pbProgreso: ProgressBar
    private lateinit var tvHoraEntrega: TextView
    private lateinit var ivCooking: ImageView
    private lateinit var tvDireccion: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvItemsTracking: TextView

    private val orderRepository by lazy { OrderRepository(this) }
    private val deliveryRepository by lazy { DeliveryRepository(this) }
    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_tracking)

        initializeViews()
        setupListeners()
        loadTrackingData()
    }

    private fun initializeViews() {
        btnClose = findViewById(R.id.btn_close_tracking)
        tvEstado = findViewById(R.id.tv_estado)
        tvHoraLlegada = findViewById(R.id.tv_hora_llegada)
        pbProgreso = findViewById(R.id.pb_progreso)
        tvHoraEntrega = findViewById(R.id.tv_hora_entrega)
        ivCooking = findViewById(R.id.iv_cooking)
        tvDireccion = findViewById(R.id.tv_direccion_tracking)
        tvTotal = findViewById(R.id.tv_total_tracking)
        tvItemsTracking = findViewById(R.id.tv_items_tracking)
    }

    private fun setupListeners() {
        btnClose.setOnClickListener {
            finish()
        }
    }

    private fun loadTrackingData() {
        orderId = intent.getStringExtra("order_id")
            ?: intent.getIntExtra("order_id", 0).takeIf { it > 0 }?.toString()
            ?: ""

        if (orderId.isBlank()) {
            showErrorState(getString(R.string.order_tracking_error))
            return
        }

        lifecycleScope.launch {
            showLoadingState()

            val order = orderRepository.getOrder(orderId)
            if (order != null) {
                displayTrackingData(order)

                val delivery = deliveryRepository.getOrderDelivery(orderId)
                if (delivery != null) {
                    tvDireccion.text = delivery.address ?: tvDireccion.text
                    if (!delivery.status.isNullOrBlank()) {
                        tvEstado.text = "${tvEstado.text} · ${delivery.status}"
                    }
                }
            } else {
                showErrorState(getString(R.string.order_tracking_error))
            }
        }
    }

    private fun showLoadingState() {
        tvEstado.text = getString(R.string.order_tracking_loading)
        tvHoraLlegada.text = getString(R.string.order_tracking_loading)
        tvHoraEntrega.text = getString(R.string.order_tracking_loading)
        pbProgreso.isIndeterminate = true
        pbProgreso.visibility = View.VISIBLE
    }

    private fun showErrorState(message: String) {
        pbProgreso.isIndeterminate = false
        pbProgreso.visibility = View.GONE
        tvEstado.text = message
        tvHoraLlegada.text = getString(R.string.order_tracking_error)
        tvHoraEntrega.text = getString(R.string.order_tracking_error)
        tvDireccion.text = getString(R.string.order_location_value)
        tvItemsTracking.text = getString(R.string.order_tracking_items_sample)
        tvTotal.text = toMxn(0.0)
    }

    private fun displayTrackingData(order: OrderDetailFullDto) {
        pbProgreso.isIndeterminate = false
        pbProgreso.visibility = View.VISIBLE

        val statusKey = order.status?.lowercase().orEmpty()
        val statusLabel = when (statusKey) {
            "pending", "preparing" -> getString(R.string.order_preparando)
            "ready" -> "Orden lista"
            "completed", "delivered" -> "Pedido entregado"
            "canceled", "cancelled" -> "Pedido cancelado"
            else -> getString(R.string.order_preparando)
        }
        val etaText = when (statusKey) {
            "pending", "preparing" -> "15-30 min"
            "ready" -> "5-10 min"
            "completed", "delivered" -> "Entregada"
            "canceled", "cancelled" -> "Cancelada"
            else -> "15-30 min"
        }

        tvEstado.text = if (orderId.isNotBlank()) {
            "$statusLabel · ${getString(R.string.order_tracking_order_number, orderId)}"
        } else {
            statusLabel
        }
        tvHoraLlegada.text = getString(R.string.order_llegando, etaText)
        tvHoraEntrega.text = getString(R.string.order_llegada_a_las, etaText)
        pbProgreso.progress = when (statusKey) {
            "pending", "preparing" -> 35
            "ready" -> 70
            "completed", "delivered" -> 100
            "canceled", "cancelled" -> 0
            else -> 35
        }

        tvDireccion.text = order.address?.address ?: getString(R.string.order_location_value)

        val itemsText = if (order.details.isNotEmpty()) {
            order.details.joinToString("\n") { detail ->
                val productName = detail.product?.name ?: "Producto"
                "${detail.quantity} x $productName"
            }
        } else {
            getString(R.string.order_tracking_items_sample)
        }
        tvItemsTracking.text = itemsText

        tvTotal.text = toMxn(order.total)
    }
}
    private fun toMxn(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        formatter.currency = java.util.Currency.getInstance("MXN")
        return formatter.format(value)
    }

