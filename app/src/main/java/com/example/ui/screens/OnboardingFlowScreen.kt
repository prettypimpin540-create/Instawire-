package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingFlowScreen(
    userIdentity: UserIdentity,
    onCompleteVerification: (confirmedPhoneNumber: String) -> Unit,
    onPreviewSoundChirp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = Color(userIdentity.themeScheme.primaryHex)

    // Current step in onboarding:
    // 0: Welcome Screen
    // 1: Anti-Abuse & Zero Data Guarantee Agreement
    // 2: Phone Confirmation & Human Anti-Bot Challenge
    // 3: Terms of Service & Developer Liability Protection (Agree / Disagree)
    var currentStep by remember { mutableIntStateOf(0) }

    // User inputs
    var phoneNumberInput by remember { mutableStateOf(userIdentity.phoneNumber.ifBlank { "+1 (555) 839-2041" }) }
    var smsCodeInput by remember { mutableStateOf("729-104") }
    var isCodeSent by remember { mutableStateOf(false) }
    var isHumanChallengePassed by remember { mutableStateOf(false) }
    var hasAgreedToTerms by remember { mutableStateOf(false) }
    var termsExpanded by remember { mutableStateOf(true) }

    val totalSteps = 4
    val stepTitles = listOf(
        "WELCOME",
        "ANTI-ABUSE POLICY",
        "HUMAN VERIFICATION",
        "TERMS & IMMUNITY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Bar with Step Indicators & Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    IconButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(TacticalSurface)
                            .testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TacticalCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(38.dp))
                }

                // Step Progress Indicators (1 to 4)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalSteps) {
                        val isCurrent = i == currentStep
                        val isDone = i < currentStep
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isCurrent) 28.dp else 14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isCurrent -> accentColor
                                        isDone -> PttNeonGreen
                                        else -> TacticalCardBorder
                                    }
                                )
                        )
                    }
                }

                // Step Label
                Text(
                    text = "STEP ${currentStep + 1}/$totalSteps",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TacticalTextMuted
                )
            }

            // Animated Screen Container
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally(animationSpec = tween(280)) { width -> width } + fadeIn() with
                                slideOutHorizontally(animationSpec = tween(280)) { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally(animationSpec = tween(280)) { width -> -width } + fadeIn() with
                                slideOutHorizontally(animationSpec = tween(280)) { width -> width } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    0 -> WelcomeScreenStep(
                        accentColor = accentColor,
                        onNext = {
                            onPreviewSoundChirp()
                            currentStep = 1
                        }
                    )
                    1 -> AntiAbusePolicyStep(
                        accentColor = accentColor,
                        onAgree = {
                            onPreviewSoundChirp()
                            currentStep = 2
                        },
                        onDisagree = {
                            val activity = (context as? Activity)
                            activity?.finishAffinity() ?: exitProcess(0)
                        }
                    )
                    2 -> PhoneAndHumanVerificationStep(
                        accentColor = accentColor,
                        phoneNumberInput = phoneNumberInput,
                        onPhoneNumberChange = {
                            phoneNumberInput = it
                            isCodeSent = false
                            isHumanChallengePassed = false
                        },
                        smsCodeInput = smsCodeInput,
                        onSmsCodeChange = { smsCodeInput = it },
                        isCodeSent = isCodeSent,
                        onSendCode = {
                            isCodeSent = true
                            onPreviewSoundChirp()
                        },
                        isHumanChallengePassed = isHumanChallengePassed,
                        onPassChallenge = {
                            isHumanChallengePassed = true
                            onPreviewSoundChirp()
                        },
                        onNext = {
                            onPreviewSoundChirp()
                            currentStep = 3
                        }
                    )
                    3 -> TermsAndImmunityStep(
                        accentColor = accentColor,
                        hasAgreedToTerms = hasAgreedToTerms,
                        onToggleAgree = { hasAgreedToTerms = it },
                        termsExpanded = termsExpanded,
                        onToggleExpanded = { termsExpanded = !termsExpanded },
                        onDoNotAgree = {
                            val activity = (context as? Activity)
                            activity?.finishAffinity() ?: exitProcess(0)
                        },
                        onConfirmAndEnter = {
                            if (hasAgreedToTerms) {
                                onCompleteVerification(phoneNumberInput)
                            }
                        }
                    )
                }
            }
        }
    }
}

