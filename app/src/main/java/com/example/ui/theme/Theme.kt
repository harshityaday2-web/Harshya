package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LunaDarkColorScheme = darkColorScheme(
    primary = LunaCyanPrimary,
    onPrimary = Color(0xFF00363F),
    primaryContainer = LunaCyanContainer,
    onPrimaryContainer = LunaOnCyanContainer,
    secondary = LunaPurpleSecondary,
    onSecondary = Color(0xFF280680),
    secondaryContainer = LunaPurpleContainer,
    onSecondaryContainer = LunaOnPurpleContainer,
    tertiary = LunaAmberTertiary,
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = LunaAmberContainer,
    background = LunaDarkBackground,
    onBackground = LunaTextPrimary,
    surface = LunaDarkSurface,
    onSurface = LunaTextPrimary,
    surfaceVariant = LunaDarkSurfaceVariant,
    onSurfaceVariant = LunaTextSecondary,
    outline = LunaDarkOutline
)

private val LunaLightColorScheme = lightColorScheme(
    primary = Color(0xFF006878),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9EEFFF),
    onPrimaryContainer = Color(0xFF001F25),
    secondary = Color(0xFF6750A4),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9DDFF),
    onSecondaryContainer = Color(0xFF22005D),
    tertiary = Color(0xFF7A5900),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDEA1),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to deep celestial theme for Luna Assistant
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> LunaDarkColorScheme
        else -> LunaLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
