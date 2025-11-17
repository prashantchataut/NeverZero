package com.productivitystreak.ui.state.onboarding

data class OnboardingState(
    val currentStep: Int = 0,
    val totalSteps: Int = 5,
    val categories: List<OnboardingCategory> = defaultOnboardingCategories,
    val selectedCategories: Set<String> = emptySet(),
    val goalHabit: String = "",
    val commitmentDurationMinutes: Int = 5,
    val commitmentFrequencyPerWeek: Int = 3,
    val reminderTime: String = "08:30 PM",
    val allowNotifications: Boolean = true,
    val showNotificationPrompt: Boolean = false,
    val hasCompleted: Boolean = false
)

data class OnboardingCategory(
    val id: String,
    val label: String,
    val emoji: String
)

private val defaultOnboardingCategories = listOf(
    OnboardingCategory("health", "Health", "💗"),
    OnboardingCategory("fitness", "Fitness", "🏋️"),
    OnboardingCategory("mindfulness", "Mindfulness", "🧘"),
    OnboardingCategory("learning", "Learning", "📚"),
    OnboardingCategory("career", "Career", "💼"),
    OnboardingCategory("finance", "Finance", "🏦")
)
