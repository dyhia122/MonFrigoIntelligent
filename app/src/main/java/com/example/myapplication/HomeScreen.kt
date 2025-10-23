package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToMenu: () -> Unit,
    onNavigateToCompte: () -> Unit // 👈 ajouté ici
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gestion de Frigo Intelligent",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { onNavigateToMenu() },
            modifier = Modifier
                .width(200.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Menu")
        }

        Button(
            onClick = { onNavigateToCompte() }, // 👈 navigation vers l’écran Compte
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Compte")
        }

        Text(
            text = "Accueil",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}
