package com.productivitystreak.data.repository

import com.productivitystreak.data.model.StreakDifficulty
import com.productivitystreak.data.model.StreakFrequency
import com.productivitystreak.data.model.StreakTemplate

class TemplateRepository {

    fun getCuratedTemplates(): List<StreakTemplate> {
        return listOf(
            // Health & Fitness
            StreakTemplate(
                id = "health_water",
                name = "Drink Water",
                description = "Stay hydrated by drinking 8 glasses daily.",
                goalPerDay = 8,
                unit = "glasses",
                category = "Health",
                icon = "water_drop",
                color = "#3B82F6", // Blue
                difficulty = StreakDifficulty.EASY
            ),
            StreakTemplate(
                id = "health_walk",
                name = "Daily Walk",
                description = "Take a 30-minute walk to clear your mind.",
                goalPerDay = 30,
                unit = "minutes",
                category = "Health",
                icon = "directions_walk",
                color = "#10B981", // Emerald
                difficulty = StreakDifficulty.EASY
            ),
            StreakTemplate(
                id = "health_workout",
                name = "Workout",
                description = "Exercise to stay fit and strong.",
                goalPerDay = 45,
                unit = "minutes",
                category = "Health",
                icon = "fitness_center",
                color = "#EF4444", // Red
                difficulty = StreakDifficulty.CHALLENGING
            ),

            // Learning & Growth
            StreakTemplate(
                id = "learn_read",
                name = "Read a Book",
                description = "Read for 20 minutes every day.",
                goalPerDay = 20,
                unit = "minutes",
                category = "Learning",
                icon = "menu_book",
                color = "#8B5CF6", // Violet
                difficulty = StreakDifficulty.BALANCED
            ),
            StreakTemplate(
                id = "learn_code",
                name = "Code Practice",
                description = "Write code or solve a problem daily.",
                goalPerDay = 1,
                unit = "session",
                category = "Learning",
                icon = "code",
                color = "#6366F1", // Indigo
                difficulty = StreakDifficulty.CHALLENGING
            ),
            StreakTemplate(
                id = "learn_vocab",
                name = "Vocabulary",
                description = "Learn 5 new words every day.",
                goalPerDay = 5,
                unit = "words",
                category = "Learning",
                icon = "school",
                color = "#F59E0B", // Amber
                difficulty = StreakDifficulty.BALANCED
            ),

            // Mindfulness
            StreakTemplate(
                id = "mind_meditate",
                name = "Meditation",
                description = "Practice mindfulness for 10 minutes.",
                goalPerDay = 10,
                unit = "minutes",
                category = "Mindfulness",
                icon = "self_improvement",
                color = "#EC4899", // Pink
                difficulty = StreakDifficulty.BALANCED
            ),
            StreakTemplate(
                id = "mind_journal",
                name = "Journaling",
                description = "Write down your thoughts or gratitude.",
                goalPerDay = 1,
                unit = "entry",
                category = "Mindfulness",
                icon = "edit_note",
                color = "#14B8A6", // Teal
                difficulty = StreakDifficulty.EASY
            ),

            // Productivity
            StreakTemplate(
                id = "prod_plan",
                name = "Plan the Day",
                description = "Spend 5 minutes planning your tasks.",
                goalPerDay = 5,
                unit = "minutes",
                category = "Productivity",
                icon = "checklist",
                color = "#F97316", // Orange
                difficulty = StreakDifficulty.EASY
            ),
            StreakTemplate(
                id = "prod_focus",
                name = "Deep Work",
                description = "90 minutes of uninterrupted focus.",
                goalPerDay = 90,
                unit = "minutes",
                category = "Productivity",
                icon = "timer",
                color = "#374151", // Gray
                difficulty = StreakDifficulty.CHALLENGING
            )
        )
    }

    fun getTemplatesByCategory(category: String): List<StreakTemplate> {
        return getCuratedTemplates().filter { it.category.equals(category, ignoreCase = true) }
    }
}
