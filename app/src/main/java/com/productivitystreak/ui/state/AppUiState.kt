package com.productivitystreak.ui.state

import com.productivitystreak.data.model.Quote
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.state.discover.DiscoverState
import com.productivitystreak.ui.state.onboarding.OnboardingState
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.reading.ReadingTrackerState
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.state.stats.StatsState
import com.productivitystreak.ui.state.vocabulary.VocabularyState

data class AppUiState(
    val userName: String = "Alex",
    val quote: Quote? = null,
    val isQuoteLoading: Boolean = false,
    val quoteErrorMessage: String? = null,
    val streaks: List<Streak> = emptyList(),
    val selectedStreakId: String? = null,
    val todayTasks: List<DashboardTask> = emptyList(),
    val statsState: StatsState = StatsState(),
    val discoverState: DiscoverState = DiscoverState(),
    val readingTrackerState: ReadingTrackerState = ReadingTrackerState(),
    val vocabularyState: VocabularyState = VocabularyState(),
    val profileState: ProfileState = ProfileState(),
    val onboardingState: OnboardingState = OnboardingState(),
    val settingsState: SettingsState = SettingsState(),
    val addUiState: AddUiState = AddUiState(),
    val totalPoints: Int = 0,
    val showOnboarding: Boolean = true,
    val onboardingCelebration: Boolean = false,
    val permissionState: PermissionUiState = PermissionUiState(),
    val isDataLoading: Boolean = false,
    val uiMessage: UiMessage? = null
)

data class DashboardTask(
    val id: String,
    val title: String,
    val category: String,
    val streakId: String,
    val isCompleted: Boolean,
    val accentHex: String
)

enum class UiMessageType { SUCCESS, ERROR, INFO }

data class UiMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isBlocking: Boolean = false,
    val actionLabel: String? = null,
    val type: UiMessageType = UiMessageType.INFO
)

data class AddUiState(
    val isMenuOpen: Boolean = false,
    val activeForm: AddEntryType? = null,
    val isSubmitting: Boolean = false
)

enum class AddEntryType { HABIT, WORD, JOURNAL }
