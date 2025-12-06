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

    // Database
    lateinit var database: AppDatabase
        private set

    // Repositories
    lateinit var streakRepository: StreakRepository
        private set
    lateinit var templateRepository: TemplateRepository
        private set
    lateinit var geminiRepository: GeminiRepository
        private set
    lateinit var achievementRepository: AchievementRepository
        private set
    lateinit var timeCapsuleRepository: TimeCapsuleRepository
        private set
    lateinit var reflectionRepository: ReflectionRepository
        private set
    lateinit var quoteRepository: QuoteRepository
        private set
    lateinit var buddhaRepository: com.productivitystreak.data.ai.BuddhaRepository
        private set
    lateinit var socialRepository: SocialRepository
        private set
    lateinit var journalRepository: JournalRepository
        private set
    lateinit var assetRepository: com.productivitystreak.data.repository.AssetRepository
        private set
    lateinit var gamificationEngine: com.productivitystreak.data.gamification.GamificationEngine
        private set

    // AI & Services
    lateinit var geminiClient: com.productivitystreak.data.gemini.GeminiClient
        private set
    lateinit var aiCoach: com.productivitystreak.data.ai.AICoach
        private set
    lateinit var personalizedQuoteEngine: PersonalizedQuoteEngine
        private set
    lateinit var preferencesManager: PreferencesManager
        private set
    lateinit var ghostScheduler: GhostNotificationScheduler
        private set
    lateinit var backupManager: com.productivitystreak.data.backup.BackupManager
        private set

    // Use Cases (Shared Logic)
    lateinit var rpgStatsUseCase: com.productivitystreak.domain.usecase.RpgStatsUseCase
        private set
    lateinit var geminiAIUseCase: com.productivitystreak.domain.usecase.GeminiAIUseCase
        private set
    lateinit var jsonSerializationUseCase: com.productivitystreak.domain.usecase.JsonSerializationUseCase
        private set

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(this))

        initializeDependencies()

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
        try {
            ghostScheduler.schedule()
        } catch (e: Exception) {
            android.util.Log.e("NeverZeroApp", "Failed to schedule ghost notifications", e)
        }
    }

    private fun initializeDependencies() {
        // 1. Core Data & Utils
        database = AppDatabase.getDatabase(this)
        preferencesManager = PreferencesManager(this)
        val moshi = com.squareup.moshi.Moshi.Builder()
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()

        // 2. Network & AI
        val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = okhttp3.OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create(moshi))
            .build()

        val geminiService = retrofit.create(com.productivitystreak.data.remote.GeminiService::class.java)
        geminiClient = com.productivitystreak.data.gemini.GeminiClient.getInstance(this)
        
        // 3. Repositories
        streakRepository = StreakRepository(database.streakDao())
        templateRepository = TemplateRepository()
        achievementRepository = AchievementRepository(database.achievementDao())
        timeCapsuleRepository = TimeCapsuleRepository(database.timeCapsuleDao())
        reflectionRepository = ReflectionRepository(database.dailyReflectionDao())
        buddhaRepository = com.productivitystreak.data.ai.BuddhaRepository(this)
        socialRepository = SocialRepository()
        journalRepository = JournalRepository(database.journalDao())
        assetRepository = com.productivitystreak.data.repository.AssetRepository()
        gamificationEngine = com.productivitystreak.data.gamification.GamificationEngine(
            userStatsDao = database.userStatsDao(),
            streakDao = database.streakDao()
        )

        geminiRepository = GeminiRepository(
            apiKey = com.productivitystreak.data.config.ApiKeyManager.getApiKey(this),
            service = geminiService,
            moshi = moshi
        )

        // 4. Complex Dependencies
        aiCoach = com.productivitystreak.data.ai.AICoach(geminiClient)
        personalizedQuoteEngine = PersonalizedQuoteEngine(geminiClient)
        
        quoteRepository = QuoteRepository(
            personalizedEngine = personalizedQuoteEngine,
            reflectionRepository = reflectionRepository,
            timeCapsuleRepository = timeCapsuleRepository,
            geminiClient = geminiClient
        )

        ghostScheduler = GhostNotificationScheduler(this)
        backupManager = com.productivitystreak.data.backup.BackupManager(this, database)

        // 5. Use Cases (Shared Logic)
        rpgStatsUseCase = com.productivitystreak.domain.usecase.RpgStatsUseCase()
        geminiAIUseCase = com.productivitystreak.domain.usecase.GeminiAIUseCase(geminiClient)
        jsonSerializationUseCase = com.productivitystreak.domain.usecase.JsonSerializationUseCase(moshi)
    }
}
