package com.productivitystreak.ui.state.settings

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dailyRemindersEnabled: Boolean = true,
    val weeklyBackupsEnabled: Boolean = true,
    val reminderTime: String = "09:00",
    val hapticFeedbackEnabled: Boolean = true,
    val isBackupInProgress: Boolean = false,
    val isRestoreInProgress: Boolean = false,
    val lastBackupTime: String? = null,
    val appVersion: String = "1.0.0",
    val showTimePickerDialog: Boolean = false,
    val showRestoreDialog: Boolean = false,
    val showBackupSuccessMessage: Boolean = false,
    val showRestoreSuccessMessage: Boolean = false,
    val errorMessage: String? = null
)

enum class ThemeMode(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System Default")
}
