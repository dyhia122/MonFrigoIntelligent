package com.example.myapplication

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ExpirationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val dao = FrigoDatabase.getDatabase(applicationContext).alimentDao()
            val aliments = dao.getAllNow()  // Méthode DAO pour récupérer tous les aliments
            val today = LocalDate.now()

            aliments.forEach { aliment ->
                val exp = LocalDate.parse(aliment.dateExpiration)
                val daysLeft = ChronoUnit.DAYS.between(today, exp)

                if (daysLeft in 0..7) {
                    sendNotification(
                        title = "Expiration proche",
                        message = "${aliment.nom} expire dans $daysLeft jours !"
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(applicationContext, "expiration_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(applicationContext)
            .notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
