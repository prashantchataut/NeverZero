package com.productivitystreak.ui.state.onboarding

data class OnboardingState(
    val currentStep: Int = 0,
    val totalSteps: Int = 3,
    val selectedCategories: Set<String> = setOf("Reading", "Vocabulary"),
    val reminderTime: String = "8:00 PM",
    val allowNotifications: Boolean = true,
    val hasCompleted: Boolean = false
)
