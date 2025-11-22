package com.productivitystreak.data

import com.productivitystreak.data.model.Quote
import com.productivitystreak.data.model.QuoteCategory
import com.productivitystreak.data.model.QuoteContext
import com.productivitystreak.data.model.QuoteTemplate
import com.productivitystreak.data.model.UserContext
import kotlin.math.abs

import com.productivitystreak.data.gemini.GeminiClient
import kotlinx.coroutines.runBlocking

class PersonalizedQuoteEngine(
    private val geminiClient: GeminiClient
) {

    private val templates = listOf(
        // MOMENTUM_BUILDER - Active streaks, good performance
        QuoteTemplate(
            id = "momentum_1",
            category = QuoteCategory.MOMENTUM_BUILDER,
            template = "You've shown up for {streak_days} days straight. That's {streak_days} times you chose momentum over comfort.",
            context = QuoteContext(minStreakDays = 3)
        ),
        QuoteTemplate(
            id = "momentum_2",
            category = QuoteCategory.MOMENTUM_BUILDER,
            template = "{user_name}, you're {completion_rate}% through today's goals. Finish strong.",
            context = QuoteContext(minCompletionRate = 50, maxCompletionRate = 99)
        ),
        QuoteTemplate(
            id = "momentum_3",
            category = QuoteCategory.MOMENTUM_BUILDER,
            template = "Every day you show up, you're proving who you are. Keep building.",
            context = QuoteContext(minStreakDays = 1)
        ),
        QuoteTemplate(
            id = "momentum_4",
            category = QuoteCategory.MOMENTUM_BUILDER,
            template = "You've earned {total_points} points. That's not luck—that's discipline.",
            context = QuoteContext(minStreakDays = 5)
        ),
        QuoteTemplate(
            id = "momentum_5",
            category = QuoteCategory.MOMENTUM_BUILDER,
            template = "{streak_days} days without breaking. You're becoming unstoppable.",
            context = QuoteContext(minStreakDays = 7)
        ),

        // COMEBACK_COACH - Broken streaks, returning users
        QuoteTemplate(
            id = "comeback_1",
            category = QuoteCategory.COMEBACK_COACH,
            template = "You took a break. That's human. But you're back. That's strength.",
            context = QuoteContext(requiresBrokenStreak = true)
        ),
        QuoteTemplate(
            id = "comeback_2",
            category = QuoteCategory.COMEBACK_COACH,
            template = "The streak broke, but you didn't. Welcome back, {user_name}.",
            context = QuoteContext(requiresBrokenStreak = true)
        ),
        QuoteTemplate(
            id = "comeback_3",
            category = QuoteCategory.COMEBACK_COACH,
            template = "Most people quit after a setback. You're here. That's the difference.",
            context = QuoteContext(requiresBrokenStreak = true)
        ),
        QuoteTemplate(
            id = "comeback_4",
            category = QuoteCategory.COMEBACK_COACH,
            template = "Starting over is not failure. Staying away is. You chose to return.",
            context = QuoteContext(requiresBrokenStreak = true)
        ),

        // RESCUE_MOTIVATOR - Late in day, no progress
        QuoteTemplate(
            id = "rescue_1",
            category = QuoteCategory.RESCUE_MOTIVATOR,
            template = "It's late. You have time for one small win. Take it.",
            context = QuoteContext(requiresRescue = true, hourOfDayMin = 17)
        ),
        QuoteTemplate(
            id = "rescue_2",
            category = QuoteCategory.RESCUE_MOTIVATOR,
            template = "Momentum beats perfection. Do the 1% right now.",
            context = QuoteContext(requiresRescue = true, hourOfDayMin = 17)
        ),
        QuoteTemplate(
            id = "rescue_3",
            category = QuoteCategory.RESCUE_MOTIVATOR,
            template = "The day isn't over. One action preserves everything.",
            context = QuoteContext(requiresRescue = true, hourOfDayMin = 17)
        ),
        QuoteTemplate(
            id = "rescue_4",
            category = QuoteCategory.RESCUE_MOTIVATOR,
            template = "{user_name}, you've built {streak_days} days. Don't let this one slip. Do something small.",
            context = QuoteContext(requiresRescue = true, hourOfDayMin = 17, minStreakDays = 1)
        ),

        // ACHIEVEMENT_CELEBRATOR - Milestones, high completion
        QuoteTemplate(
            id = "achievement_1",
            category = QuoteCategory.ACHIEVEMENT_CELEBRATOR,
            template = "You just hit {total_points} points. You're not tracking progress—you're building proof.",
            context = QuoteContext(minCompletionRate = 100)
        ),
        QuoteTemplate(
            id = "achievement_2",
            category = QuoteCategory.ACHIEVEMENT_CELEBRATOR,
            template = "All tasks complete. This is what consistency looks like.",
            context = QuoteContext(minCompletionRate = 100)
        ),
        QuoteTemplate(
            id = "achievement_3",
            category = QuoteCategory.ACHIEVEMENT_CELEBRATOR,
            template = "{streak_days} days of showing up. Most people don't make it past 3.",
            context = QuoteContext(minStreakDays = 10)
        ),

        // IDENTITY_REINFORCER - Consistent behavior, identity building
        QuoteTemplate(
            id = "identity_1",
            category = QuoteCategory.IDENTITY_REINFORCER,
            template = "You're the person who shows up. {streak_days} days proves it.",
            context = QuoteContext(minStreakDays = 5)
        ),
        QuoteTemplate(
            id = "identity_2",
            category = QuoteCategory.IDENTITY_REINFORCER,
            template = "Most people quit. You're still here. That's the difference.",
            context = QuoteContext(minStreakDays = 3)
        ),
        QuoteTemplate(
            id = "identity_3",
            category = QuoteCategory.IDENTITY_REINFORCER,
            template = "You don't need motivation. You have momentum. Keep going.",
            context = QuoteContext(minStreakDays = 7)
        ),
        QuoteTemplate(
            id = "identity_4",
            category = QuoteCategory.IDENTITY_REINFORCER,
            template = "Every action you take is a vote for the person you're becoming.",
            context = QuoteContext(minStreakDays = 1)
        ),

        // MORNING ACTIVATION
        QuoteTemplate(
            id = "morning_1",
            category = QuoteCategory.MOMENTUM_BUILDER,
            template = "Good morning, {user_name}. Today is another chance to prove who you are.",
            context = QuoteContext(hourOfDayMin = 5, hourOfDayMax = 11)
        ),
        QuoteTemplate(
            id = "morning_2",
            category = QuoteCategory.MOMENTUM_BUILDER,
            template = "The day starts now. Make the first move count.",
            context = QuoteContext(hourOfDayMin = 5, hourOfDayMax = 11)
        )
    )

    fun generateQuote(userContext: UserContext): Quote {
        // Try to use Gemini for a dynamic quote occasionally (e.g., 30% of the time) or if context is special
        // For now, we'll do a simple check. In a real app, we'd be more sophisticated.
        // Since this is synchronous, we use runBlocking for the suspend call.
        // Ideally, this should be fully async, but for now we wrap it.
        
        val shouldUseGemini = (System.currentTimeMillis() % 10) < 3 // 30% chance
        
        if (shouldUseGemini) {
            try {
                val prompt = buildPrompt(userContext)
                val aiQuoteText = runBlocking { geminiClient.generateMotivationPrompt(prompt) }
                return Quote(
                    text = aiQuoteText,
                    author = "Never Zero AI"
                )
            } catch (e: Exception) {
                // Fallback to templates on error
            }
        }

        val matchingTemplates = templates.filter { template ->
            matchesContext(template.context, userContext)
        }

        if (matchingTemplates.isEmpty()) {
            return getFallbackQuote(userContext)
        }

        // Use deterministic selection based on day of year + user name hash
        val dayOfYear = java.time.LocalDate.now().dayOfYear
        val seed = dayOfYear + userContext.userName.hashCode()
        val selectedTemplate = matchingTemplates[abs(seed) % matchingTemplates.size]

        return Quote(
            text = injectUserData(selectedTemplate.template, userContext),
            author = "Never Zero"
        )
    }

    private fun buildPrompt(context: UserContext): String {
        return """
            Generate a short, punchy motivational quote for a user named ${context.userName}.
            Context:
            - Current streak: ${context.currentStreakDays} days
            - Time of day: ${formatTime(context.timeOfDay.hour)}
            - Total points: ${context.totalPoints}
            - Completion rate today: ${context.completionRate}%
            
            The quote should be direct, encouraging, and under 20 words. Do not use quotes around the text.
        """.trimIndent()
    }

    private fun matchesContext(templateContext: QuoteContext, userContext: UserContext): Boolean {
        // Check streak days
        if (templateContext.minStreakDays != null && userContext.currentStreakDays < templateContext.minStreakDays) {
            return false
        }
        if (templateContext.maxStreakDays != null && userContext.currentStreakDays > templateContext.maxStreakDays) {
            return false
        }

        // Check hour of day
        if (templateContext.hourOfDayMin != null && userContext.hourOfDay < templateContext.hourOfDayMin) {
            return false
        }
        if (templateContext.hourOfDayMax != null && userContext.hourOfDay > templateContext.hourOfDayMax) {
            return false
        }

        // Check rescue requirement
        if (templateContext.requiresRescue && !userContext.needsRescue) {
            return false
        }

        // Check broken streak requirement
        if (templateContext.requiresBrokenStreak && !userContext.hasBrokenStreak) {
            return false
        }

        // Check completion rate
        if (templateContext.minCompletionRate != null && userContext.completionRate < templateContext.minCompletionRate) {
            return false
        }
        if (templateContext.maxCompletionRate != null && userContext.completionRate > templateContext.maxCompletionRate) {
            return false
        }

        return true
    }

    private fun injectUserData(template: String, userContext: UserContext): String {
        return template
            .replace("{user_name}", userContext.userName)
            .replace("{streak_days}", userContext.currentStreakDays.toString())
            .replace("{completion_rate}", userContext.completionRate.toString())
            .replace("{total_points}", userContext.totalPoints.toString())
            .replace("{current_time}", formatTime(userContext.timeOfDay.hour))
    }

    private fun formatTime(hour: Int): String {
        return when {
            hour < 12 -> "${hour} AM"
            hour == 12 -> "12 PM"
            else -> "${hour - 12} PM"
        }
    }

    private fun getFallbackQuote(userContext: UserContext): Quote {
        return Quote(
            text = "You're here. That's what matters. Keep building, ${userContext.userName}.",
            author = "Never Zero"
        )
    }
}
