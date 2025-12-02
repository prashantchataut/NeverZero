package com.productivitystreak.data.repository

import com.productivitystreak.data.model.Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChallengeRepository {

    fun getAvailableChallenges(): Flow<List<Challenge>> = flow {
        emit(
            listOf(
                Challenge(
                    id = "iron_mind",
                    title = "The Iron Mind",
                    description = "30 days of mental fortitude. Read 10 pages, Meditate 10 mins, No social media.",
                    durationDays = 30,
                    iconId = "brain",
                    colorHex = "#6366F1", // Indigo
                    requiredHabits = listOf("Reading", "Meditation"),
                    difficulty = "Hard"
                ),
                Challenge(
                    id = "savage_september",
                    title = "Savage 30-Day Protocol",
                    description = "Wake up at 5 AM. Cold Shower. 100 Pushups.",
                    durationDays = 30,
                    iconId = "sword",
                    colorHex = "#EF4444", // Red
                    requiredHabits = listOf("Wake Early", "Workout"),
                    difficulty = "Savage"
                ),
                Challenge(
                    id = "digital_detox",
                    title = "Digital Detox",
                    description = "No phone 1 hour before bed. No phone 1 hour after waking.",
                    durationDays = 14,
                    iconId = "phone",
                    colorHex = "#10B981", // Emerald
                    requiredHabits = listOf("No Phone"),
                    difficulty = "Medium"
                )
            )
        )
    }
}
