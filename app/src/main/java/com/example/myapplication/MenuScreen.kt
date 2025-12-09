package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

class MenuScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenuScreenContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreenContent() {
    val context = LocalContext.current
    val blueColor = Color(0xFF2196F3)
    val backgroundColor = Color(0xFFF3F7FB)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FridgeMate",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = blueColor,
                        letterSpacing = 1.2.sp
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Kitchen,
                        contentDescription = "Logo",
                        tint = blueColor,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(28.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuCard(Icons.Default.Home, "Accueil", blueColor) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
                MenuCard(Icons.Default.Kitchen, "Mon Frigo", blueColor) {
                    context.startActivity(Intent(context, FrigoActivity::class.java))
                }
                MenuCard(Icons.Default.Book, "Mes Recettes", blueColor) {
                    context.startActivity(Intent(context, RecettesActivity::class.java))
                }
                MenuCard(Icons.Default.BarChart, "Mes Statistiques", blueColor) {
                    context.startActivity(Intent(context, StatsActivity::class.java))
                }
                MenuCard(Icons.Default.Delete, "Corbeille", blueColor) {
                    context.startActivity(Intent(context, CorbeilleActivity::class.java))
                }
            }
        }
    )
}

@Composable
fun MenuCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, color),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
