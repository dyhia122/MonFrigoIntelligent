package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class FrigoActivity : ComponentActivity() {

    private lateinit var dao: AlimentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dao = FrigoDatabase.getDatabase(this).alimentDao()

        setContent {
            FrigoScreen(
                dao = dao,
                onNavigateToMenu = { startActivity(Intent(this, MenuScreen::class.java)) },
                onNavigateToCompte = { startActivity(Intent(this, CompteActivity::class.java)) },
                onScanClick = { startActivity(Intent(this, ScanActivity::class.java)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrigoScreen(
    dao: AlimentDao,
    onNavigateToMenu: () -> Unit,
    onNavigateToCompte: () -> Unit,
    onScanClick: () -> Unit
) {
    var aliments by remember { mutableStateOf(listOf<Aliment>()) }
    val coroutineScope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        dao.getAllAliments().collect { list ->
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text("Scanner") },
                    icon = { Text("📷") },
                    onClick = onScanClick,
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                )
                ExtendedFloatingActionButton(
                    text = { Text("Ajouter") },
                    icon = { Text("+") },
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            }
        },
        content = { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF3F7FB))) {

                if (aliments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aucun aliment pour le moment 🍽️", fontSize = 18.sp, color = Color(0xFF1976D2))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(aliments) { aliment ->
                            AlimentCardWithControls(aliment = aliment, dao = dao)
                        }
                    }
                }

                if (showAddDialog) {
                    var nom by remember { mutableStateOf("") }
                    var quantite by remember { mutableStateOf("1") }
                    var dateExp by remember { mutableStateOf("") }

                    AlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        title = { Text("Ajouter un aliment") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = nom,
                                    onValueChange = { nom = it },
                                    label = { Text("Nom") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = quantite,
                                    onValueChange = { quantite = it.filter { c -> c.isDigit() } },
                                    label = { Text("Quantité") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = dateExp,
                                    onValueChange = { dateExp = it },
                                    label = { Text("Date d'expiration (YYYY-MM-DD)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val q = quantite.toIntOrNull() ?: 1
                                if (nom.isNotBlank() && dateExp.isNotBlank()) {
                                    coroutineScope.launch {
                                        dao.insert(Aliment(nom = nom, quantite = q, dateExpiration = dateExp))
                                    }
                                    showAddDialog = false
                                }
                            }) { Text("Ajouter") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddDialog = false }) { Text("Annuler") }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun AlimentCardWithControls(aliment: Aliment, dao: AlimentDao) {
    val today = LocalDate.now()
    val expiration = LocalDate.parse(aliment.dateExpiration)
    val daysLeft = ChronoUnit.DAYS.between(today, expiration).toInt()
    val coroutineScope = rememberCoroutineScope()

    val barColor = when {
        daysLeft < 0 -> Color.Red
        daysLeft <= 3 -> Color(0xFFFFA500)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(aliment.nom, fontSize = 20.sp, color = Color(0xFF1976D2))
                    Text("Quantité : ${aliment.quantite}", fontSize = 14.sp, color = Color.Gray)
                }
                if (daysLeft < 0) {
                    Text(
                        "PÉRIMÉ",
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Red, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                } else if (daysLeft <= 3) {
                    Text(
                        "Bientôt périmé",
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0xFFFFA500), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color.LightGray, RoundedCornerShape(5.dp))
            ) {
                val progress = when {
                    daysLeft < 0 -> 1f
                    daysLeft >= 10 -> 0.1f
                    else -> 0.1f + (10 - daysLeft) / 10f
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(barColor, RoundedCornerShape(5.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = {
                    coroutineScope.launch { dao.update(aliment.copy(quantite = aliment.quantite + 1)) }
                }) { Icon(Icons.Default.Add, contentDescription = "Augmenter") }

                IconButton(onClick = {
                    if (aliment.quantite > 1) {
                        coroutineScope.launch { dao.update(aliment.copy(quantite = aliment.quantite - 1)) }
                    } else {
                        coroutineScope.launch { dao.delete(aliment) }
                    }
                }) { Icon(Icons.Default.Remove, contentDescription = "Diminuer") }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    coroutineScope.launch { dao.delete(aliment) }
                }) { Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red) }
            }

            Text("Expire le : ${aliment.dateExpiration}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
