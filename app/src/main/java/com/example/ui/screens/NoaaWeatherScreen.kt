package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.NoaaWeatherStation
import com.example.data.model.WeatherAlertLevel
import com.example.ui.components.SpectrumVisualizer
import com.example.ui.theme.BurnerGold
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttHotRed
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
fun NoaaWeatherScreen(
    isNoaaPlaying: Boolean,
    activeNoaaId: String?,
    spectrumBars: List<Float>,
    onPlayNoaa: (NoaaWeatherStation) -> Unit,
    onStopNoaa: () -> Unit,
    onPlayAlertTone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var volumeSlider by remember { mutableStateOf(0.9f) }

    // Official Real NOAA Weather Radio Stations (Continuous Audio Broadcasts)
    val noaaStations = listOf(
        NoaaWeatherStation(
            id = "noaa_kwo35_nyc",
            callsign = "KWO35",
            locationName = "New York City & Tri-State Area",
            state = "NY",
            frequencyMhz = "162.550 MHz",
            streamUrl = "https://stream.weatherusa.net/KWO35",
            currentTempF = 72,
            condition = "Partly Cloudy",
            humidityPct = 58,
            windMph = 8,
            windDir = "NE",
            baroInHg = 30.12,
            alertLevel = WeatherAlertLevel.NORMAL,
            activeAlertText = "Fair skies. Coastal marine navigation calm with 1-2 ft seas."
        ),
        NoaaWeatherStation(
            id = "noaa_kzz57_chicago",
            callsign = "KZZ57",
            locationName = "Chicago Metro & Lake Michigan",
            state = "IL",
            frequencyMhz = "162.425 MHz",
            streamUrl = "https://stream.weatherusa.net/KZZ57",
            currentTempF = 68,
            condition = "Breezy Lake Wind",
            humidityPct = 64,
            windMph = 15,
            windDir = "NNE",
            baroInHg = 29.98,
            alertLevel = WeatherAlertLevel.ADVISORY,
            activeAlertText = "Small Craft Advisory in effect for southern Lake Michigan waters."
        ),
        NoaaWeatherStation(
            id = "noaa_khb34_miami",
            callsign = "KHB34",
            locationName = "Miami & South Florida Coastal",
            state = "FL",
            frequencyMhz = "162.550 MHz",
            streamUrl = "https://stream.weatherusa.net/KHB34",
            currentTempF = 86,
            condition = "Tropical Thunderstorms",
            humidityPct = 78,
            windMph = 12,
            windDir = "ESE",
            baroInHg = 29.94,
            alertLevel = WeatherAlertLevel.WATCH,
            activeAlertText = "Severe Thunderstorm Watch in effect. Rip current risk elevated along Atlantic beaches."
        ),
        NoaaWeatherStation(
            id = "noaa_wxl40_la",
            callsign = "WXL40",
            locationName = "Los Angeles & SoCal Coastal",
            state = "CA",
            frequencyMhz = "162.550 MHz",
            streamUrl = "https://stream.weatherusa.net/WXL40",
            currentTempF = 78,
            condition = "Clear Sky & Sunny",
            humidityPct = 42,
            windMph = 6,
            windDir = "WSW",
            baroInHg = 30.04,
            alertLevel = WeatherAlertLevel.NORMAL,
            activeAlertText = "Clear visibility across coastal basins and inland valleys."
        ),
        NoaaWeatherStation(
            id = "noaa_wxm42_seattle",
            callsign = "WXM42",
            locationName = "Seattle & Puget Sound Marine",
            state = "WA",
            frequencyMhz = "162.425 MHz",
            streamUrl = "https://stream.weatherusa.net/WXM42",
            currentTempF = 62,
            condition = "Coastal Overcast & Rain",
            humidityPct = 82,
            windMph = 9,
            windDir = "SSW",
            baroInHg = 29.88,
            alertLevel = WeatherAlertLevel.ADVISORY,
            activeAlertText = "Coastal light rain and maritime fog advisory overnight."
        ),
        NoaaWeatherStation(
            id = "noaa_kec61_denver",
            callsign = "KEC61",
            locationName = "Denver & Front Range High Plains",
            state = "CO",
            frequencyMhz = "162.550 MHz",
            streamUrl = "https://stream.weatherusa.net/KEC61",
            currentTempF = 71,
            condition = "Sunny / High Plains Breeze",
            humidityPct = 28,
            windMph = 14,
            windDir = "WNW",
            baroInHg = 30.18,
            alertLevel = WeatherAlertLevel.NORMAL,
            activeAlertText = "Dry conditions with low humidity across the eastern plains."
        )
    )

    val activeStation = noaaStations.find { it.id == activeNoaaId }

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
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "NOAA Weather",
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NOAA WEATHER RADIO NWS",
                                color = TacticalTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "Real-Time 24/7 National Weather Service Audio Station",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onPlayAlertTone,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PttHotRed.copy(alpha = 0.2f))
                            .border(1.dp, PttHotRed, CircleShape)
                            .testTag("noaa_1050hz_siren_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "SAME 1050Hz Siren Test",
                            tint = PttHotRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ACTIVE NOAA STATION PLAYER (When playing)
            item {
                if (isNoaaPlaying && activeStation != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF60A5FA))
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
                                            .background(Color(0xFF60A5FA))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIVE NWS BROADCAST",
                                        color = Color(0xFF60A5FA),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    text = "${activeStation.callsign} • ${activeStation.frequencyMhz}",
                                    color = TacticalTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = activeStation.locationName,
                                color = TacticalTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            // Weather Telemetry Strip
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TacticalSurface)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text("TEMP", fontSize = 8.sp, color = TacticalTextMuted, fontWeight = FontWeight.Bold)
                                        Text("${activeStation.currentTempF}°F", fontSize = 14.sp, color = TacticalTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TacticalSurface)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text("WIND", fontSize = 8.sp, color = TacticalTextMuted, fontWeight = FontWeight.Bold)
                                        Text("${activeStation.windMph} mph ${activeStation.windDir}", fontSize = 12.sp, color = TacticalCyan, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TacticalSurface)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text("HUMIDITY", fontSize = 8.sp, color = TacticalTextMuted, fontWeight = FontWeight.Bold)
                                        Text("${activeStation.humidityPct}%", fontSize = 12.sp, color = TacticalTextSecondary, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TacticalSurface)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text("BARO", fontSize = 8.sp, color = TacticalTextMuted, fontWeight = FontWeight.Bold)
                                        Text("${activeStation.baroInHg}", fontSize = 12.sp, color = TacticalTextSecondary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Spectrum Visualizer
                            SpectrumVisualizer(
                                bars = spectrumBars,
                                primaryColor = Color(0xFF60A5FA),
                                modifier = Modifier.height(30.dp)
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
                                            thumbColor = Color(0xFF60A5FA),
                                            activeTrackColor = Color(0xFF60A5FA),
                                            inactiveTrackColor = TacticalSurface
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = onStopNoaa,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PttHotRed,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("stop_noaa_button")
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

            // STATIONS LIST
            items(noaaStations) { station ->
                val isThisPlaying = isNoaaPlaying && activeNoaaId == station.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (isThisPlaying) onStopNoaa() else onPlayNoaa(station)
                        }
                        .testTag("noaa_station_card_${station.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isThisPlaying) TacticalSurfaceElevated else TacticalSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isThisPlaying) 1.5.dp else 1.dp,
                        if (isThisPlaying) Color(0xFF60A5FA) else TacticalCardBorder
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
                                        if (isThisPlaying) Color(0xFF60A5FA).copy(alpha = 0.2f)
                                        else TacticalSurfaceElevated
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radio,
                                    contentDescription = "NOAA Station",
                                    tint = if (isThisPlaying) Color(0xFF60A5FA) else TacticalTextPrimary,
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
                                            text = station.callsign,
                                            color = Color(0xFF60A5FA),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = station.frequencyMhz,
                                        color = TacticalTextMuted,
                                        fontSize = 9.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = station.locationName,
                                    color = TacticalTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "${station.condition} • ${station.currentTempF}°F • Wind ${station.windMph}mph",
                                    color = TacticalTextSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                if (isThisPlaying) onStopNoaa() else onPlayNoaa(station)
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isThisPlaying) PttHotRed else Color(0xFF60A5FA),
                                contentColor = if (isThisPlaying) Color.White else Color.Black
                            ),
                            modifier = Modifier.size(42.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (isThisPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isThisPlaying) "Stop" else "Tune In",
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
