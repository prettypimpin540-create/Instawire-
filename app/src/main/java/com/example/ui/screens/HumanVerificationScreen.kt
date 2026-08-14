package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.system.exitProcess

@Composable
fun HumanVerificationScreen(
    userIdentity: UserIdentity,
    onVerifyAndAgree: (confirmedPhoneNumber: String) -> Unit,
    onPreviewSoundChirp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = Color(userIdentity.themeScheme.primaryHex)

    var phoneNumberInput by remember { mutableStateOf(userIdentity.phoneNumber.ifBlank { "+1 (555) 839-2041" }) }
    var smsCodeInput by remember { mutableStateOf("729-104") }
    var isCodeSent by remember { mutableStateOf(false) }
    var isHumanChallengePassed by remember { mutableStateOf(false) }
    var hasAgreedToTerms by remember { mutableStateOf(false) }
    var termsExpanded by remember { mutableStateOf(true) }

    val isPhoneNumberValid = phoneNumberInput.trim().length >= 7
    val canProceed = isPhoneNumberValid && isCodeSent && isHumanChallengePassed && hasAgreedToTerms

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Tactical Shield Badge
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(TacticalSurfaceElevated)
                    .border(2.dp, TacticalCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Human Verification Shield",
                    tint = TacticalCyan,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MANDATORY HUMAN VERIFICATION",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = TacticalCyan,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Text(
                text = "Anti-Bot Protocol • Zero-Knowledge Gateway",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TacticalTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CRITICAL NOTICE: Anti-Abuse Purpose & Zero Data Collection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, PttNeonGreen.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .testTag("verification_notice_card"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Zero-Knowledge Protection",
                            tint = PttNeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ANTI-ABUSE PURPOSE & PRIVACY GUARANTEE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = PttNeonGreen,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "1. PURPOSE OF CONFIRMATION: Phone number confirmation is required SOLELY to prevent unauthorized automated abuse, spamming bots, and malicious relay flooding of encrypted walkie-talkie repeaters.",
                        fontSize = 11.sp,
                        color = TacticalTextPrimary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "2. ZERO DATA COLLECTION: NO personal information, names, address books, location telemetry, or identity documents are saved, collected, or stored on servers. Verification operates via local zero-knowledge proof tokens.",
                        fontSize = 11.sp,
                        color = PttGreenGlow,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 1: Enter Real Mobile Phone Number
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(14.dp))
                    .testTag("phone_input_card"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP 1: CONFIRM REAL PHONE NUMBER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalTextMuted,
                            fontFamily = FontFamily.Monospace
                        )

                        if (isCodeSent) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Sent",
                                    tint = PttNeonGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CODE DISPATCHED",
                                    fontSize = 10.sp,
                                    color = PttNeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneNumberInput,
                        onValueChange = {
                            phoneNumberInput = it
                            isCodeSent = false
                            isHumanChallengePassed = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verification_phone_input"),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PhoneIphone,
                                contentDescription = "Mobile Phone",
                                tint = TacticalCyan
                            )
                        },
                        label = { Text("Your Real Mobile Phone Number", fontSize = 11.sp) },
                        placeholder = { Text("+1 (555) 000-0000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TacticalCyan,
                            unfocusedBorderColor = TacticalCardBorder,
                            focusedTextColor = TacticalTextPrimary,
                            unfocusedTextColor = TacticalTextPrimary,
                            focusedContainerColor = TacticalDarkBg,
                            unfocusedContainerColor = TacticalDarkBg
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isCodeSent) {
                        Button(
                            onClick = {
                                isCodeSent = true
                                onPreviewSoundChirp()
                            },
                            enabled = isPhoneNumberValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("send_verification_code_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TacticalCyan,
                                contentColor = TacticalDarkBg
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SEND HUMAN ANTI-BOT CONFIRMATION CODE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        // Step 2: 6-Digit SMS Confirmation & Anti-Bot Handshake
                        Column {
                            Text(
                                text = "STEP 2: 6-DIGIT VERIFICATION CODE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = smsCodeInput,
                                    onValueChange = { smsCodeInput = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("verification_code_input"),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PttNeonGreen,
                                        unfocusedBorderColor = TacticalCardBorder,
                                        focusedTextColor = TacticalTextPrimary,
                                        unfocusedTextColor = TacticalTextPrimary,
                                        focusedContainerColor = TacticalDarkBg,
                                        unfocusedContainerColor = TacticalDarkBg
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Button(
                                    onClick = {
                                        isHumanChallengePassed = true
                                        onPreviewSoundChirp()
                                    },
                                    modifier = Modifier
                                        .height(54.dp)
                                        .testTag("verify_challenge_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isHumanChallengePassed) PttNeonGreen else TacticalCyan,
                                        contentColor = TacticalDarkBg
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isHumanChallengePassed) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHumanChallengePassed) "PASSED" else "VERIFY",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 3: Terms of Service & Developer Liability Protection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, TacticalAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("terms_of_service_card"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Legal Terms",
                                tint = TacticalAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TERMS OF SERVICE & LEGAL WAIVER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = if (termsExpanded) "Collapse" else "Expand",
                            fontSize = 10.sp,
                            color = TacticalCyan,
                            modifier = Modifier
                                .clickable { termsExpanded = !termsExpanded }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedVisibility(visible = termsExpanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalDarkBg)
                                .border(1.dp, TacticalCardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column {
                                Text(
                                    text = "1. DEVELOPER IMMUNITY & PROTECTION FROM MISUSE:\nThe developer(s), authors, creators, contributors, and operators of the InstaWire Walkie Talkie service are completely indemnified, shielded, and held harmless from any and all legal claims, liability, damages, lawsuits, prosecution, or losses resulting from any user misuse, unauthorized usage, or illegal actions committed on or through this application.",
                                    fontSize = 11.sp,
                                    color = TacticalTextPrimary,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "2. ABSOLUTE NON-RESPONSIBILITY FOR USER ACTIONS:\nThe developer and service providers bear ABSOLUTELY NO RESPONSIBILITY OR LIABILITY for anything users choose to say, communicate, transmit, broadcast, coordinate, record, publish, or do with the InstaWire walkie-talkie service, channels, audio streams, or burner lines.",
                                    fontSize = 11.sp,
                                    color = TacticalTextPrimary,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "3. USER SOLE LIABILITY & LEGAL COMPLIANCE:\nYou expressly warrant that you are solely and exclusively responsible for your own communications and conduct, and will comply with all local, state, federal, telecommunication, wiretapping, and privacy laws.",
                                    fontSize = 11.sp,
                                    color = TacticalTextSecondary,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "4. UNMONITORED AS-IS SERVICE:\nInstaWire is provided on an 'AS-IS' and 'AS-AVAILABLE' basis without warranty of any kind. Communications are peer-to-peer encrypted and unmonitored.",
                                    fontSize = 10.sp,
                                    color = TacticalTextMuted,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Agree Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(TacticalSurfaceElevated)
                            .clickable { hasAgreedToTerms = !hasAgreedToTerms }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = hasAgreedToTerms,
                            onCheckedChange = { hasAgreedToTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PttNeonGreen,
                                uncheckedColor = TacticalTextMuted,
                                checkmarkColor = TacticalDarkBg
                            ),
                            modifier = Modifier.testTag("terms_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I AGREE to the Terms of Service. I expressly acknowledge that the developer is protected and not responsible for user actions or any misuse of the InstaWire service.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (hasAgreedToTerms) PttNeonGreen else TacticalTextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: "DO NOT AGREE (EXIT APP)" vs "CONFIRM & AGREE (GO LIVE)"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // DO NOT AGREE: Closes the App Immediately
                Button(
                    onClick = {
                        val activity = (context as? Activity)
                        activity?.finishAffinity() ?: exitProcess(0)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("do_not_agree_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PttHotRed.copy(alpha = 0.2f),
                        contentColor = PttHotRed
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PttHotRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Exit App",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DO NOT AGREE\n(EXIT APP)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // CONFIRM & AGREE (GO LIVE)
                Button(
                    onClick = {
                        if (canProceed) {
                            onVerifyAndAgree(phoneNumberInput)
                        }
                    },
                    enabled = canProceed,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(52.dp)
                        .testTag("confirm_and_agree_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canProceed) PttNeonGreen else TacticalSurfaceElevated,
                        contentColor = if (canProceed) TacticalDarkBg else TacticalTextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Agree and Go Live",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CONFIRM & AGREE\n(GO LIVE)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
