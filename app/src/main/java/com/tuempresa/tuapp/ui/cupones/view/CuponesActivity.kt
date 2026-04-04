package com.tuempresa.tuapp.ui.cupones.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.ui.carrito.view.CarritoActivity
import com.tuempresa.tuapp.ui.cupones.adapter.CuponesAdapter
import com.tuempresa.tuapp.ui.cupones.model.Cupon
import com.tuempresa.tuapp.ui.home.view.HomeActivity

class CuponesActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var rvCupones: RecyclerView
    private lateinit var cuponesAdapter: CuponesAdapter
    private lateinit var navInicio: LinearLayout
    private lateinit var navCarrito: LinearLayout
    private lateinit var navMiCuenta: LinearLayout

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
        navInicio = findViewById(R.id.nav_inicio)
        navCarrito = findViewById(R.id.nav_carrito)
        navMiCuenta = findViewById(R.id.nav_mi_cuenta)

        rvCupones.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cuponesAdapter = CuponesAdapter(emptyList())
        rvCupones.adapter = cuponesAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        navInicio.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        navCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
            finish()
        }

        navMiCuenta.setOnClickListener {
            // Navegar a Mi Cuenta (crear después)
        }
    }

     private fun loadCupones() {
         // Crear datos de ejemplo de cupones
         val cupones = listOf(
             Cupon(
                 id = 1,
                 titulo = getString(R.string.cupon_25_descuento),
                 descripcion = getString(R.string.cupon_descripcion_default),
                 descuento = getString(R.string.cupon_25_monto),
                 botonTexto = getString(R.string.cupon_comprar_ahora)
             ),
             Cupon(
                 id = 2,
                 titulo = getString(R.string.cupon_50_descuento),
                 descripcion = getString(R.string.cupon_descripcion_especial),
                 descuento = getString(R.string.cupon_50_monto),
                 botonTexto = getString(R.string.cupon_comprar_ahora)
             ),
             Cupon(
                 id = 3,
                 titulo = getString(R.string.cupon_15_descuento),
                 descripcion = getString(R.string.cupon_descripcion_limitada),
                 descuento = getString(R.string.cupon_15_monto),
                 botonTexto = getString(R.string.cupon_comprar_ahora)
             )
         )

         cuponesAdapter = CuponesAdapter(cupones)
         rvCupones.adapter = cuponesAdapter
     }
}

