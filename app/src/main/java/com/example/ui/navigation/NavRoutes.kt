package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Outlined.Home, Icons.Filled.Home)
    object Matches : Screen("matches", "Matches", Icons.Outlined.SportsCricket, Icons.Filled.SportsCricket)
    object Players : Screen("players", "Players", Icons.Outlined.Groups, Icons.Filled.Groups)
    object Statistics : Screen("statistics", "Stats", Icons.Outlined.BarChart, Icons.Filled.BarChart)
    object More : Screen("more", "More", Icons.Outlined.GridView, Icons.Filled.GridView)

    // Sub screens
    object LiveScoring : Screen("live_scoring/{matchId}", "Live Scoring", Icons.Default.SportsCricket, Icons.Filled.SportsCricket)
    object Scorecard : Screen("scorecard/{matchId}", "Scorecard", Icons.Default.Receipt, Icons.Filled.Receipt)
    object Fixtures : Screen("fixtures", "Fixtures", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth)
    object News : Screen("news", "Team News", Icons.Outlined.Newspaper, Icons.Filled.Newspaper)
    object Expenses : Screen("expenses", "Expenses", Icons.Outlined.Payments, Icons.Filled.Payments)
    object Settings : Screen("settings", "Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
    object GlobalSearch : Screen("global_search", "Search", Icons.Outlined.Search, Icons.Filled.Search)

    // Version 3.0 Additions
    object About : Screen("about", "About & Creator", Icons.Outlined.Info, Icons.Filled.Info)
    object AiAssistant : Screen("ai_assistant", "AI Assistant", Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome)
    object Tournaments : Screen("tournaments", "Tournaments", Icons.Outlined.EmojiEvents, Icons.Filled.EmojiEvents)
    object PlayerComparison : Screen("player_comparison", "Player Compare", Icons.Outlined.Compare, Icons.Filled.Compare)
    object Opponents : Screen("opponents", "Opponents H2H", Icons.Outlined.Shield, Icons.Filled.Shield)
    object Records : Screen("records", "Records Center", Icons.Outlined.MilitaryTech, Icons.Filled.MilitaryTech)
}
