package com.productivitystreak.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class PreferencesManager(context: Context) {
    private val dataStore = context.dataStore

    // Preference keys
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val REMINDER_FREQUENCY = stringPreferencesKey("reminder_frequency")
        val WEEKLY_SUMMARY_ENABLED = booleanPreferencesKey("weekly_summary_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
        val SOUND_EFFECTS_ENABLED = booleanPreferencesKey("sound_effects_enabled")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val STREAK_FREEZE_REMINDERS = booleanPreferencesKey("streak_freeze_reminders")
        val SHOW_PROGRESS_WIDGETS = booleanPreferencesKey("show_progress_widgets")
        val DEFAULT_VIEW = stringPreferencesKey("default_view")
        val STATS_PERIOD = stringPreferencesKey("stats_period")
        val USER_NAME = stringPreferencesKey("user_name")
        val TOTAL_POINTS = intPreferencesKey("total_points")

        // Reading Tracker
        val READING_STREAK_DAYS = intPreferencesKey("reading_streak_days")
        val PAGES_READ_TODAY = intPreferencesKey("pages_read_today")
        val READING_GOAL_PAGES = intPreferencesKey("reading_goal_pages")
        val READING_LAST_DATE = stringPreferencesKey("reading_last_date")
        val READING_ACTIVITY = stringPreferencesKey("reading_activity")

        // Vocabulary
        val VOCABULARY_STREAK_DAYS = intPreferencesKey("vocabulary_streak_days")
        val WORDS_ADDED_TODAY = intPreferencesKey("words_added_today")
        val VOCABULARY_LAST_DATE = stringPreferencesKey("vocabulary_last_date")
        val VOCABULARY_WORDS = stringPreferencesKey("vocabulary_words")

        // Achievements
        val ACHIEVEMENTS_DATA = stringPreferencesKey("achievements_data")
    }

    // Theme mode
    val themeMode: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: "system"
        }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    // Notifications
    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // Reminder time
    val reminderTime: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME] ?: "09:00"
        }

    suspend fun setReminderTime(time: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME] = time
        }
    }

    // Reminder frequency
    val reminderFrequency: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.REMINDER_FREQUENCY] ?: "daily"
        }

    suspend fun setReminderFrequency(frequency: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_FREQUENCY] = frequency
        }
    }

    val weeklySummaryEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.WEEKLY_SUMMARY_ENABLED] ?: true
        }

    suspend fun setWeeklySummaryEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEEKLY_SUMMARY_ENABLED] = enabled
        }
    }

    // Onboarding
    val onboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    // App lock
    val appLockEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.APP_LOCK_ENABLED] ?: false
        }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LOCK_ENABLED] = enabled
        }
    }

    // Biometric auth
    val biometricEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] ?: false
        }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = enabled
        }
    }

    // Haptic feedback
    val hapticFeedbackEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] ?: true
        }

    suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] = enabled
        }
    }

    // Sound effects
    val soundEffectsEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SOUND_EFFECTS_ENABLED] ?: true
        }

    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_EFFECTS_ENABLED] = enabled
        }
    }

    // Daily reminder
    val dailyReminderEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] ?: true
        }

    suspend fun setDailyReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] = enabled
        }
    }

    // User name
    val userName: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: ""
        }

    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    // Total points
    val totalPoints: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.TOTAL_POINTS] ?: 0
        }

    suspend fun addPoints(points: Int) {
        dataStore.edit { preferences ->
            val currentPoints = preferences[PreferencesKeys.TOTAL_POINTS] ?: 0
            preferences[PreferencesKeys.TOTAL_POINTS] = currentPoints + points
        }
    }

    suspend fun resetPoints() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOTAL_POINTS] = 0
        }
    }

    // Clear all preferences
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Reading Tracker
    val readingStreakDays: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.READING_STREAK_DAYS] ?: 0
        }

    suspend fun setReadingStreakDays(days: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.READING_STREAK_DAYS] = days
        }
    }

    val pagesReadToday: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.PAGES_READ_TODAY] ?: 0
        }

    suspend fun setPagesReadToday(pages: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAGES_READ_TODAY] = pages
        }
    }

    val readingGoalPages: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.READING_GOAL_PAGES] ?: 30
        }

    suspend fun setReadingGoalPages(pages: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.READING_GOAL_PAGES] = pages
        }
    }

    val readingLastDate: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.READING_LAST_DATE] ?: ""
        }

    suspend fun setReadingLastDate(date: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.READING_LAST_DATE] = date
        }
    }

    val readingActivity: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.READING_ACTIVITY] ?: "[]"
        }

    suspend fun setReadingActivity(activityJson: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.READING_ACTIVITY] = activityJson
        }
    }

    // Vocabulary
    val vocabularyStreakDays: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.VOCABULARY_STREAK_DAYS] ?: 0
        }

    suspend fun setVocabularyStreakDays(days: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOCABULARY_STREAK_DAYS] = days
        }
    }

    val wordsAddedToday: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.WORDS_ADDED_TODAY] ?: 0
        }

    suspend fun setWordsAddedToday(words: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORDS_ADDED_TODAY] = words
        }
    }

    val vocabularyLastDate: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.VOCABULARY_LAST_DATE] ?: ""
        }

    suspend fun setVocabularyLastDate(date: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOCABULARY_LAST_DATE] = date
        }
    }

    val vocabularyWords: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.VOCABULARY_WORDS] ?: "[]"
        }

    suspend fun setVocabularyWords(wordsJson: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOCABULARY_WORDS] = wordsJson
        }
    }

    // Achievements
    val achievementsData: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.ACHIEVEMENTS_DATA] ?: "[]"
        }

    suspend fun setAchievementsData(achievementsJson: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACHIEVEMENTS_DATA] = achievementsJson
        }
    }
}
