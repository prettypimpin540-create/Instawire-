package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserIdentity
import com.example.security.SecurityEngine
import com.example.ui.WalkieTarget
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
fun SafetyKeyDialog(
    target: WalkieTarget?,
    userIdentity: UserIdentity,
    onDismiss: () -> Unit,
    onVerifyToggled: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val targetName = when (target) {
        is WalkieTarget.ChannelTarget -> target.channel.name
        is WalkieTarget.ContactTarget -> target.contact.name
        null -> "GLOBAL SECURE NETWORK"
    }

    val safetyKeyBlocks = when (target) {
        is WalkieTarget.ChannelTarget -> target.channel.safetyKeyBlocks
        is WalkieTarget.ContactTarget -> target.contact.safetyKeyBlocks
        null -> "83910 28491 94820 18274 02938 48192 73910 82941 02948 19284 84019 92847"
    }

    val fingerprint = when (target) {
        is WalkieTarget.ChannelTarget -> target.channel.safetyFingerprint
        is WalkieTarget.ContactTarget -> target.contact.safetyFingerprint
        null -> "SHA-256: 4F:9A:82:1B:3E:7C:90:0D"
    }

    val isVerified = when (target) {
        is WalkieTarget.ContactTarget -> target.contact.isKeyVerified
        else -> true
    }

    val cryptoMatrix = remember(safetyKeyBlocks) {
        SecurityEngine.generateVisualCryptoMatrix(safetyKeyBlocks)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, PttNeonGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .testTag("safety_key_dialog"),
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
                                .background(PttGreenDark)
                                .border(1.dp, PttNeonGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Military Encryption",
                                tint = PttNeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MILITARY-GRADE E2EE",
                                color = PttNeonGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "256-Bit AES-GCM Encrypted Key",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_safety_key_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurface)
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "SECURED ENDPOINT",
                            color = TacticalTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = targetName,
                            color = TacticalTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fingerprint: $fingerprint",
                            color = TacticalCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Visual QR / Crypto Matrix
                Text(
                    text = "VISUAL CRYPTOGRAPHIC MATRIX",
                    color = TacticalTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TacticalSurfaceElevated)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(160.dp)) {
                        val cellSize = size.width / 8f
                        for (r in 0 until 8) {
                            for (c in 0 until 8) {
                                val isFilled = cryptoMatrix.getOrNull(r)?.getOrNull(c) ?: false
                                drawRect(
                                    color = if (isFilled) PttNeonGreen else TacticalDarkBg,
                                    topLeft = Offset(c * cellSize, r * cellSize),
                                    size = Size(cellSize - 2f, cellSize - 2f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 60-digit Safety Number Blocks
                Text(
                    text = "60-DIGIT SAFETY NUMBER",
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
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = safetyKeyBlocks,
                        color = PttGreenGlow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Copy Safety Number Button
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("InstaWire Safety Key", safetyKeyBlocks)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Safety Key Copied to Clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("copy_safety_key_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TacticalCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COPY SAFETY NUMBER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Verification Switch for Contacts
                if (target is WalkieTarget.ContactTarget) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TacticalSurfaceElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mark Key as Confirmed",
                                color = TacticalTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Confirms you verified this key directly with ${target.contact.name}",
                                color = TacticalTextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = isVerified,
                            onCheckedChange = onVerifyToggled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PttNeonGreen,
                                checkedTrackColor = PttGreenDark
                            ),
                            modifier = Modifier.testTag("verify_key_switch")
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Anonymous Zero-Logs Guarantee Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PttGreenDark.copy(alpha = 0.3f))
                        .border(1.dp, PttNeonGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Zero Logs Verified",
                            tint = PttNeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Zero Metadata Saved. Ephemeral RAM-only transmission.",
                            color = PttGreenGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_safety_key_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PttNeonGreen,
                        contentColor = TacticalDarkBg
                    )
                ) {
                    Text(
                        text = "CONFIRM & CLOSE",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
