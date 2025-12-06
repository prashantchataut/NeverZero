package com.productivitystreak.domain.usecase

import com.productivitystreak.data.model.HabitAttribute
import com.productivitystreak.data.model.RpgStats
import com.productivitystreak.data.model.Streak
import com.productivitystreak.data.model.StreakDifficulty

/**
 * Shared use case for RPG stats calculation.
 * Eliminates duplication between ProfileViewModel and StreakViewModel.
 */
class RpgStatsUseCase {

    fun computeRpgStats(streaks: List<Streak>): RpgStats {
        if (streaks.isEmpty()) return RpgStats()

        var strengthXp = 0
        var intelligenceXp = 0
        var charismaXp = 0
        var wisdomXp = 0
        var disciplineXp = 0

        streaks.forEach { streak ->
            val attribute = mapCategoryToAttribute(streak.category)
            val basePerCompletion = when (streak.difficulty) {
                StreakDifficulty.EASY -> 8
                StreakDifficulty.BALANCED -> 12
                StreakDifficulty.CHALLENGING -> 18
            }
            val completedDays = streak.history.count { it.metGoal }
            if (completedDays == 0) return@forEach

            val xp = completedDays * basePerCompletion

            when (attribute) {
                HabitAttribute.STRENGTH -> strengthXp += xp
                HabitAttribute.INTELLIGENCE -> intelligenceXp += xp
                HabitAttribute.CHARISMA -> charismaXp += xp
                HabitAttribute.WISDOM -> wisdomXp += xp
                HabitAttribute.DISCIPLINE, HabitAttribute.NONE -> disciplineXp += xp
            }

            // General discipline grows with every day the user meets any goal
            disciplineXp += completedDays * 4
        }

        val strength = xpToStat(strengthXp)
        val intelligence = xpToStat(intelligenceXp)
        val charisma = xpToStat(charismaXp)
        val wisdom = xpToStat(wisdomXp)
        val discipline = xpToStat(disciplineXp)

        val totalXp = strengthXp + intelligenceXp + charismaXp + wisdomXp + disciplineXp
        val level = if (totalXp <= 0) 1 else (totalXp / 100) + 1
        val currentXp = if (totalXp <= 0) 0 else totalXp % 100
        val xpToNextLevel = if (totalXp <= 0) 100 else 100 - currentXp

        return RpgStats(
            strength = strength,
            intelligence = intelligence,
            charisma = charisma,
            wisdom = wisdom,
            discipline = discipline,
            level = level,
            currentXp = currentXp,
            xpToNextLevel = xpToNextLevel
        )
    }

    fun mapCategoryToAttribute(category: String): HabitAttribute {
        val normalized = category.lowercase()
        return when {
            "read" in normalized || "study" in normalized || "learn" in normalized || 
            "vocab" in normalized || "language" in normalized -> HabitAttribute.INTELLIGENCE
            
            "fitness" in normalized || "workout" in normalized || "run" in normalized || 
            "gym" in normalized || "strength" in normalized -> HabitAttribute.STRENGTH
            
            "social" in normalized || "network" in normalized || "friend" in normalized || 
            "relationship" in normalized -> HabitAttribute.CHARISMA
            
            "meditat" in normalized || "mindful" in normalized || "journal" in normalized || 
            "reflect" in normalized || "wellness" in normalized || "sleep" in normalized -> HabitAttribute.WISDOM
            
            else -> HabitAttribute.DISCIPLINE
        }
    }

    private fun xpToStat(xp: Int): Int {
        if (xp <= 0) return 1
        val stat = xp / 50 + 1
        return stat.coerceIn(1, 10)
    }
}
