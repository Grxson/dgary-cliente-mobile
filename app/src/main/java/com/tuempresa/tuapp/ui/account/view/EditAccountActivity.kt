package com.tuempresa.tuapp.ui.account.view

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tuempresa.tuapp.R

class EditAccountActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var btnGuardarCambios: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        initializeViews()
        setupListeners()
        loadUserData()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_edit_account)
        etNombre = findViewById(R.id.et_nombre)
        etEmail = findViewById(R.id.et_email)
        etTelefono = findViewById(R.id.et_telefono)
        btnGuardarCambios = findViewById(R.id.btn_guardar_cambios)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }

    private fun loadUserData() {
        // Cargar datos de prueba
        etNombre.setText(getString(R.string.account_user_test))
        etEmail.setText(getString(R.string.account_email_default))
        etTelefono.setText(getString(R.string.account_telefono_default))
    }

    private fun guardarCambios() {
        val nombre = etNombre.text.toString()
        val email = etEmail.text.toString()
        val telefono = etTelefono.text.toString()

        // Validaciones básicas
        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            // Mostrar error
            return
        }

        // Aquí iría la lógica para guardar los cambios (llamar a API, guardar en BD, etc.)
        finish()
    }
}

