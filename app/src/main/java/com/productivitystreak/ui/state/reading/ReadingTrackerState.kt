package com.productivitystreak.ui.state.reading

data class ReadingTrackerState(
    val currentStreakDays: Int = 0,
    val pagesReadToday: Int = 0,
    val goalPagesPerDay: Int = 30,
    val recentActivity: List<ReadingLog> = emptyList()
) {
    val progressFraction: Float
        get() = pagesReadToday / goalPagesPerDay.toFloat().coerceAtLeast(1f)
}

data class ReadingLog(
    val dateLabel: String,
    val pages: Int
)
