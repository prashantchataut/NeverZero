package com.productivitystreak.data.repository

import android.database.sqlite.SQLiteException
import com.productivitystreak.data.local.dao.DailyLogDao
import com.productivitystreak.data.local.dao.ProtocolDao
import com.productivitystreak.data.local.entity.DailyLogEntity
import com.productivitystreak.data.local.entity.LogStatus
import com.productivitystreak.data.local.entity.Protocol
import com.productivitystreak.data.local.entity.ProtocolEntity
import com.productivitystreak.data.local.entity.ProtocolFrequency
import com.productivitystreak.data.local.entity.ProtocolType
import com.productivitystreak.data.local.entity.toProtocol
import com.productivitystreak.data.model.HabitAttribute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

/**
 * Repository for managing Identity Protocols and their daily tracking.
 * Provides CRUD operations and streak calculation logic.
 */
class ProtocolRepository(
    private val protocolDao: ProtocolDao,
    private val dailyLogDao: DailyLogDao
) {
    /**
     * Observe all active protocols as a Flow.
     */
    fun observeActiveProtocols(): Flow<List<Protocol>> =
        protocolDao.observeActiveProtocols().map { entities ->
            entities.map { it.toProtocol() }
        }

    /**
     * Observe all protocols (including inactive).
     */
    fun observeAllProtocols(): Flow<List<Protocol>> =
        protocolDao.observeAllProtocols().map { entities ->
            entities.map { it.toProtocol() }
        }

    /**
     * Get a single protocol by ID.
     */
    suspend fun getProtocolById(id: String): Protocol? =
        protocolDao.getById(id)?.toProtocol()

    /**
     * Create a new protocol.
     *
     * @return The ID of the created protocol, or an error result.
     */
    suspend fun createProtocol(
        name: String,
        type: ProtocolType = ProtocolType.DAILY,
        icon: String = "check_circle",
        frequency: ProtocolFrequency = ProtocolFrequency.DAILY,
        linkedAttribute: HabitAttribute = HabitAttribute.DISCIPLINE
    ): RepositoryResult<String> {
        return try {
            val id = UUID.randomUUID().toString()
            val protocol = ProtocolEntity(
                id = id,
                name = name,
                type = type,
                icon = icon,
                frequency = frequency,
                linkedAttribute = linkedAttribute,
                createdAt = System.currentTimeMillis(),
                isActive = true
            )
            protocolDao.insert(protocol)
            RepositoryResult.Success(id)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    /**
     * Mark a protocol as complete for a specific date.
     *
     * @param protocolId The ID of the protocol
     * @param date ISO date string (e.g., "2025-12-04"), defaults to today
     * @return Success with the log ID, or error result
     */
    suspend fun markComplete(
        protocolId: String,
        date: String = LocalDate.now().toString()
    ): RepositoryResult<Long> {
        return try {
            val existingLog = dailyLogDao.getLogForDate(protocolId, date)
            val log = DailyLogEntity(
                id = existingLog?.id ?: 0,
                date = date,
                protocolId = protocolId,
                status = LogStatus.COMPLETE,
                completedAt = System.currentTimeMillis()
            )
            dailyLogDao.upsert(log)
            RepositoryResult.Success(log.id)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    /**
     * Calculate the current streak for a protocol.
     *
     * Counts consecutive completed dates backwards from today.
     * If today is not completed, starts checking from yesterday.
     *
     * @param protocolId The ID of the protocol
     * @return Number of consecutive days completed
     */
    suspend fun calculateCurrentStreak(protocolId: String): Int {
        val completedLogs = dailyLogDao.getCompletedLogs(protocolId)
        if (completedLogs.isEmpty()) return 0

        // Build a set of completed dates for O(1) lookup
        val completedDates = completedLogs.map { LocalDate.parse(it.date) }.toSet()

        var streak = 0
        var checkDate = LocalDate.now()

        // If today is not completed, start from yesterday
        if (!completedDates.contains(checkDate)) {
            checkDate = checkDate.minusDays(1)
        }

        // Count consecutive completed days
        while (completedDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }

    /**
     * Get the longest streak ever achieved for a protocol.
     *
     * @param protocolId The ID of the protocol
     * @return The longest consecutive days completed
     */
    suspend fun calculateLongestStreak(protocolId: String): Int {
        val completedLogs = dailyLogDao.getCompletedLogs(protocolId)
        if (completedLogs.isEmpty()) return 0

        val sortedDates = completedLogs
            .map { LocalDate.parse(it.date) }
            .sortedDescending()

        var longestStreak = 1
        var currentStreak = 1

        for (i in 1 until sortedDates.size) {
            val dayDiff = sortedDates[i - 1].toEpochDay() - sortedDates[i].toEpochDay()
            if (dayDiff == 1L) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        return longestStreak
    }

    /**
     * Delete a protocol and all its logs.
     */
    suspend fun deleteProtocol(id: String): RepositoryResult<Unit> {
        return try {
            protocolDao.deleteById(id)
            // Logs are automatically deleted via CASCADE
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    /**
     * Toggle a protocol's active status.
     */
    suspend fun setProtocolActive(id: String, isActive: Boolean): RepositoryResult<Unit> {
        return try {
            protocolDao.setActive(id, isActive)
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }
}
