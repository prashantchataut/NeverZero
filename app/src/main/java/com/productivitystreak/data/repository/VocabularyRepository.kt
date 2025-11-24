package com.productivitystreak.data.repository

import android.database.sqlite.SQLiteException
import com.productivitystreak.data.local.dao.VocabularyDao
import com.productivitystreak.data.local.entity.VocabularyWordEntity
import kotlinx.coroutines.flow.Flow

class VocabularyRepository(private val vocabularyDao: VocabularyDao) {

    fun observeAllWords(): Flow<List<VocabularyWordEntity>> = vocabularyDao.getAllWords()

    fun searchWords(query: String): Flow<List<VocabularyWordEntity>> = vocabularyDao.searchWords(query)

    suspend fun getWordById(id: Long): VocabularyWordEntity? = vocabularyDao.getWordById(id)

    suspend fun getWordsForPractice(limit: Int = 10): List<VocabularyWordEntity> =
        vocabularyDao.getWordsForPractice(limit)

    suspend fun addWord(
        word: String,
        definition: String,
        example: String? = null,
        partOfSpeech: String? = null,
        synonyms: List<String> = emptyList()
    ): RepositoryResult<Long> {
        return try {
            val wordEntity = VocabularyWordEntity(
                word = word,
                definition = definition,
                example = example,
                partOfSpeech = partOfSpeech,
                synonyms = synonyms
            )
            val id = vocabularyDao.insertWord(wordEntity)
            RepositoryResult.Success(id)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun updateWord(word: VocabularyWordEntity): RepositoryResult<Unit> {
        return try {
            vocabularyDao.updateWord(word)
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun reviewWord(wordId: Long, correct: Boolean): RepositoryResult<Unit> {
        return try {
            val word = vocabularyDao.getWordById(wordId) 
                ?: return RepositoryResult.DbError(IllegalStateException("Word not found"))
            
            val newMasteryLevel = if (correct) {
                minOf(5, word.masteryLevel + 1)
            } else {
                maxOf(0, word.masteryLevel - 1)
            }

            val updatedWord = word.copy(
                masteryLevel = newMasteryLevel,
                timesReviewed = word.timesReviewed + 1,
                lastReviewedAt = System.currentTimeMillis()
            )
            vocabularyDao.updateWord(updatedWord)
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun deleteWord(word: VocabularyWordEntity): RepositoryResult<Unit> {
        return try {
            vocabularyDao.deleteWord(word)
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun getWordCount(): Int = vocabularyDao.getWordCount()

    suspend fun getMasteredWordCount(): Int = vocabularyDao.getMasteredWordCount()
}
