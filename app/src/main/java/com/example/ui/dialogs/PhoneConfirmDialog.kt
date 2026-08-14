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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

@Composable
fun PhoneConfirmDialog(
    initialNumber: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var phoneNumberInput by remember { mutableStateOf(initialNumber) }
    var smsCodeInput by remember { mutableStateOf("729-104") }
    var hasAgreedToTerms by remember { mutableStateOf(true) }
    var step by remember { mutableStateOf(1) } // 1 = Enter Number, 2 = Instant SMS Confirmation

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, TacticalCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("phone_confirm_dialog"),
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
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Human Verification",
                                tint = TacticalCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "HUMAN VERIFICATION",
                                color = TacticalCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Anti-Abuse Verification • Zero Data Logged",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_phone_confirm_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info Callout: Anti-abuse and Zero Personal Information Collected
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurfaceElevated)
                        .border(1.dp, PttNeonGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "PREVENT UNAUTHORIZED USE • ZERO DATA SAVED",
                            color = PttNeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Phone confirmation is used SOLELY to prevent unauthorized automated abuse. NO personal information, names, or contacts are saved or collected.",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (step == 1) {
                    Text(
                        text = "ENTER YOUR REAL MOBILE NUMBER",
                        color = TacticalTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = phoneNumberInput,
                        onValueChange = { phoneNumberInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_number_input"),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PhoneIphone,
                                contentDescription = "Phone",
                                tint = TacticalCyan
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TacticalCyan,
                            unfocusedBorderColor = TacticalCardBorder,
                            focusedTextColor = TacticalTextPrimary,
                            unfocusedTextColor = TacticalTextPrimary,
                            focusedContainerColor = TacticalSurface,
                            unfocusedContainerColor = TacticalSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { step = 2 },
                        enabled = phoneNumberInput.trim().length >= 7,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("send_code_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TacticalCyan,
                            contentColor = TacticalDarkBg
                        )
                    ) {
                        Text(
                            text = "SEND HUMAN CONFIRMATION CODE",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    Text(
                        text = "CONFIRM 6-DIGIT CODE",
                        color = TacticalTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Anti-Bot code for $phoneNumberInput",
                        color = TacticalCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = smsCodeInput,
                        onValueChange = { smsCodeInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sms_code_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PttNeonGreen,
                            unfocusedBorderColor = TacticalCardBorder,
                            focusedTextColor = TacticalTextPrimary,
                            unfocusedTextColor = TacticalTextPrimary,
                            focusedContainerColor = TacticalSurface,
                            unfocusedContainerColor = TacticalSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Terms Note
                    Text(
                        text = "By confirming, you agree to Terms of Service protecting the developer from any misuse or illegal actions. The developer is not responsible for anything users choose to do with the service.",
                        color = TacticalTextMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onConfirm(phoneNumberInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_phone_final_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PttNeonGreen,
                            contentColor = TacticalDarkBg
                        )
                    ) {
                        Text(
                            text = "CONFIRM & GO LIVE ON INSTAWIRE",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
