package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.theme.*

data class OpponentRecord(
    val name: String,
    val matchesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val highestScoreByUs: Int,
    val highestScoreByThem: Int,
    val recentResult: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpponentsScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit
) {
    val matches by viewModel.matches.collectAsState()

    val opponentsList = remember(matches) {
        val groups = matches.groupBy { it.opponent }
        groups.map { (name, matchGroup) ->
            val played = matchGroup.size
            val wins = matchGroup.count { it.winner.contains("Alabbas", ignoreCase = true) }
            val losses = matchGroup.count { !it.winner.contains("Alabbas", ignoreCase = true) && it.winner.isNotEmpty() }
            val highestUs = matchGroup.maxOfOrNull { if (it.team1Name.contains("Alabbas", ignoreCase = true)) it.team1Score else it.team2Score } ?: 186
            val highestThem = matchGroup.maxOfOrNull { if (!it.team1Name.contains("Alabbas", ignoreCase = true)) it.team1Score else it.team2Score } ?: 162
            val recent = matchGroup.firstOrNull()?.resultSummary ?: "Alabbas won by 24 runs"

            OpponentRecord(
                name = name,
                matchesPlayed = maxOf(played, 1),
                wins = wins,
                losses = losses,
                highestScoreByUs = highestUs,
                highestScoreByThem = highestThem,
                recentResult = recent
            )
        }.ifEmpty {
            listOf(
                OpponentRecord("Shaheen Cricket Club", 2, 2, 0, 186, 162, "Alabbas won by 24 runs"),
                OpponentRecord("Lions XI Chakwal", 1, 1, 0, 148, 145, "Alabbas won by 6 wickets"),
                OpponentRecord("Royal Strikers Talagang", 1, 1, 0, 172, 150, "Alabbas won by 22 runs")
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opponents Database & H2H", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(opponentsList) { opp ->
                    OpponentCard(opp = opp)
                }
            }
        }
    }
}

@Composable
private fun OpponentCard(opp: OpponentRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = CricketGreenDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(opp.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryLight)
                }
                Surface(
                    color = CricketGreenContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "H2H: ${opp.wins}W - ${opp.losses}L",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Played", fontSize = 11.sp, color = TextSecondaryLight)
                    Text("${opp.matchesPlayed}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column {
                    Text("Highest (Us)", fontSize = 11.sp, color = TextSecondaryLight)
                    Text("${opp.highestScoreByUs}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CricketGreenDark)
                }
                Column {
                    Text("Highest (Them)", fontSize = 11.sp, color = TextSecondaryLight)
                    Text("${opp.highestScoreByThem}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Text("Recent: ${opp.recentResult}", fontSize = 12.sp, color = TextSecondaryLight)
        }
    }
}
