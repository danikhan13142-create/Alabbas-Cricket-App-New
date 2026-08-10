package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = CricketGold,
    onPrimary = CricketGreenDark,
    primaryContainer = CricketGreenPrimary,
    onPrimaryContainer = Color.White,
    secondary = CricketGreenLight,
    onSecondary = Color.White,
    tertiary = CricketAccentRed,
    background = SurfaceDark,
    surface = CardDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF1B3D2B),
    onSurfaceVariant = TextSecondaryDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CricketGreenDark,
    onPrimary = Color.White,
    primaryContainer = CricketGreenContainer,
    onPrimaryContainer = CricketGreenDark,
    secondary = CricketGold,
    onSecondary = CricketGreenDark,
    secondaryContainer = CricketGoldLight,
    onSecondaryContainer = CricketGoldDark,
    tertiary = CricketAccentRed,
    background = SurfaceLight,
    surface = CardLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = TextSecondaryLight
  )

@Composable
fun CricketAppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

