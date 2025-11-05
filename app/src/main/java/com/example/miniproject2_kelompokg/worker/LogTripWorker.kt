package com.example.miniproject2_kelompokg.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class LogTripWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    // Ini adalah "Background Worker" murni.
    // Ia tidak memanggil setForeground() dan tidak menampilkan notifikasi.

    override suspend fun doWork(): Result {
        // Simulasikan tugas "diam-diam" (misalnya menyimpan ke database)
        delay(1000L)

        Log.d("LogTripWorker", "====== TRIP SELESAI ======")
        Log.d("LogTripWorker", "Riwayat perjalanan berhasil disimpan ke database.")
        Log.d("LogTripWorker", "==========================")

        return Result.success()
    }
}