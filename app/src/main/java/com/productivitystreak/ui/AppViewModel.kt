package com.productivitystreak.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.model.Streak
import com.productivitystreak.data.repository.StreakRepository
import com.productivitystreak.notifications.StreakReminderScheduler
import com.productivitystreak.ui.state.AppUiState
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
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ProfileTheme
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.reading.ReadingLog
import com.productivitystreak.ui.state.reading.ReadingTrackerState
import com.productivitystreak.ui.state.stats.HabitBreakdown
import com.productivitystreak.ui.state.stats.LeaderboardEntry
import com.productivitystreak.ui.state.stats.StatsState
import com.productivitystreak.ui.state.vocabulary.VocabularyState
import com.productivitystreak.ui.state.vocabulary.VocabularyWord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.productivitystreak.ui.utils.hapticFeedbackManager

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
        bootstrapStaticState()
        observeStreaks()
        refreshQuote()
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

    fun refreshQuote() {
        quoteRefreshJob?.cancel()
        quoteRefreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isQuoteLoading = true, quoteErrorMessage = null) }
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
                        quoteErrorMessage = error.message ?: "Unable to load quote"
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

        if (snapshot.profileState.notificationEnabled) {
            reminderScheduler.scheduleReminder(
                frequency = snapshot.profileState.reminderFrequency,
                categories = snapshot.onboardingState.selectedCategories,
                userName = snapshot.userName
            )
        }
    }

    fun onToggleNotifications(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(notificationEnabled = enabled))
        }

        // Save to PreferencesManager
        viewModelScope.launch {
            try {
                preferencesManager.setNotificationsEnabled(enabled)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error saving notification preference", e)
            }
        }

        val current = _uiState.value
        if (enabled) {
            reminderScheduler.scheduleReminder(
                frequency = current.profileState.reminderFrequency,
                categories = current.onboardingState.selectedCategories,
                userName = current.userName
            )
        } else {
            reminderScheduler.cancelReminders()
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
            reminderScheduler.scheduleReminder(
                frequency = frequency,
                categories = current.onboardingState.selectedCategories,
                userName = current.userName
            )
        }
    }

    fun onToggleWeeklySummary(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(hasWeeklySummary = enabled))
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
                _uiState.update { state ->
                    val selectedId = state.selectedStreakId ?: streaks.firstOrNull()?.id
                    state.copy(
                        streaks = streaks,
                        selectedStreakId = selectedId,
                        todayTasks = buildTasksForStreaks(streaks),
                        statsState = buildStatsStateFromStreaks(streaks)
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
                isCompleted = streak.history.lastOrNull()?.let { it >= streak.goalPerDay } ?: false,
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

        return StatsState(
            currentLongestStreak = longestStreak?.currentCount ?: 0,
            currentLongestStreakName = longestStreak?.name ?: "",
            averageDailyProgressPercent = avgProgress.toInt(),
            averageDailyTrend = emptyList(), // TODO: compute from history if needed
            streakConsistency = emptyList(), // TODO: compute consistency metrics
            habitBreakdown = habitBreakdown,
            leaderboard = emptyList() // Handled separately by observeTopStreaks
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
}
