package com.productivitystreak.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.model.Streak
import com.productivitystreak.data.repository.StreakRepository
import com.productivitystreak.notifications.StreakReminderScheduler
import com.productivitystreak.ui.state.AppUiState
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppViewModel(
    application: Application,
    private val quoteRepository: QuoteRepository,
    private val streakRepository: StreakRepository,
    private val reminderScheduler: StreakReminderScheduler,
    private val vocabularyRepository: com.productivitystreak.data.repository.VocabularyRepository,
    private val bookRepository: com.productivitystreak.data.repository.BookRepository,
    private val reflectionRepository: com.productivitystreak.data.repository.ReflectionRepository,
    private val achievementRepository: com.productivitystreak.data.repository.AchievementRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var quoteRefreshJob: Job? = null

    // New data streams
    val achievements = achievementRepository.observeAllAchievements()
    val unlockedAchievements = achievementRepository.observeUnlockedAchievements()
    val vocabularyWords = vocabularyRepository.observeAllWords()
    val books = bookRepository.observeAllBooks()

    init {
        bootstrapStaticState()
        observeStreaks()
        refreshQuote()
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
        _uiState.update { state ->
            val updatedTasks = state.todayTasks.map { task ->
                if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
            }
            state.copy(todayTasks = updatedTasks)
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

    fun onChangeTheme(theme: ProfileTheme) {
        _uiState.update { state ->
            state.copy(profileState = state.profileState.copy(theme = theme))
        }
    }

    fun onLogReadingProgress(pages: Int) {
        if (pages <= 0) return
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        _uiState.update { state ->
            val tracker = state.readingTrackerState
            val updatedPages = tracker.pagesReadToday + pages
            val newLog = ReadingLog(
                dateLabel = LocalDate.now().format(formatter),
                pages = pages
            )
            val updatedActivity = (listOf(newLog) + tracker.recentActivity).take(7)
            state.copy(
                readingTrackerState = tracker.copy(
                    pagesReadToday = updatedPages,
                    recentActivity = updatedActivity,
                    currentStreakDays = (tracker.currentStreakDays + 1).coerceAtLeast(tracker.currentStreakDays)
                )
            )
        }
        simulateTaskCompletion("reading", pages)
    }

    fun onAddVocabularyWord(word: String, definition: String, example: String?) {
        if (word.isBlank() || definition.isBlank()) return
        _uiState.update { state ->
            val vocabulary = state.vocabularyState
            val newEntry = VocabularyWord(
                word = word.trim(),
                definition = definition.trim(),
                example = example?.takeIf { it.isNotBlank() }?.trim(),
                addedToday = true
            )
            state.copy(
                vocabularyState = vocabulary.copy(
                    wordsAddedToday = vocabulary.wordsAddedToday + 1,
                    words = listOf(newEntry) + vocabulary.words
                )
            )
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
                        todayTasks = buildTasksForStreaks(streaks)
                    )
                }
            }
        }
    }

    private fun bootstrapStaticState() {
        _uiState.update { state ->
            state.copy(
                statsState = buildStatsState(),
                discoverState = buildDiscoverState(),
                readingTrackerState = buildReadingState(),
                vocabularyState = buildVocabularyState(),
                profileState = buildProfileState()
            )
        }
    }

    private fun buildTasksForStreaks(streaks: List<Streak>): List<DashboardTask> {
        if (streaks.isEmpty()) return emptyList()
        val accentPalette = listOf("#6C63FF", "#FF6584", "#4CD964", "#F7B500")
        return streaks.mapIndexed { index, streak ->
            DashboardTask(
                id = "task-${streak.id}",
                title = "Log ${streak.goalPerDay} ${streak.unit}",
                category = streak.category,
                streakId = streak.id,
                isCompleted = streak.history.lastOrNull()?.let { it >= streak.goalPerDay } ?: false,
                accentHex = accentPalette[index % accentPalette.size]
            )
        }
    }

    private fun buildStatsState(): StatsState = StatsState(
        currentLongestStreak = 42,
        currentLongestStreakName = "Read 30 mins",
        averageDailyProgressPercent = 76,
        averageDailyTrend = listOf(65, 70, 80, 76, 90, 88, 92),
        streakConsistency = listOf(7, 6, 5, 7, 7, 6, 5),
        habitBreakdown = listOf(
            HabitBreakdown("Read 30 mins", 92, "#6C63FF"),
            HabitBreakdown("Meditate", 86, "#4CD964"),
            HabitBreakdown("Add Vocabulary", 74, "#FF6584")
        ),
        leaderboard = listOf(
            LeaderboardEntry(position = 1, name = "Alex", streakDays = 24),
            LeaderboardEntry(position = 2, name = "Maya", streakDays = 21),
            LeaderboardEntry(position = 3, name = "Jordan", streakDays = 19)
        )
    )

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
            checkAchievements()
        }
    }

    // Achievement checking system
    private suspend fun checkAchievements() {
        val streaks = _uiState.value.streaks

        // Check streak milestones
        streaks.forEach { streak ->
            when {
                streak.currentCount >= 365 ->
                    achievementRepository.incrementProgress("streak_365", streak.currentCount)
                streak.currentCount >= 100 ->
                    achievementRepository.incrementProgress("streak_100", streak.currentCount)
                streak.currentCount >= 30 ->
                    achievementRepository.incrementProgress("streak_30", streak.currentCount)
                streak.currentCount >= 7 ->
                    achievementRepository.incrementProgress("streak_7", streak.currentCount)
            }
        }

        // Check book completions
        val finishedBooks = bookRepository.getFinishedBookCount()
        if (finishedBooks >= 10) {
            achievementRepository.updateProgress("books_10", finishedBooks)
        }
        if (finishedBooks >= 1) {
            achievementRepository.updateProgress("books_1", finishedBooks)
        }

        // Check vocabulary
        val wordCount = vocabularyRepository.getWordCount()
        when {
            wordCount >= 500 -> achievementRepository.updateProgress("words_500", wordCount)
            wordCount >= 200 -> achievementRepository.updateProgress("words_200", wordCount)
            wordCount >= 50 -> achievementRepository.updateProgress("words_50", wordCount)
        }

        // Check reflections
        val reflectionCount = reflectionRepository.getReflectionCount()
        when {
            reflectionCount >= 30 -> achievementRepository.updateProgress("reflections_30", reflectionCount)
            reflectionCount >= 7 -> achievementRepository.updateProgress("reflections_7", reflectionCount)
        }
    }

    // Vocabulary functions
    fun addVocabularyWord(word: String, definition: String, example: String?, partOfSpeech: String?) {
        if (word.isBlank() || definition.isBlank()) return
        viewModelScope.launch {
            vocabularyRepository.addWord(
                word = word,
                definition = definition,
                example = example,
                partOfSpeech = partOfSpeech
            )
            checkAchievements()
        }
    }

    suspend fun getWordsForPractice(limit: Int = 10) =
        vocabularyRepository.getWordsForPractice(limit)

    fun reviewWord(wordId: Long, correct: Boolean) {
        viewModelScope.launch {
            vocabularyRepository.reviewWord(wordId, correct)
        }
    }

    // Book functions
    fun addBook(title: String, author: String, totalPages: Int, genre: String? = null) {
        viewModelScope.launch {
            bookRepository.addBook(
                title = title,
                author = author,
                totalPages = totalPages,
                genre = genre
            )
        }
    }

    fun logReadingSession(bookId: Long, pagesRead: Int, startPage: Int, notes: String? = null) {
        viewModelScope.launch {
            val success = bookRepository.logReadingSession(bookId, pagesRead, startPage, notes)
            if (success) {
                checkAchievements()
            }
        }
    }

    // Reflection functions
    suspend fun getTodayReflection() = reflectionRepository.getTodayReflection()

    fun saveReflection(mood: Int, notes: String, highlights: String?, challenges: String?, gratitude: String?) {
        viewModelScope.launch {
            reflectionRepository.saveReflection(
                mood = mood,
                notes = notes,
                highlights = highlights,
                challenges = challenges,
                gratitude = gratitude
            )
            checkAchievements()
        }
    }
}
