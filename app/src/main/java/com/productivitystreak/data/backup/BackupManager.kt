package com.productivitystreak.data.backup

import android.content.Context
import com.productivitystreak.data.local.AppDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BackupManager(private val context: Context, private val database: AppDatabase) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    data class BackupData(
        val version: Int = 1,
        val timestamp: Long = System.currentTimeMillis(),
        val streaks: String,
        val books: String,
        val vocabularyWords: String,
        val readingSessions: String,
        val reflections: String,
        val achievements: String,
        val quotes: String
    )

    suspend fun createBackup(): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Gather all data
            val streaks = database.streakDao().getAllStreaks().first()
            val books = database.bookDao().getAllBooks().first()
            val vocabularyWords = database.vocabularyDao().getAllWords().first()
            val reflections = database.dailyReflectionDao().getAllReflections().first()
            val achievements = database.achievementDao().getAllAchievements().first()
            val quotes = database.quoteDao().getAllQuotes().first()

            // Convert to JSON
            val backupData = BackupData(
                streaks = moshi.adapter(List::class.java).toJson(streaks),
                books = moshi.adapter(List::class.java).toJson(books),
                vocabularyWords = moshi.adapter(List::class.java).toJson(vocabularyWords),
                readingSessions = "[]", // Can be expanded
                reflections = moshi.adapter(List::class.java).toJson(reflections),
                achievements = moshi.adapter(List::class.java).toJson(achievements),
                quotes = moshi.adapter(List::class.java).toJson(quotes)
            )

            val adapter = moshi.adapter(BackupData::class.java)
            val json = adapter.toJson(backupData)

            // Save to file
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val fileName = "productivity_streak_backup_$timestamp.json"

            val backupDir = File(context.getExternalFilesDir(null), "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val backupFile = File(backupDir, fileName)
            backupFile.writeText(json)

            Result.success(backupFile)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun restoreBackup(file: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = file.readText()
            val adapter = moshi.adapter(BackupData::class.java)
            val backupData = adapter.fromJson(json) ?: return@withContext Result.failure(
                Exception("Failed to parse backup file")
            )

            // This is a basic implementation
            // In production, you'd want to:
            // 1. Validate the backup data
            // 2. Show user a preview
            // 3. Let them choose what to restore
            // 4. Merge or replace existing data

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getBackupFiles(): List<File> = withContext(Dispatchers.IO) {
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        if (!backupDir.exists()) {
            return@withContext emptyList()
        }

        backupDir.listFiles()
            ?.filter { it.extension == "json" && it.name.startsWith("productivity_streak_backup_") }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    suspend fun deleteBackup(file: File): Boolean = withContext(Dispatchers.IO) {
        file.delete()
    }
}
