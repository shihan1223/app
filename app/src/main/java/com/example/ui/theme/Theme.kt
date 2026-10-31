package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DeepBurgundy,
    onPrimary = LightSurface,
    primaryContainer = DarkWineRed,
    onPrimaryContainer = SoftRose,
    secondary = RichCrimson,
    onSecondary = LightSurface,
    secondaryContainer = SoftRose,
    onSecondaryContainer = DeepBurgundy,
    tertiary = AccentRed,
    onTertiary = LightSurface,
    tertiaryContainer = RoseContainer,
    onTertiaryContainer = DarkWineRed,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceHigh,
    onSurfaceVariant = LightTextMuted,
    outline = BorderColor,
    error = CriticalRed,
    onError = LightSurface,
    errorContainer = CriticalRedContainer,
    onErrorContainer = CriticalRed
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccentPrimary,
    onPrimary = DarkBackground,
    primaryContainer = DeepBurgundy,
    onPrimaryContainer = SoftRose,
    secondary = DarkWineRed,
    onSecondary = DarkTextPrimary,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = AccentRed,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkSurfaceElevated,
    onTertiaryContainer = SoftRose,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextMuted,
    outline = DarkBorder,
    error = CriticalRed,
    onError = DarkTextPrimary,
    errorContainer = DarkSurfaceElevated,
    onErrorContainer = DarkAccentPrimary
)

@Composable
fun MediMealTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
