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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transmission
import com.example.data.model.UserIdentity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransmissionsLogScreen(
    transmissions: List<Transmission>,
    userIdentity: UserIdentity,
    onPlayTransmission: (Transmission) -> Unit,
    onWipeAllLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SECURE LOGS",
                            color = TacticalTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Ephemeral Memory • Auto-Purge Active",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onWipeAllLogs,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PttHotRed.copy(alpha = 0.2f),
                            contentColor = PttHotRed
                        ),
                        modifier = Modifier
                            .border(1.dp, PttHotRed, RoundedCornerShape(8.dp))
                            .testTag("wipe_logs_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Wipe Logs",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "WIPE ALL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Zero-Knowledge Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurfaceElevated)
                        .border(1.dp, PttNeonGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = PttNeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ZERO-METADATA ANONYMOUS MEMORY",
                                color = PttNeonGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Transmissions automatically erased from RAM after ${userIdentity.ephemeralTimeoutSeconds} seconds to preserve anonymity.",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (transmissions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TacticalSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Clean Memory",
                                tint = TacticalTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "MEMORY STERILIZED",
                                color = TacticalTextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "No active voice packets in local ephemeral buffer",
                                color = TacticalTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                items(transmissions) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, TacticalCardBorder, RoundedCornerShape(12.dp))
                            .clickable { onPlayTransmission(item) }
                            .testTag("log_transmission_${item.id}"),
                        colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TacticalSurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = PttNeonGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = item.senderName,
                                        color = TacticalTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Duration: %.1fs • %s".format(item.durationSeconds, item.senderNumber),
                                        color = TacticalCyan,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Sent: ${dateFormat.format(Date(item.timestamp))}",
                                        color = TacticalTextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TacticalDarkBg)
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = PttNeonGreen,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "E2EE",
                                    color = PttNeonGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
