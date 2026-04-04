package com.tuempresa.tuapp.ui.cupones.view

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.ui.cupones.adapter.CuponesAdapter
import com.tuempresa.tuapp.ui.cupones.model.Cupon

class CuponesActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var rvCupones: RecyclerView
    private lateinit var cuponesAdapter: CuponesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cupones)

        initializeViews()
        setupListeners()
        loadCupones()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_cupones)
        rvCupones = findViewById(R.id.rv_cupones)

        rvCupones.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cuponesAdapter = CuponesAdapter(emptyList())
        rvCupones.adapter = cuponesAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadCupones() {
        // Crear datos de ejemplo de cupones
        val cupones = listOf(
            Cupon(
                id = 1,
                titulo = "MXN\$25 Descuento",
                descripcion = "Descripción del Cupón",
                descuento = "\$25",
                botonTexto = "Comprar Ahora"
            ),
            Cupon(
                id = 2,
                titulo = "MXN\$50 Descuento",
                descripcion = "Cupón especial para compras mayores",
                descuento = "\$50",
                botonTexto = "Comprar Ahora"
            ),
            Cupon(
                id = 3,
                titulo = "MXN\$15 Descuento",
                descripcion = "Oferta limitada",
                descuento = "\$15",
                botonTexto = "Comprar Ahora"
            )
        )

        cuponesAdapter = CuponesAdapter(cupones)
        rvCupones.adapter = cuponesAdapter
    }
}

