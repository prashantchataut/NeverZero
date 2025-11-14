package com.productivitystreak.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.productivitystreak.data.backup.BackupManager
import com.productivitystreak.NeverZeroApplication

/**
 * Worker that reminds users weekly to backup their data
 */
class WeeklyBackupWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val app = context.applicationContext as NeverZeroApplication
            val backupManager = app.backupManager
            
            // Show notification reminding user to backup
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showBackupReminder()
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
