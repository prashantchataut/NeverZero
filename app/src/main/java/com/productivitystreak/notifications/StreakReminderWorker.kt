package com.productivitystreak.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.productivitystreak.NeverZeroApplication
import com.productivitystreak.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class StreakReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as? NeverZeroApplication 
                ?: return Result.failure()
            val preferencesManager = PreferencesManager(applicationContext)

            // Check if notifications are enabled
            val notificationsEnabled = preferencesManager.notificationsEnabled.first()
            if (!notificationsEnabled) {
                return Result.success()
            }

            val frequency = inputData.getString("frequency") ?: "Daily"
            val userName = preferencesManager.userName.first()
            val notificationHelper = NotificationHelper(applicationContext)

            // If Weekly, just show generic reminder (simple fallback)
            if (frequency == "Weekly") {
                val activeStreakCount = app.streakRepository.getActiveStreakCount()
                notificationHelper.showDailyReminder(userName, activeStreakCount)
                return Result.success()
            }

            // Adaptive Logic for Daily/Hourly checks
            val engine = SmartNotificationEngine()
            val streaks = app.streakRepository.observeStreaks().first()
            val now = LocalDateTime.now()
            val currentHour = now.hour
            val today = now.toLocalDate().toString()
            val prefs = applicationContext.getSharedPreferences("notification_history", Context.MODE_PRIVATE)

            var notificationSent = false

            for (streak in streaks) {
                // Skip if already completed today
                if (streak.history.any { it.date == today && it.metGoal }) {
                    continue
                }

                val reminderKey = "reminder_${streak.id}_$today"
                val dangerKey = "danger_${streak.id}_$today"

                // 1. Adaptive Timing Reminder
                val optimalTime = engine.getOptimalNotificationTime(streak)
                if (currentHour == optimalTime && !prefs.contains(reminderKey)) {
                    val milestone = engine.checkMilestone(streak)
                    notificationHelper.showAdaptiveReminder(streak.name, milestone)
                    prefs.edit().putBoolean(reminderKey, true).apply()
                    notificationSent = true
                }

                // 2. Danger Warning
                val danger = engine.checkStreakDanger(streak)
                if (danger == SmartNotificationEngine.DangerLevel.CRITICAL && !prefs.contains(dangerKey)) {
                    notificationHelper.showStreakDangerWarning(streak.name, 24 - currentHour)
                    prefs.edit().putBoolean(dangerKey, true).apply()
                    notificationSent = true
                }
            }

            // If no specific notifications sent and it's 9 AM (default fallback), send generic if nothing else
            // But since we run hourly, we don't want to spam generic.
            // Maybe only send generic if user has NO streaks?
            if (!notificationSent && streaks.isEmpty() && currentHour == 9) {
                val genericKey = "generic_reminder_$today"
                if (!prefs.contains(genericKey)) {
                    notificationHelper.showDailyReminder(userName, 0)
                    prefs.edit().putBoolean(genericKey, true).apply()
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        private const val KEY_CATEGORIES = "streak_categories"
        private const val KEY_USER_NAME = "streak_user_name"

        fun createInputData(categories: Set<String>, userName: String, frequency: String): Data {
            return Data.Builder()
                .putStringArray(KEY_CATEGORIES, categories.toTypedArray())
                .putString(KEY_USER_NAME, userName)
                .putString("frequency", frequency)
                .build()
        }
    }
}
