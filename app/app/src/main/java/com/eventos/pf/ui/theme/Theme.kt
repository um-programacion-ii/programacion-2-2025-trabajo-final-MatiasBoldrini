package com.eventos.pf.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Emerald,
    onPrimary = ColorWhite,
    primaryContainer = Emerald.copy(alpha = 0.12f),
    onPrimaryContainer = EmeraldDark,
    secondary = Graphite,
    onSecondary = ColorWhite,
    background = Cloud,
    onBackground = Midnight,
    surface = ColorWhite,
    onSurface = Midnight,
    outline = Outline,
    error = Error,
    onError = ColorWhite
)

private val DarkColors = darkColorScheme(
    primary = Emerald,
    onPrimary = ColorBlack,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = ColorWhite,
    secondary = Graphite,
    onSecondary = ColorWhite,
    background = Midnight,
    onBackground = ColorWhite,
    surface = Graphite,
    onSurface = ColorWhite,
    outline = Outline,
    error = Error,
    onError = ColorWhite
)

@Composable
fun EventosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}

