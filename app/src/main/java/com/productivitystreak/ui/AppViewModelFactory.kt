package com.productivitystreak.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.productivitystreak.NeverZeroApplication
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.notifications.StreakReminderScheduler
import com.productivitystreak.ui.screens.discover.DiscoverViewModel
import com.productivitystreak.ui.screens.journal.JournalViewModel
import com.productivitystreak.ui.screens.onboarding.OnboardingViewModel
import com.productivitystreak.ui.screens.profile.ProfileViewModel
import com.productivitystreak.ui.screens.reading.ReadingViewModel
import com.productivitystreak.ui.screens.stats.StreakViewModel
import com.productivitystreak.ui.screens.vocabulary.VocabularyViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val app = application as NeverZeroApplication
        
        return when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> {
                AppViewModel(
                    application = application,
                    quoteRepository = app.quoteRepository,
                    preferencesManager = app.preferencesManager,
                    streakRepository = app.streakRepository,
                    templateRepository = app.templateRepository
                ) as T
            }
            modelClass.isAssignableFrom(StreakViewModel::class.java) -> {
                StreakViewModel(
                    streakRepository = app.streakRepository,
                    preferencesManager = app.preferencesManager,
                    moshi = moshi,
                    geminiClient = app.geminiClient,
                    socialRepository = app.socialRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    application = application,
                    preferencesManager = app.preferencesManager,
                    timeCapsuleRepository = app.timeCapsuleRepository,
                    reminderScheduler = StreakReminderScheduler(application)
                ) as T
            }
            modelClass.isAssignableFrom(VocabularyViewModel::class.java) -> {
                VocabularyViewModel(
                    preferencesManager = app.preferencesManager,
                    moshi = moshi,
                    geminiClient = app.geminiClient
                ) as T
            }
            modelClass.isAssignableFrom(ReadingViewModel::class.java) -> {
                ReadingViewModel(
                    preferencesManager = app.preferencesManager,
                    moshi = moshi
                ) as T
            }
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(
                    preferencesManager = app.preferencesManager,
                    streakRepository = app.streakRepository,
                    reminderScheduler = StreakReminderScheduler(application),
                    geminiClient = app.geminiClient
                ) as T
            }
            modelClass.isAssignableFrom(DiscoverViewModel::class.java) -> {
                DiscoverViewModel(
                    assetRepository = app.assetRepository,
                    preferencesManager = app.preferencesManager
                ) as T
            }
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> {
                JournalViewModel(
                    reflectionRepository = app.reflectionRepository,
                    geminiClient = app.geminiClient
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
