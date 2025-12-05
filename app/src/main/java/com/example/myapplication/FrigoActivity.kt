package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit
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
import com.example.myapplication.CompteActivity

class FrigoActivity : AppCompatActivity() {

    private lateinit var listeAliments: ListView
    private lateinit var textAucunAliment: TextView
    private lateinit var btnAjouter: Button
    private lateinit var btnScanner: Button
    private lateinit var dao: AlimentDao
    private lateinit var corbeilleDao: CorbeilleDao
    private lateinit var adapter: AlimentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // DAO et adapter
        dao = FrigoDatabase.getDatabase(this).alimentDao()
        corbeilleDao = FrigoDatabase.getDatabase(this).corbeilleDao()
        adapter = AlimentAdapter(this, mutableListOf(), dao, corbeilleDao)

        // Permissions notifications Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Notification channel
        NotificationHelper.createNotificationChannel(this)

        // Utilisation de Compose pour le TopAppBar
        setContent {
            FrigoScreenWithTopBar(
                onNavigateToMenu = { startActivity(Intent(this, MenuScreen::class.java)) },
                onNavigateToCompte = { startActivity(Intent(this, CompteActivity::class.java)) }
            )
        }

        // Observer les aliments
        lifecycleScope.launch {
            dao.getAllAliments().collectLatest { aliments ->
                adapter.updateData(aliments)
                // Correction : masquer le message si aliments présents
                textAucunAliment.visibility = if (aliments.isEmpty()) TextView.VISIBLE else TextView.GONE
            }
        }

        // Lancer Worker périodique
        lancerVerificationExpiration()
        val testWorker = OneTimeWorkRequestBuilder<ExpirationWorker>().build()
        WorkManager.getInstance(this).enqueue(testWorker)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200 && resultCode == RESULT_OK) {
            val scannedCode = data?.getStringExtra("scanned_code") ?: run {
                Toast.makeText(this, "Aucun code reçu", Toast.LENGTH_SHORT).show()
                return
            }

            lifecycleScope.launch {
                val produit = rechercherProduitParCode(scannedCode)
                if (produit == null) {
                    runOnUiThread {
                        AlertDialog.Builder(this@FrigoActivity)
                            .setTitle("Produit inconnu")
                            .setMessage("Aucun produit trouvé pour le code $scannedCode.\nSouhaites-tu l'ajouter manuellement ?")
                            .setPositiveButton("Oui") { _, _ -> afficherDialogAjoutAvecNom("") }
                            .setNegativeButton("Non", null)
                            .show()
                    }
                } else {
                    dao.insert(
                        Aliment(
                            nom = produit.nom,
                            quantite = 1,
                            dateExpiration = produit.dateExpiration
                        )
                    )
                    runOnUiThread {
                        Toast.makeText(this@FrigoActivity, "${produit.nom} ajouté au frigo", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun afficherDialogAjout() {
        afficherDialogAjoutAvecNom(null)
    }

    private fun afficherDialogAjoutAvecNom(prefillName: String?) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }
        val nomInput = EditText(this).apply { hint = "Nom de l’aliment"; setText(prefillName ?: "") }
        val quantiteInput = EditText(this).apply { hint = "Quantité"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val dateInput = EditText(this).apply { hint = "Date d’expiration (YYYY-MM-DD)" }

        layout.addView(nomInput)
        layout.addView(quantiteInput)
        layout.addView(dateInput)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un aliment")
            .setView(layout)
            .setPositiveButton("Ajouter") { _, _ ->
                val nom = nomInput.text.toString().trim()
                val quantite = quantiteInput.text.toString().toIntOrNull() ?: 1
                val dateExp = dateInput.text.toString().trim()
                if (nom.isEmpty() || dateExp.isEmpty() || !dateValide(dateExp)) {
                    Toast.makeText(this, "Remplir correctement tous les champs", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                lifecycleScope.launch {
                    dao.insert(Aliment(nom = nom, quantite = quantite, dateExpiration = dateExp))
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun dateValide(date: String): Boolean {
        return try { LocalDate.parse(date); true } catch (_: Exception) { false }
    }

    private fun lancerVerificationExpiration() {
        val request = PeriodicWorkRequestBuilder<ExpirationWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "expiration_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    data class ProduitScanne(val nom: String, val dateExpiration: String)

    private suspend fun rechercherProduitParCode(code: String): ProduitScanne? {
        return when (code.trim()) {
            "3017620422003" -> ProduitScanne("Nutella", LocalDate.now().plusMonths(6).toString())
            "3229820783223" -> ProduitScanne("Lait demi-écrémé", LocalDate.now().plusDays(7).toString())
            "036000291452" -> ProduitScanne("Céréales aux fruits", LocalDate.now().plusMonths(3).toString())
            else -> null
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FrigoScreenWithTopBar(onNavigateToMenu: () -> Unit, onNavigateToCompte: () -> Unit) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Frigo", fontSize = 22.sp, color = Color.White) },
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
                Column {
                    FloatingActionButton(
                        onClick = { val intent = Intent(this@FrigoActivity, ScanActivity::class.java); startActivityForResult(intent, 200) },
                        containerColor = Color(0xFF2196F3),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text("📷", fontSize = 20.sp)
                    }
                    FloatingActionButton(
                        onClick = { afficherDialogAjout() },
                        containerColor = Color(0xFF2196F3)
                    ) {
                        Text("+", fontSize = 20.sp)
                    }
                }
            },
            content = { innerPadding ->
                AndroidView(
                    factory = { context ->
                        val layout = LinearLayout(context).apply {
                            orientation = LinearLayout.VERTICAL
                            setPadding(24, 24, 24, 24)
                            setBackgroundColor(android.graphics.Color.parseColor("#F3F7FB"))
                        }

                        textAucunAliment = TextView(context).apply {
                            text = "Aucun aliment pour le moment 🍽️"
                            textSize = 18f
                            gravity = android.view.Gravity.CENTER
                            setTextColor(android.graphics.Color.parseColor("#1976D2"))
                            visibility = if (adapter.count == 0) TextView.VISIBLE else TextView.GONE
                        }
                        layout.addView(textAucunAliment)

                        listeAliments = ListView(context).apply {
                            adapter = this@FrigoActivity.adapter
                            divider = context.getDrawable(android.R.color.darker_gray)
                            dividerHeight = 1
                        }
                        layout.addView(listeAliments)

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