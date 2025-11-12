package com.productivitystreak.ui.state

import com.productivitystreak.data.model.Quote
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.state.discover.DiscoverState
import com.productivitystreak.ui.state.onboarding.OnboardingState
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.reading.ReadingTrackerState
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
    val showOnboarding: Boolean = true
)

data class DashboardTask(
    val id: String,
    val title: String,
    val category: String,
    val streakId: String,
    val isCompleted: Boolean,
    val accentHex: String
)
