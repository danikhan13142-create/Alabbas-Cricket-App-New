package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val jerseyNumber: Int,
    val role: String, // "Batsman", "Bowler", "All-rounder", "Wicketkeeper"
    val battingStyle: String = "Right-hand bat", // "Right-hand bat", "Left-hand bat"
    val bowlingStyle: String = "Right-arm fast", // "Right-arm fast", "Right-arm medium", "Right-arm spin", "Left-arm fast", "Left-arm spin"
    val phone: String = "",
    val joiningDate: String = "",
    val notes: String = "",
    val photoUri: String = "",
    val isActive: Boolean = true
)

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teamName: String = "Alabbas Cricket Mithial",
    val opponent: String,
    val date: String,
    val venue: String,
    val matchType: String = "T20", // "T20", "15 Overs", "10 Overs", "8 Overs", "6 Overs", "Custom"
    val totalOvers: Int = 20,
    val tossWinner: String = "",
    val tossDecision: String = "", // "Bat", "Bowl"
    val status: String = "UPCOMING", // "UPCOMING", "LIVE", "COMPLETED"
    val currentInnings: Int = 1, // 1 or 2
    
    // Innings 1 (Alabbas or Opponent)
    val team1Name: String = "Alabbas Cricket Mithial",
    val team1Score: Int = 0,
    val team1Wickets: Int = 0,
    val team1Overs: Float = 0.0f, // e.g. 19.4 -> 19 overs 4 balls
    
    // Innings 2
    val team2Name: String = "",
    val team2Score: Int = 0,
    val team2Wickets: Int = 0,
    val team2Overs: Float = 0.0f,
    
    val targetScore: Int = 0,
    val winner: String = "",
    val resultSummary: String = "",
    val playerOfMatch: String = "",
    val topScorer: String = "",
    val bestBowler: String = "",
    val highestPartnership: String = "",
    val isLocked: Boolean = false,
    val tournamentId: Long = 0L,
    val notes: String = ""
)

@Entity(tableName = "ball_events")
data class BallEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchId: Long,
    val inningsIndex: Int, // 1 or 2
    val overNumber: Int,
    val ballNumberInOver: Int,
    val batterName: String,
    val bowlerName: String,
    val runsScored: Int,
    val extraType: String = "NONE", // "NONE", "WIDE", "NO_BALL", "BYE", "LEG_BYE"
    val extraRuns: Int = 0,
    val isWicket: Boolean = false,
    val dismissalType: String = "", // "Bowled", "Caught", "LBW", "Run Out", "Stumped", "Hit Wicket", "Retired", "Other"
    val dismissedBatter: String = "",
    val fielderName: String = "",
    val shotDirection: String = "", // "Off", "Cover", "Point", "Third Man", "Fine Leg", "Midwicket", "Long On", "Long Off"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val format: String = "T20",
    val season: String = "2026",
    val teamsJson: String = "", // JSON list of team names
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "ONGOING", // "UPCOMING", "ONGOING", "COMPLETED"
    val winnerTeam: String = ""
)

@Entity(tableName = "fixtures")
data class Fixture(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val opponent: String,
    val date: String,
    val time: String,
    val venue: String,
    val matchType: String,
    val overs: Int,
    val notes: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "news")
data class NewsItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val date: String,
    val category: String = "General"
)

@Entity(tableName = "expenses")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val date: String,
    val category: String, // "Ground", "Equipment", "Tournament Fee", "Transport", "Food", "Uniform", "Other"
    val notes: String = ""
)

data class PlayerBattingStats(
    val matches: Int = 0,
    val innings: Int = 0,
    val runs: Int = 0,
    val balls: Int = 0,
    val highestScore: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val fifties: Int = 0,
    val hundreds: Int = 0,
    val notOuts: Int = 0,
    val average: Double = 0.0,
    val strikeRate: Double = 0.0
)

data class PlayerBowlingStats(
    val matches: Int = 0,
    val overs: Double = 0.0,
    val balls: Int = 0,
    val runsConceded: Int = 0,
    val wickets: Int = 0,
    val bestBowling: String = "0/0",
    val economy: Double = 0.0,
    val average: Double = 0.0,
    val threeWickets: Int = 0,
    val fiveWickets: Int = 0
)

data class PlayerFieldingStats(
    val catches: Int = 0,
    val runOuts: Int = 0,
    val stumpings: Int = 0
)

data class ComprehensivePlayerStats(
    val player: Player,
    val batting: PlayerBattingStats,
    val bowling: PlayerBowlingStats,
    val fielding: PlayerFieldingStats
)

data class TeamOverviewStats(
    val matchesPlayed: Int = 0,
    val matchesWon: Int = 0,
    val matchesLost: Int = 0,
    val ties: Int = 0,
    val winPercentage: Double = 0.0,
    val totalRuns: Int = 0,
    val totalWickets: Int = 0,
    val highestTeamScore: Int = 0,
    val lowestTeamScore: Int = 0,
    val bestRunChase: Int = 0,
    val biggestWin: String = "N/A"
)
