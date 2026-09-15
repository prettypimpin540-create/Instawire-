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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserIdentity
import com.example.ui.theme.BurnerGold
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
fun WalkieTopBar(
    userIdentity: UserIdentity,
    onOpenHelp: () -> Unit,
    onToggleMute: () -> Unit,
    isMuted: Boolean = false,
    onOpenWorldwideChatrooms: () -> Unit = {},
    onOpenMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentColor = Color(userIdentity.themeScheme.primaryHex)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TacticalDarkBg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity: Clean & Friendly
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("app_header")
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = "Walkie Talkie",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "InstaWire",
                        color = TacticalTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PttNeonGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PttNeonGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ONLINE",
                                color = PttNeonGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = "Encrypted Walkie Talkie",
                    color = TacticalTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Quick controls: Worldwide Rooms, Mute & Help
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = onOpenWorldwideChatrooms,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TacticalSurfaceElevated)
                    .border(1.dp, TacticalCardBorder, CircleShape)
                    .testTag("top_worldwide_rooms_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Worldwide Rooms",
                    tint = TacticalCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onToggleMute,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TacticalSurfaceElevated)
                    .border(1.dp, TacticalCardBorder, CircleShape)
                    .testTag("top_mute_button")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    tint = if (isMuted) Color(0xFFEF4444) else TacticalTextPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }

            IconButton(
                onClick = onOpenHelp,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TacticalSurfaceElevated)
                    .border(1.dp, TacticalCardBorder, CircleShape)
                    .testTag("top_help_button")
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "How to use",
                    tint = TacticalTextSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}
