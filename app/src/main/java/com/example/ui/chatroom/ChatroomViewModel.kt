package com.example.ui.chatroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class WorldwideWalkieChannel(
    val id: String,
    val name: String,
    val region: String,
    val frequency: String,
    val flagEmoji: String,
    val activeListeners: Int,
    val category: String,
    val description: String,
    val isVerifiedFeed: Boolean = true
)

data class ChatroomUiState(
    val channels: List<WorldwideWalkieChannel> = emptyList(),
    val activeRoom: WorldwideWalkieChannel? = null,
    val isAudioStreaming: Boolean = false,
    val isTransmitting: Boolean = false,
    val isMuted: Boolean = false,
    val volume: Float = 0.85f,
    val liveAudioSpectrum: List<Float> = List(16) { 0.15f },
    val activeSpeakerName: String? = null,
    val connectionStatus: String = "Ready"
)

/**
 * ViewModel that manages worldwide walkie-talkie channels, room membership,
 * and simulated real-time audio streaming.
 */
class ChatroomViewModel : ViewModel() {

    private val defaultChannels = listOf(
        WorldwideWalkieChannel(
            id = "ww_tokyo",
            name = "Tokyo Shibuya Night PTT",
            region = "Asia-Pacific",
            frequency = "446.125 MHz",
            flagEmoji = "🇯🇵",
            activeListeners = 142,
            category = "Night Owls",
            description = "Live two-way banter & city vibes across Shibuya & Shinjuku"
        ),
        WorldwideWalkieChannel(
            id = "ww_london",
            name = "London Skyline Relay",
            region = "Europe",
            frequency = "446.006 MHz",
            flagEmoji = "🇬🇧",
            activeListeners = 98,
            category = "General",
            description = "Open UK walkie frequency, traffic updates & open airwaves"
        ),
        WorldwideWalkieChannel(
            id = "ww_nyc",
            name = "NYC Manhattan Emergency Net",
            region = "Americas",
            frequency = "462.562 MHz",
            flagEmoji = "🇺🇸",
            activeListeners = 230,
            category = "Emergency",
            description = "Community civil watch, emergency dispatch & street relay"
        ),
        WorldwideWalkieChannel(
            id = "ww_berlin",
            name = "Berlin Underground Beats",
            region = "Europe",
            frequency = "446.031 MHz",
            flagEmoji = "🇩🇪",
            activeListeners = 84,
            category = "Music & Jam",
            description = "Lo-fi jams, indie sound checks & electronic synthesizer tests"
        ),
        WorldwideWalkieChannel(
            id = "ww_sao_paulo",
            name = "São Paulo Trucker Dispatch",
            region = "Americas",
            frequency = "462.612 MHz",
            flagEmoji = "🇧🇷",
            activeListeners = 115,
            category = "Travel & Meet",
            description = "Road status, highway chatter and inter-state voice dispatch"
        ),
        WorldwideWalkieChannel(
            id = "ww_paris",
            name = "Paris Montmartre Cafe Chat",
            region = "Europe",
            frequency = "446.081 MHz",
            flagEmoji = "🇫🇷",
            activeListeners = 64,
            category = "Language Exchange",
            description = "French-English casual conversational exchange & coffee banter"
        )
    )

    private val _uiState = MutableStateFlow(
        ChatroomUiState(channels = defaultChannels)
    )
    val uiState: StateFlow<ChatroomUiState> = _uiState.asStateFlow()

    private var audioSimulationJob: Job? = null

    /**
     * Joins a worldwide walkie-talkie room and automatically starts streaming audio.
     */
    fun joinRoom(channel: WorldwideWalkieChannel) {
        _uiState.value = _uiState.value.copy(
            activeRoom = channel,
            isAudioStreaming = true,
            connectionStatus = "Connected to ${channel.name}",
            activeSpeakerName = "Repeater Station (${channel.frequency})"
        )
        startAudioSpectrumSimulation()
    }

    /**
     * Leaves the current room and stops streaming audio.
     */
    fun leaveRoom() {
        audioSimulationJob?.cancel()
        audioSimulationJob = null
        _uiState.value = _uiState.value.copy(
            activeRoom = null,
            isAudioStreaming = false,
            isTransmitting = false,
            connectionStatus = "Idle",
            activeSpeakerName = null,
            liveAudioSpectrum = List(16) { 0.15f }
        )
    }

    /**
     * Manually starts streaming audio in the active room.
     */
    fun startStreamingAudio() {
        if (_uiState.value.activeRoom == null) return
        _uiState.value = _uiState.value.copy(isAudioStreaming = true)
        startAudioSpectrumSimulation()
    }

    /**
     * Stops streaming audio in the active room.
     */
    fun stopStreamingAudio() {
        audioSimulationJob?.cancel()
        audioSimulationJob = null
        _uiState.value = _uiState.value.copy(
            isAudioStreaming = false,
            isTransmitting = false,
            liveAudioSpectrum = List(16) { 0.15f }
        )
    }

    /**
     * Push-To-Talk transmission into the active room.
     */
    fun setPushToTalk(isPressed: Boolean) {
        if (_uiState.value.activeRoom == null) return
        _uiState.value = _uiState.value.copy(
            isTransmitting = isPressed,
            activeSpeakerName = if (isPressed) "You (Broadcasting)" else "Repeater Station"
        )
    }

    /**
     * Toggles mute state for audio playback.
     */
    fun toggleMute() {
        val newMute = !_uiState.value.isMuted
        _uiState.value = _uiState.value.copy(isMuted = newMute)
    }

    /**
     * Updates speaker volume level.
     */
    fun setVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(volume = volume.coerceIn(0f, 1f))
    }

    private fun startAudioSpectrumSimulation() {
        audioSimulationJob?.cancel()
        audioSimulationJob = viewModelScope.launch {
            while (true) {
                delay(120)
                val isTransmitting = _uiState.value.isTransmitting
                val isMuted = _uiState.value.isMuted
                val isStreaming = _uiState.value.isAudioStreaming

                if (!isStreaming || isMuted) {
                    _uiState.value = _uiState.value.copy(
                        liveAudioSpectrum = List(16) { 0.12f }
                    )
                } else {
                    val baseAmp = if (isTransmitting) 0.7f else 0.45f
                    val newBars = List(16) {
                        (baseAmp * (0.3f + Random.nextFloat() * 0.7f)).coerceIn(0.12f, 1.0f)
                    }
                    _uiState.value = _uiState.value.copy(liveAudioSpectrum = newBars)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioSimulationJob?.cancel()
    }
}
