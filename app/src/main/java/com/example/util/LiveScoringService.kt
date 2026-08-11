package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.database.AppDatabase
import com.example.data.model.Match
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class LiveScoringService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var activeMatchJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START_SERVICE
        val matchId = intent?.getLongExtra(EXTRA_MATCH_ID, -1L) ?: -1L

        when (action) {
            ACTION_START_SERVICE -> {
                startForegroundNotification()
                if (matchId != -1L) {
                    observeLiveMatch(matchId)
                } else {
                    observeAnyLiveMatch()
                }
            }
            ACTION_STOP_SERVICE -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_STICKY
    }

    private fun startForegroundNotification() {
        createNotificationChannel()
        val notification = buildNotification("Alabbas Cricket Live", "Live Match Scoring Active")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun observeLiveMatch(matchId: Long) {
        activeMatchJob?.cancel()
        activeMatchJob = serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            db.matchDao().getMatchByIdFlow(matchId).collectLatest { match ->
                if (match != null && match.status == "LIVE") {
                    updateNotificationForMatch(match)
                } else {
                    stopSelf()
                }
            }
        }
    }

    private fun observeAnyLiveMatch() {
        activeMatchJob?.cancel()
        activeMatchJob = serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            db.matchDao().getAllMatches().collectLatest { matches ->
                val liveMatch = matches.find { it.status == "LIVE" }
                if (liveMatch != null) {
                    updateNotificationForMatch(liveMatch)
                } else {
                    stopSelf()
                }
            }
        }
    }

    private fun updateNotificationForMatch(match: Match) {
        val inningsText = if (match.currentInnings == 1) {
            "${match.team1Name}: ${match.team1Score}/${match.team1Wickets} (${CricketOverUtils.formatOversFromFloat(match.team1Overs)} ov)"
        } else {
            "${match.team2Name}: ${match.team2Score}/${match.team2Wickets} (${CricketOverUtils.formatOversFromFloat(match.team2Overs)} ov) | Target: ${match.targetScore}"
        }

        val title = "🏏 ${match.team1Name} vs ${match.team2Name}"
        val notification = buildNotification(title, inningsText)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(title: String, contentText: String): android.app.Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSubText("Tap to return to live scoring")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Live Scoring Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps live match scoring active in background"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "live_scoring_service_channel"
        const val NOTIFICATION_ID = 20261
        const val ACTION_START_SERVICE = "ACTION_START_LIVE_SCORING"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_LIVE_SCORING"
        const val EXTRA_MATCH_ID = "EXTRA_MATCH_ID"

        fun startService(context: Context, matchId: Long? = null) {
            val intent = Intent(context, LiveScoringService::class.java).apply {
                action = ACTION_START_SERVICE
                matchId?.let { putExtra(EXTRA_MATCH_ID, it) }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, LiveScoringService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.stopService(intent)
        }
    }
}
