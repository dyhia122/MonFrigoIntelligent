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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

fun generateSalt(): String {
    val sr = SecureRandom()
    val salt = ByteArray(16)
    sr.nextBytes(salt)
    return Base64.encodeToString(salt, Base64.NO_WRAP)
}

fun hashPassword(password: String, salt: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = (salt + password).toByteArray(Charsets.UTF_8)
    val digest = md.digest(bytes)
    return Base64.encodeToString(digest, Base64.NO_WRAP)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompteScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val scope = rememberCoroutineScope()

    var nom by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }

    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF64B5F6), Color(0xFF1976D2)),
        start = Offset(0f, 0f),
        end = Offset(1000f, 0f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FridgeMate", fontSize = 22.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* peut rester vide */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Compte", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(gradient).height(64.dp)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF3F7FB)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mon Compte",
                    fontSize = 24.sp,
                    color = Color(0xFF0D47A1),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom d’utilisateur") },
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
                        scope.launch {
                            val user = userDao.getUserByNom(nom)
                            if (user != null) {
                                val hashSaisi = hashPassword(motDePasse, user.salt)
                                if (hashSaisi == user.motDePasseHash) {
                                    Toast.makeText(context, "Connexion réussie ✅", Toast.LENGTH_SHORT).show()
                                    onNavigateToHome() // uniquement ici
                                } else {
                                    Toast.makeText(context, "Nom ou mot de passe incorrect ❌", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Nom ou mot de passe incorrect ❌", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Se connecter", color = Color.White)
                }

                // Bouton Créer un compte
                Button(
                    onClick = {
                        if (nom.isNotBlank() && motDePasse.isNotBlank()) {
                            scope.launch {
                                val salt = generateSalt()
                                val motDePasseHash = hashPassword(motDePasse, salt)
                                userDao.insertUser(User(nom = nom, motDePasseHash = motDePasseHash, salt = salt))
                                Toast.makeText(context, "Compte créé avec succès ✅", Toast.LENGTH_SHORT).show()
                                onNavigateToHome() // uniquement ici
                            }
                        } else {
                            Toast.makeText(context, "Veuillez remplir tous les champs ❗", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Text("Créer un compte", color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = { /* peut rester vide ou revenir à Frigo */ }) {
                    Text("← Retour à l'accueil", color = Color(0xFF0D47A1))
                }
            }
        }
    )
}
