package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppThemeStyle(val displayName: String) {
    STADIUM_GREEN("Stadium Green"),
    LUXURY_DARK("Luxury Dark"),
    AMOLED_BLACK("AMOLED Black"),
    NAVY_BLUE("Navy Blue"),
    CRIMSON_RED("Crimson Red"),
    GOLD_CHAMPION("Gold Champion"),
    CLASSIC_LIGHT("Classic Light")
}

enum class AppFontFamily(val displayName: String) {
    DEFAULT("Default Sans"),
    SERIF("Serif Elegant"),
    MONOSPACE("Digital Monospace"),
    SANS_SERIF("Sans Serif Clean")
}

enum class AppFontScale(val displayName: String, val scale: Float) {
    SMALL("Small (90%)", 0.9f),
    MEDIUM("Medium (100%)", 1.0f),
    LARGE("Large (115%)", 1.15f),
    EXTRA_LARGE("Extra Large (125%)", 1.25f)
}

data class ThemeSettings(
    val themeStyle: AppThemeStyle = AppThemeStyle.STADIUM_GREEN,
    val customPrimaryColorHex: String? = null,
    val customAccentColorHex: String? = null,
    val fontFamily: AppFontFamily = AppFontFamily.DEFAULT,
    val fontScale: AppFontScale = AppFontScale.MEDIUM,
    val isDarkMode: Boolean = false
)

data class ScorecardCustomization(
    val style: String = "Professional", // "Classic", "Professional", "Compact", "Detailed", "Broadcast", "Minimal", "Live Score", "Ball-by-Ball"
    val showTeamLogo: Boolean = true,
    val showPlayerPhotos: Boolean = true,
    val showStrikeIndicator: Boolean = true,
    val showPartnerships: Boolean = true,
    val showFallOfWickets: Boolean = true,
    val showExtrasBreakdown: Boolean = true,
    val showRunRates: Boolean = true,
    val showTargetInfo: Boolean = true,
    val showBowlerStats: Boolean = true,
    val showBatterStats: Boolean = true,
    val showWagonWheel: Boolean = true,
    val showCommentary: Boolean = true,
    val showMatchResult: Boolean = true,
    val showPlayerOfMatch: Boolean = true
)
