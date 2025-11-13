package com.productivitystreak.data.repository

import com.productivitystreak.data.local.dao.StreakDao
import com.productivitystreak.data.local.entity.StreakEntity
import com.productivitystreak.data.local.entity.StreakLogEntity
import com.productivitystreak.data.local.entity.toDomain
import com.productivitystreak.data.local.entity.toEntity
import com.productivitystreak.data.model.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class StreakRepository(private val streakDao: StreakDao) {

    fun observeStreaks(): Flow<List<Streak>> = streakDao.observeActiveStreaks().map { entities ->
        entities.map { entity ->
            val logs = streakDao.getStreakLogs(entity.id, limit = 30)
            entity.toDomain(logs.map { it.value })
        }
    }

    suspend fun getStreakById(streakId: String): Streak? {
        val entity = streakDao.getStreakById(streakId) ?: return null
        val logs = streakDao.getStreakLogs(streakId, limit = 30)
        return entity.toDomain(logs.map { it.value })
    }

    suspend fun createStreak(streak: Streak) {
        streakDao.insertStreak(streak.toEntity())
    }

    suspend fun updateStreak(streak: Streak) {
        streakDao.updateStreak(streak.toEntity())
    }

    suspend fun deleteStreak(streakId: String) {
        streakDao.deleteStreakById(streakId)
    }

    suspend fun logProgress(streakId: String, value: Int) {
        val entity = streakDao.getStreakById(streakId) ?: return
        val today = getTodayStartMillis()

        // Insert log entry
        streakDao.insertStreakLog(
            StreakLogEntity(
                streakId = streakId,
                value = value,
                date = System.currentTimeMillis()
            )
        )

        // Update streak counts
        val isGoalMet = value >= entity.goalPerDay
        val lastCompletedDate = entity.lastCompletedDate ?: 0
        val isConsecutiveDay = isConsecutive(lastCompletedDate, today)

        val updatedCount = when {
            isGoalMet && isConsecutiveDay -> entity.currentCount + 1
            isGoalMet && !isConsecutiveDay -> 1
            else -> entity.currentCount
        }

        val updatedLongest = maxOf(entity.longestCount, updatedCount)

        streakDao.updateStreak(
            entity.copy(
                currentCount = updatedCount,
                longestCount = updatedLongest,
                lastCompletedDate = if (isGoalMet) today else entity.lastCompletedDate
            )
        )
    }

    suspend fun initializeSampleData() {
        val count = streakDao.getActiveStreakCount()
        if (count == 0) {
            val sampleStreaks = listOf(
                StreakEntity(
                    id = "reading",
                    name = "Read 30 mins",
                    currentCount = 15,
                    longestCount = 42,
                    goalPerDay = 30,
                    unit = "minutes",
                    category = "Reading"
                ),
                StreakEntity(
                    id = "vocabulary",
                    name = "Add 5 new words",
                    currentCount = 9,
                    longestCount = 28,
                    goalPerDay = 5,
                    unit = "words",
                    category = "Vocabulary"
                ),
                StreakEntity(
                    id = "wellness",
                    name = "Meditation",
                    currentCount = 23,
                    longestCount = 60,
                    goalPerDay = 10,
                    unit = "minutes",
                    category = "Wellness"
                )
            )
            streakDao.insertStreaks(sampleStreaks)

            // Add some sample logs
            val sampleLogs = listOf(
                StreakLogEntity(streakId = "reading", value = 30, date = System.currentTimeMillis() - 86400000 * 4),
                StreakLogEntity(streakId = "reading", value = 35, date = System.currentTimeMillis() - 86400000 * 3),
                StreakLogEntity(streakId = "reading", value = 25, date = System.currentTimeMillis() - 86400000 * 2),
                StreakLogEntity(streakId = "reading", value = 40, date = System.currentTimeMillis() - 86400000),
                StreakLogEntity(streakId = "reading", value = 30, date = System.currentTimeMillis()),

                StreakLogEntity(streakId = "vocabulary", value = 5, date = System.currentTimeMillis() - 86400000 * 4),
                StreakLogEntity(streakId = "vocabulary", value = 3, date = System.currentTimeMillis() - 86400000 * 3),
                StreakLogEntity(streakId = "vocabulary", value = 6, date = System.currentTimeMillis() - 86400000 * 2),
                StreakLogEntity(streakId = "vocabulary", value = 5, date = System.currentTimeMillis() - 86400000),
                StreakLogEntity(streakId = "vocabulary", value = 7, date = System.currentTimeMillis()),

                StreakLogEntity(streakId = "wellness", value = 8, date = System.currentTimeMillis() - 86400000 * 4),
                StreakLogEntity(streakId = "wellness", value = 12, date = System.currentTimeMillis() - 86400000 * 3),
                StreakLogEntity(streakId = "wellness", value = 10, date = System.currentTimeMillis() - 86400000 * 2),
                StreakLogEntity(streakId = "wellness", value = 9, date = System.currentTimeMillis() - 86400000),
                StreakLogEntity(streakId = "wellness", value = 11, date = System.currentTimeMillis())
            )
            sampleLogs.forEach { streakDao.insertStreakLog(it) }
        }
    }

    private fun getTodayStartMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun isConsecutive(lastDate: Long, currentDate: Long): Boolean {
        if (lastDate == 0L) return false
        val oneDayMillis = 86400000L
        val diff = currentDate - lastDate
        return diff in oneDayMillis..(oneDayMillis * 2)
    }
}
