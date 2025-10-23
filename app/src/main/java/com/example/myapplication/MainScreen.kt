package com.example.myapplication

import androidx.compose.runtime.*

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToMenu = { currentScreen = "menu" },
            onNavigateToCompte = { currentScreen = "compte" } // 👈 ajouté ici
        )
        "menu" -> MenuScreen(onNavigateToHome = { currentScreen = "home" })
        "compte" -> CompteScreen(onNavigateToHome = { currentScreen = "home" }) // 👈 nouveau
    }
}
