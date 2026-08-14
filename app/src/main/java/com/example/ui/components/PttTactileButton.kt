package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PttState
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttHotRed
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.PttRedDark
import com.example.ui.theme.PttRedGlow
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalCyanGlow
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Large circular Hold-to-Talk button component with multi-layered pulse wave animations.
 * Triggers audio recording on touch hold and stops on release.
 */
@Composable
fun HoldToTalkButton(
    pttState: PttState,
    transmitElapsedSec: Float,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    themeScheme: com.example.data.model.AppThemeScheme = com.example.data.model.AppThemeScheme.TACTICAL_GREEN,
    modifier: Modifier = Modifier
) {
    PttTactileButton(
        pttState = pttState,
        transmitElapsedSec = transmitElapsedSec,
        onPress = onPress,
        onRelease = onRelease,
        themeScheme = themeScheme,
        modifier = modifier
    )
}

@Composable
fun PttTactileButton(
    pttState: PttState,
    transmitElapsedSec: Float,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    themeScheme: com.example.data.model.AppThemeScheme = com.example.data.model.AppThemeScheme.TACTICAL_GREEN,
    modifier: Modifier = Modifier
) {
    val isTransmitting = pttState == PttState.TRANSMITTING
    val isIncoming = pttState == PttState.INCOMING_TRANSMISSION
    var isPhysicallyPressed by remember { mutableStateOf(false) }

    // Physical depression spring scale
    val buttonScale by animateFloatAsState(
        targetValue = if (isPhysicallyPressed || isTransmitting) 0.95f else 1.0f,
        animationSpec = tween(durationMillis = 120),
        label = "button_scale"
    )

    // Pulse animations for outer acoustic/radar waves
    val infiniteTransition = rememberInfiniteTransition(label = "ptt_pulse_engine")

    // Pulse Ring 1 (Primary high-energy wave)
    val pulseWave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isTransmitting) 900 else 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_wave_1"
    )

    // Pulse Ring 2 (Secondary staggered wave)
    val pulseWave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isTransmitting) 900 else 2400, delayMillis = if (isTransmitting) 450 else 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_wave_2"
    )

    // Ambient breathing aura
    val breathingAura by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isTransmitting) 500 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_aura"
    )

    val customPrimary = Color(themeScheme.primaryHex)
    val customGlow = Color(themeScheme.glowHex)
    val customDark = Color(themeScheme.darkHex)

    val primaryColor = when {
        isTransmitting -> PttHotRed
        isIncoming -> TacticalCyan
        else -> customPrimary
    }

    val glowColor = when {
        isTransmitting -> PttRedGlow
        isIncoming -> TacticalCyanGlow
        else -> customGlow
    }

    val darkBaseColor = when {
        isTransmitting -> PttRedDark
        isIncoming -> Color(0xFF1F6FEB)
        else -> customDark
    }

    Box(
        modifier = modifier
            .size(260.dp)
            .testTag("ptt_button_container"),
        contentAlignment = Alignment.Center
    ) {
        // Multi-stage visual pulse waves and tactical bezel markings
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("pulse_ring_canvas")
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = 100.dp.toPx()
            val maxExpandRadius = (size.minDimension / 2f) - 4.dp.toPx()

            // 1. Expanding Staggered Pulse Wave 1
            val radius1 = baseRadius + (maxExpandRadius - baseRadius) * pulseWave1
            val alpha1 = (1f - pulseWave1).coerceIn(0f, 1f) * if (isTransmitting) 0.85f else 0.4f
            drawCircle(
                color = primaryColor.copy(alpha = alpha1),
                radius = radius1,
                center = center,
                style = Stroke(width = if (isTransmitting) 4.dp.toPx() else 2.dp.toPx())
            )

            // 2. Expanding Staggered Pulse Wave 2
            val radius2 = baseRadius + (maxExpandRadius - baseRadius) * pulseWave2
            val alpha2 = (1f - pulseWave2).coerceIn(0f, 1f) * if (isTransmitting) 0.85f else 0.4f
            drawCircle(
                color = primaryColor.copy(alpha = alpha2),
                radius = radius2,
                center = center,
                style = Stroke(width = if (isTransmitting) 3.5.dp.toPx() else 1.5.dp.toPx())
            )

            // 3. Ambient Breathing Corona Halo
            val coronaRadius = (baseRadius + 8.dp.toPx()) * breathingAura
            drawCircle(
                color = glowColor.copy(alpha = if (isTransmitting) 0.35f else 0.12f),
                radius = coronaRadius,
                center = center,
                style = Stroke(width = if (isTransmitting) 8.dp.toPx() else 3.dp.toPx())
            )

            // 4. Tactical Dial Compass Tick Marks
            val tickCount = 24
            for (i in 0 until tickCount) {
                val angle = (i * (360f / tickCount)) * (PI / 180f)
                val isMajorTick = i % 6 == 0
                val tickLength = if (isMajorTick) 8.dp.toPx() else 4.dp.toPx()
                val tickInnerRadius = baseRadius + 12.dp.toPx()
                val tickOuterRadius = tickInnerRadius + tickLength

                val start = Offset(
                    x = center.x + (tickInnerRadius * cos(angle)).toFloat(),
                    y = center.y + (tickInnerRadius * sin(angle)).toFloat()
                )
                val end = Offset(
                    x = center.x + (tickOuterRadius * cos(angle)).toFloat(),
                    y = center.y + (tickOuterRadius * sin(angle)).toFloat()
                )

                val tickAlpha = when {
                    isTransmitting -> if (isMajorTick) 0.8f else 0.4f
                    isMajorTick -> 0.5f
                    else -> 0.2f
                }

                drawLine(
                    color = if (isTransmitting) PttHotRed.copy(alpha = tickAlpha) else TacticalCardBorder.copy(alpha = tickAlpha),
                    start = start,
                    end = end,
                    strokeWidth = if (isMajorTick) 2.dp.toPx() else 1.dp.toPx()
                )
            }
        }

        // Heavy chassis rim
        Box(
            modifier = Modifier
                .size(208.dp)
                .scale(buttonScale)
                .clip(CircleShape)
                .background(TacticalSurfaceElevated)
                .border(2.dp, TacticalCardBorder, CircleShape)
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            // Main tactile Hold-to-Talk button surface
            val buttonGradient = Brush.radialGradient(
                colors = listOf(
                    glowColor,
                    primaryColor,
                    darkBaseColor
                ),
                radius = 280f
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(buttonGradient)
                    .shadow(
                        elevation = if (isTransmitting) 20.dp else 10.dp,
                        shape = CircleShape,
                        spotColor = glowColor
                    )
                    .border(
                        width = if (isTransmitting) 3.dp else 1.5.dp,
                        color = if (isTransmitting) PttRedGlow else Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            isPhysicallyPressed = true
                            onPress()
                            try {
                                waitForUpOrCancellation()
                            } finally {
                                isPhysicallyPressed = false
                                onRelease()
                            }
                        }
                    }
                    .testTag("ptt_tactile_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(14.dp)
                ) {
                    // Center Mode Icon
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(TacticalDarkBg.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isTransmitting -> Icons.Default.Mic
                                isIncoming -> Icons.Default.VolumeUp
                                else -> Icons.Default.Radio
                            },
                            contentDescription = "Hold to Talk Microphone",
                            tint = TacticalDarkBg,
                            modifier = Modifier
                                .size(34.dp)
                                .scale(if (isTransmitting) 1.18f else 1.0f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Primary Action Label
                    Text(
                        text = when {
                            isTransmitting -> "RECORDING LIVE"
                            isIncoming -> "RECEIVING LIVE"
                            else -> "HOLD TO TALK"
                        },
                        color = TacticalDarkBg,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.2.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Dynamic readout or sub-label
                    if (isTransmitting) {
                        Text(
                            text = "%02d:%04.1fs".format((transmitElapsedSec / 60).toInt(), transmitElapsedSec % 60),
                            color = TacticalDarkBg,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else if (isIncoming) {
                        Text(
                            text = "AUDIO STREAM ACTIVE",
                            color = TacticalDarkBg.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(TacticalDarkBg.copy(alpha = 0.7f))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PRESS & SPEAK",
                                color = TacticalDarkBg.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

