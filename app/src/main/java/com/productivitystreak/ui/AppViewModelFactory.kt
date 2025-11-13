package com.productivitystreak.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.productivitystreak.NeverZeroApplication
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.notifications.StreakReminderScheduler

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val app = application as NeverZeroApplication
            return AppViewModel(
                application = application,
                quoteRepository = QuoteRepository(),
                streakRepository = app.streakRepository,
                reminderScheduler = StreakReminderScheduler(application),
                vocabularyRepository = app.vocabularyRepository,
                bookRepository = app.bookRepository,
                reflectionRepository = app.reflectionRepository,
                achievementRepository = app.achievementRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
