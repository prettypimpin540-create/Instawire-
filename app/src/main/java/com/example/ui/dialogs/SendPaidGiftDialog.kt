package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PaidGiftItem
import com.example.data.model.WorldwideGiftCatalog
import com.example.data.model.WorldwideSpeaker

@Composable
fun SendPaidGiftDialog(
    userCoinsBalance: Int,
    targetSpeaker: WorldwideSpeaker?,
    availableSpeakers: List<WorldwideSpeaker>,
    onDismiss: () -> Unit,
    onSendGift: (PaidGiftItem, WorldwideSpeaker, String) -> Unit,
    onOpenBuyCoins: () -> Unit
) {
    var selectedSpeaker by remember(targetSpeaker) {
        mutableStateOf(targetSpeaker ?: availableSpeakers.firstOrNull())
    }
    var selectedGift by remember {
        mutableStateOf(WorldwideGiftCatalog.GIFTS.first())
    }
    var customMessage by remember { mutableStateOf("") }

    val accentGold = Color(0xFFF59E0B)
    val darkCard = Color(0xFF161B22)
    val darkSurface = Color(0xFF0D1117)
    val borderCol = Color(0xFF30363D)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            color = darkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .background(accentGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = accentGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SEND PAID GIFT",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Support global speakers & broadcast live effects",
                                color = Color(0xFF8B949E),
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF8B949E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User Coin Balance Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = darkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF21262D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = accentGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "YOUR BALANCE",
                                    color = Color(0xFF8B949E),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "$userCoinsBalance COINS",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Button(
                            onClick = onOpenBuyCoins,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentGold.copy(alpha = 0.2f),
                                contentColor = accentGold
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GET COINS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Recipient selector
                if (availableSpeakers.isNotEmpty()) {
                    Text(
                        text = "SELECT RECIPIENT ON STAGE",
                        color = Color(0xFF8B949E),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableSpeakers.forEach { speaker ->
                            val isSelected = selectedSpeaker?.id == speaker.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) accentGold.copy(alpha = 0.15f) else darkCard)
                                    .border(
                                        1.dp,
                                        if (isSelected) accentGold else Color(0xFF21262D),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedSpeaker = speaker }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${speaker.countryFlag} @${speaker.username}",
                                        color = if (isSelected) Color.White else Color(0xFFC9D1D9),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = if (speaker.isHost) "👑 Host" else "Speaker",
                                        color = if (isSelected) accentGold else Color(0xFF8B949E),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gifts Grid
                Text(
                    text = "SELECT GIFT TO SEND",
                    color = Color(0xFF8B949E),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(180.dp)
                ) {
                    items(WorldwideGiftCatalog.GIFTS) { gift ->
                        val isSelected = selectedGift.id == gift.id
                        val canAfford = userCoinsBalance >= gift.coinsCost
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) accentGold.copy(alpha = 0.2f) else darkCard
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) accentGold else Color(0xFF21262D)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedGift = gift }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = gift.emoji,
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = gift.name,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${gift.coinsCost} Coins",
                                    color = if (canAfford) accentGold else Color(0xFFEF4444),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Message TextField
                OutlinedTextField(
                    value = customMessage,
                    onValueChange = { customMessage = it },
                    placeholder = {
                        Text(
                            text = "Add an optional cheer message...",
                            color = Color(0xFF8B949E),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentGold,
                        unfocusedBorderColor = Color(0xFF30363D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = darkCard,
                        unfocusedContainerColor = darkCard
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action Send Button
                val hasRecipient = selectedSpeaker != null
                val canAffordSelected = userCoinsBalance >= selectedGift.coinsCost

                Button(
                    onClick = {
                        if (selectedSpeaker != null) {
                            onSendGift(
                                selectedGift,
                                selectedSpeaker!!,
                                if (customMessage.isBlank()) "Enjoy this ${selectedGift.name}!" else customMessage
                            )
                        }
                    },
                    enabled = hasRecipient,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAffordSelected) accentGold else Color(0xFF3B82F6),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = if (canAffordSelected) Icons.Default.Send else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (canAffordSelected) Color.Black else Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (canAffordSelected) {
                            "SEND ${selectedGift.emoji} FOR ${selectedGift.coinsCost} COINS"
                        } else {
                            "NEED ${selectedGift.coinsCost - userCoinsBalance} MORE COINS • RECHARGE"
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (canAffordSelected) Color.Black else Color.White
                    )
                }
            }
        }
    }
}
