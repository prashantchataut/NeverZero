package com.productivitystreak.data.repository

import com.productivitystreak.data.model.Streak
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StreakRepository {
    private val streaks = MutableStateFlow(sampleStreaks())

    fun observeStreaks(): Flow<List<Streak>> = streaks.asStateFlow()

    suspend fun logProgress(streakId: String, value: Int) {
        delay(150) // simulate persistence latency
        streaks.value = streaks.value.map { streak ->
            if (streak.id == streakId) {
                streak.copy(
                    currentCount = streak.currentCount + if (value > 0) 1 else 0,
                    history = streak.history + value
                )
            } else {
                streak
            }
        }
    }

    companion object {
        private fun sampleStreaks(): List<Streak> = listOf(
            Streak(
                id = "reading",
                name = "Read 30 mins",
                currentCount = 15,
                longestCount = 42,
                goalPerDay = 30,
                unit = "minutes",
                category = "Reading",
                history = listOf(30, 35, 25, 40, 30)
            ),
            Streak(
                id = "vocabulary",
                name = "Add 5 new words",
                currentCount = 9,
                longestCount = 28,
                goalPerDay = 5,
                unit = "words",
                category = "Vocabulary",
                history = listOf(5, 3, 6, 5, 7)
            ),
            Streak(
                id = "wellness",
                name = "Meditation",
                currentCount = 23,
                longestCount = 60,
                goalPerDay = 10,
                unit = "minutes",
                category = "Wellness",
                history = listOf(8, 12, 10, 9, 11)
            )
        )
    }
}
