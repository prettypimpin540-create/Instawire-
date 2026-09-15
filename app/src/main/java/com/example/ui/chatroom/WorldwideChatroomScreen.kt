package com.example.ui.chatroom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedDarkBorder
import com.example.ui.theme.SophisticatedDarkError
import com.example.ui.theme.SophisticatedDarkPrimary
import com.example.ui.theme.SophisticatedDarkSecondary
import com.example.ui.theme.SophisticatedDarkSurface
import com.example.ui.theme.SophisticatedDarkSurfaceElevated
import com.example.ui.theme.SophisticatedDarkTertiary
import com.example.ui.theme.SophisticatedDarkTextMuted
import com.example.ui.theme.SophisticatedDarkTextPrimary
import com.example.ui.theme.SophisticatedDarkTextSecondary

@Composable
fun WorldwideChatroomScreen(
    viewModel: ChatroomViewModel,
    onBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SophisticatedDarkBackground)
            .testTag("worldwide_chatroom_screen")
    ) {
        if (uiState.activeRoom == null) {
            // Room list view
            WorldwideRoomListView(
                channels = uiState.channels,
                onJoinRoom = { channel -> viewModel.joinRoom(channel) },
                onBack = onBack
            )
        } else {
            // Active joined room view with live audio streaming
            ActiveRoomAudioStreamView(
                room = uiState.activeRoom!!,
                isStreaming = uiState.isAudioStreaming,
                isTransmitting = uiState.isTransmitting,
                isMuted = uiState.isMuted,
                volume = uiState.volume,
                audioSpectrum = uiState.liveAudioSpectrum,
                speakerName = uiState.activeSpeakerName,
                onLeaveRoom = { viewModel.leaveRoom() },
                onToggleStreaming = {
                    if (uiState.isAudioStreaming) viewModel.stopStreamingAudio()
                    else viewModel.startStreamingAudio()
                },
                onPttChange = { isPressed -> viewModel.setPushToTalk(isPressed) },
                onToggleMute = { viewModel.toggleMute() },
                onVolumeChange = { viewModel.setVolume(it) }
            )
        }
    }
}

@Composable
private fun WorldwideRoomListView(
    channels: List<WorldwideWalkieChannel>,
    onJoinRoom: (WorldwideWalkieChannel) -> Unit,
    onBack: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SophisticatedDarkTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = SophisticatedDarkSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Worldwide Walkie Channels",
                        color = SophisticatedDarkTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Join global audio chatrooms and stream live airwaves",
                    color = SophisticatedDarkTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(channels, key = { it.id }) { channel ->
                WorldwideChannelCard(
                    channel = channel,
                    onJoin = { onJoinRoom(channel) }
                )
            }
        }
    }
}

@Composable
private fun WorldwideChannelCard(
    channel: WorldwideWalkieChannel,
    onJoin: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SophisticatedDarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SophisticatedDarkBorder, RoundedCornerShape(14.dp))
            .testTag("channel_card_${channel.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = channel.flagEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = channel.name,
                            color = SophisticatedDarkTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${channel.region} • ${channel.frequency}",
                            color = SophisticatedDarkTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SophisticatedDarkPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedDarkPrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SophisticatedDarkPrimary)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${channel.activeListeners} online",
                            color = SophisticatedDarkPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = channel.description,
                color = SophisticatedDarkTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SophisticatedDarkSurfaceElevated
                ) {
                    Text(
                        text = channel.category,
                        color = SophisticatedDarkTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Button(
                    onClick = onJoin,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedDarkSecondary,
                        contentColor = SophisticatedDarkBackground
                    ),
                    modifier = Modifier.testTag("join_room_btn_${channel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Join & Stream", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ActiveRoomAudioStreamView(
    room: WorldwideWalkieChannel,
    isStreaming: Boolean,
    isTransmitting: Boolean,
    isMuted: Boolean,
    volume: Float,
    audioSpectrum: List<Float>,
    speakerName: String?,
    onLeaveRoom: () -> Unit,
    onToggleStreaming: () -> Unit,
    onPttChange: (Boolean) -> Unit,
    onToggleMute: () -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Room header bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLeaveRoom, modifier = Modifier.testTag("leave_room_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Leave Room",
                        tint = SophisticatedDarkTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "${room.flagEmoji} ${room.name}",
                        color = SophisticatedDarkTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "${room.frequency} • ${room.activeListeners} Listening",
                        color = SophisticatedDarkTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = onLeaveRoom,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SophisticatedDarkError.copy(alpha = 0.2f),
                    contentColor = SophisticatedDarkError
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Leave", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live streaming audio visualizer card
        Card(
            colors = CardDefaults.cardColors(containerColor = SophisticatedDarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SophisticatedDarkBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isStreaming) SophisticatedDarkPrimary else SophisticatedDarkTextMuted)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isStreaming) "STREAMING AUDIO LIVE" else "STREAM PAUSED",
                            color = if (isStreaming) SophisticatedDarkPrimary else SophisticatedDarkTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(onClick = onToggleMute) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = if (isMuted) SophisticatedDarkError else SophisticatedDarkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Spectrum waveform visualizer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(SophisticatedDarkBackground, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    audioSpectrum.forEach { level ->
                        val barHeight = (level * 50f).coerceIn(4f, 50f).dp
                        val barColor = when {
                            isTransmitting -> SophisticatedDarkPrimary
                            isMuted -> SophisticatedDarkTextMuted
                            else -> SophisticatedDarkSecondary
                        }
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isTransmitting) "You are broadcasting to room..." else "Current Speaker: ${speakerName ?: "Repeater Station"}",
                    color = if (isTransmitting) SophisticatedDarkPrimary else SophisticatedDarkTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Volume control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = SophisticatedDarkTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = SophisticatedDarkSecondary,
                    activeTrackColor = SophisticatedDarkSecondary,
                    inactiveTrackColor = SophisticatedDarkSurfaceElevated
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${(volume * 100).toInt()}%",
                color = SophisticatedDarkTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // The Push-to-Talk Transmission Button for the Worldwide Room
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .background(
                    if (isTransmitting) SophisticatedDarkPrimary.copy(alpha = 0.2f)
                    else SophisticatedDarkSecondary.copy(alpha = 0.1f)
                )
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isTransmitting) listOf(
                                SophisticatedDarkPrimary,
                                Color(0xFF0D381E)
                            ) else listOf(
                                SophisticatedDarkSecondary,
                                Color(0xFF0F3B66)
                            )
                        )
                    )
                    .border(
                        width = 3.dp,
                        color = if (isTransmitting) SophisticatedDarkPrimary else SophisticatedDarkSecondary,
                        shape = CircleShape
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                onPttChange(true)
                                tryAwaitRelease()
                                onPttChange(false)
                            }
                        )
                    }
                    .testTag("worldwide_ptt_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "PTT",
                        tint = SophisticatedDarkBackground,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isTransmitting) "TRANSMITTING" else "HOLD TO TALK",
                        color = SophisticatedDarkBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
