package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val totalPages: Int,
    val currentPage: Int = 0,
    val coverImageUrl: String? = null,
    val startedAt: Long? = null,
    val finishedAt: Long? = null,
    val rating: Float? = null,
    val notes: String? = null,
    val genre: String? = null,
    val isbn: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val progress: Float
        get() = if (totalPages == 0) 0f else currentPage.toFloat() / totalPages.toFloat()

    val isFinished: Boolean
        get() = currentPage >= totalPages
}
