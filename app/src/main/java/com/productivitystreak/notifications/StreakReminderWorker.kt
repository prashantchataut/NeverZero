package com.productivitystreak.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters

class StreakReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // TODO: Implement reminder notification logic.
        return Result.success()
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
