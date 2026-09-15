package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.data.model.NoaaWeatherStation
import com.example.data.model.PublicScannerFeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class AudioEngine(private val context: Context, private val scope: CoroutineScope) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 85)
    } catch (e: Exception) {
        null
    }

    private val _liveAudioAmplitude = MutableStateFlow(0f)
    val liveAudioAmplitude: StateFlow<Float> = _liveAudioAmplitude.asStateFlow()

    private val _spectrumBars = MutableStateFlow(List(16) { 0.1f })
    val spectrumBars: StateFlow<List<Float>> = _spectrumBars.asStateFlow()

    private val _isPlayingIncoming = MutableStateFlow(false)
    val isPlayingIncoming: StateFlow<Boolean> = _isPlayingIncoming.asStateFlow()

    // Live Scanner & NOAA Weather Stream states
    private val _isScannerPlaying = MutableStateFlow(false)
    val isScannerPlaying: StateFlow<Boolean> = _isScannerPlaying.asStateFlow()

    private val _activeScannerId = MutableStateFlow<String?>(null)
    val activeScannerId: StateFlow<String?> = _activeScannerId.asStateFlow()

    private val _isNoaaPlaying = MutableStateFlow(false)
    val isNoaaPlaying: StateFlow<Boolean> = _isNoaaPlaying.asStateFlow()

    private val _activeNoaaId = MutableStateFlow<String?>(null)
    val activeNoaaId: StateFlow<String?> = _activeNoaaId.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var scannerSimulationJob: Job? = null
    private var noaaSimulationJob: Job? = null

    val coreRecordingService = CoreAudioRecordingService(context, scope)
    val isNoiseSuppressionActive: StateFlow<Boolean> = coreRecordingService.isNoiseSuppressionActive

    private var isRecordingMic = false
    private var recordingJob: Job? = null
    private var animationJob: Job? = null

    init {
        startIdleWaveAnimation()
    }

    private fun startIdleWaveAnimation() {
        animationJob?.cancel()
        animationJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                if (com.example.service.AudioCaptureForegroundService.isServiceRunning) {
                    val state = com.example.service.AudioCaptureForegroundService.captureState.value
                    _spectrumBars.value = state.spectrumBars
                    _liveAudioAmplitude.value = state.amplitude
                    delay(45)
                } else if (!isRecordingMic && !_isPlayingIncoming.value && !_isScannerPlaying.value && !_isNoaaPlaying.value) {
                    val idleBars = List(16) { index ->
                        0.08f + (0.05f * sin((System.currentTimeMillis() / 250.0) + index).toFloat()).coerceAtLeast(0f)
                    }
                    _spectrumBars.value = idleBars
                    _liveAudioAmplitude.value = 0.05f
                    delay(50)
                } else {
                    delay(30)
                }
            }
        }
    }

    fun triggerPttPress(
        enableChirp: Boolean,
        enableHaptic: Boolean = true,
        soundProfile: com.example.data.model.PttSoundProfile = com.example.data.model.PttSoundProfile.NEXTEL_TACTICAL
    ) {
        if (enableHaptic) {
            triggerHapticPttPress()
        }
        if (enableChirp) {
            scope.launch(Dispatchers.IO) {
                playProfilePressSound(soundProfile)
            }
        }
        startMicrophoneCapture()
    }

    fun triggerPttRelease(
        enableRogerBeep: Boolean,
        enableHaptic: Boolean = true,
        soundProfile: com.example.data.model.PttSoundProfile = com.example.data.model.PttSoundProfile.NEXTEL_TACTICAL,
        onFinished: (durationSec: Float, waveAmps: String) -> Unit
    ) {
        if (enableHaptic) {
            triggerHapticPttRelease()
        }
        val (duration, recordedAmps) = stopMicrophoneCapture()
        
        if (enableRogerBeep) {
            scope.launch(Dispatchers.IO) {
                playProfileReleaseSound(soundProfile)
            }
        }
        onFinished(duration, recordedAmps)
    }

    private fun startMicrophoneCapture() {
        isRecordingMic = true
        coreRecordingService.startRecording()

        recordingJob?.cancel()
        recordingJob = scope.launch(Dispatchers.Default) {
            while (isActive && isRecordingMic) {
                _liveAudioAmplitude.value = coreRecordingService.amplitude.value
                _spectrumBars.value = coreRecordingService.spectrumBars.value
                delay(30)
            }
        }
    }

    private fun stopMicrophoneCapture(): Pair<Float, String> {
        isRecordingMic = false
        recordingJob?.cancel()
        return coreRecordingService.stopRecording()
    }

    fun playTransmissionAudio(durationSec: Float, waveAmps: String, enableSquelch: Boolean = true) {
        scope.launch(Dispatchers.IO) {
            _isPlayingIncoming.value = true
            vibrateShort(25)

            // Play incoming squelch burst
            if (enableSquelch) {
                playSquelchBurst()
            }

            // Zero-lag direct PCM playback if recorded audio is available
            val recordedPcm = coreRecordingService.lastRecordedAudio.value
            if (recordedPcm != null && recordedPcm.isNotEmpty()) {
                playRawPcmDirect(recordedPcm, CoreAudioRecordingService.SAMPLE_RATE_HZ)
            } else {
                playSynthesizedVoiceTransmission(durationSec, waveAmps)
            }

            // Play Roger Beep on end of transmission
            playRogerBeep()
            _isPlayingIncoming.value = false
            _liveAudioAmplitude.value = 0.05f
        }
    }

    private fun playRawPcmDirect(pcmBytes: ByteArray, sampleRate: Int) {
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(1024)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
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
                .setBufferSizeInBytes(minBuf)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                    }
                }
                .build()

            track.play()
            val chunkSize = 640
            var offset = 0
            while (offset < pcmBytes.size && _isPlayingIncoming.value) {
                val bytesToWrite = (pcmBytes.size - offset).coerceAtMost(chunkSize)
                track.write(pcmBytes, offset, bytesToWrite)
                offset += bytesToWrite

                // Update live visualizer
                val amp = 0.4f + (Random.nextFloat() * 0.5f)
                _liveAudioAmplitude.value = amp
                _spectrumBars.value = List(16) { i ->
                    (amp * (0.3f + 0.7f * sin((offset / 320.0) + (i * 0.5)).toFloat())).coerceIn(0.1f, 1f)
                }
            }
            try {
                track.stop()
                track.release()
            } catch (e: Exception) {
                // ignore
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error in zero-lag direct PCM playback", e)
        }
    }

    private suspend fun playSynthesizedVoiceTransmission(durationSec: Float, waveAmps: String) {
        val totalSteps = (durationSec * 20).toInt().coerceAtLeast(10)
        val ampValues = waveAmps.split(",").mapNotNull { it.trim().toFloatOrNull() }

        for (step in 0 until totalSteps) {
            val baseAmp = if (ampValues.isNotEmpty()) {
                (ampValues[step % ampValues.size] / 100f).coerceIn(0.2f, 1f)
            } else {
                Random.nextFloat() * 0.8f + 0.2f
            }
            _liveAudioAmplitude.value = baseAmp
            _spectrumBars.value = List(16) { i ->
                (baseAmp * (0.3f + 0.7f * sin((step * 0.5) + i).toFloat())).coerceIn(0.1f, 1f)
            }
            delay(50)
        }
    }

    // ==========================================
    // BROADCASTIFY LIVE PUBLIC SAFETY SCANNER
    // ==========================================

    fun playLiveScanner(feed: PublicScannerFeed) {
        stopNoaaWeatherRadio()
        stopScanner()

        _isScannerPlaying.value = true
        _activeScannerId.value = feed.id
        vibrateShort(30)

        // Try streaming via Android MediaPlayer
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.parse(feed.streamUrl))
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnErrorListener { _, _, _ ->
                    // Fallback to synthesized scanner RF background audio
                    startScannerSynthesizedAudio()
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            startScannerSynthesizedAudio()
        }

        // Start scanner audio animation & occasional radio chatter squelch simulation
        startScannerSynthesizedAudio()
    }

    private fun startScannerSynthesizedAudio() {
        scannerSimulationJob?.cancel()
        scannerSimulationJob = scope.launch(Dispatchers.Default) {
            // Play initial trunked scanner beep
            try {
                generateTonePcm(listOf(850, 1050), 40)
            } catch (e: Exception) {
                // ignore
            }

            while (isActive && _isScannerPlaying.value) {
                // Generate dynamic realistic scanner amplitude burst
                val isBurst = Random.nextFloat() > 0.35f
                val baseAmp = if (isBurst) Random.nextFloat() * 0.7f + 0.3f else 0.15f
                _liveAudioAmplitude.value = baseAmp
                _spectrumBars.value = List(16) { i ->
                    val v = (baseAmp * (0.2f + 0.8f * sin((System.currentTimeMillis() / 150.0) + (i * 0.6)).toFloat())).coerceIn(0.1f, 1f)
                    v
                }
                delay(60)

                // Occasionally play realistic radio squelch tail
                if (Random.nextInt(100) < 4) {
                    playSquelchBurst()
                }
            }
        }
    }

    fun stopScanner() {
        _isScannerPlaying.value = false
        _activeScannerId.value = null
        scannerSimulationJob?.cancel()
        scannerSimulationJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            // ignore
        }
    }

    // ==========================================
    // NATIONAL WEATHER SERVICE (NOAA) RADIO
    // ==========================================

    fun playNoaaWeatherRadio(station: NoaaWeatherStation) {
        stopScanner()
        stopNoaaWeatherRadio()

        _isNoaaPlaying.value = true
        _activeNoaaId.value = station.id
        vibrateShort(30)

        // Try streaming NOAA via MediaPlayer
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.parse(station.streamUrl))
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnErrorListener { _, _, _ ->
                    startNoaaSynthesizedBroadcast(station)
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            startNoaaSynthesizedBroadcast(station)
        }

        startNoaaSynthesizedBroadcast(station)
    }

    private fun startNoaaSynthesizedBroadcast(station: NoaaWeatherStation) {
        noaaSimulationJob?.cancel()
        noaaSimulationJob = scope.launch(Dispatchers.Default) {
            // Play NOAA automated carrier tone
            try {
                generateTonePcm(listOf(1050), 75)
            } catch (e: Exception) {
                // ignore
            }

            while (isActive && _isNoaaPlaying.value) {
                val amp = 0.35f + (Random.nextFloat() * 0.45f)
                _liveAudioAmplitude.value = amp
                _spectrumBars.value = List(16) { i ->
                    (amp * (0.4f + 0.6f * sin((System.currentTimeMillis() / 200.0) + (i * 0.4)).toFloat())).coerceIn(0.12f, 0.95f)
                }
                delay(70)
            }
        }
    }

    fun stopNoaaWeatherRadio() {
        _isNoaaPlaying.value = false
        _activeNoaaId.value = null
        noaaSimulationJob?.cancel()
        noaaSimulationJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            // ignore
        }
    }

    fun playNoaa1050HzAlertTone() {
        vibrateShort(250)
        scope.launch(Dispatchers.IO) {
            try {
                // 1050 Hz SAME Emergency Alert Siren (National Weather Service standard)
                generateTonePcm(listOf(1050, 1050, 1050, 1050), 300)
            } catch (e: Exception) {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 1000)
            }
        }
    }

    fun playMaydayDistressSiren() {
        vibrateShort(500)
        scope.launch(Dispatchers.IO) {
            try {
                // International Maritime Mayday Dual-Tone Alarm (2200 Hz & 1300 Hz)
                generateTonePcm(listOf(2200, 1300, 2200, 1300, 2200, 1300), 200)
            } catch (e: Exception) {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 1200)
            }
        }
    }

    fun playProfilePressSound(profile: com.example.data.model.PttSoundProfile) {
        try {
            generateTonePcm(profile.pressFrequencies, profile.pressDurationMs)
        } catch (e: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        }
    }

    fun playProfileReleaseSound(profile: com.example.data.model.PttSoundProfile) {
        try {
            generateTonePcm(profile.releaseFrequencies, profile.releaseDurationMs)
        } catch (e: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 90)
        }
    }

    fun previewSound(profile: com.example.data.model.PttSoundProfile, isPress: Boolean) {
        vibrateShort(30)
        scope.launch(Dispatchers.IO) {
            if (isPress) {
                playProfilePressSound(profile)
            } else {
                playProfileReleaseSound(profile)
            }
        }
    }

    fun playChirpPress(profile: com.example.data.model.PttSoundProfile? = null) {
        if (profile != null) {
            playProfilePressSound(profile)
        } else {
            playNextelOpenChirp()
        }
    }

    fun playChirpRelease(profile: com.example.data.model.PttSoundProfile? = null) {
        if (profile != null) {
            playProfileReleaseSound(profile)
        } else {
            playRogerBeep()
        }
    }

    fun playTransmissionNoiseBurst() {
        playSquelchBurst()
    }

    fun playNextelOpenChirp() {
        try {
            // Classic TiKL/Nextel triple chirp sound (high frequencies)
            generateTonePcm(listOf(1200, 1800, 2400), 28)
        } catch (e: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        }
    }

    fun playRogerBeep() {
        try {
            // Classic military walkie roger beep: 880 Hz then 1760 Hz
            generateTonePcm(listOf(1400, 1000), 45)
        } catch (e: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 90)
        }
    }

    fun playSquelchBurst() {
        try {
            generateTonePcm(listOf(750), 30)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun playKeyVerifiedTone() {
        try {
            generateTonePcm(listOf(900, 1200, 1600, 2100), 35)
        } catch (e: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 100)
        }
    }

    private fun generateTonePcm(frequencies: List<Int>, durationMsPerTone: Int) {
        val sampleRate = 22050
        val numSamplesPerTone = (sampleRate * (durationMsPerTone / 1000.0)).toInt()
        val totalSamples = numSamplesPerTone * frequencies.size
        val generatedSnd = ShortArray(totalSamples)

        var offset = 0
        for (freq in frequencies) {
            for (i in 0 until numSamplesPerTone) {
                val angle = 2.0 * Math.PI * i / (sampleRate.toDouble() / freq)
                // Sine wave with soft attack & decay envelope
                val envelope = when {
                    i < 80 -> i / 80.0
                    i > numSamplesPerTone - 80 -> (numSamplesPerTone - i) / 80.0
                    else -> 1.0
                }
                generatedSnd[offset + i] = (sin(angle) * 30000 * envelope).toInt().toShort()
            }
            offset += numSamplesPerTone
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(generatedSnd.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(generatedSnd, 0, generatedSnd.size)
        track.play()
        scope.launch(Dispatchers.IO) {
            delay((durationMsPerTone * frequencies.size + 80).toLong())
            try {
                track.release()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun triggerHapticPttPress(enableHaptic: Boolean = true) {
        if (!enableHaptic) return
        vibrateShort(45)
    }

    fun triggerHapticPttRelease(enableHaptic: Boolean = true) {
        if (!enableHaptic) return
        vibrateShort(25)
    }

    fun triggerHapticBusyWarning(enableHaptic: Boolean = true) {
        if (!enableHaptic) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 70, 50, 70, 50, 70),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 70, 50, 70, 50, 70), -1)
            }
        } catch (e: Exception) {
            vibrateShort(120)
        }
    }

    fun setAudioRouting(toEarpiece: Boolean) {
        audioManager?.let { am ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val available = am.availableCommunicationDevices
                    val targetType = if (toEarpiece) {
                        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
                    } else {
                        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
                    }
                    val targetDevice = available.firstOrNull { it.type == targetType }
                    if (targetDevice != null) {
                        am.setCommunicationDevice(targetDevice)
                        Log.d("AudioEngine", "Communication device routed to: ${targetDevice.productName} (type=$targetType)")
                    } else {
                        am.clearCommunicationDevice()
                        am.isSpeakerphoneOn = !toEarpiece
                    }
                } else {
                    @Suppress("DEPRECATION")
                    am.mode = AudioManager.MODE_IN_COMMUNICATION
                    @Suppress("DEPRECATION")
                    am.isSpeakerphoneOn = !toEarpiece
                }
            } catch (e: Exception) {
                Log.w("AudioEngine", "Failed to switch audio routing to earpiece=$toEarpiece", e)
            }
        }
    }

    private fun vibrateShort(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    fun release() {
        stopScanner()
        stopNoaaWeatherRadio()
        toneGenerator?.release()
        toneGenerator = null
        animationJob?.cancel()
        recordingJob?.cancel()
    }
}
