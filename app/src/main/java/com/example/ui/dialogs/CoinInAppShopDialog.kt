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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CoinInAppCatalog
import com.example.data.model.CoinInAppItem
import com.example.data.model.CoinStoreCategory
import java.util.Locale

@Composable
fun CoinInAppShopDialog(
    currentCoinBalance: Int,
    onDismiss: () -> Unit,
    onBuyItem: (CoinInAppItem) -> Unit,
    onOpenBuyCoins: () -> Unit,
    onOpenCashout: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<CoinStoreCategory?>(null) }

    val accentGold = Color(0xFFF59E0B)
    val accentCyan = Color(0xFF00E5FF)
    val accentGreen = Color(0xFF10B981)
    val darkSurface = Color(0xFF0D1117)
    val darkCard = Color(0xFF161B22)
    val borderCol = Color(0xFF30363D)
    val textMuted = Color(0xFF8B949E)
    val textPrimary = Color(0xFFF0F6FC)

    val displayedItems = remember(selectedCategory) {
        if (selectedCategory == null) {
            CoinInAppCatalog.ITEMS
        } else {
            CoinInAppCatalog.ITEMS.filter { it.category == selectedCategory }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            color = darkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(accentGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "Coin Shop",
                                tint = accentGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VIRTUAL COIN STORE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentGold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "In-App Purchases with Coins",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("coin_shop_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Balance and Action Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = darkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentGold.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "YOUR COIN BALANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$currentCoinBalance",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentGold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = " Coins",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = accentGold
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onDismiss()
                                    onOpenBuyCoins()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentGold),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ Add Coins",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Button(
                                onClick = {
                                    onDismiss()
                                    onOpenCashout()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentGreen.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, accentGreen.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Cash Out 💵",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentGreen
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Categories
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isAllSelected = selectedCategory == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAllSelected) accentGold.copy(alpha = 0.25f) else darkCard)
                                .border(1.dp, if (isAllSelected) accentGold else borderCol, RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = null }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "All Items (${CoinInAppCatalog.ITEMS.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAllSelected) accentGold else textMuted
                            )
                        }
                    }

                    items(CoinStoreCategory.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accentGold.copy(alpha = 0.25f) else darkCard)
                                .border(1.dp, if (isSelected) accentGold else borderCol, RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) accentGold else textMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Items list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedItems) { item ->
                        val canAfford = currentCoinBalance >= item.coinCost
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (item.isFeatured) accentGold.copy(alpha = 0.5f) else borderCol,
                                    RoundedCornerShape(12.dp)
                                )
                                .testTag("coin_shop_item_${item.id}"),
                            colors = CardDefaults.cardColors(containerColor = darkCard)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.emoji,
                                            fontSize = 22.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = item.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                            Text(
                                                text = item.subtitle,
                                                fontSize = 11.sp,
                                                color = textMuted
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (item.isFeatured) accentGold.copy(alpha = 0.2f)
                                                else Color(0xFF21262D)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isFeatured) accentGold else textMuted,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    color = textMuted,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = borderCol.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${item.coinCost}",
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Black,
                                                color = accentGold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = " Coins",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = accentGold
                                            )
                                        }
                                        Text(
                                            text = item.usdValueDisplay,
                                            fontSize = 10.sp,
                                            color = textMuted
                                        )
                                    }

                                    Button(
                                        onClick = { onBuyItem(item) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (canAfford) accentGold else Color(0xFF21262D)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("buy_with_coins_btn_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LockOpen,
                                            contentDescription = null,
                                            tint = if (canAfford) Color.Black else textMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (canAfford) "Unlock (${item.coinCost} 🪙)" else "Need +${item.coinCost - currentCoinBalance} Coins",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (canAfford) Color.Black else textMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
