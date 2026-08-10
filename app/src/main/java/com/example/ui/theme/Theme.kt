package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

private fun parseHexColor(hex: String?, defaultColor: Color): Color {
    if (hex.isNullOrEmpty()) return defaultColor
    return try {
        val cleaned = hex.removePrefix("#")
        val colorInt = if (cleaned.length == 6) {
            (0xFF000000 or cleaned.toLong(16)).toInt()
        } else {
            cleaned.toLong(16).toInt()
        }
        Color(colorInt)
    } catch (e: Exception) {
        defaultColor
    }
}

@Composable
fun CricketAppTheme(
    settings: ThemeSettings = ThemeSettings(),
    content: @Composable () -> Unit,
) {
    val isDark = settings.isDarkMode || settings.themeStyle in listOf(
        AppThemeStyle.LUXURY_DARK,
        AppThemeStyle.AMOLED_BLACK
    )

    val customPrimary = parseHexColor(settings.customPrimaryColorHex, CricketGreenDark)
    val customAccent = parseHexColor(settings.customAccentColorHex, CricketGold)

    val colorScheme = when (settings.themeStyle) {
        AppThemeStyle.STADIUM_GREEN -> if (isDark) {
            darkColorScheme(
                primary = customAccent,
                onPrimary = CricketGreenDark,
                primaryContainer = CricketGreenPrimary,
                onPrimaryContainer = Color.White,
                secondary = CricketGreenLight,
                background = SurfaceDark,
                surface = CardDark,
                onBackground = TextPrimaryDark,
                onSurface = TextPrimaryDark
            )
        } else {
            lightColorScheme(
                primary = customPrimary,
                onPrimary = Color.White,
                primaryContainer = CricketGreenContainer,
                onPrimaryContainer = CricketGreenDark,
                secondary = customAccent,
                background = SurfaceLight,
                surface = CardLight,
                onBackground = TextPrimaryLight,
                onSurface = TextPrimaryLight
            )
        }
        AppThemeStyle.LUXURY_DARK -> darkColorScheme(
            primary = Color(0xFFFFD700),
            onPrimary = Color(0xFF121212),
            primaryContainer = Color(0xFF2C2C2C),
            secondary = Color(0xFFFFC107),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onBackground = Color(0xFFEEEEEE),
            onSurface = Color(0xFFEEEEEE)
        )
        AppThemeStyle.AMOLED_BLACK -> darkColorScheme(
            primary = Color(0xFF00E676),
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF181818),
            secondary = Color(0xFF00B0FF),
            background = Color.Black,
            surface = Color(0xFF121212),
            onBackground = Color.White,
            onSurface = Color.White
        )
        AppThemeStyle.NAVY_BLUE -> lightColorScheme(
            primary = Color(0xFF1A365D),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE2E8F0),
            secondary = Color(0xFF3182CE),
            background = Color(0xFFF7FAFC),
            surface = Color.White,
            onBackground = Color(0xFF1A202C),
            onSurface = Color(0xFF1A202C)
        )
        AppThemeStyle.CRIMSON_RED -> lightColorScheme(
            primary = Color(0xFF9B111E),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFEBEE),
            secondary = Color(0xFFD32F2F),
            background = Color(0xFFFAFAFA),
            surface = Color.White,
            onBackground = Color(0xFF212121),
            onSurface = Color(0xFF212121)
        )
        AppThemeStyle.GOLD_CHAMPION -> lightColorScheme(
            primary = Color(0xFFB8860B),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFF8DC),
            secondary = Color(0xFFDAA520),
            background = Color(0xFFFCFBF7),
            surface = Color.White,
            onBackground = Color(0xFF2C2518),
            onSurface = Color(0xFF2C2518)
        )
        AppThemeStyle.CLASSIC_LIGHT -> lightColorScheme(
            primary = Color(0xFF2E7D32),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE8F5E9),
            secondary = Color(0xFF1565C0),
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            onBackground = Color(0xFF212121),
            onSurface = Color(0xFF212121)
        )
    }

    val selectedFontFamily = when (settings.fontFamily) {
        AppFontFamily.SERIF -> FontFamily.Serif
        AppFontFamily.MONOSPACE -> FontFamily.Monospace
        AppFontFamily.SANS_SERIF -> FontFamily.SansSerif
        AppFontFamily.DEFAULT -> FontFamily.Default
    }

    val baseTypography = Typography
    val customTypography = Typography(
        displayLarge = baseTypography.displayLarge.copy(fontFamily = selectedFontFamily),
        displayMedium = baseTypography.displayMedium.copy(fontFamily = selectedFontFamily),
        displaySmall = baseTypography.displaySmall.copy(fontFamily = selectedFontFamily),
        headlineLarge = baseTypography.headlineLarge.copy(fontFamily = selectedFontFamily),
        headlineMedium = baseTypography.headlineMedium.copy(fontFamily = selectedFontFamily),
        headlineSmall = baseTypography.headlineSmall.copy(fontFamily = selectedFontFamily),
        titleLarge = baseTypography.titleLarge.copy(fontFamily = selectedFontFamily),
        titleMedium = baseTypography.titleMedium.copy(fontFamily = selectedFontFamily),
        titleSmall = baseTypography.titleSmall.copy(fontFamily = selectedFontFamily),
        bodyLarge = baseTypography.bodyLarge.copy(fontFamily = selectedFontFamily),
        bodyMedium = baseTypography.bodyMedium.copy(fontFamily = selectedFontFamily),
        bodySmall = baseTypography.bodySmall.copy(fontFamily = selectedFontFamily),
        labelLarge = baseTypography.labelLarge.copy(fontFamily = selectedFontFamily),
        labelMedium = baseTypography.labelMedium.copy(fontFamily = selectedFontFamily),
        labelSmall = baseTypography.labelSmall.copy(fontFamily = selectedFontFamily)
    )

    MaterialTheme(colorScheme = colorScheme, typography = customTypography, content = content)
}


