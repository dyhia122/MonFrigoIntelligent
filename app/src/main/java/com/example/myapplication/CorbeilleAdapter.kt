package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CorbeilleAdapter(
    private val context: Context,
    private var aliments: MutableList<CorbeilleAliment>,
    private val corbeilleDao: CorbeilleDao
) : BaseAdapter() {

    fun updateData(newList: List<CorbeilleAliment>) {
        aliments = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun getCount(): Int = aliments.size
    override fun getItem(position: Int): Any = aliments[position]
    override fun getItemId(position: Int): Long = aliments[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_corbeille, parent, false)

        val aliment = aliments[position]

        val textNom = view.findViewById<TextView>(R.id.textNom)
        val textDetails = view.findViewById<TextView>(R.id.textDetails)

        textNom.text = "${aliment.nom} x${aliment.quantite}"
        textDetails.text = "Supprimé le ${aliment.dateSuppression} | Exp: ${aliment.dateExpiration}"

        return view
    }
}
