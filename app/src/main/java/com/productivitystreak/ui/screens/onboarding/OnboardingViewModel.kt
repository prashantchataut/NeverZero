package com.productivitystreak.ui.screens.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.repository.StreakRepository
import com.productivitystreak.data.repository.onSuccess
import com.productivitystreak.notifications.StreakReminderScheduler
import com.productivitystreak.ui.state.onboarding.OnboardingState
import com.productivitystreak.ui.state.profile.ReminderFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.productivitystreak.data.gemini.GeminiClient

class OnboardingViewModel(
    private val preferencesManager: PreferencesManager,
    private val streakRepository: StreakRepository,
    private val reminderScheduler: StreakReminderScheduler,
    private val geminiClient: GeminiClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()
    
    private val _showOnboarding = MutableStateFlow(true)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadOnboardingState()
    }

    private fun loadOnboardingState() {
        viewModelScope.launch {
            preferencesManager.onboardingCompleted.collect { completed ->
                _showOnboarding.value = !completed
                _isLoading.value = false
            }
        }
        // Load other onboarding prefs if needed
    }

    fun onSetOnboardingGoal(goal: String) {
        _uiState.update { it.copy(goalHabit = goal) }
        viewModelScope.launch {
            preferencesManager.setOnboardingGoal(goal)
        }
    }

    fun onSetOnboardingCommitment(durationMinutes: Int, frequencyPerWeek: Int) {
        _uiState.update {
            it.copy(
                commitmentDurationMinutes = durationMinutes,
                commitmentFrequencyPerWeek = frequencyPerWeek
            )
        }
        viewModelScope.launch {
            preferencesManager.setOnboardingCommitmentDuration(durationMinutes)
            preferencesManager.setOnboardingCommitmentFrequency(frequencyPerWeek)
        }
    }

    fun onToggleOnboardingCategory(category: String) {
        _uiState.update { state ->
            val current = state.selectedCategories
            val updated = if (current.contains(category) && current.size > 1) {
                current - category
            } else {
                current + category
            }
            state.copy(selectedCategories = updated)
        }
        
        // Debounce or just trigger suggestion generation
        generateHabitSuggestions()
    }

    private fun generateHabitSuggestions() {
        val categories = _uiState.value.selectedCategories.joinToString(", ")
        if (categories.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingSuggestions = true) }
            val suggestions = geminiClient.generateHabitSuggestions(categories)
            _uiState.update { it.copy(isGeneratingSuggestions = false, habitSuggestions = suggestions) }
        }
    }

    fun onNextOnboardingStep() {
        _uiState.update { state ->
            val next = (state.currentStep + 1).coerceAtMost(state.totalSteps - 1)
            state.copy(currentStep = next)
        }
    }

    fun onPreviousOnboardingStep() {
        _uiState.update { state ->
            val previous = (state.currentStep - 1).coerceAtLeast(0)
            state.copy(currentStep = previous)
        }
    }

    fun onToggleOnboardingNotifications(enabled: Boolean) {
        _uiState.update { it.copy(allowNotifications = enabled) }
    }

    fun onSetOnboardingReminderTime(time: String) {
        _uiState.update { it.copy(reminderTime = time) }
    }

    fun onUserNameChange(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    fun onHabitNameChange(name: String) {
        _uiState.update { it.copy(goalHabit = name) }
    }

    fun onIconSelected(icon: String) {
        _uiState.update { it.copy(selectedIcon = icon) }
    }

    fun onCompleteOnboarding() {
        val snapshot = _uiState.value
        _uiState.update { it.copy(hasCompleted = true) }
        _showOnboarding.value = false

        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted(true)
            preferencesManager.setUserName(snapshot.userName)
            // Save profile photo URI if we had one
        }

        seedInitialHabitFromOnboarding(snapshot)

        if (snapshot.allowNotifications) {
            reminderScheduler.scheduleReminder(
                frequency = deriveReminderFrequency(snapshot.commitmentFrequencyPerWeek),
                categories = snapshot.selectedCategories,
                userName = snapshot.userName
            )
        }
    }

    private fun seedInitialHabitFromOnboarding(stateSnapshot: OnboardingState) {
        val goal = stateSnapshot.goalHabit.trim()
        if (goal.isBlank()) return

        viewModelScope.launch {
            val minutes = stateSnapshot.commitmentDurationMinutes.coerceAtLeast(1)
            val category = stateSnapshot.selectedCategories.firstOrNull() ?: "Focus"
            val result = streakRepository.createStreak(
                name = goal,
                goalPerDay = minutes,
                unit = "minutes",
                category = category,
                icon = stateSnapshot.selectedIcon
            )
            // Handle result if needed
        }
    }
    
    private fun deriveReminderFrequency(frequencyPerWeek: Int): ReminderFrequency {
        return if (frequencyPerWeek >= 7) ReminderFrequency.Daily else ReminderFrequency.Weekly
    }
}
