package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NumberType
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserIdentity
import com.example.ui.theme.BurnerGold
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
fun HudTopBar(
    userIdentity: UserIdentity,
    onOpenSafetyKey: () -> Unit,
    onOpenNoiseCancel: () -> Unit,
    onOpenBurnerStore: () -> Unit,
    onOpenSubscriptionPlans: () -> Unit = {},
    onOpenThemeSelector: () -> Unit = {},
    onOpenPhoneConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val glowColor = Color(userIdentity.themeScheme.glowHex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Main Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Radio antenna status dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "INSTAWIRE",
                    color = TacticalTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Subscription Tier Badge pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (userIdentity.subscriptionTier) {
                                SubscriptionTier.FREE -> TacticalSurfaceElevated
                                SubscriptionTier.PRO -> TacticalCyan.copy(alpha = 0.2f)
                                SubscriptionTier.BLACK_OPS -> BurnerGold.copy(alpha = 0.2f)
                                SubscriptionTier.GHOST_SENTINEL -> accentColor.copy(alpha = 0.2f)
                            }
                        )
                        .clickable { onOpenSubscriptionPlans() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("hud_tier_badge")
                ) {
                    Text(
                        text = userIdentity.subscriptionTier.badgeLabel,
                        color = when (userIdentity.subscriptionTier) {
                            SubscriptionTier.FREE -> TacticalTextMuted
                            SubscriptionTier.PRO -> TacticalCyan
                            SubscriptionTier.BLACK_OPS -> BurnerGold
                            SubscriptionTier.GHOST_SENTINEL -> accentColor
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Themes & Sounds Palette Quick Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape)
                        .clickable { onOpenThemeSelector() }
                        .testTag("hud_theme_palette_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Themes & Layouts Menu",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Visual Encryption Status Badge with Green Lock Icon
                EncryptionStatusBadge(
                    isEncrypted = userIdentity.isE2eeActive,
                    isKeyVerified = true,
                    onClick = onOpenSafetyKey,
                    modifier = Modifier.testTag("e2ee_status_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Secondary Info Strip: Active Number & Noise Cancellation Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Active Phone / Burner chip
            val isBurner = userIdentity.activeNumberType == NumberType.BURNER
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TacticalSurface)
                    .border(
                        1.dp,
                        if (isBurner) BurnerGold.copy(alpha = 0.6f) else TacticalCyan.copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        if (isBurner) onOpenBurnerStore() else onOpenPhoneConfirm()
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("active_number_chip"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isBurner) Icons.Default.Whatshot else Icons.Default.PhoneIphone,
                    contentDescription = "Active Number",
                    tint = if (isBurner) BurnerGold else TacticalCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (isBurner) "VIP BURNER LINE" else "PHONE NUMBER LINE",
                        color = if (isBurner) BurnerGold else TacticalCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = userIdentity.activeDisplayNumber,
                        color = TacticalTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Advanced Noise Filter chip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(TacticalSurface)
                    .border(
                        1.dp,
                        if (userIdentity.noiseFilterEnabled) PttGreenGlow.copy(alpha = 0.5f) else TacticalCardBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onOpenNoiseCancel() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("noise_filter_chip"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Noise Filter",
                    tint = if (userIdentity.noiseFilterEnabled) PttNeonGreen else TacticalTextMuted,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Column {
                    Text(
                        text = "NOISE FILTER",
                        color = if (userIdentity.noiseFilterEnabled) PttNeonGreen else TacticalTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (userIdentity.noiseFilterEnabled) "ACTIVE ON" else "BYPASS OFF",
                        color = TacticalTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
