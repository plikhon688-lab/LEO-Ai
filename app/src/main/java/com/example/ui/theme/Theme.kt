package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NexaColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = VoidBlack,
    primaryContainer = Color(0xFF33206B),
    onPrimaryContainer = Color(0xFFEADBFF),
    secondary = ElectricCyan,
    onSecondary = VoidBlack,
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = Color(0xFFA6F5FF),
    tertiary = CyberPink,
    onTertiary = Color.White,
    background = VoidBlack,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF201B46),
    onSurfaceVariant = TextSecondary,
    outline = BorderGlow,
    outlineVariant = Color(0xFF262052)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark for futuristic anime aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NexaColorScheme,
        typography = Typography,
        content = content
    )
}
