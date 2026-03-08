package com.tuempresa.tuapp.ui.auth.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import android.widget.ProgressBar
import android.widget.TextView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.AuthResult
import com.tuempresa.tuapp.domain.usecase.RegisterUseCase
import com.tuempresa.tuapp.ui.auth.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        initializeViews()

        // Inicializar ViewModel
        viewModel = RegisterViewModel(RegisterUseCase())

        // Configurar listeners
        setupListeners()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnRegister = findViewById(R.id.btn_register)
        pbLoading = findViewById(R.id.pb_loading)
        tvError = findViewById(R.id.tv_error)
        tvLoginLink = findViewById(R.id.tv_login_link)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (validateInputs(name, email, password, confirmPassword)) {
                lifecycleScope.launch {
                    viewModel.register(email, password, name)
                    observeAuthResult()
                }
            }
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
        password: String,
        confirmPassword: String
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

        if (password.isEmpty()) {
            showError(getString(R.string.register_empty_password))
            isValid = false
        } else if (password.length < 4) {
            showError(getString(R.string.register_short_password))
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            showError(getString(R.string.register_empty_confirm_password))
            isValid = false
        } else if (password != confirmPassword) {
            showError(getString(R.string.register_password_mismatch))
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
        tvError.setTextColor(android.graphics.Color.RED)
        tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }

    private fun showSuccess() {
        tvError.text = getString(R.string.register_success)
        tvError.setTextColor(android.graphics.Color.GREEN)
        tvError.visibility = View.VISIBLE
        // Esperar un segundo y luego navegar a login
        tvError.postDelayed({ navigateToLogin() }, 1000)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
