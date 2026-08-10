package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: CricketViewModel,
    onNavigateToLiveScoring: (Long) -> Unit,
    onNavigateToScorecard: (Long) -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToFixtures: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onStartNewMatch: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToPlayerComparison: () -> Unit,
    onNavigateToRecords: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val players by viewModel.players.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val teamStats by viewModel.teamStats.collectAsState()

    val liveMatches = matches.filter { it.status == "LIVE" }
    val upcomingMatches = matches.filter { it.status == "UPCOMING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CricketBgLight),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Header
        item {
            DashboardHeader(
                teamName = "Alabbas Cricket",
                subtitle = "Mithial League Manager • By Zaryab Khan",
                matchesCount = teamStats.matchesPlayed,
                winsCount = teamStats.matchesWon,
                playersCount = players.size,
                onSearchClick = onNavigateToSearch
            )
        }

        // Creator Banner Card with Official Team Photo
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateToAbout() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketGreenDark),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cricket_team_hero_1786351528394),
                            contentDescription = "Team",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = CricketGold,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Created by Zaryab Khan",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "ALABBAS CRICKET MITHIAL",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            "Tap to view team history & app info",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        // Live / Upcoming Match Banner
        item {
            Spacer(modifier = Modifier.height(16.dp))
            if (liveMatches.isNotEmpty()) {
                val match = liveMatches.first()
                SectionHeader(
                    title = "Live Match",
                    actionText = "Scorecard",
                    onActionClick = { onNavigateToLiveScoring(match.id) }
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToLiveScoring(match.id) },
                    shape = RoundedCornerShape(20.dp),
                    color = CricketAccentRed,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "LIVE • ${match.matchType}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Button(
                                onClick = { onNavigateToLiveScoring(match.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("Score Live", color = CricketAccentRed, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${match.team1Name} vs ${match.team2Name}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Score: ${match.team1Score}/${match.team1Wickets} (${match.team1Overs} overs)",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f))
                        )
                    }
                }
            } else if (upcomingMatches.isNotEmpty()) {
                val upcoming = upcomingMatches.first()
                SectionHeader(
                    title = "Upcoming Fixture",
                    actionText = "View All",
                    onActionClick = onNavigateToFixtures
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CricketGreenContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = upcoming.team1Name.take(3).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CricketGreenDark
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = upcoming.team1Name,
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryLight),
                                maxLines = 1
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = CricketRedContainer
                            ) {
                                Text(
                                    text = upcoming.matchType,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CricketAccentRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondaryLight
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = upcoming.team2Name.take(3).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryLight
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = upcoming.team2Name,
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryLight),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Quick Tools & AI")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    label = "AI Assistant",
                    icon = Icons.Default.AutoAwesome,
                    backgroundColor = CricketGreenDark,
                    contentColor = Color.White,
                    onClick = onNavigateToAiAssistant,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "Tournaments",
                    icon = Icons.Default.EmojiEvents,
                    backgroundColor = CricketGreenContainer,
                    contentColor = CricketGreenDark,
                    onClick = onNavigateToTournaments,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "Compare",
                    icon = Icons.Default.Compare,
                    backgroundColor = CricketGoldLight,
                    contentColor = CricketGoldDark,
                    onClick = onNavigateToPlayerComparison,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "Records",
                    icon = Icons.Default.MilitaryTech,
                    backgroundColor = Color(0xFFF0F2F5),
                    contentColor = TextSecondaryLight,
                    onClick = onNavigateToRecords,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Team Overview Stats
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Team Performance")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PerformanceRow(label = "Matches Played", value = "${teamStats.matchesPlayed}")
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    PerformanceRow(label = "Matches Won", value = "${teamStats.matchesWon}")
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    PerformanceRow(label = "Total Runs Scored", value = "${teamStats.totalRuns}")
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    PerformanceRow(label = "Total Wickets Taken", value = "${teamStats.totalWickets}")
                }
            }
        }
    }
}

@Composable
private fun PerformanceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryLight))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )
        )
    }
}
