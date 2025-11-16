package com.productivitystreak.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.NeverZeroApplication
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.model.Streak
import com.productivitystreak.data.repository.StreakRepository
import com.productivitystreak.data.repository.onSuccess
import com.productivitystreak.notifications.StreakReminderScheduler
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.state.settings.ThemeMode
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.state.discover.CategoryItem
import com.productivitystreak.ui.state.discover.ChallengeItem
import com.productivitystreak.ui.state.discover.DiscoverState
import com.productivitystreak.ui.state.discover.FeaturedContent
import com.productivitystreak.ui.state.discover.SuggestionItem
import com.productivitystreak.ui.state.onboarding.OnboardingState
import com.productivitystreak.ui.state.UiMessage
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ProfileTheme
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.reading.ReadingLog
import com.productivitystreak.ui.state.reading.ReadingTrackerState
import com.productivitystreak.ui.state.stats.CalendarHeatMap
import com.productivitystreak.ui.state.stats.HabitBreakdown
import com.productivitystreak.ui.state.stats.HeatMapDay
import com.productivitystreak.ui.state.stats.HeatMapWeek
import com.productivitystreak.ui.state.stats.LeaderboardEntry
import com.productivitystreak.ui.state.stats.StatsState
import com.productivitystreak.ui.state.stats.AverageDailyTrend
import com.productivitystreak.ui.state.stats.ConsistencyLevel
import com.productivitystreak.ui.state.stats.ConsistencyScore
import com.productivitystreak.ui.state.stats.TrendPoint
import com.productivitystreak.ui.state.vocabulary.VocabularyState
import com.productivitystreak.ui.state.vocabulary.VocabularyWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.productivitystreak.ui.utils.hapticFeedbackManager
import kotlin.math.roundToInt

