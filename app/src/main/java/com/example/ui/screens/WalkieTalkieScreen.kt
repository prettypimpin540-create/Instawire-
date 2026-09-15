package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.data.model.Contact
import com.example.data.model.Transmission
import com.example.data.model.UserIdentity
import com.example.ui.PttState
import com.example.ui.WalkieTarget
import com.example.ui.components.SpectrumVisualizer
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttHotRed
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

@Composable
fun WalkieTalkieScreen(
    activeTarget: WalkieTarget?,
    channels: List<Channel>,
    contacts: List<Contact>,
    recentTransmissions: List<Transmission>,
    pttState: PttState,
    transmitElapsedSec: Float,
    spectrumBars: List<Float>,
    userIdentity: UserIdentity,
    onSelectTarget: (WalkieTarget) -> Unit,
    onPttPress: () -> Unit,
    onPttRelease: () -> Unit,
    onOpenSafetyKey: () -> Unit = {},
    onOpenNoiseCancel: () -> Unit = {},
    onToggleNoiseCancellation: () -> Unit = {},
    onOpenSubscriptionPlans: () -> Unit = {},
    onOpenThemeSelector: () -> Unit = {},
    onOpenMenu: () -> Unit = {},
    onOpenScanners: () -> Unit = {},
    onOpenWeather: () -> Unit = {},
    onOpenEmergency: () -> Unit = {},
    onPlayTransmission: (Transmission) -> Unit,
    pttErrorMessage: String? = null,
    onPttErrorClick: () -> Unit = {},
    audioCaptureState: com.example.service.AudioCaptureState = com.example.service.AudioCaptureState(),
    onToggleScreenLockedBroadcast: () -> Unit = {},
    onToggleBroadcastMute: () -> Unit = {},
    onToggleAudioRouting: () -> Unit = {},
    onNavigateToChannels: () -> Unit = {},
    onOpenAddChannel: () -> Unit = {},
    onOpenJoinChannel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isBroadcastingForeground = audioCaptureState.isCapturing
    val isTransmitting = (pttState == PttState.TRANSMITTING || pttState == PttState.RECORDING) || isBroadcastingForeground
    val isIncoming = pttState == PttState.INCOMING_TRANSMISSION
    val accentColor = Color(userIdentity.themeScheme.primaryHex)

    var volumeSlider by remember { mutableStateOf(userIdentity.volumeLevel) }

    val currentChannel = if (activeTarget is WalkieTarget.ChannelTarget) activeTarget.channel else null

    val currentTargetName = when (activeTarget) {
        is WalkieTarget.ChannelTarget -> activeTarget.channel.name
        is WalkieTarget.ContactTarget -> activeTarget.contact.name
        null -> if (channels.isNotEmpty()) channels.first().name else "No Channel Selected"
    }

    val currentTargetSubtitle = when (activeTarget) {
        is WalkieTarget.ChannelTarget -> "Code: ${activeTarget.channel.displayFrequencyCode} • ${activeTarget.channel.frequency}"
        is WalkieTarget.ContactTarget -> "Direct Walkie • ${if (activeTarget.contact.isOnline) "Online" else "Offline"}"
        null -> if (channels.isNotEmpty()) "Tap to switch active channel" else "Create a custom channel or enter code"
    }

    // Pulse animation for transmitting state
    val infiniteTransition = rememberInfiniteTransition(label = "ptt_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 1. ACTIVE CHANNEL / CONTACT CARD (Clear, tappable)
        item {
            val context = LocalContext.current
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.2.dp, if (isTransmitting) PttNeonGreen else TacticalCardBorder, RoundedCornerShape(16.dp))
                    .clickable { onNavigateToChannels() }
                    .testTag("active_target_card"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (activeTarget is WalkieTarget.ContactTarget) Icons.Default.Person else Icons.Default.Radio,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentTargetName,
                                        color = TacticalTextPrimary,
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentTargetSubtitle,
                                    color = TacticalTextSecondary,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalSurface)
                                .clickable { onNavigateToChannels() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Channels",
                                    color = TacticalCyan,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Open channels",
                                    tint = TacticalCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (currentChannel != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalDarkBg)
                                .border(1.dp, TacticalCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable {
                                    val code = currentChannel.displayFrequencyCode
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Frequency Code", code))
                                    val shareText = "📻 Join my InstaWire Walkie-Talkie channel!\nChannel: ${currentChannel.name}\nFrequency Code: $code\nFrequency: ${currentChannel.frequency}"
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Frequency Code"))
                                    Toast.makeText(context, "Copied code '$code' to clipboard & opening share menu", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = TacticalCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CODE: ${currentChannel.displayFrequencyCode}",
                                    color = TacticalCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TacticalCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Code",
                                    tint = TacticalCyan,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SHARE",
                                    color = TacticalCyan,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    } else if (channels.isEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onOpenAddChannel,
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TacticalCyan,
                                    contentColor = TacticalDarkBg
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("+ Create Channel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onOpenJoinChannel,
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TacticalSurface,
                                    contentColor = TacticalCyan
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("# Join with Code", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. AUDIO SPECTRUM VISUALIZER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(TacticalSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
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
                                .background(
                                    when {
                                        isTransmitting -> PttNeonGreen
                                        isIncoming -> TacticalCyan
                                        else -> TacticalTextMuted
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                isTransmitting -> "TRANSMITTING VOICE • LIVE"
                                isIncoming -> "RECEIVING TRANSMISSION..."
                                else -> "CHANNEL AUDIO MONITOR • READY"
                            },
                            color = when {
                                isTransmitting -> PttNeonGreen
                                isIncoming -> TacticalCyan
                                else -> TacticalTextMuted
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (isTransmitting) {
                        Text(
                            text = String.format("%.1fs", transmitElapsedSec),
                            color = PttNeonGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                SpectrumVisualizer(
                    bars = spectrumBars,
                    isTransmitting = isTransmitting,
                    isIncoming = isIncoming,
                    modifier = Modifier.height(36.dp)
                )
            }
        }

        // 2.5. PTT BUSY WARNING BANNER
        if (pttState == PttState.BUSY || pttErrorMessage?.contains("PLEASE HOLD") == true) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(10.dp))
                        .testTag("ptt_busy_alert_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF331C00))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "PTT In Use",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PTT IS IN USE — PLEASE HOLD",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Another user is currently speaking. Channel is busy.",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. THE BIG CENTER PUSH-TO-TALK BUTTON
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                com.example.ui.components.PttTactileButton(
                    pttState = pttState,
                    transmitElapsedSec = transmitElapsedSec,
                    onPress = onPttPress,
                    onRelease = onPttRelease,
                    errorMessage = pttErrorMessage,
                    onErrorClick = onPttErrorClick,
                    themeScheme = userIdentity.themeScheme,
                    modifier = Modifier.size(260.dp)
                )
            }
        }

        // 3.5. AUDIO ROUTING TOGGLE (EARPIECE VS SPEAKERPHONE)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = if (userIdentity.audioRoutingToEarpiece) TacticalCyan.copy(alpha = 0.6f) else TacticalCardBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("audio_routing_toggle_card"),
                colors = CardDefaults.cardColors(
                    containerColor = TacticalSurface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (userIdentity.audioRoutingToEarpiece) TacticalCyan.copy(alpha = 0.15f) else accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (userIdentity.audioRoutingToEarpiece) Icons.Default.Hearing else Icons.Default.VolumeUp,
                                contentDescription = "Audio Output Device",
                                tint = if (userIdentity.audioRoutingToEarpiece) TacticalCyan else accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (userIdentity.audioRoutingToEarpiece) "EARPIECE MODE" else "SPEAKERPHONE",
                                    color = TacticalTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (userIdentity.audioRoutingToEarpiece) TacticalCyan.copy(alpha = 0.2f) else accentColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = if (userIdentity.audioRoutingToEarpiece) "DISCREET" else "LOUD",
                                        color = if (userIdentity.audioRoutingToEarpiece) TacticalCyan else accentColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = if (userIdentity.audioRoutingToEarpiece)
                                    "Audio routed to private phone earpiece"
                                else
                                    "Audio routed to external speakerphone",
                                color = TacticalTextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    Button(
                        onClick = onToggleAudioRouting,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (userIdentity.audioRoutingToEarpiece) TacticalCyan else accentColor,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("toggle_audio_routing_button")
                    ) {
                        Text(
                            text = if (userIdentity.audioRoutingToEarpiece) "USE SPEAKER" else "USE EARPIECE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4. QUICK AUDIO LEVEL SLIDER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TacticalSurface)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speaker Volume",
                    tint = TacticalTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Speaker",
                    color = TacticalTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = volumeSlider,
                    onValueChange = { volumeSlider = it },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = TacticalSurfaceElevated
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${(volumeSlider * 100).toInt()}%",
                    color = TacticalTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 5. RECENT ACTIVITY & VOICE REPLAY
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Activity",
                            color = TacticalTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${recentTransmissions.size} transmissions",
                            color = TacticalTextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (recentTransmissions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No messages yet. Hold the green button to start talking!",
                                color = TacticalTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        recentTransmissions.take(4).forEach { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TacticalSurfaceElevated)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(accentColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = tx.senderCallsign,
                                            color = TacticalTextPrimary,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${tx.durationSeconds}s audio message",
                                            color = TacticalTextSecondary,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onPlayTransmission(tx) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accentColor.copy(alpha = 0.18f),
                                        contentColor = accentColor
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Replay", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
