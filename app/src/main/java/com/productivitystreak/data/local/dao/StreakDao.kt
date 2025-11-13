package com.productivitystreak.data.local.dao

import androidx.room.*
import com.productivitystreak.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE isArchived = 0 ORDER BY lastUpdated DESC")
    fun getAllStreaks(): Flow<List<StreakEntity>>

    @Query("SELECT * FROM streaks WHERE isArchived = 1 ORDER BY lastUpdated DESC")
    fun getArchivedStreaks(): Flow<List<StreakEntity>>

    @Query("SELECT * FROM streaks WHERE id = :id")
    suspend fun getStreakById(id: String): StreakEntity?

    @Query("SELECT * FROM streaks WHERE category = :category AND isArchived = 0")
    fun getStreaksByCategory(category: String): Flow<List<StreakEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreaks(streaks: List<StreakEntity>)

    @Update
    suspend fun updateStreak(streak: StreakEntity)

    @Delete
    suspend fun deleteStreak(streak: StreakEntity)

    @Query("DELETE FROM streaks WHERE id = :id")
    suspend fun deleteStreakById(id: String)

    @Query("UPDATE streaks SET isArchived = 1 WHERE id = :id")
    suspend fun archiveStreak(id: String)

    @Query("UPDATE streaks SET isArchived = 0 WHERE id = :id")
    suspend fun unarchiveStreak(id: String)

    @Query("SELECT COUNT(*) FROM streaks WHERE isArchived = 0")
    suspend fun getActiveStreakCount(): Int

    @Query("SELECT * FROM streaks WHERE currentCount > 0 AND isArchived = 0 ORDER BY currentCount DESC LIMIT :limit")
    fun getTopStreaks(limit: Int): Flow<List<StreakEntity>>

    @Query("DELETE FROM streaks")
    suspend fun deleteAllStreaks()
}
