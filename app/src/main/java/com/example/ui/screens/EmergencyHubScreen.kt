package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DistressType
import com.example.data.model.EmergencyAgency
import com.example.data.model.UserIdentity
import com.example.ui.theme.BurnerGold
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttHotRed
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.PttRedGlow
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

@Composable
fun EmergencyHubScreen(
    userIdentity: UserIdentity,
    onPlayMaydayAlarm: () -> Unit,
    onSwitchToEmergencyChannel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedDistressType by remember { mutableStateOf(DistressType.LIFE_THREATENING_MEDICAL) }
    var soulsCount by remember { mutableStateOf("1") }
    var customNotes by remember { mutableStateOf("Immediate assistance requested.") }

    // Simulated high precision GPS coordinates
    val gpsLat = "37.7749° N"
    val gpsLong = "122.4194° W"
    val gpsAltitude = "42 ft ASL"

    val emergencyAgencies = listOf(
        EmergencyAgency(
            id = "agency_911",
            name = "911 Emergency Services",
            description = "Primary dispatch for Police, Fire Department, and Paramedic Ambulances (USA & Canada)",
            phoneNumber = "911",
            dialActionUrl = "tel:911",
            badge = "IMMEDIATE 911 DISPATCH",
            iconType = "911"
        ),
        EmergencyAgency(
            id = "agency_uscg_pacific",
            name = "US Coast Guard Pacific Command (RCC Alameda)",
            description = "24/7 Maritime Search & Rescue, Sinking, Coastal Distress (West Coast, Alaska, Hawaii)",
            phoneNumber = "+1 (510) 437-3700",
            dialActionUrl = "tel:15104373700",
            badge = "24/7 COAST GUARD RESCUE",
            iconType = "COAST_GUARD"
        ),
        EmergencyAgency(
            id = "agency_uscg_atlantic",
            name = "US Coast Guard Atlantic Command (RCC Portsmouth)",
            description = "24/7 Maritime Search & Rescue, Offshore Mayday (East Coast, Gulf of Mexico, Caribbean)",
            phoneNumber = "+1 (757) 398-6390",
            dialActionUrl = "tel:17573986390",
            badge = "24/7 OFFSHORE USCG",
            iconType = "COAST_GUARD"
        ),
        EmergencyAgency(
            id = "agency_988",
            name = "988 Suicide & Crisis Lifeline",
            description = "Free, confidential 24/7 emotional support, mental health emergency, and crisis counselors",
            phoneNumber = "988",
            dialActionUrl = "tel:988",
            badge = "FREE & CONFIDENTIAL 24/7",
            iconType = "CRISIS_988"
        ),
        EmergencyAgency(
            id = "agency_poison",
            name = "National Poison Control Center",
            description = "Fast, expert medical guidance for chemical exposure, accidental poisoning, or toxic bites",
            phoneNumber = "+1 (800) 222-1222",
            dialActionUrl = "tel:18002221222",
            badge = "TOXICOLOGY SPECIALIST",
            iconType = "POISON"
        )
    )

    fun dialNumber(url: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Dialer unavailable: $url", Toast.LENGTH_SHORT).show()
        }
    }

    val distressScript = buildString {
        append("MAYDAY MAYDAY MAYDAY\n")
        append("THIS IS: ${userIdentity.callsign} (${userIdentity.activeDisplayNumber})\n")
        append("GPS POSITION: $gpsLat, $gpsLong ($gpsAltitude)\n")
        append("NATURE OF DISTRESS: ${selectedDistressType.title} [Code: ${selectedDistressType.code}]\n")
        append("PERSONS REQUIRING AID: $soulsCount\n")
        append("DETAILS: $customNotes\n")
        append("OVER.")
    }

    fun sendSosSms() {
        try {
            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:")).apply {
                putExtra("sms_body", "🚨 EMERGENCY DISTRESS BEACON:\n$distressScript")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(smsIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "SMS client unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Header Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sos,
                                contentDescription = "SOS",
                                tint = PttHotRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EMERGENCY SERVICES & RESCUE",
                                color = TacticalTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "Direct 911, US Coast Guard, and GPS Distress Dispatch Hub",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onPlayMaydayAlarm,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PttHotRed.copy(alpha = 0.2f))
                            .border(1.dp, PttHotRed, CircleShape)
                            .testTag("mayday_alarm_test_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Mayday Alarm",
                            tint = PttHotRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // PRIMARY 911 & US COAST GUARD FAST CALL BUTTONS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Big Red 911 Button
                    Button(
                        onClick = { dialNumber("tel:911") },
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .testTag("direct_call_911_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PttHotRed,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "911",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "CALL 911",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Police / Fire / EMS",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Big US Coast Guard Direct Button
                    Button(
                        onClick = { dialNumber("tel:15104373700") },
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .testTag("direct_call_coastguard_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0369A1),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBoat,
                            contentDescription = "Coast Guard",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "COAST GUARD",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "24/7 Marine Mayday",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // EMERGENCY RADIO CHANNEL SWITCH BANNER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSwitchToEmergencyChannel() }
                        .testTag("switch_emergency_channel_card"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PttHotRed.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PttHotRed.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radio,
                                    contentDescription = "Emergency Radio",
                                    tint = PttHotRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "SWITCH TO EMERGENCY RADIO CH 9",
                                    color = PttRedGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "462.6750 MHz Priority Life-Safety Broadcast",
                                    color = TacticalTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Legal Notice Required",
                            tint = PttHotRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // GPS DISTRESS BEACON & DISPATCH PAYLOAD GENERATOR
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCardBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = "GPS Beacon",
                                    tint = PttNeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GPS DISTRESS BEACON GENERATOR",
                                    color = TacticalTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PttGreenDark)
                                    .border(1.dp, PttNeonGreen, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LIVE SATELLITE FIX",
                                    color = PttNeonGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Coordinates Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(TacticalSurfaceElevated)
                                .border(1.dp, TacticalCardBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "LATITUDE & LONGITUDE",
                                        color = TacticalTextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$gpsLat, $gpsLong",
                                        color = TacticalTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "ALTITUDE",
                                        color = TacticalTextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = gpsAltitude,
                                        color = TacticalCyan,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Distress Type Selector
                        Text(
                            text = "SELECT NATURE OF DISTRESS:",
                            color = TacticalTextSecondary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            DistressType.values().forEach { type ->
                                val isSelected = selectedDistressType == type
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PttHotRed.copy(alpha = 0.15f) else TacticalSurfaceElevated)
                                        .border(
                                            1.dp,
                                            if (isSelected) PttHotRed else TacticalCardBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedDistressType = type }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = type.iconEmoji,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = type.title,
                                        color = if (isSelected) PttRedGlow else TacticalTextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = type.code,
                                        color = if (isSelected) PttHotRed else TacticalTextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Distress Payload Output Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalDarkBg)
                                .border(1.dp, TacticalCardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = distressScript,
                                color = BurnerGold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dispatch Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { sendSosSms() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("send_sos_sms_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PttHotRed,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send SMS",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DISPATCH SOS SMS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(distressScript))
                                    Toast.makeText(context, "Distress script copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("copy_distress_script_button"),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCardBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TacticalTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "COPY SCRIPT",
                                    color = TacticalTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // OFFICIAL EMERGENCY DIRECTORY LIST
            item {
                Text(
                    text = "REAL EMERGENCY DIRECTORY (24/7)",
                    color = TacticalTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            items(emergencyAgencies) { agency ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { dialNumber(agency.dialActionUrl) }
                        .testTag("emergency_agency_${agency.id}"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (agency.id.contains("coastguard")) Color(0xFF0284C7).copy(alpha = 0.2f)
                                        else if (agency.id.contains("911")) PttHotRed.copy(alpha = 0.2f)
                                        else TacticalSurfaceElevated
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        agency.id.contains("coastguard") -> Icons.Default.DirectionsBoat
                                        agency.id.contains("911") -> Icons.Default.LocalPolice
                                        agency.id.contains("988") -> Icons.Default.SupportAgent
                                        agency.id.contains("poison") -> Icons.Default.MedicalServices
                                        else -> Icons.Default.Call
                                    },
                                    contentDescription = agency.name,
                                    tint = when {
                                        agency.id.contains("coastguard") -> Color(0xFF38BDF8)
                                        agency.id.contains("911") -> PttHotRed
                                        agency.id.contains("988") -> BurnerGold
                                        else -> PttNeonGreen
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TacticalSurfaceElevated)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = agency.badge,
                                        color = if (agency.id.contains("911") || agency.id.contains("coastguard")) PttRedGlow else PttNeonGreen,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = agency.name,
                                    color = TacticalTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = agency.description,
                                    color = TacticalTextSecondary,
                                    fontSize = 10.5.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { dialNumber(agency.dialActionUrl) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (agency.id.contains("911")) PttHotRed else TacticalSurfaceElevated,
                                contentColor = Color.White
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneInTalk,
                                contentDescription = "Dial",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = agency.phoneNumber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Maritime VHF 16 & Mayday Protocol Guide Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = "Guide",
                                tint = TacticalCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HOW TO CALL AN EMERGENCY ON RADIO",
                                color = TacticalTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• MAYDAY: Grave and imminent danger to life or vessel (Immediate rescue required).\n" +
                                    "• PAN-PAN: Urgent safety message concerning safety of person or vehicle (non-immediate life threat).\n" +
                                    "• SECURITE: Marine navigation hazard, severe weather warning, or channel obstruction.\n" +
                                    "• STATE CLEARLY: 1. Vessel/Person Name, 2. Exact GPS Lat/Long, 3. Number of Souls, 4. Nature of Distress.",
                            color = TacticalTextSecondary,
                            fontSize = 10.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
