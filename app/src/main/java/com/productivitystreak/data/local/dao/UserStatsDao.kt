package com.productivitystreak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.productivitystreak.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun observeStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getStats(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET currentXp = :xp WHERE id = 1")
    suspend fun updateXp(xp: Int)

    @Query("UPDATE user_stats SET level = :level, currentXp = :xp WHERE id = 1")
    suspend fun updateLevelAndXp(level: Int, xp: Int)

    @Query("""
        UPDATE user_stats SET 
            strength = strength + :str,
            intelligence = intelligence + :intel,
            wisdom = wisdom + :wis,
            discipline = discipline + :dis,
            charisma = charisma + :cha
        WHERE id = 1
    """)
    suspend fun incrementStats(str: Int = 0, intel: Int = 0, wis: Int = 0, dis: Int = 0, cha: Int = 0)

    @Query("DELETE FROM user_stats")
    suspend fun deleteAll()
}
