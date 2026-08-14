package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Contact
import com.example.ui.WalkieTarget
import com.example.ui.components.EncryptionStatusBadge
import com.example.ui.theme.BurnerGold
import com.example.ui.theme.PttGreenGlow
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

@Composable
fun ContactsScreen(
    contacts: List<Contact>,
    activeTarget: WalkieTarget?,
    onSelectContact: (Contact) -> Unit,
    onOpenAddContact: () -> Unit,
    onOpenBurnerProvisioning: () -> Unit = {},
    onOpenSafetyKey: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredContacts = when (selectedFilter) {
        "VERIFIED" -> contacts.filter { it.isKeyVerified }
        "ONLINE" -> contacts.filter { it.isOnline }
        else -> contacts
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_online")
    val onlinePulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Header with title and Add Contact button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DIRECT 1-ON-1 WIRE",
                            color = TacticalTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Verified Users & Ephemeral Lines",
                            color = TacticalTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onOpenAddContact,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PttNeonGreen,
                            contentColor = TacticalDarkBg
                        ),
                        modifier = Modifier.testTag("add_contact_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ADD CONTACT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Menu Option Banner: Generate Temporary Burner (Firebase Functions Provisioning)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2E2305), TacticalSurface)
                            )
                        )
                        .border(1.dp, BurnerGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onOpenBurnerProvisioning() }
                        .testTag("generate_burner_menu_banner"),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(BurnerGold.copy(alpha = 0.2f))
                                    .border(1.dp, BurnerGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Burner Option",
                                    tint = BurnerGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PROVISION TEMPORARY BURNER",
                                    color = BurnerGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Firebase Functions anonymous provisioning",
                                    color = TacticalCyan,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BurnerGold)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "GENERATE",
                                color = TacticalDarkBg,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Tabs: All, Verified E2EE, Online
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        "ALL" to "ALL (${contacts.size})",
                        "VERIFIED" to "VERIFIED (${contacts.count { it.isKeyVerified }})",
                        "ONLINE" to "ONLINE (${contacts.count { it.isOnline }})"
                    )
                    items(filters) { (key, label) ->
                        val isSelected = selectedFilter == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PttNeonGreen.copy(alpha = 0.2f) else TacticalSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) PttNeonGreen else TacticalCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedFilter = key }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("filter_chip_$key")
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) PttGreenGlow else TacticalTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            if (filteredContacts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(TacticalSurface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No contacts match current filter",
                            color = TacticalTextMuted,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            items(filteredContacts) { contact ->
                val isSelected = activeTarget is WalkieTarget.ContactTarget && activeTarget.contact.id == contact.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (isSelected) PttNeonGreen else TacticalCardBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectContact(contact) }
                        .testTag("contact_card_${contact.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) TacticalSurfaceElevated else TacticalSurface
                    )
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
                            // Avatar with Online Status Indicator Badge
                            Box(
                                modifier = Modifier.size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (contact.isBurner) BurnerGold.copy(alpha = 0.2f)
                                            else TacticalSurfaceElevated
                                        )
                                        .border(
                                            1.dp,
                                            if (contact.isBurner) BurnerGold else PttNeonGreen,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (contact.isBurner) Icons.Default.Whatshot else Icons.Default.Person,
                                        contentDescription = "Avatar",
                                        tint = if (contact.isBurner) BurnerGold else PttNeonGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Online / Offline indicator dot on avatar corner
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(
                                            if (contact.isOnline) PttNeonGreen else TacticalTextMuted
                                        )
                                        .border(1.5.dp, TacticalDarkBg, CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = contact.name,
                                        color = TacticalTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (contact.isKeyVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Verified E2EE User",
                                            tint = PttNeonGreen,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "VERIFIED",
                                            color = PttNeonGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Text(
                                    text = "${contact.callsign} • ${contact.number}",
                                    color = if (contact.isBurner) BurnerGold else TacticalCyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    // Visual online/offline status indicator
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (contact.isOnline) PttNeonGreen else TacticalTextMuted
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (contact.isOnline) "ONLINE & READY" else "OFFLINE",
                                        color = if (contact.isOnline) PttGreenGlow else TacticalTextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Right action: E2EE Lock / Safety Key button
                        IconButton(
                            onClick = { onOpenSafetyKey(contact) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TacticalDarkBg)
                                .border(
                                    1.dp,
                                    if (contact.isKeyVerified) PttNeonGreen.copy(alpha = 0.5f) else TacticalCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .testTag("verify_key_btn_${contact.id}")
                        ) {
                            Icon(
                                imageVector = if (contact.isKeyVerified) Icons.Default.Lock else Icons.Default.Shield,
                                contentDescription = "Safety Key",
                                tint = if (contact.isKeyVerified) PttNeonGreen else TacticalAmber,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
