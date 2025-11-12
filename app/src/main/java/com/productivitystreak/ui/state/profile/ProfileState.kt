package com.productivitystreak.ui.state.profile

data class ProfileState(
    val email: String = "alex@neverzero.app",
    val notificationEnabled: Boolean = true,
    val reminderFrequency: ReminderFrequency = ReminderFrequency.Daily,
    val hasWeeklySummary: Boolean = true,
    val theme: ProfileTheme = ProfileTheme.Dark,
    val activeCategories: Set<String> = setOf("Reading", "Vocabulary"),
    val legalLinks: List<LegalItem> = listOf(
        LegalItem("Privacy Policy", "https://neverzero.app/privacy"),
        LegalItem("Terms of Service", "https://neverzero.app/terms")
    )
)

enum class ReminderFrequency { None, Daily, Weekly }

enum class ProfileTheme { Dark, Light, Auto }

data class LegalItem(val label: String, val url: String)
