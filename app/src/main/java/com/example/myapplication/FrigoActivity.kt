package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class FrigoActivity : AppCompatActivity() {

    private lateinit var listeAliments: ListView
    private lateinit var textAucunAliment: TextView
    private lateinit var btnAjouter: Button
    private lateinit var btnMenu: Button
    private lateinit var btnCompte: Button

    private lateinit var dao: AlimentDao
    private lateinit var adapter: AlimentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frigo)

        listeAliments = findViewById(R.id.listeAliments)
        textAucunAliment = findViewById(R.id.textAucunAliment)
        btnAjouter = findViewById(R.id.btnAjouter)
        btnMenu = findViewById(R.id.btnMenu)
        btnCompte = findViewById(R.id.btnCompte)

        dao = FrigoDatabase.getDatabase(this).alimentDao()
        adapter = AlimentAdapter(this, mutableListOf(), dao)
        listeAliments.adapter = adapter

        lifecycleScope.launch {
            dao.getAllAliments().collectLatest { aliments ->
                adapter.updateData(aliments)
                textAucunAliment.visibility =
                    if (aliments.isEmpty()) TextView.VISIBLE else TextView.GONE
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

    private fun dateValide(date: String): Boolean {
        return try {
            LocalDate.parse(date)
            true
        } catch (_: Exception) {
            false
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

                if (nom.isEmpty() || dateExp.isEmpty()) {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (!dateValide(dateExp)) {
                    Toast.makeText(this, "Format date incorrect (YYYY-MM-DD)", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    dao.insert(Aliment(nom = nom, quantite = quantite, dateExpiration = dateExp))
                }
            }
            .setNegativeButton("Annuler", null)
            .create()
            .show()
    }
}
