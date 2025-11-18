package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AlimentAdapter(
    private val context: Context,
    private val aliments: MutableList<Aliment>,
    private val dao: AlimentDao,
    private val lifecycle: androidx.lifecycle.LifecycleOwner
) : BaseAdapter() {

    override fun getCount(): Int = aliments.size
    override fun getItem(position: Int): Any = aliments[position]
    override fun getItemId(position: Int): Long = aliments[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_aliment, parent, false)

        val nom = view.findViewById<TextView>(R.id.textNom)
        val quantite = view.findViewById<TextView>(R.id.textQuantite)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMoins = view.findViewById<Button>(R.id.btnMoins)

        val aliment = aliments[position]

        nom.text = "${aliment.nom} (Exp: ${aliment.dateExpiration})"
        quantite.text = aliment.quantite.toString()

        // --- Bouton + ---
        btnPlus.setOnClickListener {
            val newQuantite = aliment.quantite + 1

            val updated = aliment.copy(quantite = newQuantite)

            lifecycle.lifecycleScope.launch {
                dao.update(updated)
            }
        }

        // --- Bouton – / corbeille ---
        btnMoins.setOnClickListener {
            val newQuantite = aliment.quantite - 1

            lifecycle.lifecycleScope.launch {
                if (newQuantite <= 0) {
                    dao.delete(aliment)
                } else {
                    dao.update(aliment.copy(quantite = newQuantite))
                }
            }
        }

        return view
    }
}
