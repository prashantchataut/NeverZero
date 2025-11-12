package com.productivitystreak.ui.state.vocabulary

data class VocabularyState(
    val currentStreakDays: Int = 0,
    val wordsAddedToday: Int = 0,
    val words: List<VocabularyWord> = emptyList()
)

data class VocabularyWord(
    val word: String,
    val definition: String,
    val example: String? = null,
    val addedToday: Boolean = false
)
