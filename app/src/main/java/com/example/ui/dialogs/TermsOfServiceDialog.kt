package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.PttGreenDark
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
fun TermsOfServiceDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, TacticalCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("terms_of_service_dialog"),
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
                                .background(TacticalCyan.copy(alpha = 0.2f))
                                .border(1.dp, TacticalCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Legal Terms",
                                tint = TacticalCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TERMS OF SERVICE",
                                color = TacticalCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Developer Immunity & User Agreement",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_terms_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Privacy & Human Verification Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurfaceElevated)
                        .border(1.dp, PttNeonGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Privacy Shield",
                            tint = PttNeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ANTI-ABUSE PURPOSE & ZERO DATA COLLECTION",
                                color = PttNeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Phone confirmation is required solely to verify human identity and prevent unauthorized automated bot abuse, spamming, and sybil attacks. No personal information, names, contacts, or voice data are saved, stored, or collected on central servers.",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Developer Immunity & Protection from Misuse
                TermsSectionCard(
                    number = "1",
                    title = "DEVELOPER IMMUNITY & MISUSE PROTECTION",
                    accentColor = PttHotRed,
                    body = "The developer(s), authors, creators, contributors, and hosting providers of the InstaWire Walkie Talkie service are completely indemnified, shielded, and held harmless from any and all legal claims, liability, prosecution, penalties, lawsuits, losses, damages, or disputes arising from any misuse, abuse, illegal actions, or unlawful conduct committed by users on or through this application."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Section 2: Zero Responsibility Clause
                TermsSectionCard(
                    number = "2",
                    title = "ABSOLUTE NON-RESPONSIBILITY CLAUSE",
                    accentColor = TacticalAmber,
                    body = "The developer and service operators bear ABSOLUTELY NO RESPONSIBILITY OR LIABILITY for anything users choose to say, communicate, transmit, broadcast, coordinate, record, publish, or do with the InstaWire walkie talkie service, audio channels, frequencies, or burner lines. The app is an unmonitored communication instrument."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Section 3: User Sole Liability & Law Compliance
                TermsSectionCard(
                    number = "3",
                    title = "SOLE USER RESPONSIBILITY & LEGAL COMPLIANCE",
                    accentColor = TacticalCyan,
                    body = "Each user agrees and covenants that they are solely and exclusively responsible for their own conduct, transmissions, communications, and adherence to all local, state, federal, international, and telecommunications laws (including wiretapping, audio recording consent, and FCC regulations)."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Section 4: As-Is Provision & Disclaimers
                TermsSectionCard(
                    number = "4",
                    title = "SERVICE PROVIDED 'AS-IS' WITHOUT WARRANTY",
                    accentColor = TacticalTextMuted,
                    body = "InstaWire is provided on an 'AS-IS' and 'AS-AVAILABLE' basis with zero warranties of express, implied, or merchantability nature. Emergency communications should always rely on official public emergency dispatch (e.g. 911/112)."
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("acknowledge_terms_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TacticalCyan,
                        contentColor = TacticalDarkBg
                    )
                ) {
                    Text(
                        text = "I UNDERSTAND & ACKNOWLEDGE",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun TermsSectionCard(
    number: String,
    title: String,
    accentColor: androidx.compose.ui.graphics.Color,
    body: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TacticalSurface)
            .border(1.dp, TacticalCardBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f))
                        .border(1.dp, accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                color = TacticalTextPrimary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}
