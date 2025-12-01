package com.productivitystreak.data.ai

import com.productivitystreak.data.model.Streak
import com.productivitystreak.data.model.RpgStats
import com.productivitystreak.data.gemini.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AICoach(
    private val geminiClient: GeminiClient
) {

    suspend fun generateDailyBriefing(
        userName: String,
        streaks: List<Streak>,
        rpgStats: RpgStats
    ): String = withContext(Dispatchers.IO) {
        // Construct a prompt based on user data
        val activeHabits = streaks.joinToString(", ") { "${it.name} (${it.currentCount} days)" }
        val statsSummary = "Level ${rpgStats.level} | STR: ${rpgStats.strength}, WIS: ${rpgStats.wisdom}, DIS: ${rpgStats.discipline}"
        
        val prompt = """
            You are a stoic, high-performance productivity coach for $userName.
            Here is their current status:
            - Active Habits: $activeHabits
            - RPG Stats: $statsSummary
            
            Generate a short, punchy daily briefing (max 3 sentences).
            Focus on what they need to improve based on their lowest stat or a habit with a low streak.
            Be motivating but tough. Use "We" or "You".
        """.trimIndent()

        try {
            geminiClient.generateMotivationPrompt(prompt)
        } catch (e: Exception) {
            "Focus on the process. The results will follow."
        }
    }
}
