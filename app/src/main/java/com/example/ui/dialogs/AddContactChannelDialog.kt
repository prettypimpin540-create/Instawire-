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
    onAddContact: (name: String, number: String, callsign: String, isBurner: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var callsign by remember { mutableStateOf("") }
    var isBurner by remember { mutableStateOf(false) }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_callsign_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PttNeonGreen,
                        unfocusedBorderColor = TacticalCardBorder,
                        focusedTextColor = TacticalTextPrimary,
                        unfocusedTextColor = TacticalTextPrimary,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface
                    )
                )

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
                        text = "Is VIP Burner Number?",
                        color = TacticalTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isBurner,
                        onCheckedChange = { isBurner = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PttNeonGreen,
                            checkedTrackColor = PttGreenDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && number.isNotBlank()) {
                            onAddContact(name, number, callsign, isBurner)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_contact_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PttNeonGreen,
                        contentColor = TacticalDarkBg
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
    onAddChannel: (name: String, frequency: String, description: String) -> Unit
) {
    var channelName by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("462.7000 MHz") }
    var description by remember { mutableStateOf("") }

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
                        Text(
                            text = "CREATE FREQUENCY CHANNEL",
                            color = TacticalCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
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

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (channelName.isNotBlank()) {
                            onAddChannel(channelName, frequency, description)
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
                        text = "LOCK ENCRYPTED CHANNEL",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
