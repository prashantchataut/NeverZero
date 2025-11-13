package com.productivitystreak.data.model

data class HabitTemplate(
    val id: String,
    val name: String,
    val category: String,
    val goalPerDay: Int,
    val unit: String,
    val description: String,
    val icon: String,
    val color: String,
    val tips: List<String> = emptyList()
)

object HabitTemplates {
    val templates = listOf(
        // Reading
        HabitTemplate(
            id = "read_30min",
            name = "Read 30 minutes",
            category = "Reading",
            goalPerDay = 30,
            unit = "minutes",
            description = "Build a daily reading habit",
            icon = "menu_book",
            color = "#8B5CF6",
            tips = listOf(
                "Start with just 10 minutes if 30 feels too much",
                "Keep a book with you at all times",
                "Try reading before bed instead of scrolling"
            )
        ),
        HabitTemplate(
            id = "read_pages",
            name = "Read 20 pages",
            category = "Reading",
            goalPerDay = 20,
            unit = "pages",
            description = "Track your reading by pages",
            icon = "auto_stories",
            color = "#8B5CF6"
        ),

        // Vocabulary
        HabitTemplate(
            id = "vocab_5words",
            name = "Learn 5 new words",
            category = "Vocabulary",
            goalPerDay = 5,
            unit = "words",
            description = "Expand your vocabulary daily",
            icon = "school",
            color = "#10B981",
            tips = listOf(
                "Use flashcards for better retention",
                "Try to use new words in sentences",
                "Review previous words weekly"
            )
        ),
        HabitTemplate(
            id = "vocab_10words",
            name = "Learn 10 new words",
            category = "Vocabulary",
            goalPerDay = 10,
            unit = "words",
            description = "Intensive vocabulary building",
            icon = "menu_book",
            color = "#10B981"
        ),

        // Wellness
        HabitTemplate(
            id = "meditate_10min",
            name = "Meditate 10 minutes",
            category = "Wellness",
            goalPerDay = 10,
            unit = "minutes",
            description = "Daily mindfulness practice",
            icon = "spa",
            color = "#F59E0B",
            tips = listOf(
                "Use guided meditation apps",
                "Find a quiet space",
                "Be consistent with timing"
            )
        ),
        HabitTemplate(
            id = "exercise_30min",
            name = "Exercise 30 minutes",
            category = "Wellness",
            goalPerDay = 30,
            unit = "minutes",
            description = "Stay active daily",
            icon = "fitness_center",
            color = "#EF4444"
        ),
        HabitTemplate(
            id = "walk_steps",
            name = "Walk 10,000 steps",
            category = "Wellness",
            goalPerDay = 10000,
            unit = "steps",
            description = "Hit your daily step goal",
            icon = "directions_walk",
            color = "#EF4444"
        ),
        HabitTemplate(
            id = "water_glasses",
            name = "Drink 8 glasses of water",
            category = "Wellness",
            goalPerDay = 8,
            unit = "glasses",
            description = "Stay hydrated",
            icon = "local_drink",
            color = "#3B82F6"
        ),

        // Learning
        HabitTemplate(
            id = "code_hour",
            name = "Code for 1 hour",
            category = "Learning",
            goalPerDay = 60,
            unit = "minutes",
            description = "Practice programming daily",
            icon = "code",
            color = "#6366F1"
        ),
        HabitTemplate(
            id = "study_pomodoro",
            name = "2 Pomodoro sessions",
            category = "Learning",
            goalPerDay = 2,
            unit = "sessions",
            description = "Focused study time",
            icon = "timer",
            color = "#6366F1"
        ),
        HabitTemplate(
            id = "online_course",
            name = "1 course lesson",
            category = "Learning",
            goalPerDay = 1,
            unit = "lessons",
            description = "Make progress on online courses",
            icon = "play_circle",
            color = "#EC4899"
        ),

        // Creativity
        HabitTemplate(
            id = "write_journal",
            name = "Journal entry",
            category = "Creativity",
            goalPerDay = 1,
            unit = "entries",
            description = "Daily journaling",
            icon = "edit_note",
            color = "#F97316"
        ),
        HabitTemplate(
            id = "draw_sketch",
            name = "Draw for 15 minutes",
            category = "Creativity",
            goalPerDay = 15,
            unit = "minutes",
            description = "Practice drawing daily",
            icon = "brush",
            color = "#F97316"
        ),
        HabitTemplate(
            id = "music_practice",
            name = "Practice instrument",
            category = "Creativity",
            goalPerDay = 30,
            unit = "minutes",
            description = "Daily music practice",
            icon = "music_note",
            color = "#8B5CF6"
        ),

        // Productivity
        HabitTemplate(
            id = "deep_work",
            name = "2 hours deep work",
            category = "Productivity",
            goalPerDay = 120,
            unit = "minutes",
            description = "Focused, undistracted work",
            icon = "work",
            color = "#14B8A6"
        ),
        HabitTemplate(
            id = "plan_day",
            name = "Plan tomorrow",
            category = "Productivity",
            goalPerDay = 1,
            unit = "plans",
            description = "Daily planning habit",
            icon = "event",
            color = "#14B8A6"
        ),
        HabitTemplate(
            id = "inbox_zero",
            name = "Clear inbox",
            category = "Productivity",
            goalPerDay = 1,
            unit = "times",
            description = "Maintain inbox zero",
            icon = "inbox",
            color = "#14B8A6"
        ),

        // Social
        HabitTemplate(
            id = "gratitude",
            name = "List 3 things I'm grateful for",
            category = "Social",
            goalPerDay = 3,
            unit = "items",
            description = "Daily gratitude practice",
            icon = "favorite",
            color = "#EC4899"
        ),
        HabitTemplate(
            id = "call_friend",
            name = "Call a friend/family",
            category = "Social",
            goalPerDay = 1,
            unit = "calls",
            description = "Stay connected",
            icon = "phone",
            color = "#EC4899"
        )
    )

    fun getByCategory(category: String): List<HabitTemplate> =
        templates.filter { it.category == category }

    fun getById(id: String): HabitTemplate? =
        templates.find { it.id == id }

    val categories = templates.map { it.category }.distinct()
}
