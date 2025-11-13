package com.productivitystreak.data.repository

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
    ): Long {
        val wordEntity = VocabularyWordEntity(
            word = word,
            definition = definition,
            example = example,
            partOfSpeech = partOfSpeech,
            synonyms = synonyms
        )
        return vocabularyDao.insertWord(wordEntity)
    }

    suspend fun updateWord(word: VocabularyWordEntity) {
        vocabularyDao.updateWord(word)
    }

    suspend fun reviewWord(wordId: Long, correct: Boolean) {
        val word = vocabularyDao.getWordById(wordId) ?: return
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
    }

    suspend fun deleteWord(word: VocabularyWordEntity) {
        vocabularyDao.deleteWord(word)
    }

    suspend fun getWordCount(): Int = vocabularyDao.getWordCount()

    suspend fun getMasteredWordCount(): Int = vocabularyDao.getMasteredWordCount()
}
