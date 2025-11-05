package com.example.miniproject2_kelompokg.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.miniproject2_kelompokg.R // Pastikan import R benar
import kotlinx.coroutines.delay

class SearchDriverWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val CHANNEL_ID = "SearchDriverChannel"
    private val NOTIF_ID = 1
    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        // 1. Buat notif sebagai foreground
        setForeground(createForegroundInfo())

        // 2. mencari selama 3 detik
        delay(3000L)

        // 3. Trigger worker berikutnya
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        createChannel()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Mencari Driver")
            .setContentText("Sedang mencari driver terdekat untuk kamu...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti ikon
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIF_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC // <-- TAMBAHAN KRUSIAL
            )
        } else {
            ForegroundInfo(NOTIF_ID, notification)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Search Driver Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}