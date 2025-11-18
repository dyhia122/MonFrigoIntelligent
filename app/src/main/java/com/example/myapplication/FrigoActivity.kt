package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class FrigoActivity : AppCompatActivity() {

    private lateinit var listeAliments: ListView
    private lateinit var textAucunAliment: TextView
    private lateinit var btnAjouter: Button
    private lateinit var btnMenu: Button
    private lateinit var btnCompte: Button

    private val aliments = mutableListOf<Aliment>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dao: AlimentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frigo)

        // Initialisation
        listeAliments = findViewById(R.id.listeAliments)
        textAucunAliment = findViewById(R.id.textAucunAliment)
        btnAjouter = findViewById(R.id.btnAjouter)
        btnMenu = findViewById(R.id.btnMenu)
        btnCompte = findViewById(R.id.btnCompte)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listeAliments.adapter = adapter

        // Charger DAO
        dao = FrigoDatabase.getDatabase(this).alimentDao()

        // Observation des aliments dans Room
        lifecycleScope.launch {
            dao.getAllAliments().collect { alimentsBDD ->
                aliments.clear()
                aliments.addAll(alimentsBDD)

                adapter.clear()
                adapter.addAll(aliments.map { formatAliment(it) })
                adapter.notifyDataSetChanged()

                mettreAJourAffichage()
            }
        }

        btnAjouter.setOnClickListener { afficherDialogAjout() }

        btnMenu.setOnClickListener {
            startActivity(Intent(this, MenuScreen::class.java))
        }

        btnCompte.setOnClickListener {
            startActivity(Intent(this, CompteActivity::class.java))
        }
    }

    private fun afficherDialogAjout() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val nomInput = EditText(this).apply { hint = "Nom de l’aliment" }
        val quantiteInput = EditText(this).apply {
            hint = "Quantité"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val dateInput = EditText(this).apply { hint = "Date d’expiration (YYYY-MM-DD)" }

        layout.apply {
            addView(nomInput)
            addView(quantiteInput)
            addView(dateInput)
        }

        AlertDialog.Builder(this)
            .setTitle("Ajouter un aliment")
            .setView(layout)
            .setPositiveButton("Ajouter") { _, _ ->
                val nom = nomInput.text.toString().trim()
                val quantite = quantiteInput.text.toString().toIntOrNull() ?: 1
                val dateExp = dateInput.text.toString().trim()

                if (nom.isNotEmpty() && dateExp.isNotEmpty()) {
                    val aliment = Aliment(
                        nom = nom,
                        quantite = quantite,
                        dateExpiration = dateExp
                    )

                    // Insérer dans Room
                    lifecycleScope.launch {
                        dao.insert(aliment)
                    }

                    Toast.makeText(this, "$nom ajouté!", Toast.LENGTH_SHORT).show()
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
