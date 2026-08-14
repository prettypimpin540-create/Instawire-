package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PttNeonGreen,
    onPrimary = TacticalDarkBg,
    primaryContainer = PttGreenDark,
    onPrimaryContainer = PttGreenGlow,
    secondary = TacticalCyan,
    onSecondary = TacticalDarkBg,
    secondaryContainer = TacticalCyanDark,
    onSecondaryContainer = TacticalCyanGlow,
    tertiary = TacticalAmber,
    onTertiary = TacticalDarkBg,
    background = TacticalDarkBg,
    onBackground = TacticalTextPrimary,
    surface = TacticalSurface,
    onSurface = TacticalTextPrimary,
    surfaceVariant = TacticalSurfaceElevated,
    onSurfaceVariant = TacticalTextSecondary,
    error = PttHotRed,
    onError = TacticalTextPrimary,
    outline = TacticalCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // InstaWire is tactical dark by design
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = TacticalDarkBg.toArgb()
                window.navigationBarColor = TacticalDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
