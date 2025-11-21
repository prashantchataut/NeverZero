package com.productivitystreak.data.model

data class StreakTemplate(
    val id: String,
    val name: String,
    val description: String,
    val goalPerDay: Int,
    val unit: String,
    val category: String,
    val icon: String,
    val color: String,
    val difficulty: StreakDifficulty = StreakDifficulty.BALANCED,
    val frequency: StreakFrequency = StreakFrequency.DAILY
)
