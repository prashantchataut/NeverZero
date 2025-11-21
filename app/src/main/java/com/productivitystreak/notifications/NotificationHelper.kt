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
        private const val CHANNEL_ID_TIME_CAPSULE = "time_capsule"
        private const val CHANNEL_ID_GHOST = "ghost_notifications"

        private const val NOTIFICATION_ID_DAILY_REMINDER = 1
        private const val NOTIFICATION_ID_ACHIEVEMENT = 2
        private const val NOTIFICATION_ID_MILESTONE = 3
        private const val NOTIFICATION_ID_BACKUP = 4
        private const val NOTIFICATION_ID_STREAK_DANGER = 5
        private const val NOTIFICATION_ID_TIME_CAPSULE = 6
        private const val NOTIFICATION_ID_GHOST = 7
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

            // Time Capsule deliveries
            val timeCapsuleChannel = NotificationChannel(
                CHANNEL_ID_TIME_CAPSULE,
                "Time Capsules",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Scheduled reflections from your past self"
                enableVibration(true)
            }

            // Ghost notifications
            val ghostChannel = NotificationChannel(
                CHANNEL_ID_GHOST,
                "Ghost Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Occasional data-based nudges that sound like they’re from you."
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(
                listOf(remindersChannel, achievementsChannel, milestonesChannel, backupChannel, dangerChannel, timeCapsuleChannel, ghostChannel)
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

    fun showAdaptiveReminder(streakName: String, milestone: SmartNotificationEngine.MilestoneType?) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            streakName.hashCode(), // Unique ID per streak
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = "Time for $streakName"
        val message = if (milestone != null) {
            "Do it today to reach ${milestone.title}! ${milestone.message}"
        } else {
            "It's your usual time to work on $streakName. Keep the momentum going!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(streakName.hashCode(), notification)
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

    fun showTimeCapsuleDelivery(goalDescription: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = "Message from your past self"
        val message = if (goalDescription.isNotBlank()) {
            "Revisit the commitment: $goalDescription"
        } else {
            "Take a moment to compare who you intended to be with who you are today."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TIME_CAPSULE)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_TIME_CAPSULE, notification)
    }

    fun showGhostSlumpNudge(userName: String, daysInactive: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = if (userName.isNotBlank()) {
            "$userName, your habits have been quiet"
        } else {
            "Your habits have been quiet"
        }

        val message = if (daysInactive <= 3) {
            "It’s been a couple of days since you checked in. Take one small step your future self would recognize."
        } else {
            "It’s been a while since you checked in. Start with a 60-second action today and rebuild from there."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GHOST)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_GHOST, notification)
    }

    fun showGhostMomentumNudge(streakName: String, currentCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = "Protect your run"
        val message = "You’re $currentCount days into \"$streakName\". A small action today keeps the story going."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GHOST)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_GHOST, notification)
    }
}
