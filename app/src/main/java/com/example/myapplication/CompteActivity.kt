package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CompteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompteScreen(
                onNavigateToHome = { finish() }, // Retour à l'écran précédent
                onNavigateToMenu = { startActivity(Intent(this, MenuScreen::class.java)) } // Ouvre le menu
            )
        }
    }
}
