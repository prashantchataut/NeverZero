package com.productivitystreak.data.model

data class Streak(
    val id: String,
    val name: String,
    val currentCount: Int,
    val longestCount: Int,
    val goalPerDay: Int,
    val unit: String,
    val category: String,
    val history: List<StreakDayRecord>,
    val color: String = "#6366F1",
    val icon: String = "flag",
    val frequency: StreakFrequency = StreakFrequency.DAILY,
    val targetPerPeriod: Int? = null,
    val customDaysOfWeek: List<String> = emptyList(),
    val reminderEnabled: Boolean = true,
    val reminderTime: String = "09:00",
    val difficulty: StreakDifficulty = StreakDifficulty.BALANCED,
    val allowFreezeDays: Boolean = true,
    val rescuedDates: List<String> = emptyList()
) {
    val progress: Float
        get() = history.lastOrNull()?.completionFraction ?: 0f
}

enum class StreakFrequency {
    DAILY,
    WEEKLY,
    CUSTOM
}

enum class StreakDifficulty {
    EASY,
    BALANCED,
    CHALLENGING
}

data class StreakDayRecord(
    val date: String,
    val completed: Int,
    val goal: Int,
    val wasRescued: Boolean = false,
    val completedAt: Long? = null
) {
    val completionFraction: Float
        get() = if (goal == 0) 0f else (completed / goal.toFloat()).coerceAtMost(1f)

    val metGoal: Boolean
        get() = completed >= goal
}
