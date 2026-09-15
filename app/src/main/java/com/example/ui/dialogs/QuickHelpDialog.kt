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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

@Composable
fun QuickHelpDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, TacticalCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("quick_help_dialog"),
            colors = CardDefaults.cardColors(containerColor = TacticalSurface)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How to Use InstaWire",
                    color = TacticalTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Simple, instant two-way voice communication",
                    color = TacticalTextMuted,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                HelpStepItem(
                    stepNumber = "1",
                    icon = Icons.Default.Groups,
                    iconColor = TacticalCyan,
                    title = "Create or Join a Custom Channel",
                    description = "Create your own custom channel and frequency code to share with others, or enter a code to join."
                )

                Spacer(modifier = Modifier.height(14.dp))

                HelpStepItem(
                    stepNumber = "2",
                    icon = Icons.Default.Mic,
                    iconColor = PttNeonGreen,
                    title = "Hold to Talk or Use Volume Up",
                    description = "Press the screen PTT button or configure the physical Volume Up key (hold or toggle mode) to transmit."
                )

                Spacer(modifier = Modifier.height(14.dp))

                HelpStepItem(
                    stepNumber = "3",
                    icon = Icons.Default.VolumeUp,
                    iconColor = Color(0xFFF59E0B),
                    title = "Release to Listen",
                    description = "Release the button when finished speaking to hear teammates in real-time instantly!"
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalCyan, contentColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("help_got_it_button")
                ) {
                    Text(
                        text = "Got It, Let's Talk!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpStepItem(
    stepNumber: String,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TacticalSurfaceElevated)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f))
                .border(1.dp, iconColor.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$stepNumber. $title",
                color = TacticalTextPrimary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TacticalTextSecondary,
                fontSize = 11.5.sp,
                lineHeight = 16.sp
            )
        }
    }
}
