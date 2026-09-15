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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppThemeScheme
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
 * Large circular Push-to-Talk button component with highly responsive touch gestures
 * and distinct visual feedback states:
 * 1. IDLE: Calm rhythmic ambient breathing aura, bezel compass ticks, tactical readiness.
 * 2. RECORDING: Multi-tiered expanding acoustic radar shockwaves, pulsing hot red core, live duration readout.
 * 3. ERROR: Warning hazard strobe, crimson alert halo, warning iconography, and tap-to-recover prompt.
 */
@Composable
fun HoldToTalkButton(
    pttState: PttState,
    transmitElapsedSec: Float,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    errorMessage: String? = null,
    onErrorClick: () -> Unit = {},
    themeScheme: AppThemeScheme = AppThemeScheme.TACTICAL_GREEN,
    modifier: Modifier = Modifier
) {
    PttTactileButton(
        pttState = pttState,
        transmitElapsedSec = transmitElapsedSec,
        onPress = onPress,
        onRelease = onRelease,
        errorMessage = errorMessage,
        onErrorClick = onErrorClick,
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
    errorMessage: String? = null,
    onErrorClick: () -> Unit = {},
    themeScheme: AppThemeScheme = AppThemeScheme.TACTICAL_GREEN,
    modifier: Modifier = Modifier
) {
    val isRecording = pttState == PttState.RECORDING || pttState == PttState.TRANSMITTING
    val isError = pttState == PttState.ERROR
    val isBusy = pttState == PttState.BUSY
    val isIncoming = pttState == PttState.INCOMING_TRANSMISSION
    val isIdle = pttState == PttState.IDLE

    var isPhysicallyPressed by remember { mutableStateOf(false) }

    // Physical depression scale feedback
    val buttonScale by animateFloatAsState(
        targetValue = when {
            isPhysicallyPressed || isRecording -> 0.94f
            isError -> 0.98f
            else -> 1.0f
        },
        animationSpec = tween(durationMillis = 100),
        label = "button_scale"
    )

    // Pulse animations engine
    val infiniteTransition = rememberInfiniteTransition(label = "ptt_pulse_engine")

    // Pulse Ring 1 (Primary shockwave)
    val pulseWave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when {
                    isError -> 450
                    isRecording -> 650
                    else -> 2400
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_wave_1"
    )

    // Pulse Ring 2 (Staggered secondary wave)
    val pulseWave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when {
                    isError -> 450
                    isRecording -> 650
                    else -> 2400
                },
                delayMillis = when {
                    isError -> 225
                    isRecording -> 325
                    else -> 1200
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_wave_2"
    )

    // Ambient breathing aura / glowing recording pulse
    val breathingAura by infiniteTransition.animateFloat(
        initialValue = if (isRecording || isError) 0.96f else 0.98f,
        targetValue = if (isRecording || isError) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isRecording || isError) 360 else 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_aura"
    )

    // Icon rhythmic pulse scale while recording or error
    val iconPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = when {
            isRecording -> 1.30f
            isError -> 1.18f
            else -> 1.0f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(if (isRecording || isError) 380 else 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_pulse_scale"
    )

    // Recording dot blink animation
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isError) 220 else 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    // Color derivation according to active state
    val customPrimary = Color(themeScheme.primaryHex)
    val customGlow = Color(themeScheme.glowHex)
    val customDark = Color(themeScheme.darkHex)

    val primaryColor = when {
        isBusy -> Color(0xFFFFB300)
        isError -> Color(0xFFEF4444)
        isRecording -> PttHotRed
        isIncoming -> TacticalCyan
        else -> customPrimary
    }

    val glowColor = when {
        isBusy -> Color(0xFFFFD54F)
        isError -> Color(0xFFF87171)
        isRecording -> PttRedGlow
        isIncoming -> TacticalCyanGlow
        else -> customGlow
    }

    val darkBaseColor = when {
        isBusy -> Color(0xFF5D4037)
        isError -> Color(0xFF450A0A)
        isRecording -> PttRedDark
        isIncoming -> Color(0xFF1F6FEB)
        else -> customDark
    }

    val stateTestTag = when {
        isBusy -> "ptt_state_busy"
        isError -> "ptt_state_error"
        isRecording -> "ptt_state_recording"
        isIncoming -> "ptt_state_incoming"
        else -> "ptt_state_idle"
    }

    Box(
        modifier = modifier
            .size(260.dp)
            .testTag("ptt_button_container")
            .testTag(stateTestTag),
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
            val alpha1 = (1f - pulseWave1).coerceIn(0f, 1f) * if (isRecording || isError) 0.92f else 0.4f
            drawCircle(
                color = primaryColor.copy(alpha = alpha1),
                radius = radius1,
                center = center,
                style = Stroke(width = if (isRecording || isError) 5.dp.toPx() else 2.dp.toPx())
            )

            // 2. Expanding Staggered Pulse Wave 2
            val radius2 = baseRadius + (maxExpandRadius - baseRadius) * pulseWave2
            val alpha2 = (1f - pulseWave2).coerceIn(0f, 1f) * if (isRecording || isError) 0.85f else 0.35f
            drawCircle(
                color = primaryColor.copy(alpha = alpha2),
                radius = radius2,
                center = center,
                style = Stroke(width = if (isRecording || isError) 4.dp.toPx() else 1.5.dp.toPx())
            )

            // 3. Ambient Breathing Halo
            val coronaRadius = (baseRadius + 8.dp.toPx()) * breathingAura
            drawCircle(
                color = glowColor.copy(alpha = if (isRecording || isError) 0.48f else 0.12f),
                radius = coronaRadius,
                center = center,
                style = Stroke(width = if (isRecording || isError) 10.dp.toPx() else 3.dp.toPx())
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
                    isError -> if (isMajorTick) 0.85f else 0.45f
                    isRecording -> if (isMajorTick) 0.8f else 0.4f
                    isMajorTick -> 0.5f
                    else -> 0.2f
                }

                drawLine(
                    color = when {
                        isError -> Color(0xFFEF4444).copy(alpha = tickAlpha)
                        isRecording -> PttHotRed.copy(alpha = tickAlpha)
                        else -> TacticalCardBorder.copy(alpha = tickAlpha)
                    },
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
                .border(
                    width = if (isError) 2.5.dp else 2.dp,
                    color = if (isError) Color(0xFFEF4444) else TacticalCardBorder,
                    shape = CircleShape
                )
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
                        elevation = when {
                            isRecording -> 22.dp
                            isError -> 16.dp
                            else -> 10.dp
                        },
                        shape = CircleShape,
                        spotColor = glowColor
                    )
                    .border(
                        width = when {
                            isRecording -> 3.dp
                            isError -> 3.dp
                            else -> 1.5.dp
                        },
                        color = when {
                            isError -> Color(0xFFFBBF24) // Warning Amber accent
                            isRecording -> PttRedGlow
                            else -> Color.White.copy(alpha = 0.25f)
                        },
                        shape = CircleShape
                    )
                    .then(
                        if (isError) {
                            Modifier.clickable {
                                onErrorClick()
                            }
                        } else {
                            Modifier.pointerInput(Unit) {
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
                        }
                    )
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
                            .background(
                                TacticalDarkBg.copy(
                                    alpha = when {
                                        isError -> 0.45f
                                        isRecording -> 0.35f
                                        else -> 0.25f
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isError -> Icons.Default.Warning
                                isRecording -> Icons.Default.Mic
                                isIncoming -> Icons.Default.VolumeUp
                                else -> Icons.Default.Radio
                            },
                            contentDescription = when {
                                isError -> "PTT Error: ${errorMessage ?: "Mic Access Required"}"
                                isRecording -> "Transmitting Audio Live"
                                else -> "Hold to Talk Microphone"
                            },
                            tint = when {
                                isError -> Color(0xFFFDE047) // Bright alert amber
                                else -> TacticalDarkBg
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .scale(iconPulseScale)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Primary State Label
                    when {
                        isBusy -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(TacticalDarkBg.copy(alpha = blinkAlpha))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PTT IN USE",
                                    color = TacticalDarkBg,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.0.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        isError -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFDE047).copy(alpha = blinkAlpha))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MIC RESTRICTED",
                                    color = TacticalDarkBg,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.0.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        isRecording -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(TacticalDarkBg.copy(alpha = blinkAlpha))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "RECORDING LIVE",
                                    color = TacticalDarkBg,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.2.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        isIncoming -> {
                            Text(
                                text = "RECEIVING LIVE",
                                color = TacticalDarkBg,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        else -> {
                            Text(
                                text = "HOLD TO TALK",
                                color = TacticalDarkBg,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Dynamic Sub-label / Live Readout
                    when {
                        isBusy -> {
                            Text(
                                text = "PLEASE HOLD WHILE IN USE",
                                color = TacticalDarkBg,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        isError -> {
                            Text(
                                text = errorMessage ?: "TAP TO PERMIT MIC",
                                color = TacticalDarkBg,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        isRecording -> {
                            Text(
                                text = "%02d:%04.1fs".format((transmitElapsedSec / 60).toInt(), transmitElapsedSec % 60),
                                color = TacticalDarkBg,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        isIncoming -> {
                            Text(
                                text = "AUDIO STREAM ACTIVE",
                                color = TacticalDarkBg.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        else -> {
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
}
