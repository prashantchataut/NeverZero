package com.productivitystreak.ui.state.stats

data class StatsState(
    val currentLongestStreak: Int = 0,
    val currentLongestStreakName: String = "",
    val averageDailyProgressPercent: Int = 0,
    val averageDailyTrend: AverageDailyTrend? = null,
    val streakConsistency: List<ConsistencyScore> = emptyList(),
    val habitBreakdown: List<HabitBreakdown> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val calendarHeatMap: CalendarHeatMap? = null
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

data class AverageDailyTrend(
    val windowSize: Int,
    val points: List<TrendPoint>
)

data class TrendPoint(
    val date: String,
    val percent: Int
)

data class ConsistencyScore(
    val streakId: String,
    val streakName: String,
    val score: Int,
    val completionRate: Int,
    val variance: Float,
    val level: ConsistencyLevel
)

enum class ConsistencyLevel { High, Medium, NeedsAttention }

data class CalendarHeatMap(
    val weeks: List<HeatMapWeek>,
    val completedDays: Int,
    val totalDays: Int
)

data class HeatMapWeek(val days: List<HeatMapDay>)

data class HeatMapDay(
    val date: String,
    val intensity: Float,
    val isToday: Boolean
)
