package com.productivitystreak.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.ai.BuddhaInsightState
import com.productivitystreak.data.ai.BuddhaQuest
import com.productivitystreak.data.ai.BuddhaRepository
import com.productivitystreak.data.ai.WisdomType
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.state.home.ContentType
import com.productivitystreak.ui.state.home.DailyContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel backing the Home screen vocabulary teacher.
 *
 * Exposes a [StateFlow] of [VocabularyWord] that is stable for the current
 * day and can be refreshed when the vocabulary list changes.
 */
class HomeViewModel : ViewModel() {

    private val buddhaRepository = BuddhaRepository()

    private val _buddhaInsightState = MutableStateFlow<BuddhaInsightState>(BuddhaInsightState.Loading)
    val buddhaInsightState: StateFlow<BuddhaInsightState> = _buddhaInsightState.asStateFlow()

    private val _dailyContent = MutableStateFlow<DailyContent?>(null)
    val dailyContent: StateFlow<DailyContent?> = _dailyContent.asStateFlow()

    private val _sidequest = MutableStateFlow<BuddhaQuest?>(null)
    val sidequest: StateFlow<BuddhaQuest?> = _sidequest.asStateFlow()

    init {
        // Preload static content when the Home screen is first shown.
        loadDailyContent()
        loadSidequest()
    }

    fun loadBuddhaInsight(streaks: List<Streak>) {
        // Allow loading even if streaks are empty (for new users)
        // if (streaks.isEmpty()) return

        // Don't reload if we already have a success state to avoid unnecessary API calls
        if (_buddhaInsightState.value is BuddhaInsightState.Success) return

        _buddhaInsightState.value = BuddhaInsightState.Loading

        viewModelScope.launch {
            val result = buddhaRepository.getInsightForStreaks(streaks)
            result.fold(
                onSuccess = { insight ->
                    _buddhaInsightState.value = BuddhaInsightState.Success(insight)
                },
                onFailure = { error ->
                    _buddhaInsightState.value = BuddhaInsightState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun retryBuddhaInsight(streaks: List<Streak>) {
        _buddhaInsightState.value = BuddhaInsightState.Loading
        loadBuddhaInsight(streaks)
    }

    private fun loadDailyContent() {
        viewModelScope.launch {
            val result = buddhaRepository.getDailyWisdom()
            result.onSuccess { wisdom ->
                _dailyContent.value = DailyContent(
                    id = "buddha_wisdom_${System.currentTimeMillis()}",
                    type = if (wisdom.type == WisdomType.WORD) ContentType.VOCABULARY else ContentType.PHILOSOPHY,
                    title = wisdom.content.lowercase(),
                    subtitle = wisdom.origin ?: "unknown origin",
                    content = wisdom.meaning.lowercase(),
                    actionLabel = "internalize"
                )
            }
        }
    }

    private fun loadSidequest() {
        viewModelScope.launch {
            val result = buddhaRepository.generateSidequest()
            result.onSuccess { quest ->
                _sidequest.value = quest
            }
        }
    }

    fun onContentAction(content: DailyContent) {
        // Handle action (e.g., mark as collected, open details)
    }
}
