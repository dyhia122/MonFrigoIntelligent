package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompteScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nom by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }

    val primaryBlue = Color(0xFF2196F3)
    val backgroundWhite = Color.White
    val backgroundGray = Color(0xFFF3F7FB)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FridgeMate",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = primaryBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Optionnel */ }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Compte", tint = primaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundWhite)
            )
        },
        containerColor = backgroundGray,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Les champs de connexion
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom d'utilisateur") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = motDePasse,
                    onValueChange = { motDePasse = it },
                    label = { Text("Mot de passe") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                val buttonModifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)

                // Bouton Se connecter
                Button(
                    onClick = {
                        Toast.makeText(context, "Connexion simulée ✅", Toast.LENGTH_SHORT).show()
                        onNavigateToHome()
                    },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text("Se connecter", color = Color.White)
                }

                // Bouton S'inscrire
                Button(
                    onClick = {
                        Toast.makeText(context, "Inscription simulée ✅", Toast.LENGTH_SHORT).show()
                        onNavigateToHome()
                    },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Text("S'inscrire", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { onNavigateToHome() }) {
                    Text("← Retour à l'accueil", color = Color(0xFF0D47A1))
                }
            }
        }
    )
}
