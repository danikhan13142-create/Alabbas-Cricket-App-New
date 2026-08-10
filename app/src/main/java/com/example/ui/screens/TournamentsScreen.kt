package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentsScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit
) {
    val tournaments by viewModel.tournaments.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tournaments & League Table", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Tournament", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CricketGreenDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CricketGreenDark,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Tournament")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CricketBgLight)
                .padding(16.dp)
        ) {
            if (tournaments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No active tournaments. Tap + to create one!", color = TextSecondaryLight)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(tournaments) { tournament ->
                        TournamentCard(tournament = tournament)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTournamentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, format, season ->
                viewModel.addTournament(
                    Tournament(
                        name = name,
                        format = format,
                        season = season,
                        teamsJson = "[\"Alabbas Cricket Mithial\", \"Shaheen Cricket Club\", \"Lions XI Chakwal\", \"Royal Strikers\"]",
                        startDate = "2026-08-10",
                        status = "ONGOING"
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TournamentCard(tournament: Tournament) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CricketGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tournament.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CricketGreenDark)
                }
                Surface(
                    color = CricketGreenContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = tournament.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Format: ${tournament.format} • Season ${tournament.season}", fontSize = 12.sp, color = TextSecondaryLight)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Points Table Preview
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Leaderboard, contentDescription = null, tint = CricketGreenDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Official Points Table", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryLight)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CricketGreenContainer.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Team", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                Text("P", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                Text("W", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                Text("L", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                Text("Pts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CricketGreenDark, modifier = Modifier.weight(0.8f))
            }

            // Standings Rows
            PointsRow("Alabbas Cricket Mithial", "2", "2", "0", "4", isMainTeam = true)
            PointsRow("Lions XI Chakwal", "2", "1", "1", "2")
            PointsRow("Shaheen Cricket Club", "2", "0", "2", "0")
            PointsRow("Royal Strikers Talagang", "0", "0", "0", "0")
        }
    }
}

@Composable
private fun PointsRow(team: String, p: String, w: String, l: String, pts: String, isMainTeam: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            team,
            fontSize = 12.sp,
            fontWeight = if (isMainTeam) FontWeight.Bold else FontWeight.Normal,
            color = if (isMainTeam) CricketGreenDark else TextPrimaryLight,
            modifier = Modifier.weight(2f)
        )
        Text(p, fontSize = 11.sp, modifier = Modifier.weight(0.6f))
        Text(w, fontSize = 11.sp, modifier = Modifier.weight(0.6f))
        Text(l, fontSize = 11.sp, modifier = Modifier.weight(0.6f))
        Text(pts, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CricketGreenDark, modifier = Modifier.weight(0.8f))
    }
}

@Composable
private fun AddTournamentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, format: String, season: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("T20") }
    var season by remember { mutableStateOf("2026") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Tournament") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tournament Name") })
                OutlinedTextField(value = format, onValueChange = { format = it }, label = { Text("Format (e.g. T20, 15 Overs)") })
                OutlinedTextField(value = season, onValueChange = { season = it }, label = { Text("Season") })
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, format, season) },
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
