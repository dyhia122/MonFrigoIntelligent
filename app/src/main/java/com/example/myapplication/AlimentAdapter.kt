package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AlimentAdapter(
    private val context: Context,
    private var aliments: MutableList<Aliment>,
    private val dao: AlimentDao,
    private val corbeilleDao: CorbeilleDao
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
        val (text, color) = getExpirationText(aliment.dateExpiration)
        expiration.text = text
        expiration.setTextColor(color.toArgb())

        // Amélioration design : Boutons avec icônes colorées
        btnPlus.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_green_dark))
        btnMoins.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_orange_dark))
        btnDelete.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark))

        btnPlus.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    dao.update(aliment.copy(quantite = aliment.quantite + 1))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        btnMoins.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (aliment.quantite > 1) {
                        dao.update(aliment.copy(quantite = aliment.quantite - 1))
                    } else {
                        val corbeilleItem = CorbeilleAliment(
                            nom = aliment.nom,
                            quantite = aliment.quantite,
                            dateExpiration = aliment.dateExpiration,
                            dateSuppression = LocalDate.now().toString()
                        )
                        corbeilleDao.insert(corbeilleItem)
                        dao.delete(aliment)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val corbeilleItem = CorbeilleAliment(
                        nom = aliment.nom,
                        quantite = aliment.quantite,
                        dateExpiration = aliment.dateExpiration,
                        dateSuppression = LocalDate.now().toString()
                    )
                    corbeilleDao.insert(corbeilleItem)
                    dao.delete(aliment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return view
    }

    private fun getExpirationText(date: String): Pair<String, Color> {
        return try {
            val today = LocalDate.now()
            val exp = LocalDate.parse(date)
            val days = ChronoUnit.DAYS.between(today, exp)

            val (text, color) = when {
                days < 0 -> "⚠ Périmé depuis ${-days} jours" to Color.Red
                days == 0L -> "⚠ Expire aujourd’hui" to Color.Red
                days == 1L -> "Expire dans 1 jour" to Color(0xFFFF9800)
                days <= 4L -> "Expire dans $days jours" to Color(0xFFFF9800)
                else -> "Expire dans $days jours" to Color.Green
            }
            Pair(text, color)
        } catch (e: Exception) {
            "Date invalide" to Color.Gray
        }
    }
}