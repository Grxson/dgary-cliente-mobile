package com.tuempresa.tuapp.ui.account.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.SavedAddress
import com.tuempresa.tuapp.ui.account.adapter.SavedAddressAdapter
import com.tuempresa.tuapp.ui.auth.view.LoginActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvEditCuenta: LinearLayout
    private lateinit var rvSavedAddresses: RecyclerView
    private lateinit var btnCerrarSesion: LinearLayout
    private lateinit var addressAdapter: SavedAddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeViews()
        setupListeners()
        loadUserData()
        loadSavedAddresses()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_settings)
        tvUserName = findViewById(R.id.tv_settings_user_name)
        tvEditCuenta = findViewById(R.id.btn_edit_cuenta)
        rvSavedAddresses = findViewById(R.id.rv_saved_addresses)
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion)

        rvSavedAddresses.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        addressAdapter = SavedAddressAdapter(emptyList())
        rvSavedAddresses.adapter = addressAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        tvEditCuenta.setOnClickListener {
            startActivity(Intent(this, EditAccountActivity::class.java))
        }

        btnCerrarSesion.setOnClickListener {
            // Cerrar sesión
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity() // Termina todas las actividades
        }
    }

    private fun loadUserData() {
        // Datos de prueba
        tvUserName.text = getString(R.string.account_user_test)
    }

    private fun loadSavedAddresses() {
        // Datos de prueba
        val addresses = listOf(
            SavedAddress(
                id = "1",
                titulo = "Casa",
                direccion = "XXXXX",
                detalles = "",
                esPrincipal = true
            )
        )

        addressAdapter = SavedAddressAdapter(addresses)
        rvSavedAddresses.adapter = addressAdapter
    }
}

