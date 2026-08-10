package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.data.model.Match

object CricketScoringNotification {

    private const val CHANNEL_ID = "cricket_live_scoring_channel"
    private const val NOTIF_ID = 2026

    fun updateLiveScoreNotification(context: Context, match: Match) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Cricket Live Score Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live ongoing match score in system tray"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val inningsText = if (match.currentInnings == 1) {
            "${match.team1Name}: ${match.team1Score}/${match.team1Wickets} (${CricketOverUtils.formatOversFromFloat(match.team1Overs)} ov)"
        } else {
            "${match.team2Name}: ${match.team2Score}/${match.team2Wickets} (${CricketOverUtils.formatOversFromFloat(match.team2Overs)} ov) | Target: ${match.targetScore}"
        }

        val statusText = if (match.status == "COMPLETED") match.resultSummary else "Live Match Ongoing"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("🏏 ${match.team1Name} vs ${match.team2Name}")
            .setContentText(inningsText)
            .setSubText(statusText)
            .setOngoing(match.status == "LIVE")
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(NOTIF_ID, notification)
    }

    fun clearNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIF_ID)
    }
}
