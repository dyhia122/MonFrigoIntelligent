package com.example.myapplication

import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.HomeScreen

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("home") }
    val context = LocalContext.current

    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToMenu = {
                val intent = Intent(context, MenuScreen::class.java)
                context.startActivity(intent)
            },
            onNavigateToCompte = { currentScreen = "compte" }
        )

        "compte" -> CompteScreen(
            onNavigateToHome = { currentScreen = "home" },
            onNavigateToMenu = {
                val intent = Intent(context, MenuScreen::class.java)
                context.startActivity(intent)
            }
        )
    }
}