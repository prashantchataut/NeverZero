package com.productivitystreak.data.ai

import com.productivitystreak.data.gemini.GeminiClient
import com.productivitystreak.data.model.Streak
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AI-powered habit creation assistant
 * Analyzes existing habits and suggests complementary ones
 */
class HabitCreationAssistant(
    private val geminiClient: GeminiClient
) {
    
    /**
     * Suggest complementary habits based on existing ones
     */
    suspend fun suggestComplementaryHabits(
        existingHabits: List<Streak>,
        userGoal: String? = null
    ): List<HabitSuggestion> = withContext(Dispatchers.IO) {
        if (existingHabits.isEmpty()) {
            return@withContext emptyList()
        }
        
        val habitSummary = existingHabits.joinToString("\n") { 
            "- ${it.name} (${it.category})" 
        }
        
        val prompt = """
            The user has these habits:
            $habitSummary
            
            ${if (userGoal != null) "Their goal is: $userGoal" else ""}
            
            Suggest 3 complementary habits that would create a balanced routine.
            Consider:
            - Missing categories (e.g., if they have fitness but no mindfulness)
            - Synergistic habits (e.g., if they read, suggest note-taking)
            - Time of day balance
            
            Format as JSON array:
            [
              {
                "name": "Habit name",
                "category": "Category",
                "reason": "Why this complements their routine",
                "goalPerDay": 15,
                "unit": "minutes",
                "bestTime": "morning/afternoon/evening"
              }
            ]
        """.trimIndent()
        
        try {
            val response = geminiClient.generateMotivationPrompt(prompt)
            // Parse JSON response (simplified for now)
            // In production, use Moshi to parse properly
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Analyze best time to schedule a habit
     */
    suspend fun suggestOptimalSchedule(
        habitName: String,
        existingHabits: List<Streak>
    ): ScheduleSuggestion = withContext(Dispatchers.IO) {
        val busyTimes = existingHabits.groupBy { it.category }
        
        val prompt = """
            The user wants to add: "$habitName"
            They already have ${existingHabits.size} habits.
            
            Suggest the best time of day and frequency.
            Consider energy levels, habit stacking, and avoiding overload.
            
            Respond with JSON:
            {
              "timeOfDay": "morning/afternoon/evening",
              "frequency": "daily/3x week/weekends",
              "reason": "Brief explanation"
            }
        """.trimIndent()
        
        // Fallback
        ScheduleSuggestion(
            timeOfDay = "morning",
            frequency = "daily",
            reason = "Morning habits have highest completion rates"
        )
    }
    
    data class HabitSuggestion(
        val name: String,
        val category: String,
        val reason: String,
        val goalPerDay: Int,
        val unit: String,
        val bestTime: String
    )
    
    data class ScheduleSuggestion(
        val timeOfDay: String,
        val frequency: String,
        val reason: String
    )
}
