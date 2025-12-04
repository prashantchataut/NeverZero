package com.productivitystreak.data.repository

import android.database.sqlite.SQLiteException
import com.productivitystreak.data.local.dao.UserStatsDao
import com.productivitystreak.data.local.entity.UserStats
import com.productivitystreak.data.local.entity.UserStatsEntity
import com.productivitystreak.data.local.entity.toUserStats
import com.productivitystreak.data.model.HabitAttribute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Result of an XP award operation.
 */
data class LevelUpResult(
    val leveledUp: Boolean,
    val newLevel: Int,
    val currentXp: Int,
    val xpGained: Int
)

/**
 * Repository for managing user gamification stats (XP, Level, RPG attributes).
 * Implements the core leveling mechanics.
 */
class GamificationRepository(
    private val userStatsDao: UserStatsDao
) {
    /**
     * Observe user stats as a Flow.
     */
    fun observeUserStats(): Flow<UserStats> =
        userStatsDao.observeStats().map { entity ->
            entity?.toUserStats() ?: UserStats.default()
        }

    /**
     * Get current user stats (one-shot).
     */
    suspend fun getUserStats(): UserStats =
        userStatsDao.getStats()?.toUserStats() ?: UserStats.default()

    /**
     * Initialize user stats if they don't exist.
     */
    suspend fun initializeIfNeeded(): RepositoryResult<Unit> {
        return try {
            if (userStatsDao.getStats() == null) {
                userStatsDao.upsert(UserStatsEntity())
            }
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    /**
     * Award XP for completing a protocol.
     *
     * Logic:
     * - Adds 10 XP per completion
     * - If XP >= 100, levels up and resets XP counter (keeping overflow)
     *
     * @param attribute Optional attribute to also increment (from completed protocol)
     * @return LevelUpResult with new stats and whether a level-up occurred
     */
    suspend fun awardXpForCompletion(
        attribute: HabitAttribute? = null
    ): RepositoryResult<LevelUpResult> {
        return try {
            val stats = userStatsDao.getStats() ?: run {
                userStatsDao.upsert(UserStatsEntity())
                userStatsDao.getStats()!!
            }

            val newXp = stats.currentXp + XP_PER_COMPLETION

            val result = if (newXp >= XP_THRESHOLD) {
                // Level Up!
                val overflowXp = newXp - XP_THRESHOLD
                val newLevel = stats.level + 1

                userStatsDao.updateLevelAndXp(newLevel, overflowXp)

                LevelUpResult(
                    leveledUp = true,
                    newLevel = newLevel,
                    currentXp = overflowXp,
                    xpGained = XP_PER_COMPLETION
                )
            } else {
                userStatsDao.updateXp(newXp)

                LevelUpResult(
                    leveledUp = false,
                    newLevel = stats.level,
                    currentXp = newXp,
                    xpGained = XP_PER_COMPLETION
                )
            }

            // Optionally increment the linked attribute
            if (attribute != null && attribute != HabitAttribute.NONE) {
                incrementAttribute(attribute)
            }

            RepositoryResult.Success(result)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    /**
     * Increment a specific attribute by 1 point.
     */
    suspend fun incrementAttribute(attribute: HabitAttribute): RepositoryResult<Unit> {
        return try {
            when (attribute) {
                HabitAttribute.STRENGTH -> userStatsDao.incrementStats(str = 1)
                HabitAttribute.INTELLIGENCE -> userStatsDao.incrementStats(intel = 1)
                HabitAttribute.WISDOM -> userStatsDao.incrementStats(wis = 1)
                HabitAttribute.DISCIPLINE -> userStatsDao.incrementStats(dis = 1)
                HabitAttribute.CHARISMA -> userStatsDao.incrementStats(cha = 1)
                HabitAttribute.NONE -> { /* No-op */ }
            }
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    /**
     * Reset all user stats to defaults.
     */
    suspend fun resetStats(): RepositoryResult<Unit> {
        return try {
            userStatsDao.deleteAll()
            userStatsDao.upsert(UserStatsEntity())
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    companion object {
        /** XP awarded per protocol completion */
        const val XP_PER_COMPLETION = 10

        /** XP required to level up */
        const val XP_THRESHOLD = 100
    }
}
