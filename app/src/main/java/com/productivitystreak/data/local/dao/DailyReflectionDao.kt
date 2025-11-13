package com.productivitystreak.data.local.dao

import androidx.room.*
import com.productivitystreak.data.local.entity.DailyReflectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReflectionDao {
    @Query("SELECT * FROM daily_reflections ORDER BY date DESC")
    fun getAllReflections(): Flow<List<DailyReflectionEntity>>

    @Query("SELECT * FROM daily_reflections WHERE date = :date")
    suspend fun getReflectionByDate(date: String): DailyReflectionEntity?

    @Query("SELECT * FROM daily_reflections WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getReflectionsBetweenDates(startDate: String, endDate: String): Flow<List<DailyReflectionEntity>>

    @Query("SELECT * FROM daily_reflections ORDER BY date DESC LIMIT :limit")
    fun getRecentReflections(limit: Int): Flow<List<DailyReflectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: DailyReflectionEntity)

    @Update
    suspend fun updateReflection(reflection: DailyReflectionEntity)

    @Delete
    suspend fun deleteReflection(reflection: DailyReflectionEntity)

    @Query("SELECT AVG(mood) FROM daily_reflections WHERE date >= :startDate")
    suspend fun getAverageMoodSince(startDate: String): Float?

    @Query("SELECT COUNT(*) FROM daily_reflections")
    suspend fun getReflectionCount(): Int

    @Query("DELETE FROM daily_reflections")
    suspend fun deleteAllReflections()
}
