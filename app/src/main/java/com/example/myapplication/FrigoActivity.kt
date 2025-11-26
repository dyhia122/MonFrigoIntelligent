package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

class FrigoActivity : AppCompatActivity() {

    private lateinit var listeAliments: ListView
    private lateinit var textAucunAliment: TextView
    private lateinit var btnAjouter: Button
    private lateinit var dao: AlimentDao
    private lateinit var adapter: AlimentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frigo)

        // Notification channel
        NotificationHelper.createNotificationChannel(this)

        // Permissions Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // DAO et adapter
        dao = FrigoDatabase.getDatabase(this).alimentDao()
        adapter = AlimentAdapter(this, mutableListOf(), dao)

        // Vues
        listeAliments = findViewById(R.id.listeAliments)
        listeAliments.adapter = adapter
        textAucunAliment = findViewById(R.id.textAucunAliment)
        btnAjouter = findViewById(R.id.btnAjouter)

        // Observer les aliments
        lifecycleScope.launch {
            dao.getAllAliments().collectLatest { aliments ->
                adapter.updateData(aliments)
                textAucunAliment.visibility = if (aliments.isEmpty()) TextView.VISIBLE else TextView.GONE
            }
        }

        // Bouton ajouter
        btnAjouter.setOnClickListener { afficherDialogAjout() }

        // Lancer Worker périodique
        lancerVerificationExpiration()

        // Test immédiat notification
        NotificationManagerCompat.from(this).notify(
            123,
            NotificationCompat.Builder(this, "expiration_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Test Notification")
                .setContentText("Si tu vois ceci, les notifications fonctionnent !")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
        )

        // Test du Worker une fois
        val testWorker = OneTimeWorkRequestBuilder<ExpirationWorker>().build()
        WorkManager.getInstance(this).enqueue(testWorker)
    }

    private fun afficherDialogAjout() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24,24,24,24)
        }
        val nomInput = EditText(this).apply { hint = "Nom de l’aliment" }
        val quantiteInput = EditText(this).apply {
            hint = "Quantité"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
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

                lifecycleScope.launch { dao.insert(Aliment(nom = nom, quantite = quantite, dateExpiration = dateExp)) }
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
}
