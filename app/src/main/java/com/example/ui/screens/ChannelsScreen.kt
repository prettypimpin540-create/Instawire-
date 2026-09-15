package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.ui.WalkieTarget
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
fun ChannelsScreen(
    channels: List<Channel>,
    activeTarget: WalkieTarget?,
    onSelectChannel: (Channel) -> Unit,
    onOpenAddChannel: () -> Unit,
    onOpenJoinChannel: () -> Unit,
    onDeleteChannel: (String) -> Unit,
    onOpenSafetyKey: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var channelToDelete by remember { mutableStateOf<Channel?>(null) }

    if (channelToDelete != null) {
        val target = channelToDelete!!
        AlertDialog(
            onDismissRequest = { channelToDelete = null },
            title = {
                Text(
                    text = "DELETE CHANNEL?",
                    color = TacticalTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove channel '${target.name}'? You will need its frequency code (${target.displayFrequencyCode}) to re-join.",
                    color = TacticalTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteChannel(target.id)
                        channelToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PttHotRed)
                ) {
                    Text("DELETE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { channelToDelete = null }) {
                    Text("CANCEL", color = TacticalTextSecondary)
                }
            },
            containerColor = TacticalDarkBg
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Section
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Custom Channels",
                            color = TacticalTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Create custom frequencies or join by code",
                            color = TacticalTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onOpenJoinChannel,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCyan),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TacticalCyan),
                            modifier = Modifier.testTag("join_channel_header_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = "Join Code",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Join Code",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        }

                        Button(
                            onClick = onOpenAddChannel,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TacticalCyan,
                                contentColor = TacticalDarkBg
                            ),
                            modifier = Modifier.testTag("add_channel_header_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Channel",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Create",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Empty state when no channels exist
            if (channels.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, TacticalCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(TacticalCyan.copy(alpha = 0.15f))
                                    .border(1.2.dp, TacticalCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radio,
                                    contentDescription = null,
                                    tint = TacticalCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "NO CUSTOM CHANNELS YET",
                                color = TacticalTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "InstaWire custom channels give you complete control. Create your own private squad channel and share its frequency code, or enter a code from a teammate to tune in.",
                                color = TacticalTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onOpenAddChannel,
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TacticalCyan,
                                        contentColor = TacticalDarkBg
                                    )
                                ) {
                                    Text(
                                        text = "+ CREATE CHANNEL",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = onOpenJoinChannel,
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCyan),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TacticalCyan)
                                ) {
                                    Text(
                                        text = "# ENTER CODE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // List of user custom channels
                items(channels) { channel ->
                    val isSelected = activeTarget is WalkieTarget.ChannelTarget && activeTarget.channel.id == channel.id
                    val frequencyCode = channel.displayFrequencyCode

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) TacticalCyan else TacticalCardBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectChannel(channel) }
                            .testTag("channel_card_${channel.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) TacticalSurfaceElevated else TacticalSurface
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: Icon, Channel Name, Frequency & Active Badge / Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) TacticalCyan.copy(alpha = 0.2f)
                                                else TacticalDarkBg
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) TacticalCyan else TacticalCardBorder,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Radio,
                                            contentDescription = channel.name,
                                            tint = if (isSelected) TacticalCyan else TacticalTextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = channel.name,
                                            color = if (isSelected) TacticalCyan else TacticalTextPrimary,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = channel.frequency,
                                            color = TacticalTextMuted,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(TacticalCyan.copy(alpha = 0.2f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE LINE",
                                                color = TacticalCyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }

                                    IconButton(
                                        onClick = { channelToDelete = channel },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Channel",
                                            tint = TacticalTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Frequency Code Box with Tap to Copy/Share
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TacticalDarkBg)
                                    .border(1.dp, TacticalCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        copyAndShareCode(context, channel)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = null,
                                        tint = TacticalCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "FREQUENCY CODE",
                                            color = TacticalTextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = frequencyCode,
                                            color = TacticalCyan,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TacticalCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share Code",
                                        tint = TacticalCyan,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "SHARE",
                                        color = TacticalCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            if (channel.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = channel.description,
                                    color = TacticalTextSecondary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bottom bar: Security Key and Tune In button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TacticalDarkBg)
                                        .clickable { onOpenSafetyKey(channel) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Encrypted",
                                        tint = if (channel.isEncrypted) PttNeonGreen else TacticalTextMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (channel.isEncrypted) "AES-256 E2EE" else "OPEN FREQ",
                                        color = if (channel.isEncrypted) PttNeonGreen else TacticalTextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                if (!isSelected) {
                                    Button(
                                        onClick = { onSelectChannel(channel) },
                                        modifier = Modifier.height(32.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TacticalCyan.copy(alpha = 0.2f),
                                            contentColor = TacticalCyan
                                        ),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                    ) {
                                        Text(
                                            text = "TUNE IN",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
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

private fun copyAndShareCode(context: Context, channel: Channel) {
    val code = channel.displayFrequencyCode
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Frequency Code", code)
    clipboard.setPrimaryClip(clip)

    val shareText = "📻 Tune into my InstaWire Walkie-Talkie channel!\n\nChannel: ${channel.name}\nFrequency Code: $code\nFrequency: ${channel.frequency}\n\nTo join, open InstaWire -> Channels -> Join Code, and enter '$code'."

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Frequency Code $code")
    context.startActivity(shareIntent)
    Toast.makeText(context, "Copied code '$code' & opening share sheet", Toast.LENGTH_SHORT).show()
}
