package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * 'Sophisticated Dark' Color Palette
 * An elegant, modern dark theme utilizing deep obsidian / charcoal surfaces,
 * vibrant electric tactical accents, crisp contrast typography, and subtle borders.
 */
val SophisticatedDarkBackground = Color(0xFF0C1017)
val SophisticatedDarkSurface = Color(0xFF161B22)
val SophisticatedDarkSurfaceElevated = Color(0xFF21262D)
val SophisticatedDarkSurfaceHighest = Color(0xFF2D333B)
val SophisticatedDarkBorder = Color(0xFF30363D)
val SophisticatedDarkPrimary = Color(0xFF3FB950)       // Luminous Emerald Accent
val SophisticatedDarkOnPrimary = Color(0xFF071B0D)
val SophisticatedDarkPrimaryContainer = Color(0xFF0D381E)
val SophisticatedDarkOnPrimaryContainer = Color(0xFF56D364)
val SophisticatedDarkSecondary = Color(0xFF58A6FF)     // Tactical Electric Cyan
val SophisticatedDarkOnSecondary = Color(0xFF0B1B34)
val SophisticatedDarkSecondaryContainer = Color(0xFF1F6FEB)
val SophisticatedDarkOnSecondaryContainer = Color(0xFF79C0FF)
val SophisticatedDarkTertiary = Color(0xFFE3B341)      // Burner Gold / Amber
val SophisticatedDarkOnTertiary = Color(0xFF291B00)
val SophisticatedDarkError = Color(0xFFF85149)
val SophisticatedDarkOnError = Color(0xFF3D0606)
val SophisticatedDarkTextPrimary = Color(0xFFF0F6FC)
val SophisticatedDarkTextSecondary = Color(0xFF8B949E)
val SophisticatedDarkTextMuted = Color(0xFF6E7681)

/**
 * 'Sophisticated Dark' ColorScheme globally applied via Material3 darkColorScheme
 */
val SophisticatedDarkColorScheme = darkColorScheme(
    primary = SophisticatedDarkPrimary,
    onPrimary = SophisticatedDarkOnPrimary,
    primaryContainer = SophisticatedDarkPrimaryContainer,
    onPrimaryContainer = SophisticatedDarkOnPrimaryContainer,
    secondary = SophisticatedDarkSecondary,
    onSecondary = SophisticatedDarkOnSecondary,
    secondaryContainer = SophisticatedDarkSecondaryContainer,
    onSecondaryContainer = SophisticatedDarkOnSecondaryContainer,
    tertiary = SophisticatedDarkTertiary,
    onTertiary = SophisticatedDarkOnTertiary,
    background = SophisticatedDarkBackground,
    onBackground = SophisticatedDarkTextPrimary,
    surface = SophisticatedDarkSurface,
    onSurface = SophisticatedDarkTextPrimary,
    surfaceVariant = SophisticatedDarkSurfaceElevated,
    onSurfaceVariant = SophisticatedDarkTextSecondary,
    surfaceTint = SophisticatedDarkPrimary,
    inverseSurface = SophisticatedDarkTextPrimary,
    inverseOnSurface = SophisticatedDarkBackground,
    error = SophisticatedDarkError,
    onError = SophisticatedDarkOnError,
    outline = SophisticatedDarkBorder,
    outlineVariant = SophisticatedDarkSurfaceHighest
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
                window.statusBarColor = SophisticatedDarkBackground.toArgb()
                window.navigationBarColor = SophisticatedDarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = SophisticatedDarkColorScheme,
        typography = Typography,
        content = content
    )
}
