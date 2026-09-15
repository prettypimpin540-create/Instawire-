package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary

/**
 * Custom Material 3 Rationale Dialog explaining why RECORD_AUDIO is essential
 * for Push-to-Talk voice transmission, acoustic noise suppression, and encrypted broadcasting.
 */
@Composable
fun AudioPermissionRationaleDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequestPermission: () -> Unit,
    isPermanentlyDenied: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_permission_rationale_dialog"),
        shape = RoundedCornerShape(20.dp),
        containerColor = TacticalSurfaceElevated,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPermanentlyDenied) Color(0xFFEF4444).copy(alpha = 0.15f)
                            else PttNeonGreen.copy(alpha = 0.15f)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isPermanentlyDenied) Color(0xFFEF4444) else PttNeonGreen,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPermanentlyDenied) Icons.Default.Warning else Icons.Default.Mic,
                        contentDescription = "Microphone Permission Required",
                        tint = if (isPermanentlyDenied) Color(0xFFEF4444) else PttNeonGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isPermanentlyDenied) "MICROPHONE ACCESS BLOCKED" else "MICROPHONE PERMISSION NEEDED",
                    color = TacticalTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (isPermanentlyDenied) {
                        "InstaWire cannot transmit voice because microphone access was permanently disabled in Android system settings. Please grant microphone access to use Push-to-Talk."
                    } else {
                        "To transmit encrypted voice messages and broadcast across walkie-talkie channels, InstaWire requires microphone access."
                    },
                    color = TacticalTextMuted,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Feature Highlights
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TacticalCardBorder)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        RationaleFeatureItem(
                            icon = Icons.Default.VolumeUp,
                            title = "16kHz Wideband Voice",
                            description = "Studio-fidelity push-to-talk audio transmission."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        RationaleFeatureItem(
                            icon = Icons.Default.Security,
                            title = "Zero Eavesdropping",
                            description = "Audio is ONLY recorded while actively holding the PTT button."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        RationaleFeatureItem(
                            icon = Icons.Default.Mic,
                            title = "Hardware Noise Suppression",
                            description = "Filters background noise and acoustic echo on-chip."
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isPermanentlyDenied) {
                        openAppSettings(context)
                        onDismissRequest()
                    } else {
                        onConfirmRequestPermission()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPermanentlyDenied) Color(0xFFEF4444) else PttNeonGreen,
                    contentColor = TacticalDarkBg
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_mic_permission_button")
            ) {
                Icon(
                    imageVector = if (isPermanentlyDenied) Icons.Default.Settings else Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPermanentlyDenied) "OPEN APP SETTINGS" else "ALLOW MICROPHONE",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dismiss_mic_permission_button")
            ) {
                Text(
                    text = "LISTEN ONLY (CONTINUE WITHOUT MIC)",
                    color = TacticalTextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    )
}

@Composable
private fun RationaleFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PttNeonGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PttNeonGreen,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = TacticalTextPrimary,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = TacticalTextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
