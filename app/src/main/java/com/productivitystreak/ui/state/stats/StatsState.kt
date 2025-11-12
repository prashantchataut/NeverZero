package com.productivitystreak.ui.state.stats

data class StatsState(
    val currentLongestStreak: Int = 0,
    val currentLongestStreakName: String = "",
    val averageDailyProgressPercent: Int = 0,
    val averageDailyTrend: List<Int> = emptyList(),
    val streakConsistency: List<Int> = emptyList(),
    val habitBreakdown: List<HabitBreakdown> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList()
)

data class HabitBreakdown(
    val name: String,
    val completionPercent: Int,
    val accentHex: String
)

data class LeaderboardEntry(
    val position: Int,
    val name: String,
    val streakDays: Int
)
