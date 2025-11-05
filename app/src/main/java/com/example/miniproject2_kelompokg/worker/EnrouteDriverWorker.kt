package com.example.miniproject2_kelompokg.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.miniproject2_kelompokg.MainActivity
import com.example.miniproject2_kelompokg.R
import kotlinx.coroutines.delay

class EnrouteDriverWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val CHANNEL_ID = "EnrouteDriverChannel"
    private val NOTIF_ID = 2
    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Driver Menuju Lokasi")
            .setContentText("Driver sedang dalam perjalanan (10 detik)...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        createChannel()

        // 1. membuat foreground info
        val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIF_ID,
                builder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIF_ID, builder.build())
        }

        // melakuakn delay
        setForeground(foregroundInfo)

        // 2. mulai countdown
        for (i in 10 downTo 1) {
            delay(1000L)
            builder.setContentText("Driver akan tiba dalam $i detik...")
            notificationManager.notify(NOTIF_ID, builder.build())
        }

        // 3. Pekerjaan Selesai. Buat notifikasi akhir yang bisa diklik.
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val finalNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Driver Tiba!")
            .setContentText("Driver telah sampai di lokasi Anda. Klik untuk membuka aplikasi.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIF_ID, finalNotification)
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Enroute Driver Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}