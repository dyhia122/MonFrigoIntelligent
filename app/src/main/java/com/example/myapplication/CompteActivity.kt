package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

class CompteActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentScreen by remember { mutableStateOf("compte") }
            val context = LocalContext.current

            when (currentScreen) {
                "compte" -> CompteScreen(
                    onNavigateToHome = { currentScreen = "home" },
                    onNavigateToMenu = {
                        val intent = Intent(context, MenuScreen::class.java)
                        context.startActivity(intent)
                    }
                )
                "home" -> {
                    // Ici tu peux afficher un autre composable ou revenir à FrigoActivity
                    // Par exemple, finir cette activité pour retourner à FrigoActivity :
                    LaunchedEffect(Unit) {
                        finish()
                    }
                }
            }
        }
    }
}
