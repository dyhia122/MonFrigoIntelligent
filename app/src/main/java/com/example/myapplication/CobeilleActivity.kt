package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CorbeilleActivity : ComponentActivity() {

    private lateinit var corbeilleDao: CorbeilleDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        corbeilleDao = FrigoDatabase.getDatabase(this).corbeilleDao()

        setContent {
            CorbeilleScreen(
                corbeilleDao = corbeilleDao,
                onNavigateToMenu = { startActivity(Intent(this, MenuScreen::class.java)) },
                onNavigateToCompte = { startActivity(Intent(this, CompteActivity::class.java)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorbeilleScreen(
    corbeilleDao: CorbeilleDao,
    onNavigateToMenu: () -> Unit,
    onNavigateToCompte: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val aliments by corbeilleDao.getAllCorbeille().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Corbeille", fontSize = 22.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCompte) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Compte", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF3F7FB))
            ) {
                if (aliments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("La corbeille est vide 🗑️", fontSize = 18.sp, color = Color(0xFF1976D2))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(aliments) { aliment ->
                            CorbeilleCard(aliment = aliment, corbeilleDao = corbeilleDao)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CorbeilleCard(aliment: CorbeilleAliment, corbeilleDao: CorbeilleDao) {
    val coroutineScope = rememberCoroutineScope()

    val today = LocalDate.now()
    val expiration = try { LocalDate.parse(aliment.dateExpiration) } catch (_: Exception) { today }
    val daysLeft = ChronoUnit.DAYS.between(today, expiration).toInt()
    val badgeColor = when {
        daysLeft < 0 -> Color.Red
        daysLeft <= 3 -> Color(0xFFFFA500)
        else -> Color(0xFF4CAF50)
    }
    val badgeText = when {
        daysLeft < 0 -> "Périmé"
        daysLeft <= 3 -> "Bientôt périmé"
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${aliment.nom} x${aliment.quantite}", fontSize = 18.sp, color = Color(0xFF1976D2))
                Text(
                    "Supprimé le ${aliment.dateSuppression} | Exp: ${aliment.dateExpiration}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                if (badgeText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        badgeText,
                        color = Color.White,
                        modifier = Modifier
                            .background(badgeColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    coroutineScope.launch { corbeilleDao.delete(aliment) }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Red, CircleShape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer définitivement", tint = Color.White)
            }
        }
    }
}
