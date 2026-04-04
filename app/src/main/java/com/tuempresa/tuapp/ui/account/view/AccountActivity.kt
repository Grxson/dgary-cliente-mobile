package com.tuempresa.tuapp.ui.account.view

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.ui.carrito.view.CarritoActivity
import com.tuempresa.tuapp.ui.cupones.view.CuponesActivity
import com.tuempresa.tuapp.ui.home.view.HomeActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var btnOrdenes: LinearLayout
    private lateinit var btnBilletera: LinearLayout
    private lateinit var btnCupones: LinearLayout
    private lateinit var btnAjustes: LinearLayout
    private lateinit var navInicio: LinearLayout
    private lateinit var navCarrito: LinearLayout
    private lateinit var navMiCuenta: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        initializeViews()
        setupListeners()
        loadUserData()
    }

    private fun initializeViews() {
        tvUserName = findViewById(R.id.tv_user_name)
        btnOrdenes = findViewById(R.id.btn_ordenes)
        btnBilletera = findViewById(R.id.btn_billetera)
        btnCupones = findViewById(R.id.btn_cupones)
        btnAjustes = findViewById(R.id.btn_ajustes)
        navInicio = findViewById(R.id.nav_inicio)
        navCarrito = findViewById(R.id.nav_carrito)
        navMiCuenta = findViewById(R.id.nav_mi_cuenta)
    }

    private fun setupListeners() {
        btnOrdenes.setOnClickListener {
            // Navegar a pantalla de órdenes (crear después)
        }

        btnBilletera.setOnClickListener {
            // Navegar a pantalla de billetera (crear después)
        }

        btnCupones.setOnClickListener {
            startActivity(Intent(this, CuponesActivity::class.java))
        }

        btnAjustes.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
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
            // Ya estamos en Mi Cuenta
        }
    }

    private fun loadUserData() {
        // Por ahora datos de prueba, idealmente vendrían de un ViewModel o repositorio
        tvUserName.text = getString(R.string.account_user_test)
    }
}

