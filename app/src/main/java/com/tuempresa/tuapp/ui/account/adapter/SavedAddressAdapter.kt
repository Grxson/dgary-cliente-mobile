package com.tuempresa.tuapp.ui.account.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.SavedAddress

class SavedAddressAdapter(private var addresses: List<SavedAddress>) :
    RecyclerView.Adapter<SavedAddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitulo: TextView = itemView.findViewById(R.id.tv_address_titulo)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tv_address_direccion)

        fun bind(address: SavedAddress) {
            tvTitulo.text = address.titulo
            tvDireccion.text = address.direccion
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addresses[position])
    }

    override fun getItemCount() = addresses.size
}

