package com.productivitystreak.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.productivitystreak.NeverZeroApplication
import com.productivitystreak.data.local.PreferencesManager
import kotlinx.coroutines.flow.first

class StreakReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as NeverZeroApplication
            val preferencesManager = PreferencesManager(applicationContext)

            // Check if notifications are enabled
            val notificationsEnabled = preferencesManager.notificationsEnabled.first()
            if (!notificationsEnabled) {
                return Result.success()
            }

            // Get user preferences
            val userName = preferencesManager.userName.first()
            val activeStreakCount = app.streakRepository.getActiveStreakCount()

            // Show the reminder notification
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.showDailyReminder(userName, activeStreakCount)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        private const val KEY_CATEGORIES = "streak_categories"
        private const val KEY_USER_NAME = "streak_user_name"

        fun createInputData(categories: Set<String>, userName: String): Data {
            return Data.Builder()
                .putStringArray(KEY_CATEGORIES, categories.toTypedArray())
                .putString(KEY_USER_NAME, userName)
                .build()
        }
    }
}
