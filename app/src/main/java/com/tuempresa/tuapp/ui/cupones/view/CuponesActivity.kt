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
import com.tuempresa.tuapp.data.repository.CouponRepository
import com.tuempresa.tuapp.ui.account.view.AccountActivity
import com.tuempresa.tuapp.ui.carrito.view.CarritoActivity
import com.tuempresa.tuapp.ui.cupones.adapter.CuponesAdapter
import com.tuempresa.tuapp.ui.cupones.model.Cupon
import com.tuempresa.tuapp.ui.home.view.HomeActivity
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            val coupons = couponRepository.getCoupons().map { dto ->
                Cupon(
                    id = dto.id,
                    titulo = dto.coupon?.name ?: "Cupón ${dto.id}",
                    descripcion = "Descuento disponible para tu siguiente compra",
                    descuento = "${dto.discount}%",
                    botonTexto = getString(R.string.coupon_apply)
                )
            }

            cuponesAdapter.updateItems(coupons)
            tvCouponsStatus.text = if (coupons.isEmpty()) {
                getString(R.string.coupons_empty)
            } else {
                ""
            }
        }
    }

    private fun applyCoupon(coupon: Cupon) {
        lifecycleScope.launch {
            val result = couponRepository.validateCoupon(coupon.id, 100.0)
            if (result != null) {
                val message = "${coupon.titulo} aplicado. Nuevo total: $${result.total}"
                Toast.makeText(this@CuponesActivity, message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this@CuponesActivity,
                    "No se pudo validar el cupón",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

