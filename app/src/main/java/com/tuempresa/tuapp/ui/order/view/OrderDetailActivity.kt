package com.tuempresa.tuapp.ui.order.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.Order
import com.tuempresa.tuapp.domain.model.OrderItem
import com.tuempresa.tuapp.domain.model.Preparacion
import com.tuempresa.tuapp.ui.order.adapter.OrderItemsAdapter
import com.tuempresa.tuapp.ui.order.adapter.PreparacionAdapter
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

    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        initializeViews()
        setupListeners()
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

        // Configurar RecyclerViews
        rvOrdenes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvPreparaciones.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAnadirArticulos.setOnClickListener {
            // Navegar a agregar artículos
        }

        ivMetodoPago.setOnClickListener {
            // Mostrar opciones de métodos de pago
        }

        btnSeguir.setOnClickListener {
            // Proceder con la orden
            finish()
        }
    }

    private fun loadOrderData() {
        // Datos de prueba
        val preparaciones = listOf(
            Preparacion("1", "Preparación 1"),
            Preparacion("2", "Preparación 2"),
            Preparacion("3", "Preparación 3")
        )

        val items = listOf(
            OrderItem(
                id = "1",
                nombre = "Helao",
                cantidad = 1,
                precio = 13.18,
                preparaciones = preparaciones
            )
        )

        order = Order(
            id = "123",
            ubicacion = "GG",
            tiempoEntrega = "15-30 min(s)",
            items = items,
            subtotal = 19.99,
            descuento = 19.99,
            total = 10.71,
            cuponAplicado = "Estás ahorrando \$X pesos",
            ahorro = 19.99
        )

        displayOrderData()
    }

    private fun displayOrderData() {
        order?.let { order ->
            // Mostrar ubicación y tiempo
            tvUbicacion.text = order.ubicacion
            tvTiempoEntrega.text = order.tiempoEntrega

            // Mostrar items
            val itemsAdapter = OrderItemsAdapter(order.items)
            rvOrdenes.adapter = itemsAdapter

            // Mostrar preparaciones
            val allPreparaciones = order.items.flatMap { it.preparaciones }
            val preparacionAdapter = PreparacionAdapter(allPreparaciones)
            rvPreparaciones.adapter = preparacionAdapter

            // Mostrar resumen
            if (order.cuponAplicado != null) {
                tvCupon.text = order.cuponAplicado
                tvAhorro.text = String.format(Locale.US, "-$%.2f", order.ahorro)
            }

            tvSubtotal.text = String.format(Locale.US, "$%.2f", order.subtotal)
            tvDescuento.text = String.format(Locale.US, "-$%.2f", order.descuento)
            tvTotal.text = String.format(Locale.US, "$%.2f", order.total)
            btnSeguir.text = getString(R.string.order_seguir, String.format(Locale.US, "%.2f", order.total))
        }
    }
}

