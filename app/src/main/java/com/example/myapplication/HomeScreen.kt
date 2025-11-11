package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToMenu: () -> Unit = {},
    onNavigateToCompte: () -> Unit = {},
    iconSize: Dp = 36.dp
) {
    val backgroundColor = Color(0xFF2196F3) // bleu

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateToMenu) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCompte) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Compte",
                            tint = Color.White,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                modifier = Modifier.height(64.dp)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                // Nom de l'application sur plusieurs lignes
                Text(
                    text = "Mon\nFrigo\nIntelligent",
                    fontSize = 36.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Image centrale
                Image(
                    painter = painterResource(id = R.drawable.frigo01),
                    contentDescription = "Logo central",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

            }
        }
    )
}

// Preview
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
