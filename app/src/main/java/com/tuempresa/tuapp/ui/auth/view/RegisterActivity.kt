package com.tuempresa.tuapp.ui.auth.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.AuthResult
import com.tuempresa.tuapp.domain.usecase.RegisterUseCase
import com.tuempresa.tuapp.ui.auth.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvLoginLink: TextView
    private lateinit var countrySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        initializeViews()

        // Inicializar ViewModel
        viewModel = RegisterViewModel(RegisterUseCase(applicationContext))

        // Configurar listeners
        setupListeners()

        // Observar resultados una sola vez
        observeAuthResult()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone)
        etPassword = findViewById(R.id.et_password)
        btnRegister = findViewById(R.id.btn_register)
        btnBack = findViewById(R.id.btn_back)
        pbLoading = findViewById(R.id.pb_loading)
        tvError = findViewById(R.id.tv_error)
        tvLoginLink = findViewById(R.id.tv_login_link)
        countrySpinner = findViewById(R.id.country_spinner)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            Log.d("RegisterActivity", "🎯 BOTÓN REGISTRO CLICKEADO")
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString()

            Log.d("RegisterActivity", "📝 Validando: name=$name, email=$email, phone=$phone")
            
            if (validateInputs(name, email, phone, password)) {
                Log.d("RegisterActivity", "✅ Validación OK - Iniciando coroutine...")
                lifecycleScope.launch {
                    Log.d("RegisterActivity", "🚀 Coroutine iniciada - llamando a viewModel.register()")
                    try {
                        viewModel.register(name, email, phone, password)
                        Log.d("RegisterActivity", "✅ register() completado sin excepciones")
                    } catch (e: Exception) {
                        Log.e("RegisterActivity", "❌ EXCEPCIÓN en launch: ${e.message}", e)
                    }
                }
            } else {
                Log.d("RegisterActivity", "❌ Validación fallida")
            }
        }

        btnBack.setOnClickListener {
            finish()
        }

        tvLoginLink.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun observeAuthResult() {
        viewModel.authResult.observe(this) { result ->
            when (result) {
                is AuthResult.Loading -> {
                    showLoading(true)
                    hideError()
                }
                is AuthResult.Success -> {
                    showLoading(false)
                    showSuccess()
                }
                is AuthResult.Error -> {
                    showLoading(false)
                    showError(result.message)
                }
            }
        }
    }

    private fun validateInputs(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            showError(getString(R.string.register_empty_name))
            isValid = false
        }

        if (email.isEmpty()) {
            showError(getString(R.string.register_empty_email))
            isValid = false
        } else if (!isValidEmail(email)) {
            showError(getString(R.string.register_invalid_email))
            isValid = false
        }

        if (phone.isEmpty()) {
            showError(getString(R.string.register_empty_phone))
            isValid = false
        } else if (!phone.matches(Regex("^[0-9]{10}$"))) {
            showError("El teléfono debe tener 10 dígitos (sin +52 ni espacios)")
            isValid = false
        }

        if (password.isEmpty()) {
            showError(getString(R.string.register_empty_password))
            isValid = false
        } else if (password.length < 8) {
            showError(getString(R.string.register_short_password))
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showLoading(show: Boolean) {
        pbLoading.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !show
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.setTextColor(getColor(R.color.dgary_red))
        tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }

    private fun showSuccess() {
        tvError.text = getString(R.string.register_success)
        tvError.setTextColor(getColor(R.color.dgary_green))
        tvError.visibility = View.VISIBLE
        // Esperar un segundo y luego navegar a login
        tvError.postDelayed({ navigateToLogin() }, 1000)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
