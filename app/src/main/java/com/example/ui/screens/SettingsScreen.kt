package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppThemeScheme
import com.example.data.model.CoinCashoutTransaction
import com.example.data.model.GiftTransaction
import com.example.data.model.PttSoundProfile
import com.example.data.model.UserIdentity
import com.example.data.model.UserProfile
import com.example.data.model.WalkieLayoutType
import com.example.ui.theme.BurnerGold
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
fun SettingsScreen(
    userIdentity: UserIdentity,
    userProfile: UserProfile? = null,
    cashoutHistory: List<CoinCashoutTransaction> = emptyList(),
    giftHistory: List<GiftTransaction> = emptyList(),
    onSetLayoutType: (WalkieLayoutType) -> Unit = {},
    onSetThemeScheme: (AppThemeScheme) -> Unit,
    onSetSoundProfile: (PttSoundProfile) -> Unit,
    onPreviewSound: (PttSoundProfile, Boolean) -> Unit,
    onOpenThemeLayoutSelector: () -> Unit = {},
    onToggleChirp: () -> Unit,
    onToggleRogerBeep: () -> Unit,
    onToggleHapticFeedback: () -> Unit = {},
    onToggleAudioRouting: () -> Unit = {},
    onToggleSleepModeListening: () -> Unit = {},
    onToggleHardwareVolumePtt: () -> Unit = {},
    onToggleHardwareVolumePttToggleMode: () -> Unit = {},
    checkCallsignConflict: (String) -> com.example.data.model.CallsignConflict = { com.example.data.model.CallsignConflict(false) },
    onToggleBackgroundMonitoring: () -> Unit = {},
    onToggleBackgroundAudioBeep: () -> Unit = {},
    onToggleZeroLogs: () -> Unit = {},
    onSetEphemeralTimeout: (Int) -> Unit = {},
    onSetCallsign: (String) -> Unit,
    onSetVolumeLevel: (Float) -> Unit,
    onSetSquelchLevel: (Float) -> Unit = {},
    onConnectToSupport: () -> Unit = {},
    onOpenSubscriptionPlans: () -> Unit = {},
    onOpenNoiseCancelModal: () -> Unit = {},
    onOpenPhoneConfirmModal: () -> Unit = {},
    onOpenTermsOfService: () -> Unit = {},
    onWipeAllLogs: () -> Unit = {},
    onOpenCashoutModal: () -> Unit = {},
    onOpenCoinShopModal: () -> Unit = {},
    onOpenBuyCoinsModal: () -> Unit = {},
    onRunAudioLoopbackTest: () -> Unit = {},
    onRunLatencyDiagnostic: () -> Unit = {},
    onResetSettingsToDefaults: () -> Unit,
    onOpenWelcomeScreen: () -> Unit = {},
    onOpenVerificationScreen: () -> Unit = {},
    onOpenBurnerManager: () -> Unit = {},
    onOpenWorldwideChatrooms: () -> Unit = {},
    onPanicWipeAllData: () -> Unit = {},
    isLoopbackRecording: Boolean = false,
    loopbackStatus: String? = null,
    isDiagnosticsRunning: Boolean = false,
    diagnosticsResult: String? = null,
    audioCaptureState: com.example.service.AudioCaptureState = com.example.service.AudioCaptureState(),
    onToggleScreenLockedBroadcast: () -> Unit = {},
    onToggleBroadcastMute: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    var editName by remember(userIdentity.callsign) { mutableStateOf(userIdentity.callsign) }
    var hasNameSaved by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Settings",
                color = TacticalTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Personalize your walkie-talkie name, sounds, and look",
                color = TacticalTextMuted,
                fontSize = 12.5.sp
            )
        }

        // CARD 1: PROFILE / NAME
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(1.dp, accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Your Walkie Name",
                                color = TacticalTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Displayed to others on air",
                                color = TacticalTextSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val callsignConflict = checkCallsignConflict(editName)
                    val isConflict = callsignConflict.isTaken && !editName.trim().equals(userIdentity.callsign.trim(), ignoreCase = true)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = {
                                editName = it
                                hasNameSaved = false
                            },
                            label = { Text("Display Name / Callsign") },
                            singleLine = true,
                            isError = isConflict,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isConflict) PttHotRed else accentColor,
                                unfocusedBorderColor = if (isConflict) PttHotRed.copy(alpha = 0.6f) else TacticalCardBorder,
                                focusedTextColor = TacticalTextPrimary,
                                unfocusedTextColor = TacticalTextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_name_input")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                if (editName.isNotBlank() && !isConflict) {
                                    onSetCallsign(editName.trim())
                                    hasNameSaved = true
                                }
                            },
                            enabled = !isConflict && editName.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasNameSaved) PttNeonGreen else accentColor,
                                contentColor = Color.Black,
                                disabledContainerColor = TacticalSurfaceElevated,
                                disabledContentColor = TacticalTextMuted
                            ),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("settings_save_name_button")
                        ) {
                            Icon(
                                imageVector = if (hasNameSaved) Icons.Default.Check else Icons.Default.CheckCircle,
                                contentDescription = "Save",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (hasNameSaved) "Saved" else "Save",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }
                    }

                    if (isConflict) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(PttHotRed.copy(alpha = 0.15f))
                                .border(1.dp, PttHotRed.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = PttHotRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Callsign already claimed by ${callsignConflict.takenBy}! Two operators cannot use the same callsign.",
                                color = PttHotRed,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else if (editName.isNotBlank() && !editName.trim().equals(userIdentity.callsign.trim(), ignoreCase = true)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "✓ Unique callsign available to claim",
                            color = PttNeonGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // CARD 2: AUDIO & SOUND EFFECTS
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TacticalCyan.copy(alpha = 0.15f))
                                .border(1.dp, TacticalCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = TacticalCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Audio & Sounds",
                                color = TacticalTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Walkie beeps, chirps, and speaker volume",
                                color = TacticalTextSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Start Talk Chirp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TacticalSurfaceElevated)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Talk Button Chime",
                                color = TacticalTextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Plays a quick chirp when you press to talk",
                                color = TacticalTextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onPreviewSound(userIdentity.soundProfile, true) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Test sound",
                                    tint = TacticalCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Switch(
                                checked = userIdentity.chirpSoundEnabled,
                                onCheckedChange = { onToggleChirp() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = accentColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Roger Beep
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TacticalSurfaceElevated)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "End Roger Beep",
                                color = TacticalTextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Plays a confirmation beep when you release talk",
                                color = TacticalTextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onPreviewSound(userIdentity.soundProfile, false) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Test roger beep",
                                    tint = TacticalCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Switch(
                                checked = userIdentity.rogerBeepEnabled,
                                onCheckedChange = { onToggleRogerBeep() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = accentColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // PTT Haptic Vibration Feedback
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TacticalSurfaceElevated)
                            .padding(12.dp)
                            .testTag("settings_haptic_feedback_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Vibration,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PTT Haptic Vibration",
                                    color = TacticalTextPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Vibration confirmation when starting and stopping speech",
                                color = TacticalTextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = userIdentity.hapticFeedbackEnabled,
                            onCheckedChange = { onToggleHapticFeedback() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = accentColor
                            ),
                            modifier = Modifier.testTag("settings_haptic_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Audio Routing (Earpiece vs Speakerphone)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TacticalSurfaceElevated)
                            .padding(12.dp)
                            .testTag("settings_audio_routing_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (userIdentity.audioRoutingToEarpiece) Icons.Default.Hearing else Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = if (userIdentity.audioRoutingToEarpiece) TacticalCyan else accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Audio Output Device",
                                    color = TacticalTextPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = if (userIdentity.audioRoutingToEarpiece)
                                    "Discreet Mode (Phone Earpiece)"
                                else
                                    "Loud Mode (Speakerphone)",
                                color = TacticalTextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = onToggleAudioRouting,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (userIdentity.audioRoutingToEarpiece) TacticalCyan else accentColor,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("settings_audio_routing_switch_button")
                        ) {
                            Text(
                                text = if (userIdentity.audioRoutingToEarpiece) "EARPIECE" else "SPEAKER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sound Profile Choices
                    Text(
                        text = "CHIME STYLE",
                        color = TacticalTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            listOf(
                                PttSoundProfile.NEXTEL_TACTICAL,
                                PttSoundProfile.RETRO_VHF,
                                PttSoundProfile.CYBER_SYNTH,
                                PttSoundProfile.STEALTH_PULSE
                            )
                        ) { prof ->
                            val isSelected = userIdentity.soundProfile == prof
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) accentColor.copy(alpha = 0.2f) else TacticalSurfaceElevated)
                                    .border(1.dp, if (isSelected) accentColor else TacticalCardBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        onSetSoundProfile(prof)
                                        onPreviewSound(prof, true)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = prof.title,
                                    color = if (isSelected) accentColor else TacticalTextPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Volume level
                    Text(
                        text = "SPEAKER VOLUME: ${(userIdentity.volumeLevel * 100).toInt()}%",
                        color = TacticalTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = userIdentity.volumeLevel,
                        onValueChange = { onSetVolumeLevel(it) },
                        colors = SliderDefaults.colors(
                            thumbColor = accentColor,
                            activeTrackColor = accentColor,
                            inactiveTrackColor = TacticalSurfaceElevated
                        )
                    )
                }
            }
        }

        // CARD 2.5: HARDWARE BUTTONS & VOLUME UP PTT
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = if (userIdentity.hardwareVolumePttEnabled) accentColor.copy(alpha = 0.6f) else TacticalCardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("hardware_volume_ptt_card"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Volume Up PTT Key",
                                    color = TacticalTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Use physical Volume Up to transmit",
                                    color = TacticalTextSecondary,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                        Switch(
                            checked = userIdentity.hardwareVolumePttEnabled,
                            onCheckedChange = { onToggleHardwareVolumePtt() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = accentColor
                            ),
                            modifier = Modifier.testTag("settings_volume_ptt_switch")
                        )
                    }

                    if (userIdentity.hardwareVolumePttEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Toggle Mode vs Momentary (Hold to talk) Mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(TacticalSurfaceElevated)
                                .border(
                                    width = 1.dp,
                                    color = if (userIdentity.hardwareVolumePttToggleMode) PttNeonGreen.copy(alpha = 0.5f) else TacticalCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                                .testTag("settings_volume_toggle_mode_row"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Toggle PTT Mode",
                                        color = TacticalTextPrimary,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (userIdentity.hardwareVolumePttToggleMode)
                                                    PttNeonGreen.copy(alpha = 0.2f)
                                                else
                                                    TacticalDarkBg
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (userIdentity.hardwareVolumePttToggleMode) "TOGGLE (TAP)" else "MOMENTARY (HOLD)",
                                            color = if (userIdentity.hardwareVolumePttToggleMode) PttNeonGreen else TacticalTextMuted,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (userIdentity.hardwareVolumePttToggleMode)
                                        "Tap Volume Up once to transmit, tap again to stop (hands-free)"
                                    else
                                        "Hold Volume Up to talk, release to stop transmitting (traditional PTT)",
                                    color = TacticalTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = userIdentity.hardwareVolumePttToggleMode,
                                onCheckedChange = { onToggleHardwareVolumePttToggleMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PttNeonGreen
                                ),
                                modifier = Modifier.testTag("settings_volume_toggle_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Info Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalDarkBg)
                                .border(1.dp, TacticalCardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Hearing,
                                    contentDescription = null,
                                    tint = if (userIdentity.hardwareVolumePttToggleMode) PttNeonGreen else accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (userIdentity.hardwareVolumePttToggleMode)
                                        "ACTIVE: Tap phone Volume Up button once to begin transmitting, tap once more to release."
                                    else
                                        "ACTIVE: Press and hold phone Volume Up button to talk. Audio transmits while pressed.",
                                    color = TacticalTextSecondary,
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // CARD 3: THEME & COLOR ACCENTS
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFA855F7).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFA855F7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "App Theme & Color",
                                color = TacticalTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Choose your favorite accent glow",
                                color = TacticalTextSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            listOf(
                                AppThemeScheme.TACTICAL_GREEN,
                                AppThemeScheme.STEALTH_CYAN,
                                AppThemeScheme.CYBER_AMBER,
                                AppThemeScheme.CRIMSON_ALERT
                            )
                        ) { theme ->
                            val isSelected = userIdentity.themeScheme == theme
                            val col = Color(theme.primaryHex)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TacticalSurfaceElevated)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) col else TacticalCardBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onSetThemeScheme(theme) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(col)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = theme.title,
                                        color = if (isSelected) col else TacticalTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // CARD 3.5: WORKMANAGER BATTERY OPTIMIZATION & SLEEP MODE AUDIO
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = if (userIdentity.sleepModeBackgroundListeningEnabled) TacticalCyan.copy(alpha = 0.6f) else TacticalCardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("sleep_mode_audio_card"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(TacticalCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryChargingFull,
                                    contentDescription = "Sleep Mode Audio",
                                    tint = TacticalCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sleep Mode Audio",
                                    color = TacticalTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "WorkManager Battery Optimization",
                                    color = TacticalCyan,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        Switch(
                            checked = userIdentity.sleepModeBackgroundListeningEnabled,
                            onCheckedChange = { onToggleSleepModeListening() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TacticalCyan
                            ),
                            modifier = Modifier.testTag("sleep_mode_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Allows your device to hear incoming PTT voice transmissions even when the screen is off or in deep sleep. Uses Android WorkManager with power-efficient radio polling constraints to minimize battery consumption.",
                        color = TacticalTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalSurfaceElevated)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("SCHEDULE DISPATCHER", color = TacticalTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("Android WorkManager", color = TacticalTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalSurfaceElevated)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("STANDBY STATUS", color = TacticalTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    if (userIdentity.sleepModeBackgroundListeningEnabled) "Active • Standby Rx" else "Disabled",
                                    color = if (userIdentity.sleepModeBackgroundListeningEnabled) TacticalCyan else TacticalTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // CARD 5: HOW IT WORKS & RESET
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = TacticalCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How to Use InstaWire",
                            color = TacticalTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. Pick a channel from the Channels tab (Channel 1 is default).\n" +
                               "2. Hold the big green button on the Talk screen while speaking.\n" +
                               "3. Let go of the button to hear replies in real-time.",
                        color = TacticalTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onResetSettingsToDefaults,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TacticalSurfaceElevated,
                            contentColor = TacticalTextSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("settings_reset_defaults_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reset All Settings to Default",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "InstaWire • Simple Push-to-Talk Walkie Talkie",
                    color = TacticalTextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
