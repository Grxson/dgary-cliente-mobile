package com.tuempresa.tuapp.ui.cupones.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.local.SelectedCoupon
import com.tuempresa.tuapp.data.local.SelectedCouponStore
import com.tuempresa.tuapp.data.repository.CouponRepository
import com.tuempresa.tuapp.ui.account.view.AccountActivity
import com.tuempresa.tuapp.ui.carrito.view.CarritoActivity
import com.tuempresa.tuapp.ui.cupones.adapter.CuponesAdapter
import com.tuempresa.tuapp.ui.cupones.model.Cupon
import com.tuempresa.tuapp.ui.home.view.HomeActivity
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CuponesActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var rvCupones: RecyclerView
    private lateinit var cuponesAdapter: CuponesAdapter
    private lateinit var navInicio: LinearLayout
    private lateinit var navCarrito: LinearLayout
    private lateinit var navMiCuenta: LinearLayout
    private lateinit var tvCouponsStatus: TextView

    private lateinit var couponRepository: CouponRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cupones)

        couponRepository = CouponRepository(this)

        initializeViews()
        setupListeners()
        loadCupones()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_cupones)
        rvCupones = findViewById(R.id.rv_cupones)
        navInicio = findViewById(R.id.nav_inicio)
        navCarrito = findViewById(R.id.nav_carrito)
        navMiCuenta = findViewById(R.id.nav_mi_cuenta)
        tvCouponsStatus = findViewById(R.id.tv_coupons_status)

        rvCupones.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cuponesAdapter = CuponesAdapter(emptyList()) { coupon ->
            applyCoupon(coupon)
        }
        rvCupones.adapter = cuponesAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
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
            startActivity(Intent(this, AccountActivity::class.java))
            finish()
        }
    }

    private fun loadCupones() {
        tvCouponsStatus.text = getString(R.string.coupons_loading)
        val selectedCouponCode = SelectedCouponStore.selectedCoupon.value?.code

        lifecycleScope.launch {
            val coupons = couponRepository.getCoupons().map { dto ->
                val couponReference = dto.code
                    ?.takeIf { it.isNotBlank() }
                    ?: dto.coupon?.code?.takeIf { it.isNotBlank() }
                    ?: dto.id.toString()

                val couponTitle = dto.name
                    ?: dto.coupon?.name
                    ?: "Cupón #${dto.id}"

                val couponDescription = dto.description
                    ?: dto.coupon?.description
                    ?: "Descuento disponible para tu siguiente compra"

                val couponDiscount = dto.discount
                    ?: dto.coupon?.discount
                    ?: 0
                val discountAmount = couponDiscount.toDouble()
                val discountLabel = "-${formatMxn(discountAmount)}"

                Cupon(
                    id = dto.id,
                    code = couponReference,
                    titulo = couponTitle,
                    descripcion = couponDescription,
                    discountAmount = discountAmount,
                    descuento = discountLabel,
                    botonTexto = if (couponReference == selectedCouponCode) {
                        getString(R.string.coupon_applied)
                    } else {
                        getString(R.string.coupon_apply)
                    }
                )
            }

            cuponesAdapter.updateItems(coupons, selectedCouponCode)
            tvCouponsStatus.text = if (coupons.isEmpty()) {
                getString(R.string.coupons_empty)
            } else {
                ""
            }
        }
    }

    private fun applyCoupon(coupon: Cupon) {
        SelectedCouponStore.select(
            SelectedCoupon(
                code = coupon.code,
                title = coupon.titulo,
                discountAmount = coupon.discountAmount,
                discountLabel = coupon.descuento
            )
        )

        loadCupones()

        lifecycleScope.launch {
            val result = couponRepository.validateCoupon(coupon.code, 100.0)
            val message = if (result != null) {
                "${coupon.titulo} quedó aplicado para tu carrito"
            } else {
                "No se pudo validar ${coupon.titulo}, pero puedes volver a intentarlo"
            }
            Toast.makeText(
                this@CuponesActivity,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun formatMxn(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        formatter.currency = java.util.Currency.getInstance("MXN")
        return formatter.format(value)
    }
}

