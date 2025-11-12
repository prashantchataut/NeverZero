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
import java.util.concurrent.TimeUnit

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

        val intervalDays = when (frequency) {
            ReminderFrequency.Daily -> 1L
            ReminderFrequency.Weekly -> 7L
            ReminderFrequency.None -> return
        }

        val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(intervalDays, TimeUnit.DAYS)
            .setInputData(StreakReminderWorker.createInputData(categories, userName))
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

    companion object {
        const val CHANNEL_ID = "never_zero_streak_channel"
        private const val UNIQUE_WORK_NAME = "never_zero_streak_reminder"
    }
}
