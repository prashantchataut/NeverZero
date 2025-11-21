package com.productivitystreak.notifications

import com.productivitystreak.data.model.Streak
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class SmartNotificationEngine {

    fun getOptimalNotificationTime(streak: Streak): Int {
        // Default to 9 AM if no history
        if (streak.history.isEmpty()) return 9

        // Filter for completed days with timestamps
        val completedTimes = streak.history
            .filter { it.metGoal && it.completedAt != null }
            .mapNotNull { it.completedAt }

        if (completedTimes.isEmpty()) return 9

        // Calculate average hour of completion
        val hours = completedTimes.map { timestamp ->
            LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).hour
        }

        val averageHour = hours.average().toInt()

        // Suggest a time 1-2 hours before their average completion time
        // Ensure it's not too early (e.g., before 7 AM) unless they are early risers
        return (averageHour - 2).coerceAtLeast(7)
    }

    fun checkMilestone(streak: Streak): MilestoneType? {
        val count = streak.currentCount
        return when {
            count == 3 -> MilestoneType.STARTING_STRONG
            count == 7 -> MilestoneType.WEEK_WARRIOR
            count == 14 -> MilestoneType.TWO_WEEKS
            count == 30 -> MilestoneType.MONTH_MASTER
            count == 100 -> MilestoneType.CENTURION
            count > 0 && count % 50 == 0 -> MilestoneType.BIG_MILESTONE
            else -> null
        }
    }

    fun checkStreakDanger(streak: Streak): DangerLevel {
        val now = LocalDateTime.now()
        val hour = now.hour
        
        // If already completed today, no danger
        val today = now.toLocalDate().toString()
        val todayRecord = streak.history.find { it.date == today }
        if (todayRecord?.metGoal == true) return DangerLevel.SAFE

        // Danger zones based on time of day
        return when {
            hour >= 23 -> DangerLevel.CRITICAL // < 1 hour left
            hour >= 20 -> DangerLevel.HIGH     // < 4 hours left
            hour >= 17 -> DangerLevel.MODERATE // Evening
            else -> DangerLevel.SAFE
        }
    }

    enum class MilestoneType(val title: String, val message: String) {
        STARTING_STRONG("Starting Strong", "3 days in a row! You're building momentum."),
        WEEK_WARRIOR("Week Warrior", "7 days straight! You've conquered the week."),
        TWO_WEEKS("Unstoppable", "14 days! Two weeks of consistency."),
        MONTH_MASTER("Month Master", "30 days! You've built a solid habit."),
        CENTURION("Centurion", "100 days! You are in the top 1%."),
        BIG_MILESTONE("Milestone Reached", "Another 50 days down! Keep it up.")
    }

    enum class DangerLevel {
        SAFE,
        MODERATE,
        HIGH,
        CRITICAL
    }
}
