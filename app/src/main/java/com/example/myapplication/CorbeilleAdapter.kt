package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    override fun getView(position: Int, convertView: android.view.View?, parent: ViewGroup?): android.view.View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val aliment = aliments[position]
        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)

        text1.text = "${aliment.nom} x${aliment.quantite}"
        text2.text = "Supprimé le ${aliment.dateSuppression} | Exp: ${aliment.dateExpiration}"

        return view
    }
}
