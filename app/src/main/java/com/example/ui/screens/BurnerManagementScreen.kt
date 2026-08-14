package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Whatshot
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.BurnerLine
import com.example.data.model.NumberType
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserIdentity
import com.example.ui.theme.BurnerGold
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
import java.util.Locale
import kotlin.random.Random

data class AreaCodePreset(
    val code: String,
    val city: String,
    val state: String
)

val MAJOR_AREA_CODES = listOf(
    AreaCodePreset("415", "San Francisco", "CA"),
    AreaCodePreset("212", "New York", "NY"),
    AreaCodePreset("310", "Los Angeles", "CA"),
    AreaCodePreset("305", "Miami", "FL"),
    AreaCodePreset("512", "Austin", "TX"),
    AreaCodePreset("206", "Seattle", "WA"),
    AreaCodePreset("702", "Las Vegas", "NV"),
    AreaCodePreset("312", "Chicago", "IL"),
    AreaCodePreset("888", "Toll-Free USA", "US"),
    AreaCodePreset("800", "VIP Ghost Line", "US")
)

@Composable
fun BurnerManagementScreen(
    userIdentity: UserIdentity,
    savedBurnerLines: List<BurnerLine>,
    onToggleActiveNumber: () -> Unit,
    onActivateBurnerLine: (BurnerLine) -> Unit,
    onAddAndActivateBurnerLine: (number: String, label: String, areaCode: String, cityRegion: String) -> Unit,
    onDeleteBurnerLine: (String) -> Unit,
    onOpenSubscriptionPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val tier = userIdentity.subscriptionTier

    var selectedAreaCode by remember { mutableStateOf("415") }
    var selectedCityRegion by remember { mutableStateOf("San Francisco, CA") }
    var customAreaCodeInput by remember { mutableStateOf("") }
    var customLabelInput by remember { mutableStateOf("") }
    var isLocationDetecting by remember { mutableStateOf(false) }
    var locationStatusMessage by remember { mutableStateOf<String?>(null) }
    var selectedGeneratedNumber by remember { mutableStateOf<String?>(null) }

    // Number generator pool based on tier limits and selected area code
    val poolSize = tier.burnerSelectionPoolSize
    var generationSeed by remember { mutableStateOf(1) }

    val generatedPool = remember(selectedAreaCode, tier, generationSeed) {
        generateNumberCandidates(selectedAreaCode, tier, poolSize)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            detectAreaCodeFromLocation(
                context = context,
                onDetected = { code, region ->
                    selectedAreaCode = code
                    selectedCityRegion = region
                    locationStatusMessage = "Detected Location: $region ($code)"
                    isLocationDetecting = false
                },
                onError = {
                    selectedAreaCode = "415"
                    selectedCityRegion = "San Francisco, CA (Default)"
                    locationStatusMessage = "Location unavailable. Defaulted to (415)"
                    isLocationDetecting = false
                }
            )
        } else {
            locationStatusMessage = "Location permission denied. Select area code manually below."
            isLocationDetecting = false
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card: Active Outgoing Line & Privacy Shield Status
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("active_burner_card"),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                                    .background(
                                        if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold.copy(alpha = 0.2f)
                                        else TacticalCyan.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (userIdentity.activeNumberType == NumberType.BURNER) Icons.Default.Whatshot else Icons.Default.PhoneAndroid,
                                    contentDescription = "Active Line",
                                    tint = if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold else TacticalCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "OUTGOING CALLER ID",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (userIdentity.activeNumberType == NumberType.BURNER) "ANONYMOUS BURNER LINE" else "PRIMARY VERIFIED NUMBER",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold else TacticalTextPrimary
                                )
                            }
                        }

                        // Privacy Indicator Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold.copy(alpha = 0.2f) else TacticalSurfaceElevated)
                                .border(1.dp, if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold else TacticalCardBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (userIdentity.activeNumberType == NumberType.BURNER) Icons.Default.Security else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold else TacticalTextMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (userIdentity.activeNumberType == NumberType.BURNER) "SHIELDED" else "EXPOSED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold else TacticalTextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Number Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(TacticalDarkBg)
                            .border(1.dp, TacticalCardBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = userIdentity.activeDisplayNumber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userIdentity.activeNumberType == NumberType.BURNER) BurnerGold else TacticalTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (userIdentity.activeNumberType == NumberType.BURNER) "Carrier: InstaWire Virtual Cloud Mesh" else "Carrier: Cellular Primary Sim",
                                    fontSize = 11.sp,
                                    color = TacticalTextMuted
                                )
                            }

                            // 1-Tap Toggle between Primary and Burner
                            Button(
                                onClick = { onToggleActiveNumber() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (userIdentity.activeNumberType == NumberType.BURNER) TacticalSurfaceElevated else BurnerGold
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("toggle_active_number_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Swap",
                                    tint = if (userIdentity.activeNumberType == NumberType.BURNER) TacticalTextPrimary else TacticalDarkBg,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (userIdentity.activeNumberType == NumberType.BURNER) "Use SIM" else "Use Burner",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userIdentity.activeNumberType == NumberType.BURNER) TacticalTextPrimary else TacticalDarkBg
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Subscription Tier Burner Allocation Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, TacticalCardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = TacticalSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${tier.badgeLabel} POOL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${tier.burnerSelectionPoolSize} Numbers Available",
                                fontSize = 11.sp,
                                color = TacticalTextSecondary
                            )
                        }
                        Text(
                            text = if (tier == SubscriptionTier.FREE) "Upgrade to generate and activate live burner numbers"
                            else "Allocated slots: ${savedBurnerLines.size} / ${if (tier.hasUnlimitedBurners) "Unlimited" else tier.burnerLinesLimit}",
                            fontSize = 12.sp,
                            color = TacticalTextMuted
                        )
                    }

                    Button(
                        onClick = { onOpenSubscriptionPlans() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("tier_upgrade_btn")
                    ) {
                        Text(
                            text = if (tier == SubscriptionTier.GHOST_SENTINEL) "VIP Max" else "More Lines",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            }
        }

        // 3. Area Code Selection & GPS Location Detection
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SELECT REGION & AREA CODE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalTextMuted,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))

                // GPS Location Quick Detect Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, TacticalCardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            isLocationDetecting = true
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            if (hasFine || hasCoarse) {
                                detectAreaCodeFromLocation(
                                    context = context,
                                    onDetected = { code, region ->
                                        selectedAreaCode = code
                                        selectedCityRegion = region
                                        locationStatusMessage = "Detected Location: $region ($code)"
                                        isLocationDetecting = false
                                    },
                                    onError = {
                                        selectedAreaCode = "415"
                                        selectedCityRegion = "San Francisco, CA (Default)"
                                        locationStatusMessage = "Location unavailable. Defaulted to (415)"
                                        isLocationDetecting = false
                                    }
                                )
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                        .testTag("detect_location_area_code_btn"),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Location",
                            tint = TacticalCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Use Nearby GPS Location",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextPrimary
                            )
                            Text(
                                text = "Detect area code based on your current device location",
                                fontSize = 11.sp,
                                color = TacticalTextMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TacticalCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (locationStatusMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = locationStatusMessage ?: "",
                        fontSize = 11.sp,
                        color = TacticalCyan,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Area Code Horizontal Selector
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp),
                    modifier = Modifier.testTag("area_code_selector_row")
                ) {
                    items(MAJOR_AREA_CODES) { preset ->
                        val isSelected = selectedAreaCode == preset.code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) accentColor.copy(alpha = 0.25f) else TacticalSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) accentColor else TacticalCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedAreaCode = preset.code
                                    selectedCityRegion = "${preset.city}, ${preset.state}"
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("area_code_${preset.code}")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "(${preset.code})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) accentColor else TacticalTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = preset.city,
                                    fontSize = 10.sp,
                                    color = TacticalTextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Area Code & Line Label Inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customAreaCodeInput,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }.take(3)
                            customAreaCodeInput = clean
                            if (clean.length == 3) {
                                selectedAreaCode = clean
                                selectedCityRegion = "Custom Area ($clean)"
                            }
                        },
                        label = { Text("Custom Code", fontSize = 11.sp) },
                        placeholder = { Text("e.g. 917", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = TacticalCardBorder,
                            focusedTextColor = TacticalTextPrimary,
                            unfocusedTextColor = TacticalTextPrimary,
                            focusedLabelColor = accentColor,
                            unfocusedLabelColor = TacticalTextMuted
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_area_code_input")
                    )

                    OutlinedTextField(
                        value = customLabelInput,
                        onValueChange = { customLabelInput = it },
                        label = { Text("Line Label", fontSize = 11.sp) },
                        placeholder = { Text("e.g. Work Ghost", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = TacticalCardBorder,
                            focusedTextColor = TacticalTextPrimary,
                            unfocusedTextColor = TacticalTextPrimary,
                            focusedLabelColor = accentColor,
                            unfocusedLabelColor = TacticalTextMuted
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("custom_label_input")
                    )
                }
            }
        }

        // 4. App-Generated Numbers Pool (Tiered Selection)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GENERATED NUMBER POOL (${generatedPool.size} OPTIONS)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalTextMuted,
                        fontFamily = FontFamily.Monospace
                    )

                    IconButton(
                        onClick = { generationSeed++ },
                        modifier = Modifier.size(28.dp).testTag("refresh_pool_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Candidates",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("generated_number_pool_list")
                ) {
                    generatedPool.forEachIndexed { index, number ->
                        val isChosen = selectedGeneratedNumber == number
                        val isCurrentActive = userIdentity.burnerNumber == number && userIdentity.activeNumberType == NumberType.BURNER

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (isCurrentActive) BurnerGold
                                    else if (isChosen) accentColor
                                    else TacticalCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedGeneratedNumber = number
                                }
                                .testTag("generated_candidate_$index"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChosen) TacticalSurfaceElevated else TacticalSurface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (tier == SubscriptionTier.GHOST_SENTINEL) Icons.Default.Star else Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = if (isCurrentActive) BurnerGold else if (isChosen) accentColor else TacticalTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = number,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrentActive) BurnerGold else if (isChosen) accentColor else TacticalTextPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "$selectedCityRegion • Instant Provisioning",
                                            fontSize = 11.sp,
                                            color = TacticalTextMuted
                                        )
                                    }
                                }

                                if (isCurrentActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(BurnerGold.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BurnerGold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            if (tier == SubscriptionTier.FREE) {
                                                onOpenSubscriptionPlans()
                                            } else {
                                                onAddAndActivateBurnerLine(
                                                    number,
                                                    if (customLabelInput.isNotBlank()) customLabelInput else "Line ($selectedAreaCode)",
                                                    selectedAreaCode,
                                                    selectedCityRegion
                                                )
                                                selectedGeneratedNumber = null
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (tier == SubscriptionTier.FREE) TacticalSurfaceElevated else accentColor
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("activate_number_btn_$index")
                                    ) {
                                        Text(
                                            text = if (tier == SubscriptionTier.FREE) "Unlock Tier" else "Claim & Swap",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (tier == SubscriptionTier.FREE) accentColor else TacticalDarkBg
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Saved Burner Vault & Quick Swap List
        if (savedBurnerLines.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SAVED BURNER VAULT (${savedBurnerLines.size} LINES)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalTextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("saved_burner_vault_list")
                    ) {
                        savedBurnerLines.forEach { line ->
                            val isCurrentlyActive = userIdentity.burnerNumber == line.number && userIdentity.activeNumberType == NumberType.BURNER

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        1.dp,
                                        if (isCurrentlyActive) BurnerGold else TacticalCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .testTag("saved_line_${line.number}"),
                                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Whatshot,
                                            contentDescription = null,
                                            tint = if (isCurrentlyActive) BurnerGold else TacticalAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = line.number,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCurrentlyActive) BurnerGold else TacticalTextPrimary,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = "${line.label} • ${line.cityRegion}",
                                                fontSize = 11.sp,
                                                color = TacticalTextMuted
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isCurrentlyActive) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(BurnerGold.copy(alpha = 0.2f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "CURRENT CALLER ID",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BurnerGold,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = { onActivateBurnerLine(line) },
                                                colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.testTag("swap_to_line_${line.number}")
                                            ) {
                                                Text(
                                                    text = "Swap To",
                                                    fontSize = 11.sp,
                                                    color = TacticalCyan
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        IconButton(
                                            onClick = { onDeleteBurnerLine(line.number) },
                                            modifier = Modifier.size(32.dp).testTag("delete_line_${line.number}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Burn Line",
                                                tint = PttHotRed.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun generateNumberCandidates(areaCode: String, tier: SubscriptionTier, count: Int): List<String> {
    val result = mutableListOf<String>()
    val rng = Random(areaCode.hashCode() + tier.ordinal * 100)

    for (i in 0 until count) {
        val mid = when (tier) {
            SubscriptionTier.GHOST_SENTINEL -> if (rng.nextBoolean()) "GHOST" else "777"
            SubscriptionTier.BLACK_OPS -> if (rng.nextBoolean()) "OPS" else rng.nextInt(200, 899).toString()
            else -> "WIRE"
        }
        val end = rng.nextInt(1000, 9999)
        val formatted = if (mid.length == 3) {
            "+1 ($areaCode) $mid-$end"
        } else {
            "+1 ($areaCode) $mid-$end"
        }
        result.add(formatted)
    }
    return result
}

private fun detectAreaCodeFromLocation(
    context: Context,
    onDetected: (code: String, region: String) -> Unit,
    onError: () -> Unit
) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val lastLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (lastLocation != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val state = addresses[0].adminArea ?: "CA"
                val city = addresses[0].locality ?: "San Francisco"
                val matchedCode = when (state.uppercase()) {
                    "CALIFORNIA", "CA" -> if (city.contains("Los Angeles", true)) "310" else "415"
                    "NEW YORK", "NY" -> "212"
                    "TEXAS", "TX" -> "512"
                    "FLORIDA", "FL" -> "305"
                    "WASHINGTON", "WA" -> "206"
                    "NEVADA", "NV" -> "702"
                    "ILLINOIS", "IL" -> "312"
                    else -> "415"
                }
                onDetected(matchedCode, "$city, $state")
                return
            }
        }
        onDetected("415", "San Francisco, CA")
    } catch (e: Exception) {
        onError()
    }
}
