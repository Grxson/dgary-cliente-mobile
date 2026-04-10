package com.tuempresa.tuapp.ui.account.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.repository.AuthRepository
import com.tuempresa.tuapp.domain.model.AuthResult
import kotlinx.coroutines.launch

class EditAccountActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var btnGuardarCambios: MaterialButton

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        authRepository = AuthRepository(this)

        initializeViews()
        setupListeners()
        loadUserData()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_edit_account)
        etNombre = findViewById(R.id.et_nombre)
        etEmail = findViewById(R.id.et_email)
        etTelefono = findViewById(R.id.et_telefono)
        etDireccion = findViewById(R.id.et_direccion)
        btnGuardarCambios = findViewById(R.id.btn_guardar_cambios)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        btnGuardarCambios.setOnClickListener { guardarCambios() }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val profile = authRepository.getProfile()
            if (profile != null) {
                etNombre.setText(profile.name)
                etEmail.setText(profile.email)
                etTelefono.setText(profile.phone.orEmpty())
                etDireccion.setText(profile.address.orEmpty())
                etEmail.isEnabled = false
            } else {
                etNombre.setText(getString(R.string.account_user_test))
                etEmail.setText(getString(R.string.account_email_default))
                etTelefono.setText(getString(R.string.account_telefono_default))
            }
        }
    }

    private fun guardarCambios() {
        val nombre = etNombre.text?.toString().orEmpty().trim()
        val telefono = etTelefono.text?.toString().orEmpty().trim()
        val direccion = etDireccion.text?.toString().orEmpty().trim()

        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            btnGuardarCambios.isEnabled = false
            btnGuardarCambios.text = "Guardando..."

            when (val result = authRepository.updateProfile(nombre, telefono, direccion)) {
                is AuthResult.Success -> {
                    Toast.makeText(this@EditAccountActivity, result.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
                is AuthResult.Error -> {
                    Toast.makeText(this@EditAccountActivity, result.message, Toast.LENGTH_LONG).show()
                }
                AuthResult.Loading -> Unit
            }

            btnGuardarCambios.isEnabled = true
            btnGuardarCambios.text = getString(R.string.account_save_changes)
        }
    }
}

