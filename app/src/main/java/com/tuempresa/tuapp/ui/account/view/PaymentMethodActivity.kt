package com.tuempresa.tuapp.ui.account.view

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.stripe.android.PaymentConfiguration
import com.tuempresa.tuapp.R

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnGuardarTarjeta: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        // Clave de prueba temporal para sandbox; se reemplaza por la del proyecto Stripe.
        PaymentConfiguration.init(applicationContext, "pk_test_12345")

        btnBack = findViewById(R.id.btn_back_payment)
        btnGuardarTarjeta = findViewById(R.id.btn_guardar_tarjeta)

        btnBack.setOnClickListener { finish() }
        btnGuardarTarjeta.setOnClickListener { finish() }
    }
}

