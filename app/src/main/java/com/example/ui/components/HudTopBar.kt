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
import androidx.compose.material.icons.filled.Menu
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
    onOpenMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val isBurner = userIdentity.activeNumberType == NumberType.BURNER

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Main Clean Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Logo & Identity Info
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "INSTAWIRE",
                        color = TacticalTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Subscription tier badge
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
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Active callsign & number subtitle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenPhoneConfirm() }
                ) {
                    Text(
                        text = userIdentity.callsign,
                        color = accentColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = " • ${userIdentity.activeDisplayNumber}",
                        color = TacticalTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    if (isBurner) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "[BURNER]",
                            color = BurnerGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Right: Security Lock & All-Features Menu Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Encryption status lock icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(TacticalSurfaceElevated)
                        .border(1.dp, TacticalCardBorder, CircleShape)
                        .clickable { onOpenSafetyKey() }
                        .testTag("e2ee_status_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "256-Bit E2EE Security",
                        tint = if (userIdentity.isE2eeActive) PttNeonGreen else TacticalAmber,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // PROMINENT ALL-FEATURES MENU BUTTON
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.18f))
                        .border(1.2.dp, accentColor, RoundedCornerShape(10.dp))
                        .clickable { onOpenMenu() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("hud_menu_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Features Menu",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MENU",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

