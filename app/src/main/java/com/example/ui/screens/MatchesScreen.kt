package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    viewModel: CricketViewModel,
    onNavigateToLiveScoring: (Long) -> Unit,
    onNavigateToScorecard: (Long) -> Unit,
    onStartNewMatchRequested: Boolean = false
) {
    val matches by viewModel.matches.collectAsState()
    var selectedStatusTab by remember { mutableStateOf("All") }
    var showStartMatchDialog by remember { mutableStateOf(onStartNewMatchRequested) }
    var matchToDelete by remember { mutableStateOf<Match?>(null) }

    val tabs = listOf("All", "LIVE", "UPCOMING", "COMPLETED")

    val liveMatch = remember(matches) { matches.firstOrNull { it.status == "LIVE" } }

    val filteredMatches = matches.filter { match ->
        if (selectedStatusTab == "All") true else match.status.equals(selectedStatusTab, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showStartMatchDialog = true },
                containerColor = CricketGreenDark,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Match")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CricketBgLight)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CricketGreenDark,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text(
                        text = "Match Center",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Live scoring, upcoming fixtures & match archives",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            // Resume Live Match Banner if active match exists
            if (liveMatch != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable { onNavigateToLiveScoring(liveMatch.id) },
                    colors = CardDefaults.cardColors(containerColor = CricketAccentRed),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = Color.White, shape = RoundedCornerShape(6.dp)) {
                                    Text("LIVE NOW", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = CricketAccentRed, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(liveMatch.opponent, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Innings ${liveMatch.currentInnings} • ${if (liveMatch.currentInnings == 1) liveMatch.team1Score else liveMatch.team2Score}/${if (liveMatch.currentInnings == 1) liveMatch.team1Wickets else liveMatch.team2Wickets} in ${if (liveMatch.currentInnings == 1) liveMatch.team1Overs else liveMatch.team2Overs} ov",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = { onNavigateToLiveScoring(liveMatch.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = CricketAccentRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("RESUME", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Status Tabs
            TabRow(
                selectedTabIndex = tabs.indexOf(selectedStatusTab),
                containerColor = CricketBgLight,
                contentColor = CricketGreenDark
            ) {
                tabs.forEach { status ->
                    Tab(
                        selected = selectedStatusTab == status,
                        onClick = { selectedStatusTab = status },
                        text = {
                            Text(
                                text = status,
                                fontWeight = if (selectedStatusTab == status) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedStatusTab == status) CricketGreenDark else TextSecondaryLight
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredMatches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No matches found in this category", color = TextSecondaryLight)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMatches, key = { it.id }) { match ->
                        MatchListCard(
                            match = match,
                            onClick = {
                                if (match.status == "LIVE") {
                                    onNavigateToLiveScoring(match.id)
                                } else {
                                    onNavigateToScorecard(match.id)
                                }
                            },
                            onDelete = { matchToDelete = match },
                            onDuplicate = { viewModel.duplicateMatch(match) },
                            onToggleLock = { viewModel.toggleLockMatch(match) }
                        )
                    }
                }
            }
        }
    }

    if (showStartMatchDialog) {
        CreateMatchDialog(
            onDismiss = { showStartMatchDialog = false },
            onCreate = { opponent, venue, date, matchType, overs, tossWinner, tossDecision ->
                viewModel.createMatch(opponent, venue, date, matchType, overs, tossWinner, tossDecision)
                showStartMatchDialog = false
            }
        )
    }

    // Confirmation Dialog for Match Deletion
    if (matchToDelete != null) {
        val target = matchToDelete!!
        val isLive = target.status == "LIVE"

        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            title = {
                Text(
                    text = if (isLive) "⚠️ WARNING: Delete LIVE Match?" else "Delete Match Record?",
                    fontWeight = FontWeight.Bold,
                    color = CricketAccentRed
                )
            },
            text = {
                Text(
                    if (isLive)
                        "Match vs '${target.opponent}' is currently LIVE! Deleting it will permanently remove all live deliveries, scores, and statistics. Are you sure?"
                    else
                        "Are you sure you want to delete the match vs '${target.opponent}' (${target.date})? This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMatch(target)
                        matchToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketAccentRed)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { matchToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MatchListCard(
    match: Match,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onToggleLock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (match.status) {
                        "LIVE" -> CricketAccentRed
                        "COMPLETED" -> CricketGreenDark
                        else -> Color(0xFF1976D2)
                    }
                ) {
                    Text(
                        text = match.status,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${match.matchType} • ${match.venue}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryLight)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onToggleLock, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (match.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock",
                            tint = if (match.isLocked) CricketAccentRed else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onDuplicate, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CricketAccentRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = match.team1Name, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Text(text = "${match.team1Score}/${match.team1Wickets} (${match.team1Overs} ov)", color = TextSecondaryLight, fontSize = 13.sp)
                }
                Text("VS", color = CricketGold, fontWeight = FontWeight.Bold)
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = match.team2Name, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Text(text = "${match.team2Score}/${match.team2Wickets} (${match.team2Overs} ov)", color = TextSecondaryLight, fontSize = 13.sp)
                }
            }

            if (match.resultSummary.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = match.resultSummary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CricketGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun CreateMatchDialog(
    onDismiss: () -> Unit,
    onCreate: (opponent: String, venue: String, date: String, matchType: String, overs: Int, tossWinner: String, tossDecision: String) -> Unit
) {
    var opponent by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("Mithial Sports Stadium") }
    var date by remember { mutableStateOf("2026-08-10") }
    var matchType by remember { mutableStateOf("T20") }
    var oversText by remember { mutableStateOf("20") }
    var tossWinner by remember { mutableStateOf("Alabbas Cricket Mithial") }
    var tossDecision by remember { mutableStateOf("Bat") }

    val matchTypes = listOf("T20", "15 Overs", "10 Overs", "8 Overs", "6 Overs")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start New Match", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = "Alabbas Cricket Mithial",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Your Team") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("Opponent Team Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = oversText,
                    onValueChange = { oversText = it },
                    label = { Text("Number of Overs") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Match Type", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    matchTypes.take(3).forEach { t ->
                        FilterChip(
                            selected = matchType == t,
                            onClick = {
                                matchType = t
                                oversText = when (t) {
                                    "T20" -> "20"
                                    "15 Overs" -> "15"
                                    "10 Overs" -> "10"
                                    else -> "20"
                                }
                            },
                            label = { Text(t) }
                        )
                    }
                }

                Text("Toss Winner", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = tossWinner == "Alabbas Cricket Mithial",
                        onClick = { tossWinner = "Alabbas Cricket Mithial" },
                        label = { Text("Alabbas") }
                    )
                    FilterChip(
                        selected = tossWinner == opponent && opponent.isNotEmpty(),
                        onClick = { if (opponent.isNotEmpty()) tossWinner = opponent },
                        label = { Text(if (opponent.isEmpty()) "Opponent" else opponent) }
                    )
                }

                Text("Toss Decision", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = tossDecision == "Bat", onClick = { tossDecision = "Bat" }, label = { Text("Bat") })
                    FilterChip(selected = tossDecision == "Bowl", onClick = { tossDecision = "Bowl" }, label = { Text("Bowl") })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (opponent.isNotEmpty()) {
                        onCreate(opponent, venue, date, matchType, oversText.toIntOrNull() ?: 20, tossWinner, tossDecision)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
            ) {
                Text("Start Scoring")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
