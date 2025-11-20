package com.productivitystreak.data.model

import java.time.LocalDate
import java.time.LocalTime

data class UserContext(
    val userName: String,
    val currentStreakDays: Int,
    val totalTasksToday: Int,
    val completedTasksToday: Int,
    val timeOfDay: LocalTime,
    val lastActivityDate: LocalDate?,
    val totalPoints: Int
) {
    val completionRate: Int
        get() = if (totalTasksToday > 0) {
            ((completedTasksToday.toFloat() / totalTasksToday) * 100).toInt()
        } else 0

    val hourOfDay: Int
        get() = timeOfDay.hour

    val needsRescue: Boolean
        get() = hourOfDay >= 17 && completedTasksToday == 0 && totalTasksToday > 0

    val hasBrokenStreak: Boolean
        get() = lastActivityDate?.let { 
            val daysSinceLastActivity = java.time.temporal.ChronoUnit.DAYS.between(it, LocalDate.now())
            daysSinceLastActivity > 1
        } ?: false
}
