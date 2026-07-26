package com.poetry.shijian.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MoonAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF0E6D3),
    onPrimaryContainer = Color(0xFF3D2E14),
    secondary = MoonTextSecondary,
    onSecondary = Color.White,
    background = MoonWhite,
    onBackground = MoonTextPrimary,
    surface = MoonSurface,
    onSurface = MoonTextPrimary,
    surfaceVariant = MoonSurfaceWhite,
    onSurfaceVariant = MoonTextSecondary,
    outline = MoonDivider,
    outlineVariant = MoonDivider,
)

private val DarkColorScheme = darkColorScheme(
    primary = InkAccent,
    onPrimary = Color(0xFF1A140E),
    primaryContainer = Color(0xFF3D2E14),
    onPrimaryContainer = Color(0xFFE0DDD5),
    secondary = InkTextSecondary,
    onSecondary = Color(0xFF1A1A1A),
    background = InkBackground,
    onBackground = InkTextPrimary,
    surface = InkSurface,
    onSurface = InkTextPrimary,
    surfaceVariant = InkSurfaceElevated,
    onSurfaceVariant = InkTextSecondary,
    outline = InkDivider,
    outlineVariant = InkDivider,
)

@Composable
fun ShijianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ShijianTypography,
        content = content,
    )
}
