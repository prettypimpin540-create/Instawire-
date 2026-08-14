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
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.NumberType
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserIdentity
import com.example.security.FirebaseProvisioningResponse
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
fun BurnerStoreDialog(
    userIdentity: UserIdentity,
    isProvisioning: Boolean = false,
    lastProvisioningResponse: FirebaseProvisioningResponse? = null,
    onDismiss: () -> Unit,
    onGenerateNewBurner: () -> Unit,
    onToggleActiveNumber: () -> Unit,
    onOpenSubscriptionPlans: () -> Unit = {}
) {
    val isBurnerActive = userIdentity.activeNumberType == NumberType.BURNER
    val isFreeTier = userIdentity.subscriptionTier == SubscriptionTier.FREE

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, BurnerGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("burner_store_dialog"),
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
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Burner Store",
                                tint = BurnerGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TEMPORARY BURNER LINE",
                                color = BurnerGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Firebase Cloud Functions Provisioning",
                                color = TacticalCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_burner_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Subscription Status Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TacticalSurfaceElevated)
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(8.dp))
                        .clickable { onOpenSubscriptionPlans() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Tier",
                            tint = if (isFreeTier) TacticalAmber else BurnerGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PLAN: ${userIdentity.subscriptionTier.title.uppercase()} (${userIdentity.subscriptionTier.priceDisplay})",
                            color = TacticalTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "VIEW TIERS",
                        color = TacticalCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Transmitting Line Switcher Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TacticalSurfaceElevated)
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "ACTIVE TRANSMITTING CALLSIGN & LINE",
                            color = TacticalTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Phone Option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isBurnerActive) TacticalCyan.copy(alpha = 0.2f) else TacticalSurface)
                                    .border(
                                        1.dp,
                                        if (!isBurnerActive) TacticalCyan else TacticalCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { if (isBurnerActive) onToggleActiveNumber() }
                                    .padding(10.dp)
                                    .testTag("select_phone_number_option")
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PhoneIphone,
                                            contentDescription = "Real Phone",
                                            tint = if (!isBurnerActive) TacticalCyan else TacticalTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Phone Line",
                                            color = if (!isBurnerActive) TacticalCyan else TacticalTextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = userIdentity.phoneNumber,
                                        color = TacticalTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Burner Option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isBurnerActive) BurnerGold.copy(alpha = 0.2f) else TacticalSurface)
                                    .border(
                                        1.dp,
                                        if (isBurnerActive) BurnerGold else TacticalCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        if (!isBurnerActive && !isFreeTier) {
                                            onToggleActiveNumber()
                                        } else if (isFreeTier) {
                                            onOpenSubscriptionPlans()
                                        }
                                    }
                                    .padding(10.dp)
                                    .testTag("select_burner_number_option")
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Whatshot,
                                            contentDescription = "Burner",
                                            tint = if (isBurnerActive) BurnerGold else TacticalTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "VIP Burner",
                                            color = if (isBurnerActive) BurnerGold else TacticalTextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isFreeTier) "Locked (Tier 1+)" else userIdentity.burnerNumber,
                                        color = if (isFreeTier) TacticalAmber else TacticalTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Firebase Functions Cloud Provisioning Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1E2838),
                                    TacticalSurface
                                )
                            )
                        )
                        .border(1.dp, TacticalCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FIREBASE FUNCTIONS STATUS",
                                color = TacticalCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PttNeonGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LIVE REGION [us-central1]",
                                    color = PttGreenGlow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Function: provisionBurnerNumber() • Protocol: SIP/VoIP over AES-GCM",
                            color = TacticalTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        if (lastProvisioningResponse != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Last Request: ${lastProvisioningResponse.requestId} (${lastProvisioningResponse.executionTimeMs}ms)",
                                color = TacticalTextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Burner Line Card with Generate button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2E2305),
                                    TacticalSurface
                                )
                            )
                        )
                        .border(1.dp, BurnerGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TEMPORARY ANONYMOUS NUMBER",
                                color = BurnerGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BurnerGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isFreeTier) "TIER REQUIRED" else "FIREBASE READY",
                                    color = if (isFreeTier) TacticalAmber else BurnerGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isFreeTier) "Upgrade to generate anonymous burner lines" else userIdentity.burnerNumber,
                            color = TacticalTextPrimary,
                            fontSize = if (isFreeTier) 13.sp else 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = "Provisioned through Firebase Cloud Functions. Zero personal SIM or KYC data required.",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isFreeTier) {
                            Button(
                                onClick = onOpenSubscriptionPlans,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("upgrade_plan_for_burner_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurnerGold,
                                    contentColor = TacticalDarkBg
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Upgrade",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "UPGRADE TO PRO ($1.99/MO)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            Button(
                                onClick = onGenerateNewBurner,
                                enabled = !isProvisioning,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("generate_new_burner_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurnerGold,
                                    contentColor = TacticalDarkBg
                                )
                            ) {
                                if (isProvisioning) {
                                    CircularProgressIndicator(
                                        color = TacticalDarkBg,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "PROVISIONING VIA FIREBASE...",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Autorenew,
                                        contentDescription = "Generate Burner",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "GENERATE TEMPORARY BURNER NUMBER",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Plan Features Breakdown
                Text(
                    text = "ANONYMOUS TRANSMISSION FEATURES",
                    color = TacticalTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))

                listOf(
                    "Cloud-Provisioned VoIP SIP lines via Firebase Functions",
                    "Ephemeral auto-expiration timers with zero caller metadata",
                    "256-Bit Military End-to-End Cryptographic Voice Tunnel",
                    "Instant 1-tap rotation for maximum operational privacy",
                    "Seamless PTT interoperability with direct radio channels"
                ).forEach { perk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Included",
                            tint = PttNeonGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = perk,
                            color = TacticalTextPrimary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dismiss_burner_dialog"),
                    shape = RoundedCornerShape(10.dp),
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
