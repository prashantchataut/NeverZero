package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores RPG-style user progression stats using a single-row pattern.
 * Only one row exists in this table (id = 1).
 *
 * @property id Always 1 (single-row pattern)
 * @property level Current player level
 * @property currentXp XP accumulated toward next level
 * @property xpToNextLevel XP required to level up (default: 100)
 * @property strength Physical power and endurance stat
 * @property intelligence Mental acuity and knowledge stat
 * @property wisdom Insight and experience stat
 * @property discipline Willpower and consistency stat
 * @property charisma Social influence and charm stat
 */
@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 100,
    val strength: Int = 1,
    val intelligence: Int = 1,
    val wisdom: Int = 1,
    val discipline: Int = 1,
    val charisma: Int = 1
) {
    /**
     * Calculates total stat points across all attributes.
     */
    val totalStatPoints: Int
        get() = strength + intelligence + wisdom + discipline + charisma

    /**
     * Returns XP progress as a fraction (0.0 to 1.0).
     */
    val xpProgress: Float
        get() = if (xpToNextLevel > 0) currentXp.toFloat() / xpToNextLevel else 0f
}

// --- Domain Mapping Extensions ---

data class UserStats(
    val level: Int,
    val currentXp: Int,
    val xpToNextLevel: Int,
    val strength: Int,
    val intelligence: Int,
    val wisdom: Int,
    val discipline: Int,
    val charisma: Int
) {
    val totalStatPoints: Int
        get() = strength + intelligence + wisdom + discipline + charisma

    val xpProgress: Float
        get() = if (xpToNextLevel > 0) currentXp.toFloat() / xpToNextLevel else 0f

    companion object {
        fun default(): UserStats = UserStats(
            level = 1,
            currentXp = 0,
            xpToNextLevel = 100,
            strength = 1,
            intelligence = 1,
            wisdom = 1,
            discipline = 1,
            charisma = 1
        )
    }
}

fun UserStatsEntity.toUserStats(): UserStats = UserStats(
    level = level,
    currentXp = currentXp,
    xpToNextLevel = xpToNextLevel,
    strength = strength,
    intelligence = intelligence,
    wisdom = wisdom,
    discipline = discipline,
    charisma = charisma
)

fun UserStats.toEntity(): UserStatsEntity = UserStatsEntity(
    id = 1,
    level = level,
    currentXp = currentXp,
    xpToNextLevel = xpToNextLevel,
    strength = strength,
    intelligence = intelligence,
    wisdom = wisdom,
    discipline = discipline,
    charisma = charisma
)
