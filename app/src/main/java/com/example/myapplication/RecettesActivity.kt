package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class RecettesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecettesScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecettesScreen() {
    val recettes = listOf(
        "Salade César : Laitue, poulet, parmesan, sauce César.",
        "Pâtes Bolognaise : Tomates, viande hachée, pâtes, fromage.",
        "Omelette : Œufs, fromage, herbes.",
        "Soupe de légumes : Carottes, pommes de terre, oignons.",
        "Smoothie banane : Bananes, lait, miel."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Recettes", fontSize = 22.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(recettes) { recette ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Text(recette, modifier = Modifier.padding(16.dp), fontSize = 16.sp)
                }
            }
        }
    }
}