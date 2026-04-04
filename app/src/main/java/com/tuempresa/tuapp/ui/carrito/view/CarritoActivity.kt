package com.tuempresa.tuapp.ui.carrito.view

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.ui.home.view.HomeActivity

class CarritoActivity : AppCompatActivity() {

    private lateinit var btnIniciarCompra: MaterialButton
    private lateinit var btnInicio: LinearLayout
    private lateinit var btnCarrito: LinearLayout
    private lateinit var btnMiCuenta: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        btnIniciarCompra = findViewById(R.id.btn_iniciar_compra)
        btnInicio = findViewById(R.id.nav_inicio)
        btnCarrito = findViewById(R.id.nav_carrito)
        btnMiCuenta = findViewById(R.id.nav_mi_cuenta)
    }

    private fun setupListeners() {
        btnIniciarCompra.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnInicio.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnCarrito.setOnClickListener {
            // Ya estamos en Carrito
        }

        btnMiCuenta.setOnClickListener {
            // Navegar a Mi Cuenta (crear después)
        }
    }
}


