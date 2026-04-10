package com.tuempresa.tuapp.ui.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.remote.dto.OrderListDto
import java.util.Locale

class OrdersAdapter(
    private var items: List<OrderListDto>,
    private val onClick: (OrderListDto) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tv_order_summary_id)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_order_summary_status)
        private val tvTotal: TextView = itemView.findViewById(R.id.tv_order_summary_total)
        private val tvItems: TextView = itemView.findViewById(R.id.tv_order_summary_items)

        fun bind(item: OrderListDto, onClick: (OrderListDto) -> Unit) {
            tvId.text = itemView.context.getString(R.string.order_summary_number, item.id)
            tvStatus.text = item.status ?: itemView.context.getString(R.string.order_status_unknown)
            tvTotal.text = String.format(Locale.US, "$%.2f", item.total)
            tvItems.text = item.details.firstOrNull()?.product?.name ?: itemView.context.getString(R.string.order_summary_no_items)
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_summary, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<OrderListDto>) {
        items = newItems
        notifyDataSetChanged()
    }
}