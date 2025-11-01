package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class FrigoActivity : AppCompatActivity() {

    private lateinit var listeAliments: ListView
    private lateinit var textAucunAliment: TextView
    private lateinit var btnAjouter: Button

    private val aliments = mutableListOf<String>() // Liste simple pour débuter
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frigo)

        // Liaison entre le code et le XML
        listeAliments = findViewById(R.id.listeAliments)
        textAucunAliment = findViewById(R.id.textAucunAliment)
        btnAjouter = findViewById(R.id.btnAjouter)

        // Adapter = permet d'afficher la liste
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, aliments)
        listeAliments.adapter = adapter

        // Quand on clique sur "Ajouter un aliment"
        btnAjouter.setOnClickListener {
            afficherDialogAjout()
        }

        mettreAJourAffichage()
    }

    // Affiche la boîte de dialogue pour ajouter un aliment
    private fun afficherDialogAjout() {
        val editText = EditText(this)
        editText.hint = "Nom de l’aliment"

        val dialog = AlertDialog.Builder(this) // ✅ fonctionne maintenant
            .setTitle("Ajouter un aliment")
            .setView(editText)
            .setPositiveButton("Ajouter") { _, _ ->
                val nom = editText.text.toString().trim()
                if (nom.isNotEmpty()) {
                    aliments.add(nom)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "$nom ajouté au frigo", Toast.LENGTH_SHORT).show()
                    mettreAJourAffichage()
                }
            }
            .setNegativeButton("Annuler", null)
            .create()

        dialog.show()
    }

    private fun mettreAJourAffichage() {
        if (aliments.isEmpty()) {
            textAucunAliment.visibility = TextView.VISIBLE
        } else {
            textAucunAliment.visibility = TextView.GONE
        }
    }
}
