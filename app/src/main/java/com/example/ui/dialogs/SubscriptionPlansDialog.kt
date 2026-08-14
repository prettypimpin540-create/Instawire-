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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserIdentity
import com.example.ui.theme.BurnerGold
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalCyanGlow
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

@Composable
fun SubscriptionPlansDialog(
    userIdentity: UserIdentity,
    onDismiss: () -> Unit,
    onSelectTier: (SubscriptionTier) -> Unit
) {
    var selectedTier by remember { mutableStateOf(userIdentity.subscriptionTier) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, BurnerGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("subscription_plans_dialog"),
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BurnerGold.copy(alpha = 0.2f))
                                .border(1.dp, BurnerGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Subscription Plans",
                                tint = BurnerGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "COMMUNICATION TIERS",
                                color = BurnerGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "3 Paid Tiers + Free Basic Walkie Service",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_subscription_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tier Cards List
                SubscriptionTier.entries.forEach { tier ->
                    val isCurrent = userIdentity.subscriptionTier == tier
                    val isSelected = selectedTier == tier

                    val tierColor = when (tier) {
                        SubscriptionTier.FREE -> TacticalTextMuted
                        SubscriptionTier.PRO -> TacticalCyan
                        SubscriptionTier.BLACK_OPS -> BurnerGold
                        SubscriptionTier.GHOST_SENTINEL -> PttNeonGreen
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) tierColor.copy(alpha = 0.15f) else TacticalSurface
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) tierColor else TacticalCardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedTier = tier }
                            .padding(14.dp)
                            .testTag("tier_card_${tier.name}")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(tierColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = tier.badgeLabel,
                                            color = tierColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = tier.title,
                                        color = TacticalTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    text = tier.priceDisplay,
                                    color = if (tier == SubscriptionTier.FREE) TacticalTextMuted else BurnerGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = tier.tagline,
                                color = TacticalTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Features breakdown
                            when (tier) {
                                SubscriptionTier.FREE -> {
                                    TierFeatureItem("Basic & Tactical walkie talkie channels", true, tierColor)
                                    TierFeatureItem("Military 256-Bit AES-GCM Audio Encryption (E2EE)", true, tierColor)
                                    TierFeatureItem("Direct 1-on-1 private encrypted contacts", true, tierColor)
                                    TierFeatureItem("30-second PTT transmission time", true, tierColor)
                                    TierFeatureItem("AI Studio Noise Cancellation DSP", false, tierColor)
                                    TierFeatureItem("Firebase Anonymous Burner Numbers", false, tierColor)
                                }
                                SubscriptionTier.PRO -> {
                                    TierFeatureItem("Everything in Basic Walkie included", true, tierColor)
                                    TierFeatureItem("AI Crystal Studio & Wind Noise Cancellation Filter", true, tierColor)
                                    TierFeatureItem("1 Active Firebase Provisioned Burner Line", true, tierColor)
                                    TierFeatureItem("60-second PTT transmission time", true, tierColor)
                                    TierFeatureItem("Audio Replay & Transmission History Vault", true, tierColor)
                                }
                                SubscriptionTier.BLACK_OPS -> {
                                    TierFeatureItem("Everything in Tactical Pro included", true, tierColor)
                                    TierFeatureItem("5 Active Firebase Cloud Burner Lines with Instant Rotation", true, tierColor)
                                    TierFeatureItem("Military VHF Bandpass DSP Filter & Voice Clarifier", true, tierColor)
                                    TierFeatureItem("Priority Low-Latency Mesh routing (<15ms latency)", true, tierColor)
                                    TierFeatureItem("180-second PTT transmission time", true, tierColor)
                                    TierFeatureItem("Zero-Knowledge Safety Key Exchange & Verification", true, tierColor)
                                }
                                SubscriptionTier.GHOST_SENTINEL -> {
                                    TierFeatureItem("Everything in Black Ops included", true, tierColor)
                                    TierFeatureItem("Post-Quantum Cryptographic Voice Tunnel", true, tierColor)
                                    TierFeatureItem("Unlimited Ephemeral Self-Destruct Burner lines with custom TTL", true, tierColor)
                                    TierFeatureItem("Extreme Suppression Acoustic Isolation engine", true, tierColor)
                                    TierFeatureItem("Satellite Mesh backup uplink & anonymous callsign masking", true, tierColor)
                                    TierFeatureItem("Unlimited Continuous PTT Transmission Time", true, tierColor)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (isCurrent) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = PttNeonGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "CURRENT ACTIVE PLAN",
                                        color = PttGreenGlow,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        onSelectTier(tier)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .testTag("activate_tier_${tier.name}"),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = tierColor,
                                        contentColor = TacticalDarkBg
                                    )
                                ) {
                                    Text(
                                        text = if (tier == SubscriptionTier.FREE) "DOWNGRADE TO FREE BASIC" else "ACTIVATE ${tier.title.uppercase()}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dismiss_subscription_dialog"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TacticalTextPrimary
                    )
                ) {
                    Text(
                        text = "DONE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun TierFeatureItem(text: String, included: Boolean, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (included) Icons.Default.Check else Icons.Default.Close,
            contentDescription = if (included) "Included" else "Not Included",
            tint = if (included) PttNeonGreen else TacticalTextMuted,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = if (included) TacticalTextPrimary else TacticalTextMuted,
            fontSize = 11.sp
        )
    }
}