class AppViewModel(
    application: Application,
    private val quoteRepository: QuoteRepository,
    private val streakRepository: StreakRepository,
    private val reminderScheduler: StreakReminderScheduler,
    private val preferencesManager: PreferencesManager
) : AndroidViewModel(application) {

    private val hapticManager = application.hapticFeedbackManager()

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var quoteRefreshJob: Job? = null

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    init {
        loadUserPreferences()
        loadReadingTrackerData()
        loadVocabularyData()
        loadSettingsPreferences()
        bootstrapStaticState()
        observeStreaks()
        refreshQuote()
    }

    fun onSetOnboardingGoal(goal: String) {
        _uiState.update { state ->
            state.copy(onboardingState = state.onboardingState.copy(goalHabit = goal))
        }

        viewModelScope.launch {
            try {
                preferencesManager.setOnboardingGoal(goal)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving onboarding goal", e)
            }
        }
    }

    fun onSetOnboardingCommitment(durationMinutes: Int, frequencyPerWeek: Int) {
        _uiState.update { state ->
            state.copy(
                onboardingState = state.onboardingState.copy(
                    commitmentDurationMinutes = durationMinutes,
                    commitmentFrequencyPerWeek = frequencyPerWeek
                )
            )
        }

        viewModelScope.launch {
            try {
                preferencesManager.setOnboardingCommitmentDuration(durationMinutes)
                preferencesManager.setOnboardingCommitmentFrequency(frequencyPerWeek)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving onboarding commitment", e)
            }
        }
    }

    private fun loadUserPreferences() {
        viewModelScope.launch {
            try {
                // Load user name
                preferencesManager.userName.collect { name ->
                    if (name.isNotEmpty()) {
                        _uiState.update { it.copy(userName = name) }
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading user name", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load onboarding completion status
                preferencesManager.onboardingCompleted.collect { completed ->
                    _uiState.update { it.copy(showOnboarding = !completed) }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading onboarding status", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.onboardingGoal.collect { goal ->
                    _uiState.update { state ->
                        state.copy(onboardingState = state.onboardingState.copy(goalHabit = goal))
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading onboarding goal", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.onboardingCommitmentDuration.collect { minutes ->
                    _uiState.update { state ->
                        state.copy(onboardingState = state.onboardingState.copy(commitmentDurationMinutes = minutes))
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading commitment duration", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.onboardingCommitmentFrequency.collect { frequency ->
                    _uiState.update { state ->
                        state.copy(onboardingState = state.onboardingState.copy(commitmentFrequencyPerWeek = frequency))
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading commitment frequency", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load theme mode
                preferencesManager.themeMode.collect { mode ->
                    val theme = when (mode) {
                        "dark" -> ProfileTheme.Dark
                        "light" -> ProfileTheme.Light
                        else -> ProfileTheme.Auto
                    }
                    _uiState.update { state ->
                        state.copy(profileState = state.profileState.copy(theme = theme))
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading theme", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load notifications enabled
                preferencesManager.notificationsEnabled.collect { enabled ->
                    _uiState.update { state ->
                        state.copy(profileState = state.profileState.copy(notificationEnabled = enabled))
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading notifications", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.weeklySummaryEnabled.collect { enabled ->
                    _uiState.update { state ->
                        state.copy(profileState = state.profileState.copy(hasWeeklySummary = enabled))
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading weekly summary preference", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.hapticFeedbackEnabled.collect { enabled ->
                    hapticManager.setEnabled(enabled)
                    _uiState.update { state ->
                        state.copy(profileState = state.profileState.copy(hapticsEnabled = enabled))
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading haptic preference", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load reminder frequency
                preferencesManager.reminderFrequency.collect { frequency ->
                    val freq = when (frequency) {
                        "daily" -> ReminderFrequency.Daily
                        "weekly" -> ReminderFrequency.Weekly
                        else -> ReminderFrequency.None
                    }
                    _uiState.update { state ->
                        state.copy(profileState = state.profileState.copy(reminderFrequency = freq))
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading reminder frequency", e)
            }
        }
    }

    private fun loadReadingTrackerData() {
        viewModelScope.launch {
            try {
                // Load reading streak days
                preferencesManager.readingStreakDays.collect { streakDays ->
                    _uiState.update { state ->
                        state.copy(
                            readingTrackerState = state.readingTrackerState.copy(
                                currentStreakDays = streakDays
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading reading streak days", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load pages read today and check if we need to reset
                preferencesManager.readingLastDate.collect { lastDate ->
                    val today = LocalDate.now().toString()
                    if (lastDate != today) {
                        // New day, reset pages
                        preferencesManager.setPagesReadToday(0)
                        preferencesManager.setReadingLastDate(today)
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error checking reading date", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.pagesReadToday.collect { pages ->
                    _uiState.update { state ->
                        state.copy(
                            readingTrackerState = state.readingTrackerState.copy(
                                pagesReadToday = pages
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading pages read today", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.readingGoalPages.collect { goal ->
                    _uiState.update { state ->
                        state.copy(
                            readingTrackerState = state.readingTrackerState.copy(
                                goalPagesPerDay = goal
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading reading goal", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.readingActivity.collect { activityJson ->
                    val type = Types.newParameterizedType(List::class.java, ReadingLog::class.java)
                    val adapter = moshi.adapter<List<ReadingLog>>(type)
                    val activity = try {
                        adapter.fromJson(activityJson) ?: emptyList()
                    } catch (e: Exception) {
                        Log.e("AppViewModel", "Error parsing reading activity", e)
                        emptyList()
                    }
                    _uiState.update { state ->
                        state.copy(
                            readingTrackerState = state.readingTrackerState.copy(
                                recentActivity = activity
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading reading activity", e)
            }
        }
    }

    private fun loadVocabularyData() {
        viewModelScope.launch {
            try {
                // Load vocabulary streak days
                preferencesManager.vocabularyStreakDays.collect { streakDays ->
                    _uiState.update { state ->
                        state.copy(
                            vocabularyState = state.vocabularyState.copy(
                                currentStreakDays = streakDays
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading vocabulary streak days", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load words added today and check if we need to reset
                preferencesManager.vocabularyLastDate.collect { lastDate ->
                    val today = LocalDate.now().toString()
                    if (lastDate != today) {
                        // New day, reset count
                        preferencesManager.setWordsAddedToday(0)
                        preferencesManager.setVocabularyLastDate(today)
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error checking vocabulary date", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.wordsAddedToday.collect { count ->
                    _uiState.update { state ->
                        state.copy(
                            vocabularyState = state.vocabularyState.copy(
                                wordsAddedToday = count
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading words added today", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.vocabularyWords.collect { wordsJson ->
                    val type = Types.newParameterizedType(List::class.java, VocabularyWord::class.java)
                    val adapter = moshi.adapter<List<VocabularyWord>>(type)
                    val words = try {
                        adapter.fromJson(wordsJson) ?: emptyList()
                    } catch (e: Exception) {
                        Log.e("AppViewModel", "Error parsing vocabulary words", e)
                        emptyList()
                    }
                    _uiState.update { state ->
                        state.copy(
                            vocabularyState = state.vocabularyState.copy(
                                words = words
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading vocabulary words", e)
            }
        }
    }

    private fun loadSettingsPreferences() {
        viewModelScope.launch {
            try {
                preferencesManager.themeMode.collect { mode ->
                    val themeMode = when (mode) {
                        "light" -> com.productivitystreak.ui.state.settings.ThemeMode.LIGHT
                        "dark" -> com.productivitystreak.ui.state.settings.ThemeMode.DARK
                        else -> com.productivitystreak.ui.state.settings.ThemeMode.SYSTEM
                    }
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(themeMode = themeMode)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading settings theme", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.dailyReminderEnabled.collect { enabled ->
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(dailyRemindersEnabled = enabled)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading daily reminders setting", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.weeklySummaryEnabled.collect { enabled ->
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(weeklyBackupsEnabled = enabled)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading weekly backups setting", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.reminderTime.collect { time ->
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(reminderTime = time)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading reminder time", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.hapticFeedbackEnabled.collect { enabled ->
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(hapticFeedbackEnabled = enabled)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error loading haptic feedback setting", e)
            }
        }
    }

    fun refreshQuote() {
        quoteRefreshJob?.cancel()
        quoteRefreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isQuoteLoading = true, uiMessage = null) }
            try {
                val quote = quoteRepository.getDailyQuote(tags = "motivational|success")
                _uiState.update { state ->
                    state.copy(
                        quote = quote,
                        isQuoteLoading = false
                    )
                }
            } catch (error: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isQuoteLoading = false,
                        uiMessage = UiMessage(
                            text = error.message ?: "Unable to load quote",
                            isBlocking = false,
                            actionLabel = "Retry"
                        )
                    )
                }
            }
        }
    }

    fun onSelectStreak(streakId: String) {
        _uiState.update { it.copy(selectedStreakId = streakId) }
    }

    fun onToggleTask(taskId: String) {
        val snapshot = _uiState.value
        val task = snapshot.todayTasks.find { it.id == taskId } ?: return
        if (task.isCompleted) return

        val streak = snapshot.streaks.find { it.id == task.streakId }
        if (streak == null) {
            Log.w("AppViewModel", "Streak not found for task ${task.streakId}")
            return
        }

        _uiState.update { state ->
            state.copy(
                todayTasks = state.todayTasks.map { current ->
                    if (current.id == taskId) current.copy(isCompleted = true) else current
                }
            )
        }

        viewModelScope.launch {
            try {
                streakRepository.logProgress(task.streakId, streak.goalPerDay)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error logging progress for streak ${task.streakId}", e)
                _uiState.update { state ->
                    state.copy(todayTasks = buildTasksForStreaks(state.streaks))
                }
            }
        }
    }

    fun onToggleOnboardingCategory(category: String) {
        _uiState.update { state ->
            val current = state.onboardingState.selectedCategories
            val updated = if (current.contains(category) && current.size > 1) {
                current - category
            } else {
                current + category
            }
            state.copy(
                onboardingState = state.onboardingState.copy(selectedCategories = updated),
                profileState = state.profileState.copy(activeCategories = updated)
            )
        }
    }

    fun onNextOnboardingStep() {
        _uiState.update { state ->
            val next = (state.onboardingState.currentStep + 1).coerceAtMost(state.onboardingState.totalSteps - 1)
            state.copy(onboardingState = state.onboardingState.copy(currentStep = next))
        }
    }

    fun onPreviousOnboardingStep() {
        _uiState.update { state ->
            val previous = (state.onboardingState.currentStep - 1).coerceAtLeast(0)
            state.copy(onboardingState = state.onboardingState.copy(currentStep = previous))
        }
    }

    fun onToggleOnboardingNotifications(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(onboardingState = state.onboardingState.copy(allowNotifications = enabled))
        }
    }

    fun onSetOnboardingReminderTime(time: String) {
        _uiState.update { state ->
            state.copy(onboardingState = state.onboardingState.copy(reminderTime = time))
        }
    }

    fun onDismissOnboarding() {
        _uiState.update { state ->
            state.copy(showOnboarding = false)
        }
    }

    fun onCompleteOnboarding() {
        val snapshot = _uiState.value
        _uiState.update { state ->
            state.copy(
                showOnboarding = false,
                onboardingState = state.onboardingState.copy(hasCompleted = true)
            )
        }

        // Save onboarding completion to PreferencesManager
        viewModelScope.launch {
            try {
                preferencesManager.setOnboardingCompleted(true)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving onboarding completion", e)
            }
        }

        if (snapshot.onboardingState.allowNotifications) {
            onToggleNotifications(true)
        }

        seedInitialHabitFromOnboarding(snapshot)

        if (snapshot.profileState.notificationEnabled || snapshot.onboardingState.allowNotifications) {
            reminderScheduler.scheduleReminder(
                frequency = deriveReminderFrequency(snapshot.onboardingState.commitmentFrequencyPerWeek),
                categories = snapshot.onboardingState.selectedCategories,
                userName = snapshot.userName
            )
        }
    }

    private fun deriveReminderFrequency(frequencyPerWeek: Int): ReminderFrequency {
        return if (frequencyPerWeek >= 5) ReminderFrequency.Daily else ReminderFrequency.Weekly
    }

    private fun seedInitialHabitFromOnboarding(stateSnapshot: AppUiState) {
        if (stateSnapshot.streaks.isNotEmpty()) return
        val goal = stateSnapshot.onboardingState.goalHabit.trim()
        if (goal.isBlank()) return

        viewModelScope.launch {
            val minutes = stateSnapshot.onboardingState.commitmentDurationMinutes.coerceAtLeast(1)
            val category = stateSnapshot.onboardingState.selectedCategories.firstOrNull() ?: "Focus"
            val result = streakRepository.createStreak(
                name = goal,
                goalPerDay = minutes,
                unit = "minutes",
                category = category
            )
            result.onSuccess { id ->
                _uiState.update { state ->
                    state.copy(selectedStreakId = id)
                }
            }
        }
    }

    fun onShowNotificationPermissionDialog() {
        _uiState.update { state ->
            state.copy(permissionState = state.permissionState.copy(showNotificationDialog = true))
        }
    }

    fun onDismissNotificationPermissionDialog() {
        _uiState.update { state ->
            state.copy(permissionState = state.permissionState.copy(showNotificationDialog = false))
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
                    scheduleReminderForCurrentState()
                } else {
                    reminderScheduler.cancelReminders()
                }
            } catch (error: Exception) {
                Log.e("AppViewModel", "Failed to toggle notifications", error)
                _uiState.update { state ->
                    state.copy(
                        profileState = state.profileState.copy(notificationEnabled = !enabled),
                        uiMessage = UiMessage(
                            text = error.message ?: "Unable to update notifications",
                            actionLabel = "Retry"
                        )
                    )
                }
            }
        }
    }

    fun onChangeReminderFrequency(frequency: ReminderFrequency) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(reminderFrequency = frequency))
        }

        // Save to PreferencesManager
        viewModelScope.launch {
            try {
                val freqString = when (frequency) {
                    ReminderFrequency.Daily -> "daily"
                    ReminderFrequency.Weekly -> "weekly"
                    ReminderFrequency.None -> "none"
                }
                preferencesManager.setReminderFrequency(freqString)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving reminder frequency", e)
            }
        }

        val current = _uiState.value
        if (current.profileState.notificationEnabled) {
            scheduleReminderForCurrentState(frequency)
        }
    }

    fun onToggleWeeklySummary(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(hasWeeklySummary = enabled))
        }

        viewModelScope.launch {
            try {
                preferencesManager.setWeeklySummaryEnabled(enabled)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving weekly summary preference", e)
                _uiState.update { state ->
                    state.copy(
                        profileState = state.profileState.copy(hasWeeklySummary = !enabled),
                        uiMessage = UiMessage(
                            text = "Couldn't update weekly summary",
                            actionLabel = "Retry"
                        )
                    )
                }
            }
        }
    }

    fun onToggleHaptics(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(hapticsEnabled = enabled))
        }

        hapticManager.setEnabled(enabled)

        viewModelScope.launch {
            try {
                preferencesManager.setHapticFeedbackEnabled(enabled)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving haptic preference", e)
            }
        }
    }

    fun onChangeTheme(theme: ProfileTheme) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(theme = theme))
        }

        // Save to PreferencesManager
        viewModelScope.launch {
            try {
                val themeString = when (theme) {
                    ProfileTheme.Dark -> "dark"
                    ProfileTheme.Light -> "light"
                    ProfileTheme.Auto -> "system"
                }
                preferencesManager.setThemeMode(themeString)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving theme preference", e)
            }
        }
    }

    fun onLogReadingProgress(pages: Int) {
        if (pages <= 0) return
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        val today = LocalDate.now()

        viewModelScope.launch {
            try {
                val currentState = _uiState.value.readingTrackerState
                val updatedPages = currentState.pagesReadToday + pages
                val newLog = ReadingLog(
                    dateLabel = today.format(formatter),
                    pages = pages
                )
                val updatedActivity = (listOf(newLog) + currentState.recentActivity).take(7)

                // Check if we should increment streak (met goal)
                val shouldIncrementStreak = updatedPages >= currentState.goalPagesPerDay
                val updatedStreakDays = if (shouldIncrementStreak) {
                    currentState.currentStreakDays + 1
                } else {
                    currentState.currentStreakDays
                }

                // Update UI state
                _uiState.update { state ->
                    state.copy(
                        readingTrackerState = state.readingTrackerState.copy(
                            pagesReadToday = updatedPages,
                            recentActivity = updatedActivity,
                            currentStreakDays = updatedStreakDays
                        )
                    )
                }

                // Save to PreferencesManager
                preferencesManager.setPagesReadToday(updatedPages)
                preferencesManager.setReadingLastDate(today.toString())
                if (shouldIncrementStreak) {
                    preferencesManager.setReadingStreakDays(updatedStreakDays)
                }

                // Save activity log
                val type = Types.newParameterizedType(List::class.java, ReadingLog::class.java)
                val adapter = moshi.adapter<List<ReadingLog>>(type)
                val activityJson = adapter.toJson(updatedActivity)
                preferencesManager.setReadingActivity(activityJson)

                // Check achievements
                checkReadingAchievements(updatedPages, updatedStreakDays)

            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving reading progress", e)
            }
        }

        simulateTaskCompletion("reading", pages)
    }

    fun onAddVocabularyWord(word: String, definition: String, example: String?) {
        if (word.isBlank() || definition.isBlank()) return
        val today = LocalDate.now()

        viewModelScope.launch {
            try {
                val currentState = _uiState.value.vocabularyState
                val newEntry = VocabularyWord(
                    word = word.trim(),
                    definition = definition.trim(),
                    example = example?.takeIf { it.isNotBlank() }?.trim(),
                    addedToday = true
                )
                val updatedWords = listOf(newEntry) + currentState.words
                val updatedCount = currentState.wordsAddedToday + 1

                // Check if we should increment streak (first word of the day)
                val shouldIncrementStreak = currentState.wordsAddedToday == 0
                val updatedStreakDays = if (shouldIncrementStreak) {
                    currentState.currentStreakDays + 1
                } else {
                    currentState.currentStreakDays
                }

                // Update UI state
                _uiState.update { state ->
                    state.copy(
                        vocabularyState = state.vocabularyState.copy(
                            wordsAddedToday = updatedCount,
                            words = updatedWords,
                            currentStreakDays = updatedStreakDays
                        )
                    )
                }

                // Save to PreferencesManager
                preferencesManager.setWordsAddedToday(updatedCount)
                preferencesManager.setVocabularyLastDate(today.toString())
                if (shouldIncrementStreak) {
                    preferencesManager.setVocabularyStreakDays(updatedStreakDays)
                }

                // Save words list
                val type = Types.newParameterizedType(List::class.java, VocabularyWord::class.java)
                val adapter = moshi.adapter<List<VocabularyWord>>(type)
                val wordsJson = adapter.toJson(updatedWords)
                preferencesManager.setVocabularyWords(wordsJson)

                // Check achievements
                checkVocabularyAchievements(updatedWords.size, updatedStreakDays)

            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving vocabulary word", e)
            }
        }

        simulateTaskCompletion("vocabulary", 1)
    }

    private fun observeStreaks() {
        viewModelScope.launch {
            streakRepository.observeStreaks().collectLatest { streaks ->
                val stats = withContext(Dispatchers.Default) { buildStatsStateFromStreaks(streaks) }
                _uiState.update { state ->
                    val selectedId = state.selectedStreakId ?: streaks.firstOrNull()?.id
                    state.copy(
                        streaks = streaks,
                        selectedStreakId = selectedId,
                        todayTasks = buildTasksForStreaks(streaks),
                        statsState = stats
                    )
                }
            }
        }

        viewModelScope.launch {
            streakRepository.observeTopStreaks(5).collectLatest { topStreaks ->
                _uiState.update { state ->
                    state.copy(
                        statsState = state.statsState.copy(
                            leaderboard = topStreaks.mapIndexed { index, streak ->
                                LeaderboardEntry(
                                    position = index + 1,
                                    name = streak.name,
                                    streakDays = streak.currentCount
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    private fun bootstrapStaticState() {
        _uiState.update { state ->
            state.copy(
                discoverState = buildDiscoverState(),
                readingTrackerState = buildReadingState(),
                vocabularyState = buildVocabularyState(),
                profileState = buildProfileState()
            )
        }
    }

    private fun buildTasksForStreaks(streaks: List<Streak>): List<DashboardTask> {
        if (streaks.isEmpty()) return emptyList()
        return streaks.map { streak ->
            DashboardTask(
                id = "task-${streak.id}",
                title = "Log ${streak.goalPerDay} ${streak.unit}",
                category = streak.category,
                streakId = streak.id,
                isCompleted = streak.history.lastOrNull()?.metGoal == true,
                accentHex = streak.color
            )
        }
    }

    private fun buildStatsStateFromStreaks(streaks: List<Streak>): StatsState {
        if (streaks.isEmpty()) return StatsState()

        val longestStreak = streaks.maxByOrNull { it.currentCount }
        val avgProgress = streaks.map { it.progress }.average() * 100
        val habitBreakdown = streaks.map { streak ->
            HabitBreakdown(
                name = streak.name,
                completionPercent = (streak.progress * 100).toInt(),
                accentHex = streak.color
            )
        }

        val dailyTrend = computeAverageDailyTrend(streaks)
        val consistency = computeStreakConsistency(streaks)
        val heatMap = computeCalendarHeatMap(streaks)

        return StatsState(
            currentLongestStreak = longestStreak?.currentCount ?: 0,
            currentLongestStreakName = longestStreak?.name ?: "",
            averageDailyProgressPercent = avgProgress.toInt(),
            averageDailyTrend = dailyTrend,
            streakConsistency = consistency,
            habitBreakdown = habitBreakdown,
            leaderboard = emptyList(), // Handled separately by observeTopStreaks
            calendarHeatMap = heatMap
        )
    }

    private fun computeCalendarHeatMap(
        streaks: List<Streak>,
        totalWeeks: Int = 6
    ): CalendarHeatMap? {
        if (streaks.isEmpty()) return null

        val daysPerWeek = 7
        val horizonDays = totalWeeks * daysPerWeek
        val today = LocalDate.now()
        val startDate = today.minusDays((horizonDays - 1).toLong())

        val aggregates = mutableMapOf<LocalDate, MutableList<Float>>()
        streaks.forEach { streak ->
            streak.history.forEach { record ->
                val date = parseDate(record.date) ?: return@forEach
                val fraction = record.completionFraction.coerceIn(0f, 1f)
                aggregates.getOrPut(date) { mutableListOf() }.add(fraction)
            }
        }

        var activeDayCount = 0
        val days = (0 until horizonDays).map { offset ->
            val date = startDate.plusDays(offset.toLong())
            val values = aggregates[date]
            val intensity = values?.let { list ->
                (list.sum() / list.size).coerceIn(0f, 1f)
            } ?: 0f
            if (intensity > 0f) activeDayCount++
            HeatMapDay(
                date = date.toString(),
                intensity = intensity,
                isToday = date == today
            )
        }

        if (days.all { it.intensity == 0f }) {
            return null
        }

        val weeks = days.chunked(daysPerWeek).map { chunk ->
            HeatMapWeek(days = chunk)
        }

        return CalendarHeatMap(
            weeks = weeks,
            completedDays = activeDayCount,
            totalDays = horizonDays
        )
    }

    private fun buildDiscoverState(): DiscoverState = DiscoverState(
        featuredContent = FeaturedContent(
            id = "mindset",
            title = "The Science of Habit Building",
            description = "Micro-habits backed by behavioral science.",
            accentHex = "#6C63FF"
        ),
        categories = listOf(
            CategoryItem("mindfulness", "Mindfulness", "#FF6584"),
            CategoryItem("reading", "Reading", "#6C63FF"),
            CategoryItem("productivity", "Productivity", "#4CD964"),
            CategoryItem("fitness", "Fitness", "#F7B500")
        ),
        suggestions = listOf(
            SuggestionItem("read-poems", "Read 10 Poems", "Great for reflective evenings", "#6C63FF"),
            SuggestionItem("drink-water", "Hydrate Hourly", "Keep a glass nearby.", "#4CD964"),
            SuggestionItem("gratitude", "Log Gratitude", "Write 3 wins every night.", "#FF6584")
        ),
        communityChallenges = listOf(
            ChallengeItem("meditation", "30-Day Meditation", "Day 12 of 30", 4281, "#6C63FF"),
            ChallengeItem("journal", "Weekly Journaling", "2 days left", 1372, "#FF6584")
        )
    )

    private fun buildReadingState(): ReadingTrackerState = ReadingTrackerState(
        currentStreakDays = 24,
        pagesReadToday = 18,
        goalPagesPerDay = 30,
        recentActivity = listOf(
            ReadingLog("Nov 11", 32),
            ReadingLog("Nov 10", 28),
            ReadingLog("Nov 9", 30)
        )
    )

    private fun buildVocabularyState(): VocabularyState = VocabularyState(
        currentStreakDays = 15,
        wordsAddedToday = 3,
        words = listOf(
            VocabularyWord("Tenacity", "The quality of being determined.", "Your tenacity keeps the streak alive."),
            VocabularyWord("Liminal", "Occupying a position at a boundary.", "Morning routines live in the liminal space between rest and action."),
            VocabularyWord("Verve", "Energy and enthusiasm.")
        )
    )

    private fun buildProfileState(): ProfileState = ProfileState(
        notificationEnabled = true,
        reminderFrequency = ReminderFrequency.Daily,
        hasWeeklySummary = true,
        theme = ProfileTheme.Dark,
        activeCategories = _uiState.value.onboardingState.selectedCategories
    )

    fun simulateTaskCompletion(streakId: String, value: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { state ->
                    val busyTasks = state.todayTasks.map { task ->
                        if (task.streakId == streakId) task.copy(isCompleted = true) else task
                    }
                    state.copy(todayTasks = busyTasks)
                }
                streakRepository.logProgress(streakId, value)
                delay(300)
                _uiState.update { state ->
                    state.copy(todayTasks = buildTasksForStreaks(state.streaks))
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error completing task for streak $streakId", e)
                // Revert the task completion on error
                _uiState.update { state ->
                    state.copy(todayTasks = buildTasksForStreaks(state.streaks))
                }
            }
        }
    }

    // Achievement checking methods
    private data class Achievement(
        val id: String,
        val title: String,
        val description: String,
        val requirement: Int,
        val points: Int,
        val category: String
    )

    private val readingAchievements = listOf(
        Achievement("reading_100", "Page Turner", "Read 100 pages total", 100, 50, "reading"),
        Achievement("reading_500", "Avid Reader", "Read 500 pages total", 500, 100, "reading"),
        Achievement("reading_1000", "Bookworm", "Read 1000 pages total", 1000, 250, "reading"),
        Achievement("reading_streak_7", "Week Reader", "7 day reading streak", 7, 75, "reading"),
        Achievement("reading_streak_30", "Monthly Reader", "30 day reading streak", 30, 200, "reading")
    )

    private val vocabularyAchievements = listOf(
        Achievement("vocab_10", "Word Collector", "Add 10 words", 10, 25, "vocabulary"),
        Achievement("vocab_50", "Vocabulary Builder", "Add 50 words", 50, 100, "vocabulary"),
        Achievement("vocab_100", "Word Master", "Add 100 words", 100, 200, "vocabulary"),
        Achievement("vocab_streak_7", "Weekly Wordsmith", "7 day vocabulary streak", 7, 75, "vocabulary"),
        Achievement("vocab_streak_30", "Monthly Linguist", "30 day vocabulary streak", 30, 200, "vocabulary")
    )

    private suspend fun checkReadingAchievements(totalPages: Int, streakDays: Int) {
        try {
            val achievementsJson = preferencesManager.achievementsData.first()
            val type = Types.newParameterizedType(Map::class.java, String::class.java, Boolean::class.javaObjectType)
            val adapter = moshi.adapter<Map<String, Boolean>>(type)
            val unlockedAchievements = try {
                adapter.fromJson(achievementsJson)?.toMutableMap() ?: mutableMapOf()
            } catch (e: Exception) {
                mutableMapOf()
            }

            var pointsEarned = 0

            readingAchievements.forEach { achievement ->
                if (unlockedAchievements[achievement.id] != true) {
                    val progress = when {
                        achievement.id.contains("streak") -> streakDays
                        else -> totalPages
                    }

                    if (progress >= achievement.requirement) {
                        unlockedAchievements[achievement.id] = true
                        pointsEarned += achievement.points
                        Log.i("AppViewModel", "Achievement unlocked: ${achievement.title}")
                    }
                }
            }

            if (pointsEarned > 0) {
                preferencesManager.addPoints(pointsEarned)
                val updatedJson = adapter.toJson(unlockedAchievements)
                preferencesManager.setAchievementsData(updatedJson)
            }
        } catch (e: Exception) {
            Log.e("AppViewModel", "Error checking reading achievements", e)
        }
    }

    private suspend fun checkVocabularyAchievements(totalWords: Int, streakDays: Int) {
        try {
            val achievementsJson = preferencesManager.achievementsData.first()
            val type = Types.newParameterizedType(Map::class.java, String::class.java, Boolean::class.javaObjectType)
            val adapter = moshi.adapter<Map<String, Boolean>>(type)
            val unlockedAchievements = try {
                adapter.fromJson(achievementsJson)?.toMutableMap() ?: mutableMapOf()
            } catch (e: Exception) {
                mutableMapOf()
            }

            var pointsEarned = 0

            vocabularyAchievements.forEach { achievement ->
                if (unlockedAchievements[achievement.id] != true) {
                    val progress = when {
                        achievement.id.contains("streak") -> streakDays
                        else -> totalWords
                    }

                    if (progress >= achievement.requirement) {
                        unlockedAchievements[achievement.id] = true
                        pointsEarned += achievement.points
                        Log.i("AppViewModel", "Achievement unlocked: ${achievement.title}")
                    }
                }
            }

            if (pointsEarned > 0) {
                preferencesManager.addPoints(pointsEarned)
                val updatedJson = adapter.toJson(unlockedAchievements)
                preferencesManager.setAchievementsData(updatedJson)
            }
        } catch (e: Exception) {
            Log.e("AppViewModel", "Error checking vocabulary achievements", e)
        }
    }

    private fun computeAverageDailyTrend(
        streaks: List<Streak>,
        horizonDays: Int = 21,
        windowSize: Int = 7
    ): AverageDailyTrend? {
        if (streaks.isEmpty()) return null

        val today = LocalDate.now()
        val perStreakDateMap = streaks.associateBy(
            keySelector = { it.id },
            valueTransform = { streak ->
                streak.history.mapNotNull { record ->
                    parseDate(record.date)?.let { it to record }
                }.toMap()
            }
        )

        data class DailyAggregate(val date: LocalDate, val completed: Int, val goal: Int)

        val aggregates = (0 until horizonDays).map { offset ->
            val day = today.minusDays(offset.toLong())
            val dayString = day.toString()
            val totals = perStreakDateMap.values.fold(0 to 0) { acc, map ->
                val record = map[day]
                if (record != null) {
                    val goalValue = record.goal.coerceAtLeast(0)
                    val completedValue = record.completed.coerceAtLeast(0).coerceAtMost(goalValue)
                    (acc.first + completedValue) to (acc.second + goalValue)
                } else acc
            }
            DailyAggregate(date = day, completed = totals.first, goal = totals.second)
        }.reversed()

        val trendPoints = aggregates.mapIndexedNotNull { index, aggregate ->
            val startIndex = (index - windowSize + 1).coerceAtLeast(0)
            val windowSlice = aggregates.subList(startIndex, index + 1)
            val windowGoal = windowSlice.sumOf { it.goal }
            if (windowGoal == 0) return@mapIndexedNotNull null
            val windowCompleted = windowSlice.sumOf { it.completed }
            val percent = ((windowCompleted / windowGoal.toFloat()) * 100f).roundToInt().coerceIn(0, 100)
            TrendPoint(date = aggregate.date.toString(), percent = percent)
        }

        return trendPoints.takeIf { it.isNotEmpty() }?.let {
            AverageDailyTrend(windowSize = windowSize, points = it)
        }
    }

    private fun computeStreakConsistency(
        streaks: List<Streak>,
        windowDays: Int = 30
    ): List<ConsistencyScore> {
        if (streaks.isEmpty()) return emptyList()

        return streaks.mapNotNull { streak ->
            val history = streak.history.takeLast(windowDays)
            if (history.isEmpty()) return@mapNotNull null

            val completionRate = history.count { it.metGoal }.toFloat() / history.size.toFloat()
            val fractions = history.map { it.completionFraction.coerceIn(0f, 1f) }
            val mean = fractions.average().toFloat()
            val variance = fractions.fold(0f) { acc, value ->
                val diff = value - mean
                acc + diff * diff
            } / fractions.size.toFloat()
            val varianceNorm = (variance / 0.25f).coerceIn(0f, 1f)
            val score = ((completionRate * 70f) + ((1 - varianceNorm) * 30f)).roundToInt().coerceIn(0, 100)
            val completionPercent = (completionRate * 100f).roundToInt().coerceIn(0, 100)
            val level = when {
                score >= 80 -> ConsistencyLevel.High
                score >= 50 -> ConsistencyLevel.Medium
                else -> ConsistencyLevel.NeedsAttention
            }

            ConsistencyScore(
                streakId = streak.id,
                streakName = streak.name,
                score = score,
                completionRate = completionPercent,
                variance = variance,
                level = level
            )
        }.sortedByDescending { it.score }
    }

    private fun parseDate(date: String): LocalDate? = runCatching { LocalDate.parse(date) }.getOrNull()

    // Quick action: Schedule weekly backup reminder
    fun scheduleWeeklyBackupReminder() {
        viewModelScope.launch {
            try {
                reminderScheduler.scheduleWeeklyBackup()
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error scheduling weekly backup", e)
            }
        }
    }

    // Quick action: Auto-switch theme based on time
    fun checkAndUpdateThemeByTime() {
        viewModelScope.launch {
            try {
                val currentHour = java.time.LocalTime.now().hour
                val shouldBeDark = currentHour < 6 || currentHour >= 18
                val currentTheme = preferencesManager.themeMode.first()
                
                if (currentTheme == "auto_time") {
                    val newTheme = if (shouldBeDark) "dark" else "light"
                    preferencesManager.setThemeMode(newTheme)
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error auto-switching theme", e)
            }
        }
    }

    // Settings Screen Actions
    fun onSettingsThemeChange(themeMode: com.productivitystreak.ui.state.settings.ThemeMode) {
        val themeString = when (themeMode) {
            com.productivitystreak.ui.state.settings.ThemeMode.LIGHT -> "light"
            com.productivitystreak.ui.state.settings.ThemeMode.DARK -> "dark"
            com.productivitystreak.ui.state.settings.ThemeMode.SYSTEM -> "system"
        }
        
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(themeMode = themeMode)
            )
        }
        
        viewModelScope.launch {
            try {
                preferencesManager.setThemeMode(themeString)
                onChangeTheme(when (themeMode) {
                    com.productivitystreak.ui.state.settings.ThemeMode.LIGHT -> ProfileTheme.Light
                    com.productivitystreak.ui.state.settings.ThemeMode.DARK -> ProfileTheme.Dark
                    com.productivitystreak.ui.state.settings.ThemeMode.SYSTEM -> ProfileTheme.Auto
                })
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving theme", e)
            }
        }
    }

    fun onSettingsDailyRemindersToggle(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(dailyRemindersEnabled = enabled)
            )
        }
        
        viewModelScope.launch {
            try {
                preferencesManager.setDailyReminderEnabled(enabled)
                if (enabled) {
                    scheduleReminderForCurrentState(ReminderFrequency.Daily)
                } else {
                    reminderScheduler.cancelReminders()
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error toggling daily reminders", e)
            }
        }
    }

    fun onSettingsWeeklyBackupsToggle(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(weeklyBackupsEnabled = enabled)
            )
        }
        
        viewModelScope.launch {
            try {
                preferencesManager.setWeeklySummaryEnabled(enabled)
                if (enabled) {
                    reminderScheduler.scheduleWeeklyBackup()
                } else {
                    reminderScheduler.cancelWeeklyBackup()
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error toggling weekly backups", e)
            }
        }
    }

    private fun scheduleReminderForCurrentState(
        frequencyOverride: ReminderFrequency? = null
    ) {
        val current = _uiState.value
        val frequency = frequencyOverride ?: current.profileState.reminderFrequency
        reminderScheduler.scheduleReminder(
            frequency = frequency,
            categories = current.onboardingState.selectedCategories,
            userName = current.userName
        )
    }

    fun onSettingsReminderTimeChange(time: String) {
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(
                    reminderTime = time,
                    showTimePickerDialog = true
                )
            )
        }
        
        viewModelScope.launch {
            try {
                preferencesManager.setReminderTime(time)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving reminder time", e)
            }
        }
    }

    fun onSettingsHapticFeedbackToggle(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(hapticFeedbackEnabled = enabled)
            )
        }
        
        onToggleHaptics(enabled)
    }

    fun onSettingsCreateBackup() {
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(isBackupInProgress = true)
            )
        }
        
        viewModelScope.launch {
            try {
                val app = getApplication<com.productivitystreak.NeverZeroApplication>()
                val result = app.backupManager.createBackup()
                
                result.onSuccess { file ->
                    val dateFormat = java.text.SimpleDateFormat("MMM d, yyyy 'at' h:mm a", java.util.Locale.getDefault())
                    val timestamp = dateFormat.format(java.util.Date())
                    
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(
                                isBackupInProgress = false,
                                showBackupSuccessMessage = true,
                                lastBackupTime = timestamp
                            )
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(
                                isBackupInProgress = false,
                                errorMessage = "Backup failed: ${error.message}"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error creating backup", e)
                _uiState.update { state ->
                    state.copy(
                        settingsState = state.settingsState.copy(
                            isBackupInProgress = false,
                            errorMessage = "Backup failed: ${e.message}"
                        )
                    )
                }
            }
        }
    }

    fun onSettingsRestoreBackup() {
        // This will be triggered by file picker in the UI
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(showRestoreDialog = true)
            )
        }
    }

    fun onSettingsDismissRestoreDialog() {
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(
                    showRestoreDialog = false,
                    isRestoreInProgress = false
                )
            )
        }
    }

    fun onSettingsRestoreFromFile(fileUri: android.net.Uri) {
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(
                    isRestoreInProgress = true,
                    showRestoreDialog = false
                )
            )
        }
        
        viewModelScope.launch {
            try {
                val app = getApplication<com.productivitystreak.NeverZeroApplication>()
                val inputStream = app.contentResolver.openInputStream(fileUri)
                val tempFile = java.io.File.createTempFile("restore", ".json", app.cacheDir)
                tempFile.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }
                
                val result = app.backupManager.restoreBackup(tempFile, mergeMode = false)
                
                result.onSuccess { message ->
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(
                                isRestoreInProgress = false,
                                showRestoreSuccessMessage = true
                            )
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { state ->
                        state.copy(
                            settingsState = state.settingsState.copy(
                                isRestoreInProgress = false,
                                errorMessage = "Restore failed: ${error.message}"
                            )
                        )
                    }
                }
                
                tempFile.delete()
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error restoring backup", e)
                _uiState.update { state ->
                    state.copy(
                        settingsState = state.settingsState.copy(
                            isRestoreInProgress = false,
                            errorMessage = "Restore failed: ${e.message}"
                        )
                    )
                }
            }
        }
    }

    fun onSettingsDismissMessage() {
        _uiState.update { state ->
            state.copy(
                settingsState = state.settingsState.copy(
                    showTimePickerDialog = false,
                    showBackupSuccessMessage = false,
                    showRestoreSuccessMessage = false,
                    errorMessage = null
                )
            )
        }
    }
}
