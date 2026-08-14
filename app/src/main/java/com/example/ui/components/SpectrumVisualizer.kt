package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttHotRed
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface

@Composable
fun SpectrumVisualizer(
    bars: List<Float>,
    isTransmitting: Boolean,
    isIncoming: Boolean,
    modifier: Modifier = Modifier
) {
    val activeColor = when {
        isTransmitting -> PttHotRed
        isIncoming -> TacticalCyan
        else -> PttNeonGreen
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(TacticalSurface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        // Grid lines overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepY = size.height / 3f
            drawLine(
                color = TacticalCardBorder.copy(alpha = 0.4f),
                start = Offset(0f, stepY),
                end = Offset(size.width, stepY),
                strokeWidth = 1f
            )
            drawLine(
                color = TacticalCardBorder.copy(alpha = 0.4f),
                start = Offset(0f, stepY * 2),
                end = Offset(size.width, stepY * 2),
                strokeWidth = 1f
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            bars.forEachIndexed { index, heightFraction ->
                val animatedHeight by animateFloatAsState(
                    targetValue = heightFraction.coerceIn(0.08f, 1f),
                    animationSpec = tween(durationMillis = 40),
                    label = "bar_$index"
                )

                val barBrush = Brush.verticalGradient(
                    colors = if (isTransmitting) {
                        listOf(PttHotRed, TacticalAmber, PttHotRed.copy(alpha = 0.5f))
                    } else if (isIncoming) {
                        listOf(TacticalCyan, PttGreenGlow, TacticalCyan.copy(alpha = 0.5f))
                    } else {
                        listOf(PttGreenGlow, PttNeonGreen, PttNeonGreen.copy(alpha = 0.4f))
                    }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(animatedHeight)
                        .clip(RoundedCornerShape(3.dp))
                        .background(barBrush)
                )
            }
        }
    }
}
