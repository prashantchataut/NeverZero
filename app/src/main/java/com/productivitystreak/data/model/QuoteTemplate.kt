package com.productivitystreak.data.model

enum class QuoteCategory {
    MOMENTUM_BUILDER,
    COMEBACK_COACH,
    RESCUE_MOTIVATOR,
    ACHIEVEMENT_CELEBRATOR,
    IDENTITY_REINFORCER
}

data class QuoteContext(
    val minStreakDays: Int? = null,
    val maxStreakDays: Int? = null,
    val hourOfDayMin: Int? = null,
    val hourOfDayMax: Int? = null,
    val requiresRescue: Boolean = false,
    val requiresBrokenStreak: Boolean = false,
    val minCompletionRate: Int? = null,
    val maxCompletionRate: Int? = null
)

data class QuoteTemplate(
    val id: String,
    val category: QuoteCategory,
    val template: String,
    val context: QuoteContext
)
