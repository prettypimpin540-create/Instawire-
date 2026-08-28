package com.example.ui.dialogs

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Channel
import com.example.ui.theme.PttHotRed
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.PttRedGlow
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

@Composable
fun EmergencyDisclaimerDialog(
    channel: Channel,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    var hasCheckedAcknowledgment by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, PttHotRed, RoundedCornerShape(18.dp))
                .background(TacticalDarkBg)
                .testTag("emergency_disclaimer_dialog"),
            color = TacticalDarkBg
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Icon & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PttHotRed.copy(alpha = 0.2f))
                            .border(1.dp, PttHotRed, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency Warning",
                            tint = PttHotRed,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "LEGAL NOTICE",
                            color = PttHotRed,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "EMERGENCY FREQUENCY COMPLIANCE",
                            color = TacticalTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Channel Info Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurfaceElevated)
                        .border(1.dp, PttHotRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = channel.name,
                                color = PttRedGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = channel.frequency,
                                color = TacticalTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = channel.description,
                            color = TacticalTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Federal Law & Criminal Penalties Text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurface)
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Law",
                                tint = PttHotRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "47 U.S.C. § 325 & 47 CFR § 97.403",
                                color = TacticalTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "1. LIFE-SAFETY RESTRICTION: This frequency is strictly dedicated to immediate life-safety emergencies, search & rescue operations, and official disaster relief dispatch.",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Text(
                            text = "2. PROHIBITED CONDUCT: It is a severe federal offense to transmit false distress calls (MAYDAY / SOS / 911), prank broadcasts, commercial advertising, or casual chatter on emergency frequencies.",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Text(
                            text = "3. CRIMINAL PENALTIES: Violations are prosecuted under federal telecommunications law, punishable by fines of up to $10,000+, forfeiture of equipment, and criminal imprisonment.",
                            color = PttRedGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Acknowledgment Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { hasCheckedAcknowledgment = !hasCheckedAcknowledgment }
                        .background(if (hasCheckedAcknowledgment) PttHotRed.copy(alpha = 0.1f) else Color.Transparent)
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Checkbox(
                        checked = hasCheckedAcknowledgment,
                        onCheckedChange = { hasCheckedAcknowledgment = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PttHotRed,
                            uncheckedColor = TacticalTextMuted,
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.testTag("emergency_acknowledgment_checkbox")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I acknowledge that I am facing a genuine life-safety emergency or authorized safety operation, and I accept all legal penalties.",
                        color = if (hasCheckedAcknowledgment) TacticalTextPrimary else TacticalTextSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = if (hasCheckedAcknowledgment) FontWeight.Bold else FontWeight.Normal,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Button(
                    onClick = {
                        if (hasCheckedAcknowledgment) {
                            onAccept()
                        }
                    },
                    enabled = hasCheckedAcknowledgment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("accept_emergency_channel_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PttHotRed,
                        disabledContainerColor = TacticalSurfaceElevated,
                        contentColor = Color.White,
                        disabledContentColor = TacticalTextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Accept",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ENTER EMERGENCY FREQUENCY",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("cancel_emergency_channel_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCardBorder)
                ) {
                    Text(
                        text = "CANCEL & RETURN TO SAFE CHANNEL",
                        color = TacticalTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
