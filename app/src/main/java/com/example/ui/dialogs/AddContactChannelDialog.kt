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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.ui.platform.LocalClipboardManager
import kotlin.random.Random
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    checkCallsignConflict: (String) -> com.example.data.model.CallsignConflict = { com.example.data.model.CallsignConflict(false) },
    onAddContact: (name: String, number: String, callsign: String, isBurner: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var callsign by remember { mutableStateOf("") }
    var isBurner by remember { mutableStateOf(false) }

    val callsignConflict = remember(callsign) { checkCallsignConflict(callsign) }
    val isConflict = callsign.isNotBlank() && callsignConflict.isTaken

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, PttNeonGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("add_contact_dialog"),
            colors = CardDefaults.cardColors(containerColor = TacticalDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .background(PttGreenDark)
                                .border(1.dp, PttNeonGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Add Contact",
                                tint = PttNeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ADD WIRE CONTACT",
                            color = PttNeonGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_contact_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name", color = TacticalTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PttNeonGreen,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalTextPrimary,
                        unfocusedTextColor = TacticalTextPrimary,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Phone Number or Burner ID", color = TacticalTextSecondary) },
                    placeholder = { Text("+1 (555) 000-0000", color = TacticalTextSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_number_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PttNeonGreen,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalTextPrimary,
                        unfocusedTextColor = TacticalTextPrimary,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = callsign,
                    onValueChange = { callsign = it },
                    label = { Text("Tactical Callsign (e.g. ALPHA-9)", color = TacticalTextSecondary) },
                    isError = isConflict,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_callsign_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isConflict) com.example.ui.theme.PttHotRed else PttNeonGreen,
                        unfocusedBorderColor = if (isConflict) com.example.ui.theme.PttHotRed.copy(alpha = 0.6f) else TacticalCardBorder,
                        focusedTextColor = TacticalTextPrimary,
                        unfocusedTextColor = TacticalTextPrimary,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                if (isConflict) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ Callsign already claimed by ${callsignConflict.takenBy}! Must be unique.",
                        color = com.example.ui.theme.PttHotRed,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else if (callsign.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ Unique callsign available",
                        color = PttNeonGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TacticalSurfaceElevated)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "End-to-End Encrypted (AES-256)",
                        color = PttNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && number.isNotBlank() && !isConflict) {
                            onAddContact(name, number, callsign, isBurner)
                        }
                    },
                    enabled = name.isNotBlank() && number.isNotBlank() && !isConflict,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_contact_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PttNeonGreen,
                        contentColor = TacticalDarkBg,
                        disabledContainerColor = TacticalSurfaceElevated,
                        disabledContentColor = com.example.ui.theme.TacticalTextMuted
                    )
                ) {
                    Text(
                        text = "SAVE & INITIALIZE ENCRYPTION",
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
fun AddChannelDialog(
    onDismiss: () -> Unit,
    onAddChannel: (name: String, frequency: String, frequencyCode: String, description: String, isEncrypted: Boolean) -> Unit
) {
    var channelName by remember { mutableStateOf("") }
    var frequencyCode by remember {
        val randomNum = Random.nextInt(1000, 9999)
        mutableStateOf("FRQ-$randomNum")
    }
    var frequency by remember { mutableStateOf("462.5625 MHz (FRS 1)") }
    var description by remember { mutableStateOf("") }
    var isEncrypted by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, TacticalCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("add_channel_dialog"),
            colors = CardDefaults.cardColors(containerColor = TacticalDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .background(TacticalCyan.copy(alpha = 0.2f))
                                .border(1.dp, TacticalCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = "New Channel",
                                tint = TacticalCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CREATE CUSTOM CHANNEL",
                                color = TacticalCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Share code with others to join",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_channel_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = channelName,
                    onValueChange = { channelName = it },
                    label = { Text("Channel Name (e.g. SQUAD BRAVO)", color = TacticalTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("channel_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalCyan,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalTextPrimary,
                        unfocusedTextColor = TacticalTextPrimary,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Frequency Code Field with Regenerate Button
                OutlinedTextField(
                    value = frequencyCode,
                    onValueChange = { frequencyCode = it.uppercase() },
                    label = { Text("Frequency Code to Share (e.g. FRQ-7734)", color = TacticalTextSecondary) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val randomNum = Random.nextInt(1000, 9999)
                                frequencyCode = "FRQ-$randomNum"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Generate New Code",
                                tint = TacticalCyan
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("channel_frequency_code_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalCyan,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalCyan,
                        unfocusedTextColor = TacticalCyan,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Radio Frequency Band", color = TacticalTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("channel_frequency_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalCyan,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalTextPrimary,
                        unfocusedTextColor = TacticalTextPrimary,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Group Purpose / Squad Description", color = TacticalTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("channel_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalCyan,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalTextPrimary,
                        unfocusedTextColor = TacticalTextPrimary,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalSurface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "End-to-End Encryption (AES-256)",
                            color = TacticalTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Protects voice audio on this frequency",
                            color = TacticalTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = isEncrypted,
                        onCheckedChange = { isEncrypted = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TacticalCyan,
                            checkedTrackColor = TacticalCyan.copy(alpha = 0.3f),
                            uncheckedThumbColor = TacticalTextSecondary,
                            uncheckedTrackColor = TacticalCardBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (channelName.isNotBlank()) {
                            onAddChannel(
                                channelName.trim(),
                                frequency.trim(),
                                frequencyCode.trim(),
                                description.trim(),
                                isEncrypted
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_channel_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TacticalCyan,
                        contentColor = TacticalDarkBg
                    )
                ) {
                    Text(
                        text = "CREATE CHANNEL & ACTIVATE CODE",
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
fun JoinChannelDialog(
    onDismiss: () -> Unit,
    onJoinChannel: (code: String, customName: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var frequencyCode by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, TacticalCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("join_channel_dialog"),
            colors = CardDefaults.cardColors(containerColor = TacticalDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .background(TacticalCyan.copy(alpha = 0.2f))
                                .border(1.dp, TacticalCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = "Join Channel",
                                tint = TacticalCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "JOIN CHANNEL BY CODE",
                                color = TacticalCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Tune into a shared frequency code",
                                color = TacticalTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_join_channel_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Enter the frequency code provided by the squad or channel creator (e.g. FRQ-7734):",
                    color = TacticalTextPrimary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = frequencyCode,
                    onValueChange = { frequencyCode = it.uppercase().trim() },
                    label = { Text("Frequency Code (e.g. FRQ-7734)", color = TacticalTextSecondary) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val text = clipboardManager.getText()?.text
                                if (!text.isNullOrBlank()) {
                                    frequencyCode = text.trim().uppercase()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste Code",
                                tint = TacticalCyan
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("join_code_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalCyan,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalCyan,
                        unfocusedTextColor = TacticalCyan,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("Optional Channel Nickname", color = TacticalTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("join_nickname_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalCyan,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalTextPrimary,
                        unfocusedTextColor = TacticalTextPrimary,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (frequencyCode.isNotBlank()) {
                            onJoinChannel(frequencyCode, customName)
                        }
                    },
                    enabled = frequencyCode.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_join_channel_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TacticalCyan,
                        contentColor = TacticalDarkBg,
                        disabledContainerColor = TacticalSurface,
                        disabledContentColor = TacticalTextSecondary
                    )
                ) {
                    Text(
                        text = "TUNE IN & JOIN CHANNEL",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
