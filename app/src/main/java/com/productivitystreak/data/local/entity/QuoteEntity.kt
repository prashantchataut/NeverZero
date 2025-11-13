package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.productivitystreak.data.model.Quote

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val author: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toQuote() = Quote(
        text = text,
        author = author
    )
}

fun Quote.toEntity() = QuoteEntity(
    text = text,
    author = author
)
