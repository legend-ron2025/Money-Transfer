package com.moneytracker.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val DarkPrimary = Color(0xFF6C63FF)
private val DarkSecondary = Color(0xFF03DAC6)
private val DarkTertiary = Color(0xFF4CAF50)
private val DarkError = Color(0xFFCF6679)
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkOnPrimary = Color(0xFFFFFFFF)
private val DarkOnSecondary = Color(0xFF000000)
private val DarkOnBackground = Color(0xFFFFFFFF)
private val DarkOnSurface = Color(0xFFFFFFFF)

private val LightPrimary = Color(0xFF6C63FF)
private val LightSecondary = Color(0xFF006D6B)
private val LightTertiary = Color(0xFF2E7D32)
private val LightError = Color(0xFFB3261E)
private val LightBackground = Color(0xFFFFFBFE)
private val LightSurface = Color(0xFFFFFBFE)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightOnBackground = Color(0xFF1C1B1F)
private val LightOnSurface = Color(0xFF1C1B1F)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    error = DarkError,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnSecondary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    surfaceContainer = Color(0xFF2C2C2C),
    surfaceContainerHigh = Color(0xFF363636),
    surfaceContainerHighest = Color(0xFF404040),
    outline = Color(0xFF8A8A8A),
    outlineVariant = Color(0xFF6E6E6E),
    primaryContainer = Color(0xFF3E3A7E),
    secondaryContainer = Color(0xFF004F4D),
    tertiaryContainer = Color(0xFF1B5E20),
    errorContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFD0CDFF),
    onSecondaryContainer = Color(0xFFA7F6F3),
    onTertiaryContainer = Color(0xFFA5D6A7),
    onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF6C63FF),
    scrim = Color(0xFF000000),
    surfaceTint = DarkPrimary,
)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
    error = LightError,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnSecondary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    surfaceContainer = Color(0xFFF5F5F5),
    surfaceContainerHigh = Color(0xFFE8E8E8),
    surfaceContainerHighest = Color(0xFFDEDEDE),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFBDBDBD),
    primaryContainer = Color(0xFFE8E6FF),
    secondaryContainer = Color(0xFFCCF2F1),
    tertiaryContainer = Color(0xFFC8E6C9),
    errorContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF211D5E),
    onSecondaryContainer = Color(0xFF003836),
    onTertiaryContainer = Color(0xFF1B5E20),
    onErrorContainer = Color(0xFF410002),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFD0CDFF),
    scrim = Color(0xFF000000),
    surfaceTint = LightPrimary,
)