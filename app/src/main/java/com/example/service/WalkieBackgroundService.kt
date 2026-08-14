package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.local.InstaWireDatabase
import com.example.data.model.UserIdentity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class WalkieBackgroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var backgroundMonitoringJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var toneGenerator: ToneGenerator? = null

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator ?: (getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        toneGenerator = try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 90)
        } catch (e: Exception) {
            null
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "InstaWire::WalkieBackgroundRxWakeLock"
        )?.apply {
            setReferenceCounted(false)
        }

        createNotificationChannel()
        startForegroundServiceNotification("Frequency: 462.5625 MHz • Background Rx Listening Active")
        startBackgroundRadioMonitor()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SERVICE) {
            stopForegroundService()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    private fun startBackgroundRadioMonitor() {
        wakeLock?.acquire(10 * 60 * 1000L /*10 minutes*/)
        backgroundMonitoringJob?.cancel()
        backgroundMonitoringJob = serviceScope.launch {
            val database = InstaWireDatabase.getDatabase(applicationContext, serviceScope)
            val dao = database.instaWireDao()

            while (isActive) {
                // Periodic simulated radio chatter when in background every 30 to 55 seconds
                val nextTransmissionDelay = Random.nextLong(30000L, 55000L)
                delay(nextTransmissionDelay)

                val user = dao.getUserIdentitySync() ?: UserIdentity()
                if (!user.backgroundMonitoringEnabled) {
                    continue
                }

                // Pick an active channel caller or direct contact
                val channelCallers = listOf(
                    "SHADOW-01 (Marcus Vance)" to "462.5625 MHz",
                    "RAVEN-9 (Elena Rostova)" to "462.5625 MHz",
                    "PHANTOM (Ghost Operator)" to "462.6125 MHz",
                    "COMMAND-CHIEF (Chief Miller)" to "462.5625 MHz"
                )
                val (caller, freq) = channelCallers.random()

                if (user.backgroundAudioBeepEnabled) {
                    playBackgroundIncomingTransmissionAudio()
                }

                // Update notification with incoming radio transmission
                updateNotification(
                    title = "Radio Chatter Active: $caller",
                    text = "Frequency: $freq • Live Incoming Audio Heard"
                )

                // Record transmission in database so it appears in logs
                dao.insertTransmission(
                    com.example.data.model.Transmission(
                        senderName = caller,
                        senderNumber = "+1 (888) WIRE-${Random.nextInt(1000, 9999)}",
                        senderCallsign = caller.substringBefore(" "),
                        targetType = "CHANNEL",
                        targetId = "tactical_alpha",
                        durationSeconds = Random.nextDouble(2.2, 4.2).toFloat(),
                        timestamp = System.currentTimeMillis(),
                        isEncrypted = true,
                        isKeyVerified = true,
                        waveAmplitudes = List(8) { Random.nextInt(30, 95) }.joinToString(","),
                        audioEffect = "CRYSTAL_CLEAR"
                    )
                )

                delay(6000L)
                updateNotification(
                    title = "InstaWire PTT Live Transceiver",
                    text = "Frequency: 462.5625 MHz • Background Rx Listening Active"
                )
            }
        }
    }

    private fun playBackgroundIncomingTransmissionAudio() {
        try {
            // Vibrate short double pulse
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 50, 40), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 40, 50, 40), -1)
            }

            // Generate initial squelch chirp tone
            generateTonePcm(listOf(750, 1200), 30)

            // Synthesize voice chatter harmonic bursts
            val harmonicFrequencies = listOf(350, 420, 520, 680, 450, 380)
            generateTonePcm(harmonicFrequencies, 60)

            // Final roger beep
            generateTonePcm(listOf(1400, 1000), 40)
        } catch (e: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 80)
        }
    }

    private fun generateTonePcm(frequencies: List<Int>, durationMsPerTone: Int) {
        try {
            val sampleRate = 22050
            val numSamplesPerTone = (sampleRate * (durationMsPerTone / 1000.0)).toInt()
            val totalSamples = numSamplesPerTone * frequencies.size
            val generatedSnd = ShortArray(totalSamples)

            var offset = 0
            for (freq in frequencies) {
                for (i in 0 until numSamplesPerTone) {
                    val angle = 2.0 * Math.PI * i / (sampleRate.toDouble() / freq)
                    val envelope = when {
                        i < 60 -> i / 60.0
                        i > numSamplesPerTone - 60 -> (numSamplesPerTone - i) / 60.0
                        else -> 1.0
                    }
                    val sample = (sin(angle) * envelope * 0.75 * Short.MAX_VALUE).toInt().toShort()
                    generatedSnd[offset + i] = sample
                }
                offset += numSamplesPerTone
            }

            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(totalSamples * 2)

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()

            // Release after playing
            serviceScope.launch {
                delay(durationMsPerTone * frequencies.size + 100L)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (e: Exception) {
                    // ignore
                }
            }
        } catch (e: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "InstaWire Background Radio Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps InstaWire Walkie-Talkie transceiver listening for incoming voice transmissions in background"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startForegroundServiceNotification(statusText: String) {
        val notification = buildNotification(
            title = "InstaWire PTT Live Transceiver",
            text = statusText
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification(title: String, text: String) {
        val notification = buildNotification(title, text)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(title: String, text: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_speakerphone)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun stopForegroundService() {
        isRunning = false
        backgroundMonitoringJob?.cancel()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        backgroundMonitoringJob?.cancel()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        serviceScope.cancel()
        try {
            toneGenerator?.release()
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "instawire_ptt_background_channel"
        const val NOTIFICATION_ID = 40401
        const val ACTION_STOP_SERVICE = "com.example.service.ACTION_STOP_SERVICE"

        @Volatile
        var isRunning = false

        fun start(context: Context) {
            val intent = Intent(context, WalkieBackgroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, WalkieBackgroundService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.startService(intent)
        }
    }
}
