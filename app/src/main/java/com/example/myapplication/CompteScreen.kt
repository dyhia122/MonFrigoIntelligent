package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun CompteScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val scope = rememberCoroutineScope()

    var nom by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mon Compte",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Champ Nom
        OutlinedTextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text("Nom d’utilisateur") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Champ Mot de passe
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

        // Bouton Se connecter
        Button(
            onClick = {
                scope.launch {
                    val user = userDao.loginUser(nom, motDePasse)
                    if (user != null) {
                        Toast.makeText(context, "Connexion réussie ✅", Toast.LENGTH_SHORT).show()
                        onNavigateToHome()
                    } else {
                        Toast.makeText(context, "Nom ou mot de passe incorrect ❌", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text("Se connecter")
        }

        // Bouton Créer un compte
        Button(
            onClick = {
                if (nom.isNotBlank() && motDePasse.isNotBlank()) {
                    scope.launch {
                        userDao.insertUser(User(nom = nom, motDePasse = motDePasse))
                        Toast.makeText(context, "Compte créé avec succès ✅", Toast.LENGTH_SHORT).show()
                        onNavigateToHome()
                    }
                } else {
                    Toast.makeText(context, "Veuillez remplir tous les champs ❗", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Créer un compte")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Retour
        TextButton(onClick = { onNavigateToHome() }) {
            Text("← Retour à l'accueil")
        }
    }
}
