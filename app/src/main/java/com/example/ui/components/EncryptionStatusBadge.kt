package com.example.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted

/**
 * Visual 'Encryption Status' badge displaying a green lock icon and E2EE status
 * when the current communication session uses end-to-end encryption.
 */
@Composable
fun EncryptionStatusBadge(
    isEncrypted: Boolean = true,
    isKeyVerified: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "e2ee_badge_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val badgeBorderColor = if (isEncrypted) {
        PttNeonGreen.copy(alpha = pulseAlpha)
    } else {
        TacticalCardBorder
    }

    val badgeIconTint = if (isEncrypted) {
        PttNeonGreen
    } else {
        TacticalAmber
    }

    val badgeTextColor = if (isEncrypted) {
        PttGreenGlow
    } else {
        TacticalTextMuted
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(TacticalSurfaceElevated)
            .border(1.dp, badgeBorderColor, RoundedCornerShape(20.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("encryption_status_badge"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Green Lock Icon
        Icon(
            imageVector = if (isEncrypted) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = if (isEncrypted) "End-to-End Encrypted Lock" else "Unencrypted Public Airwaves",
            tint = badgeIconTint,
            modifier = Modifier.size(13.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        // Status Indicator Dot
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isEncrypted) PttNeonGreen else TacticalAmber)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = if (isEncrypted) {
                if (isKeyVerified) "E2EE 256-BIT" else "E2EE ENCRYPTED"
            } else {
                "STANDARD FM"
            },
            color = badgeTextColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
