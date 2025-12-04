package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CorbeilleActivity : AppCompatActivity() {

    private lateinit var listeCorbeille: ListView
    private lateinit var adapter: CorbeilleAdapter
    private lateinit var corbeilleDao: CorbeilleDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        corbeilleDao = FrigoDatabase.getDatabase(this).corbeilleDao()
        adapter = CorbeilleAdapter(this, mutableListOf(), corbeilleDao)

        setContent {
            CorbeilleScreenWithTopBar(
                onNavigateToMenu = { startActivity(Intent(this, MenuScreen::class.java)) },
                onNavigateToCompte = { startActivity(Intent(this, CompteActivity::class.java)) }
            )
        }

        // Récupération des données depuis la base
        lifecycleScope.launch {
            corbeilleDao.getAllCorbeille().collectLatest { list ->
                adapter.updateData(list)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CorbeilleScreenWithTopBar(
        onNavigateToMenu: () -> Unit,
        onNavigateToCompte: () -> Unit
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Corbeille", fontSize = 22.sp, color = Color.White) },
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
            content = { innerPadding ->
                AndroidView(
                    factory = { context ->
                        val layout = LinearLayout(context).apply {
                            orientation = LinearLayout.VERTICAL
                            setPadding(24, 24, 24, 24)
                            setBackgroundColor(android.graphics.Color.parseColor("#F3F7FB"))
                        }

                        val textAucuneCorbeille = TextView(context).apply {
                            text = "La corbeille est vide"
                            textSize = 18f
                            gravity = android.view.Gravity.CENTER
                            setTextColor(android.graphics.Color.parseColor("#1976D2"))
                            visibility = if (adapter.count == 0) TextView.VISIBLE else TextView.GONE
                        }
                        layout.addView(textAucuneCorbeille)

                        listeCorbeille = ListView(context).apply {
                            adapter = this@CorbeilleActivity.adapter
                            divider = context.getDrawable(android.R.color.darker_gray)
                            dividerHeight = 1
                        }
                        layout.addView(listeCorbeille)
                        layout
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        )
    }
}
