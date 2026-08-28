package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.data.model.Channel
import com.example.ui.WalkieTarget
import com.example.ui.dialogs.EmergencyDisclaimerDialog
import com.example.ui.theme.BurnerGold
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttHotRed
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.PttRedGlow
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
    onOpenSafetyKey: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingEmergencyChannel by remember { mutableStateOf<Channel?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All", "Community", "Travel & Road", "Marine", "Search & Rescue", "Outdoors", "Emergency")

    val filteredChannels = if (selectedCategoryFilter == null || selectedCategoryFilter == "All") {
        channels
    } else {
        channels.filter { it.channelCategory.equals(selectedCategoryFilter, ignoreCase = true) || (selectedCategoryFilter == "Emergency" && it.isEmergency) }
    }

    if (pendingEmergencyChannel != null) {
        EmergencyDisclaimerDialog(
            channel = pendingEmergencyChannel!!,
            onAccept = {
                val chan = pendingEmergencyChannel!!
                pendingEmergencyChannel = null
                onSelectChannel(chan)
            },
            onDismiss = {
                pendingEmergencyChannel = null
            }
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
                            text = "RADIO CHANNELS",
                            color = TacticalTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Practical Real-Life Two-Way Voice Frequencies",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onOpenAddChannel,
                        shape = RoundedCornerShape(8.dp),
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
                            text = "CUSTOM CH",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = (selectedCategoryFilter == null && category == "All") || selectedCategoryFilter == category
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryFilter = if (category == "All") null else category
                            },
                            label = { Text(category, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (category == "Emergency") PttHotRed else TacticalCyan,
                                selectedLabelColor = if (category == "Emergency") Color.White else Color.Black,
                                containerColor = TacticalSurface,
                                labelColor = TacticalTextSecondary
                            )
                        )
                    }
                }
            }

            items(filteredChannels) { channel ->
                val isSelected = activeTarget is WalkieTarget.ChannelTarget && activeTarget.channel.id == channel.id
                val isEmergency = channel.isEmergency || channel.id.contains("emergency", ignoreCase = true)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            when {
                                isSelected && isEmergency -> PttHotRed
                                isSelected -> TacticalCyan
                                isEmergency -> PttHotRed.copy(alpha = 0.5f)
                                else -> TacticalCardBorder
                            },
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            if (isEmergency && !isSelected) {
                                pendingEmergencyChannel = channel
                            } else {
                                onSelectChannel(channel)
                            }
                        }
                        .testTag("channel_card_${channel.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) TacticalSurfaceElevated else TacticalSurface
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
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
                                            when {
                                                isEmergency -> PttHotRed.copy(alpha = 0.2f)
                                                isSelected -> TacticalCyan.copy(alpha = 0.2f)
                                                else -> TacticalDarkBg
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            when {
                                                isEmergency -> PttHotRed
                                                isSelected -> TacticalCyan
                                                else -> TacticalCardBorder
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isEmergency -> Icons.Default.Warning
                                            channel.id.contains("marine") -> Icons.Default.DirectionsBoat
                                            channel.id.contains("road") -> Icons.Default.DirectionsCar
                                            channel.id.contains("camp") -> Icons.Default.Park
                                            channel.id.contains("community") -> Icons.Default.Home
                                            else -> Icons.Default.Radio
                                        },
                                        contentDescription = channel.name,
                                        tint = when {
                                            isEmergency -> PttHotRed
                                            isSelected -> TacticalCyan
                                            else -> TacticalTextMuted
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = channel.name,
                                        color = when {
                                            isEmergency -> PttRedGlow
                                            isSelected -> TacticalCyan
                                            else -> TacticalTextPrimary
                                        },
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.5.sp
                                    )
                                    Text(
                                        text = channel.frequency,
                                        color = TacticalTextMuted,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isEmergency) PttHotRed.copy(alpha = 0.25f)
                                            else TacticalCyan.copy(alpha = 0.2f)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE LINE",
                                        color = if (isEmergency) PttRedGlow else TacticalCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else if (isEmergency) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PttHotRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "LEGAL REQ",
                                        color = PttHotRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = channel.description,
                            color = TacticalTextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Members",
                                    tint = PttNeonGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${channel.activeMembersCount} OPERATORS ONLINE",
                                    color = PttGreenGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TacticalDarkBg)
                                    .clickable { onOpenSafetyKey(channel) }
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
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

