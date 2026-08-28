package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PublicScannerFeed
import com.example.data.model.ScannerCategory
import com.example.ui.components.SpectrumVisualizer
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
fun LiveScannersScreen(
    isScannerPlaying: Boolean,
    activeScannerId: String?,
    spectrumBars: List<Float>,
    onPlayScanner: (PublicScannerFeed) -> Unit,
    onStopScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<ScannerCategory?>(null) }
    var volumeSlider by remember { mutableStateOf(0.85f) }

    // Broadcastify-style top public safety live audio streams
    val scannerFeeds = listOf(
        PublicScannerFeed(
            id = "scanner_lapd_south",
            name = "LAPD - South Bureau Dispatch & Hotshot",
            agency = "Los Angeles Police Department",
            location = "Los Angeles, CA",
            stateCode = "CA",
            category = ScannerCategory.POLICE,
            frequency = "484.7125 MHz",
            activeListeners = 1420,
            streamUrl = "https://broadcastify.cdnstream1.com/2026",
            description = "Priority dispatch, citywide hotshot calls, and South Los Angeles patrol radio traffic.",
            tag = "HOTSHOT 911"
        ),
        PublicScannerFeed(
            id = "scanner_chicago_fire",
            name = "Chicago Fire Dept - Main & Tactical Extra Alarm",
            agency = "Chicago Fire Department (Engines & Trucks)",
            location = "Chicago, IL",
            stateCode = "IL",
            category = ScannerCategory.FIRE_RESCUE,
            frequency = "154.1300 MHz",
            activeListeners = 980,
            streamUrl = "https://broadcastify.cdnstream1.com/933",
            description = "Live working structure fires, EMS emergency responses, and high-rise alarms.",
            tag = "2ND ALARM ACTIVE"
        ),
        PublicScannerFeed(
            id = "scanner_nypd_citywide",
            name = "NYPD Special Operations & Citywide 1",
            agency = "New York City Police Department",
            location = "New York City, NY",
            stateCode = "NY",
            category = ScannerCategory.POLICE,
            frequency = "476.3875 MHz",
            activeListeners = 1840,
            streamUrl = "https://broadcastify.cdnstream1.com/32525",
            description = "Citywide emergency responses, tactical aviation, ESU, and harbor units.",
            tag = "SPECIAL OPS"
        ),
        PublicScannerFeed(
            id = "scanner_uscg_pac",
            name = "US Coast Guard Sector SF & VHF 16 Marine Distress",
            agency = "US Coast Guard 11th District",
            location = "San Francisco Bay / Pacific",
            stateCode = "CA",
            category = ScannerCategory.MARINE_COAST_GUARD,
            frequency = "156.8000 MHz",
            activeListeners = 630,
            streamUrl = "https://broadcastify.cdnstream1.com/15680",
            description = "Live international marine VHF channel 16, maritime search & rescue, and vessel emergency hails.",
            tag = "USCG MARITIME"
        ),
        PublicScannerFeed(
            id = "scanner_houston_ems",
            name = "Houston Fire & Citywide Paramedic Trauma",
            agency = "Houston Fire & Emergency Medical",
            location = "Houston, TX",
            stateCode = "TX",
            category = ScannerCategory.EMS_MEDICAL,
            frequency = "853.8625 MHz",
            activeListeners = 420,
            streamUrl = "https://broadcastify.cdnstream1.com/11223",
            description = "Trauma responses, advanced life support ambulances, and Life Flight medical air dispatch.",
            tag = "ALS TRAUMA"
        ),
        PublicScannerFeed(
            id = "scanner_lax_tower",
            name = "LAX Tower & SoCal TRACON Air Traffic",
            agency = "Federal Aviation Administration",
            location = "Los Angeles Int'l Airport",
            stateCode = "CA",
            category = ScannerCategory.AVIATION_TOWER,
            frequency = "120.9500 MHz",
            activeListeners = 710,
            streamUrl = "https://broadcastify.cdnstream1.com/12095",
            description = "Commercial airline approach clearances, runways 24L/25R, and air traffic control commands.",
            tag = "ATC TOWER"
        ),
        PublicScannerFeed(
            id = "scanner_miami_metro",
            name = "Miami-Dade Police & Coastal Marine Patrol",
            agency = "Miami-Dade Police Department",
            location = "Miami, FL",
            stateCode = "FL",
            category = ScannerCategory.POLICE,
            frequency = "856.2375 MHz",
            activeListeners = 550,
            streamUrl = "https://broadcastify.cdnstream1.com/4450",
            description = "Coastal patrol, tactical highway units, and seaport emergency communications.",
            tag = "COASTAL PATROL"
        )
    )

    val filteredFeeds = if (selectedCategory == null) {
        scannerFeeds
    } else {
        scannerFeeds.filter { it.category == selectedCategory }
    }

    val activeFeed = scannerFeeds.find { it.id == activeScannerId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = "Live Scanners",
                                tint = TacticalCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIVE POLICE & FIRE SCANNERS",
                                color = TacticalTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "Broadcastify Real-Time Public Safety Feeds",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isScannerPlaying) PttGreenDark else TacticalSurfaceElevated)
                            .border(1.dp, if (isScannerPlaying) PttNeonGreen else TacticalCardBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isScannerPlaying) "LIVE AUDIO STREAMING" else "RECEIVER READY",
                            color = if (isScannerPlaying) PttNeonGreen else TacticalTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // ACTIVE SCANNER PLAYER CARD (When playing)
            item {
                if (isScannerPlaying && activeFeed != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, TacticalCyan)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(PttHotRed)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIVE AUDIO RECEIVER",
                                        color = TacticalCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    text = activeFeed.frequency,
                                    color = TacticalTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = activeFeed.name,
                                color = TacticalTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${activeFeed.agency} • ${activeFeed.location}",
                                color = TacticalTextSecondary,
                                fontSize = 11.5.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Spectrum Visualizer
                            SpectrumVisualizer(
                                bars = spectrumBars,
                                primaryColor = TacticalCyan,
                                modifier = Modifier.height(34.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Controls Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Volume",
                                        tint = TacticalTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Slider(
                                        value = volumeSlider,
                                        onValueChange = { volumeSlider = it },
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors(
                                            thumbColor = TacticalCyan,
                                            activeTrackColor = TacticalCyan,
                                            inactiveTrackColor = TacticalSurface
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = onStopScanner,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PttHotRed,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("stop_live_scanner_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("STOP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // DEPARTMENT FILTER CHIPS
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All Scanners (${scannerFeeds.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TacticalCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = TacticalSurface,
                                labelColor = TacticalTextSecondary
                            )
                        )
                    }

                    items(ScannerCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = if (isSelected) null else category },
                            label = {
                                Text("${category.badgeIcon} ${category.title}", fontSize = 11.sp)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TacticalCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = TacticalSurface,
                                labelColor = TacticalTextSecondary
                            )
                        )
                    }
                }
            }

            // SCANNER FEEDS LIST
            items(filteredFeeds) { feed ->
                val isThisFeedActive = isScannerPlaying && activeScannerId == feed.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (isThisFeedActive) {
                                onStopScanner()
                            } else {
                                onPlayScanner(feed)
                            }
                        }
                        .testTag("scanner_feed_card_${feed.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isThisFeedActive) TacticalSurfaceElevated else TacticalSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isThisFeedActive) 1.5.dp else 1.dp,
                        if (isThisFeedActive) TacticalCyan else TacticalCardBorder
                    )
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
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isThisFeedActive) TacticalCyan.copy(alpha = 0.2f)
                                        else TacticalSurfaceElevated
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (feed.category) {
                                        ScannerCategory.POLICE -> Icons.Default.LocalPolice
                                        ScannerCategory.FIRE_RESCUE -> Icons.Default.LocalFireDepartment
                                        ScannerCategory.EMS_MEDICAL -> Icons.Default.LocalHospital
                                        ScannerCategory.MARINE_COAST_GUARD -> Icons.Default.DirectionsBoat
                                        ScannerCategory.AVIATION_TOWER -> Icons.Default.AirplanemodeActive
                                        ScannerCategory.ALL_HAZARDS -> Icons.Default.Radio
                                    },
                                    contentDescription = feed.category.title,
                                    tint = if (isThisFeedActive) TacticalCyan else TacticalTextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(TacticalSurfaceElevated)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = feed.tag,
                                            color = TacticalCyan,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = "${feed.activeListeners} Listeners",
                                        color = TacticalTextMuted,
                                        fontSize = 9.5.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = feed.name,
                                    color = TacticalTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "${feed.location} • ${feed.frequency}",
                                    color = TacticalTextSecondary,
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                if (isThisFeedActive) onStopScanner() else onPlayScanner(feed)
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isThisFeedActive) PttHotRed else TacticalCyan,
                                contentColor = if (isThisFeedActive) Color.White else Color.Black
                            ),
                            modifier = Modifier.size(42.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (isThisFeedActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isThisFeedActive) "Stop" else "Listen Live",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
