package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color(0xFF0F172A),
    secondary = AmberAccent,
    onSecondary = Color(0xFF0F172A),
    tertiary = EmeraldSuccess,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    error = RoseError
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlueSecondary,
    onPrimary = Color.White,
    secondary = AmberAccent,
    onSecondary = Color.White,
    tertiary = EmeraldSuccess,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = RoseError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to deep slate dark theme for tactile puzzle contrast
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
