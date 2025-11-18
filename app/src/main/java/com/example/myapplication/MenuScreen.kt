package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MenuScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(ContextCompat.getColor(this@MenuScreen, android.R.color.white))
            setPadding(40, 80, 40, 40)
        }

        val titre = TextView(this).apply {
            text = "FridgeMate"
            textSize = 26f
            setTextColor(ContextCompat.getColor(this@MenuScreen, android.R.color.holo_blue_dark))
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setPadding(0, 0, 0, 40)
        }
        layout.addView(titre)

        fun creerBouton(texte: String, couleur: Int): Button {
            return Button(this).apply {
                text = texte
                setBackgroundColor(ContextCompat.getColor(this@MenuScreen, couleur))
                setTextColor(ContextCompat.getColor(this@MenuScreen, android.R.color.white))
                textSize = 18f
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            }
        }

        val btnAccueil = creerBouton("Accueil", android.R.color.holo_blue_dark)
        val btnFrigo = creerBouton("Mon Frigo", android.R.color.holo_blue_light)
        val btnRecettes = creerBouton("Mes Recettes", android.R.color.holo_blue_dark)
        val btnStats = creerBouton("Mes Statistiques", android.R.color.holo_blue_light)
        val btnCorbeille = creerBouton("Corbeille", android.R.color.holo_blue_dark)

        layout.addView(btnAccueil)
        layout.addView(btnFrigo)
        layout.addView(btnRecettes)
        layout.addView(btnStats)
        layout.addView(btnCorbeille)

        // === 🏠 Accueil : ouvre HomeScreen ===
        btnAccueil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Accueil ouvert", Toast.LENGTH_SHORT).show()
        }

        // === 🧊 Mon Frigo : ouvre FrigoActivity ===
        btnFrigo.setOnClickListener {
            val intent = Intent(this, FrigoActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Ouverture du frigo...", Toast.LENGTH_SHORT).show()
        }

        // === Les autres ===
        btnRecettes.setOnClickListener {
            Toast.makeText(this, "Ouverture des recettes...", Toast.LENGTH_SHORT).show()
        }

        btnStats.setOnClickListener {
            Toast.makeText(this, "Ouverture des statistiques...", Toast.LENGTH_SHORT).show()
        }

        btnCorbeille.setOnClickListener {
            val intent = Intent(this, CorbeilleActivity::class.java)
            startActivity(intent)
            // Optionnel : toast
            Toast.makeText(this, "Ouverture de la corbeille...", Toast.LENGTH_SHORT).show()
        }


        setContentView(layout)
    }
}
