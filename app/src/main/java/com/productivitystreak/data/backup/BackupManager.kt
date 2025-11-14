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

    suspend fun restoreBackup(file: File, mergeMode: Boolean = false): Result<String> = withContext(Dispatchers.IO) {
        try {
            val json = file.readText()
            val adapter = moshi.adapter(BackupData::class.java)
            val backupData = adapter.fromJson(json) ?: return@withContext Result.failure(
                Exception("Failed to parse backup file")
            )

            // Validate backup version
            if (backupData.version > 1) {
                return@withContext Result.failure(
                    Exception("Backup version ${backupData.version} is not supported by this app version")
                )
            }

            var restoredCount = 0

            // Phase 4: Complete implementation with merge/replace options
            if (!mergeMode) {
                // Replace mode: Clear existing data first
                database.streakDao().deleteAll()
                database.bookDao().deleteAll()
                database.vocabularyDao().deleteAll()
                database.dailyReflectionDao().deleteAll()
                database.achievementDao().deleteAll()
                database.quoteDao().deleteAll()
            }

            // Restore streaks
            try {
                val streaksAdapter = moshi.adapter<List<com.productivitystreak.data.local.entity.StreakEntity>>()
                val streaks = streaksAdapter.fromJson(backupData.streaks)
                streaks?.forEach { streak ->
                    if (mergeMode) {
                        // In merge mode, only add if not exists
                        val existing = database.streakDao().getStreakById(streak.id)
                        if (existing == null) {
                            database.streakDao().insertStreak(streak)
                            restoredCount++
                        }
                    } else {
                        database.streakDao().insertStreak(streak)
                        restoredCount++
                    }
                }
            } catch (e: Exception) {
                // Continue even if one type fails
            }

            // Restore books
            try {
                val booksAdapter = moshi.adapter<List<com.productivitystreak.data.local.entity.BookEntity>>()
                val books = booksAdapter.fromJson(backupData.books)
                books?.forEach { book ->
                    if (mergeMode) {
                        val existing = database.bookDao().getBookById(book.id)
                        if (existing == null) {
                            database.bookDao().insertBook(book)
                            restoredCount++
                        }
                    } else {
                        database.bookDao().insertBook(book)
                        restoredCount++
                    }
                }
            } catch (e: Exception) {
                // Continue
            }

            // Restore vocabulary
            try {
                val vocabAdapter = moshi.adapter<List<com.productivitystreak.data.local.entity.VocabularyWordEntity>>()
                val words = vocabAdapter.fromJson(backupData.vocabularyWords)
                words?.forEach { word ->
                    if (mergeMode) {
                        val existing = database.vocabularyDao().getWordById(word.id)
                        if (existing == null) {
                            database.vocabularyDao().insertWord(word)
                            restoredCount++
                        }
                    } else {
                        database.vocabularyDao().insertWord(word)
                        restoredCount++
                    }
                }
            } catch (e: Exception) {
                // Continue
            }

            // Restore reflections
            try {
                val reflectionsAdapter = moshi.adapter<List<com.productivitystreak.data.local.entity.DailyReflectionEntity>>()
                val reflections = reflectionsAdapter.fromJson(backupData.reflections)
                reflections?.forEach { reflection ->
                    if (mergeMode) {
                        val existing = database.dailyReflectionDao().getReflectionByDate(reflection.date)
                        if (existing == null) {
                            database.dailyReflectionDao().insertReflection(reflection)
                            restoredCount++
                        }
                    } else {
                        database.dailyReflectionDao().insertReflection(reflection)
                        restoredCount++
                    }
                }
            } catch (e: Exception) {
                // Continue
            }

            // Restore achievements
            try {
                val achievementsAdapter = moshi.adapter<List<com.productivitystreak.data.local.entity.AchievementEntity>>()
                val achievements = achievementsAdapter.fromJson(backupData.achievements)
                achievements?.forEach { achievement ->
                    if (mergeMode) {
                        val existing = database.achievementDao().getAchievementById(achievement.id)
                        if (existing == null) {
                            database.achievementDao().insertAchievement(achievement)
                            restoredCount++
                        }
                    } else {
                        database.achievementDao().insertAchievement(achievement)
                        restoredCount++
                    }
                }
            } catch (e: Exception) {
                // Continue
            }

            // Restore quotes
            try {
                val quotesAdapter = moshi.adapter<List<com.productivitystreak.data.local.entity.QuoteEntity>>()
                val quotes = quotesAdapter.fromJson(backupData.quotes)
                quotes?.forEach { quote ->
                    database.quoteDao().insertQuote(quote)
                    restoredCount++
                }
            } catch (e: Exception) {
                // Continue
            }

            val mode = if (mergeMode) "merged" else "replaced"
            Result.success("Successfully $mode $restoredCount items from backup")
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