/* =======================================================================
   SCREEN 1: WELCOME SCREEN STEP
   ======================================================================= */
@Composable
private fun WelcomeScreenStep(
    accentColor: Color,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Glowing PTT Tactical Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(TacticalSurfaceElevated)
                    .border(2.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = "InstaWire Walkie Talkie",
                    tint = accentColor,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "INSTAWIRE",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp
            )

            Text(
                text = "TACTICAL PUSH-TO-TALK NETWORK",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TacticalCyan,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Next-generation encrypted instant audio communications, military-grade roger beeps, and private burner numbers.",
                fontSize = 12.sp,
                color = TacticalTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Highlights
            WelcomeFeatureItem(
                icon = Icons.Default.FlashOn,
                iconColor = PttNeonGreen,
                title = "ZERO-LATENCY PTT AUDIO",
                description = "Instantaneous sub-50ms push-to-talk voice streaming with vintage squelch and tactical chirps."
            )

            Spacer(modifier = Modifier.height(10.dp))

            WelcomeFeatureItem(
                icon = Icons.Default.Groups,
                iconColor = TacticalCyan,
                title = "GLOBAL & TACTICAL CHANNELS",
                description = "Tune into public safety, emergency, convoys, or 256-bit encrypted private squads."
            )

            Spacer(modifier = Modifier.height(10.dp))

            WelcomeFeatureItem(
                icon = Icons.Default.Whatshot,
                iconColor = BurnerGold,
                title = "DISPOSABLE BURNER CALLER ID",
                description = "Generate disposable secondary numbers with local GPS area code detection for maximum privacy."
            )

            Spacer(modifier = Modifier.height(10.dp))

            WelcomeFeatureItem(
                icon = Icons.Default.Shield,
                iconColor = PttNeonGreen,
                title = "ZERO-KNOWLEDGE PRIVACY",
                description = "No passwords, no central database tracking, and automatic cryptographic zero-log purging."
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large Easy Start Button
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("welcome_get_started_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = TacticalDarkBg
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "GET STARTED & VERIFY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun WelcomeFeatureItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, TacticalCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = TacticalSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TacticalTextPrimary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/* =======================================================================
   SCREEN 2: ANTI-ABUSE & ZERO DATA GUARANTEE STEP
   ======================================================================= */
@Composable
private fun AntiAbusePolicyStep(
    accentColor: Color,
    onAgree: () -> Unit,
    onDisagree: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(TacticalSurfaceElevated)
                    .border(2.dp, PttNeonGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Privacy Shield",
                    tint = PttNeonGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "ANTI-ABUSE POLICY & PRIVACY",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = PttNeonGreen,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Text(
                text = "Strict Anti-Spam • Zero Personal Data Collected",
                fontSize = 12.sp,
                color = TacticalTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Information Cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, PttNeonGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = PttNeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. PURPOSE OF CONFIRMATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = PttNeonGreen,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Phone number confirmation is required ONLY to verify you are a genuine human user and to PREVENT UNAUTHORIZED USE, automated spamming bots, sybil attacks, and malicious radio frequency flooding.",
                        fontSize = 11.sp,
                        color = TacticalTextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, TacticalCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = TacticalCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. NO PERSONAL INFORMATION SAVED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = TacticalCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• NO names, addresses, or profile documents are gathered.\n• NO contact address books are uploaded or harvested.\n• NO audio recordings are retained on central servers.\n• All transmissions use decentralized ephemeral streaming.",
                        fontSize = 11.sp,
                        color = TacticalTextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Agree & Disagree Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onDisagree,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("policy_disagree_exit_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PttHotRed.copy(alpha = 0.2f),
                    contentColor = PttHotRed
                ),
                border = BorderStroke(1.dp, PttHotRed)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DISAGREE\n(EXIT APP)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onAgree,
                modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .testTag("policy_agree_continue_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PttNeonGreen,
                    contentColor = TacticalDarkBg
                )
            ) {
                Text(
                    text = "I AGREE & PROCEED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/* =======================================================================
   SCREEN 3: PHONE CONFIRMATION & HUMAN ANTI-BOT CHALLENGE STEP
   ======================================================================= */
@Composable
private fun PhoneAndHumanVerificationStep(
    accentColor: Color,
    phoneNumberInput: String,
    onPhoneNumberChange: (String) -> Unit,
    smsCodeInput: String,
    onSmsCodeChange: (String) -> Unit,
    isCodeSent: Boolean,
    onSendCode: () -> Unit,
    isHumanChallengePassed: Boolean,
    onPassChallenge: () -> Unit,
    onNext: () -> Unit
) {
    val isPhoneValid = phoneNumberInput.trim().length >= 7
    val canContinue = isPhoneValid && isCodeSent && isHumanChallengePassed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(TacticalSurfaceElevated)
                    .border(2.dp, TacticalCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneIphone,
                    contentDescription = "Phone Verification",
                    tint = TacticalCyan,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "PHONE CONFIRMATION",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = TacticalCyan,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Text(
                text = "Confirm Real Number to Prevent Unauthorized Use",
                fontSize = 12.sp,
                color = TacticalTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Real Phone Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. ENTER REAL MOBILE NUMBER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalTextMuted,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneNumberInput,
                        onValueChange = onPhoneNumberChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_phone_input"),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PhoneIphone,
                                contentDescription = "Phone",
                                tint = TacticalCyan
                            )
                        },
                        label = { Text("Your Mobile Number", fontSize = 11.sp) },
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Easy 1-Tap Preset Formats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PresetPhoneChip("+1 (555) 839-2041", "US Demo", onPhoneNumberChange)
                        PresetPhoneChip("+1 (415) 902-1188", "West Coast", onPhoneNumberChange)
                        PresetPhoneChip("+44 7911 123456", "UK Intl", onPhoneNumberChange)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isCodeSent) {
                        Button(
                            onClick = onSendCode,
                            enabled = isPhoneValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("onboarding_send_code_btn"),
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
                                text = "SEND ANTI-BOT CONFIRMATION CODE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        // Human Anti-Bot Token Verification
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "2. 6-DIGIT HUMAN CONFIRMATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PttNeonGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "SMS DISPATCHED",
                                    fontSize = 10.sp,
                                    color = PttNeonGreen,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = smsCodeInput,
                                    onValueChange = onSmsCodeChange,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("onboarding_code_input"),
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
                                    onClick = onPassChallenge,
                                    modifier = Modifier
                                        .height(54.dp)
                                        .testTag("onboarding_verify_human_btn"),
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
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Continue to Legal Terms Button
        Button(
            onClick = onNext,
            enabled = canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("onboarding_phone_next_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canContinue) PttNeonGreen else TacticalSurfaceElevated,
                contentColor = if (canContinue) TacticalDarkBg else TacticalTextMuted
            )
        ) {
            Text(
                text = "CONTINUE TO LEGAL TERMS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun PresetPhoneChip(
    number: String,
    label: String,
    onSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TacticalSurfaceElevated)
            .border(1.dp, TacticalCardBorder, RoundedCornerShape(6.dp))
            .clickable { onSelect(number) }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = TacticalCyan,
            fontFamily = FontFamily.Monospace
        )
    }
}

/* =======================================================================
   SCREEN 4: TERMS OF SERVICE & DEVELOPER IMMUNITY STEP
   ======================================================================= */
@Composable
private fun TermsAndImmunityStep(
    accentColor: Color,
    hasAgreedToTerms: Boolean,
    onToggleAgree: (Boolean) -> Unit,
    termsExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onDoNotAgree: () -> Unit,
    onConfirmAndEnter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(TacticalSurfaceElevated)
                    .border(2.dp, TacticalAmber, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "Legal Terms",
                    tint = TacticalAmber,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "TERMS OF SERVICE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = TacticalAmber,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Text(
                text = "Developer Immunity & Service Non-Responsibility",
                fontSize = 12.sp,
                color = TacticalTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Terms Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, TacticalAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LEGAL AGREEMENT & WAIVER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalAmber,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = if (termsExpanded) "Collapse" else "Expand",
                            fontSize = 10.sp,
                            color = TacticalCyan,
                            modifier = Modifier
                                .clickable { onToggleExpanded() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedVisibility(visible = termsExpanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(165.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalDarkBg)
                                .border(1.dp, TacticalCardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column {
                                Text(
                                    text = "1. DEVELOPER IMMUNITY & PROTECTION FROM MISUSE:\nThe developer(s), authors, creators, contributors, and operators of the InstaWire Walkie Talkie service are completely indemnified, shielded, and held harmless from any and all legal claims, liability, prosecution, penalties, lawsuits, losses, or damages arising from any misuse, illegal activities, or unlawful actions committed by users on or through this application.",
                                    fontSize = 11.sp,
                                    color = TacticalTextPrimary,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "2. ABSOLUTE NON-RESPONSIBILITY CLAUSE:\nThe developer and service providers bear ABSOLUTELY NO RESPONSIBILITY OR LIABILITY for anything users choose to say, communicate, transmit, broadcast, coordinate, record, publish, or do with the InstaWire walkie-talkie service, channels, audio streams, or burner lines. The app is an unmonitored communication tool.",
                                    fontSize = 11.sp,
                                    color = TacticalTextPrimary,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "3. USER SOLE LIABILITY & LEGAL COMPLIANCE:\nYou expressly covenant and agree that you are solely and exclusively responsible for your own communications and conduct, and will comply with all local, state, federal, telecommunication, and wiretapping laws.",
                                    fontSize = 11.sp,
                                    color = TacticalTextSecondary,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "4. SERVICE PROVIDED 'AS-IS':\nInstaWire is provided on an 'AS-IS' and 'AS-AVAILABLE' basis without warranty of any kind. Always contact 911/112 for official emergency services.",
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
                            .clickable { onToggleAgree(!hasAgreedToTerms) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = hasAgreedToTerms,
                            onCheckedChange = onToggleAgree,
                            colors = CheckboxDefaults.colors(
                                checkedColor = PttNeonGreen,
                                uncheckedColor = TacticalTextMuted,
                                checkmarkColor = TacticalDarkBg
                            ),
                            modifier = Modifier.testTag("onboarding_terms_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I AGREE to the Terms of Service. I agree that the developer is protected from any misuse or illegal actions and is not responsible for anything users choose to do with InstaWire.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (hasAgreedToTerms) PttNeonGreen else TacticalTextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: DO NOT AGREE (EXIT APP) vs AGREE & ENTER INSTAWIRE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onDoNotAgree,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("terms_do_not_agree_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PttHotRed.copy(alpha = 0.2f),
                    contentColor = PttHotRed
                ),
                border = BorderStroke(1.dp, PttHotRed)
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
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onConfirmAndEnter,
                enabled = hasAgreedToTerms,
                modifier = Modifier
                    .weight(1.3f)
                    .height(54.dp)
                    .testTag("terms_agree_enter_app_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasAgreedToTerms) PttNeonGreen else TacticalSurfaceElevated,
                    contentColor = if (hasAgreedToTerms) TacticalDarkBg else TacticalTextMuted
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Agree and Enter",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "I AGREE\n(ENTER INSTAWIRE)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
