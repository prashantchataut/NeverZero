package com.productivitystreak.ui.state.home

enum class ContentType {
    MENTAL_MODEL,
    PSYCHOLOGY_TRICK,
    VOCABULARY,
    BOOK_EXCERPT,
    PHILOSOPHY
}

data class DailyContent(
    val id: String,
    val type: ContentType,
    val title: String,
    val subtitle: String?, // pronunciation for vocab, category for others
    val content: String,   // definition or main content
    val actionLabel: String // "Add to Vocabulary", "Learn More", "Take Quiz"
)
