package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.productivitystreak.data.model.Streak

@Entity(tableName = "streaks")
@TypeConverters(Converters::class)
data class StreakEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val currentCount: Int,
    val longestCount: Int,
    val goalPerDay: Int,
    val unit: String,
    val category: String,
    val history: List<Int>,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val color: String = "#6366F1",
    val icon: String = "flag",
    val isArchived: Boolean = false,
    val freezeDaysUsed: Int = 0,
    val freezeDaysAvailable: Int = 3
) {
    fun toStreak() = Streak(
        id = id,
        name = name,
        currentCount = currentCount,
        longestCount = longestCount,
        goalPerDay = goalPerDay,
        unit = unit,
        category = category,
        history = history
    )
}

fun Streak.toEntity() = StreakEntity(
    id = id,
    name = name,
    currentCount = currentCount,
    longestCount = longestCount,
    goalPerDay = goalPerDay,
    unit = unit,
    category = category,
    history = history
)
