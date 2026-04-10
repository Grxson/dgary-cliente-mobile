package com.tuempresa.tuapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val products: List<Product>,
    private val onAddToCart: (Product) -> Unit,
    private val onProductClick: ((Product) -> Unit)? = null
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productLogo: ImageView = itemView.findViewById(R.id.product_logo)
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)
        private val addButton: View = itemView.findViewById(R.id.btn_add_product)

        fun bind(
            product: Product,
            onAddToCart: (Product) -> Unit,
            onProductClick: ((Product) -> Unit)?
        ) {
            productName.text = product.name
            productPrice.text = "${toMxn(product.price)} | ${product.estimatedTime}"
            productLogo.setImageResource(R.drawable.logo_nombre)
            addButton.setOnClickListener { onAddToCart(product) }
            itemView.setOnClickListener { onProductClick?.invoke(product) }
        }

        private fun toMxn(value: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"))
            formatter.currency = java.util.Currency.getInstance("MXN")
            return formatter.format(value)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], onAddToCart, onProductClick)
    }

    override fun getItemCount() = products.size
}

