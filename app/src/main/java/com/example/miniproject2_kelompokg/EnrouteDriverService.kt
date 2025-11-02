package com.example.miniproject2_kelompokg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class EnrouteDriverService : Service() {

    private val CHANNEL_ID = "EnrouteDriverChannel"
    private val NOTIF_ID = 2
    private var timer: CountDownTimer? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Driver Menuju Lokasi")
            .setContentText("Driver sedang dalam perjalanan (10 detik)...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        startForeground(NOTIF_ID, builder.build())

        timer = object : CountDownTimer(10_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                builder.setContentText("Driver akan tiba dalam $seconds detik...")
                manager.notify(NOTIF_ID, builder.build())
            }

            override fun onFinish() {
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                builder.setContentText("Driver telah sampai di lokasi!")
                builder.setOngoing(false)
                manager.notify(NOTIF_ID, builder.build())
                stopForeground(true)
                stopSelf()
            }
        }.start()

        return START_STICKY
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Enroute Driver Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null
}
