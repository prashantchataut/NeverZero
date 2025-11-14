package com.productivitystreak

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.productivitystreak.ui.AppViewModel
import com.productivitystreak.ui.AppViewModelFactory
import com.productivitystreak.ui.navigation.NeverZeroApp
import com.productivitystreak.ui.theme.ProductivityStreakTheme
import com.productivitystreak.ui.utils.PermissionManager

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: AppViewModel by viewModels { AppViewModelFactory(application) }
        setContent {
            ProductivityStreakTheme {
                val state = viewModel.uiState.collectAsStateWithLifecycle()
                Surface {
                    NeverZeroApp(
                        uiState = state.value,
                        onRefreshQuote = viewModel::refreshQuote,
                        onSelectStreak = viewModel::onSelectStreak,
                        onToggleTask = viewModel::onToggleTask,
                        onSimulateTaskCompletion = viewModel::simulateTaskCompletion,
                        onLogReadingProgress = viewModel::onLogReadingProgress,
                        onAddVocabularyWord = viewModel::onAddVocabularyWord,
                        onToggleOnboardingCategory = viewModel::onToggleOnboardingCategory,
                        onCompleteOnboarding = viewModel::onCompleteOnboarding,
                        onDismissOnboarding = viewModel::onDismissOnboarding,
                        onToggleNotifications = viewModel::onToggleNotifications,
                        onChangeReminderFrequency = viewModel::onChangeReminderFrequency,
                        onToggleWeeklySummary = viewModel::onToggleWeeklySummary,
                        onChangeTheme = viewModel::onChangeTheme,
                        onToggleHaptics = viewModel::onToggleHaptics,
                        onSettingsThemeChange = viewModel::onSettingsThemeChange,
                        onSettingsDailyRemindersToggle = viewModel::onSettingsDailyRemindersToggle,
                        onSettingsWeeklyBackupsToggle = viewModel::onSettingsWeeklyBackupsToggle,
                        onSettingsReminderTimeChange = viewModel::onSettingsReminderTimeChange,
                        onSettingsHapticFeedbackToggle = viewModel::onSettingsHapticFeedbackToggle,
                        onSettingsCreateBackup = viewModel::onSettingsCreateBackup,
                        onSettingsRestoreBackup = viewModel::onSettingsRestoreBackup,
                        onSettingsDismissMessage = viewModel::onSettingsDismissMessage
                    )
                }
            }
        }

        requestNotificationPermissionIfNeeded()
        ensureExactAlarmCapability()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (!PermissionManager.shouldRequestNotificationPermission(this)) return

        if (PermissionManager.shouldShowNotificationPermissionRationale(this)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.notification_permission_title)
                .setMessage(R.string.notification_permission_rationale)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        } else {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun ensureExactAlarmCapability() {
        if (PermissionManager.canScheduleExactAlarms(this)) return

        AlertDialog.Builder(this)
            .setTitle(R.string.exact_alarm_permission_title)
            .setMessage(R.string.exact_alarm_permission_rationale)
            .setPositiveButton(R.string.exact_alarm_permission_action) { _, _ ->
                PermissionManager.launchExactAlarmSettings(this)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
