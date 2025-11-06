package com.example.miniproject2_kelompokg.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.miniproject2_kelompokg.MainActivity
import com.example.miniproject2_kelompokg.R
import kotlinx.coroutines.delay

class LogTripWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val CHANNEL_ID = "LogTripChannel"
    private val NOTIF_ID = 3 // id biar ga bentrok
    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {

        // 1. simpan secara tidak terlihat
        Log.d("LogTripWorker", "Memulai penyimpanan riwayat perjalanan...")
        delay(2000L) // Jeda 2 detik untuk simulasi simpan ke database
        Log.d("LogTripWorker", "Riwayat disimpan. Menyiapkan notifikasi penyelesaian.")

        // 2. fungsi notif akhir
        createChannel()

        // intentnya biar ke main lagi
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val finalNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Perjalanan Selesai!")
            .setContentText("Riwayat perjalanan Anda telah disimpan. Klik untuk kembali ke aplikasi.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent) //buat clickable
            .setAutoCancel(true) //ilangin notif abis di pence
            .build()

        notificationManager.notify(NOTIF_ID, finalNotification)

        return Result.success()
    }

    // spesisal di buat biar bisa pake importamnce
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Log Perjalanan",
                NotificationManager.IMPORTANCE_HIGH //biar ke pop up kedalem lyar
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}