package com.productivitystreak.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.local.AppDatabase
import com.productivitystreak.data.local.UserPreferencesDataStore
import com.productivitystreak.data.repository.StreakRepository
import com.productivitystreak.notifications.StreakReminderScheduler

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            val database = AppDatabase.getInstance(application)
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(
                application = application,
                quoteRepository = QuoteRepository(),
                streakRepository = StreakRepository(database.streakDao()),
                reminderScheduler = StreakReminderScheduler(application),
                userPreferencesDataStore = UserPreferencesDataStore(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
