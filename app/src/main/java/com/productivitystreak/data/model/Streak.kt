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
    val icon: String = "flag"
) {
    val progress: Float
        get() = history.lastOrNull()?.completionFraction ?: 0f
}

data class StreakDayRecord(
    val date: String,
    val completed: Int,
    val goal: Int
) {
    val completionFraction: Float
        get() = if (goal == 0) 0f else (completed / goal.toFloat()).coerceAtMost(1f)

    val metGoal: Boolean
        get() = completed >= goal
}
