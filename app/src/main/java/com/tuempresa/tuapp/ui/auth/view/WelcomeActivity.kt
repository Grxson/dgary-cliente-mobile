package com.tuempresa.tuapp.ui.auth.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R

class WelcomeActivity : AppCompatActivity() {

    private lateinit var btnSignIn: MaterialButton
    private lateinit var btnSignUp: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Inicializar vistas
        initializeViews()

        // Configurar listeners
        setupListeners()
    }

    private fun initializeViews() {
        btnSignIn = findViewById(R.id.btn_sign_in)
        btnSignUp = findViewById(R.id.btn_sign_up)
    }

    private fun setupListeners() {
        btnSignIn.setOnClickListener {
            navigateToLogin()
        }

        btnSignUp.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}

