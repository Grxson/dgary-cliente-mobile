package com.tuempresa.tuapp.ui.carrito.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.CartItem
import java.util.Locale

class CartAdapter(
    private var items: List<CartItem>,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_cart_item_name)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_cart_item_price)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tv_cart_item_quantity)
        private val btnIncrease: View = itemView.findViewById(R.id.btn_cart_increase)
        private val btnDecrease: View = itemView.findViewById(R.id.btn_cart_decrease)
        private val btnRemove: ImageView = itemView.findViewById(R.id.btn_cart_remove)

        fun bind(
            item: CartItem,
            onIncrease: (CartItem) -> Unit,
            onDecrease: (CartItem) -> Unit,
            onRemove: (CartItem) -> Unit
        ) {
            tvName.text = item.name
            tvPrice.text = String.format(Locale.US, "$%.2f", item.price * item.quantity)
            tvQuantity.text = item.quantity.toString()
            btnIncrease.setOnClickListener { onIncrease(item) }
            btnDecrease.setOnClickListener { onDecrease(item) }
            btnRemove.setOnClickListener { onRemove(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position], onIncrease, onDecrease, onRemove)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}