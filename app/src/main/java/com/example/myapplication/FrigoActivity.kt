package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*

class FrigoActivity : AppCompatActivity() {

    private lateinit var listeAliments: ListView
    private lateinit var textAucunAliment: TextView
    private lateinit var btnAjouter: Button

    private lateinit var db: FrigoDatabase
    private lateinit var adapter: ArrayAdapter<String>

    private val alimentsAffiches = mutableListOf<String>() // noms affichés dans la liste

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frigo)

        // Lier les éléments XML
        listeAliments = findViewById(R.id.listeAliments)
        textAucunAliment = findViewById(R.id.textAucunAliment)
        btnAjouter = findViewById(R.id.btnAjouter)

        // Initialiser la base de données
        db = FrigoDatabase.getDatabase(this)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, alimentsAffiches)
        listeAliments.adapter = adapter

        // Charger les aliments depuis la base
        chargerAliments()

        // Bouton "Ajouter"
        btnAjouter.setOnClickListener {
            afficherDialogAjout()
        }
    }

    private fun afficherDialogAjout() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val editNom = EditText(this).apply { hint = "Nom de l’aliment" }
        val editQuantite = EditText(this).apply {
            hint = "Quantité"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val editDate = EditText(this).apply {
            hint = "Date d’expiration"
            isFocusable = false
        }

        // Ouvre le calendrier quand on clique sur le champ de date
        editDate.setOnClickListener {
            val c = Calendar.getInstance()
            val y = c.get(Calendar.YEAR)
            val m = c.get(Calendar.MONTH)
            val d = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, year, month, day ->
                editDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            }, y, m, d).show()
        }

        layout.addView(editNom)
        layout.addView(editQuantite)
        layout.addView(editDate)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un aliment")
            .setView(layout)
            .setPositiveButton("Ajouter") { _, _ ->
                val nom = editNom.text.toString().trim()
                val quantite = editQuantite.text.toString().toIntOrNull() ?: 0
                val date = editDate.text.toString().trim()

                if (nom.isNotEmpty() && date.isNotEmpty()) {
                    val aliment = Aliment(nom = nom, quantite = quantite, dateExpiration = date)
                    ajouterAliment(aliment)
                } else {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun ajouterAliment(aliment: Aliment) {
        CoroutineScope(Dispatchers.IO).launch {
            db.alimentDao().insert(aliment)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@FrigoActivity, "${aliment.nom} ajouté", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chargerAliments() {
        CoroutineScope(Dispatchers.IO).launch {
            db.alimentDao().getAllAliments().collect { liste ->
                withContext(Dispatchers.Main) {
                    alimentsAffiches.clear()
                    alimentsAffiches.addAll(liste.map {
                        "${it.nom} (${it.quantite}) - expire le ${it.dateExpiration}"
                    })
                    adapter.notifyDataSetChanged()
                    textAucunAliment.visibility =
                        if (alimentsAffiches.isEmpty()) TextView.VISIBLE else TextView.GONE
                }
            }
        }
    }
}
