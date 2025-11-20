package com.productivitystreak.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.productivitystreak.MainActivity
import com.productivitystreak.R

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID_REMINDERS = "streak_reminders"
        private const val CHANNEL_ID_ACHIEVEMENTS = "achievements"
        private const val CHANNEL_ID_MILESTONES = "milestones"
        private const val CHANNEL_ID_BACKUP = "backup_reminders"
        private const val CHANNEL_ID_STREAK_DANGER = "streak_danger"

        private const val NOTIFICATION_ID_DAILY_REMINDER = 1
        private const val NOTIFICATION_ID_ACHIEVEMENT = 2
        private const val NOTIFICATION_ID_MILESTONE = 3
        private const val NOTIFICATION_ID_BACKUP = 4
        private const val NOTIFICATION_ID_STREAK_DANGER = 5
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Reminders channel
            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to maintain your streaks"
                enableVibration(true)
            }

            // Achievements channel
            val achievementsChannel = NotificationChannel(
                CHANNEL_ID_ACHIEVEMENTS,
                "Achievements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when you unlock achievements"
                enableVibration(true)
            }

            // Milestones channel
            val milestonesChannel = NotificationChannel(
                CHANNEL_ID_MILESTONES,
                "Milestones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Celebrations for reaching milestones"
                enableVibration(true)
            }

            // Backup reminders channel
            val backupChannel = NotificationChannel(
                CHANNEL_ID_BACKUP,
                "Backup Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Weekly reminders to backup your data"
            }

            // Streak danger alerts
            val dangerChannel = NotificationChannel(
                CHANNEL_ID_STREAK_DANGER,
                "Streak Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts when streaks are about to break"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(
                listOf(remindersChannel, achievementsChannel, milestonesChannel, backupChannel, dangerChannel)
            )
        }
    }

    fun showDailyReminder(userName: String, activeStreakCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = if (userName.isNotEmpty()) {
            "Hi $userName! Time to maintain your streaks"
        } else {
            "Don't break your streak!"
        }

        val message = when {
            activeStreakCount == 0 -> "Start building your first streak today!"
            activeStreakCount == 1 -> "You have 1 active streak. Keep it going!"
            else -> "You have $activeStreakCount active streaks. Keep them alive!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_DAILY_REMINDER, notification)
    }

    fun showAchievementUnlocked(title: String, description: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Achievement Unlocked!")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$title\n\n$description"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ACHIEVEMENT, notification)
    }

    fun showMilestone(streakName: String, count: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MILESTONES)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Milestone Reached!")
            .setContentText("$streakName: $count day streak!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Congratulations! You've reached a $count day streak for '$streakName'. Keep up the amazing work!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_MILESTONE, notification)
    }

    // Phase 4: Advanced notification - Backup reminder
    fun showBackupReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BACKUP)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Time to Backup Your Data")
            .setContentText("Keep your streaks safe! Export a backup today.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BACKUP, notification)
    }

    // Phase 4: Advanced notification - Streak danger warning (23h mark)
    fun showStreakDangerWarning(streakName: String, hoursRemaining: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_STREAK_DANGER)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Streak Alert!")
            .setContentText("$streakName streak will break in $hoursRemaining hour(s)!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Your '$streakName' streak is in danger! You have only $hoursRemaining hour(s) left to maintain it. Take action now!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_STREAK_DANGER, notification)
    }
}
