package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.InstaWireDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that ensures background audio reception remains active
 * during Android Doze mode and sleep cycles.
 */
class SleepModeAudioWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SleepModeAudioWorker"
        const val WORK_NAME = "instawire_sleep_mode_audio_worker"

        fun schedule(context: Context) {
            try {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val periodicRequest = PeriodicWorkRequestBuilder<SleepModeAudioWorker>(
                    15, TimeUnit.MINUTES
                )
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    periodicRequest
                )
                Log.d(TAG, "SleepModeAudioWorker scheduled successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to schedule SleepModeAudioWorker", e)
            }
        }

        fun cancel(context: Context) {
            try {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
                Log.d(TAG, "SleepModeAudioWorker cancelled")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel SleepModeAudioWorker", e)
            }
        }

        fun isBatteryOptimizationIgnored(context: Context): Boolean {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            return powerManager?.isIgnoringBatteryOptimizations(context.packageName) == true
        }

        fun requestIgnoreBatteryOptimizationsIntent(context: Context): Intent {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            } else {
                Intent(Settings.ACTION_SETTINGS)
            }
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = InstaWireDatabase.getDatabase(context, kotlinx.coroutines.CoroutineScope(Dispatchers.IO))
            val user = database.instaWireDao().getUserIdentitySync()

            if (user?.backgroundMonitoringEnabled == true) {
                // Ensure the background foreground service is running
                if (!WalkieBackgroundService.isRunning) {
                    val serviceIntent = Intent(context, WalkieBackgroundService::class.java)
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error executing SleepModeAudioWorker", e)
            Result.retry()
        }
    }
}
