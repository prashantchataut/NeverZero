package com.productivitystreak.ui.state.vocabulary

import com.productivitystreak.data.gemini.TeachingLesson

data class VocabularyState(
    val currentStreakDays: Int = 0,
    val wordsAddedToday: Int = 0,
    val words: List<VocabularyWord> = emptyList(),
    val wordOfTheDay: VocabularyWord? = null
)

data class VocabularyWord(
    val word: String,
    val definition: String,
    val example: String? = null,
    val addedToday: Boolean = false
)

data class TeachWordUiState(
    val wordInput: String = "",
    val learnerContext: String = "",
    val isGenerating: Boolean = false,
    val lesson: TeachingLesson? = null,
    val errorMessage: String? = null
)
