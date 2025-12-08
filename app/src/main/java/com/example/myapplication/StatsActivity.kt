package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class StatsActivity : ComponentActivity() {

    private lateinit var dao: AlimentDao
    private lateinit var corbeilleDao: CorbeilleDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération des DAOs
        dao = FrigoDatabase.getDatabase(this).alimentDao()
        corbeilleDao = FrigoDatabase.getDatabase(this).corbeilleDao()

        setContent {
            StatsScreen(
                onNavigateToMenu = { startActivity(Intent(this, MenuScreen::class.java)) },
                onNavigateToCompte = { startActivity(Intent(this, CompteActivity::class.java)) },
                dao = dao,
                corbeilleDao = corbeilleDao
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateToMenu: () -> Unit,
    onNavigateToCompte: () -> Unit,
    dao: AlimentDao,
    corbeilleDao: CorbeilleDao
) {
    var nbFrigo by remember { mutableStateOf(0) }
    var nbCorbeille by remember { mutableStateOf(0) }
    var nbPerimes by remember { mutableStateOf(0) }
    var nbExpBientot by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf(false) }

    // Chargement des données dans un coroutine
    LaunchedEffect(Unit) {
        try {
            // Utiliser Dispatchers.IO pour ne pas bloquer le main thread
            val aliments = kotlinx.coroutines.withContext(Dispatchers.IO) { dao.getAllNow() }
            val corbeille = kotlinx.coroutines.withContext(Dispatchers.IO) { corbeilleDao.getAllCorbeilleNow() }
            val today = java.time.LocalDate.now()

            nbFrigo = aliments.size
            nbCorbeille = corbeille.size
            nbPerimes = aliments.count { java.time.temporal.ChronoUnit.DAYS.between(today, java.time.LocalDate.parse(it.dateExpiration)) < 0 }
            nbExpBientot = aliments.count {
                val days = java.time.temporal.ChronoUnit.DAYS.between(today, java.time.LocalDate.parse(it.dateExpiration))
                days in 0..4
            }

        } catch (e: Exception) {
            e.printStackTrace()
            error = true
        }
    }

    val values = listOf(
        nbFrigo.toFloat(),
        nbCorbeille.toFloat(),
        nbPerimes.toFloat(),
        nbExpBientot.toFloat()
    )
    val labels = listOf("Frigo", "Corbeille", "Périmés", "Expire ≤4j")
    val colors = listOf(Color(0xFF42A5F5), Color(0xFFFF7043), Color(0xFFE53935), Color(0xFFFFC107))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Statistiques 📊", color = Color.White) },
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (error) {
                Text(
                    "Erreur lors du chargement des statistiques",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Text(
                    "Résumé des produits",
                    fontSize = 22.sp,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(16.dp)
                )
                StatsBarChart(values = values, labels = labels, colors = colors)
            }
        }
    }
}

@Composable
fun StatsBarChart(
    values: List<Float>,
    labels: List<String>,
    colors: List<Color>
) {
    val maxValue = values.maxOrNull() ?: 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {

        values.forEachIndexed { index, value ->
            val ratio = value / maxValue

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // BARRE
                Box(
                    modifier = Modifier
                        .height((220 * ratio).dp)
                        .width(50.dp)
                        .background(
                            color = colors[index],
                            shape = RoundedCornerShape(14.dp)
                        )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // LABEL
                Text(labels[index], fontSize = 14.sp, color = Color.DarkGray)

                // VALEUR
                Text(
                    "${value.roundToInt()}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
