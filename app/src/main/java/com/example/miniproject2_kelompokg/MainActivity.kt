package com.example.miniproject2_kelompokg

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.miniproject2_kelompokg.worker.EnrouteDriverWorker
import com.example.miniproject2_kelompokg.worker.SearchDriverWorker
import com.example.miniproject2_kelompokg.worker.LogTripWorker

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnFindDriver: Button
    private val workManager by lazy { WorkManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnFindDriver = findViewById(R.id.btnFindDriver)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        btnFindDriver.setOnClickListener {
            btnFindDriver.isEnabled = false
            startSearchProcessWorkManager()
        }
    }

    private fun startSearchProcessWorkManager() {
        // 1. perizinan setiap worker
        val searchWorkRequest = OneTimeWorkRequestBuilder<SearchDriverWorker>().build()
        val enrouteWorkRequest = OneTimeWorkRequestBuilder<EnrouteDriverWorker>().build()

        val logTripRequest = OneTimeWorkRequestBuilder<LogTripWorker>().build()

        // 2. Rantai (Chain) pekerjaan:
        workManager.beginWith(searchWorkRequest)
            .then(enrouteWorkRequest)
            .then(logTripRequest)
            .enqueue()

        // 3. Amati (Observe) pekerjaan untuk memperbarui UI
        workManager.getWorkInfoByIdLiveData(searchWorkRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.RUNNING -> {
                            tvStatus.text = "Mencari driver terdekat..."
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            tvStatus.text = "Driver ditemukan!"
                            showToast("Driver ditemukan!")
                        }
                        else -> {}
                    }
                }
            })

        workManager.getWorkInfoByIdLiveData(enrouteWorkRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.RUNNING -> {
                            tvStatus.text = "Driver menuju lokasi..."
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            tvStatus.text = "Driver telah sampai di lokasi!"
                            btnFindDriver.isEnabled = true
                        }
                        WorkInfo.State.FAILED -> {
                            tvStatus.text = "Pencarian gagal. Coba lagi."
                            btnFindDriver.isEnabled = true
                        }
                        else -> {}
                    }
                }
            })

        workManager.getWorkInfoByIdLiveData(logTripRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // Kita tidak perlu update UI, tapi kita bisa lihat di Logcat
                    Log.d("MainActivity", "LogTripWorker Selesai. Riwayat disimpan.")
                }
            })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}