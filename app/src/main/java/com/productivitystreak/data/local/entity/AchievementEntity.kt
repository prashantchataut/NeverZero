package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val category: String,
    val requirement: Int,
    val progress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val tier: String, // bronze, silver, gold, platinum
    val points: Int
) {
    val isCompleted: Boolean
        get() = progress >= requirement
}
