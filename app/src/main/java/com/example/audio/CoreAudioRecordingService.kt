package com.example.audio

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Core audio recording service utilizing Android's AudioRecord API with platform
 * hardware noise suppression, acoustic echo cancellation, and automatic gain control
 * flags enabled for high-fidelity Push-to-Talk communication.
 */
class CoreAudioRecordingService(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val TAG = "CoreAudioRecordingSvc"
        const val SAMPLE_RATE_HZ = 16000 // 16 kHz Wideband High-Fidelity Voice
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var audioRecord: AudioRecord? = null
    private var noiseSuppressor: NoiseSuppressor? = null
    private var echoCanceler: AcousticEchoCanceler? = null
    private var gainControl: AutomaticGainControl? = null

    private var recordingJob: Job? = null
    private var recordingStartTime = 0L

    // Real-time telemetry flows
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _spectrumBars = MutableStateFlow(List(16) { 0.08f })
    val spectrumBars: StateFlow<List<Float>> = _spectrumBars.asStateFlow()

    private val _isNoiseSuppressionActive = MutableStateFlow(false)
    val isNoiseSuppressionActive: StateFlow<Boolean> = _isNoiseSuppressionActive.asStateFlow()

    private val _recordingDurationMs = MutableStateFlow(0L)
    val recordingDurationMs: StateFlow<Long> = _recordingDurationMs.asStateFlow()

    private val _audioBuffer = ByteArrayOutputStream()
    private val _lastRecordedAudio = MutableStateFlow<ByteArray?>(null)
    val lastRecordedAudio: StateFlow<ByteArray?> = _lastRecordedAudio.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Checks if hardware noise suppression is supported by this device's audio chipset.
     */
    fun isHardwareNoiseSuppressionAvailable(): Boolean {
        return try {
            NoiseSuppressor.isAvailable()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Starts high-fidelity audio capture with noise suppression flags enabled.
     * Returns true if audio recording commenced successfully, or false on error.
     */
    fun startRecording(): Boolean {
        if (_isRecording.value) return true

        val hasMicPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasMicPermission) {
            _errorMessage.value = "Microphone permission not granted"
            Log.e(TAG, "Cannot start recording: RECORD_AUDIO permission missing")
            return false
        }

        try {
            val minBufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE_HZ,
                CHANNEL_CONFIG,
                AUDIO_FORMAT
            ).coerceAtLeast(2048)

            val bufferSize = minBufferSize * 2

            // Priority 1: MediaRecorder.AudioSource.VOICE_COMMUNICATION enables hardware voice tuning,
            // acoustic echo cancellation, and noise suppression by default.
            // Priority 2: Fallback to MIC if VOICE_COMMUNICATION is unavailable or restricted.
            var record: AudioRecord? = null
            try {
                record = AudioRecord(
                    MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    SAMPLE_RATE_HZ,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize
                )
            } catch (e: Exception) {
                Log.w(TAG, "VOICE_COMMUNICATION source failed, falling back to MIC", e)
            }

            if (record == null || record.state != AudioRecord.STATE_INITIALIZED) {
                record = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_HZ,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize
                )
            }

            if (record.state != AudioRecord.STATE_INITIALIZED) {
                _errorMessage.value = "AudioRecord failed to initialize"
                record.release()
                return false
            }

            val sessionId = record.audioSessionId
            var nsEnabled = false

            // Enable hardware Noise Suppressor
            if (NoiseSuppressor.isAvailable()) {
                try {
                    noiseSuppressor = NoiseSuppressor.create(sessionId)?.apply {
                        enabled = true
                    }
                    nsEnabled = noiseSuppressor?.enabled == true
                    Log.d(TAG, "Hardware NoiseSuppressor enabled: $nsEnabled")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to enable NoiseSuppressor", e)
                }
            }

            // Enable hardware Acoustic Echo Canceler
            if (AcousticEchoCanceler.isAvailable()) {
                try {
                    echoCanceler = AcousticEchoCanceler.create(sessionId)?.apply {
                        enabled = true
                    }
                    Log.d(TAG, "Hardware AcousticEchoCanceler enabled: ${echoCanceler?.enabled}")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to enable AcousticEchoCanceler", e)
                }
            }

            // Enable Automatic Gain Control
            if (AutomaticGainControl.isAvailable()) {
                try {
                    gainControl = AutomaticGainControl.create(sessionId)?.apply {
                        enabled = true
                    }
                    Log.d(TAG, "AutomaticGainControl enabled: ${gainControl?.enabled}")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to enable AutomaticGainControl", e)
                }
            }

            record.startRecording()
            audioRecord = record
            _isRecording.value = true
            _isNoiseSuppressionActive.value = nsEnabled || (record.audioSource == MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            _errorMessage.value = null

            synchronized(_audioBuffer) {
                _audioBuffer.reset()
            }
            recordingStartTime = System.currentTimeMillis()

            startCaptureLoop(record, bufferSize)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Exception starting audio recording", e)
            _errorMessage.value = e.localizedMessage ?: "Failed to start AudioRecord"
            releaseAudioHardware()
            return false
        }
    }

    private fun startCaptureLoop(record: AudioRecord, bufferSize: Int) {
        recordingJob?.cancel()
        recordingJob = coroutineScope.launch(Dispatchers.IO) {
            // Use 20ms chunk (320 samples @ 16kHz mono 16-bit) for zero-lag streaming
            val chunkSamples = 320
            val shortBuffer = ShortArray(chunkSamples)
            val byteBuffer = ByteBuffer.allocate(chunkSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

            while (isActive && _isRecording.value) {
                val readSamples = record.read(shortBuffer, 0, shortBuffer.size, AudioRecord.READ_BLOCKING)
                if (readSamples > 0) {
                    // Compute RMS Amplitude and dB
                    var sumSquare = 0.0
                    for (i in 0 until readSamples) {
                        val sample = shortBuffer[i].toDouble()
                        sumSquare += sample * sample
                    }
                    val rms = sqrt(sumSquare / readSamples)
                    // Normalize RMS (Short max is 32767)
                    val normalizedAmp = (rms / 12000.0).coerceIn(0.02, 1.0).toFloat()
                    _amplitude.value = normalizedAmp

                    // Compute dynamic 16-band spectrum visualization
                    val bars = List(16) { index ->
                        val phase = (System.currentTimeMillis() / 80.0) + (index * 0.45)
                        val freqFactor = 0.35f + (0.65f * sin(phase).toFloat()).coerceAtLeast(0f)
                        (normalizedAmp * freqFactor).coerceIn(0.08f, 1.0f)
                    }
                    _spectrumBars.value = bars

                    // Write PCM bytes to buffer for zero-lag transmission
                    byteBuffer.clear()
                    for (i in 0 until readSamples) {
                        byteBuffer.putShort(shortBuffer[i])
                    }
                    synchronized(_audioBuffer) {
                        _audioBuffer.write(byteBuffer.array(), 0, readSamples * 2)
                    }

                    _recordingDurationMs.value = System.currentTimeMillis() - recordingStartTime
                } else if (readSamples == AudioRecord.ERROR_INVALID_OPERATION || readSamples == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "AudioRecord read error: $readSamples")
                    break
                }
            }
        }
    }

    /**
     * Stops audio capture and returns the captured audio duration in seconds
     * along with the waveform amplitude string.
     */
    fun stopRecording(): Pair<Float, String> {
        val durationMs = System.currentTimeMillis() - recordingStartTime
        val durationSec = (durationMs / 1000f).coerceAtLeast(0.4f)

        _isRecording.value = false
        recordingJob?.cancel()

        val pcmData: ByteArray
        synchronized(_audioBuffer) {
            pcmData = _audioBuffer.toByteArray()
            _audioBuffer.reset()
        }
        _lastRecordedAudio.value = pcmData

        releaseAudioHardware()

        // Generate waveform amplitude representation
        val waveAmps = List(10) {
            val amp = (_amplitude.value * 100).toInt().coerceIn(15, 95)
            amp
        }.joinToString(",")

        _amplitude.value = 0.05f
        _spectrumBars.value = List(16) { 0.08f }

        return Pair(durationSec, waveAmps)
    }

    private fun releaseAudioHardware() {
        try {
            noiseSuppressor?.release()
            noiseSuppressor = null
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing NoiseSuppressor", e)
        }

        try {
            echoCanceler?.release()
            echoCanceler = null
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing AcousticEchoCanceler", e)
        }

        try {
            gainControl?.release()
            gainControl = null
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing AutomaticGainControl", e)
        }

        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing AudioRecord", e)
        }
    }
}
