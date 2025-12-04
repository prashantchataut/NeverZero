package com.productivitystreak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.productivitystreak.data.local.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {

    @Query("SELECT * FROM daily_logs WHERE protocolId = :protocolId ORDER BY date DESC")
    fun observeLogsForProtocol(protocolId: String): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE protocolId = :protocolId AND status = 'COMPLETE' ORDER BY date DESC")
    suspend fun getCompletedLogs(protocolId: String): List<DailyLogEntity>

    @Query("SELECT * FROM daily_logs WHERE protocolId = :protocolId ORDER BY date DESC")
    suspend fun getAllLogsForProtocol(protocolId: String): List<DailyLogEntity>

    @Query("SELECT * FROM daily_logs WHERE protocolId = :protocolId AND date = :date")
    suspend fun getLogForDate(protocolId: String, date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    suspend fun getLogsForDate(date: String): List<DailyLogEntity>

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    fun observeLogsForDate(date: String): Flow<List<DailyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: DailyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(logs: List<DailyLogEntity>)

    @Query("DELETE FROM daily_logs WHERE protocolId = :protocolId")
    suspend fun deleteLogsForProtocol(protocolId: String)

    @Query("DELETE FROM daily_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM daily_logs WHERE protocolId = :protocolId AND status = 'COMPLETE'")
    suspend fun getCompletedCount(protocolId: String): Int

    @Query("DELETE FROM daily_logs")
    suspend fun deleteAll()
}
