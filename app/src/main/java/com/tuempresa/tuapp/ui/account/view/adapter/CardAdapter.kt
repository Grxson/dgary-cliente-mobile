package com.tuempresa.tuapp.ui.account.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.remote.dto.PaymentMethodDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

class CardAdapter : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private val cards = mutableListOf<PaymentMethodDto>()

    inner class CardViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvCardBrand: TextView = itemView.findViewById(R.id.tv_card_brand)
        private val tvCardDate: TextView = itemView.findViewById(R.id.tv_card_date)
        private val tvCardLastFour: TextView = itemView.findViewById(R.id.tv_card_last_four)
        private val tvCardUsage: TextView = itemView.findViewById(R.id.tv_card_usage)

        fun bind(card: PaymentMethodDto) {
            // Marca de tarjeta
            tvCardBrand.text = card.card_brand

            // Última vez usada
            tvCardDate.text = formatCardDate(card.last_used)

            // Últimos 4 dígitos
            tvCardLastFour.text = "${card.card_brand} •••• ${card.card_last_four}"

            // Cuántas veces se usó
            val usageText = when {
                card.usage_count == 1 -> "Usada 1 vez"
                else -> "Usada ${card.usage_count} veces"
            }
            tvCardUsage.text = usageText
        }

        /**
         * Formatea la fecha de la tarjeta a un formato legible
         */
        private fun formatCardDate(dateString: String?): String {
            if (dateString.isNullOrBlank()) return "Sin información"

            return try {
                // Parsear ISO 8601 a LocalDateTime
                val dateTime = LocalDateTime.parse(
                    dateString,
                    DateTimeFormatter.ISO_DATE_TIME
                )

                // Formatear a "Usado el 08 de Abril de 2026"
                val formatter = DateTimeFormatterBuilder()
                    .appendLiteral("Usado el ")
                    .appendPattern("dd 'de' MMMM 'de' yyyy")
                    .toFormatter(Locale("es", "ES"))

                formatter.format(dateTime)
            } catch (e: Exception) {
                "Fecha desconocida"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_method, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    /**
     * Actualizar lista de tarjetas
     */
    fun updateCards(newCards: List<PaymentMethodDto>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    /**
     * Limpiar lista de tarjetas
     */
    fun clearCards() {
        cards.clear()
        notifyDataSetChanged()
    }
}
