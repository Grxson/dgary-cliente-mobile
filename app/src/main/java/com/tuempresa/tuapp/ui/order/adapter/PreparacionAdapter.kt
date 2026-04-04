package com.tuempresa.tuapp.ui.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.Preparacion

class PreparacionAdapter(private val preparaciones: List<Preparacion>) :
    RecyclerView.Adapter<PreparacionAdapter.PreparacionViewHolder>() {

    class PreparacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_preparacion_nombre)
        private val ivAdd: ImageView = itemView.findViewById(R.id.iv_add_preparacion)
        private val llPreparacion: View = itemView

        fun bind(preparacion: Preparacion) {
            tvNombre.text = preparacion.nombre
            ivAdd.setOnClickListener {
                // Lógica para agregar preparación
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreparacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preparacion, parent, false)
        return PreparacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreparacionViewHolder, position: Int) {
        holder.bind(preparaciones[position])
    }

    override fun getItemCount() = preparaciones.size
}

