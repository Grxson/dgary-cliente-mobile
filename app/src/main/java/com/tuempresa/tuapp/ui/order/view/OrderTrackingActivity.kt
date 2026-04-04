package com.tuempresa.tuapp.ui.order.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.OrderTracking
import java.util.Locale

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

    private var tracking: OrderTracking? = null

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
        tracking = OrderTracking(
            id = "123",
            estado = getString(R.string.order_preparando),
            horaLlegada = "10:15",
            horaEntrega = "10:40",
            direccion = "GG, GG",
            items = listOf("XXXXXXXXXX"),
            total = 13.18,
            progreso = 0.4f
        )

        displayTrackingData()
    }

    private fun displayTrackingData() {
        tracking?.let { tracking ->
            // Mostrar estado
            tvEstado.text = tracking.estado

            // Mostrar horas
            tvHoraLlegada.text = getString(R.string.order_llegando_a, tracking.horaLlegada)
            tvHoraEntrega.text = getString(R.string.order_llegada_a_las, tracking.horaEntrega)

            // Mostrar progreso
            pbProgreso.progress = (tracking.progreso * 100).toInt()

            // Mostrar dirección
            tvDireccion.text = tracking.direccion

             // Mostrar items
             val itemsText = tracking.items.joinToString("\n") { "1 $it" }
             tvItemsTracking.text = itemsText

             // Mostrar total
             tvTotal.text = String.format(Locale.US, "$%.2f", tracking.total)
         }
     }
}

