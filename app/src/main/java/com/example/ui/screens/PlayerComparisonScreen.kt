package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Player
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerComparisonScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit
) {
    val players by viewModel.players.collectAsState()

    var selectedPlayer1 by remember { mutableStateOf<Player?>(null) }
    var selectedPlayer2 by remember { mutableStateOf<Player?>(null) }

    LaunchedEffect(players) {
        if (players.size >= 2) {
            if (selectedPlayer1 == null) selectedPlayer1 = players[0]
            if (selectedPlayer2 == null) selectedPlayer2 = players[1]
        }
    }

    var expanded1 by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player Comparison", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CricketGreenDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CricketBgLight)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Selectors Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selector 1
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expanded1 = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(selectedPlayer1?.name ?: "Select Player 1", fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = expanded1, onDismissRequest = { expanded1 = false }) {
                        players.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    selectedPlayer1 = p
                                    expanded1 = false
                                }
                            )
                        }
                    }
                }

                Icon(
                    Icons.Default.Compare,
                    contentDescription = null,
                    tint = CricketGreenDark,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                // Selector 2
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expanded2 = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(selectedPlayer2?.name ?: "Select Player 2", fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                        players.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    selectedPlayer2 = p
                                    expanded2 = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val p1 = selectedPlayer1
            val p2 = selectedPlayer2

            if (p1 != null && p2 != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Head-to-Head Statistics",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CricketGreenDark
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ComparisonMetricRow("Role", p1.role, p2.role)
                        ComparisonMetricRow("Batting Style", p1.battingStyle, p2.battingStyle)
                        ComparisonMetricRow("Bowling Style", p1.bowlingStyle, p2.bowlingStyle)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Sample Stats Comparison
                        ComparisonMetricRow("Total Runs", "248", "215", highlightWinner = true)
                        ComparisonMetricRow("Batting Average", "41.3", "35.8", highlightWinner = true)
                        ComparisonMetricRow("Strike Rate", "174.6", "165.3", highlightWinner = true)
                        ComparisonMetricRow("Highest Score", "74", "62*", highlightWinner = true)
                        ComparisonMetricRow("Wickets Taken", "7", "3")
                        ComparisonMetricRow("Economy Rate", "6.8", "7.4")
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonMetricRow(
    label: String,
    val1: String,
    val2: String,
    highlightWinner: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = 12.sp, color = TextSecondaryLight, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                val1,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = CricketGreenDark,
                modifier = Modifier.weight(1f)
            )
            Text(
                val2,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = CricketGold,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(top = 4.dp))
    }
}
