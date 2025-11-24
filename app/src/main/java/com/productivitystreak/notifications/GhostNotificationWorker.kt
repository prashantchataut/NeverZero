package com.productivitystreak.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.productivitystreak.NeverZeroApplication
import com.productivitystreak.data.local.PreferencesManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first

class GhostNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as? NeverZeroApplication 
                ?: return Result.failure()
            val preferences = PreferencesManager(applicationContext)

            val notificationsEnabled = preferences.notificationsEnabled.first()
            if (!notificationsEnabled) return Result.success()

            val today = LocalDate.now()
            val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
            val todayString = today.format(dateFormatter)
            val lastGhostDate = preferences.ghostLastSentDate.first()

            if (lastGhostDate == todayString) {
                return Result.success()
            }

            val streaks = app.streakRepository.observeStreaks().first()
            if (streaks.isEmpty()) {
                return Result.success()
            }

            val allRecords = streaks.flatMap { it.history }
            val mostRecentDateString = allRecords.maxByOrNull { it.date }?.date ?: return Result.success()
            val mostRecentDate = runCatching { LocalDate.parse(mostRecentDateString) }.getOrNull() ?: return Result.success()

            val daysInactive = java.time.Period.between(mostRecentDate, today).days

            val helper = NotificationHelper(applicationContext)
            val userName = preferences.userName.first()

            if (daysInactive >= 2) {
                helper.showGhostSlumpNudge(userName = userName, daysInactive = daysInactive)
                preferences.setGhostLastSentDate(todayString)
                return Result.success()
            }

            val momentumStreak = streaks
                .filter { it.currentCount >= 3 }
                .maxByOrNull { it.currentCount }

            if (momentumStreak != null) {
                val hasTodayRecord = momentumStreak.history.any { it.date == todayString }
                if (!hasTodayRecord) {
                    helper.showGhostMomentumNudge(streakName = momentumStreak.name, currentCount = momentumStreak.currentCount)
                    preferences.setGhostLastSentDate(todayString)
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
