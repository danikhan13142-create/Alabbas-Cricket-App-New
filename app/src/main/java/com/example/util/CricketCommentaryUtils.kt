package com.example.util

import com.example.data.model.BallEvent

object CricketCommentaryUtils {

    fun generateCommentary(ball: BallEvent): String {
        val overBall = "${ball.overNumber}.${ball.ballNumberInOver}"
        val batter = ball.batterName
        val bowler = ball.bowlerName
        val dir = if (ball.shotDirection.isNotEmpty()) " towards ${ball.shotDirection}" else ""

        if (ball.isWicket) {
            val type = if (ball.dismissalType.isNotEmpty()) ball.dismissalType else "Wicket"
            val fielder = if (ball.fielderName.isNotEmpty()) " by ${ball.fielderName}" else ""
            return "OUT! $bowler gets $batter! ($type$fielder). Crucial breakthrough!"
        }

        return when (ball.extraType) {
            "WIDE" -> {
                val totalExtra = 1 + ball.extraRuns
                "WIDE! $bowler strays down the leg side, $totalExtra extra runs added."
            }
            "NO_BALL" -> "NO BALL! $bowler oversteps the crease. Free Hit coming up!"
            "BYE" -> "BYE! $batter misses, $bowler's delivery passes the keeper for ${ball.extraRuns} bye runs."
            "LEG_BYE" -> "LEG BYE! Off the pad$dir, batters take ${ball.extraRuns} leg bye run(s)."
            else -> {
                when (ball.runsScored) {
                    0 -> "Dot ball. $bowler bowls tight line to $batter, no run taken."
                    1 -> "1 run. $batter tucks it away$dir for a quick single."
                    2 -> "2 runs. Driven nicely$dir, excellent running between the wickets."
                    3 -> "3 runs. Great placement$dir, fielders chase it down near the rope."
                    4 -> "FOUR! $batter beautifully strokes $bowler's delivery$dir straight to the boundary fence!"
                    6 -> "SIX! MASSIVE HIT! $batter launches $bowler high over $dir into the stands!"
                    else -> "${ball.runsScored} runs scored off $bowler."
                }
            }
        }
    }
}
