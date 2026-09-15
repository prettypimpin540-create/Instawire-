package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.PhoneAuthManager
import com.example.auth.PhoneAuthState
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedDarkBorder
import com.example.ui.theme.SophisticatedDarkError
import com.example.ui.theme.SophisticatedDarkPrimary
import com.example.ui.theme.SophisticatedDarkSecondary
import com.example.ui.theme.SophisticatedDarkSurface
import com.example.ui.theme.SophisticatedDarkSurfaceElevated
import com.example.ui.theme.SophisticatedDarkTextMuted
import com.example.ui.theme.SophisticatedDarkTextPrimary
import com.example.ui.theme.SophisticatedDarkTextSecondary

@Composable
fun VerificationScreen(
    phoneAuthManager: PhoneAuthManager,
    initialPhone: String = "",
    onVerificationSuccess: (phoneNumber: String) -> Unit,
    onBackToWelcome: () -> Unit,
    onSkipToDashboard: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val authState by phoneAuthManager.authState.collectAsState()

    var phoneNumberInput by remember { mutableStateOf(if (initialPhone.isNotBlank()) initialPhone else "+15550192834") }
    var smsCodeInput by remember { mutableStateOf("") }
    var isCodeSentState by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SophisticatedDarkBackground)
            .padding(24.dp)
            .testTag("verification_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top back action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToWelcome, modifier = Modifier.testTag("verification_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SophisticatedDarkTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Phone Verification",
                    color = SophisticatedDarkTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Icon Shield Graphic
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(SophisticatedDarkSecondary.copy(alpha = 0.15f))
                    .border(2.dp, SophisticatedDarkSecondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SophisticatedDarkSecondary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Authenticate Caller ID",
                color = SophisticatedDarkTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Firebase Phone Authentication verifies your voice node identity on the secure mesh network.",
                color = SophisticatedDarkTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Phone Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SophisticatedDarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SophisticatedDarkBorder, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = phoneNumberInput,
                        onValueChange = { phoneNumberInput = it },
                        label = { Text("Phone Number (E.164)") },
                        placeholder = { Text("+1 (555) 000-0000") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = SophisticatedDarkSecondary)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SophisticatedDarkSecondary,
                            unfocusedBorderColor = SophisticatedDarkBorder,
                            focusedTextColor = SophisticatedDarkTextPrimary,
                            unfocusedTextColor = SophisticatedDarkTextPrimary,
                            focusedLabelColor = SophisticatedDarkSecondary,
                            unfocusedLabelColor = SophisticatedDarkTextSecondary
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verification_phone_input")
                    )

                    Button(
                        onClick = {
                            if (activity != null && phoneNumberInput.isNotBlank()) {
                                isCodeSentState = true
                                phoneAuthManager.sendVerificationCode(activity, phoneNumberInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedDarkSecondary,
                            contentColor = SophisticatedDarkBackground
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("verification_send_code_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCodeSentState) "Resend SMS Code" else "Send SMS Verification Code",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // SMS Code Section
                    if (isCodeSentState || authState is PhoneAuthState.CodeSent) {
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = smsCodeInput,
                            onValueChange = { if (it.length <= 6) smsCodeInput = it },
                            label = { Text("6-Digit SMS Code") },
                            placeholder = { Text("123456") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SophisticatedDarkPrimary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SophisticatedDarkPrimary,
                                unfocusedBorderColor = SophisticatedDarkBorder,
                                focusedTextColor = SophisticatedDarkTextPrimary,
                                unfocusedTextColor = SophisticatedDarkTextPrimary,
                                focusedLabelColor = SophisticatedDarkPrimary,
                                unfocusedLabelColor = SophisticatedDarkTextSecondary
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verification_sms_code_input")
                        )

                        Button(
                            onClick = {
                                phoneAuthManager.verifySmsCode(smsCodeInput) { success, _ ->
                                    if (success) {
                                        onVerificationSuccess(phoneNumberInput)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedDarkPrimary,
                                contentColor = SophisticatedDarkBackground
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("verification_verify_code_btn")
                        ) {
                            Text("Verify Code & Enter", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time Status Card
            when (val state = authState) {
                is PhoneAuthState.SendingCode -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = SophisticatedDarkSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sending SMS code via Firebase...", color = SophisticatedDarkSecondary, fontSize = 12.sp)
                    }
                }
                is PhoneAuthState.CodeSent -> {
                    Text(
                        text = "SMS Code dispatched to ${state.phoneNumber}",
                        color = SophisticatedDarkPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                is PhoneAuthState.Verifying -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = SophisticatedDarkPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Verifying authentication credential...", color = SophisticatedDarkPrimary, fontSize = 12.sp)
                    }
                }
                is PhoneAuthState.Authenticated -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SophisticatedDarkPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Verified: ${state.phoneNumber}", color = SophisticatedDarkPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                is PhoneAuthState.Error -> {
                    Text(
                        text = state.message,
                        color = SophisticatedDarkError,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Demo Bypass / Skip Button
            OutlinedButton(
                onClick = {
                    onVerificationSuccess(phoneNumberInput)
                    onSkipToDashboard()
                },
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedDarkBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SophisticatedDarkTextSecondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("verification_skip_btn")
            ) {
                Text(text = "Skip & Continue to Dashboard", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}
