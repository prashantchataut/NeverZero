package com.productivitystreak.ui.screens.home

import androidx.lifecycle.ViewModel
import com.productivitystreak.ui.state.home.ContentType
import com.productivitystreak.ui.state.home.DailyContent
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel backing the Home screen vocabulary teacher.
 *
 * Exposes a [StateFlow] of [VocabularyWord] that is stable for the current
 * day and can be refreshed when the vocabulary list changes.
 */
class HomeViewModel : ViewModel() {

    private val _dailyContent = MutableStateFlow<DailyContent?>(null)
    val dailyContent: StateFlow<DailyContent?> = _dailyContent.asStateFlow()

    init {
        // In a real app, this would come from a repository
        loadDailyContent()
    }

    private fun loadDailyContent() {
        // Sample content for "The Zeigarnik Effect"
        _dailyContent.value = DailyContent(
            id = "zeigarnik_effect",
            type = ContentType.PSYCHOLOGY_TRICK,
            title = "The Zeigarnik Effect",
            subtitle = "Cognitive Bias",
            content = "Your brain remembers unfinished tasks better than finished ones. Use this to your advantage: start a difficult task, then deliberately pause. Your brain will itch to complete it.",
            actionLabel = "Claim Upgrade"
        )
    }

    fun onContentAction(content: DailyContent) {
        // Handle action (e.g., mark as collected, open details)
    }
}
