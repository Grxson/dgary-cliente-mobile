package com.tuempresa.tuapp.ui.cupones.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.ui.cupones.model.Cupon

class CuponesAdapter(
    private var cupones: List<Cupon>,
    private val onApplyCoupon: (Cupon) -> Unit
) : RecyclerView.Adapter<CuponesAdapter.CuponViewHolder>() {

    class CuponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitulo: TextView = itemView.findViewById(R.id.tv_cupon_titulo)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tv_cupon_descripcion)
        private val tvDescuento: TextView = itemView.findViewById(R.id.tv_cupon_descuento)
        private val btnComprar: MaterialButton = itemView.findViewById(R.id.btn_cupon_comprar)

        fun bind(cupon: Cupon, onApplyCoupon: (Cupon) -> Unit) {
            tvTitulo.text = cupon.titulo
            tvDescripcion.text = cupon.descripcion
            tvDescuento.text = cupon.descuento

            btnComprar.text = cupon.botonTexto
            btnComprar.setOnClickListener {
                onApplyCoupon(cupon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cupon, parent, false)
        return CuponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuponViewHolder, position: Int) {
        holder.bind(cupones[position], onApplyCoupon)
    }

    override fun getItemCount() = cupones.size

    fun updateItems(items: List<Cupon>) {
        cupones = items
        notifyDataSetChanged()
    }
}

