package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BallEvent
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.components.BallBadge
import com.example.ui.theme.*
import com.example.util.CricketOverUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScoringScreen(
    matchId: Long,
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToScorecard: (Long) -> Unit
) {
    val matchFlow = viewModel.getMatchByIdFlow(matchId).collectAsState(initial = null)
    val match = matchFlow.value

    val striker by viewModel.currentStriker.collectAsState()
    val nonStriker by viewModel.currentNonStriker.collectAsState()
    val bowler by viewModel.currentBowler.collectAsState()
    val previousBowler by viewModel.previousBowler.collectAsState()
    val overCompletedEvent by viewModel.overCompletedEvent.collectAsState()
    val activePlayers by viewModel.activePlayers.collectAsState()
    val undoneBalls by viewModel.undoneBallEvents.collectAsState()

    var showWicketDialog by remember { mutableStateOf(false) }
    var showExtraDialog by remember { mutableStateOf<String?>(null) } // "WIDE", "NO_BALL", "BYE", "LEG_BYE"
    var showEndInningsDialog by remember { mutableStateOf(false) }
    var showSelectBowlerDialog by remember { mutableStateOf(false) }
    var showSelectBatsmanDialog by remember { mutableStateOf<String?>(null) } // "STRIKER", "NON_STRIKER"
    var editingBallEvent by remember { mutableStateOf<BallEvent?>(null) }
    var selectedShotDirection by remember { mutableStateOf("") }

    LaunchedEffect(overCompletedEvent) {
        if (overCompletedEvent != null) {
            showSelectBowlerDialog = true
            viewModel.resetOverCompletedEvent()
        }
    }

    if (match == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CricketGreenDark)
        }
        return
    }

    val isInnings1 = match.currentInnings == 1
    val currentTeamName = if (isInnings1) match.team1Name else match.team2Name
    val currentScore = if (isInnings1) match.team1Score else match.team2Score
    val currentWickets = if (isInnings1) match.team1Wickets else match.team2Wickets
    val currentOvers = if (isInnings1) match.team1Overs else match.team2Overs

    val ballEvents by viewModel.getBallEventsForInnings(matchId, match.currentInnings).collectAsState(initial = emptyList())

    val totalBallsLegal = CricketOverUtils.oversToLegalBalls(currentOvers)
    val currentRunRate = CricketOverUtils.calculateRunRate(currentScore, totalBallsLegal)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Scoring • ${match.matchType}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToScorecard(matchId) }) {
                        Icon(Icons.Default.Receipt, contentDescription = "View Scorecard")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CricketGreenDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Live Score Board Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CricketGreenDark),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentTeamName,
                            style = MaterialTheme.typography.titleMedium.copy(color = CricketGold, fontWeight = FontWeight.Bold)
                        )
                        if (match.isLocked) {
                            Surface(color = Color.Red, shape = RoundedCornerShape(8.dp)) {
                                Text("LOCKED", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "$currentScore/$currentWickets",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Overs: ${CricketOverUtils.formatOversFromFloat(currentOvers)} / ${match.totalOvers}",
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                            )
                            Text(
                                text = "CRR: ${"%.2f".format(currentRunRate)}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = CricketGoldLight)
                            )
                        }
                    }

                    if (!isInnings1) {
                        val runsNeeded = match.targetScore - currentScore
                        val ballsRemaining = (match.totalOvers * 6) - totalBallsLegal
                        val reqRate = if (ballsRemaining > 0) (runsNeeded.toDouble() / ballsRemaining) * 6 else 0.0

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Need $runsNeeded runs in $ballsRemaining balls • RRR: ${"%.2f".format(reqRate)}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = CricketGold, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Current Over Ball Log (Tap to Edit Ball Event)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("This Over: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (ballEvents.isEmpty()) {
                        Text("No deliveries yet", style = MaterialTheme.typography.bodySmall)
                    } else {
                        val recentBalls = ballEvents.takeLast(8)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(recentBalls) { b ->
                                val text = when {
                                    b.isWicket -> "W"
                                    b.extraType == "WIDE" -> "Wd"
                                    b.extraType == "NO_BALL" -> "Nb"
                                    b.extraType == "BYE" -> "${b.extraRuns}B"
                                    b.extraType == "LEG_BYE" -> "${b.extraRuns}LB"
                                    else -> "${b.runsScored}"
                                }
                                Box(modifier = Modifier.clickable { editingBallEvent = b }) {
                                    BallBadge(
                                        ballText = text,
                                        isWicket = b.isWicket,
                                        isBoundary = b.runsScored == 4 || b.runsScored == 6,
                                        isExtra = b.extraType != "NONE"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Striker, Non-Striker & Bowler Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Batters
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSelectBatsmanDialog = "STRIKER" },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏏 Striker: $striker *", fontWeight = FontWeight.Bold, color = CricketGreenDark)
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Change Striker")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSelectBatsmanDialog = "NON_STRIKER" },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏏 Non-Striker: $nonStriker", fontWeight = FontWeight.Normal)
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Change Non-Striker")
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Bowler
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSelectBowlerDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚡ Bowler: $bowler", fontWeight = FontWeight.Bold, color = CricketGreenDark)
                        Text("Change", style = MaterialTheme.typography.labelMedium, color = CricketGreenDark)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Ball Control Scoring Buttons Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Optional Shot Direction Selector (Wagon Wheel)
                Text("Shot Area (Wagon Wheel):", fontSize = 11.sp, color = TextSecondaryLight)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(listOf("Off", "Cover", "Point", "Third Man", "Fine Leg", "Midwicket", "Long On", "Long Off")) { direction ->
                        FilterChip(
                            selected = selectedShotDirection == direction,
                            onClick = {
                                selectedShotDirection = if (selectedShotDirection == direction) "" else direction
                            },
                            label = { Text(direction, fontSize = 10.sp) }
                        )
                    }
                }

                // Runs row 1: 0, 1, 2, 3
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0, 1, 2, 3).forEach { run ->
                        Button(
                            onClick = {
                                viewModel.recordBall(runs = run, shotDirection = selectedShotDirection)
                                selectedShotDirection = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                        ) {
                            Text("$run", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Runs row 2: 4, 5, 6, WICKET
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.recordBall(runs = 4, shotDirection = selectedShotDirection.ifEmpty { "Cover" })
                            selectedShotDirection = ""
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGold)
                    ) {
                        Text("4", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CricketGreenDark)
                    }
                    Button(
                        onClick = {
                            viewModel.recordBall(runs = 5, shotDirection = selectedShotDirection)
                            selectedShotDirection = ""
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                    ) {
                        Text("5", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            viewModel.recordBall(runs = 6, shotDirection = selectedShotDirection.ifEmpty { "Long On" })
                            selectedShotDirection = ""
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGold)
                    ) {
                        Text("6", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CricketGreenDark)
                    }
                    Button(
                        onClick = { showWicketDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketAccentRed)
                    ) {
                        Text("WICKET", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Extras Row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = { showExtraDialog = "WIDE" }, modifier = Modifier.weight(1f)) { Text("Wide") }
                    OutlinedButton(onClick = { showExtraDialog = "NO_BALL" }, modifier = Modifier.weight(1f)) { Text("No Ball") }
                    OutlinedButton(onClick = { showExtraDialog = "BYE" }, modifier = Modifier.weight(1f)) { Text("Bye") }
                    OutlinedButton(onClick = { showExtraDialog = "LEG_BYE" }, modifier = Modifier.weight(1f)) { Text("LegBye") }
                }

                // Undo, Redo, End Innings Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.undoLastBall() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Undo", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.redoLastBall() },
                        enabled = undoneBalls.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = "Redo", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Redo", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showEndInningsDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                    ) {
                        Text("End Innings", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Edit Ball Dialog
    if (editingBallEvent != null) {
        val ball = editingBallEvent!!
        var editRuns by remember { mutableStateOf(ball.runsScored.toString()) }
        var editExtraType by remember { mutableStateOf(ball.extraType) }
        var editIsWicket by remember { mutableStateOf(ball.isWicket) }

        AlertDialog(
            onDismissRequest = { editingBallEvent = null },
            title = { Text("Edit Delivery (Ball ${ball.overNumber}.${ball.ballNumberInOver})", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editRuns,
                        onValueChange = { editRuns = it },
                        label = { Text("Runs Scored") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = editIsWicket, onCheckedChange = { editIsWicket = it })
                        Text("Wicket on this delivery")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val runs = editRuns.toIntOrNull() ?: ball.runsScored
                        viewModel.editBallEvent(
                            ball.copy(
                                runsScored = runs,
                                extraType = editExtraType,
                                isWicket = editIsWicket
                            )
                        )
                        editingBallEvent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                ) {
                    Text("Save Correction")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBallEvent = null }) { Text("Cancel") }
            }
        )
    }

    // Wicket Dismissal Dialog
    if (showWicketDialog) {
        var dismissalType by remember { mutableStateOf("Bowled") }
        var newBatterName by remember { mutableStateOf("") }
        val dismissalTypes = listOf("Bowled", "Caught", "LBW", "Run Out", "Stumped", "Hit Wicket", "Retired", "Other")

        AlertDialog(
            onDismissRequest = { showWicketDialog = false },
            title = { Text("Record Wicket Dismissal", fontWeight = FontWeight.Bold, color = CricketAccentRed) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Dismissal Type:")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(dismissalTypes) { type ->
                            FilterChip(
                                selected = dismissalType == type,
                                onClick = { dismissalType = type },
                                label = { Text(type) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = newBatterName,
                        onValueChange = { newBatterName = it },
                        label = { Text("Next Batsman Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordBall(
                            runs = 0,
                            isWicket = true,
                            dismissalType = dismissalType,
                            dismissedBatter = striker,
                            newBatterName = newBatterName,
                            shotDirection = selectedShotDirection
                        )
                        if (newBatterName.isNotEmpty()) viewModel.setStriker(newBatterName)
                        showWicketDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketAccentRed)
                ) {
                    Text("Confirm Wicket")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWicketDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Extra Runs Dialog
    if (showExtraDialog != null) {
        val extraType = showExtraDialog!!
        var extraRunsText by remember { mutableStateOf("1") }

        AlertDialog(
            onDismissRequest = { showExtraDialog = null },
            title = { Text("Record Extra: $extraType", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = extraRunsText,
                    onValueChange = { extraRunsText = it },
                    label = { Text("Additional Runs (e.g. 1, 2, 4)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val runs = extraRunsText.toIntOrNull() ?: 1
                        viewModel.recordBall(runs = runs, extraType = extraType)
                        showExtraDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                ) {
                    Text("Add Extra")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExtraDialog = null }) { Text("Cancel") }
            }
        )
    }

    // Select Bowler Dialog
    if (showSelectBowlerDialog) {
        var newBowler by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSelectBowlerDialog = false },
            title = { Text("Select Bowler") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newBowler,
                        onValueChange = { newBowler = it },
                        label = { Text("Bowler Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Quick Pick:")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(activePlayers.filter { it.role == "Bowler" || it.role == "All-rounder" }) { p ->
                            SuggestionChip(onClick = { newBowler = p.name }, label = { Text(p.name) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBowler.isNotEmpty()) viewModel.setBowler(newBowler)
                        showSelectBowlerDialog = false
                    }
                ) { Text("Select") }
            }
        )
    }

    // Select Batter Dialog
    if (showSelectBatsmanDialog != null) {
        val target = showSelectBatsmanDialog!!
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSelectBatsmanDialog = null },
            title = { Text("Change $target") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Batter Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(activePlayers) { p ->
                            SuggestionChip(onClick = { name = p.name }, label = { Text(p.name) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotEmpty()) {
                            if (target == "STRIKER") viewModel.setStriker(name) else viewModel.setNonStriker(name)
                        }
                        showSelectBatsmanDialog = null
                    }
                ) { Text("Set Batter") }
            }
        )
    }

    // End Innings / Declare Winner Dialog
    if (showEndInningsDialog) {
        AlertDialog(
            onDismissRequest = { showEndInningsDialog = false },
            title = { Text("Innings Control") },
            text = {
                Text(
                    if (isInnings1) "Innings 1 complete! Ready to start Innings 2 chase?"
                    else "Match complete! Finish match and declare result?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isInnings1) {
                            viewModel.switchInnings()
                        } else {
                            val winner = if (match.team1Score > match.team2Score) match.team1Name else match.team2Name
                            val diff = kotlin.math.abs(match.team1Score - match.team2Score)
                            val summary = "$winner won by $diff runs!"
                            viewModel.finishMatch(winner, summary)
                            onNavigateToScorecard(matchId)
                        }
                        showEndInningsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                ) {
                    Text(if (isInnings1) "Start 2nd Innings" else "Declare Match Winner")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndInningsDialog = false }) { Text("Cancel") }
            }
        )
    }
}
