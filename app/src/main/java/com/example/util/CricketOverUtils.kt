package com.example.util

object CricketOverUtils {

    /**
     * Converts overs represented as Float (e.g. 1.3f) to total legal balls integer.
     * Avoids float truncation/precision issues.
     */
    fun oversToLegalBalls(oversFloat: Float): Int {
        val fullOvers = oversFloat.toInt()
        val ballsInOver = Math.round((oversFloat - fullOvers) * 10f)
        return (fullOvers * 6) + ballsInOver
    }

    /**
     * Converts total legal balls (e.g. 9) to overs Float (1.3f).
     */
    fun legalBallsToOversFloat(legalBalls: Int): Float {
        val fullOvers = legalBalls / 6
        val ballsInOver = legalBalls % 6
        return fullOvers + (ballsInOver / 10.0f)
    }

    /**
     * Formats legal balls count as a standard cricket overs string e.g. 9 -> "1.3", 12 -> "2.0".
     */
    fun formatOvers(legalBalls: Int): String {
        return "${legalBalls / 6}.${legalBalls % 6}"
    }

    /**
     * Formats overs Float cleanly without precision bugs e.g. 1.3000002f -> "1.3".
     */
    fun formatOversFromFloat(oversFloat: Float): String {
        return formatOvers(oversToLegalBalls(oversFloat))
    }

    /**
     * Checks if a delivery extraType is legal (Not a WIDE or NO_BALL).
     */
    fun isLegalDelivery(extraType: String): Boolean {
        return extraType != "WIDE" && extraType != "NO_BALL"
    }

    /**
     * Calculates current run rate (CRR).
     */
    fun calculateRunRate(runs: Int, legalBalls: Int): Double {
        return if (legalBalls > 0) (runs.toDouble() / legalBalls) * 6.0 else 0.0
    }

    /**
     * Calculates required run rate (RRR).
     */
    fun calculateRequiredRunRate(runsNeeded: Int, ballsRemaining: Int): Double {
        return if (ballsRemaining > 0 && runsNeeded > 0) (runsNeeded.toDouble() / ballsRemaining) * 6.0 else 0.0
    }
}
