package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BallEvent
import com.example.data.model.Match
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorecardScreen(
    matchId: Long,
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit
) {
    val matchFlow = viewModel.getMatchByIdFlow(matchId).collectAsState(initial = null)
    val match = matchFlow.value

    val ballsInnings1 by viewModel.getBallEventsForInnings(matchId, 1).collectAsState(initial = emptyList())
    val ballsInnings2 by viewModel.getBallEventsForInnings(matchId, 2).collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (match == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CricketGreenDark)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Scorecard & Charts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleLockMatch(match) }) {
                        Icon(
                            if (match.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock Match",
                            tint = if (match.isLocked) CricketAccentRed else Color.White
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Match", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CricketGreenDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CricketBgLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Match Header Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CricketGreenDark),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${match.matchType} • ${match.venue}", color = CricketGoldLight, style = MaterialTheme.typography.bodySmall)
                            if (match.isLocked) {
                                Surface(color = CricketAccentRed, shape = RoundedCornerShape(6.dp)) {
                                    Text("LOCKED", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${match.team1Name} vs ${match.team2Name}", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (match.resultSummary.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CricketGold
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CricketGreenDark, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = match.resultSummary, color = CricketGreenDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Manhattan Runs Per Over Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = CricketGreenDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Manhattan Graph (Runs per Over)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CricketGreenDark)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ManhattanChartCanvas(balls = ballsInnings1)
                    }
                }
            }

            // Worm Run Progression Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShowChart, contentDescription = null, tint = CricketGreenDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Worm Graph (Score Progression)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CricketGreenDark)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        WormChartCanvas(balls1 = ballsInnings1, balls2 = ballsInnings2)
                    }
                }
            }

            // Innings 1 Scorecard Table
            item {
                Text(
                    text = "1st Innings: ${match.team1Name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CricketGreenDark
                )
                ScorecardTableCard(
                    teamScore = match.team1Score,
                    wickets = match.team1Wickets,
                    overs = match.team1Overs,
                    balls = ballsInnings1
                )
            }

            // Innings 2 Scorecard Table if played
            if (match.team2Name.isNotEmpty()) {
                item {
                    Text(
                        text = "2nd Innings: ${match.team2Name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CricketGreenDark
                    )
                    ScorecardTableCard(
                        teamScore = match.team2Score,
                        wickets = match.team2Wickets,
                        overs = match.team2Overs,
                        balls = ballsInnings2
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        val isLive = match.status == "LIVE"
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    if (isLive) "⚠️ WARNING: Delete LIVE Match?" else "Delete Match Record?",
                    fontWeight = FontWeight.Bold,
                    color = CricketAccentRed
                )
            },
            text = {
                Text(
                    if (isLive) "This match is currently LIVE! Deleting it will purge all ball events and scores permanently."
                    else "Are you sure you want to delete this match record permanently?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMatch(match)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketAccentRed)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ScorecardTableCard(
    teamScore: Int,
    wickets: Int,
    overs: Float,
    balls: List<BallEvent>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Score:", fontWeight = FontWeight.Bold)
                Text("$teamScore/$wickets ($overs overs)", fontWeight = FontWeight.Bold, color = CricketGreenDark)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Batting Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Batter", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                Text("R", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                Text("B", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                Text("4s", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                Text("6s", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Group ball events by batter
            val batterGroups = balls.groupBy { it.batterName }
            if (batterGroups.isEmpty()) {
                Text("Scorecard records generated automatically during live play", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
            } else {
                batterGroups.forEach { (batterName, batterBalls) ->
                    val r = batterBalls.sumOf { it.runsScored }
                    val b = batterBalls.count { it.extraType != "WIDE" }
                    val fours = batterBalls.count { it.runsScored == 4 }
                    val sixes = batterBalls.count { it.runsScored == 6 }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(batterName, modifier = Modifier.weight(2f), maxLines = 1, fontWeight = FontWeight.Medium)
                        Text("$r", modifier = Modifier.weight(0.6f), fontWeight = FontWeight.Bold, color = CricketGreenDark)
                        Text("$b", modifier = Modifier.weight(0.6f))
                        Text("$fours", modifier = Modifier.weight(0.6f))
                        Text("$sixes", modifier = Modifier.weight(0.6f))
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Extras
            val wides = balls.count { it.extraType == "WIDE" }
            val noBalls = balls.count { it.extraType == "NO_BALL" }
            val byes = balls.filter { it.extraType == "BYE" }.sumOf { it.extraRuns }
            val legByes = balls.filter { it.extraType == "LEG_BYE" }.sumOf { it.extraRuns }
            val totalExtras = wides + noBalls + byes + legByes

            Text("Extras: $totalExtras (W: $wides, NB: $noBalls, B: $byes, LB: $legByes)", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
        }
    }
}

@Composable
fun ManhattanChartCanvas(balls: List<BallEvent>) {
    val overGrouped = balls.groupBy { it.overNumber }
    val maxOvers = maxOf(overGrouped.keys.maxOrNull() ?: 1, 10)
    val runsPerOver = (0..maxOvers).map { ov ->
        overGrouped[ov]?.sumOf { it.runsScored + it.extraRuns } ?: 0
    }
    val maxRunsInOver = maxOf(runsPerOver.maxOrNull() ?: 1, 12)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(8.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = width / (maxOvers + 1) * 0.7f
        val gap = width / (maxOvers + 1) * 0.3f

        for (i in 0..maxOvers) {
            val runs = runsPerOver.getOrElse(i) { 0 }
            val barHeight = (runs.toFloat() / maxRunsInOver) * height
            val x = i * (barWidth + gap)
            val y = height - barHeight

            drawRect(
                color = if (runs >= 10) CricketGold else CricketGreenDark,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
fun WormChartCanvas(balls1: List<BallEvent>, balls2: List<BallEvent>) {
    val overs1 = (balls1.maxOfOrNull { it.overNumber } ?: 1)
    val overs2 = (balls2.maxOfOrNull { it.overNumber } ?: 1)
    val maxOvers = maxOf(overs1, overs2, 10)

    val cumulative1 = mutableListOf<Float>()
    var sum1 = 0f
    for (i in 0..maxOvers) {
        val runs = balls1.filter { it.overNumber == i }.sumOf { it.runsScored + it.extraRuns }
        sum1 += runs
        cumulative1.add(sum1)
    }

    val cumulative2 = mutableListOf<Float>()
    var sum2 = 0f
    for (i in 0..maxOvers) {
        val runs = balls2.filter { it.overNumber == i }.sumOf { it.runsScored + it.extraRuns }
        sum2 += runs
        cumulative2.add(sum2)
    }

    val maxScore = maxOf(cumulative1.lastOrNull() ?: 1f, cumulative2.lastOrNull() ?: 1f, 100f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(8.dp)
    ) {
        val w = size.width
        val h = size.height

        // Draw Innings 1 Line (CricketGreenDark)
        val path1 = Path()
        for (i in 0..maxOvers) {
            val x = (i.toFloat() / maxOvers) * w
            val y = h - ((cumulative1.getOrElse(i) { 0f } / maxScore) * h)
            if (i == 0) path1.moveTo(x, y) else path1.lineTo(x, y)
        }
        drawPath(path = path1, color = CricketGreenDark, style = Stroke(width = 4f))

        // Draw Innings 2 Line (CricketGold) if balls exist
        if (balls2.isNotEmpty()) {
            val path2 = Path()
            for (i in 0..maxOvers) {
                val x = (i.toFloat() / maxOvers) * w
                val y = h - ((cumulative2.getOrElse(i) { 0f } / maxScore) * h)
                if (i == 0) path2.moveTo(x, y) else path2.lineTo(x, y)
            }
            drawPath(path = path2, color = CricketGold, style = Stroke(width = 4f))
        }
    }
}
