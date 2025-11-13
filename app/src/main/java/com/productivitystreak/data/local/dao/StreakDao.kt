package com.productivitystreak.data.local.dao

import androidx.room.*
import com.productivitystreak.data.local.entity.StreakEntity
import com.productivitystreak.data.local.entity.StreakLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE isActive = 1 ORDER BY createdAt DESC")
    fun observeActiveStreaks(): Flow<List<StreakEntity>>

    @Query("SELECT * FROM streaks WHERE id = :id")
    suspend fun getStreakById(id: String): StreakEntity?

    @Query("SELECT * FROM streak_logs WHERE streakId = :streakId ORDER BY date DESC LIMIT :limit")
    suspend fun getStreakLogs(streakId: String, limit: Int = 30): List<StreakLogEntity>

    @Query("SELECT * FROM streak_logs WHERE streakId = :streakId AND date >= :startDate ORDER BY date DESC")
    suspend fun getStreakLogsSince(streakId: String, startDate: Long): List<StreakLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreaks(streaks: List<StreakEntity>)

    @Insert
    suspend fun insertStreakLog(log: StreakLogEntity)

    @Update
    suspend fun updateStreak(streak: StreakEntity)

    @Delete
    suspend fun deleteStreak(streak: StreakEntity)

    @Query("DELETE FROM streaks WHERE id = :streakId")
    suspend fun deleteStreakById(streakId: String)

    @Query("SELECT COUNT(*) FROM streaks WHERE isActive = 1")
    suspend fun getActiveStreakCount(): Int

    @Query("""
        SELECT * FROM streak_logs
        WHERE date >= :startDate AND date < :endDate
        ORDER BY date DESC
    """)
    suspend fun getLogsInRange(startDate: Long, endDate: Long): List<StreakLogEntity>
}
