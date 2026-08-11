package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.*
import com.example.util.CricketOverUtils
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
        val q = prompt.lowercase().trim()

        // Detect Urdu script presence
        val isUrdu = prompt.any { it in '\u0600'..'\u06FF' }

        if (isUrdu) {
            return when {
                q.contains("آؤٹ") || q.contains("قسم") || q.contains("ڈسمسل") -> {
                    "کرکٹ میں ۱۰ قانونی قسم کے آؤٹ ہوتے ہیں:\n" +
                            "۱. بولڈ (Bowled)\n" +
                            "۲. کیچ (Caught)\n" +
                            "۳. ایل بی ڈبلیو (LBW)\n" +
                            "۴. رن آؤٹ (Run Out)\n" +
                            "۵. اسٹمپ (Stumped)\n" +
                            "۶. ہٹ وکٹ (Hit Wicket)\n" +
                            "۷. فیلڈر کو روکنا (Obstructing Field)\n" +
                            "۸. ٹائمڈ آؤٹ (Timed Out)\n" +
                            "۹. بال کو دو بار مارنا (Hit Ball Twice)\n" +
                            "۱۰. ریٹائرڈ آؤٹ (Retired Out)"
                }
                q.contains("رنز") || q.contains("اسکور") || q.contains("سب سے زیادہ") -> {
                    val pList = players.take(5).joinToString(", ") { it.name }
                    "العباس کرکٹ مٹھیال ٹیم کے اہم بلے باز: $pList۔ مجموعی میچز: ${teamStats.matchesPlayed}، کل رنز: ${teamStats.totalRuns}۔"
                }
                q.contains("وکٹ") || q.contains("بولر") -> {
                    "العباس کرکٹ مٹھیال کے اہم باؤلرز: حمزہ علی، بلال حسن، اور عباس مٹھیال۔ مجموعی وکٹیں: ${teamStats.totalWickets}۔"
                }
                q.contains("میچ") || q.contains("فتوحات") || q.contains("ریکارڈ") -> {
                    "العباس کرکٹ مٹھیال کے اعداد و شمار:\n" +
                            "کھیلے گئے میچز: ${teamStats.matchesPlayed}\n" +
                            "جیتے گئے میچز: ${teamStats.matchesWon}\n" +
                            "ہار: ${teamStats.matchesLost}\n" +
                            "جیت کا تناسب: ${"%.1f".format(teamStats.winPercentage)}٪"
                }
                else -> {
                    "العباس کرکٹ مٹھیال اسسٹنٹ: کھیلے گئے میچز ${teamStats.matchesPlayed}، رجسٹرڈ کھلاڑی ${players.size}۔ آپ کھلاڑیوں کے اسکور، براہ راست میچ، یا کرکٹ کے قوانین کے بارے میں سوال پوچھ سکتے ہیں۔"
                }
            }
        }

        return when {
            q.contains("types of out") || q.contains("how many outs") || q.contains("dismissal") || q.contains("out types") || q.contains("ways to get out") -> {
                "There are 10 official modes of dismissal (outs) in cricket:\n\n" +
                        "1. Bowled: The delivery hits and dislodges bails from stumps.\n" +
                        "2. Caught: Fielder or wicketkeeper catches the ball off bat/glove before it grounds.\n" +
                        "3. LBW (Leg Before Wicket): Ball hits batter's body in line with stumps, preventing a hit.\n" +
                        "4. Run Out: Fielder breaks stumps while batter is outside crease attempting a run.\n" +
                        "5. Stumped: Wicketkeeper breaks stumps while batter is out of crease without running.\n" +
                        "6. Hit Wicket: Batter dislodges bails with bat or body while playing a delivery.\n" +
                        "7. Obstructing the Field: Batter willfully obstructs or distracts a fielding player.\n" +
                        "8. Timed Out: Incoming batter takes over 3 minutes (2 mins in T20) to reach crease.\n" +
                        "9. Hit the Ball Twice: Batter deliberately strikes the ball a second time.\n" +
                        "10. Retired Out: Batter leaves the field without umpire or captain consent."
            }
            q.contains("lbw") -> {
                "LBW (Leg Before Wicket) is given when the ball pitches in line or outside off, strikes the batter's leg or pad without contacting the bat first, and in the umpire's judgment would have hit the stumps."
            }
            q.contains("no ball") || q.contains("no-ball") -> {
                "A No-Ball is an illegal delivery caused by overstepping the popping crease, bowling above waist height without bouncing (beamer), or violating field settings. The batting team gets 1 extra run and a Free Hit on the next ball in limited-overs matches."
            }
            q.contains("wide") -> {
                "A Wide ball is an illegal delivery bowled beyond the batter's normal reach. It awards 1 extra run to the batting team and does not count as a legal delivery in the over."
            }
            q.contains("powerplay") || q.contains("power play") -> {
                "Powerplay refers to initial overs in limited-overs cricket where field restrictions apply (e.g. max 2 fielders outside the 30-yard circle in T20 overs 1-6)."
            }
            q.contains("free hit") -> {
                "A Free Hit is awarded after a front-foot No-Ball. On a Free Hit ball, the batter can only be dismissed via Run Out, Obstructing the Field, or Hit the Ball Twice."
            }
            q.contains("drs") || q.contains("review") -> {
                "DRS (Decision Review System) allows teams to challenge a field umpire's decision using technology like Hawk-Eye ball tracking, UltraEdge sound sensors, and ball tracking."
            }
            q.contains("creator") || q.contains("who created") || q.contains("developer") -> {
                "This official cricket scoring application was designed and built by Zaryab Khan for Alabbas Cricket Mithial."
            }
            q.contains("top scorer") || q.contains("highest runs") || q.contains("most runs") || q.contains("leading scorer") -> {
                if (players.isNotEmpty()) {
                    val pList = players.take(3).joinToString(", ") { "${it.name} (${it.role})" }
                    "Key batters in the Alabbas Cricket Mithial squad: $pList. Team Total Runs in completed matches: ${teamStats.totalRuns} across ${teamStats.matchesPlayed} matches."
                } else {
                    "No registered players in squad. Add players in Team Management."
                }
            }
            q.contains("wicket") || q.contains("bowler") || q.contains("top bowler") -> {
                val bowlers = players.filter { it.role == "Bowler" || it.role == "All-rounder" }.take(3).joinToString(", ") { it.name }
                "Leading bowlers for Alabbas Cricket Mithial: ${bowlers.ifEmpty { "Hamza Ali, Bilal Hassan, Abbas Mithial" }}. Total Team Wickets: ${teamStats.totalWickets}."
            }
            q.contains("live") || q.contains("current score") -> {
                if (liveMatch != null && liveMatch.status == "LIVE") {
                    val currScore = if (liveMatch.currentInnings == 1) liveMatch.team1Score else liveMatch.team2Score
                    val currWickets = if (liveMatch.currentInnings == 1) liveMatch.team1Wickets else liveMatch.team2Wickets
                    val currOvers = if (liveMatch.currentInnings == 1) liveMatch.team1Overs else liveMatch.team2Overs
                    "LIVE MATCH vs ${liveMatch.opponent}: Innings ${liveMatch.currentInnings} — $currScore/$currWickets in ${CricketOverUtils.formatOversFromFloat(currOvers)} overs (Target: ${liveMatch.targetScore})."
                } else {
                    "No match is currently LIVE. You can start a new match from the Matches screen."
                }
            }
            q.contains("matches won") || q.contains("win") || q.contains("overall") || q.contains("team stat") -> {
                if (teamStats.matchesPlayed == 0) {
                    "Alabbas Cricket Mithial currently has 0 completed matches recorded in the database. All stats reset clean."
                } else {
                    "Alabbas Cricket Mithial Stats:\nMatches Played: ${teamStats.matchesPlayed}\nWon: ${teamStats.matchesWon}\nLost: ${teamStats.matchesLost}\nTies: ${teamStats.ties}\nWin Rate: ${"%.1f".format(teamStats.winPercentage)}%\nTotal Runs: ${teamStats.totalRuns}\nHighest Team Score: ${teamStats.highestTeamScore}"
                }
            }
            q.contains("playing xi") || q.contains("suggest xi") || q.contains("team suggestion") || q.contains("squad") -> {
                if (players.isEmpty()) {
                    "Squad is empty. Register players in the Players section first!"
                } else {
                    val xi = players.take(11).joinToString("\n") { "${it.jerseyNumber}. ${it.name} (${it.role})" }
                    "Suggested Playing XI for Alabbas Cricket Mithial:\n\n$xi"
                }
            }
            else -> {
                "Alabbas Cricket Assistant: Squad size: ${players.size} players | Completed Matches: ${teamStats.matchesPlayed} | Win Rate: ${"%.1f".format(teamStats.winPercentage)}%.\n\nAsk me about cricket rules (outs, LBW, No-balls, Powerplay), team statistics, player roles, or live score updates!"
            }
        }
    }
}
