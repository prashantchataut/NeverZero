package com.productivitystreak.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.productivitystreak.ui.state.profile.ReminderFrequency
import java.time.Duration

class StreakReminderScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    fun scheduleReminder(
        frequency: ReminderFrequency,
        categories: Set<String>,
        userName: String
    ) {
        if (frequency == ReminderFrequency.None) {
            cancelReminders()
            return
        }

        createChannel()

        val repeatInterval = when (frequency) {
            ReminderFrequency.Daily -> Duration.ofHours(1) // Run hourly to check for optimal times
            ReminderFrequency.Weekly -> Duration.ofDays(7)
            ReminderFrequency.None -> return
        }

        val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(repeatInterval)
            .setInputData(StreakReminderWorker.createInputData(categories, userName, frequency.name))
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelReminders() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    // Quick Win: Schedule weekly backup reminder
    fun scheduleWeeklyBackup() {
        createBackupChannel()

        val workRequest = PeriodicWorkRequestBuilder<WeeklyBackupWorker>(Duration.ofDays(7))
            .build()

        workManager.enqueueUniquePeriodicWork(
            BACKUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun cancelWeeklyBackup() {
        workManager.cancelUniqueWork(BACKUP_WORK_NAME)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Never Zero Streak Nudges",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Friendly reminders that help you keep your streak warm."
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createBackupChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BACKUP_CHANNEL_ID,
                "Backup Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Weekly reminders to backup your data."
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "never_zero_streak_channel"
        const val BACKUP_CHANNEL_ID = "backup_reminder_channel"
        private const val UNIQUE_WORK_NAME = "never_zero_streak_reminder"
        private const val BACKUP_WORK_NAME = "weekly_backup_reminder"
    }
}
