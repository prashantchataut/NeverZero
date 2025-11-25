package com.productivitystreak

import android.app.Application
import com.productivitystreak.data.PersonalizedQuoteEngine
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.backup.BackupManager
import com.productivitystreak.data.local.AppDatabase
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.repository.*
import com.productivitystreak.debug.GlobalExceptionHandler
import com.productivitystreak.notifications.GhostNotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NeverZeroApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database by lazy { AppDatabase.getDatabase(this) }
    val preferencesManager by lazy { PreferencesManager(this) }
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    // Repositories
    val streakRepository by lazy { StreakRepository(database.streakDao()) }
    val vocabularyRepository by lazy { VocabularyRepository(database.vocabularyDao()) }
    val bookRepository by lazy { BookRepository(database.bookDao(), database.readingSessionDao()) }
    val reflectionRepository by lazy { ReflectionRepository(database.dailyReflectionDao()) }
    val achievementRepository by lazy { AchievementRepository(database.achievementDao()) }
    val assetRepository by lazy { AssetRepository() }
    val timeCapsuleRepository by lazy { TimeCapsuleRepository(database.timeCapsuleDao()) }
    val templateRepository by lazy { TemplateRepository() }
    val socialRepository by lazy { SocialRepository() }
    
    // AI
    val geminiClient by lazy { com.productivitystreak.data.gemini.GeminiClient.getInstance() }
    val quoteRepository by lazy { QuoteRepository(PersonalizedQuoteEngine(geminiClient)) }

    // Utilities
    val backupManager by lazy { BackupManager(this, database) }
    private val ghostScheduler by lazy { GhostNotificationScheduler(this) }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(this))

        // Initialize sample data if needed
        applicationScope.launch {
            try {
                streakRepository.initializeSampleData()
                achievementRepository.initializeAchievements()
                _isInitialized.value = true
            } catch (e: Exception) {
                android.util.Log.e("NeverZeroApp", "Initialization failed", e)
                // Still mark as initialized to prevent blocking the app
                _isInitialized.value = true
            }
        }

        // Schedule ghost notifications
        ghostScheduler.schedule()
    }
}
