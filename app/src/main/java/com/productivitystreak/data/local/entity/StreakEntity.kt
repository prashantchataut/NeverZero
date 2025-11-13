package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.productivitystreak.data.model.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Entity(tableName = "streaks")
@TypeConverters(Converters::class)
data class StreakEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val currentCount: Int = 0,
    val longestCount: Int = 0,
    val goalPerDay: Int,
    val unit: String,
    val category: String,
    val lastCompletedDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "streak_logs")
data class StreakLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val streakId: String,
    val value: Int,
    val date: Long = System.currentTimeMillis(),
    val notes: String? = null
)

class Converters {
    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return if (value.isEmpty()) emptyList() else value.split(",").map { it.toInt() }
    }
}

// Extension functions to convert between entities and domain models
fun StreakEntity.toDomain(history: List<Int>): Streak {
    return Streak(
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

fun Streak.toEntity(): StreakEntity {
    return StreakEntity(
        id = id,
        name = name,
        currentCount = currentCount,
        longestCount = longestCount,
        goalPerDay = goalPerDay,
        unit = unit,
        category = category
    )
}
