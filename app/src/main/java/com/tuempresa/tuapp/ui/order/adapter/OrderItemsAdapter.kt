package com.tuempresa.tuapp.ui.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.OrderItem

class OrderItemsAdapter(private val items: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCantidad: TextView = itemView.findViewById(R.id.tv_cantidad)
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_item_nombre)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tv_item_precio)
        private val ivProducto: ImageView = itemView.findViewById(R.id.iv_producto)

        fun bind(item: OrderItem) {
            tvCantidad.text = item.cantidad.toString()
            tvNombre.text = item.nombre
            tvPrecio.text = String.format("$%.2f", item.precio)
            // Cargar imagen del producto
            ivProducto.setImageResource(R.drawable.logo_nombre)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}

