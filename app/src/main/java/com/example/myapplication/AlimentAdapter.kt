package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AlimentAdapter(
    private val context: Context,
    private var aliments: MutableList<Aliment>,
    private val dao: AlimentDao
) : BaseAdapter() {

    fun updateData(newList: List<Aliment>) {
        aliments = newList.sortedBy { LocalDate.parse(it.dateExpiration) }.toMutableList()
        notifyDataSetChanged()
    }

    override fun getCount(): Int = aliments.size
    override fun getItem(position: Int): Any = aliments[position]
    override fun getItemId(position: Int): Long = aliments[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_aliment, parent, false)

        val aliment = aliments[position]

        val nom = view.findViewById<TextView>(R.id.itemNom)
        val quantite = view.findViewById<TextView>(R.id.itemQuantite)
        val expiration = view.findViewById<TextView>(R.id.itemExpiration)

        val btnPlus = view.findViewById<ImageButton>(R.id.btnPlus)
        val btnMoins = view.findViewById<ImageButton>(R.id.btnMoins)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        nom.text = aliment.nom
        quantite.text = "Quantité : ${aliment.quantite}"

        expiration.text = getExpirationText(aliment.dateExpiration)

        btnPlus.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dao.update(aliment.copy(quantite = aliment.quantite + 1))
            }
        }

        btnMoins.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (aliment.quantite > 1) {
                    dao.update(aliment.copy(quantite = aliment.quantite - 1))
                } else {
                    dao.delete(aliment)
                }
            }
        }

        btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dao.delete(aliment)
            }
        }

        return view
    }

    private fun getExpirationText(date: String): String {
        return try {
            val today = LocalDate.now()
            val exp = LocalDate.parse(date)
            val days = ChronoUnit.DAYS.between(today, exp)

            when {
                days < 0 -> "⚠ Périmé depuis ${-days} jours"
                days == 0L -> "⚠ Expire aujourd’hui"
                days == 1L -> "Expire dans 1 jour"
                else -> "Expire dans $days jours"
            }
        } catch (e: Exception) {
            "Date invalide"
        }
    }
}
