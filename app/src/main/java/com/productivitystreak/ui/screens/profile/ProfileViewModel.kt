package com.productivitystreak.ui.screens.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.repository.TimeCapsuleRepository
import com.productivitystreak.notifications.StreakReminderScheduler
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ProfileTheme
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.state.settings.ThemeMode
import com.productivitystreak.ui.utils.hapticFeedbackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.productivitystreak.notifications.TimeCapsuleDeliveryWorker
import com.productivitystreak.data.model.TimeCapsule

data class ProfileUiState(
    val profileState: ProfileState = ProfileState(),
    val settingsState: SettingsState = SettingsState(),
    val userName: String = "Alex",
    val timeCapsules: List<TimeCapsule> = emptyList(),
    val uiMessage: String? = null
)

class ProfileViewModel(
    application: Application,
    private val preferencesManager: PreferencesManager,
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val reminderScheduler: StreakReminderScheduler
) : AndroidViewModel(application) {

    private val hapticManager = application.hapticFeedbackManager()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserPreferences()
        loadSettingsPreferences()
        observeTimeCapsules()
    }

    private fun loadUserPreferences() {
        viewModelScope.launch {
            preferencesManager.userName.collect { name ->
                if (name.isNotEmpty()) _uiState.update { it.copy(userName = name) }
            }
        }
        // ... other prefs
    }

    private fun loadSettingsPreferences() {
        viewModelScope.launch {
            preferencesManager.themeMode.collect { mode ->
                val theme = when (mode) {
                    "dark" -> ProfileTheme.Dark
                    "light" -> ProfileTheme.Light
                    else -> ProfileTheme.Auto
                }
                val themeMode = when (mode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                _uiState.update { state ->
                    state.copy(
                        profileState = state.profileState.copy(theme = theme),
                        settingsState = state.settingsState.copy(themeMode = themeMode)
                    )
                }
            }
        }
        
        viewModelScope.launch {
            preferencesManager.notificationsEnabled.collect { enabled ->
                _uiState.update { state ->
                    state.copy(profileState = state.profileState.copy(notificationEnabled = enabled))
                }
            }
        }
        
        viewModelScope.launch {
            preferencesManager.hapticFeedbackEnabled.collect { enabled ->
                hapticManager.setEnabled(enabled)
                _uiState.update { state ->
                    state.copy(
                        profileState = state.profileState.copy(hapticsEnabled = enabled),
                        settingsState = state.settingsState.copy(hapticFeedbackEnabled = enabled)
                    )
                }
            }
        }
        
        // ... other settings
    }

    private fun observeTimeCapsules() {
        viewModelScope.launch {
            timeCapsuleRepository.observeTimeCapsules().collect { capsules ->
                _uiState.update { it.copy(timeCapsules = capsules) }
            }
        }
    }

    fun onToggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(profileState = state.profileState.copy(notificationEnabled = enabled))
            }
            try {
                preferencesManager.setNotificationsEnabled(enabled)
                if (enabled) {
                    // scheduleReminderForCurrentState() // TODO: Implement logic
                } else {
                    reminderScheduler.cancelReminders()
                }
            } catch (error: Exception) {
                Log.e("ProfileViewModel", "Failed to toggle notifications", error)
            }
        }
    }

    fun onToggleHaptics(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                profileState = state.profileState.copy(hapticsEnabled = enabled),
                settingsState = state.settingsState.copy(hapticFeedbackEnabled = enabled)
            )
        }
        hapticManager.setEnabled(enabled)
        viewModelScope.launch {
            preferencesManager.setHapticFeedbackEnabled(enabled)
        }
    }

    fun onChangeTheme(theme: ProfileTheme) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(theme = theme))
        }
        viewModelScope.launch {
            val themeString = when (theme) {
                ProfileTheme.Dark -> "dark"
                ProfileTheme.Light -> "light"
                ProfileTheme.Auto -> "system"
            }
            preferencesManager.setThemeMode(themeString)
        }
    }

    fun onCreateTimeCapsule(message: String, goalDescription: String, daysFromNow: Int) {
        val trimmedMessage = message.trim()
        val trimmedGoal = goalDescription.trim()
        if (trimmedMessage.isBlank() || trimmedGoal.isBlank()) {
            _uiState.update { it.copy(uiMessage = "Both the promise and the letter need to be filled.") }
            return
        }

        val safeDays = daysFromNow.coerceAtLeast(1)

        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val deliveryMillis = now + TimeUnit.DAYS.toMillis(safeDays.toLong())
                val id = timeCapsuleRepository.createTimeCapsule(
                    message = trimmedMessage,
                    deliveryDateMillis = deliveryMillis,
                    goalDescription = trimmedGoal
                )
                scheduleTimeCapsuleDelivery(id, deliveryMillis)
                _uiState.update { it.copy(uiMessage = "Time capsule scheduled") }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error creating time capsule", e)
                _uiState.update { it.copy(uiMessage = "Unable to schedule time capsule. Try again.") }
            }
        }
    }

    private fun scheduleTimeCapsuleDelivery(id: String, deliveryMillis: Long) {
        val now = System.currentTimeMillis()
        val delayMillis = (deliveryMillis - now).coerceAtLeast(0L)

        val workRequest = OneTimeWorkRequestBuilder<TimeCapsuleDeliveryWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(TimeCapsuleDeliveryWorker.createInputData(id))
            .build()

        val workManager = WorkManager.getInstance(getApplication())
        workManager.enqueueUniqueWork(
            "time_capsule_$id",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun onSettingsThemeChange(mode: ThemeMode) {
        val theme = when (mode) {
            ThemeMode.LIGHT -> ProfileTheme.Light
            ThemeMode.DARK -> ProfileTheme.Dark
            ThemeMode.SYSTEM -> ProfileTheme.Auto
        }
        onChangeTheme(theme)
    }

    fun onSettingsDailyRemindersToggle(enabled: Boolean) {
        onToggleNotifications(enabled)
    }

    fun onSettingsWeeklyBackupsToggle(enabled: Boolean) {
        onToggleWeeklySummary(enabled)
    }

    fun onSettingsReminderTimeChange(time: String) {
        viewModelScope.launch {
            preferencesManager.setReminderTime(time)
            _uiState.update { state ->
                state.copy(settingsState = state.settingsState.copy(reminderTime = time))
            }
            if (_uiState.value.profileState.notificationEnabled) {
                scheduleReminderForCurrentState()
            }
        }
    }

    fun onSettingsHapticFeedbackToggle(enabled: Boolean) {
        onToggleHaptics(enabled)
    }

    fun onSettingsCreateBackup() {
        // TODO: Implement backup creation
        _uiState.update { it.copy(uiMessage = "Backup created (Simulated)") }
    }

    fun onSettingsRestoreBackup() {
        // TODO: Show restore dialog
    }

    fun onSettingsRestoreFromFile(uri: android.net.Uri) {
        // TODO: Implement restore from file
        _uiState.update { it.copy(uiMessage = "Restore started (Simulated)") }
    }

    fun onSettingsDismissRestoreDialog() {
        // TODO: Hide restore dialog
    }

    fun onSettingsDismissMessage() {
        clearUiMessage()
    }

    fun onChangeReminderFrequency(frequency: ReminderFrequency) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(reminderFrequency = frequency))
        }
        viewModelScope.launch {
            val freqString = when (frequency) {
                ReminderFrequency.Daily -> "daily"
                ReminderFrequency.Weekly -> "weekly"
                ReminderFrequency.None -> "none"
            }
            preferencesManager.setReminderFrequency(freqString)
            if (_uiState.value.profileState.notificationEnabled) {
                scheduleReminderForCurrentState()
            }
        }
    }

    fun onToggleWeeklySummary(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                profileState = state.profileState.copy(hasWeeklySummary = enabled),
                settingsState = state.settingsState.copy(weeklyBackupsEnabled = enabled)
            )
        }
        viewModelScope.launch {
            preferencesManager.setWeeklySummaryEnabled(enabled)
        }
    }

    fun onSaveTimeCapsuleReflection(id: String, reflection: String) {
        viewModelScope.launch {
            try {
                timeCapsuleRepository.saveReflection(id, reflection)
                _uiState.update { it.copy(uiMessage = "Reflection saved") }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving reflection", e)
                _uiState.update { it.copy(uiMessage = "Error saving reflection") }
            }
        }
    }

    private fun scheduleReminderForCurrentState() {
        val state = _uiState.value
        if (!state.profileState.notificationEnabled) return

        reminderScheduler.scheduleReminder(
            frequency = state.profileState.reminderFrequency,
            categories = state.profileState.activeCategories,
            userName = state.userName
        )
    }

}
