package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
class FrigoActivity : ComponentActivity() {

    private lateinit var dao: AlimentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dao = FrigoDatabase.getDatabase(this).alimentDao()

        setContent {
            FrigoScreen(
                onNavigateToMenu = { startActivity(Intent(this, MenuScreen::class.java)) },
                onNavigateToCompte = { startActivity(Intent(this, CompteActivity::class.java)) },
                dao = dao
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrigoScreen(
    onNavigateToMenu: () -> Unit,
    onNavigateToCompte: () -> Unit,
    dao: AlimentDao
) {
    var aliments by remember { mutableStateOf(listOf<Aliment>()) }

    // Collect data from DB
    LaunchedEffect(Unit) {
        dao.getAllAliments().collectLatest { list ->
            aliments = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Frigo", fontSize = 22.sp, color = Color.White) },
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
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { /* ouvrir ScanActivity */ },
                    containerColor = Color(0xFF2196F3),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) { Text("📷", fontSize = 20.sp) }
                FloatingActionButton(
                    onClick = { /* ajouter un aliment */ },
                    containerColor = Color(0xFF2196F3)
                ) { Text("+", fontSize = 20.sp) }
            }
        }
    ) { padding ->
        if (aliments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF3F7FB)),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun aliment 🍽️", fontSize = 18.sp, color = Color(0xFF1976D2))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF3F7FB)),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(aliments) { aliment ->
                    AlimentCard(aliment = aliment)
                }
            }
        }
    }
}

@Composable
fun AlimentCard(aliment: Aliment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier
            .background(Color.White)
            .padding(16.dp)
        ) {
            Text(aliment.nom, fontSize = 18.sp, color = Color(0xFF1976D2))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Quantité : ${aliment.quantite}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Expiration : ${aliment.dateExpiration}", fontSize = 14.sp, color = if (LocalDate.parse(aliment.dateExpiration).isBefore(LocalDate.now())) Color.Red else Color.Gray)
        }
    }
}
