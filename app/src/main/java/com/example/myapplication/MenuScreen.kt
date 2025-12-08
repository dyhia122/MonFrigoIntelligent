package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
    val backgroundColor = Color(0xFF2196F3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FridgeMate", fontSize = 22.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                val buttonModifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 8.dp)

                OutlinedButton(
                    onClick = { context.startActivity(Intent(context, MainActivity::class.java)) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, Color(0xFF2196F3))
                ) {
                    Text("Accueil")
                }

                OutlinedButton(
                    onClick = { context.startActivity(Intent(context, FrigoActivity::class.java)) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, Color(0xFF2196F3))
                ) {
                    Text("Mon Frigo")
                }

                OutlinedButton(
                    onClick = { context.startActivity(Intent(context, RecettesActivity::class.java)) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, Color(0xFF2196F3))
                ) {
                    Text("Mes Recettes")
                }

                OutlinedButton(
                    onClick = { context.startActivity(Intent(context, StatsActivity::class.java)) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, Color(0xFF2196F3))
                ) {
                    Text("Mes Statistiques")
                }

                OutlinedButton(
                    onClick = { context.startActivity(Intent(context, CorbeilleActivity::class.java)) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, Color(0xFF2196F3))
                ) {
                    Text("Corbeille")
                }
            }
        }
    )
}