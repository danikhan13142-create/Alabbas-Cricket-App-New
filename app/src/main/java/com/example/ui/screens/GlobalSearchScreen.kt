package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val players by viewModel.players.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val news by viewModel.news.collectAsState()
    val fixtures by viewModel.fixtures.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    val matchedPlayers = if (query.isEmpty()) emptyList() else players.filter { it.name.contains(query, ignoreCase = true) || it.role.contains(query, ignoreCase = true) }
    val matchedMatches = if (query.isEmpty()) emptyList() else matches.filter { it.opponent.contains(query, ignoreCase = true) || it.venue.contains(query, ignoreCase = true) }
    val matchedNews = if (query.isEmpty()) emptyList() else news.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    val matchedExpenses = if (query.isEmpty()) emptyList() else expenses.filter { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Search", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CricketGreenDark, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search players, opponents, news, expenses...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            )

            if (query.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type to search across Alabbas Cricket Mithial data")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (matchedPlayers.isNotEmpty()) {
                        item { Text("Players (${matchedPlayers.size})", fontWeight = FontWeight.Bold, color = CricketGreenPrimary) }
                        items(matchedPlayers) { p ->
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("${p.name} (#${p.jerseyNumber})", fontWeight = FontWeight.Bold)
                                    Text("${p.role} • ${p.battingStyle}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    if (matchedMatches.isNotEmpty()) {
                        item { Text("Matches (${matchedMatches.size})", fontWeight = FontWeight.Bold, color = CricketGreenPrimary) }
                        items(matchedMatches) { m ->
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Vs ${m.opponent}", fontWeight = FontWeight.Bold)
                                    Text("${m.date} at ${m.venue} • ${m.resultSummary}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    if (matchedNews.isNotEmpty()) {
                        item { Text("News (${matchedNews.size})", fontWeight = FontWeight.Bold, color = CricketGreenPrimary) }
                        items(matchedNews) { n ->
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(n.title, fontWeight = FontWeight.Bold)
                                    Text(n.description, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    if (matchedExpenses.isNotEmpty()) {
                        item { Text("Expenses (${matchedExpenses.size})", fontWeight = FontWeight.Bold, color = CricketGreenPrimary) }
                        items(matchedExpenses) { e ->
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(e.title, fontWeight = FontWeight.Bold)
                                    Text("Rs ${"%,.0f".format(e.amount)} • ${e.category}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
