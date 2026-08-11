package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ComprehensivePlayerStats
import com.example.data.model.Player
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    viewModel: CricketViewModel
) {
    val players by viewModel.players.collectAsState()
    var selectedRoleTab by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPlayerForDetail by remember { mutableStateOf<Player?>(null) }
    var editingPlayer by remember { mutableStateOf<Player?>(null) }

    val roles = listOf("All", "Batsman", "Bowler", "All-rounder", "Wicketkeeper")

    val filteredPlayers = players.filter { player ->
        val matchesRole = if (selectedRoleTab == "All") true else player.role.equals(selectedRoleTab, ignoreCase = true)
        val matchesSearch = player.name.contains(searchQuery, ignoreCase = true) ||
                player.jerseyNumber.toString().contains(searchQuery)
        matchesRole && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CricketGreenDark,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Player")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CricketGreenDark,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text(
                        text = "Squad & Players",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Roster management, profiles & individual stats",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search player by name or jersey #...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Role Tabs
            ScrollableTabRow(
                selectedTabIndex = roles.indexOf(selectedRoleTab),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                roles.forEach { role ->
                    Tab(
                        selected = selectedRoleTab == role,
                        onClick = { selectedRoleTab = role },
                        text = {
                            Text(
                                text = role,
                                fontWeight = if (selectedRoleTab == role) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Player List
            if (filteredPlayers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No players found",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPlayers, key = { it.id }) { player ->
                        PlayerCardItem(
                            player = player,
                            onClick = { selectedPlayerForDetail = player },
                            onEdit = { editingPlayer = player },
                            onDelete = { viewModel.deletePlayer(player) }
                        )
                    }
                }
            }
        }
    }

    // Add Player Dialog
    if (showAddDialog) {
        PlayerFormDialog(
            player = null,
            onDismiss = { showAddDialog = false },
            onSave = { newPlayer ->
                viewModel.addPlayer(newPlayer)
                showAddDialog = false
            }
        )
    }

    // Edit Player Dialog
    if (editingPlayer != null) {
        PlayerFormDialog(
            player = editingPlayer,
            onDismiss = { editingPlayer = null },
            onSave = { updatedPlayer ->
                viewModel.updatePlayer(updatedPlayer)
                editingPlayer = null
            }
        )
    }

    // Player Profile Detail Dialog
    if (selectedPlayerForDetail != null) {
        PlayerDetailProfileModal(
            player = selectedPlayerForDetail!!,
            viewModel = viewModel,
            onDismiss = { selectedPlayerForDetail = null }
        )
    }
}

@Composable
fun PlayerCardItem(
    player: Player,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Jersey Number Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CricketGreenDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${player.jerseyNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${player.role} • ${player.battingStyle}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                if (player.phone.isNotEmpty()) {
                    Text(
                        text = "Phone: ${player.phone}",
                        style = MaterialTheme.typography.labelSmall.copy(color = CricketGreenPrimary)
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CricketGreenPrimary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PlayerFormDialog(
    player: Player?,
    onDismiss: () -> Unit,
    onSave: (Player) -> Unit
) {
    var name by remember { mutableStateOf(player?.name ?: "") }
    var jerseyNumber by remember { mutableStateOf(player?.jerseyNumber?.toString() ?: "10") }
    var role by remember { mutableStateOf(player?.role ?: "Batsman") }
    var battingStyle by remember { mutableStateOf(player?.battingStyle ?: "Right-hand bat") }
    var bowlingStyle by remember { mutableStateOf(player?.bowlingStyle ?: "Right-arm fast") }
    var phone by remember { mutableStateOf(player?.phone ?: "") }
    var joiningDate by remember { mutableStateOf(player?.joiningDate ?: "2026-01-01") }
    var notes by remember { mutableStateOf(player?.notes ?: "") }

    val roleOptions = listOf("Batsman", "Bowler", "All-rounder", "Wicketkeeper")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (player == null) "Add New Player" else "Edit Player") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Player Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = jerseyNumber,
                    onValueChange = { jerseyNumber = it },
                    label = { Text("Jersey Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Role Selector
                Text("Role", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    roleOptions.take(2).forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r) }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    roleOptions.drop(2).forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r) }
                        )
                    }
                }

                OutlinedTextField(
                    value = battingStyle,
                    onValueChange = { battingStyle = it },
                    label = { Text("Batting Style") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bowlingStyle,
                    onValueChange = { bowlingStyle = it },
                    label = { Text("Bowling Style") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        val newP = (player ?: Player(name = name, jerseyNumber = jerseyNumber.toIntOrNull() ?: 10, role = role)).copy(
                            name = name,
                            jerseyNumber = jerseyNumber.toIntOrNull() ?: 10,
                            role = role,
                            battingStyle = battingStyle,
                            bowlingStyle = bowlingStyle,
                            phone = phone,
                            joiningDate = joiningDate,
                            notes = notes
                        )
                        onSave(newP)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PlayerDetailProfileModal(
    player: Player,
    viewModel: CricketViewModel,
    onDismiss: () -> Unit
) {
    val statsState by viewModel.getPlayerStats(player.id).collectAsState(initial = null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CricketGreenDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text("#${player.jerseyNumber}", color = CricketGold, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(player.name, fontWeight = FontWeight.Bold)
                    Text(player.role, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        text = {
            val stats = statsState
            if (stats == null) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text("Batting Statistics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = CricketGreenPrimary)
                        Text("Matches: ${stats.batting.matches} | Runs: ${stats.batting.runs} | Highest: ${stats.batting.highestScore}")
                        Text("Avg: ${"%.2f".format(stats.batting.average)} | SR: ${"%.2f".format(stats.batting.strikeRate)}")
                        Text("4s: ${stats.batting.fours} | 6s: ${stats.batting.sixes} | 50s: ${stats.batting.fifties} | 100s: ${stats.batting.hundreds}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    }
                    item {
                        Text("Bowling Statistics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = CricketGreenPrimary)
                        Text("Overs: ${"%.1f".format(stats.bowling.overs)} | Wickets: ${stats.bowling.wickets}")
                        Text("Economy: ${"%.2f".format(stats.bowling.economy)} | Best: ${stats.bowling.bestBowling}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    }
                    item {
                        Text("Fielding Statistics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = CricketGreenPrimary)
                        Text("Catches: ${stats.fielding.catches} | Run Outs: ${stats.fielding.runOuts} | Stumpings: ${stats.fielding.stumpings}")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)) {
                Text("Close")
            }
        }
    )
}
