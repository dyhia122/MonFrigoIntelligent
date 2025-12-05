package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)  // Ajouté pour éviter l'erreur expérimentale
class StatsActivity : ComponentActivity() {
    private lateinit var dao: AlimentDao
    private lateinit var corbeilleDao: CorbeilleDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = FrigoDatabase.getDatabase(this).alimentDao()
        corbeilleDao = FrigoDatabase.getDatabase(this).corbeilleDao()
        setContent {
            StatsScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)  // Ajouté ici aussi
    @Composable
    fun StatsScreen() {
        var stats by remember { mutableStateOf("Chargement...") }

        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                val aliments = dao.getAllNow()
                val corbeille = corbeilleDao.getAllCorbeilleNow()
                val today = LocalDate.now()
                val perimes = aliments.count { ChronoUnit.DAYS.between(today, LocalDate.parse(it.dateExpiration)) < 0 }
                val presquePerimes = aliments.count { val days = ChronoUnit.DAYS.between(today, LocalDate.parse(it.dateExpiration)); days in 0..4 }
                stats = """
                    Produits dans frigo : ${aliments.size}
                    Produits en corbeille : ${corbeille.size}
                    Périmés : $perimes
                    Presque périmés (≤4j) : $presquePerimes
                """.trimIndent()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mes Statistiques", fontSize = 22.sp, color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
                )
            }
        ) { innerPadding ->
            Text(stats, modifier = Modifier.padding(innerPadding).padding(16.dp), fontSize = 18.sp)
        }
    }
}
