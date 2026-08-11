package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun StatisticsScreen(
    viewModel: CricketViewModel
) {
    val players by viewModel.players.collectAsState()
    var selectedCategoryTab by remember { mutableStateOf("Batting") }

    val categories = listOf("Batting", "Bowling", "Fielding", "Team Stats")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CricketGreenDark,
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = "Leaderboards & Stats",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Top runs, wickets, bowling figures & team breakdown",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }

        TabRow(
            selectedTabIndex = categories.indexOf(selectedCategoryTab),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = CricketGreenDark
        ) {
            categories.forEach { cat ->
                Tab(
                    selected = selectedCategoryTab == cat,
                    onClick = { selectedCategoryTab = cat },
                    text = {
                        Text(
                            cat,
                            fontWeight = if (selectedCategoryTab == cat) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedCategoryTab == cat) CricketGreenDark else TextSecondaryLight
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedCategoryTab) {
            "Batting" -> LeaderboardBattingList(viewModel, players.map { it.id })
            "Bowling" -> LeaderboardBowlingList(viewModel, players.map { it.id })
            "Fielding" -> LeaderboardFieldingList(viewModel, players.map { it.id })
            "Team Stats" -> TeamStatsOverview(viewModel)
        }
    }
}

@Composable
fun LeaderboardBattingList(viewModel: CricketViewModel, playerIds: List<Long>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(playerIds) { id ->
            val statsState by viewModel.getPlayerStats(id).collectAsState(initial = null)
            val stats = statsState
            if (stats != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stats.player.name, fontWeight = FontWeight.Bold)
                            Text("Runs: ${stats.batting.runs} | Avg: ${"%.1f".format(stats.batting.average)} | SR: ${"%.1f".format(stats.batting.strikeRate)}")
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CricketGreenPrimary
                        ) {
                            Text(
                                text = "${stats.batting.runs} Runs",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardBowlingList(viewModel: CricketViewModel, playerIds: List<Long>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(playerIds) { id ->
            val statsState by viewModel.getPlayerStats(id).collectAsState(initial = null)
            val stats = statsState
            if (stats != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stats.player.name, fontWeight = FontWeight.Bold)
                            Text("Wickets: ${stats.bowling.wickets} | Economy: ${"%.2f".format(stats.bowling.economy)}")
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CricketGold
                        ) {
                            Text(
                                text = "${stats.bowling.wickets} Wkts",
                                color = com.example.ui.theme.CricketGreenDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardFieldingList(viewModel: CricketViewModel, playerIds: List<Long>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(playerIds) { id ->
            val statsState by viewModel.getPlayerStats(id).collectAsState(initial = null)
            val stats = statsState
            if (stats != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stats.player.name, fontWeight = FontWeight.Bold)
                            Text("Catches: ${stats.fielding.catches} | Run Outs: ${stats.fielding.runOuts} | Stumpings: ${stats.fielding.stumpings}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamStatsOverview(viewModel: CricketViewModel) {
    val teamStats by viewModel.teamStats.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Alabbas Cricket Mithial Record", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider()
                    Text("Matches Played: ${teamStats.matchesPlayed}")
                    Text("Matches Won: ${teamStats.matchesWon}")
                    Text("Matches Lost: ${teamStats.matchesLost}")
                    Text("Win Percentage: ${"%.1f".format(teamStats.winPercentage)}%")
                    Text("Highest Team Score: ${teamStats.highestTeamScore}")
                    Text("Biggest Win: ${teamStats.biggestWin}")
                }
            }
        }
    }
}
