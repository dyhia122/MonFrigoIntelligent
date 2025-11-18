package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate

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

        // Initialisation UI
        listeAliments = findViewById(R.id.listeAliments)
        textAucunAliment = findViewById(R.id.textAucunAliment)
        btnAjouter = findViewById(R.id.btnAjouter)
        btnMenu = findViewById(R.id.btnMenu)
        btnCompte = findViewById(R.id.btnCompte)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listeAliments.adapter = adapter

        // Charger DAO depuis Room
        dao = FrigoDatabase.getDatabase(this).alimentDao()

        // Observer la base de données Room
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

        // Boutons
        btnAjouter.setOnClickListener { afficherDialogAjout() }

        btnMenu.setOnClickListener {
            startActivity(Intent(this, MenuScreen::class.java))
        }

        btnCompte.setOnClickListener {
            startActivity(Intent(this, CompteActivity::class.java))
        }
    }

    // ------------------------
    // DIALOG D’AJOUT D'ALIMENT
    // ------------------------
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

        layout.addView(nomInput)
        layout.addView(quantiteInput)
        layout.addView(dateInput)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un aliment")
            .setView(layout)
            .setPositiveButton("Ajouter") { _, _ ->
                val nom = nomInput.text.toString().trim()
                val quantite = quantiteInput.text.toString().toIntOrNull() ?: 1
                val dateExp = dateInput.text.toString().trim()

                // Vérification des champs
                if (nom.isEmpty() || dateExp.isEmpty()) {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Vérification du format de la date
                if (!dateValide(dateExp)) {
                    Toast.makeText(this, "Format de date invalide (YYYY-MM-DD)", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Création de l'objet Aliment
                val aliment = Aliment(
                    nom = nom,
                    quantite = quantite,
                    dateExpiration = dateExp
                )

                // Insertion en base
                lifecycleScope.launch {
                    dao.insert(aliment)
                }

                Toast.makeText(this, "$nom ajouté!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Annuler", null)
            .create()
            .show()
    }

    // ------------------------
    // VALIDATION DATE
    // ------------------------
    private fun dateValide(date: String): Boolean {
        return try {
            LocalDate.parse(date) // Vérifie format + validité
            true
        } catch (e: Exception) {
            false
        }
    }

    // ------------------------
    // FORMATAGE POUR LISTVIEW
    // ------------------------
    private fun formatAliment(aliment: Aliment): String {
        return "${aliment.nom} - ${aliment.quantite} pcs - Exp: ${aliment.dateExpiration}"
    }

    // ------------------------
    // GESTION DU TEXTE "AUCUN ALIMENT"
    // ------------------------
    private fun mettreAJourAffichage() {
        textAucunAliment.visibility =
            if (aliments.isEmpty()) TextView.VISIBLE else TextView.GONE
    }
}
