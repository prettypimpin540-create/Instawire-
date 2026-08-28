package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CashoutMethod
import com.example.data.model.CoinCashoutTransaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoinCashoutDialog(
    currentCoinBalance: Int,
    cashoutHistory: List<CoinCashoutTransaction>,
    onDismiss: () -> Unit,
    onRequestCashout: (coins: Int, method: CashoutMethod, destinationAccount: String, accountName: String) -> Unit,
    onOpenBuyCoins: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(CashoutMethod.PAYPAL) }
    var selectedCoinsPreset by remember { mutableStateOf(if (currentCoinBalance >= 500) 500 else if (currentCoinBalance >= 100) 100 else 0) }
    var customCoinsInput by remember { mutableStateOf("") }
    var destinationAccountInput by remember { mutableStateOf("") }
    var accountHolderNameInput by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) } // 0 = New Transfer, 1 = Payout History
    var showConfirmationNotice by remember { mutableStateOf(false) }

    val effectiveCoins = if (customCoinsInput.isNotBlank()) {
        customCoinsInput.toIntOrNull() ?: 0
    } else {
        selectedCoinsPreset
    }
    val effectiveUsd = effectiveCoins * 0.01

    val isAmountValid = effectiveCoins >= selectedMethod.minCoins && effectiveCoins <= currentCoinBalance
    val isFormValid = isAmountValid && destinationAccountInput.isNotBlank() && accountHolderNameInput.isNotBlank()

    val accentGreen = Color(0xFF10B981)
    val accentGold = Color(0xFFF59E0B)
    val darkSurface = Color(0xFF0D1117)
    val darkCard = Color(0xFF161B22)
    val borderCol = Color(0xFF30363D)
    val textMuted = Color(0xFF8B949E)
    val textPrimary = Color(0xFFF0F6FC)

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
                                .background(accentGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "Cashout",
                                tint = accentGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "REAL MONEY TRANSFER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentGreen,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Virtual Coins to Cash-Out",
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
                            .testTag("cashout_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Balance Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = darkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentGreen.copy(alpha = 0.4f)),
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
                                text = "AVAILABLE CASH-OUT BALANCE",
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "≈ \$${String.format(Locale.US, "%.2f", currentCoinBalance * 0.01)} USD",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentGreen
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onOpenBuyCoins()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentGold.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, accentGold.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+ Buy Coins",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher (New Transfer vs History)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF06090E))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (activeTab == 0) accentGreen.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = if (activeTab == 0) accentGreen else textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Transfer to Cash",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 0) accentGreen else textMuted
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (activeTab == 1) accentGreen.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = if (activeTab == 1) accentGreen else textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "History (${cashoutHistory.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) accentGreen else textMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (activeTab == 0) {
                    // NEW TRANSFER FORM
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Payment Method Picker
                        item {
                            Text(
                                text = "1. SELECT CASHOUT METHOD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                CashoutMethod.values().forEach { method ->
                                    val isSelected = selectedMethod == method
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                1.dp,
                                                if (isSelected) accentGreen else borderCol,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedMethod = method }
                                            .testTag("cashout_method_${method.name}"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) accentGreen.copy(alpha = 0.12f) else darkCard
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = when (method) {
                                                        CashoutMethod.PAYPAL -> Icons.Default.Payment
                                                        CashoutMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
                                                        CashoutMethod.CRYPTO_USDT -> Icons.Default.CurrencyBitcoin
                                                        CashoutMethod.CASH_APP -> Icons.Default.AttachMoney
                                                        CashoutMethod.STRIPE_DEBIT -> Icons.Default.CreditCard
                                                    },
                                                    contentDescription = null,
                                                    tint = if (isSelected) accentGreen else textMuted,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = method.title,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = textPrimary
                                                    )
                                                    Text(
                                                        text = "${method.speed} • ${method.feeDescription}",
                                                        fontSize = 11.sp,
                                                        color = textMuted
                                                    )
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isSelected) accentGreen.copy(alpha = 0.2f) else Color(0xFF21262D))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = method.badge,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) accentGreen else textMuted,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Coin Amount Selection
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "2. COIN AMOUNT TO TRANSFER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Presets
                            val presets = listOf(100, 250, 500, 1000, 2500)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(presets) { preset ->
                                    val isSelected = selectedCoinsPreset == preset && customCoinsInput.isBlank()
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) accentGreen.copy(alpha = 0.25f) else darkCard)
                                            .border(1.dp, if (isSelected) accentGreen else borderCol, RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedCoinsPreset = preset
                                                customCoinsInput = ""
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$preset Coins",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) accentGreen else textPrimary
                                            )
                                            Text(
                                                text = "\$${String.format(Locale.US, "%.2f", preset * 0.01)}",
                                                fontSize = 10.sp,
                                                color = textMuted
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Custom Input
                            OutlinedTextField(
                                value = customCoinsInput,
                                onValueChange = { customCoinsInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Or Enter Custom Coin Amount") },
                                placeholder = { Text("e.g. 750") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cashout_custom_amount_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentGreen,
                                    unfocusedBorderColor = borderCol,
                                    focusedTextColor = textPrimary,
                                    unfocusedTextColor = textPrimary,
                                    focusedContainerColor = darkCard,
                                    unfocusedContainerColor = darkCard
                                ),
                                trailingIcon = {
                                    if (effectiveCoins > 0) {
                                        Text(
                                            text = "= \$${String.format(Locale.US, "%.2f", effectiveUsd)} USD",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentGreen,
                                            modifier = Modifier.padding(end = 12.dp)
                                        )
                                    }
                                }
                            )
                        }

                        // 3. Destination Account Details
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "3. PAYOUT DESTINATION DETAILS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = accountHolderNameInput,
                                onValueChange = { accountHolderNameInput = it },
                                label = { Text("Full Legal Name / Account Holder") },
                                placeholder = { Text("e.g. Alex Vance") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cashout_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentGreen,
                                    unfocusedBorderColor = borderCol,
                                    focusedTextColor = textPrimary,
                                    unfocusedTextColor = textPrimary,
                                    focusedContainerColor = darkCard,
                                    unfocusedContainerColor = darkCard
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = destinationAccountInput,
                                onValueChange = { destinationAccountInput = it },
                                label = { Text("${selectedMethod.title} Account") },
                                placeholder = { Text(selectedMethod.placeholder) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cashout_destination_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentGreen,
                                    unfocusedBorderColor = borderCol,
                                    focusedTextColor = textPrimary,
                                    unfocusedTextColor = textPrimary,
                                    focusedContainerColor = darkCard,
                                    unfocusedContainerColor = darkCard
                                )
                            )
                        }

                        // Summary Box & Validation Notice
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF06090E)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Coins to Deduct:", fontSize = 12.sp, color = textMuted)
                                        Text("$effectiveCoins Coins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentGold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Payout Rate:", fontSize = 12.sp, color = textMuted)
                                        Text("100 Coins = \$1.00 USD", fontSize = 12.sp, color = textPrimary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Platform Fee:", fontSize = 12.sp, color = textMuted)
                                        Text("0% Free (Guaranteed)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentGreen)
                                    }
                                    Divider(color = borderCol, modifier = Modifier.padding(vertical = 6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Total Real Money Sent:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                        Text(
                                            "\$${String.format(Locale.US, "%.2f", effectiveUsd)} USD",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            color = accentGreen,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }

                        // Submit Button
                        item {
                            Spacer(modifier = Modifier.height(4.dp))

                            if (effectiveCoins > currentCoinBalance) {
                                Text(
                                    text = "⚠️ You do not have enough coins ($currentCoinBalance available). Buy more or lower amount.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            } else if (effectiveCoins < selectedMethod.minCoins) {
                                Text(
                                    text = "⚠️ Minimum transfer for ${selectedMethod.title} is ${selectedMethod.minCoins} Coins (\$${String.format(Locale.US, "%.2f", selectedMethod.minCoins * 0.01)} USD).",
                                    fontSize = 11.sp,
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    onRequestCashout(
                                        effectiveCoins,
                                        selectedMethod,
                                        destinationAccountInput,
                                        accountHolderNameInput
                                    )
                                },
                                enabled = isFormValid,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentGreen,
                                    disabledContainerColor = accentGreen.copy(alpha = 0.25f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_cashout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isFormValid) Color.Black else textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Transfer \$${String.format(Locale.US, "%.2f", effectiveUsd)} USD via ${selectedMethod.badge}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFormValid) Color.Black else textMuted
                                )
                            }
                        }
                    }
                } else {
                    // CASHOUT HISTORY LEDGER
                    if (cashoutHistory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = textMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No cash-out transfers yet",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Text(
                                    text = "Transfers to PayPal, Bank ACH, Crypto or Cash App will show here.",
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 380.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(cashoutHistory) { item ->
                                val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(item.timestamp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("cashout_history_item_${item.id}"),
                                    colors = CardDefaults.cardColors(containerColor = darkCard),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape)
                                                        .background(accentGreen.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = accentGreen,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "Transfer to ${item.method}",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = textPrimary
                                                    )
                                                    Text(
                                                        text = dateStr,
                                                        fontSize = 10.sp,
                                                        color = textMuted
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "+\$${String.format(Locale.US, "%.2f", item.usdAmount)} USD",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = accentGreen,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Text(
                                                    text = "-${item.coinsAmount} Coins",
                                                    fontSize = 10.sp,
                                                    color = accentGold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Divider(color = borderCol.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "To: ${item.destinationAccount} (${item.accountHolderName})",
                                                fontSize = 11.sp,
                                                color = textMuted
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(accentGreen.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = item.status,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = accentGreen,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Ref: ${item.referenceId}",
                                            fontSize = 9.sp,
                                            color = textMuted.copy(alpha = 0.7f),
                                            fontFamily = FontFamily.Monospace
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
