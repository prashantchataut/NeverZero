package com.productivitystreak.data.repository

import android.database.sqlite.SQLiteException
import com.productivitystreak.data.local.dao.StreakDao
import com.productivitystreak.data.local.entity.StreakEntity
import com.productivitystreak.data.local.entity.toEntity
import com.productivitystreak.data.model.Streak
import com.productivitystreak.data.model.StreakDayRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
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
    ): RepositoryResult<String> {
        return try {
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
            RepositoryResult.Success(id)
        } catch (exception: Exception) {
            mapError(exception)
        }
    }

    suspend fun logProgress(streakId: String, value: Int): RepositoryResult<Unit> {
        return try {
            val streak = streakDao.getStreakById(streakId)
                ?: return RepositoryResult.DbError(IllegalStateException("Streak not found"))

            val today = LocalDate.now().toString()
            val updatedHistory = streak.history.toMutableList()
            val lastEntry = updatedHistory.lastOrNull()

            if (lastEntry?.date == today) {
                updatedHistory[updatedHistory.lastIndex] = lastEntry.copy(
                    completed = (lastEntry.completed + value).coerceAtLeast(value)
                )
            } else {
                updatedHistory.add(
                    StreakDayRecord(
                        date = today,
                        completed = value,
                        goal = streak.goalPerDay
                    )
                )
            }

            val dayCompleted = updatedHistory.lastOrNull()?.metGoal == true
            val newCurrentCount = if (dayCompleted) streak.currentCount + 1 else streak.currentCount
            val newLongestCount = maxOf(streak.longestCount, newCurrentCount)

            val updatedStreak = streak.copy(
                currentCount = newCurrentCount,
                longestCount = newLongestCount,
                history = updatedHistory,
                lastUpdated = System.currentTimeMillis()
            )

            streakDao.updateStreak(updatedStreak)
            RepositoryResult.Success(Unit)
        } catch (exception: Exception) {
            mapError(exception)
        }
    }

    suspend fun updateStreak(streak: Streak): RepositoryResult<Unit> {
        return try {
            val entity = streak.toEntity().copy(
                lastUpdated = System.currentTimeMillis()
            )
            streakDao.updateStreak(entity)
            RepositoryResult.Success(Unit)
        } catch (exception: Exception) {
            mapError(exception)
        }
    }

    suspend fun archiveStreak(streakId: String): RepositoryResult<Unit> {
        return runRepositoryCall { streakDao.archiveStreak(streakId) }
    }

    suspend fun unarchiveStreak(streakId: String): RepositoryResult<Unit> {
        return runRepositoryCall { streakDao.unarchiveStreak(streakId) }
    }

    suspend fun deleteStreak(streakId: String): RepositoryResult<Unit> {
        return runRepositoryCall { streakDao.deleteStreakById(streakId) }
    }

    suspend fun useFreezeDay(streakId: String): RepositoryResult<Boolean> {
        return try {
            val streak = streakDao.getStreakById(streakId)
                ?: return RepositoryResult.DbError(IllegalStateException("Streak not found"))

            if (streak.freezeDaysAvailable <= 0) return RepositoryResult.Success(false)

            val updatedStreak = streak.copy(
                freezeDaysUsed = streak.freezeDaysUsed + 1,
                freezeDaysAvailable = streak.freezeDaysAvailable - 1,
                lastUpdated = System.currentTimeMillis()
            )

            streakDao.updateStreak(updatedStreak)
            RepositoryResult.Success(true)
        } catch (exception: Exception) {
            mapError(exception)
        }
    }

    suspend fun getActiveStreakCount(): Int = streakDao.getActiveStreakCount()

    suspend fun initializeSampleData() {
        val count = streakDao.getActiveStreakCount()
        if (count == 0) {
            streakDao.insertStreaks(sampleStreaks())
        }
    }

    companion object {
        private fun sampleStreaks(): List<StreakEntity> {
            val today = LocalDate.now()
            fun history(goal: Int): List<StreakDayRecord> = (4 downTo 0).map { offset ->
                val date = today.minusDays(offset.toLong()).toString()
                StreakDayRecord(date = date, completed = goal, goal = goal)
            }

            return listOf(
                StreakEntity(
                    id = "reading",
                    name = "Read 30 mins",
                    currentCount = 15,
                    longestCount = 42,
                    goalPerDay = 30,
                    unit = "minutes",
                    category = "Reading",
                    history = history(30),
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
                    history = history(5),
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
                    history = history(10),
                    color = "#F59E0B",
                    icon = "spa"
                )
            )
        }

        private fun <T> runRepositoryCall(block: () -> T): RepositoryResult<Unit> {
            return try {
                block()
                RepositoryResult.Success(Unit)
            } catch (exception: Exception) {
                mapError(exception)
            }
        }

        private fun mapError(exception: Exception): RepositoryResult<Nothing> {
            return when (exception) {
                is SQLiteException -> RepositoryResult.DbError(exception)
                is SecurityException -> RepositoryResult.PermissionError(exception)
                else -> RepositoryResult.UnknownError(exception)
            }
        }
    }
}
