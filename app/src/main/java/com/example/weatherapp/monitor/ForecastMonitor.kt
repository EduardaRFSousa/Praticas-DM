package com.example.weatherapp.monitor

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.db.fb.FBCity
import java.util.concurrent.TimeUnit

class ForecastMonitor (context: Context) {
    private val wm = WorkManager.getInstance(context)
    @SuppressLint("ServiceCast")
    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as
            NotificationManager
    fun updateCity(city: FBCity) {
        cancelCity(city)
        if (!city.isMonitored) return;
        val inputData = Data.Builder().putString("city", city.name).build()
        val request = PeriodicWorkRequestBuilder<ForecastWorker>(
            repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).setInitialDelay(
            duration = 10, timeUnit = TimeUnit.SECONDS
        ).setInputData(inputData).build()
        wm.enqueueUniquePeriodicWork(city.name!!,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, request )
    }
    fun cancelCity(city: FBCity) {
        wm.cancelUniqueWork(city.name!!)
        nm.cancel(city.name.hashCode())
    }
    fun cancelAll() {
        wm.cancelAllWork()
        nm.cancelAll()
    }
}