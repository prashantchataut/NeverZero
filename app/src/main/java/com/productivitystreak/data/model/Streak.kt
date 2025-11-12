package com.productivitystreak.data.model

data class Streak(
    val id: String,
    val name: String,
    val currentCount: Int,
    val longestCount: Int,
    val goalPerDay: Int,
    val unit: String,
    val category: String,
    val history: List<Int>
) {
    val progress: Float
        get() = if (goalPerDay == 0) 0f else (history.lastOrNull() ?: 0) / goalPerDay.toFloat()
}
