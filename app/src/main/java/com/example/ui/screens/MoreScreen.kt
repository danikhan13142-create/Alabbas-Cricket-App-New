package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MoreScreen(
    onNavigateToFixtures: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToPlayerComparison: () -> Unit,
    onNavigateToOpponents: () -> Unit,
    onNavigateToRecords: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CricketBgLight)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CricketGreenDark,
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = "More Options & Tools",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "AI Assistant, Tournaments, Head-to-Head & Creator Info",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            item {
                MoreMenuCard(
                    title = "AI Assistant",
                    subtitle = "Cricket Insights",
                    icon = Icons.Default.AutoAwesome,
                    color = CricketGreenDark,
                    onClick = onNavigateToAiAssistant
                )
            }
            item {
                MoreMenuCard(
                    title = "Tournaments",
                    subtitle = "Points Table & Format",
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFF1565C0),
                    onClick = onNavigateToTournaments
                )
            }
            item {
                MoreMenuCard(
                    title = "Player Compare",
                    subtitle = "H2H Player Stats",
                    icon = Icons.Default.Compare,
                    color = Color(0xFF6A1B9A),
                    onClick = onNavigateToPlayerComparison
                )
            }
            item {
                MoreMenuCard(
                    title = "Opponents DB",
                    subtitle = "Team H2H Records",
                    icon = Icons.Default.Shield,
                    color = Color(0xFF2E7D32),
                    onClick = onNavigateToOpponents
                )
            }
            item {
                MoreMenuCard(
                    title = "Records Center",
                    subtitle = "All-Time Achievements",
                    icon = Icons.Default.MilitaryTech,
                    color = Color(0xFFC62828),
                    onClick = onNavigateToRecords
                )
            }
            item {
                MoreMenuCard(
                    title = "About & Creator",
                    subtitle = "Zaryab Khan • Alabbas",
                    icon = Icons.Default.Info,
                    color = CricketGreenDark,
                    onClick = onNavigateToAbout
                )
            }
            item {
                MoreMenuCard(
                    title = "Fixtures & Schedule",
                    subtitle = "Upcoming Matches",
                    icon = Icons.Default.CalendarMonth,
                    color = Color(0xFF0277BD),
                    onClick = onNavigateToFixtures
                )
            }
            item {
                MoreMenuCard(
                    title = "Team News",
                    subtitle = "Announcements",
                    icon = Icons.Default.Newspaper,
                    color = Color(0xFF4A148C),
                    onClick = onNavigateToNews
                )
            }
            item {
                MoreMenuCard(
                    title = "Expenses (Rs)",
                    subtitle = "Budget & Ground Fees",
                    icon = Icons.Default.Payments,
                    color = Color(0xFFD84315),
                    onClick = onNavigateToExpenses
                )
            }
            item {
                MoreMenuCard(
                    title = "Settings & Backup",
                    subtitle = "Export / Restore JSON",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF37474F),
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
fun MoreMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            )
        }
    }
}
