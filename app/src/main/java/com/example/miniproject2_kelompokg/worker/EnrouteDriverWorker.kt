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
            .setOngoing(true) // notif ga bisa di hapus
            .setOnlyAlertOnce(true)

        createChannel()

        // 1. create n display foreground act
        val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIF_ID,
                builder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIF_ID, builder.build())
        }
        setForeground(foregroundInfo)

        // 2. countdown
        for (i in 10 downTo 1) {
            delay(1000L)
            builder.setContentText("Driver akan tiba dalam $i detik...")
            notificationManager.notify(NOTIF_ID, builder.build())
        }

        // 3. Pekerjaan Selesai. Hapus notifikasi "Menuju Lokasi".
        delay(500)
        notificationManager.cancel(NOTIF_ID)

        // succes kasih sinyal biar bisa lanjut
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