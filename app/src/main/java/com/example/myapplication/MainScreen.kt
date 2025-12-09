package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("home") }
    val context = LocalContext.current
    val primaryBlue = Color(0xFF2196F3)
    val backgroundWhite = Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Kitchen,
                            contentDescription = "Logo Fridge",
                            tint = primaryBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "FridgeMate",
                            fontSize = 26.sp,
                            color = primaryBlue,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, MenuScreen::class.java))
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = primaryBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { currentScreen = "compte" }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Compte", tint = primaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundWhite)
            )
        },
        containerColor = backgroundWhite,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundWhite),
                contentAlignment = Alignment.Center
            ) {
                when (currentScreen) {
                    "home" -> HomeScreen()
                    "compte" -> CompteScreen(
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToMenu = {
                            context.startActivity(Intent(context, MenuScreen::class.java))
                        }
                    )
                }
            }
        }
    )
}
