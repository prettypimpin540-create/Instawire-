package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedDarkBorder
import com.example.ui.theme.SophisticatedDarkPrimary
import com.example.ui.theme.SophisticatedDarkSecondary
import com.example.ui.theme.SophisticatedDarkSurface
import com.example.ui.theme.SophisticatedDarkSurfaceElevated
import com.example.ui.theme.SophisticatedDarkTertiary
import com.example.ui.theme.SophisticatedDarkTextMuted
import com.example.ui.theme.SophisticatedDarkTextPrimary
import com.example.ui.theme.SophisticatedDarkTextSecondary

@Composable
fun WelcomeScreen(
    onAcknowledgeAndEnter: () -> Unit = {},
    onNavigateToVerification: () -> Unit = onAcknowledgeAndEnter,
    onNavigateToDashboard: () -> Unit = onAcknowledgeAndEnter,
    modifier: Modifier = Modifier
) {
    var isAcknowledged by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedDarkBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .testTag("welcome_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Icon Graphic
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                SophisticatedDarkPrimary.copy(alpha = 0.25f),
                                SophisticatedDarkSurface
                            )
                        )
                    )
                    .border(2.dp, SophisticatedDarkPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = "InstaWire Walkie-Talkie",
                    tint = SophisticatedDarkPrimary,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "InstaWire Walkie",
                color = SophisticatedDarkTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Badges row: 100% Anonymous & Encrypted
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SophisticatedDarkPrimary.copy(alpha = 0.15f))
                        .border(1.dp, SophisticatedDarkPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "100% ANONYMOUS",
                        color = SophisticatedDarkPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SophisticatedDarkSecondary.copy(alpha = 0.15f))
                        .border(1.dp, SophisticatedDarkSecondary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "ZERO SIGN-UPS",
                        color = SophisticatedDarkSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SophisticatedDarkTertiary.copy(alpha = 0.15f))
                        .border(1.dp, SophisticatedDarkTertiary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "ENCRYPTED AUDIO",
                        color = SophisticatedDarkTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy & Features Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SophisticatedDarkSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SophisticatedDarkBorder, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FeatureRow(
                        icon = Icons.Default.NoAccounts,
                        iconTint = SophisticatedDarkPrimary,
                        title = "No Sign-Up • No Phone Verification",
                        subtitle = "Use the walkie-talkie immediately without accounts, passwords, or personal details."
                    )
                    FeatureRow(
                        icon = Icons.Default.Lock,
                        iconTint = SophisticatedDarkSecondary,
                        title = "All Communication Is Encrypted",
                        subtitle = "Every voice transmission, squad frequency, and worldwide room is cryptographically protected."
                    )
                    FeatureRow(
                        icon = Icons.Default.Security,
                        iconTint = SophisticatedDarkTertiary,
                        title = "Zero Logs & Absolute Privacy",
                        subtitle = "Live audio streams in real-time with zero audio storage and no user logging."
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // DISCLAIMER CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = SophisticatedDarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .testTag("welcome_disclaimer_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Disclaimer",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEVELOPER DISCLAIMER & ACKNOWLEDGMENT",
                            color = Color(0xFFFFD54F),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The developer is not responsible for any communications, broadcasts, or user actions conducted on this application. The developer has zero access to user audio or communications, retains no logs, and does not store or monitor any user information. All transmissions are anonymous and end-to-end encrypted.",
                        color = SophisticatedDarkTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Checkbox row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SophisticatedDarkSurface)
                            .clickable { isAcknowledged = !isAcknowledged }
                            .padding(8.dp)
                            .testTag("disclaimer_acknowledgment_checkbox_row"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isAcknowledged,
                            onCheckedChange = { isAcknowledged = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SophisticatedDarkPrimary,
                                uncheckedColor = SophisticatedDarkTextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I acknowledge that the developer is not responsible for anything and has no access to or logs of my information.",
                            color = if (isAcknowledged) SophisticatedDarkTextPrimary else SophisticatedDarkTextMuted,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp,
                            fontWeight = if (isAcknowledged) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CTA Button
            Button(
                onClick = {
                    onAcknowledgeAndEnter()
                },
                enabled = true,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SophisticatedDarkPrimary,
                    contentColor = SophisticatedDarkBackground,
                    disabledContainerColor = SophisticatedDarkSurfaceElevated,
                    disabledContentColor = SophisticatedDarkTextMuted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("welcome_get_started_btn")
            ) {
                Text(
                    text = "ENTER ANONYMOUS WALKIE-TALKIE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = SophisticatedDarkTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            )
            Text(
                text = subtitle,
                color = SophisticatedDarkTextSecondary,
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        }
    }
}
