package com.productivitystreak.data.local.dao

import androidx.room.*
import com.productivitystreak.data.local.entity.ReadingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {
    @Query("SELECT * FROM reading_sessions WHERE bookId = :bookId ORDER BY date DESC")
    fun getSessionsForBook(bookId: Long): Flow<List<ReadingSessionEntity>>

    @Query("SELECT * FROM reading_sessions ORDER BY date DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<ReadingSessionEntity>>

    @Query("SELECT SUM(pagesRead) FROM reading_sessions WHERE bookId = :bookId")
    suspend fun getTotalPagesReadForBook(bookId: Long): Int?

    @Query("SELECT SUM(pagesRead) FROM reading_sessions WHERE date >= :startDate")
    suspend fun getTotalPagesReadSince(startDate: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ReadingSessionEntity): Long

    @Update
    suspend fun updateSession(session: ReadingSessionEntity)

    @Delete
    suspend fun deleteSession(session: ReadingSessionEntity)

    @Query("DELETE FROM reading_sessions WHERE bookId = :bookId")
    suspend fun deleteSessionsForBook(bookId: Long)
}
