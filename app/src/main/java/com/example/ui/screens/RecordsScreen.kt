package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Records Center", color = Color.White, fontWeight = FontWeight.Bold) },
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
            // Team Records Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CricketGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Team All-Time Records", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CricketGreenDark)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    RecordRow("Highest Team Total", "186/5 vs Shaheen CC", "20.0 Overs")
                    RecordRow("Lowest Defended Score", "148/4 vs Lions XI", "16.2 Overs")
                    RecordRow("Biggest Victory Margin", "24 Runs vs Shaheen CC", "District League")
                    RecordRow("Longest Winning Streak", "5 Matches", "2026 Season")
                    RecordRow("Highest Partnership", "98 Runs (Zubair & Abbas)", "1st Wicket")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Player Individual Records
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = CricketGreenDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Player Individual Records", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CricketGreenDark)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    RecordRow("Most Career Runs", "Zubair Ahmad", "248 Runs")
                    RecordRow("Highest Individual Score", "Zubair Ahmad", "74 off 42 balls")
                    RecordRow("Most Career Wickets", "Hamza Ali", "7 Wickets")
                    RecordRow("Best Bowling Figure", "Hamza Ali", "4/28 in 4.0 overs")
                    RecordRow("Fastest Half-Century", "Abbas Mithial", "50 off 22 balls")
                    RecordRow("Most Sixes Hit", "Abbas Mithial", "14 Sixes")
                    RecordRow("Most Catches Taken", "Usman Ghani", "6 Catches")
                }
            }
        }
    }
}

@Composable
private fun RecordRow(title: String, holder: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(title, fontSize = 12.sp, color = TextSecondaryLight)
            Text(holder, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryLight)
        }
        Surface(
            color = CricketGreenContainer,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = detail,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CricketGreenDark
            )
        }
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(top = 4.dp))
}
