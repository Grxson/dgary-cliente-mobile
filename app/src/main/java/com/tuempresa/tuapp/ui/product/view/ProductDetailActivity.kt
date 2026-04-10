package com.tuempresa.tuapp.ui.product.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.local.CartStore
import com.tuempresa.tuapp.data.repository.ProductRepository
import kotlinx.coroutines.launch
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvEta: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnAddToCart: MaterialButton

    private lateinit var productRepository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        productRepository = ProductRepository(this)

        btnBack = findViewById(R.id.btn_back_product_detail)
        tvName = findViewById(R.id.tv_product_detail_name)
        tvCategory = findViewById(R.id.tv_product_detail_category)
        tvPrice = findViewById(R.id.tv_product_detail_price)
        tvEta = findViewById(R.id.tv_product_detail_eta)
        tvDescription = findViewById(R.id.tv_product_detail_description)
        btnAddToCart = findViewById(R.id.btn_product_detail_add_to_cart)

        btnBack.setOnClickListener { finish() }

        loadProduct()
    }

    private fun loadProduct() {
        val productId = intent.getStringExtra("product_id")
            ?: intent.getIntExtra("product_id", 0).takeIf { it > 0 }?.toString()
            ?: return

        lifecycleScope.launch {
            val product = productRepository.getProductById(productId)
            if (product == null) {
                tvName.text = getString(R.string.product_detail_error)
                btnAddToCart.isEnabled = false
                return@launch
            }

            tvName.text = product.name
            tvCategory.text = getString(R.string.product_detail_category, product.category)
            tvPrice.text = String.format(Locale.US, "$%.2f", product.price)
            tvEta.text = getString(R.string.product_detail_eta, product.estimatedTime)
            tvDescription.text = getString(R.string.product_detail_description_default)

            btnAddToCart.setOnClickListener {
                CartStore.add(product)
                finish()
            }
        }
    }
}
