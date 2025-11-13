package com.productivitystreak.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.productivitystreak.ui.state.profile.ProfileTheme
import com.productivitystreak.ui.state.profile.ReminderFrequency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val userName: String = "Alex",
    val userEmail: String = "alex@neverzero.app",
    val theme: ProfileTheme = ProfileTheme.Auto,
    val notificationsEnabled: Boolean = true,
    val reminderFrequency: ReminderFrequency = ReminderFrequency.Daily,
    val weeklySummaryEnabled: Boolean = true,
    val selectedCategories: Set<String> = setOf("Reading", "Vocabulary", "Wellness"),
    val hasCompletedOnboarding: Boolean = false,
    val showOnboarding: Boolean = true
)

class UserPreferencesDataStore(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_REMINDER_FREQUENCY = stringPreferencesKey("reminder_frequency")
        private val KEY_WEEKLY_SUMMARY = booleanPreferencesKey("weekly_summary_enabled")
        private val KEY_SELECTED_CATEGORIES = stringSetPreferencesKey("selected_categories")
        private val KEY_COMPLETED_ONBOARDING = booleanPreferencesKey("completed_onboarding")
        private val KEY_SHOW_ONBOARDING = booleanPreferencesKey("show_onboarding")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                userName = preferences[KEY_USER_NAME] ?: "Alex",
                userEmail = preferences[KEY_USER_EMAIL] ?: "alex@neverzero.app",
                theme = ProfileTheme.valueOf(
                    preferences[KEY_THEME] ?: ProfileTheme.Auto.name
                ),
                notificationsEnabled = preferences[KEY_NOTIFICATIONS_ENABLED] ?: true,
                reminderFrequency = ReminderFrequency.valueOf(
                    preferences[KEY_REMINDER_FREQUENCY] ?: ReminderFrequency.Daily.name
                ),
                weeklySummaryEnabled = preferences[KEY_WEEKLY_SUMMARY] ?: true,
                selectedCategories = preferences[KEY_SELECTED_CATEGORIES]
                    ?: setOf("Reading", "Vocabulary", "Wellness"),
                hasCompletedOnboarding = preferences[KEY_COMPLETED_ONBOARDING] ?: false,
                showOnboarding = preferences[KEY_SHOW_ONBOARDING] ?: true
            )
        }

    suspend fun updateUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = name
        }
    }

    suspend fun updateUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_EMAIL] = email
        }
    }

    suspend fun updateTheme(theme: ProfileTheme) {
        dataStore.edit { preferences ->
            preferences[KEY_THEME] = theme.name
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateReminderFrequency(frequency: ReminderFrequency) {
        dataStore.edit { preferences ->
            preferences[KEY_REMINDER_FREQUENCY] = frequency.name
        }
    }

    suspend fun updateWeeklySummary(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_WEEKLY_SUMMARY] = enabled
        }
    }

    suspend fun updateSelectedCategories(categories: Set<String>) {
        dataStore.edit { preferences ->
            preferences[KEY_SELECTED_CATEGORIES] = categories
        }
    }

    suspend fun completeOnboarding() {
        dataStore.edit { preferences ->
            preferences[KEY_COMPLETED_ONBOARDING] = true
            preferences[KEY_SHOW_ONBOARDING] = false
        }
    }

    suspend fun dismissOnboarding() {
        dataStore.edit { preferences ->
            preferences[KEY_SHOW_ONBOARDING] = false
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
