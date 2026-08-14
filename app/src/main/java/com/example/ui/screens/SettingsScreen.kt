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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.PttSoundProfile
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserIdentity
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

@Composable
fun SettingsScreen(
    userIdentity: UserIdentity,
    onSetLayoutType: (com.example.data.model.WalkieLayoutType) -> Unit = {},
    onSetThemeScheme: (AppThemeScheme) -> Unit,
    onSetSoundProfile: (PttSoundProfile) -> Unit,
    onPreviewSound: (PttSoundProfile, Boolean) -> Unit,
    onOpenThemeLayoutSelector: () -> Unit = {},
    onToggleChirp: () -> Unit,
    onToggleRogerBeep: () -> Unit,
    onToggleHardwareVolumePtt: () -> Unit = {},
    onToggleBackgroundMonitoring: () -> Unit = {},
    onToggleBackgroundAudioBeep: () -> Unit = {},
    onToggleZeroLogs: () -> Unit,
    onSetEphemeralTimeout: (Int) -> Unit,
    onSetCallsign: (String) -> Unit,
    onConnectToSupport: () -> Unit,
    onOpenSubscriptionPlans: () -> Unit,
    onOpenNoiseCancelModal: () -> Unit,
    onOpenPhoneConfirmModal: () -> Unit,
    onOpenTermsOfService: () -> Unit,
    onWipeAllLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val glowColor = Color(userIdentity.themeScheme.glowHex)
    val tier = userIdentity.subscriptionTier

    var isEditingCallsign by remember { mutableStateOf(false) }
    var newCallsignInput by remember { mutableStateOf(userIdentity.callsign) }
    var selectedAiTroubleshootTopic by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Subscription & Support Tier Overview Banner
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tier == SubscriptionTier.GHOST_SENTINEL) Icons.Default.Star else Icons.Default.Shield,
                                    contentDescription = "Subscription",
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
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
                }
            }
        }

        // 2. Automated Assistance & Live Specialist Support via PTT
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
                                text = "SUPPORT & ASSISTANCE",
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
                                text = if (tier == SubscriptionTier.FREE) "AUTOMATED AI" else "LIVE PTT DISPATCH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Access level: ${tier.supportLevel}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (tier == SubscriptionTier.FREE)
                            "Free tier includes automated AI technical assistance for radio config, encryption keys, and frequencies. Upgrade to Tactical Pro ($1.99) or higher to talk directly to live specialist dispatchers via the PTT button."
                        else
                            "Press the button below to tune into the dedicated 24/7 TacOps Specialist Channel. You can hold down the PTT button to speak directly to a live technical specialist.",
                        fontSize = 12.sp,
                        color = TacticalTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1-Tap Connect to Live Specialist PTT Channel
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
                            text = if (tier == SubscriptionTier.FREE) "Open Automated AI Assistant (PTT)" else "Tune to Live Specialist Dispatch (PTT)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tier == SubscriptionTier.FREE) TacticalCyan else TacticalDarkBg
                        )
                    }

                    // Interactive Automated Troubleshooting Cards (Free & Paid)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "AUTOMATED DIAGNOSTICS & TROUBLESHOOTING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalTextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val topics = listOf(
                        "Audio & Microphone Distortion Fix",
                        "256-Bit E2EE Safety Key Verification",
                        "Area Code & Cloud Burner Routing",
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
                                .clickable {
                                    selectedAiTroubleshootTopic = if (isExpanded) null else topic
                                }
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
                                                "Area Code & Cloud Burner Routing" -> "• Go to Burner tab to pick custom area codes (e.g. 415, 212) or use GPS.\n• Tap 'Swap Active Caller ID' to broadcast the burner.\n• Destroy lines anytime with zero trace."
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

        // 3. Walkie-Talkie Screen Layouts (5 styles with $0.99 Paid Content)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                    .testTag("walkie_layouts_card"),
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
                                imageVector = Icons.Default.Dashboard,
                                contentDescription = "Layouts",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WALKIE-TALKIE UI LAYOUTS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Open dedicated Theme & Layout Studio modal button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .border(1.dp, accentColor, RoundedCornerShape(6.dp))
                                .clickable { onOpenThemeLayoutSelector() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("open_theme_studio_button")
                        ) {
                            Text(
                                text = "CUSTOMIZE STUDIO",
                                color = accentColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        modifier = Modifier.testTag("layouts_row")
                    ) {
                        items(com.example.data.model.WalkieLayoutType.values()) { layout ->
                            val isSelected = userIdentity.layoutType == layout
                            val isUnlocked = userIdentity.isLayoutUnlocked(layout)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) accentColor.copy(alpha = 0.2f) else TacticalDarkBg)
                                    .border(
                                        2.dp,
                                        if (isSelected) accentColor else if (!isUnlocked) BurnerGold.copy(alpha = 0.5f) else TacticalCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSetLayoutType(layout) }
                                    .padding(12.dp)
                                    .width(130.dp)
                                    .testTag("layout_type_${layout.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = when (layout) {
                                            com.example.data.model.WalkieLayoutType.CLASSIC_TACTICAL -> Icons.Default.Radio
                                            com.example.data.model.WalkieLayoutType.MIL_SPEC_COCKPIT -> Icons.Default.Flight
                                            com.example.data.model.WalkieLayoutType.CYBER_SYNTH_WAVE -> Icons.Default.Terminal
                                            com.example.data.model.WalkieLayoutType.MINIMALIST_STEALTH -> Icons.Default.Shield
                                            com.example.data.model.WalkieLayoutType.RETRO_VINTAGE_CB -> Icons.Default.SettingsRemote
                                        },
                                        contentDescription = layout.title,
                                        tint = if (isSelected) accentColor else if (!isUnlocked) BurnerGold else TacticalTextPrimary,
                                        modifier = Modifier.size(26.dp)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = layout.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) accentColor else TacticalTextPrimary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = if (isSelected) "ACTIVE" else if (isUnlocked) "UNLOCKED" else "${layout.priceDisplay} BUY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) accentColor else if (isUnlocked) TacticalTextMuted else BurnerGold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Interface & PTT Button Color Schemes
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                    .testTag("color_schemes_card"),
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
                                contentDescription = "Theme",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "INTERFACE COLOR PALETTES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "10 THEMES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        modifier = Modifier.testTag("color_schemes_row")
                    ) {
                        items(AppThemeScheme.values()) { scheme ->
                            val isSelected = userIdentity.themeScheme == scheme
                            val isUnlocked = userIdentity.isThemeUnlocked(scheme)
                            val swatchPrimary = Color(scheme.primaryHex)
                            val swatchGlow = Color(scheme.glowHex)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) swatchPrimary.copy(alpha = 0.25f) else TacticalDarkBg)
                                    .border(
                                        2.dp,
                                        if (isSelected) swatchPrimary else if (!isUnlocked) BurnerGold.copy(alpha = 0.5f) else TacticalCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSetThemeScheme(scheme) }
                                    .padding(12.dp)
                                    .testTag("theme_scheme_${scheme.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Mini PTT circular preview
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(swatchPrimary)
                                            .border(2.dp, swatchGlow, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = TacticalDarkBg,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else if (!isUnlocked) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked",
                                                tint = TacticalDarkBg,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = scheme.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) swatchPrimary else TacticalTextPrimary
                                    )
                                    Text(
                                        text = if (isUnlocked) scheme.subtitle else "${scheme.priceDisplay} Unlock",
                                        fontSize = 9.sp,
                                        color = if (isUnlocked) TacticalTextMuted else BurnerGold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Pre-Loaded PTT Sound Profiles
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                    .testTag("sound_profiles_card"),
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
                                contentDescription = "Sounds",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PTT SOUND PROFILES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "7 SOUNDS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PttSoundProfile.values().forEach { profile ->
                            val isSelected = userIdentity.soundProfile == profile
                            val isUnlocked = userIdentity.isSoundUnlocked(profile)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        1.dp,
                                        if (isSelected) accentColor else if (!isUnlocked) BurnerGold.copy(alpha = 0.5f) else TacticalCardBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onSetSoundProfile(profile) }
                                    .testTag("sound_profile_${profile.name}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) TacticalSurfaceElevated else TacticalDarkBg
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = accentColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                            } else if (!isUnlocked) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = BurnerGold,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                            }
                                            Text(
                                                text = profile.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) accentColor else TacticalTextPrimary
                                            )
                                        }
                                        Text(
                                            text = if (isUnlocked) profile.description else "${profile.priceDisplay} • ${profile.description}",
                                            fontSize = 11.sp,
                                            color = if (isUnlocked) TacticalTextMuted else BurnerGold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    // Preview Press and Release Sound Buttons
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { onPreviewSound(profile, true) },
                                            modifier = Modifier.size(32.dp).testTag("preview_press_${profile.name}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Mic,
                                                contentDescription = "Preview Press Tone",
                                                tint = accentColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { onPreviewSound(profile, false) },
                                            modifier = Modifier.size(32.dp).testTag("preview_release_${profile.name}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Preview Roger Beep",
                                                tint = TacticalCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Chirp & Roger Beep Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PTT Press Chirp Sound", fontSize = 13.sp, color = TacticalTextPrimary)
                            Text("Play synthesizer tone when pressing PTT", fontSize = 11.sp, color = TacticalTextMuted)
                        }
                        Switch(
                            checked = userIdentity.chirpSoundEnabled,
                            onCheckedChange = { onToggleChirp() },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor, checkedTrackColor = accentColor.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("toggle_chirp_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PTT Roger Beep On Release", fontSize = 13.sp, color = TacticalTextPrimary)
                            Text("Play tactical acknowledgement tone when releasing", fontSize = 11.sp, color = TacticalTextMuted)
                        }
                        Switch(
                            checked = userIdentity.rogerBeepEnabled,
                            onCheckedChange = { onToggleRogerBeep() },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor, checkedTrackColor = accentColor.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("toggle_roger_beep_switch")
                        )
                    }
                }
            }
        }

        // HARDWARE PTT & BACKGROUND RADIO MONITORING
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("hardware_ptt_background_card"),
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
                                imageVector = Icons.Default.SettingsRemote,
                                contentDescription = "Hardware PTT",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HARDWARE PTT & BACKGROUND RADIO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Status Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (userIdentity.backgroundMonitoringEnabled) PttGreenDark.copy(alpha = 0.6f) else TacticalDarkBg)
                                .border(
                                    1.dp,
                                    if (userIdentity.backgroundMonitoringEnabled) PttNeonGreen else TacticalCardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (userIdentity.backgroundMonitoringEnabled) "RX MONITOR LIVE" else "STANDBY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userIdentity.backgroundMonitoringEnabled) PttNeonGreen else TacticalTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 1: Hardware Volume Up PTT Key
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hardware Volume-Up PTT Key",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hold the physical Volume Up button on your phone to transmit instantly like a rugged field radio. Release to send.",
                                fontSize = 11.sp,
                                color = TacticalTextMuted,
                                lineHeight = 15.sp
                            )
                        }
                        Switch(
                            checked = userIdentity.hardwareVolumePttEnabled,
                            onCheckedChange = { onToggleHardwareVolumePtt() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = accentColor,
                                checkedTrackColor = accentColor.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("toggle_hardware_volume_ptt_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 2: Background Radio Monitoring
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = "Background Radio Transceiver",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Keep radio listening in background. Hear incoming voice chatter and team transmissions even with screen locked or using other apps.",
                                fontSize = 11.sp,
                                color = TacticalTextMuted,
                                lineHeight = 15.sp
                            )
                        }
                        Switch(
                            checked = userIdentity.backgroundMonitoringEnabled,
                            onCheckedChange = { onToggleBackgroundMonitoring() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = accentColor,
                                checkedTrackColor = accentColor.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("toggle_background_monitoring_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 3: Background Audio Chime & Squelch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = "Background Audio Squelch Chime",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Play loud squelch pulse and alert tones when receiving voice transmissions while minimized.",
                                fontSize = 11.sp,
                                color = TacticalTextMuted,
                                lineHeight = 15.sp
                            )
                        }
                        Switch(
                            checked = userIdentity.backgroundAudioBeepEnabled,
                            onCheckedChange = { onToggleBackgroundAudioBeep() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = accentColor,
                                checkedTrackColor = accentColor.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("toggle_background_audio_beep_switch")
                        )
                    }
                }
            }
        }

        // 5. Callsign & Radio Identity Configuration
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                    .testTag("callsign_settings_card"),
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
                                imageVector = Icons.Default.Person,
                                contentDescription = "Callsign",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TACTICAL CALLSIGN & ID",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = {
                                if (isEditingCallsign) {
                                    if (newCallsignInput.isNotBlank()) {
                                        onSetCallsign(newCallsignInput)
                                    }
                                    isEditingCallsign = false
                                } else {
                                    isEditingCallsign = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("edit_callsign_btn")
                        ) {
                            Text(
                                text = if (isEditingCallsign) "Save" else "Edit",
                                fontSize = 11.sp,
                                color = accentColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isEditingCallsign) {
                        OutlinedTextField(
                            value = newCallsignInput,
                            onValueChange = { newCallsignInput = it },
                            label = { Text("Callsign", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = TacticalCardBorder,
                                focusedTextColor = TacticalTextPrimary,
                                unfocusedTextColor = TacticalTextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("callsign_input_field")
                        )
                    } else {
                        Text(
                            text = userIdentity.callsign,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Phone Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Primary SIM Phone Number", fontSize = 13.sp, color = TacticalTextPrimary)
                            Text(userIdentity.phoneNumber, fontSize = 12.sp, color = TacticalTextMuted, fontFamily = FontFamily.Monospace)
                        }
                        Button(
                            onClick = { onOpenPhoneConfirmModal() },
                            colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("change_phone_number_btn")
                        ) {
                            Text("Change", fontSize = 11.sp, color = TacticalCyan)
                        }
                    }
                }
            }
        }

        // 6. Security & Ephemeral Logs Purge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(16.dp))
                    .testTag("security_settings_card"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURITY & EPHEMERAL TIMEOUT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Zero-Knowledge Ephemeral Purge", fontSize = 13.sp, color = TacticalTextPrimary)
                            Text("Automatically wipe transmission audio & logs after timeout", fontSize = 11.sp, color = TacticalTextMuted)
                        }
                        Switch(
                            checked = userIdentity.zeroLogsEnabled,
                            onCheckedChange = { onToggleZeroLogs() },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor, checkedTrackColor = accentColor.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("toggle_zero_logs_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Auto-Purge Timeout: ${userIdentity.ephemeralTimeoutSeconds} Seconds", fontSize = 12.sp, color = TacticalTextPrimary)
                    Slider(
                        value = userIdentity.ephemeralTimeoutSeconds.toFloat(),
                        onValueChange = { onSetEphemeralTimeout(it.toInt()) },
                        valueRange = 15f..300f,
                        steps = 5,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor),
                        modifier = Modifier.testTag("ephemeral_timeout_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onWipeAllLogs() },
                        colors = ButtonDefaults.buttonColors(containerColor = PttHotRed.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wipe_all_logs_btn")
                    ) {
                        Text(
                            text = "Emergency Wipe All Transmissions & Logs",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PttHotRed
                        )
                    }
                }
            }
        }

        // 7. Human Verification & Legal Terms Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, PttNeonGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .testTag("legal_terms_settings_card"),
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
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Verification & Terms",
                                tint = PttNeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HUMAN VERIFICATION & LEGAL WAIVER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PttNeonGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PttGreenDark)
                                .border(1.dp, PttNeonGreen, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (userIdentity.isHumanVerified) "VERIFIED" else "PENDING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = PttNeonGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Purpose: Confirmation prevents unauthorized bot abuse on tactical frequencies.\n• Privacy: NO personal information is collected or saved.\n• Legal: Developer is fully protected and not responsible for any user actions or misuse.",
                        fontSize = 11.sp,
                        color = TacticalTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenTermsOfService() },
                            colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_terms_btn")
                        ) {
                            Text("Terms & Immunity", fontSize = 11.sp, color = TacticalCyan)
                        }

                        Button(
                            onClick = { onOpenPhoneConfirmModal() },
                            colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reverify_phone_btn")
                        ) {
                            Text("Verify Number", fontSize = 11.sp, color = PttNeonGreen)
                        }
                    }
                }
            }
        }
    }
}
