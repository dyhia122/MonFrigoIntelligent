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

    private val aliments = mutableListOf<Aliment>() // maintenant c'est la liste d'objets Aliment
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frigo)

        listeAliments = findViewById(R.id.listeAliments)
        textAucunAliment = findViewById(R.id.textAucunAliment)
        btnAjouter = findViewById(R.id.btnAjouter)
        btnMenu = findViewById(R.id.btnMenu)
        btnCompte = findViewById(R.id.btnCompte)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, aliments.map { formatAliment(it) })
        listeAliments.adapter = adapter

        btnAjouter.setOnClickListener { afficherDialogAjout() }

        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuScreen::class.java)
            startActivity(intent)
        }

        btnCompte.setOnClickListener {
            val intent = Intent(this, CompteActivity::class.java)
            startActivity(intent)
        }

        mettreAJourAffichage()
    }

    private fun afficherDialogAjout() {
        // Layout personnalisé pour le dialog
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val nomInput = EditText(this)
        nomInput.hint = "Nom de l’aliment"
        layout.addView(nomInput)

        val quantiteInput = EditText(this)
        quantiteInput.hint = "Quantité"
        quantiteInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        layout.addView(quantiteInput)

        val dateInput = EditText(this)
        dateInput.hint = "Date d’expiration (YYYY-MM-DD)"
        layout.addView(dateInput)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un aliment")
            .setView(layout)
            .setPositiveButton("Ajouter") { _, _ ->
                val nom = nomInput.text.toString().trim()
                val quantite = quantiteInput.text.toString().toIntOrNull() ?: 1
                val dateExp = dateInput.text.toString().trim()

                if (nom.isNotEmpty() && dateExp.isNotEmpty()) {
                    val aliment = Aliment(nom = nom, quantite = quantite, dateExpiration = dateExp)
                    aliments.add(aliment)
                    adapter.clear()
                    adapter.addAll(aliments.map { formatAliment(it) })
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "$nom ajouté au frigo", Toast.LENGTH_SHORT).show()
                    mettreAJourAffichage()
                } else {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .create()
            .show()
    }

    private fun formatAliment(aliment: Aliment): String {
        return "${aliment.nom} - ${aliment.quantite} pcs - Exp: ${aliment.dateExpiration}"
    }

    private fun mettreAJourAffichage() {
        textAucunAliment.visibility = if (aliments.isEmpty()) TextView.VISIBLE else TextView.GONE
    }
}
