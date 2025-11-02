package com.example.miniproject2_kelompokg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat

class SearchDriverService : Service() {

    private val CHANNEL_ID = "SearchDriverChannel"
    private val NOTIF_ID = 1
    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Mencari Driver")
            .setContentText("Sedang mencari driver terdekat untuk kamu...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        startForeground(NOTIF_ID, builder.build())

        handler.postDelayed({
            stopForeground(true)
            stopSelf()
        }, 3000)

        return START_STICKY
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Search Driver Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null
}
