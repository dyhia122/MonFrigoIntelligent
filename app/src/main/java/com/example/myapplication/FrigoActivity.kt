package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class FrigoActivity : AppCompatActivity() {

    private lateinit var listeAliments: ListView
    private lateinit var textAucunAliment: TextView
    private lateinit var btnAjouter: Button
    private lateinit var btnMenu: Button
    private lateinit var btnCompte: Button

    private val aliments = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frigo)

        // Liaison XML
        listeAliments = findViewById(R.id.listeAliments)
        textAucunAliment = findViewById(R.id.textAucunAliment)
        btnAjouter = findViewById(R.id.btnAjouter)
        btnMenu = findViewById(R.id.btnMenu)
        btnCompte = findViewById(R.id.btnCompte)

        // Adapter pour afficher la liste
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, aliments)
        listeAliments.adapter = adapter

        // Bouton ajouter un aliment
        btnAjouter.setOnClickListener { afficherDialogAjout() }

        // 🔹 Bouton Menu → ouvre ton MenuScreen (ou activité menu)
        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuScreen::class.java)
            startActivity(intent)
        }

        // 🔹 Bouton Compte → ouvre ton écran compte
        btnCompte.setOnClickListener {
            val intent = Intent(this, CompteActivity::class.java)
            startActivity(intent)
        }

        mettreAJourAffichage()
    }

    // Boîte de dialogue d’ajout d’aliment
    private fun afficherDialogAjout() {
        val editText = EditText(this)
        editText.hint = "Nom de l’aliment"

        AlertDialog.Builder(this)
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
            .show()
    }

    private fun mettreAJourAffichage() {
        textAucunAliment.visibility =
            if (aliments.isEmpty()) TextView.VISIBLE else TextView.GONE
    }
}
