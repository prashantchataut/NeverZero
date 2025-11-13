package com.productivitystreak.data.local.dao

import androidx.room.*
import com.productivitystreak.data.local.entity.VocabularyWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary_words ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<VocabularyWordEntity>>

    @Query("SELECT * FROM vocabulary_words WHERE masteryLevel < 5 ORDER BY lastReviewedAt ASC, timesReviewed ASC LIMIT :limit")
    suspend fun getWordsForPractice(limit: Int): List<VocabularyWordEntity>

    @Query("SELECT * FROM vocabulary_words WHERE word LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%'")
    fun searchWords(query: String): Flow<List<VocabularyWordEntity>>

    @Query("SELECT * FROM vocabulary_words WHERE id = :id")
    suspend fun getWordById(id: Long): VocabularyWordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: VocabularyWordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<VocabularyWordEntity>)

    @Update
    suspend fun updateWord(word: VocabularyWordEntity)

    @Delete
    suspend fun deleteWord(word: VocabularyWordEntity)

    @Query("SELECT COUNT(*) FROM vocabulary_words")
    suspend fun getWordCount(): Int

    @Query("SELECT COUNT(*) FROM vocabulary_words WHERE masteryLevel >= 4")
    suspend fun getMasteredWordCount(): Int

    @Query("DELETE FROM vocabulary_words")
    suspend fun deleteAllWords()
}
