package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reflections")
data class DailyReflectionEntity(
    @PrimaryKey
    val date: String, // YYYY-MM-DD format
    val mood: Int, // 1-5 scale
    val notes: String,
    val highlights: String? = null,
    val challenges: String? = null,
    val gratitude: String? = null,
    val tomorrowGoals: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)
