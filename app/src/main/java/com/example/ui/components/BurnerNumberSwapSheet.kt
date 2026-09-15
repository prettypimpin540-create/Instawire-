package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.service.BurnerNumber
import com.example.service.BurnerNumberManager
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedDarkBorder
import com.example.ui.theme.SophisticatedDarkPrimary
import com.example.ui.theme.SophisticatedDarkSecondary
import com.example.ui.theme.SophisticatedDarkSurface
import com.example.ui.theme.SophisticatedDarkSurfaceElevated
import com.example.ui.theme.SophisticatedDarkTertiary
import com.example.ui.theme.SophisticatedDarkTextMuted
import com.example.ui.theme.SophisticatedDarkTextPrimary
import com.example.ui.theme.SophisticatedDarkTextSecondary

@Composable
fun BurnerNumberSwapDialog(
    burnerManager: BurnerNumberManager,
    onDismiss: () -> Unit
) {
    val burnerNumbers by burnerManager.burnerNumbers.collectAsState()
    val activeBurner by burnerManager.activeBurnerNumber.collectAsState()
    var showGenerateNewDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SophisticatedDarkSurface,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SophisticatedDarkBorder, RoundedCornerShape(16.dp))
                .testTag("burner_swap_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top header
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
                                .background(SophisticatedDarkTertiary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Swap Burner",
                                tint = SophisticatedDarkTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Burner Number Manager",
                                color = SophisticatedDarkTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Swap active temporary phone number",
                                color = SophisticatedDarkTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("burner_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SophisticatedDarkTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Burner banner
                activeBurner?.let { active ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SophisticatedDarkSurfaceElevated),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, SophisticatedDarkPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SophisticatedDarkPrimary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACTIVE CALLER ID",
                                    color = SophisticatedDarkPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = active.formattedDisplay,
                                    color = SophisticatedDarkTextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "${active.label} • ${active.cityRegion} (${active.remainingHours}h remaining)",
                                    color = SophisticatedDarkTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = SophisticatedDarkPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Available Burner numbers list
                Text(
                    text = "AVAILABLE NUMBERS (${burnerNumbers.size})",
                    color = SophisticatedDarkTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(burnerNumbers, key = { it.id }) { burner ->
                        val isCurrentActive = burner.isActive
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentActive) SophisticatedDarkSurfaceElevated else SophisticatedDarkBackground
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (isCurrentActive) SophisticatedDarkPrimary else SophisticatedDarkBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    if (!isCurrentActive) {
                                        burnerManager.swapActiveBurner(burner.phoneNumber)
                                    }
                                }
                                .testTag("burner_item_${burner.phoneNumber}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = if (isCurrentActive) SophisticatedDarkPrimary else SophisticatedDarkTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = burner.formattedDisplay,
                                        color = SophisticatedDarkTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${burner.label} • ${burner.cityRegion}",
                                        color = SophisticatedDarkTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                if (isCurrentActive) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SophisticatedDarkPrimary.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedDarkPrimary)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = SophisticatedDarkPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { burnerManager.swapActiveBurner(burner.phoneNumber) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SophisticatedDarkSecondary.copy(alpha = 0.2f),
                                            contentColor = SophisticatedDarkSecondary
                                        ),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("swap_button_${burner.phoneNumber}")
                                    ) {
                                        Text("Swap", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { burnerManager.removeBurner(burner.phoneNumber) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = SophisticatedDarkTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            burnerManager.generateBurnerNumber()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedDarkTertiary,
                            contentColor = SophisticatedDarkBackground
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("generate_burner_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Generate New Number", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
