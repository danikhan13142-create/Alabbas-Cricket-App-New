package com.example.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiService
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.CricketRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class CricketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CricketRepository(AppDatabase.getDatabase(application))
    private val geminiService = GeminiService()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val players = repository.players.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activePlayers = repository.activePlayers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val matches = repository.matches.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val fixtures = repository.fixtures.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val news = repository.news.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val expenses = repository.expenses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tournaments = repository.tournaments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val teamStats = repository.getTeamStats().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamOverviewStats())

    // Active Live Match Scoring State
    private val _activeMatchId = MutableStateFlow<Long?>(null)
    val activeMatchId = _activeMatchId.asStateFlow()

    private val _currentStriker = MutableStateFlow("Abbas Mithial")
    val currentStriker = _currentStriker.asStateFlow()

    private val _currentNonStriker = MutableStateFlow("Zubair Ahmad")
    val currentNonStriker = _currentNonStriker.asStateFlow()

    private val _currentBowler = MutableStateFlow("Hamza Ali")
    val currentBowler = _currentBowler.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Undone Ball Stack for Redo
    private val _undoneBallEvents = MutableStateFlow<List<BallEvent>>(emptyList())
    val undoneBallEvents = _undoneBallEvents.asStateFlow()

    // AI Chat State
    private val _aiChatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "AI",
                text = "Welcome to Alabbas Cricket Mithial AI Assistant! Created by Zaryab Khan. Ask me about player statistics, live match updates, playing XI suggestions, or team insights."
            )
        )
    )
    val aiChatMessages = _aiChatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveMatch(matchId: Long) {
        _activeMatchId.value = matchId
    }

    fun setStriker(name: String) {
        _currentStriker.value = name
    }

    fun setNonStriker(name: String) {
        _currentNonStriker.value = name
    }

    fun setBowler(name: String) {
        _currentBowler.value = name
    }

    // Player Actions
    fun addPlayer(player: Player) = viewModelScope.launch {
        repository.insertPlayer(player)
    }

    fun updatePlayer(player: Player) = viewModelScope.launch {
        repository.updatePlayer(player)
    }

    fun deletePlayer(player: Player) = viewModelScope.launch {
        repository.deletePlayer(player)
    }

    fun getPlayerStats(playerId: Long) = repository.getPlayerStats(playerId)

    fun getMatchByIdFlow(matchId: Long): Flow<Match?> = repository.getMatchByIdFlow(matchId)
    fun getBallEventsForInnings(matchId: Long, inningsIndex: Int): Flow<List<BallEvent>> = repository.getBallEventsForInnings(matchId, inningsIndex)

    // Match Actions
    fun createMatch(
        opponent: String,
        venue: String,
        date: String,
        matchType: String,
        overs: Int,
        tossWinner: String,
        tossDecision: String
    ) = viewModelScope.launch {
        val isAlabbasBattingFirst = (tossWinner == "Alabbas Cricket Mithial" && tossDecision == "Bat") ||
                (tossWinner != "Alabbas Cricket Mithial" && tossDecision == "Bowl")

        val match = Match(
            teamName = "Alabbas Cricket Mithial",
            opponent = opponent,
            date = date,
            venue = venue,
            matchType = matchType,
            totalOvers = overs,
            tossWinner = tossWinner,
            tossDecision = tossDecision,
            status = "LIVE",
            currentInnings = 1,
            team1Name = if (isAlabbasBattingFirst) "Alabbas Cricket Mithial" else opponent,
            team2Name = if (isAlabbasBattingFirst) opponent else "Alabbas Cricket Mithial"
        )
        val newId = repository.insertMatch(match)
        _activeMatchId.value = newId
    }

    fun deleteMatch(match: Match) = viewModelScope.launch {
        repository.deleteMatchWithEvents(match)
        if (_activeMatchId.value == match.id) {
            _activeMatchId.value = null
        }
    }

    fun duplicateMatch(match: Match) = viewModelScope.launch {
        val copy = match.copy(
            id = 0,
            date = "2026-08-10",
            status = "UPCOMING",
            team1Score = 0,
            team1Wickets = 0,
            team1Overs = 0.0f,
            team2Score = 0,
            team2Wickets = 0,
            team2Overs = 0.0f,
            winner = "",
            resultSummary = ""
        )
        repository.insertMatch(copy)
    }

    fun toggleLockMatch(match: Match) = viewModelScope.launch {
        val updated = match.copy(isLocked = !match.isLocked)
        repository.updateMatch(updated)
    }

    // Ball-by-Ball Scoring Engine
    fun recordBall(
        runs: Int,
        extraType: String = "NONE", // "NONE", "WIDE", "NO_BALL", "BYE", "LEG_BYE"
        isWicket: Boolean = false,
        dismissalType: String = "",
        dismissedBatter: String = "",
        newBatterName: String = "",
        shotDirection: String = ""
    ) = viewModelScope.launch {
        val mId = _activeMatchId.value ?: return@launch
        val currentMatch = repository.getMatchById(mId) ?: return@launch

        if (currentMatch.isLocked) return@launch

        // Clear redo stack on new ball
        _undoneBallEvents.value = emptyList()

        val isInnings1 = currentMatch.currentInnings == 1
        var currScore = if (isInnings1) currentMatch.team1Score else currentMatch.team2Score
        var currWickets = if (isInnings1) currentMatch.team1Wickets else currentMatch.team2Wickets
        var currOversFloat = if (isInnings1) currentMatch.team1Overs else currentMatch.team2Overs

        // Calculate current legal balls
        val fullOvers = currOversFloat.toInt()
        val ballsInCurrOver = ((currOversFloat - fullOvers) * 10).toInt()
        var totalLegalBalls = (fullOvers * 6) + ballsInCurrOver

        var extraRunsToAdd = 0
        var isLegalDelivery = true

        when (extraType) {
            "WIDE", "NO_BALL" -> {
                extraRunsToAdd = 1
                isLegalDelivery = false
            }
            "BYE", "LEG_BYE" -> {
                extraRunsToAdd = runs
                isLegalDelivery = true
            }
            else -> {
                isLegalDelivery = true
            }
        }

        val totalRunsThisBall = if (extraType == "BYE" || extraType == "LEG_BYE") extraRunsToAdd else (runs + extraRunsToAdd)
        currScore += totalRunsThisBall

        if (isWicket) {
            currWickets += 1
        }

        if (isLegalDelivery) {
            totalLegalBalls += 1
        }

        val newFullOvers = totalLegalBalls / 6
        val newBallsInOver = totalLegalBalls % 6
        val newOversFloat = newFullOvers + (newBallsInOver / 10.0f)

        // Record Ball Event
        val ballEvent = BallEvent(
            matchId = mId,
            inningsIndex = currentMatch.currentInnings,
            overNumber = newFullOvers,
            ballNumberInOver = newBallsInOver,
            batterName = _currentStriker.value,
            bowlerName = _currentBowler.value,
            runsScored = if (extraType == "BYE" || extraType == "LEG_BYE") 0 else runs,
            extraType = extraType,
            extraRuns = extraRunsToAdd,
            isWicket = isWicket,
            dismissalType = dismissalType,
            dismissedBatter = if (isWicket) (dismissedBatter.ifEmpty { _currentStriker.value }) else "",
            shotDirection = shotDirection
        )
        repository.insertBallEvent(ballEvent)

        // Rotate Strike
        val runsForStrikeSwap = if (extraType == "BYE" || extraType == "LEG_BYE") extraRunsToAdd else runs
        var shouldSwapStrike = (runsForStrikeSwap % 2 != 0)

        if (isWicket && newBatterName.isNotEmpty()) {
            _currentStriker.value = newBatterName
        }

        // End of over strike rotation
        if (isLegalDelivery && newBallsInOver == 0) {
            shouldSwapStrike = !shouldSwapStrike
        }

        if (shouldSwapStrike) {
            val temp = _currentStriker.value
            _currentStriker.value = _currentNonStriker.value
            _currentNonStriker.value = temp
        }

        // Update Match in DB directly
        val updatedMatch = if (isInnings1) {
            currentMatch.copy(
                team1Score = currScore,
                team1Wickets = currWickets,
                team1Overs = newOversFloat
            )
        } else {
            val target = currentMatch.team1Score + 1
            currentMatch.copy(
                team2Score = currScore,
                team2Wickets = currWickets,
                team2Overs = newOversFloat,
                targetScore = target
            )
        }
        repository.updateMatch(updatedMatch)
    }

    // MULTI-LEVEL UNDO (WITH SCORE SYNCHRONIZATION)
    fun undoLastBall() = viewModelScope.launch {
        val mId = _activeMatchId.value ?: return@launch
        val currentMatch = repository.getMatchById(mId) ?: return@launch
        if (currentMatch.isLocked) return@launch

        val allBalls = repository.getAllBallEventsForMatchList(mId)
            .filter { it.inningsIndex == currentMatch.currentInnings }

        if (allBalls.isNotEmpty()) {
            val lastBall = allBalls.last()
            // Push to undone stack for Redo
            _undoneBallEvents.value = _undoneBallEvents.value + lastBall

            // Delete last ball event from DB
            repository.deleteLastBallEvent(mId, currentMatch.currentInnings)

            // Recalculate full Match score, wickets, overs in DB
            repository.recalculateMatchFromBallEvents(mId)
        }
    }

    // REDO FUNCTIONALITY
    fun redoLastBall() = viewModelScope.launch {
        val mId = _activeMatchId.value ?: return@launch
        val currentMatch = repository.getMatchById(mId) ?: return@launch
        if (currentMatch.isLocked) return@launch

        val undoneList = _undoneBallEvents.value
        if (undoneList.isNotEmpty()) {
            val ballToRedo = undoneList.last()
            _undoneBallEvents.value = undoneList.dropLast(1)

            repository.insertBallEvent(ballToRedo)
            repository.recalculateMatchFromBallEvents(mId)
        }
    }

    // EDIT PREVIOUS DELIVERY
    fun editBallEvent(updatedBall: BallEvent) = viewModelScope.launch {
        val mId = updatedBall.matchId
        val currentMatch = repository.getMatchById(mId) ?: return@launch
        if (currentMatch.isLocked) return@launch

        repository.updateBallEvent(updatedBall)
        repository.recalculateMatchFromBallEvents(mId)
    }

    fun switchInnings() = viewModelScope.launch {
        val mId = _activeMatchId.value ?: return@launch
        val currentMatch = repository.getMatchById(mId) ?: return@launch
        val target = currentMatch.team1Score + 1
        val updated = currentMatch.copy(
            currentInnings = 2,
            targetScore = target
        )
        repository.updateMatch(updated)
    }

    fun finishMatch(winnerName: String, summary: String, mom: String = "") = viewModelScope.launch {
        val mId = _activeMatchId.value ?: return@launch
        val currentMatch = repository.getMatchById(mId) ?: return@launch
        val updated = currentMatch.copy(
            status = "COMPLETED",
            winner = winnerName,
            resultSummary = summary,
            playerOfMatch = mom.ifEmpty { "Abbas Mithial" }
        )
        repository.updateMatch(updated)
    }

    // Tournament Actions
    fun addTournament(tournament: Tournament) = viewModelScope.launch { repository.insertTournament(tournament) }
    fun updateTournament(tournament: Tournament) = viewModelScope.launch { repository.updateTournament(tournament) }
    fun deleteTournament(tournament: Tournament) = viewModelScope.launch { repository.deleteTournament(tournament) }

    // Fixture Actions
    fun addFixture(fixture: Fixture) = viewModelScope.launch { repository.insertFixture(fixture) }
    fun updateFixture(fixture: Fixture) = viewModelScope.launch { repository.updateFixture(fixture) }
    fun deleteFixture(fixture: Fixture) = viewModelScope.launch { repository.deleteFixture(fixture) }

    // News Actions
    fun addNews(newsItem: NewsItem) = viewModelScope.launch { repository.insertNews(newsItem) }
    fun updateNews(newsItem: NewsItem) = viewModelScope.launch { repository.updateNews(newsItem) }
    fun deleteNews(newsItem: NewsItem) = viewModelScope.launch { repository.deleteNews(newsItem) }

    // Expense Actions
    fun addExpense(expenseItem: ExpenseItem) = viewModelScope.launch { repository.insertExpense(expenseItem) }
    fun updateExpense(expenseItem: ExpenseItem) = viewModelScope.launch { repository.updateExpense(expenseItem) }
    fun deleteExpense(expenseItem: ExpenseItem) = viewModelScope.launch { repository.deleteExpense(expenseItem) }

    // AI Assistant Action
    fun askAiAssistant(userPrompt: String) = viewModelScope.launch {
        if (userPrompt.isBlank()) return@launch

        _aiChatMessages.value = _aiChatMessages.value + ChatMessage("USER", userPrompt)
        _isAiLoading.value = true

        val currentM = _activeMatchId.value?.let { repository.getMatchById(it) }
        val liveBalls = if (currentM != null) repository.getAllBallEventsForMatchList(currentM.id) else emptyList()

        val answer = geminiService.generateChatResponse(
            userPrompt = userPrompt,
            players = players.value,
            matches = matches.value,
            liveMatch = currentM,
            liveBalls = liveBalls,
            teamStats = teamStats.value,
            tournaments = tournaments.value
        )

        _isAiLoading.value = false
        _aiChatMessages.value = _aiChatMessages.value + ChatMessage("AI", answer)
    }

    // Backup & Restore
    suspend fun exportBackupJson(): String {
        return repository.exportDataToJson()
    }

    suspend fun importBackupJson(jsonStr: String): Boolean {
        return repository.importDataFromJson(jsonStr)
    }
}
