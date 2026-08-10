package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Fixture
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.theme.CricketGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(
    viewModel: CricketViewModel
) {
    val fixtures by viewModel.fixtures.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CricketGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Fixture")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fixtures & Schedule",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            if (fixtures.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No fixtures scheduled")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(fixtures, key = { it.id }) { f ->
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
                                    Text("Vs ${f.opponent}", fontWeight = FontWeight.Bold)
                                    Text("${f.date} at ${f.time} • ${f.venue}", style = MaterialTheme.typography.bodySmall)
                                    Text("${f.matchType} (${f.overs} overs) - ${f.notes}", style = MaterialTheme.typography.labelSmall, color = CricketGreenPrimary)
                                }
                                IconButton(onClick = { viewModel.deleteFixture(f) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddFixtureDialog(
            onDismiss = { showAddDialog = false },
            onSave = { fixture ->
                viewModel.addFixture(fixture)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddFixtureDialog(
    onDismiss: () -> Unit,
    onSave: (Fixture) -> Unit
) {
    var opponent by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-08-20") }
    var time by remember { mutableStateOf("16:00") }
    var venue by remember { mutableStateOf("Mithial Sports Stadium") }
    var matchType by remember { mutableStateOf("T20") }
    var oversText by remember { mutableStateOf("20") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule New Fixture") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = opponent, onValueChange = { opponent = it }, label = { Text("Opponent") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (opponent.isNotEmpty()) {
                        onSave(Fixture(opponent = opponent, date = date, time = time, venue = venue, matchType = matchType, overs = oversText.toIntOrNull() ?: 20, notes = notes))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
            ) { Text("Save Fixture") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
