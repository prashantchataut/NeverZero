package com.productivitystreak

import android.app.Application
import com.productivitystreak.data.backup.BackupManager
import com.productivitystreak.data.local.AppDatabase
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.repository.*
import com.productivitystreak.debug.GlobalExceptionHandler
import com.productivitystreak.notifications.GhostNotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NeverZeroApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database by lazy { AppDatabase.getDatabase(this) }
    val preferencesManager by lazy { PreferencesManager(this) }

    // Repositories
    val streakRepository by lazy { StreakRepository(database.streakDao()) }
    val vocabularyRepository by lazy { VocabularyRepository(database.vocabularyDao()) }
    val bookRepository by lazy { BookRepository(database.bookDao(), database.readingSessionDao()) }
    val reflectionRepository by lazy { ReflectionRepository(database.dailyReflectionDao()) }
    val achievementRepository by lazy { AchievementRepository(database.achievementDao()) }
    val assetRepository by lazy { AssetRepository() }
    val timeCapsuleRepository by lazy { TimeCapsuleRepository(database.timeCapsuleDao()) }
    val timeCapsuleRepository by lazy { TimeCapsuleRepository(database.timeCapsuleDao()) }
    val templateRepository by lazy { TemplateRepository() }
    
    // AI
    val geminiClient by lazy { com.productivitystreak.data.gemini.GeminiClient.getInstance() }
    val quoteRepository by lazy { QuoteRepository(com.productivitystreak.data.PersonalizedQuoteEngine(geminiClient)) }

    // Utilities
    val backupManager by lazy { BackupManager(this, database) }
    private val ghostScheduler by lazy { GhostNotificationScheduler(this) }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(this))

        // Initialize sample data if needed
        applicationScope.launch {
            streakRepository.initializeSampleData()
            achievementRepository.initializeAchievements()
        }

        // Schedule ghost notifications
        ghostScheduler.schedule()
    }
}
