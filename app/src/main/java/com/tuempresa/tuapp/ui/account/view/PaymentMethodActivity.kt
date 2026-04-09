package com.tuempresa.tuapp.ui.account.view

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.local.SessionManager
import com.tuempresa.tuapp.data.remote.ApiClient
import com.tuempresa.tuapp.data.remote.dto.ConfirmPaymentRequestDto
import com.tuempresa.tuapp.ui.account.view.adapter.CardAdapter
import kotlinx.coroutines.launch

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnAnadirTarjeta: MaterialButton
    private lateinit var rvSavedCards: RecyclerView
    private lateinit var tvNoCards: TextView
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var cardAdapter: CardAdapter
    
    private lateinit var sessionManager: SessionManager
    private val apiService = ApiClient.apiService
    
    private var currentOrderId: String? = null
    private var currentPaymentIntentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        // Inicializar con clave pública real (se debe obtener del env o config)
        PaymentConfiguration.init(applicationContext, "pk_test_51SDU5OK5XTakJuYbJd3LwWdLT2cE16wb8f2lzKoYeydLalbF7DAwwbwPeLlzWSJC6NjfY4n9EV2jUo9r5nN9a50m00AhZXrD7X")

        btnBack = findViewById(R.id.btn_back_payment)
        btnAnadirTarjeta = findViewById(R.id.btn_anadir_tarjeta)
        rvSavedCards = findViewById(R.id.rv_saved_cards)
        tvNoCards = findViewById(R.id.tv_no_cards)
        
        sessionManager = SessionManager(this)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        
        // Configurar RecyclerView
        cardAdapter = CardAdapter()
        rvSavedCards.layoutManager = LinearLayoutManager(this)
        rvSavedCards.adapter = cardAdapter

        // Obtener orden ID del intent
        currentOrderId = intent.getStringExtra("order_id")
        
        Log.d("PaymentMethodActivity", "📱 Order ID: $currentOrderId")

        btnBack.setOnClickListener { 
            finish() 
        }
        
        btnAnadirTarjeta.setOnClickListener { 
            presentPaymentSheetForAddingCard()
        }
        
        // Cargar tarjetas guardadas al abrir
        loadSavedCards()
    }

    /**
     * Cargar las tarjetas guardadas del usuario desde la API
     */
    private fun loadSavedCards() {
        lifecycleScope.launch {
            try {
                Log.d("PaymentMethodActivity", "📥 Cargando tarjetas guardadas...")

                val token = sessionManager.getToken()
                if (token.isNullOrBlank()) {
                    Log.e("PaymentMethodActivity", "❌ Sin token de autenticación")
                    tvNoCards.visibility = android.view.View.VISIBLE
                    rvSavedCards.visibility = android.view.View.GONE
                    return@launch
                }

                val response = apiService.getPaymentMethods("Bearer $token")

                if (response.isSuccessful) {
                    val body = response.body()
                    val cards = body?.data ?: emptyList()

                    Log.d("PaymentMethodActivity", "✅ Tarjetas cargadas: ${cards.size}")

                    if (cards.isEmpty()) {
                        // Mostrar mensaje de sin tarjetas
                        tvNoCards.visibility = android.view.View.VISIBLE
                        rvSavedCards.visibility = android.view.View.GONE
                    } else {
                        // Mostrar lista de tarjetas
                        tvNoCards.visibility = android.view.View.GONE
                        rvSavedCards.visibility = android.view.View.VISIBLE
                        cardAdapter.updateCards(cards)
                    }
                } else {
                    Log.e("PaymentMethodActivity", "❌ Error cargando tarjetas: ${response.code()}")
                    tvNoCards.visibility = android.view.View.VISIBLE
                    rvSavedCards.visibility = android.view.View.GONE
                }
            } catch (e: Exception) {
                Log.e("PaymentMethodActivity", "❌ Excepción al cargar tarjetas: ${e.message}", e)
                tvNoCards.visibility = android.view.View.VISIBLE
                rvSavedCards.visibility = android.view.View.GONE
            }
        }
    }

    /**
     * Crear setup intent para AÑADIR NUEVA TARJETA (sin cobrar, solo guardar)
     */
    private fun presentPaymentSheetForAddingCard() {
        lifecycleScope.launch {
            try {
                btnAnadirTarjeta.isEnabled = false
                btnAnadirTarjeta.text = "Preparando..."

                val token = sessionManager.getToken()
                if (token.isNullOrBlank()) {
                    Toast.makeText(
                        this@PaymentMethodActivity,
                        "Error: Token de autenticación no disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnAnadirTarjeta.isEnabled = true
                    btnAnadirTarjeta.text = "+ Añadir tarjeta"
                    return@launch
                }

                Log.d("PaymentMethodActivity", "🔐 Agregando nueva tarjeta a billetera (sin cobro)...")

                val walletOrderId = "wallet-add-card"

                // Llamar API para crear setup intent
                val response = apiService.createPaymentIntent(
                    walletOrderId,
                    "Bearer $token"
                )

                Log.d("PaymentMethodActivity", "📡 Respuesta API: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    val paymentData = body?.data

                    if (paymentData != null) {
                        currentPaymentIntentId = paymentData.stripe_payment_intent_id
                        val isSetupIntent = paymentData.is_setup_intent ?: false
                        
                        Log.d(
                            "PaymentMethodActivity",
                            "✅ Setup intent creado (sin cobro): ${paymentData.stripe_payment_intent_id.take(20)}..."
                        )

                        // Presentar PaymentSheet
                        // Para SetupIntent, usar presentWithSetupIntent (NO presentWithPaymentIntent)
                        if (isSetupIntent) {
                            paymentSheet.presentWithSetupIntent(
                                paymentData.client_secret,
                                PaymentSheet.Configuration(
                                    merchantDisplayName = "Heladería"
                                )
                            )
                        } else {
                            paymentSheet.presentWithPaymentIntent(
                                paymentData.client_secret,
                                PaymentSheet.Configuration(
                                    merchantDisplayName = "Heladería"
                                )
                            )
                        }
                    } else {
                        Log.e("PaymentMethodActivity", "❌ Sin datos en respuesta")
                        Toast.makeText(
                            this@PaymentMethodActivity,
                            "Error: Sin datos de pago",
                            Toast.LENGTH_SHORT
                        ).show()
                        btnAnadirTarjeta.isEnabled = true
                        btnAnadirTarjeta.text = "+ Añadir tarjeta"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentMethodActivity", "❌ Error API: $errorBody")
                    Toast.makeText(
                        this@PaymentMethodActivity,
                        "Error al abrir formulario",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnAnadirTarjeta.isEnabled = true
                    btnAnadirTarjeta.text = "+ Añadir tarjeta"
                }
            } catch (e: Exception) {
                Log.e("PaymentMethodActivity", "❌ Excepción: ${e.message}", e)
                Toast.makeText(
                    this@PaymentMethodActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                btnAnadirTarjeta.isEnabled = true
                btnAnadirTarjeta.text = "+ Añadir tarjeta"
            }
        }
    }

    /**
     * Crear payment intent en la API y presentar PaymentSheet (para órdenes existentes)
     */
    private fun presentPaymentSheet(orderId: String?) {
        lifecycleScope.launch {
            try {
                // Si no hay orden, mostrar mensaje
                if (orderId.isNullOrBlank()) {
                    Toast.makeText(
                        this@PaymentMethodActivity,
                        "Para pagar, crea una orden primero",
                        Toast.LENGTH_LONG
                    ).show()
                    btnAnadirTarjeta.isEnabled = true
                    btnAnadirTarjeta.text = "+ Añadir tarjeta"
                    return@launch
                }

                btnAnadirTarjeta.isEnabled = false
                btnAnadirTarjeta.text = "Preparando..."

                val token = sessionManager.getToken()
                if (token.isNullOrBlank()) {
                    Toast.makeText(
                        this@PaymentMethodActivity,
                        "Error: Token de autenticación no disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnAnadirTarjeta.isEnabled = true
                    btnAnadirTarjeta.text = "+ Añadir tarjeta"
                    return@launch
                }

                Log.d("PaymentMethodActivity", "🔐 Llamando a API para crear payment intent...")

                // Llamar API para crear payment intent
                val response = apiService.createPaymentIntent(
                    orderId,
                    "Bearer $token"
                )

                Log.d("PaymentMethodActivity", "📡 Respuesta API: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    val paymentData = body?.data

                    if (paymentData != null) {
                        currentPaymentIntentId = paymentData.stripe_payment_intent_id
                        Log.d(
                            "PaymentMethodActivity",
                            "✅ Payment intent creado: ${paymentData.stripe_payment_intent_id.take(20)}..."
                        )

                        // Presentar PaymentSheet
                        paymentSheet.presentWithPaymentIntent(
                            paymentData.client_secret,
                            PaymentSheet.Configuration(
                                merchantDisplayName = "Heladería"
                            )
                        )
                    } else {
                        Log.e("PaymentMethodActivity", "❌ Sin datos en respuesta")
                        Toast.makeText(
                            this@PaymentMethodActivity,
                            "Error: Sin datos de pago",
                            Toast.LENGTH_SHORT
                        ).show()
                        btnAnadirTarjeta.isEnabled = true
                        btnAnadirTarjeta.text = "+ Añadir tarjeta"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentMethodActivity", "❌ Error API: $errorBody")
                    Toast.makeText(
                        this@PaymentMethodActivity,
                        "Error al crear payment intent",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnAnadirTarjeta.isEnabled = true
                    btnAnadirTarjeta.text = "+ Añadir tarjeta"
                }
            } catch (e: Exception) {
                Log.e("PaymentMethodActivity", "❌ Excepción: ${e.message}", e)
                Toast.makeText(
                    this@PaymentMethodActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                btnAnadirTarjeta.isEnabled = true
                btnAnadirTarjeta.text = "+ Añadir tarjeta"
            }
        }
    }

    /**
     * Callback cuando PaymentSheet se cierra
     */
    private fun onPaymentSheetResult(result: PaymentSheetResult) {
        Log.d("PaymentMethodActivity", "🎯 PaymentSheet result: $result")

        when (result) {
            is PaymentSheetResult.Completed -> {
                // Usuario completó el pago - confirmar en API
                Log.d("PaymentMethodActivity", "✅ Usuario confirmó pago")
                confirmPaymentWithBackend()
            }
            is PaymentSheetResult.Canceled -> {
                Log.d("PaymentMethodActivity", "❌ Usuario canceló")
                Toast.makeText(this@PaymentMethodActivity, "Pago cancelado", Toast.LENGTH_SHORT).show()
                btnAnadirTarjeta.isEnabled = true
                btnAnadirTarjeta.text = "+ Añadir tarjeta"
            }
            is PaymentSheetResult.Failed -> {
                Log.e("PaymentMethodActivity", "❌ PaymentSheet falló: ${result.error.message}")
                Toast.makeText(
                    this@PaymentMethodActivity,
                    "Error en pago: ${result.error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                btnAnadirTarjeta.isEnabled = true
                btnAnadirTarjeta.text = "+ Añadir tarjeta"
            }
        }
    }

    /**
     * Confirmar pago en la API
     * Si es operación de billetera (wallet-add-card), solo confirma
     * Si es orden normal, procesa pago completo
     */
    private fun confirmPaymentWithBackend() {
        lifecycleScope.launch {
            try {
                if (currentPaymentIntentId == null) {
                    Toast.makeText(
                        this@PaymentMethodActivity,
                        "Error: Datos de pago incompletos",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnAnadirTarjeta.isEnabled = true
                    btnAnadirTarjeta.text = "+ Añadir tarjeta"
                    return@launch
                }

                val token = sessionManager.getToken()
                if (token.isNullOrBlank()) {
                    Toast.makeText(
                        this@PaymentMethodActivity,
                        "Error: Token no disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnAnadirTarjeta.isEnabled = true
                    btnAnadirTarjeta.text = "+ Añadir tarjeta"
                    return@launch
                }

                // Determinar si es operación de billetera o pago de orden
                val orderId = currentOrderId ?: "wallet-add-card"
                Log.d("PaymentMethodActivity", "🔄 Confirmando en API para order: $orderId")

                val request = ConfirmPaymentRequestDto(
                    payment_intent_id = currentPaymentIntentId!!
                )

                val response = apiService.confirmPayment(
                    orderId,
                    request,
                    "Bearer $token"
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("PaymentMethodActivity", "✅ Confirmado: ${body?.message}")

                    // Mensaje diferente según si es billetera o pago
                    val successMsg = if (orderId == "wallet-add-card") {
                        "¡Tarjeta guardada exitosamente! 💳"
                    } else {
                        "¡Pago exitoso! 🎉"
                    }

                    Toast.makeText(
                        this@PaymentMethodActivity,
                        successMsg,
                        Toast.LENGTH_SHORT
                    ).show()

                    // Esperar un poco y cerrar
                    Thread.sleep(1500)
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Log.e("PaymentMethodActivity", "❌ Error: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentMethodActivity", "Error body: $errorBody")

                    Toast.makeText(
                        this@PaymentMethodActivity,
                        "Error al confirmar: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()

                    btnAnadirTarjeta.isEnabled = true
                    btnAnadirTarjeta.text = "+ Añadir tarjeta"
                }
            } catch (e: Exception) {
                Log.e("PaymentMethodActivity", "❌ Excepción: ${e.message}", e)
                Toast.makeText(
                    this@PaymentMethodActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                btnAnadirTarjeta.isEnabled = true
                btnAnadirTarjeta.text = "+ Añadir tarjeta"
            }
        }
    }
}

