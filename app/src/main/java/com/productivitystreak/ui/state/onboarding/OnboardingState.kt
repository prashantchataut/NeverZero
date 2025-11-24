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
    val allowNotifications: Boolean = false,
    val showNotificationPrompt: Boolean = false,
    val hasCompleted: Boolean = false,
    val habitSuggestions: List<String> = emptyList(),
    val isGeneratingSuggestions: Boolean = false,
    val userName: String = "",
    val selectedIcon: String = "flag",
    val profilePhotoUri: String? = null
)

data class OnboardingCategory(
    val id: String,
    val label: String,
    val iconId: String // Maps to AppIcons (health, fitness, mindfulness, etc.)
)

private val defaultOnboardingCategories = listOf(
    OnboardingCategory("health", "Health", "health"),
    OnboardingCategory("fitness", "Fitness", "fitness"),
    OnboardingCategory("mindfulness", "Mindfulness", "mindfulness"),
    OnboardingCategory("learning", "Learning", "learning"),
    OnboardingCategory("career", "Career", "career"),
    OnboardingCategory("finance", "Finance", "finance")
)
