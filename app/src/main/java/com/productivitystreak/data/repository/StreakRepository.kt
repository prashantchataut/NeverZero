package com.productivitystreak.data.repository

import com.productivitystreak.data.local.dao.StreakDao
import com.productivitystreak.data.local.entity.StreakEntity
import com.productivitystreak.data.local.entity.toEntity
import com.productivitystreak.data.model.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class StreakRepository(private val streakDao: StreakDao) {

    fun observeStreaks(): Flow<List<Streak>> =
        streakDao.getAllStreaks().map { entities ->
            entities.map { it.toStreak() }
        }

    fun observeArchivedStreaks(): Flow<List<Streak>> =
        streakDao.getArchivedStreaks().map { entities ->
            entities.map { it.toStreak() }
        }

    fun observeStreaksByCategory(category: String): Flow<List<Streak>> =
        streakDao.getStreaksByCategory(category).map { entities ->
            entities.map { it.toStreak() }
        }

    fun observeTopStreaks(limit: Int = 5): Flow<List<Streak>> =
        streakDao.getTopStreaks(limit).map { entities ->
            entities.map { it.toStreak() }
        }

    suspend fun getStreakById(id: String): Streak? =
        streakDao.getStreakById(id)?.toStreak()

    suspend fun createStreak(
        name: String,
        goalPerDay: Int,
        unit: String,
        category: String,
        color: String = "#6366F1",
        icon: String = "flag"
    ): String {
        val id = UUID.randomUUID().toString()
        val streak = StreakEntity(
            id = id,
            name = name,
            currentCount = 0,
            longestCount = 0,
            goalPerDay = goalPerDay,
            unit = unit,
            category = category,
            history = emptyList(),
            color = color,
            icon = icon
        )
        streakDao.insertStreak(streak)
        return id
    }

    suspend fun logProgress(streakId: String, value: Int) {
        val streak = streakDao.getStreakById(streakId) ?: return

        val newHistory = streak.history + value
        val dayCompleted = value >= streak.goalPerDay
        val newCurrentCount = if (dayCompleted) streak.currentCount + 1 else streak.currentCount
        val newLongestCount = maxOf(streak.longestCount, newCurrentCount)

        val updatedStreak = streak.copy(
            currentCount = newCurrentCount,
            longestCount = newLongestCount,
            history = newHistory,
            lastUpdated = System.currentTimeMillis()
        )

        streakDao.updateStreak(updatedStreak)
    }

    suspend fun updateStreak(streak: Streak) {
        val entity = streak.toEntity().copy(
            lastUpdated = System.currentTimeMillis()
        )
        streakDao.updateStreak(entity)
    }

    suspend fun archiveStreak(streakId: String) {
        streakDao.archiveStreak(streakId)
    }

    suspend fun unarchiveStreak(streakId: String) {
        streakDao.unarchiveStreak(streakId)
    }

    suspend fun deleteStreak(streakId: String) {
        streakDao.deleteStreakById(streakId)
    }

    suspend fun useFreezeDay(streakId: String): Boolean {
        val streak = streakDao.getStreakById(streakId) ?: return false

        if (streak.freezeDaysAvailable <= 0) return false

        val updatedStreak = streak.copy(
            freezeDaysUsed = streak.freezeDaysUsed + 1,
            freezeDaysAvailable = streak.freezeDaysAvailable - 1,
            lastUpdated = System.currentTimeMillis()
        )

        streakDao.updateStreak(updatedStreak)
        return true
    }

    suspend fun getActiveStreakCount(): Int = streakDao.getActiveStreakCount()

    suspend fun initializeSampleData() {
        val count = streakDao.getActiveStreakCount()
        if (count == 0) {
            streakDao.insertStreaks(sampleStreaks())
        }
    }

    companion object {
        private fun sampleStreaks(): List<StreakEntity> = listOf(
            StreakEntity(
                id = "reading",
                name = "Read 30 mins",
                currentCount = 15,
                longestCount = 42,
                goalPerDay = 30,
                unit = "minutes",
                category = "Reading",
                history = listOf(30, 35, 25, 40, 30),
                color = "#8B5CF6",
                icon = "book"
            ),
            StreakEntity(
                id = "vocabulary",
                name = "Add 5 new words",
                currentCount = 9,
                longestCount = 28,
                goalPerDay = 5,
                unit = "words",
                category = "Vocabulary",
                history = listOf(5, 3, 6, 5, 7),
                color = "#10B981",
                icon = "school"
            ),
            StreakEntity(
                id = "wellness",
                name = "Meditation",
                currentCount = 23,
                longestCount = 60,
                goalPerDay = 10,
                unit = "minutes",
                category = "Wellness",
                history = listOf(8, 12, 10, 9, 11),
                color = "#F59E0B",
                icon = "spa"
            )
        )
    }
}
