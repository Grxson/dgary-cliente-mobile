package com.tuempresa.tuapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.Product
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: android.view.View) :
        RecyclerView.ViewHolder(itemView) {
        private val productLogo: ImageView = itemView.findViewById(R.id.product_logo)
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)

        fun bind(product: Product) {
            productName.text = product.name
            productPrice.text = "${String.format("$%.2f", product.price)} | ${product.estimatedTime}"
            // Aquí podrías cargar la imagen del producto si lo deseas
            productLogo.setImageResource(R.drawable.logo_nombre)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}

