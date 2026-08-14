package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WifiCalling3
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.NoiseFilterMode
import com.example.data.model.UserIdentity
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

@Composable
fun NoiseCancellationDialog(
    userIdentity: UserIdentity,
    onDismiss: () -> Unit,
    onToggleNoiseCancellation: () -> Unit,
    onSelectFilterMode: (NoiseFilterMode) -> Unit,
    onToggleChirp: () -> Unit,
    onToggleRogerBeep: () -> Unit,
    onToggleZeroLogs: () -> Unit
) {
    var squelchValue by remember { mutableFloatStateOf(userIdentity.squelchLevel) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, TacticalCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .testTag("noise_cancellation_dialog"),
            colors = CardDefaults.cardColors(containerColor = TacticalDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TacticalCyan.copy(alpha = 0.2f))
                                .border(1.dp, TacticalCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "DSP Audio",
                                tint = TacticalCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DSP AUDIO & NOISE FILTER",
                                color = TacticalCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Crystal Clear Audio & Noise Cancellation",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_noise_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Master Noise Cancellation Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TacticalSurfaceElevated)
                        .border(
                            1.dp,
                            if (userIdentity.noiseFilterEnabled) PttNeonGreen.copy(alpha = 0.5f) else TacticalCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Active Noise Cancellation",
                            color = TacticalTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Real-time AI DSP filters out background static, wind & traffic",
                            color = TacticalTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = userIdentity.noiseFilterEnabled,
                        onCheckedChange = { onToggleNoiseCancellation() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PttNeonGreen,
                            checkedTrackColor = PttGreenDark
                        ),
                        modifier = Modifier.testTag("master_noise_switch")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Presets
                Text(
                    text = "NOISE REDUCTION PRESETS",
                    color = TacticalTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))

                NoiseFilterMode.values().forEach { mode ->
                    val isSelected = userIdentity.noiseFilterMode == mode
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) TacticalSurfaceElevated else TacticalSurface)
                            .border(
                                1.dp,
                                if (isSelected) TacticalCyan else TacticalCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectFilterMode(mode) }
                            .padding(12.dp)
                            .testTag("noise_mode_${mode.name}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) TacticalCyan else TacticalTextMuted)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = mode.label,
                                    color = if (isSelected) TacticalCyan else TacticalTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = mode.description,
                                    color = TacticalTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Squelch Level Dial / Slider
                Text(
                    text = "RADIO SQUELCH SENSITIVITY",
                    color = TacticalTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurface)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Squelch Gate Threshold",
                                color = TacticalTextPrimary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${(squelchValue * 100).toInt()}% CLAMP",
                                color = TacticalCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Slider(
                            value = squelchValue,
                            onValueChange = { squelchValue = it },
                            colors = SliderDefaults.colors(
                                thumbColor = TacticalCyan,
                                activeTrackColor = TacticalCyan,
                                inactiveTrackColor = TacticalCardBorder
                            ),
                            modifier = Modifier.testTag("squelch_slider")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Walkie Chirp & Roger Beeps
                Text(
                    text = "TACTILE AUDIO EFFECTS",
                    color = TacticalTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Nextel / TiKL Chirp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TiKL Channel Open Chirp",
                            color = TacticalTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "High-pitch synthetic tone upon pressing PTT",
                            color = TacticalTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = userIdentity.chirpSoundEnabled,
                        onCheckedChange = { onToggleChirp() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TacticalCyan,
                            checkedTrackColor = TacticalSurfaceElevated
                        ),
                        modifier = Modifier.testTag("chirp_switch")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Military Roger Beep
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Walkie Roger Beep",
                            color = TacticalTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "End-of-transmission tone upon releasing PTT",
                            color = TacticalTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = userIdentity.rogerBeepEnabled,
                        onCheckedChange = { onToggleRogerBeep() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TacticalCyan,
                            checkedTrackColor = TacticalSurfaceElevated
                        ),
                        modifier = Modifier.testTag("roger_beep_switch")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Zero-Logs Anonymous policy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zero Metadata Saved (RAM Only)",
                            color = TacticalTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Auto-wipe audio packets after ${userIdentity.ephemeralTimeoutSeconds} seconds",
                            color = TacticalTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = userIdentity.zeroLogsEnabled,
                        onCheckedChange = { onToggleZeroLogs() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PttNeonGreen,
                            checkedTrackColor = PttGreenDark
                        ),
                        modifier = Modifier.testTag("zero_logs_switch")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_noise_settings_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TacticalCyan,
                        contentColor = TacticalDarkBg
                    )
                ) {
                    Text(
                        text = "APPLY DSP SETTINGS",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
