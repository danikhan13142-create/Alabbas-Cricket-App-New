package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.util.CricketOverUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CricketRepository(private val db: AppDatabase) {

    val players: Flow<List<Player>> = db.playerDao().getAllPlayers()
    val activePlayers: Flow<List<Player>> = db.playerDao().getActivePlayers()
    val matches: Flow<List<Match>> = db.matchDao().getAllMatches()
    val fixtures: Flow<List<Fixture>> = db.fixtureDao().getAllFixtures()
    val news: Flow<List<NewsItem>> = db.newsDao().getAllNews()
    val expenses: Flow<List<ExpenseItem>> = db.expenseDao().getAllExpenses()
    val tournaments: Flow<List<Tournament>> = db.tournamentDao().getAllTournaments()

    // Seed database if empty
    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        if (db.playerDao().getPlayerCount() == 0) {
            val initialPlayers = listOf(
                Player(name = "Abbas Mithial", jerseyNumber = 7, role = "All-rounder", battingStyle = "Right-hand bat", bowlingStyle = "Right-arm fast-medium", phone = "+92 300 1234567", joiningDate = "2023-01-15", notes = "Team Captain and Key All-rounder"),
                Player(name = "Zubair Ahmad", jerseyNumber = 18, role = "Batsman", battingStyle = "Right-hand bat", bowlingStyle = "Off Spin", phone = "+92 301 2345678", joiningDate = "2023-02-10", notes = "Opening batsman with high strike rate"),
                Player(name = "Hamza Ali", jerseyNumber = 10, role = "Bowler", battingStyle = "Right-hand bat", bowlingStyle = "Right-arm fast", phone = "+92 302 3456789", joiningDate = "2023-03-01", notes = "Strike fast bowler"),
                Player(name = "Usman Ghani", jerseyNumber = 1, role = "Wicketkeeper", battingStyle = "Left-hand bat", bowlingStyle = "N/A", phone = "+92 303 4567890", joiningDate = "2023-01-20", notes = "Primary Wicketkeeper and middle-order batter"),
                Player(name = "Bilal Hassan", jerseyNumber = 99, role = "All-rounder", battingStyle = "Right-hand bat", bowlingStyle = "Leg Spin", phone = "+92 304 5678901", joiningDate = "2023-04-12", notes = "Spin bowling all-rounder"),
                Player(name = "Shahzaib Khan", jerseyNumber = 45, role = "Batsman", battingStyle = "Right-hand bat", bowlingStyle = "Medium", phone = "+92 305 6789012", joiningDate = "2023-05-05", notes = "Middle-order anchor"),
                Player(name = "Saad Malik", jerseyNumber = 11, role = "Bowler", battingStyle = "Left-hand bat", bowlingStyle = "Left-arm fast", phone = "+92 306 7890123", joiningDate = "2023-06-18", notes = "Left-arm swing bowler"),
                Player(name = "Faizan Raza", jerseyNumber = 8, role = "All-rounder", battingStyle = "Right-hand bat", bowlingStyle = "Off Spin", phone = "+92 307 8901234", joiningDate = "2023-07-22", notes = "Power hitter in death overs")
            )
            initialPlayers.forEach { db.playerDao().insertPlayer(it) }

            // Seed initial completed matches
            val match1 = Match(
                teamName = "Alabbas Cricket Mithial",
                opponent = "Shaheen Cricket Club",
                date = "2026-07-28",
                venue = "Mithial Sports Stadium",
                matchType = "T20",
                totalOvers = 20,
                tossWinner = "Alabbas Cricket Mithial",
                tossDecision = "Bat",
                status = "COMPLETED",
                team1Name = "Alabbas Cricket Mithial",
                team1Score = 186,
                team1Wickets = 5,
                team1Overs = 20.0f,
                team2Name = "Shaheen Cricket Club",
                team2Score = 162,
                team2Wickets = 9,
                team2Overs = 20.0f,
                winner = "Alabbas Cricket Mithial",
                resultSummary = "Alabbas Cricket Mithial won by 24 runs",
                playerOfMatch = "Abbas Mithial",
                topScorer = "Zubair Ahmad (74 runs off 42 balls)",
                bestBowler = "Hamza Ali (4/28 in 4 overs)",
                highestPartnership = "98 runs (Zubair & Abbas)"
            )
            val m1Id = db.matchDao().insertMatch(match1)

            val match2 = Match(
                teamName = "Alabbas Cricket Mithial",
                opponent = "Lions XI Chakwal",
                date = "2026-08-02",
                venue = "Central Cricket Ground",
                matchType = "T20",
                totalOvers = 20,
                tossWinner = "Lions XI Chakwal",
                tossDecision = "Bat",
                status = "COMPLETED",
                team1Name = "Lions XI Chakwal",
                team1Score = 145,
                team1Wickets = 10,
                team1Overs = 18.3f,
                team2Name = "Alabbas Cricket Mithial",
                team2Score = 148,
                team2Wickets = 4,
                team2Overs = 16.2f,
                targetScore = 146,
                winner = "Alabbas Cricket Mithial",
                resultSummary = "Alabbas Cricket Mithial won by 6 wickets",
                playerOfMatch = "Abbas Mithial",
                topScorer = "Abbas Mithial (62* off 35 balls)",
                bestBowler = "Bilal Hassan (3/19 in 4 overs)",
                highestPartnership = "72 runs (Abbas & Shahzaib)"
            )
            val m2Id = db.matchDao().insertMatch(match2)

            // Add sample ball events for match 1 to make live scorecards detailed
            seedSampleBallEvents(m1Id, m2Id)

            // Seed initial fixtures
            db.fixtureDao().insertFixture(Fixture(opponent = "Royal Strikers Talagang", date = "2026-08-15", time = "16:00", venue = "Mithial Sports Stadium", matchType = "T20", overs = 20, notes = "League Quarter-Final"))
            db.fixtureDao().insertFixture(Fixture(opponent = "Falcons Cricket Club", date = "2026-08-22", time = "15:30", venue = "District Sports Complex", matchType = "15 Overs", overs = 15, notes = "Friendly Match"))

            // Seed initial news
            db.newsDao().insertNews(NewsItem(title = "Alabbas Cricket Mithial Wins District Championship Opener!", description = "A fantastic performance by Zubair Ahmad (74) and Hamza Ali (4/28) led our team to a convincing victory over Shaheen CC.", date = "2026-07-29", category = "Match Report"))
            db.newsDao().insertNews(NewsItem(title = "New Training Kit Sponsor Announced", description = "We are thrilled to announce local sponsorship for our official 2026-2027 season kit.", date = "2026-08-01", category = "Team Update"))

            // Seed initial expenses
            db.expenseDao().insertExpense(ExpenseItem(title = "New Leather Balls & Match Kit", amount = 14500.0, date = "2026-07-25", category = "Cricket equipment", notes = "3 Match balls + 2 practice balls"))
            db.expenseDao().insertExpense(ExpenseItem(title = "Ground Booking Fee for Tournament", amount = 8000.0, date = "2026-07-27", category = "Ground", notes = "Full day stadium rental"))
            db.expenseDao().insertExpense(ExpenseItem(title = "Team Refreshments & Transport", amount = 4200.0, date = "2026-08-02", category = "Food", notes = "Juices, fruit & van fuel"))

            // Seed initial tournament
            db.tournamentDao().insertTournament(
                Tournament(
                    name = "Attock Premier Cricket League 2026",
                    format = "T20",
                    season = "2026",
                    teamsJson = "[\"Alabbas Cricket Mithial\", \"Shaheen Cricket Club\", \"Lions XI Chakwal\", \"Royal Strikers Talagang\"]",
                    startDate = "2026-07-20",
                    endDate = "2026-08-30",
                    status = "ONGOING",
                    winnerTeam = ""
                )
            )
        }
    }

    private suspend fun seedSampleBallEvents(m1Id: Long, m2Id: Long) {
        val ballsM1 = listOf(
            BallEvent(matchId = m1Id, inningsIndex = 1, overNumber = 0, ballNumberInOver = 1, batterName = "Zubair Ahmad", bowlerName = "Rizwan Fast", runsScored = 1),
            BallEvent(matchId = m1Id, inningsIndex = 1, overNumber = 0, ballNumberInOver = 2, batterName = "Abbas Mithial", bowlerName = "Rizwan Fast", runsScored = 4),
            BallEvent(matchId = m1Id, inningsIndex = 1, overNumber = 0, ballNumberInOver = 3, batterName = "Abbas Mithial", bowlerName = "Rizwan Fast", runsScored = 0),
            BallEvent(matchId = m1Id, inningsIndex = 1, overNumber = 0, ballNumberInOver = 4, batterName = "Abbas Mithial", bowlerName = "Rizwan Fast", runsScored = 6),
            BallEvent(matchId = m1Id, inningsIndex = 1, overNumber = 0, ballNumberInOver = 5, batterName = "Abbas Mithial", bowlerName = "Rizwan Fast", runsScored = 2),
            BallEvent(matchId = m1Id, inningsIndex = 1, overNumber = 0, ballNumberInOver = 6, batterName = "Abbas Mithial", bowlerName = "Rizwan Fast", runsScored = 1)
        )
        ballsM1.forEach { db.ballEventDao().insertBallEvent(it) }
    }

    // CRUD Player
    suspend fun insertPlayer(player: Player) = db.playerDao().insertPlayer(player)
    suspend fun updatePlayer(player: Player) = db.playerDao().updatePlayer(player)
    suspend fun deletePlayer(player: Player) = db.playerDao().deletePlayer(player)
    suspend fun getPlayerById(id: Long) = db.playerDao().getPlayerById(id)

    // CRUD Match
    suspend fun insertMatch(match: Match) = db.matchDao().insertMatch(match)
    suspend fun updateMatch(match: Match) = db.matchDao().updateMatch(match)
    suspend fun deleteMatch(match: Match) = db.matchDao().deleteMatch(match)
    suspend fun deleteMatchWithEvents(match: Match) = withContext(Dispatchers.IO) {
        db.ballEventDao().deleteBallEventsForMatch(match.id)
        db.matchDao().deleteMatch(match)
    }
    suspend fun getMatchById(id: Long) = db.matchDao().getMatchById(id)
    fun getMatchByIdFlow(id: Long) = db.matchDao().getMatchByIdFlow(id)

    // Ball Events
    fun getBallEventsForInnings(matchId: Long, inningsIndex: Int) = db.ballEventDao().getBallEventsForInnings(matchId, inningsIndex)
    suspend fun getAllBallEventsForMatchList(matchId: Long) = db.ballEventDao().getBallEventsListForMatch(matchId)
    suspend fun insertBallEvent(ballEvent: BallEvent) = db.ballEventDao().insertBallEvent(ballEvent)
    suspend fun updateBallEvent(ballEvent: BallEvent) = db.ballEventDao().updateBallEvent(ballEvent)
    suspend fun deleteLastBallEvent(matchId: Long, inningsIndex: Int) = db.ballEventDao().deleteLastBallEvent(matchId, inningsIndex)

    // Recalculate full Match state from BallEvents (Single Source of Truth)
    suspend fun recalculateMatchFromBallEvents(matchId: Long) = withContext(Dispatchers.IO) {
        val currentMatch = db.matchDao().getMatchById(matchId) ?: return@withContext
        val allBalls = db.ballEventDao().getBallEventsListForMatch(matchId)

        // Innings 1 calculation
        val balls1 = allBalls.filter { it.inningsIndex == 1 }
        val score1 = balls1.sumOf { it.runsScored + it.extraRuns }
        val wickets1 = balls1.count { it.isWicket }
        val legalBalls1 = balls1.count { CricketOverUtils.isLegalDelivery(it.extraType) }
        val overs1Float = CricketOverUtils.legalBallsToOversFloat(legalBalls1)

        // Innings 2 calculation
        val balls2 = allBalls.filter { it.inningsIndex == 2 }
        val score2 = balls2.sumOf { it.runsScored + it.extraRuns }
        val wickets2 = balls2.count { it.isWicket }
        val legalBalls2 = balls2.count { CricketOverUtils.isLegalDelivery(it.extraType) }
        val overs2Float = CricketOverUtils.legalBallsToOversFloat(legalBalls2)

        val target = if (score1 > 0 || balls1.isNotEmpty()) score1 + 1 else currentMatch.targetScore

        var status = currentMatch.status
        var winner = currentMatch.winner
        var resultSummary = currentMatch.resultSummary

        val maxBalls = currentMatch.totalOvers * 6
        val maxWickets = 10

        if (currentMatch.currentInnings == 2 || score2 > 0 || balls2.isNotEmpty()) {
            if (score2 >= target) {
                status = "COMPLETED"
                winner = currentMatch.team2Name
                val wksLeft = maxWickets - wickets2
                val ballsLeft = maxBalls - legalBalls2
                resultSummary = "$winner won by $wksLeft wickets ($ballsLeft balls left)"
            } else if (wickets2 >= maxWickets || legalBalls2 >= maxBalls) {
                status = "COMPLETED"
                val diff = (target - 1) - score2
                if (diff > 0) {
                    winner = currentMatch.team1Name
                    resultSummary = "$winner won by $diff runs"
                } else if (diff == 0) {
                    winner = "Tie"
                    resultSummary = "Match Tied!"
                }
            }
        }

        val updatedMatch = currentMatch.copy(
            team1Score = score1,
            team1Wickets = wickets1,
            team1Overs = overs1Float,
            team2Score = score2,
            team2Wickets = wickets2,
            team2Overs = overs2Float,
            targetScore = target,
            status = status,
            winner = winner,
            resultSummary = resultSummary
        )
        db.matchDao().updateMatch(updatedMatch)
    }

    // CRUD Tournament
    suspend fun insertTournament(tournament: Tournament) = db.tournamentDao().insertTournament(tournament)
    suspend fun updateTournament(tournament: Tournament) = db.tournamentDao().updateTournament(tournament)
    suspend fun deleteTournament(tournament: Tournament) = db.tournamentDao().deleteTournament(tournament)
    suspend fun getTournamentById(id: Long) = db.tournamentDao().getTournamentById(id)

    // CRUD Fixture
    suspend fun insertFixture(fixture: Fixture) = db.fixtureDao().insertFixture(fixture)
    suspend fun updateFixture(fixture: Fixture) = db.fixtureDao().updateFixture(fixture)
    suspend fun deleteFixture(fixture: Fixture) = db.fixtureDao().deleteFixture(fixture)

    // CRUD News
    suspend fun insertNews(newsItem: NewsItem) = db.newsDao().insertNews(newsItem)
    suspend fun updateNews(newsItem: NewsItem) = db.newsDao().updateNews(newsItem)
    suspend fun deleteNews(newsItem: NewsItem) = db.newsDao().deleteNews(newsItem)

    // CRUD Expense
    suspend fun insertExpense(expenseItem: ExpenseItem) = db.expenseDao().insertExpense(expenseItem)
    suspend fun updateExpense(expenseItem: ExpenseItem) = db.expenseDao().updateExpense(expenseItem)
    suspend fun deleteExpense(expenseItem: ExpenseItem) = db.expenseDao().deleteExpense(expenseItem)

    // Calculate Comprehensive Player Stats strictly from database match records and ball events
    fun getPlayerStats(playerId: Long): Flow<ComprehensivePlayerStats?> {
        return players.map { playerList ->
            val player = playerList.find { it.id == playerId } ?: return@map null
            val allBalls = db.ballEventDao().getAllBallEvents()
            val completedMatchesList = db.matchDao().getAllMatches().first().filter { it.status == "COMPLETED" }

            // Filter ball events for this player
            val batterBalls = allBalls.filter { it.batterName.equals(player.name, ignoreCase = true) }
            val runs = batterBalls.sumOf { it.runsScored }
            val balls = batterBalls.count { it.extraType != "WIDE" }
            val fours = batterBalls.count { it.runsScored == 4 }
            val sixes = batterBalls.count { it.runsScored == 6 }

            // Calculate highest score from ball events grouped by match
            val matchScores = batterBalls.groupBy { it.matchId }.map { (_, events) -> events.sumOf { it.runsScored } }
            val highestScore = matchScores.maxOrNull() ?: 0
            val fifties = matchScores.count { it in 50..99 }
            val hundreds = matchScores.count { it >= 100 }

            // Bowling stats
            val bowlerBalls = allBalls.filter { it.bowlerName.equals(player.name, ignoreCase = true) }
            val wickets = bowlerBalls.count { it.isWicket && it.dismissalType != "Run Out" && it.dismissalType != "Retired" }
            val runsConceded = bowlerBalls.sumOf { it.runsScored + it.extraRuns }
            val legalBallsBowled = bowlerBalls.count { it.extraType != "WIDE" && it.extraType != "NO_BALL" }
            val oversBowled = (legalBallsBowled / 6) + ((legalBallsBowled % 6) / 10.0)

            // Best bowling calculation per match
            val bowlingByMatch = bowlerBalls.groupBy { it.matchId }.map { (_, events) ->
                val w = events.count { it.isWicket && it.dismissalType != "Run Out" && it.dismissalType != "Retired" }
                val r = events.sumOf { it.runsScored + it.extraRuns }
                Pair(w, r)
            }
            val bestBowlingPair = bowlingByMatch.maxByOrNull { it.first * 1000 - it.second }
            val bestBowlingStr = if (bestBowlingPair != null && bestBowlingPair.first > 0) "${bestBowlingPair.first}/${bestBowlingPair.second}" else if (wickets > 0) "$wickets/$runsConceded" else "0/0"
            val threeWickets = bowlingByMatch.count { it.first in 3..4 }
            val fiveWickets = bowlingByMatch.count { it.first >= 5 }

            // Fielding stats
            val catches = allBalls.count { it.isWicket && it.dismissalType == "Caught" && it.fielderName.equals(player.name, ignoreCase = true) }
            val runOuts = allBalls.count { it.isWicket && it.dismissalType == "Run Out" && it.fielderName.equals(player.name, ignoreCase = true) }
            val stumpings = allBalls.count { it.isWicket && it.dismissalType == "Stumped" && it.fielderName.equals(player.name, ignoreCase = true) }

            // Player match participation count from ball events or completed matches
            val matchesParticipated = batterBalls.map { it.matchId }.union(bowlerBalls.map { it.matchId }).size
            val inningsCount = batterBalls.map { it.matchId to it.inningsIndex }.distinct().size

            val strikeRate = if (balls > 0) (runs.toDouble() / balls) * 100 else 0.0
            val economy = if (legalBallsBowled > 0) (runsConceded.toDouble() / legalBallsBowled) * 6 else 0.0
            val battingAvg = if (inningsCount > 0) runs.toDouble() / inningsCount else 0.0
            val bowlingAvg = if (wickets > 0) runsConceded.toDouble() / wickets else 0.0

            ComprehensivePlayerStats(
                player = player,
                batting = PlayerBattingStats(
                    matches = matchesParticipated,
                    innings = inningsCount,
                    runs = runs,
                    balls = balls,
                    highestScore = highestScore,
                    fours = fours,
                    sixes = sixes,
                    fifties = fifties,
                    hundreds = hundreds,
                    notOuts = maxOf(0, matchesParticipated - inningsCount),
                    average = battingAvg,
                    strikeRate = strikeRate
                ),
                bowling = PlayerBowlingStats(
                    matches = matchesParticipated,
                    overs = oversBowled,
                    balls = legalBallsBowled,
                    runsConceded = runsConceded,
                    wickets = wickets,
                    bestBowling = bestBowlingStr,
                    economy = economy,
                    average = bowlingAvg,
                    threeWickets = threeWickets,
                    fiveWickets = fiveWickets
                ),
                fielding = PlayerFieldingStats(
                    catches = catches,
                    runOuts = runOuts,
                    stumpings = stumpings
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    // Team Overall Stats strictly derived from valid completed database matches
    fun getTeamStats(): Flow<TeamOverviewStats> {
        return matches.map { matchDetails ->
            val completed = matchDetails.filter { it.status == "COMPLETED" }
            if (completed.isEmpty()) {
                TeamOverviewStats(
                    matchesPlayed = 0,
                    matchesWon = 0,
                    matchesLost = 0,
                    ties = 0,
                    winPercentage = 0.0,
                    totalRuns = 0,
                    totalWickets = 0,
                    highestTeamScore = 0,
                    lowestTeamScore = 0,
                    bestRunChase = 0,
                    biggestWin = "—"
                )
            } else {
                val won = completed.count { it.winner.contains("Alabbas", ignoreCase = true) }
                val lost = completed.count { !it.winner.contains("Alabbas", ignoreCase = true) && !it.winner.contains("Tie", ignoreCase = true) && it.winner.isNotEmpty() }
                val ties = completed.count { it.winner.contains("Tie", ignoreCase = true) }
                val teamScores = completed.map { if (it.team1Name.contains("Alabbas", ignoreCase = true)) it.team1Score else it.team2Score }
                val teamWickets = completed.map { if (it.team1Name.contains("Alabbas", ignoreCase = true)) it.team1Wickets else it.team2Wickets }
                
                val totalRuns = teamScores.sum()
                val totalWickets = teamWickets.sum()
                val highest = teamScores.maxOrNull() ?: 0
                val lowest = teamScores.minOrNull() ?: 0

                val chasedMatches = completed.filter { it.winner.contains("Alabbas", ignoreCase = true) && it.team2Name.contains("Alabbas", ignoreCase = true) }
                val bestChase = chasedMatches.maxOfOrNull { it.team2Score } ?: 0

                val winningMatch = completed.find { it.winner.contains("Alabbas", ignoreCase = true) }
                val biggestWinStr = winningMatch?.resultSummary ?: "—"

                val winPct = (won.toDouble() / completed.size) * 100.0

                TeamOverviewStats(
                    matchesPlayed = completed.size,
                    matchesWon = won,
                    matchesLost = lost,
                    ties = ties,
                    winPercentage = winPct,
                    totalRuns = totalRuns,
                    totalWickets = totalWickets,
                    highestTeamScore = highest,
                    lowestTeamScore = lowest,
                    bestRunChase = bestChase,
                    biggestWin = biggestWinStr
                )
            }
        }
    }

    // Export JSON Backup
    suspend fun exportDataToJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        
        val playerList = db.playerDao().getAllPlayers().first()
        val playerArray = JSONArray()
        playerList.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("jerseyNumber", p.jerseyNumber)
            obj.put("role", p.role)
            obj.put("battingStyle", p.battingStyle)
            obj.put("bowlingStyle", p.bowlingStyle)
            obj.put("phone", p.phone)
            obj.put("joiningDate", p.joiningDate)
            obj.put("notes", p.notes)
            obj.put("isActive", p.isActive)
            playerArray.put(obj)
        }
        root.put("players", playerArray)

        val matchList = db.matchDao().getAllMatches().first()
        val matchArray = JSONArray()
        matchList.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("opponent", m.opponent)
            obj.put("date", m.date)
            obj.put("venue", m.venue)
            obj.put("matchType", m.matchType)
            obj.put("totalOvers", m.totalOvers)
            obj.put("status", m.status)
            obj.put("team1Score", m.team1Score)
            obj.put("team1Wickets", m.team1Wickets)
            obj.put("team2Score", m.team2Score)
            obj.put("team2Wickets", m.team2Wickets)
            obj.put("winner", m.winner)
            obj.put("resultSummary", m.resultSummary)
            matchArray.put(obj)
        }
        root.put("matches", matchArray)

        val expenseList = db.expenseDao().getAllExpenses().first()
        val expenseArray = JSONArray()
        expenseList.forEach { e ->
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("title", e.title)
            obj.put("amount", e.amount)
            obj.put("date", e.date)
            obj.put("category", e.category)
            obj.put("notes", e.notes)
            expenseArray.put(obj)
        }
        root.put("expenses", expenseArray)

        root.toString(2)
    }

    // Import JSON Backup
    suspend fun importDataFromJson(jsonStr: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonStr)
            if (root.has("players")) {
                val array = root.getJSONArray("players")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val p = Player(
                        name = obj.getString("name"),
                        jerseyNumber = obj.getInt("jerseyNumber"),
                        role = obj.getString("role"),
                        battingStyle = obj.optString("battingStyle", "Right-hand bat"),
                        bowlingStyle = obj.optString("bowlingStyle", "Right-arm fast"),
                        phone = obj.optString("phone", ""),
                        joiningDate = obj.optString("joiningDate", ""),
                        notes = obj.optString("notes", ""),
                        isActive = obj.optBoolean("isActive", true)
                    )
                    db.playerDao().insertPlayer(p)
                }
            }
            if (root.has("expenses")) {
                val array = root.getJSONArray("expenses")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val e = ExpenseItem(
                        title = obj.getString("title"),
                        amount = obj.getDouble("amount"),
                        date = obj.getString("date"),
                        category = obj.getString("category"),
                        notes = obj.optString("notes", "")
                    )
                    db.expenseDao().insertExpense(e)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
