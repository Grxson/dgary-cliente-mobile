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
import com.tuempresa.tuapp.domain.usecase.LoginUseCase
import com.tuempresa.tuapp.ui.auth.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvRegisterLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        initializeViews()

        // Inicializar ViewModel
        viewModel = LoginViewModel(LoginUseCase())

        // Configurar listeners
        setupListeners()
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        pbLoading = findViewById(R.id.pb_loading)
        tvError = findViewById(R.id.tv_error)
        tvRegisterLink = findViewById(R.id.tv_register_link)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (validateInputs(email, password)) {
                lifecycleScope.launch {
                    viewModel.login(email, password)
                    observeAuthResult()
                }
            }
        }

        tvRegisterLink.setOnClickListener {
            navigateToRegister()
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

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            showError(getString(R.string.login_empty_email))
            isValid = false
        } else if (!isValidEmail(email)) {
            showError(getString(R.string.login_invalid_email))
            isValid = false
        }

        if (password.isEmpty()) {
            showError(getString(R.string.login_empty_password))
            isValid = false
        } else if (password.length < 4) {
            showError(getString(R.string.login_short_password))
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showLoading(show: Boolean) {
        pbLoading.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }

    private fun showSuccess() {
        tvError.text = getString(R.string.login_success)
        tvError.setTextColor(android.graphics.Color.GREEN)
        tvError.visibility = View.VISIBLE
        // Esperar un segundo y luego navegar
        tvError.postDelayed({ navigateToMain() }, 1000)
    }

    private fun navigateToMain() {
        // TODO: Implementar navegación a MainActivity
        // startActivity(Intent(this, MainActivity::class.java))
        // finish()
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}
