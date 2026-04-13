package com.tuempresa.tuapp.ui.account.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.domain.model.SavedAddress

class SavedAddressAdapter(
    private var addresses: List<SavedAddress>,
    private val onAddressAction: (SavedAddress, String) -> Unit
) : RecyclerView.Adapter<SavedAddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(
        itemView: View,
        private val onAddressAction: (SavedAddress, String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvTitulo: TextView = itemView.findViewById(R.id.tv_address_titulo)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tv_address_direccion)
        private val btnEdit: ImageView = itemView.findViewById(R.id.btn_edit_address)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete_address)

        fun bind(address: SavedAddress) {
            tvTitulo.text = address.titulo
            tvDireccion.text = address.direccion

            btnEdit.setOnClickListener {
                onAddressAction(address, ACTION_EDIT)
            }

            btnDelete.setOnClickListener {
                onAddressAction(address, ACTION_DELETE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_address, parent, false)
        return AddressViewHolder(view, onAddressAction)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addresses[position])
    }

    override fun getItemCount() = addresses.size

    companion object {
        const val ACTION_EDIT = "edit"
        const val ACTION_DELETE = "delete"
    }
}

