package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CreateWorldwideRoomDialog(
    onDismiss: () -> Unit,
    onCreateRoom: (
        name: String,
        country: String,
        countryCode: String,
        countryFlag: String,
        city: String,
        region: String,
        category: String,
        description: String,
        tags: String
    ) -> Unit
) {
    var roomName by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("United States") }
    var selectedCode by remember { mutableStateOf("US") }
    var selectedFlag by remember { mutableStateOf("🇺🇸") }
    var selectedRegion by remember { mutableStateOf("Americas") }
    var city by remember { mutableStateOf("Los Angeles") }
    var selectedCategory by remember { mutableStateOf("General") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("VOICE,MEET,TALK") }

    val presetCountries = listOf(
        CountryOption("United States", "US", "🇺🇸", "Los Angeles"),
        CountryOption("Japan", "JP", "🇯🇵", "Osaka"),
        CountryOption("United Kingdom", "GB", "🇬🇧", "Manchester"),
        CountryOption("France", "FR", "🇫🇷", "Lyon"),
        CountryOption("Germany", "DE", "🇩🇪", "Munich"),
        CountryOption("South Korea", "KR", "🇰🇷", "Busan"),
        CountryOption("Brazil", "BR", "🇧🇷", "São Paulo"),
        CountryOption("Australia", "AU", "🇦🇺", "Melbourne"),
        CountryOption("Canada", "CA", "🇨🇦", "Vancouver"),
        CountryOption("Italy", "IT", "🇮🇹", "Milan"),
        CountryOption("Spain", "ES", "🇪🇸", "Madrid"),
        CountryOption("United Arab Emirates", "AE", "🇦🇪", "Abu Dhabi")
    )

    val categories = listOf("General", "Night Owls", "Music & Jam", "Travel & Meet", "Tech & Gaming", "Language Exchange")

    val primaryCyan = Color(0xFF38BDF8)
    val darkCard = Color(0xFF161B22)
    val darkSurface = Color(0xFF0D1117)
    val borderCol = Color(0xFF30363D)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            color = darkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
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
                                .background(primaryCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = primaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CREATE GLOBAL ROOM",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Host a live walkie chatroom for any city",
                                color = Color(0xFF8B949E),
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF8B949E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Room Name
                Text(
                    text = "ROOM NAME / TOPIC",
                    color = Color(0xFF8B949E),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    placeholder = { Text("e.g., Osaka Late Night Radio 📻", color = Color(0xFF8B949E), fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryCyan,
                        unfocusedBorderColor = Color(0xFF30363D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = darkCard,
                        unfocusedContainerColor = darkCard
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Country Selector
                Text(
                    text = "SELECT HOST COUNTRY",
                    color = Color(0xFF8B949E),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetCountries.forEach { c ->
                        val isSel = selectedCountry == c.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) primaryCyan.copy(alpha = 0.2f) else darkCard)
                                .border(1.dp, if (isSel) primaryCyan else Color(0xFF30363D), RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedCountry = c.name
                                    selectedCode = c.code
                                    selectedFlag = c.flag
                                    city = c.defaultCity
                                    selectedRegion = when (c.code) {
                                        "US", "CA", "BR", "MX" -> "Americas"
                                        "GB", "FR", "DE", "IT", "ES" -> "Europe"
                                        "JP", "KR", "AU", "IN" -> "Asia-Pacific"
                                        "AE" -> "Middle East"
                                        else -> "Global"
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${c.flag} ${c.name}",
                                color = if (isSel) Color.White else Color(0xFFC9D1D9),
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // City
                Text(
                    text = "CITY / REGION",
                    color = Color(0xFF8B949E),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryCyan,
                        unfocusedBorderColor = Color(0xFF30363D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = darkCard,
                        unfocusedContainerColor = darkCard
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category
                Text(
                    text = "ROOM CATEGORY",
                    color = Color(0xFF8B949E),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF3FB950).copy(alpha = 0.2f) else darkCard)
                                .border(1.dp, if (isSel) Color(0xFF3FB950) else Color(0xFF30363D), RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.White else Color(0xFFC9D1D9),
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                Text(
                    text = "DESCRIPTION / TOPIC",
                    color = Color(0xFF8B949E),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("What should people talk about?", color = Color(0xFF8B949E), fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryCyan,
                        unfocusedBorderColor = Color(0xFF30363D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = darkCard,
                        unfocusedContainerColor = darkCard
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val finalName = if (roomName.isBlank()) "$city Worldwide Airwaves $selectedFlag" else roomName
                        val finalDesc = if (description.isBlank()) "Live conversation and community walkie in $city, $selectedCountry." else description
                        onCreateRoom(
                            finalName,
                            selectedCountry,
                            selectedCode,
                            selectedFlag,
                            city,
                            selectedRegion,
                            selectedCategory,
                            finalDesc,
                            tags
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryCyan,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CREATE & GO LIVE ON AIRWAVES",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
