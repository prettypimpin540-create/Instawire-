package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppMode
import com.example.data.model.NumberType
import com.example.data.model.UserIdentity
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
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

private data class FeatureMenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color,
    val badge: String? = null,
    val badgeColor: Color = tint,
    val testTag: String,
    val onClick: () -> Unit
)

private data class FeatureSection(
    val sectionTitle: String,
    val items: List<FeatureMenuItem>
)

@Composable
fun NavigationMenuDialog(
    userIdentity: UserIdentity,
    currentAppMode: AppMode = AppMode.TACTICAL,
    onDismiss: () -> Unit,
    onNavigateToTab: (Int) -> Unit,
    onSwitchAppMode: (AppMode) -> Unit = {},
    onOpenWorldwideProfile: () -> Unit = {},
    onOpenFriendsAndBlocked: () -> Unit = {},
    onOpenBuyCoins: () -> Unit = {},
    onOpenCashout: () -> Unit = {},
    onOpenCoinShop: () -> Unit = {},
    onOpenCreateWorldwideRoom: () -> Unit = {},
    onOpenThemes: () -> Unit,
    onOpenNoiseFilter: () -> Unit,
    onOpenSafetyKey: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onOpenPhoneConfirm: () -> Unit,
    onOpenTerms: () -> Unit
) {
    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val glowColor = Color(userIdentity.themeScheme.glowHex)

    val sections = listOf(
        FeatureSection(
            sectionTitle = "PUBLIC SAFETY & LIVE FEEDS",
            items = listOf(
                FeatureMenuItem(
                    title = "🚨 Emergency & Coast Guard Hub",
                    description = "911 Dispatch, US Coast Guard Rescue 21 & GPS Distress Beacon",
                    icon = Icons.Default.VerifiedUser,
                    tint = com.example.ui.theme.PttHotRed,
                    badge = "SOS RESCUE",
                    badgeColor = com.example.ui.theme.PttHotRed,
                    testTag = "menu_emergency_hub",
                    onClick = {
                        onSwitchAppMode(AppMode.TACTICAL)
                        onNavigateToTab(4)
                        onDismiss()
                    }
                ),
                FeatureMenuItem(
                    title = "📻 Live Public Safety Scanners",
                    description = "Listen to real-time Broadcastify Police, Fire & EMS audio feeds",
                    icon = Icons.Default.Radio,
                    tint = TacticalCyan,
                    badge = "LIVE AUDIO",
                    badgeColor = TacticalCyan,
                    testTag = "menu_live_scanners",
                    onClick = {
                        onSwitchAppMode(AppMode.TACTICAL)
                        onNavigateToTab(2)
                        onDismiss()
                    }
                ),
                FeatureMenuItem(
                    title = "⛈️ NOAA Weather Radio NWS",
                    description = "24/7 Live streaming audio from National Weather Service transmitters",
                    icon = Icons.Default.GraphicEq,
                    tint = Color(0xFF60A5FA),
                    badge = "24/7 NWS",
                    badgeColor = Color(0xFF60A5FA),
                    testTag = "menu_noaa_weather",
                    onClick = {
                        onSwitchAppMode(AppMode.TACTICAL)
                        onNavigateToTab(3)
                        onDismiss()
                    }
                )
            )
        ),
        FeatureSection(
            sectionTitle = "PRIMARY MODES & WORLDWIDE",
            items = listOf(
                FeatureMenuItem(
                    title = "🌍 Worldwide Walkie Talkie Hub",
                    description = "Explore global voice rooms across countries & cities",
                    icon = Icons.Default.Public,
                    tint = TacticalCyan,
                    badge = if (currentAppMode == AppMode.WORLDWIDE) "CURRENT MODE" else "GO GLOBAL",
                    badgeColor = TacticalCyan,
                    testTag = "menu_worldwide_hub",
                    onClick = {
                        onSwitchAppMode(AppMode.WORLDWIDE)
                        onDismiss()
                    }
                ),
                FeatureMenuItem(
                    title = "💵 Cash Out Coins to Real Money",
                    description = "Instant transfer to PayPal, Bank Wire ACH, Crypto USDT or Cash App",
                    icon = Icons.Default.MonetizationOn,
                    tint = Color(0xFF10B981),
                    badge = "REAL MONEY",
                    badgeColor = Color(0xFF10B981),
                    testTag = "menu_cashout_transfer",
                    onClick = {
                        onDismiss()
                        onOpenCashout()
                    }
                ),
                FeatureMenuItem(
                    title = "🛍️ In-App Coin Store",
                    description = "Spend virtual coins on VIP plans, Themes, and Soundboards",
                    icon = Icons.Default.CardGiftcard,
                    tint = BurnerGold,
                    badge = "SPEND COINS",
                    badgeColor = BurnerGold,
                    testTag = "menu_coin_inapp_shop",
                    onClick = {
                        onDismiss()
                        onOpenCoinShop()
                    }
                ),
                FeatureMenuItem(
                    title = "👤 Worldwide Personal Profile",
                    description = "Edit country flag, callsign, bio & reputation",
                    icon = Icons.Default.Person,
                    tint = accentColor,
                    badge = "PROFILE",
                    testTag = "menu_worldwide_profile",
                    onClick = {
                        onDismiss()
                        onOpenWorldwideProfile()
                    }
                ),
                FeatureMenuItem(
                    title = "👥 Global Friends & Block List",
                    description = "Manage worldwide contacts, blocked users & gifts",
                    icon = Icons.Default.People,
                    tint = PttNeonGreen,
                    badge = "ROSTER",
                    testTag = "menu_worldwide_friends",
                    onClick = {
                        onDismiss()
                        onOpenFriendsAndBlocked()
                    }
                ),
                FeatureMenuItem(
                    title = "🪙 Buy Virtual Coins",
                    description = "Recharge coin balance for gifts & in-app purchases",
                    icon = Icons.Default.MonetizationOn,
                    tint = BurnerGold,
                    badge = "RECHARGE",
                    badgeColor = BurnerGold,
                    testTag = "menu_worldwide_coins",
                    onClick = {
                        onDismiss()
                        onOpenBuyCoins()
                    }
                ),
                FeatureMenuItem(
                    title = "➕ Host a Worldwide Room",
                    description = "Create a custom live voice room for your city",
                    icon = Icons.Default.Public,
                    tint = TacticalCyan,
                    badge = "CREATE",
                    testTag = "menu_worldwide_create_room",
                    onClick = {
                        onDismiss()
                        onOpenCreateWorldwideRoom()
                    }
                )
            )
        ),
        FeatureSection(
            sectionTitle = "TACTICAL RADIO TRANSCEIVER",
            items = listOf(
                FeatureMenuItem(
                    title = "PTT Transceiver Radio",
                    description = "Main Push-To-Talk tactical radio interface",
                    icon = Icons.Default.Radio,
                    tint = accentColor,
                    badge = if (currentAppMode == AppMode.TACTICAL) "ACTIVE" else "SWITCH",
                    testTag = "menu_ptt_transceiver",
                    onClick = {
                        onSwitchAppMode(AppMode.TACTICAL)
                        onNavigateToTab(0)
                        onDismiss()
                    }
                ),
                FeatureMenuItem(
                    title = "Channels & Squad Bands",
                    description = "Public & private squad frequencies and rooms",
                    icon = Icons.Default.Groups,
                    tint = TacticalCyan,
                    badge = "CHANNELS",
                    testTag = "menu_channels",
                    onClick = {
                        onSwitchAppMode(AppMode.TACTICAL)
                        onNavigateToTab(1)
                        onDismiss()
                    }
                ),
                FeatureMenuItem(
                    title = "Direct 1-on-1 Contacts",
                    description = "Private encrypted walkie lines with trusted teammates",
                    icon = Icons.Default.People,
                    tint = accentColor,
                    badge = "CONTACTS",
                    testTag = "menu_contacts",
                    onClick = {
                        onSwitchAppMode(AppMode.TACTICAL)
                        onNavigateToTab(2)
                        onDismiss()
                    }
                ),
                FeatureMenuItem(
                    title = "Transmission History & Logs",
                    description = "Audio recording playback & zero-log wipe tools",
                    icon = Icons.Default.History,
                    tint = TacticalTextSecondary,
                    badge = "VOICE LOGS",
                    testTag = "menu_logs",
                    onClick = {
                        onSwitchAppMode(AppMode.TACTICAL)
                        onNavigateToTab(4)
                        onDismiss()
                    }
                )
            )
        ),

        FeatureSection(
            sectionTitle = "PRIVACY & AUDIO FILTERS",
            items = listOf(
                FeatureMenuItem(
                    title = "Burner Phone Lines",
                    description = "Anonymous disposable numbers & caller ID masking",
                    icon = Icons.Default.Whatshot,
                    tint = BurnerGold,
                    badge = if (userIdentity.hasBurnerSubscription) "VIP ACTIVE" else "UPGRADE",
                    badgeColor = BurnerGold,
                    testTag = "menu_burner_lines",
                    onClick = {
                        onNavigateToTab(3)
                        onDismiss()
                    }
                ),
                FeatureMenuItem(
                    title = "Audio DSP Noise Filter",
                    description = "Crystal Clear voice filter & squelch controls",
                    icon = Icons.Default.GraphicEq,
                    tint = if (userIdentity.noiseFilterEnabled) PttNeonGreen else TacticalTextMuted,
                    badge = if (userIdentity.noiseFilterEnabled) "FILTER ON (-28dB)" else "FILTER OFF",
                    badgeColor = if (userIdentity.noiseFilterEnabled) PttNeonGreen else TacticalTextMuted,
                    testTag = "menu_noise_filter",
                    onClick = {
                        onDismiss()
                        onOpenNoiseFilter()
                    }
                ),
                FeatureMenuItem(
                    title = "256-Bit E2EE Security Keys",
                    description = "Session encryption, safety key & fingerprint verification",
                    icon = Icons.Default.Security,
                    tint = PttNeonGreen,
                    badge = "AES-256 MESH",
                    badgeColor = PttNeonGreen,
                    testTag = "menu_e2ee_security",
                    onClick = {
                        onDismiss()
                        onOpenSafetyKey()
                    }
                ),
                FeatureMenuItem(
                    title = "Active Number & Caller ID",
                    description = "Currently active: ${userIdentity.activeDisplayNumber}",
                    icon = Icons.Default.PhoneIphone,
                    tint = if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold else TacticalCyan,
                    badge = if (userIdentity.activeNumberType == NumberType.BURNER) "BURNER" else "PHONE",
                    testTag = "menu_active_number",
                    onClick = {
                        onDismiss()
                        onOpenPhoneConfirm()
                    }
                )
            )
        ),
        FeatureSection(
            sectionTitle = "CUSTOMIZATION & SETTINGS",
            items = listOf(
                FeatureMenuItem(
                    title = "Themes, Layouts & Sounds Studio",
                    description = "10 Color themes, 5 tactical layouts & 7 PTT audio beeps",
                    icon = Icons.Default.ColorLens,
                    tint = BurnerGold,
                    badge = if (userIdentity.hasPurchasedThemePack) "ALL UNLOCKED" else "$5.94 PACK",
                    badgeColor = BurnerGold,
                    testTag = "menu_customization_studio",
                    onClick = {
                        onDismiss()
                        onOpenThemes()
                    }
                ),
                FeatureMenuItem(
                    title = "Subscription VIP Tiers",
                    description = "Current plan: ${userIdentity.subscriptionTier.title} (${userIdentity.subscriptionTier.priceDisplay})",
                    icon = Icons.Default.Star,
                    tint = BurnerGold,
                    badge = userIdentity.subscriptionTier.badgeLabel,
                    badgeColor = BurnerGold,
                    testTag = "menu_subscription_plans",
                    onClick = {
                        onDismiss()
                        onOpenSubscriptions()
                    }
                ),
                FeatureMenuItem(
                    title = "App & Hardware PTT Settings",
                    description = "Hardware volume key PTT, callsign, and background audio",
                    icon = Icons.Default.Settings,
                    tint = TacticalTextMuted,
                    badge = "CONFIG",
                    testTag = "menu_settings",
                    onClick = {
                        onNavigateToTab(5)
                        onDismiss()
                    }
                ),
                FeatureMenuItem(
                    title = "Terms of Service & Privacy Policy",
                    description = "Zero-log verification, military compliance & safety terms",
                    icon = Icons.Default.Description,
                    tint = TacticalTextMuted,
                    badge = "LEGAL & VERIFIED",
                    testTag = "menu_terms_of_service",
                    onClick = {
                        onDismiss()
                        onOpenTerms()
                    }
                )
            )
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                .testTag("navigation_menu_dialog"),
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.2f))
                                .border(1.5.dp, accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu Icon",
                                tint = accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "INSTAWIRE FEATURE HUB",
                                color = accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "All Features & Selectors",
                                color = TacticalTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_nav_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Menu",
                            tint = TacticalTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Render Categorized Sections
                sections.forEach { section ->
                    Text(
                        text = section.sectionTitle,
                        color = TacticalTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        section.items.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(14.dp))
                                    .clickable { item.onClick() }
                                    .testTag(item.testTag),
                                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                                .background(item.tint.copy(alpha = 0.15f))
                                                .border(1.dp, item.tint.copy(alpha = 0.4f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.title,
                                                tint = item.tint,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = item.title,
                                                color = TacticalTextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = item.description,
                                                color = TacticalTextMuted,
                                                fontSize = 11.sp,
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }

                                    if (item.badge != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(item.badgeColor.copy(alpha = 0.2f))
                                                .border(1.dp, item.badgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = item.badge,
                                                color = item.badgeColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
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
}

