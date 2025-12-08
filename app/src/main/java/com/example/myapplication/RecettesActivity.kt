package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

class RecettesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecettesScreen(
                onNavigateToMenu = { startActivity(Intent(this, MenuScreen::class.java)) },
                onNavigateToCompte = { startActivity(Intent(this, CompteActivity::class.java)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecettesScreen(onNavigateToMenu: () -> Unit, onNavigateToCompte: () -> Unit) {
    val recettes = listOf(
        Recette("Salade César", "Laitue, poulet, parmesan, sauce César.", "Mélanger les ingrédients et servir frais.", R.drawable.recette1),
        Recette("Pâtes Bolognaise", "Tomates, viande hachée, pâtes, fromage.", "Cuire les pâtes, préparer la sauce et mélanger.", R.drawable.recette2),
        Recette("Omelette", "Œufs, fromage, herbes.", "Battre les œufs, ajouter ingrédients et cuire.", R.drawable.recette3),
        Recette("Soupe de légumes", "Carottes, pommes de terre, oignons.", "Faire bouillir les légumes et mixer.", R.drawable.recette4),
        Recette("Smoothie banane", "Bananes, lait, miel.", "Mixer tous les ingrédients.", R.drawable.recette5),
        Recette("Quiche Lorraine", "Pâte brisée, lardons, crème, œufs.", "Préparer la garniture et cuire au four.", R.drawable.recette6),
        Recette("Ratatouille", "Aubergines, courgettes, tomates, poivrons.", "Cuire les légumes ensemble.", R.drawable.recette7),
        Recette("Tarte aux pommes", "Pâte, pommes, sucre.", "Étaler la pâte, ajouter les pommes et cuire.", R.drawable.recette8),
        Recette("Chili con carne", "Viande, haricots, tomates, épices.", "Cuire lentement tous les ingrédients.", R.drawable.recette9),
        Recette("Risotto aux champignons", "Riz, champignons, bouillon, parmesan.", "Cuire le riz en ajoutant le bouillon progressivement.", R.drawable.recette10),
        Recette("Poulet curry", "Poulet, lait de coco, curry, légumes.", "Faire revenir le poulet et ajouter les épices.", R.drawable.recette11),
        Recette("Salade de fruits", "Fruits variés, miel.", "Couper les fruits et mélanger avec miel.", R.drawable.recette12),
        Recette("Lasagnes", "Pâtes, viande, béchamel, fromage.", "Alterner couches et cuire au four.", R.drawable.recette13),
        Recette("Soupe à l'oignon", "Oignons, bouillon, pain, fromage.", "Caraméliser les oignons et servir avec gratin.", R.drawable.recette14),
        Recette("Crêpes", "Farine, œufs, lait, sucre.", "Préparer la pâte et cuire à la poêle.", R.drawable.recette15)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Recettes 🍳", fontSize = 22.sp, color = Color.White) },
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
            FloatingActionButton(
                onClick = { /* Ajouter logique pour ajouter recette */ },
                containerColor = Color(0xFF2196F3)
            ) {
                Text("+", fontSize = 20.sp, color = Color.White)
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(recettes) { recette ->
                RecetteCard(recette)
            }
        }
    }
}

@Composable
fun RecetteCard(recette: Recette) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = recette.imageRes),
                    contentDescription = recette.nom,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(recette.nom, fontSize = 18.sp, color = Color(0xFF1976D2), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(if (expanded) "Masquer Ingrédients" else "Voir Ingrédients", color = Color.White)
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ingrédients : ${recette.ingredients}", fontSize = 14.sp)
                Text("Instructions : ${recette.instructions}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

data class Recette(val nom: String, val ingredients: String, val instructions: String, val imageRes: Int)