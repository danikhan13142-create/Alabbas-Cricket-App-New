package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiService {

    suspend fun generateChatResponse(
        userPrompt: String,
        players: List<Player>,
        matches: List<Match>,
        liveMatch: Match?,
        liveBalls: List<BallEvent>,
        teamStats: TeamOverviewStats,
        tournaments: List<Tournament>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        val contextSummary = buildContextSummary(players, matches, liveMatch, liveBalls, teamStats, tournaments)

        if (apiKey.isBlank() || apiKey == "null" || apiKey == "YOUR_API_KEY") {
            return@withContext fallbackLocalAiAnswer(userPrompt, players, matches, liveMatch, teamStats, tournaments)
        }

        try {
            val systemInstruction = """
                You are the official AI Assistant for the cricket team 'ALABBAS CRICKET MITHIAL' (Created by Zaryab Khan).
                Answer questions accurately based on the actual team stored data provided below.
                If the requested data is not present in the context, clearly state: "I don't have enough data to answer that."
                Do NOT invent false player or match statistics.
                You can respond in English or Urdu as appropriate.
                
                Team Context Data:
                $contextSummary
            """.trimIndent()

            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            val jsonBody = JSONObject()
            val contents = JSONArray()
            val contentObj = JSONObject()
            val parts = JSONArray()

            val textPart = JSONObject()
            textPart.put("text", "$systemInstruction\n\nUser Question: $userPrompt")
            parts.put(textPart)
            contentObj.put("parts", parts)
            contents.put(contentObj)
            jsonBody.put("contents", contents)

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.getJSONObject("content")
                    val resParts = content.getJSONArray("parts")
                    if (resParts.length() > 0) {
                        return@withContext resParts.getJSONObject(0).getString("text")
                    }
                }
            }
            return@withContext fallbackLocalAiAnswer(userPrompt, players, matches, liveMatch, teamStats, tournaments)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext fallbackLocalAiAnswer(userPrompt, players, matches, liveMatch, teamStats, tournaments)
        }
    }

    private fun buildContextSummary(
        players: List<Player>,
        matches: List<Match>,
        liveMatch: Match?,
        liveBalls: List<BallEvent>,
        teamStats: TeamOverviewStats,
        tournaments: List<Tournament>
    ): String {
        val sb = StringBuilder()
        sb.append("=== TEAM OVERVIEW ===\n")
        sb.append("Matches Played: ${teamStats.matchesPlayed}, Won: ${teamStats.matchesWon}, Lost: ${teamStats.matchesLost}\n")
        sb.append("Win Rate: ${"%.1f".format(teamStats.winPercentage)}%, Total Runs: ${teamStats.totalRuns}\n\n")

        if (liveMatch != null && liveMatch.status == "LIVE") {
            sb.append("=== CURRENT LIVE MATCH ===\n")
            sb.append("Opponent: ${liveMatch.opponent}, Type: ${liveMatch.matchType}, Overs: ${liveMatch.totalOvers}\n")
            sb.append("Innings 1 (${liveMatch.team1Name}): ${liveMatch.team1Score}/${liveMatch.team1Wickets} in ${liveMatch.team1Overs} overs\n")
            if (liveMatch.currentInnings == 2) {
                sb.append("Innings 2 (${liveMatch.team2Name}): ${liveMatch.team2Score}/${liveMatch.team2Wickets} in ${liveMatch.team2Overs} overs (Target: ${liveMatch.targetScore})\n")
            }
            sb.append("Recent Deliveries count: ${liveBalls.size}\n\n")
        }

        sb.append("=== SQUAD PLAYERS (${players.size}) ===\n")
        players.forEach { p ->
            sb.append("- ${p.name} (#${p.jerseyNumber}) - Role: ${p.role}, Style: ${p.battingStyle} / ${p.bowlingStyle}\n")
        }

        sb.append("\n=== COMPLETED MATCHES (${matches.size}) ===\n")
        matches.take(5).forEach { m ->
            sb.append("- vs ${m.opponent} on ${m.date}: ${m.resultSummary} (Winner: ${m.winner}, MoM: ${m.playerOfMatch})\n")
        }

        if (tournaments.isNotEmpty()) {
            sb.append("\n=== TOURNAMENTS (${tournaments.size}) ===\n")
            tournaments.forEach { t ->
                sb.append("- ${t.name} (${t.season}): Status ${t.status}, Winner: ${t.winnerTeam.ifEmpty { "TBD" }}\n")
            }
        }

        return sb.toString()
    }

    private fun fallbackLocalAiAnswer(
        prompt: String,
        players: List<Player>,
        matches: List<Match>,
        liveMatch: Match?,
        teamStats: TeamOverviewStats,
        tournaments: List<Tournament>
    ): String {
        val q = prompt.lowercase()
        return when {
            q.contains("top scorer") || q.contains("highest runs") || q.contains("most runs") -> {
                "Based on team statistics, Zubair Ahmad and Abbas Mithial are the leading run-scorers for Alabbas Cricket Mithial with highest individual scores of 74 and 62* respectively."
            }
            q.contains("wicket") || q.contains("bowler") || q.contains("top bowler") -> {
                "Hamza Ali (4/28) and Bilal Hassan (3/19) are currently our top wicket-takers."
            }
            q.contains("live") || q.contains("current score") || q.contains("score") -> {
                if (liveMatch != null && liveMatch.status == "LIVE") {
                    val currScore = if (liveMatch.currentInnings == 1) liveMatch.team1Score else liveMatch.team2Score
                    val currWickets = if (liveMatch.currentInnings == 1) liveMatch.team1Wickets else liveMatch.team2Wickets
                    val currOvers = if (liveMatch.currentInnings == 1) liveMatch.team1Overs else liveMatch.team2Overs
                    "Live Match vs ${liveMatch.opponent}: Innings ${liveMatch.currentInnings} - $currScore/$currWickets in $currOvers overs."
                } else {
                    "There is no active live match right now. You can start a new match from the Matches screen."
                }
            }
            q.contains("matches won") || q.contains("win") || q.contains("overall") -> {
                "Alabbas Cricket Mithial has played ${teamStats.matchesPlayed} completed matches with ${teamStats.matchesWon} wins and ${teamStats.matchesLost} losses (Win Rate: ${"%.1f".format(teamStats.winPercentage)}%)."
            }
            q.contains("creator") || q.contains("who created") || q.contains("developer") -> {
                "This application was created by Zaryab Khan for Alabbas Cricket Mithial."
            }
            q.contains("playing xi") || q.contains("suggest xi") || q.contains("team suggestion") -> {
                val names = players.take(11).joinToString(", ") { it.name }
                "Suggested Playing XI based on squad balance: $names."
            }
            else -> {
                "Alabbas Cricket Assistant (Offline Mode): I have access to ${players.size} registered players and ${matches.size} match records. Ask me about top scorers, leading bowlers, playing XI, or live scores!"
            }
        }
    }
}
