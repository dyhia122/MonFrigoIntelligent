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
fun MenuScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit // 👈 obligatoire pour revenir
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
            onClick = { onNavigateToHome() },
            modifier = Modifier
                .width(200.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Accueil")
        }

        Button(
            onClick = {
                Toast.makeText(context, "Ouverture de mon frigo", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Mon frigo")
        }

        Button(
            onClick = {
                Toast.makeText(context, "Ouverture de mes recettes", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Mes recettes")
        }

        Button(
            onClick = {
                Toast.makeText(context, "Ouverture de mes statistiques", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Mes statistiques")
        }

        Button(
            onClick = {
                Toast.makeText(context, "Ouverture de ma corbeille", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Corbeille")
        }

        Text(
            text = "Menu",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}
