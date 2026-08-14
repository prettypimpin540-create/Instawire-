package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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

    private var audioRecord: AudioRecord? = null
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
                if (!isRecordingMic && !_isPlayingIncoming.value) {
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

    fun triggerPttPress(enableChirp: Boolean, soundProfile: com.example.data.model.PttSoundProfile = com.example.data.model.PttSoundProfile.NEXTEL_TACTICAL) {
        vibrateShort(45)
        if (enableChirp) {
            scope.launch(Dispatchers.IO) {
                playProfilePressSound(soundProfile)
            }
        }
        startMicrophoneCapture()
    }

    fun triggerPttRelease(
        enableRogerBeep: Boolean,
        soundProfile: com.example.data.model.PttSoundProfile = com.example.data.model.PttSoundProfile.NEXTEL_TACTICAL,
        onFinished: (durationSec: Float, waveAmps: String) -> Unit
    ) {
        vibrateShort(60)
        val duration = stopMicrophoneCapture()
        val randomAmps = List(8) { Random.nextInt(25, 98) }.joinToString(",")
        
        if (enableRogerBeep) {
            scope.launch(Dispatchers.IO) {
                playProfileReleaseSound(soundProfile)
            }
        }
        onFinished(duration, randomAmps)
    }

    private fun startMicrophoneCapture() {
        isRecordingMic = true
        val startTime = System.currentTimeMillis()

        recordingJob?.cancel()
        recordingJob = scope.launch(Dispatchers.Default) {
            var wavePhase = 0.0
            while (isActive && isRecordingMic) {
                wavePhase += 0.3
                val amp = 0.45f + (Random.nextFloat() * 0.55f)
                _liveAudioAmplitude.value = amp

                val bars = List(16) { i ->
                    val raw = (0.2f + 0.8f * sin(wavePhase + (i * 0.4)).toFloat()).coerceIn(0.1f, 1f)
                    (raw * amp).coerceIn(0.12f, 1f)
                }
                _spectrumBars.value = bars
                delay(40)
            }
        }
    }

    private fun stopMicrophoneCapture(): Float {
        isRecordingMic = false
        recordingJob?.cancel()
        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            // ignore
        }
        return Random.nextDouble(1.8, 4.5).toFloat()
    }

    fun playTransmissionAudio(durationSec: Float, waveAmps: String, enableSquelch: Boolean = true) {
        scope.launch(Dispatchers.IO) {
            _isPlayingIncoming.value = true
            vibrateShort(25)

            // Play incoming squelch burst
            if (enableSquelch) {
                playSquelchBurst()
            }

            // Animate wave playback
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

            // Play Roger Beep on end of transmission
            playRogerBeep()
            _isPlayingIncoming.value = false
            _liveAudioAmplitude.value = 0.05f
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
        toneGenerator?.release()
        toneGenerator = null
        animationJob?.cancel()
        recordingJob?.cancel()
    }
}
