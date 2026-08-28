package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.data.model.Contact
import com.example.data.model.Transmission
import com.example.data.model.UserIdentity
import com.example.data.model.WalkieLayoutType
import com.example.ui.PttState
import com.example.ui.WalkieTarget
import com.example.ui.components.EncryptionStatusBadge
import com.example.ui.components.PttTactileButton
import com.example.ui.components.SpectrumVisualizer
import com.example.ui.theme.BurnerGold
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttHotRed
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.PttRedGlow
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalCyanGlow
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
    onOpenSafetyKey: () -> Unit,
    onOpenNoiseCancel: () -> Unit,
    onToggleNoiseCancellation: () -> Unit = {},
    onOpenSubscriptionPlans: () -> Unit = {},
    onOpenThemeSelector: () -> Unit = {},
    onOpenMenu: () -> Unit = {},
    onOpenScanners: () -> Unit = {},
    onOpenWeather: () -> Unit = {},
    onOpenEmergency: () -> Unit = {},
    onPlayTransmission: (Transmission) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTransmitting = pttState == PttState.TRANSMITTING
    val isIncoming = pttState == PttState.INCOMING_TRANSMISSION
    val isE2eeActive = userIdentity.isE2eeActive
    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val glowColor = Color(userIdentity.themeScheme.glowHex)

    val targetTitle = when (activeTarget) {
        is WalkieTarget.ChannelTarget -> activeTarget.channel.name
        is WalkieTarget.ContactTarget -> activeTarget.contact.name
        null -> "TACTICAL ALPHA 01"
    }

    val targetSub = when (activeTarget) {
        is WalkieTarget.ChannelTarget -> "${activeTarget.channel.frequency} • ${activeTarget.channel.activeMembersCount} ON AIR"
        is WalkieTarget.ContactTarget -> "${activeTarget.contact.callsign} • ${activeTarget.contact.number}"
        null -> "462.5625 MHz • 14 ON AIR"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Clean & Focused Active Channel / Target Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    if (isTransmitting) PttHotRed.copy(alpha = 0.6f)
                    else if (isIncoming) TacticalCyan.copy(alpha = 0.6f)
                    else if (isE2eeActive) accentColor.copy(alpha = 0.45f)
                    else TacticalCardBorder,
                    RoundedCornerShape(16.dp)
                )
                .testTag("active_target_banner"),
            colors = CardDefaults.cardColors(containerColor = TacticalSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (activeTarget) {
                                is WalkieTarget.ChannelTarget -> Icons.Default.Radio
                                is WalkieTarget.ContactTarget -> Icons.Default.Person
                                null -> Icons.Default.CellTower
                            },
                            contentDescription = "Target Icon",
                            tint = if (isTransmitting) PttHotRed else accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (activeTarget) {
                                is WalkieTarget.ChannelTarget -> "GROUP CHANNEL"
                                is WalkieTarget.ContactTarget -> "DIRECT 1-ON-1"
                                null -> "STANDBY FREQUENCY"
                            },
                            color = TacticalTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Encryption Status Badge
                    EncryptionStatusBadge(
                        isEncrypted = isE2eeActive,
                        isKeyVerified = true,
                        onClick = onOpenSafetyKey,
                        modifier = Modifier.testTag("target_encryption_badge")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = targetTitle,
                    color = TacticalTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = targetSub,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Targets Switcher Strip (Clean, scannable horizontal chips)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_targets_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(channels) { channel ->
                val isSelected = activeTarget is WalkieTarget.ChannelTarget && activeTarget.channel.id == channel.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) accentColor.copy(alpha = 0.25f) else TacticalSurface)
                        .border(
                            1.dp,
                            if (isSelected) accentColor else TacticalCardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectTarget(WalkieTarget.ChannelTarget(channel)) }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("quick_channel_${channel.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Channel",
                            tint = if (isSelected) accentColor else TacticalTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = channel.name,
                            color = if (isSelected) glowColor else TacticalTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            items(contacts) { contact ->
                val isSelected = activeTarget is WalkieTarget.ContactTarget && activeTarget.contact.id == contact.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) accentColor.copy(alpha = 0.25f) else TacticalSurface)
                        .border(
                            1.dp,
                            if (isSelected) accentColor else TacticalCardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectTarget(WalkieTarget.ContactTarget(contact)) }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("quick_contact_${contact.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (contact.isBurner) Icons.Default.Whatshot else Icons.Default.Person,
                            contentDescription = "Contact",
                            tint = if (contact.isBurner) BurnerGold else if (isSelected) accentColor else TacticalTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = contact.callsign,
                            color = if (isSelected) glowColor else TacticalTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // DYNAMIC LAYOUT SWITCHER: Renders distinct tactical visuals based on WalkieLayoutType
        when (userIdentity.layoutType) {
            WalkieLayoutType.CLASSIC_TACTICAL -> {
                // Classic Rugged Walkie Layout
                SpectrumVisualizer(
                    bars = spectrumBars,
                    isTransmitting = isTransmitting,
                    isIncoming = isIncoming,
                    modifier = Modifier.testTag("spectrum_visualizer")
                )

                Spacer(modifier = Modifier.height(16.dp))

                PttTactileButton(
                    pttState = pttState,
                    transmitElapsedSec = transmitElapsedSec,
                    onPress = onPttPress,
                    onRelease = onPttRelease,
                    themeScheme = userIdentity.themeScheme,
                    modifier = Modifier.testTag("main_ptt_button")
                )
            }

            WalkieLayoutType.MIL_SPEC_COCKPIT -> {
                // Cockpit Tactical HUD with Dual Decibel VU Meters & Crosshair
                CockpitHudLayout(
                    bars = spectrumBars,
                    pttState = pttState,
                    transmitElapsedSec = transmitElapsedSec,
                    accentColor = accentColor,
                    glowColor = glowColor,
                    onPress = onPttPress,
                    onRelease = onPttRelease,
                    userIdentity = userIdentity
                )
            }

            WalkieLayoutType.CYBER_SYNTH_WAVE -> {
                // Cyberpunk Matrix Terminal Layout
                CyberSynthLayout(
                    bars = spectrumBars,
                    pttState = pttState,
                    transmitElapsedSec = transmitElapsedSec,
                    accentColor = accentColor,
                    glowColor = glowColor,
                    onPress = onPttPress,
                    onRelease = onPttRelease,
                    userIdentity = userIdentity
                )
            }

            WalkieLayoutType.MINIMALIST_STEALTH -> {
                // Stealth Deep Blackout Layout with Large Lower Ergonomic Thumb Trigger
                StealthMinimalistLayout(
                    pttState = pttState,
                    transmitElapsedSec = transmitElapsedSec,
                    accentColor = accentColor,
                    glowColor = glowColor,
                    onPress = onPttPress,
                    onRelease = onPttRelease,
                    userIdentity = userIdentity
                )
            }

            WalkieLayoutType.RETRO_VINTAGE_CB -> {
                // 1980s Retro Analog CB Radio Transceiver with Needle VU Gauge
                RetroCbRadioLayout(
                    bars = spectrumBars,
                    pttState = pttState,
                    transmitElapsedSec = transmitElapsedSec,
                    accentColor = accentColor,
                    onPress = onPttPress,
                    onRelease = onPttRelease,
                    userIdentity = userIdentity
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Clean Live Status Indicator Pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(TacticalSurfaceElevated)
                .border(
                    1.dp,
                    when {
                        isTransmitting -> PttHotRed
                        isIncoming -> TacticalCyan
                        isE2eeActive -> accentColor.copy(alpha = 0.5f)
                        else -> TacticalCardBorder
                    },
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isTransmitting -> PttHotRed
                            isIncoming -> TacticalCyan
                            isE2eeActive -> accentColor
                            else -> TacticalAmber
                        }
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when {
                    isTransmitting -> "AIRWAVES HOT • TRANSMITTING"
                    isIncoming -> "RECEIVING VOICE TRANSMISSION"
                    isE2eeActive -> "READY • 256-BIT E2EE ENCRYPTED"
                    else -> "UNENCRYPTED AIRWAVES"
                },
                color = when {
                    isTransmitting -> PttRedGlow
                    isIncoming -> TacticalCyanGlow
                    isE2eeActive -> TacticalTextSecondary
                    else -> TacticalAmber
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Beginner-Friendly Quick Live Feeds & Public Safety Hub
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live Scanners
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TacticalCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { onOpenScanners() }
                    .testTag("walkie_quick_scanners_button"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "Live Scanners",
                        tint = TacticalCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "SCANNERS",
                        color = TacticalCyan,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Police / Fire",
                        color = TacticalTextMuted,
                        fontSize = 8.5.sp
                    )
                }
            }

            // NOAA Weather
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF60A5FA).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { onOpenWeather() }
                    .testTag("walkie_quick_weather_button"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "NOAA Weather",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "WEATHER",
                        color = Color(0xFF60A5FA),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "NOAA 24/7 NWS",
                        color = TacticalTextMuted,
                        fontSize = 8.5.sp
                    )
                }
            }

            // Emergency & Coast Guard
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, PttHotRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { onOpenEmergency() }
                    .testTag("walkie_quick_emergency_button"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Emergency 911 / Coast Guard",
                        tint = PttHotRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "911 / RESCUE",
                        color = PttHotRed,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Coast Guard",
                        color = TacticalTextMuted,
                        fontSize = 8.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Clean Quick Action Hub Row (Feature Menu, Noise Filter, Themes)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Feature Menu Button
            Card(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { onOpenMenu() }
                    .testTag("walkie_menu_quick_button"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Feature Menu",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ALL FEATURES",
                        color = accentColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Quick Noise Filter Toggle
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (userIdentity.noiseFilterEnabled) PttNeonGreen.copy(alpha = 0.5f) else TacticalCardBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggleNoiseCancellation() }
                    .testTag("walkie_noise_quick_toggle"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Noise Filter",
                        tint = if (userIdentity.noiseFilterEnabled) PttNeonGreen else TacticalTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (userIdentity.noiseFilterEnabled) "FILTER ON" else "FILTER OFF",
                        color = if (userIdentity.noiseFilterEnabled) PttNeonGreen else TacticalTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Quick Themes Button
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(12.dp))
                    .clickable { onOpenThemeSelector() }
                    .testTag("walkie_themes_quick_button"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Themes",
                        tint = BurnerGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "THEMES",
                        color = BurnerGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// 1. Cockpit Tactical HUD Layout
@Composable
private fun CockpitHudLayout(
    bars: List<Float>,
    pttState: PttState,
    transmitElapsedSec: Float,
    accentColor: Color,
    glowColor: Color,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    userIdentity: UserIdentity
) {
    val isTransmitting = pttState == PttState.TRANSMITTING

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TacticalSurface)
            .border(2.dp, if (isTransmitting) PttHotRed else accentColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cockpit Telemetry Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Flight,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MIL-SPEC COCKPIT HUD",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = if (isTransmitting) "TX LEVEL: +18.4 dB" else "RX SENSITIVITY: 99.8%",
                color = if (isTransmitting) PttHotRed else TacticalCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Waterfall Spectrogram Canvas
        SpectrumVisualizer(
            bars = bars,
            isTransmitting = isTransmitting,
            isIncoming = pttState == PttState.INCOMING_TRANSMISSION,
            modifier = Modifier.testTag("cockpit_spectrogram")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Cockpit Armored PTT Trigger
        PttTactileButton(
            pttState = pttState,
            transmitElapsedSec = transmitElapsedSec,
            onPress = onPress,
            onRelease = onRelease,
            themeScheme = userIdentity.themeScheme,
            modifier = Modifier.testTag("cockpit_ptt_button")
        )
    }
}

// 2. Cyberpunk Matrix HUD Layout
@Composable
private fun CyberSynthLayout(
    bars: List<Float>,
    pttState: PttState,
    transmitElapsedSec: Float,
    accentColor: Color,
    glowColor: Color,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    userIdentity: UserIdentity
) {
    val isTransmitting = pttState == PttState.TRANSMITTING

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF030712))
            .border(2.dp, if (isTransmitting) PttHotRed else glowColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = glowColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CYBERPUNK MATRIX TERMINAL",
                    color = glowColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "HEX: 0x7F99B2",
                color = TacticalTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Oscilloscope Waveform Bars
        SpectrumVisualizer(
            bars = bars,
            isTransmitting = isTransmitting,
            isIncoming = pttState == PttState.INCOMING_TRANSMISSION,
            modifier = Modifier.testTag("cyber_spectrum")
        )

        Spacer(modifier = Modifier.height(14.dp))

        PttTactileButton(
            pttState = pttState,
            transmitElapsedSec = transmitElapsedSec,
            onPress = onPress,
            onRelease = onRelease,
            themeScheme = userIdentity.themeScheme,
            modifier = Modifier.testTag("cyber_ptt_button")
        )
    }
}

// 3. Stealth Minimalist Layout
@Composable
private fun StealthMinimalistLayout(
    pttState: PttState,
    transmitElapsedSec: Float,
    accentColor: Color,
    glowColor: Color,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    userIdentity: UserIdentity
) {
    val isTransmitting = pttState == PttState.TRANSMITTING

    val stealthPulseTransition = rememberInfiniteTransition(label = "stealth_pulse")
    val stealthMicScale by stealthPulseTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isTransmitting) 1.25f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stealth_mic_scale"
    )
    val stealthDotBlink by stealthPulseTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stealth_dot_blink"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TacticalDarkBg)
            .border(1.dp, if (isTransmitting) PttHotRed else TacticalCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STEALTH LOW-PROFILE TOUCHPAD",
                color = TacticalTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = if (isTransmitting) "TRANSMITTING %.1fs".format(transmitElapsedSec) else "STANDBY",
                color = if (isTransmitting) PttHotRed else TacticalTextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Full-width ergonomic touch trigger bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (isTransmitting) PttHotRed else accentColor)
                .border(2.dp, if (isTransmitting) PttRedGlow else glowColor, RoundedCornerShape(18.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onPress()
                            tryAwaitRelease()
                            onRelease()
                        }
                    )
                }
                .testTag("stealth_ergonomic_ptt_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "PTT Mic",
                    tint = TacticalDarkBg,
                    modifier = Modifier
                        .size(36.dp)
                        .scale(stealthMicScale)
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (isTransmitting) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TacticalDarkBg.copy(alpha = stealthDotBlink))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TRANSMITTING HOT (RECORDING)",
                            color = TacticalDarkBg,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    Text(
                        text = "HOLD ANYWHERE TO TRANSMIT",
                        color = TacticalDarkBg,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// 4. Retro 1980s CB Radio Layout
@Composable
private fun RetroCbRadioLayout(
    bars: List<Float>,
    pttState: PttState,
    transmitElapsedSec: Float,
    accentColor: Color,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    userIdentity: UserIdentity
) {
    val isTransmitting = pttState == PttState.TRANSMITTING

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1917))
            .border(2.dp, BurnerGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // CB Radio Header with Chrome Rivets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SettingsRemote,
                    contentDescription = null,
                    tint = BurnerGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "VINTAGE CB TRANSCEIVER 1980",
                    color = BurnerGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "CH 19 • SQUELCH 40%",
                color = TacticalAmber,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Analog Needle Meter Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF292524))
                .border(1.dp, BurnerGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val needleValue = if (isTransmitting) 0.85f else if (bars.isNotEmpty()) (bars.average().toFloat() / 100f).coerceIn(0.1f, 0.9f) else 0.15f
                val startX = size.width * 0.5f
                val startY = size.height * 0.9f
                val endX = size.width * (0.1f + needleValue * 0.8f)
                val endY = size.height * 0.2f

                drawLine(
                    color = if (isTransmitting) Color.Red else Color(0xFFF59E0B),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
            Text(
                text = if (isTransmitting) "VU POWER: S-9+30dB" else "ANALOG VU SQUELCH METER",
                color = BurnerGold,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        PttTactileButton(
            pttState = pttState,
            transmitElapsedSec = transmitElapsedSec,
            onPress = onPress,
            onRelease = onRelease,
            themeScheme = userIdentity.themeScheme,
            modifier = Modifier.testTag("cb_ptt_button")
        )
    }
}
