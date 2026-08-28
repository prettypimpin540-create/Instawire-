package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import com.example.data.model.NoiseFilterMode
import com.example.data.model.PttSoundProfile
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserIdentity
import com.example.data.model.UserProfile
import com.example.data.model.WalkieLayoutType
import com.example.ui.theme.BurnerGold
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttHotRed
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SettingsCategory(val title: String, val emoji: String) {
    ALL("All Settings", "⚡"),
    AUDIO_PTT("PTT & Audio", "🎙️"),
    COINS_CASHOUT("Coins & Cashout", "💰"),
    THEMES_CHASSIS("Themes & Layouts", "🎨"),
    PRIVACY_SECURITY("Privacy & Security", "🔒"),
    DIAGNOSTICS_LAB("Diagnostics & Lab", "🛠️")
}

@Composable
fun SettingsScreen(
    userIdentity: UserIdentity,
    userProfile: UserProfile = UserProfile(),
    cashoutHistory: List<CoinCashoutTransaction> = emptyList(),
    giftHistory: List<GiftTransaction> = emptyList(),
    onSetLayoutType: (WalkieLayoutType) -> Unit = {},
    onSetThemeScheme: (AppThemeScheme) -> Unit = {},
    onSetSoundProfile: (PttSoundProfile) -> Unit = {},
    onPreviewSound: (PttSoundProfile, Boolean) -> Unit = { _, _ -> },
    onOpenThemeLayoutSelector: () -> Unit = {},
    onToggleChirp: () -> Unit = {},
    onToggleRogerBeep: () -> Unit = {},
    onToggleHardwareVolumePtt: () -> Unit = {},
    onToggleBackgroundMonitoring: () -> Unit = {},
    onToggleBackgroundAudioBeep: () -> Unit = {},
    onToggleZeroLogs: () -> Unit = {},
    onSetEphemeralTimeout: (Int) -> Unit = {},
    onSetCallsign: (String) -> Unit = {},
    onSetVolumeLevel: (Float) -> Unit = {},
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
    onResetSettingsToDefaults: () -> Unit = {},
    onPanicWipeAllData: () -> Unit = {},
    isLoopbackRecording: Boolean = false,
    loopbackStatus: String? = null,
    isDiagnosticsRunning: Boolean = false,
    diagnosticsResult: String? = null,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val tier = userIdentity.subscriptionTier
    val accentGold = Color(0xFFF59E0B)
    val accentGreen = Color(0xFF10B981)

    var selectedCategory by remember { mutableStateOf(SettingsCategory.ALL) }
    var isEditingCallsign by remember { mutableStateOf(false) }
    var callsignInput by remember { mutableStateOf(userIdentity.callsign) }
    var selectedAiTroubleshootTopic by remember { mutableStateOf<String?>(null) }
    var showPanicConfirm by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Interactive Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SettingsCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) accentColor.copy(alpha = 0.22f) else TacticalSurface)
                            .border(1.dp, if (isSelected) accentColor else TacticalCardBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("settings_cat_${category.name}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = category.emoji,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accentColor else TacticalTextMuted
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION: OPERATOR IDENTITY & VIP SUBSCRIPTION TIER
        // =========================================================================
        if (selectedCategory == SettingsCategory.ALL || selectedCategory == SettingsCategory.COINS_CASHOUT) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .testTag("settings_subscription_card"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
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
                                        .background(accentColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (tier == SubscriptionTier.GHOST_SENTINEL) Icons.Default.Star else Icons.Default.Shield,
                                        contentDescription = "Subscription",
                                        tint = accentColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = tier.badgeLabel,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "${tier.title} (${tier.priceDisplay})",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalTextPrimary
                                    )
                                }
                            }

                            Button(
                                onClick = { onOpenSubscriptionPlans() },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("change_tier_button")
                            ) {
                                Text(
                                    text = if (tier == SubscriptionTier.GHOST_SENTINEL) "Manage" else "Upgrade",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalDarkBg
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = tier.tagline,
                            fontSize = 12.sp,
                            color = TacticalTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = TacticalCardBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Operator Callsign In-line Editor
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "OPERATOR CALLSIGN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = userIdentity.callsign,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = { isEditingCallsign = !isEditingCallsign },
                                colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isEditingCallsign) "Cancel" else "Change",
                                    fontSize = 11.sp,
                                    color = accentColor
                                )
                            }
                        }

                        AnimatedVisibility(visible = isEditingCallsign) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = callsignInput,
                                    onValueChange = { callsignInput = it.take(16).uppercase() },
                                    label = { Text("New Callsign") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("settings_callsign_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = accentColor,
                                        unfocusedBorderColor = TacticalCardBorder,
                                        focusedTextColor = TacticalTextPrimary,
                                        unfocusedTextColor = TacticalTextPrimary,
                                        focusedContainerColor = TacticalDarkBg,
                                        unfocusedContainerColor = TacticalDarkBg
                                    )
                                )

                                Button(
                                    onClick = {
                                        if (callsignInput.isNotBlank()) {
                                            onSetCallsign(callsignInput)
                                            isEditingCallsign = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Save", fontSize = 12.sp, color = TacticalDarkBg, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION: VIRTUAL COINS & REAL MONEY CASHOUT HUB
        // =========================================================================
        if (selectedCategory == SettingsCategory.ALL || selectedCategory == SettingsCategory.COINS_CASHOUT) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, accentGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .testTag("settings_coins_cashout_card"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
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
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "Coins",
                                        tint = accentGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "VIRTUAL COINS & REAL MONEY HUB",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentGold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Cash Out & In-App Purchases",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalTextPrimary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accentGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "100 🪙 = \$1.00",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Coin balance display
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = TacticalDarkBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCardBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "CURRENT COIN BALANCE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalTextMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${userProfile.coinsBalance}",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = accentGold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = " Coins",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = accentGold
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "REAL MONEY VALUE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalTextMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "≈ \$${String.format(Locale.US, "%.2f", userProfile.coinsBalance * 0.01)} USD",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentGreen,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Cash-Out, Spend Coins on Store, Buy Coins
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Real Money Cashout Button
                            Button(
                                onClick = { onOpenCashoutModal() },
                                colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("settings_cashout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Transfer Coins to Real Money (Cash Out 💵)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Spend Coins on In-App Store
                                Button(
                                    onClick = { onOpenCoinShopModal() },
                                    colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, accentGold.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("settings_coin_store_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = null,
                                        tint = accentGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "In-App Store 🛍️",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentGold
                                    )
                                }

                                // Buy Coins Pack
                                Button(
                                    onClick = { onOpenBuyCoinsModal() },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentGold),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("settings_buy_coins_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "+ Buy Coins 🪙",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        if (cashoutHistory.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val recent = cashoutHistory.first()
                            val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(recent.timestamp))
                            Text(
                                text = "Last transfer: \$${String.format(Locale.US, "%.2f", recent.usdAmount)} USD to ${recent.method} ($dateStr)",
                                fontSize = 11.sp,
                                color = TacticalTextMuted
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION: PTT AUDIO & CHIRP SOUND PROFILES
        // =========================================================================
        if (selectedCategory == SettingsCategory.ALL || selectedCategory == SettingsCategory.AUDIO_PTT) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                        .testTag("settings_sound_profiles_card"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Sound Profiles",
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PTT AUDIO & CHIRP SOUNDBOARD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = userIdentity.soundProfile.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sound Profiles Selector with Instant Preview
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PttSoundProfile.values().forEach { soundProf ->
                                val isSelected = userIdentity.soundProfile == soundProf
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            1.dp,
                                            if (isSelected) accentColor else TacticalCardBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onSetSoundProfile(soundProf) }
                                        .testTag("sound_profile_${soundProf.name}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) accentColor.copy(alpha = 0.12f) else TacticalDarkBg
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = soundProf.title,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) accentColor else TacticalTextPrimary
                                                )
                                                if (isSelected) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = accentColor,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = soundProf.description,
                                                fontSize = 11.sp,
                                                color = TacticalTextMuted
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = { onPreviewSound(soundProf, true) },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(TacticalSurfaceElevated)
                                                    .testTag("preview_press_${soundProf.name}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Press Chirp",
                                                    tint = accentColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = { onPreviewSound(soundProf, false) },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(TacticalSurfaceElevated)
                                                    .testTag("preview_release_${soundProf.name}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Radio,
                                                    contentDescription = "Release Beep",
                                                    tint = TacticalCyan,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = TacticalCardBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio Toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PTT Press Chirp Tone",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TacticalTextPrimary
                                )
                                Text(
                                    text = "Emits tactical tone when microphone transmitter locks",
                                    fontSize = 11.sp,
                                    color = TacticalTextMuted
                                )
                            }
                            Switch(
                                checked = userIdentity.chirpSoundEnabled,
                                onCheckedChange = { onToggleChirp() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = accentColor,
                                    checkedTrackColor = accentColor.copy(alpha = 0.5f),
                                    uncheckedThumbColor = TacticalTextMuted,
                                    uncheckedTrackColor = TacticalSurfaceElevated
                                ),
                                modifier = Modifier.testTag("chirp_toggle")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Roger Beep on Release",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TacticalTextPrimary
                                )
                                Text(
                                    text = "Military acoustic squelch tone confirming over-and-out",
                                    fontSize = 11.sp,
                                    color = TacticalTextMuted
                                )
                            }
                            Switch(
                                checked = userIdentity.rogerBeepEnabled,
                                onCheckedChange = { onToggleRogerBeep() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = accentColor,
                                    checkedTrackColor = accentColor.copy(alpha = 0.5f),
                                    uncheckedThumbColor = TacticalTextMuted,
                                    uncheckedTrackColor = TacticalSurfaceElevated
                                ),
                                modifier = Modifier.testTag("roger_beep_toggle")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hardware Volume Key PTT",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TacticalTextPrimary
                                )
                                Text(
                                    text = "Hold physical volume down key to transmit hands-free",
                                    fontSize = 11.sp,
                                    color = TacticalTextMuted
                                )
                            }
                            Switch(
                                checked = userIdentity.hardwareVolumePttEnabled,
                                onCheckedChange = { onToggleHardwareVolumePtt() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = accentColor,
                                    checkedTrackColor = accentColor.copy(alpha = 0.5f),
                                    uncheckedThumbColor = TacticalTextMuted,
                                    uncheckedTrackColor = TacticalSurfaceElevated
                                ),
                                modifier = Modifier.testTag("hw_volume_ptt_toggle")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Background Channel Monitor",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TacticalTextPrimary
                                )
                                Text(
                                    text = "Keeps radio receiver alive when screen is locked",
                                    fontSize = 11.sp,
                                    color = TacticalTextMuted
                                )
                            }
                            Switch(
                                checked = userIdentity.backgroundMonitoringEnabled,
                                onCheckedChange = { onToggleBackgroundMonitoring() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = accentColor,
                                    checkedTrackColor = accentColor.copy(alpha = 0.5f),
                                    uncheckedThumbColor = TacticalTextMuted,
                                    uncheckedTrackColor = TacticalSurfaceElevated
                                ),
                                modifier = Modifier.testTag("bg_monitor_toggle")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = TacticalCardBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio Sliders: Master Volume & Squelch Gate Threshold
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TRANSMITTER MASTER VOLUME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "${(userIdentity.volumeLevel * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Slider(
                                value = userIdentity.volumeLevel,
                                onValueChange = { onSetVolumeLevel(it) },
                                valueRange = 0.1f..1.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = accentColor,
                                    activeTrackColor = accentColor,
                                    inactiveTrackColor = TacticalSurfaceElevated
                                ),
                                modifier = Modifier.testTag("volume_slider")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ANALOG SQUELCH GATE THRESHOLD",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "-${( (1f - userIdentity.squelchLevel) * 60 ).toInt()} dB",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Slider(
                                value = userIdentity.squelchLevel,
                                onValueChange = { onSetSquelchLevel(it) },
                                valueRange = 0.05f..0.95f,
                                colors = SliderDefaults.colors(
                                    thumbColor = TacticalCyan,
                                    activeTrackColor = TacticalCyan,
                                    inactiveTrackColor = TacticalSurfaceElevated
                                ),
                                modifier = Modifier.testTag("squelch_slider")
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION: THEMES, CHASSIS & SCREEN LAYOUTS
        // =========================================================================
        if (selectedCategory == SettingsCategory.ALL || selectedCategory == SettingsCategory.THEMES_CHASSIS) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                        .testTag("settings_themes_card"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ColorLens,
                                    contentDescription = "Themes",
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "THEMES & CHASSIS LAYOUTS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = { onOpenThemeLayoutSelector() },
                                colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Full Selector 🎨", fontSize = 11.sp, color = accentColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Theme Schemes Grid
                        Text(
                            text = "COLOR SCHEMES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(AppThemeScheme.values()) { scheme ->
                                val isSelected = userIdentity.themeScheme == scheme
                                val schCol = Color(scheme.primaryHex)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) schCol.copy(alpha = 0.25f) else TacticalDarkBg)
                                        .border(1.dp, if (isSelected) schCol else TacticalCardBorder, RoundedCornerShape(10.dp))
                                        .clickable { onSetThemeScheme(scheme) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("theme_scheme_${scheme.name}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .background(schCol)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = scheme.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) schCol else TacticalTextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Layout Styles
                        Text(
                            text = "CHASSIS HARDWARE LAYOUTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            WalkieLayoutType.values().forEach { layout ->
                                val isSelected = userIdentity.layoutType == layout
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            1.dp,
                                            if (isSelected) accentColor else TacticalCardBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onSetLayoutType(layout) }
                                        .testTag("layout_type_${layout.name}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) accentColor.copy(alpha = 0.12f) else TacticalDarkBg
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = layout.title,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) accentColor else TacticalTextPrimary
                                                )
                                                if (isSelected) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = accentColor,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = layout.description,
                                                fontSize = 11.sp,
                                                color = TacticalTextMuted
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isSelected) accentColor.copy(alpha = 0.2f) else TacticalSurfaceElevated)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = layout.priceDisplay,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) accentColor else TacticalTextMuted,
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

        // =========================================================================
        // SECTION: PRIVACY, E2EE SAFETY KEYS & EPHEMERAL TIMEOUTS
        // =========================================================================
        if (selectedCategory == SettingsCategory.ALL || selectedCategory == SettingsCategory.PRIVACY_SECURITY) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                        .testTag("settings_privacy_card"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Privacy",
                                    tint = TacticalAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PRIVACY & ZERO-TRACE SECURITY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TacticalAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "KYBER-1024",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalAmber,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Zero Logs Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Zero-Logs Ephemeral Mode",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TacticalTextPrimary
                                )
                                Text(
                                    text = "Auto-purges audio waveforms and transmission packets",
                                    fontSize = 11.sp,
                                    color = TacticalTextMuted
                                )
                            }
                            Switch(
                                checked = userIdentity.zeroLogsEnabled,
                                onCheckedChange = { onToggleZeroLogs() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TacticalAmber,
                                    checkedTrackColor = TacticalAmber.copy(alpha = 0.5f),
                                    uncheckedThumbColor = TacticalTextMuted,
                                    uncheckedTrackColor = TacticalSurfaceElevated
                                ),
                                modifier = Modifier.testTag("zero_logs_toggle")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Ephemeral Timeout Selector
                        Text(
                            text = "AUTO-PURGE TIMEOUT DURATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val timeoutOptions = listOf(10, 30, 60, 300, 3600)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(timeoutOptions) { sec ->
                                val isSelected = userIdentity.ephemeralTimeoutSeconds == sec
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) TacticalAmber.copy(alpha = 0.25f) else TacticalDarkBg)
                                        .border(1.dp, if (isSelected) TacticalAmber else TacticalCardBorder, RoundedCornerShape(8.dp))
                                        .clickable { onSetEphemeralTimeout(sec) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (sec < 60) "${sec}s" else "${sec / 60}m",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TacticalAmber else TacticalTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = TacticalCardBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Terms & E2EE Safety Verification Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onOpenTermsOfService() },
                                colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Terms & Safety", fontSize = 11.sp, color = TacticalTextPrimary)
                            }

                            Button(
                                onClick = { onOpenNoiseCancelModal() },
                                colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("DSP Filter Studio", fontSize = 11.sp, color = TacticalCyan)
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION: 24/7 SPECIALIST & AI ASSISTANCE DISPATCH
        // =========================================================================
        if (selectedCategory == SettingsCategory.ALL || selectedCategory == SettingsCategory.DIAGNOSTICS_LAB) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                        .testTag("support_settings_card"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = "Support",
                                    tint = TacticalCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SUPPORT & SPECIALIST DISPATCH",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TacticalCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (tier == SubscriptionTier.FREE) "AI STUDIO ASSIST" else "24/7 LIVE PTT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (tier == SubscriptionTier.FREE)
                                "Connect to automated AI radio assistance for encryption keys, frequencies and squelch calibration. Upgrade to Tactical Pro to speak to live operators."
                            else
                                "Tune directly into the dedicated 24/7 TacOps Specialist Channel to speak directly with an authorized radio dispatcher.",
                            fontSize = 12.sp,
                            color = TacticalTextMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onConnectToSupport() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tier == SubscriptionTier.FREE) TacticalSurfaceElevated else TacticalCyan
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("connect_support_ptt_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HeadsetMic,
                                contentDescription = null,
                                tint = if (tier == SubscriptionTier.FREE) TacticalCyan else TacticalDarkBg,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (tier == SubscriptionTier.FREE) "Tune into AI Assistant PTT" else "Tune into Live Specialist Dispatch",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tier == SubscriptionTier.FREE) TacticalCyan else TacticalDarkBg
                            )
                        }

                        // Interactive Troubleshooting Accordions
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "INTERACTIVE RADIO GUIDES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val topics = listOf(
                            "Audio & Microphone Distortion Fix",
                            "256-Bit E2EE Safety Key Verification",
                            "Virtual Coins & Real Money Cash-Out Guide",
                            "Military VHF Frequency Bandpass Setup"
                        )

                        topics.forEach { topic ->
                            val isExpanded = selectedAiTroubleshootTopic == topic
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isExpanded) TacticalCyan else TacticalCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedAiTroubleshootTopic = if (isExpanded) null else topic }
                                    .testTag("troubleshoot_topic_${topic.hashCode()}"),
                                colors = CardDefaults.cardColors(containerColor = TacticalDarkBg)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = topic,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isExpanded) TacticalCyan else TacticalTextPrimary
                                        )
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.Check else Icons.Default.QuestionAnswer,
                                            contentDescription = null,
                                            tint = if (isExpanded) TacticalCyan else TacticalTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    AnimatedVisibility(visible = isExpanded) {
                                        Column(modifier = Modifier.padding(top = 8.dp)) {
                                            Text(
                                                text = when (topic) {
                                                    "Audio & Microphone Distortion Fix" -> "• Check Squelch threshold in Settings.\n• Ensure Noise Suppression mode is active (Crystal Studio or Tactical Radio).\n• Hold device 2-4 inches from mouth when pressing PTT."
                                                    "256-Bit E2EE Safety Key Verification" -> "• Compare the 12-block safety key with your recipient.\n• Tap the verified badge to lock the cryptographic session.\n• Keys use post-quantum Kyber + AES-256 GCM authenticated stream."
                                                    "Virtual Coins & Real Money Cash-Out Guide" -> "• Earn coins through worldwide room gifts, room hosting, or coin purchases.\n• 100 coins = \$1.00 USD cash-out rate with 0% platform fee.\n• Supports instant transfer to PayPal, Cash App, Direct Bank Wire ACH, and USDT crypto."
                                                    else -> "• Channels use 462 MHz UHF/VHF low-latency frequencies.\n• Custom frequencies can be added via the Channels tab.\n• Emergency SOS frequency 462.6750 MHz bypasses noise gate."
                                                },
                                                fontSize = 11.sp,
                                                color = TacticalTextSecondary,
                                                lineHeight = 16.sp
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

        // =========================================================================
        // SECTION: DIAGNOSTICS, AUDIO LOOPBACK & FACTORY RESET
        // =========================================================================
        if (selectedCategory == SettingsCategory.ALL || selectedCategory == SettingsCategory.DIAGNOSTICS_LAB) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                        .testTag("settings_diagnostics_card"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = "Diagnostics",
                                    tint = PttNeonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "HARDWARE DIAGNOSTICS & SYSTEM",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PttNeonGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "DSP LAB",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PttNeonGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio Loopback Mic & DSP Test
                        Button(
                            onClick = { onRunAudioLoopbackTest() },
                            enabled = !isLoopbackRecording,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLoopbackRecording) PttHotRed else TacticalSurfaceElevated
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("run_loopback_test_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (isLoopbackRecording) Color.White else PttNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isLoopbackRecording) "Recording Mic Loopback (3s)..." else "Run Mic & DSP Audio Loopback Test",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLoopbackRecording) Color.White else PttNeonGreen
                            )
                        }

                        if (loopbackStatus != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = loopbackStatus,
                                fontSize = 11.sp,
                                color = TacticalCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Low-latency network diagnostic
                        Button(
                            onClick = { onRunLatencyDiagnostic() },
                            enabled = !isDiagnosticsRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("run_latency_test_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = TacticalCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDiagnosticsRunning) "Testing Mesh Ping..." else "Ping Mesh & Edge Relays (Sub-50ms Test)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalCyan
                            )
                        }

                        if (diagnosticsResult != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = diagnosticsResult,
                                fontSize = 11.sp,
                                color = TacticalTextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = TacticalCardBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Reset & Emergency Wipe buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showResetConfirm = true },
                                colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCardBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = TacticalTextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Factory Reset", fontSize = 11.sp, color = TacticalTextPrimary)
                            }

                            Button(
                                onClick = { showPanicConfirm = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PttHotRed.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PttHotRed.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    tint = PttHotRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Panic Wipe", fontSize = 11.sp, color = PttHotRed, fontWeight = FontWeight.Bold)
                            }
                        }

                        AnimatedVisibility(visible = showResetConfirm) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .background(TacticalDarkBg, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Reset all audio, volume, squelch, sound profiles, and themes to default factory values?",
                                    fontSize = 11.sp,
                                    color = TacticalTextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            onResetSettingsToDefaults()
                                            showResetConfirm = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Confirm Reset", fontSize = 11.sp, color = TacticalDarkBg, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { showResetConfirm = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Cancel", fontSize = 11.sp, color = TacticalTextMuted)
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = showPanicConfirm) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .background(PttHotRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(1.dp, PttHotRed, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "⚠️ EMERGENCY PANIC WIPE: All transmission logs, cached messages, and safety keys will be irrevocably deleted.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PttHotRed
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            onPanicWipeAllData()
                                            showPanicConfirm = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PttHotRed),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("EXECUTE WIPE", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { showPanicConfirm = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Cancel", fontSize = 11.sp, color = TacticalTextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
