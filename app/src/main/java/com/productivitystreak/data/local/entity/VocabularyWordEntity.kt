package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "vocabulary_words")
@TypeConverters(Converters::class)
data class VocabularyWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val definition: String,
    val example: String? = null,
    val synonyms: List<String> = emptyList(),
    val partOfSpeech: String? = null,
    val pronunciation: String? = null,
    val masteryLevel: Int = 0, // 0-5 scale
    val timesReviewed: Int = 0,
    val lastReviewedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList()
)
