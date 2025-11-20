package com.productivitystreak.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.productivitystreak.NeverZeroApplication
import com.productivitystreak.data.local.PreferencesManager
import kotlinx.coroutines.flow.first

class TimeCapsuleDeliveryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val capsuleId = inputData.getString(KEY_CAPSULE_ID) ?: return Result.failure()
            val app = applicationContext as NeverZeroApplication
            val preferencesManager = PreferencesManager(applicationContext)

            val notificationsEnabled = preferencesManager.notificationsEnabled.first()
            if (!notificationsEnabled) return Result.success()

            val repository = app.timeCapsuleRepository
            val capsule = repository.getTimeCapsule(capsuleId) ?: return Result.success()
            if (capsule.opened) return Result.success()

            val helper = NotificationHelper(applicationContext)
            helper.showTimeCapsuleDelivery(capsule.goalDescription)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        private const val KEY_CAPSULE_ID = "time_capsule_id"

        fun createInputData(id: String): Data =
            Data.Builder()
                .putString(KEY_CAPSULE_ID, id)
                .build()
    }
}
