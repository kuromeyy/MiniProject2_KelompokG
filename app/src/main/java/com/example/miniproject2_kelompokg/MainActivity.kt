package com.example.miniproject2_kelompokg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnFindDriver: Button
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnFindDriver = findViewById(R.id.btnFindDriver)

        // Minta izin notifikasi sekali saat launch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        btnFindDriver.setOnClickListener {
            btnFindDriver.isEnabled = false
            startSearchProcess()
        }
    }

    private fun startSearchProcess() {
        tvStatus.text = "Mencari driver terdekat..."
        showToast("Sedang mencari driver...")

        val searchIntent = Intent(this, SearchDriverService::class.java)
        ContextCompat.startForegroundService(this, searchIntent)

        handler.postDelayed({
            tvStatus.text = "Driver ditemukan!"
            showToast("Driver ditemukan!")
        }, 3000)

        handler.postDelayed({
            val enrouteIntent = Intent(this, EnrouteDriverService::class.java)
            ContextCompat.startForegroundService(this, enrouteIntent)
            tvStatus.text = "Driver menuju lokasi..."
        }, 3500)

        handler.postDelayed({
            tvStatus.text = "Driver telah sampai di lokasi!"
            btnFindDriver.isEnabled = true
        }, 14_000)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
