package com.productivitystreak.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.TimeCapsule
import com.productivitystreak.ui.screens.profile.components.*
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.state.settings.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    profileState: ProfileState,
    settingsState: SettingsState,
    totalPoints: Int,
    timeCapsules: List<TimeCapsule>,
    onSettingsThemeChange: (ThemeMode) -> Unit,
    onSettingsDailyRemindersToggle: (Boolean) -> Unit,
    onSettingsWeeklyBackupsToggle: (Boolean) -> Unit,
    onSettingsReminderTimeChange: (String) -> Unit,
    onSettingsHapticFeedbackToggle: (Boolean) -> Unit,
    onSettingsCreateBackup: () -> Unit,
    onSettingsRestoreBackup: () -> Unit,
    onSettingsRestoreFileSelected: (Uri) -> Unit,
    onSettingsDismissRestoreDialog: () -> Unit,
    onSettingsDismissMessage: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onChangeReminderFrequency: (ReminderFrequency) -> Unit,
    onToggleWeeklySummary: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    onCreateTimeCapsule: (message: String, goal: String, daysFromNow: Int) -> Unit,
    onSaveTimeCapsuleReflection: (id: String, reflection: String) -> Unit,
    onEditProfile: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showTimeCapsuleSheet by rememberSaveable { mutableStateOf(false) }
    var reflectionCapsuleId by rememberSaveable { mutableStateOf<String?>(null) }
    val reflectionCapsule = reflectionCapsuleId?.let { id -> timeCapsules.firstOrNull { it.id == id } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = com.productivitystreak.ui.theme.Spacing.lg, vertical = com.productivitystreak.ui.theme.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(com.productivitystreak.ui.theme.Spacing.xl)
    ) {
        // 1. Profile Header
        ProfileHeader(
            userName = userName,
            email = profileState.email,
            totalPoints = totalPoints
        )

        // 1.5 RPG Stats
        RpgStatsCard(
            stats = profileState.rpgStats
        )

        // 2. Time Capsules
        TimeCapsuleCard(
            capsules = timeCapsules,
            onWriteNew = { showTimeCapsuleSheet = true },
            onReflect = { capsule -> reflectionCapsuleId = capsule.id }
        )

        // 3. General Settings
        SettingsSection(title = "General") {
            ThemeSettingsCard(
                selectedTheme = settingsState.themeMode,
                onSettingsThemeChange = onSettingsThemeChange
            )
            HapticsSettingsCard(
                enabled = settingsState.hapticFeedbackEnabled,
                onSettingsHapticFeedbackToggle = onSettingsHapticFeedbackToggle,
                onToggleHaptics = onToggleHaptics
            )
        }

        // 4. Notifications
        SettingsSection(title = "Notifications") {
            NotificationSettingsCard(
                profileState = profileState,
                settingsState = settingsState,
                onToggleNotifications = onToggleNotifications,
                onSettingsDailyRemindersToggle = onSettingsDailyRemindersToggle,
                onChangeReminderFrequency = onChangeReminderFrequency,
                onRequestNotificationPermission = onRequestNotificationPermission,
                onRequestExactAlarmPermission = onRequestExactAlarmPermission
            )
        }

        // 5. Data Management
        SettingsSection(title = "Data") {
            BackupSettingsCard(
                settingsState = settingsState,
                onCreateBackup = onSettingsCreateBackup,
                onRestoreBackup = onSettingsRestoreBackup,
                onSettingsRestoreFileSelected = onSettingsRestoreFileSelected
            )
        }

        // 6. About
        SettingsSection(title = "About") {
            LegalLinks(profileState)
        }
        
        Spacer(modifier = Modifier.height(com.productivitystreak.ui.theme.Spacing.xxxl))
    }

    if (showTimeCapsuleSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showTimeCapsuleSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            TimeCapsuleCreationSheet(
                onDismiss = { showTimeCapsuleSheet = false },
                onCreate = { message, goal, days ->
                    onCreateTimeCapsule(message, goal, days)
                    showTimeCapsuleSheet = false
                }
            )
        }
    }

    if (reflectionCapsule != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { reflectionCapsuleId = null },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            TimeCapsuleReflectionSheet(
                capsule = reflectionCapsule,
                onDismiss = { reflectionCapsuleId = null },
                onSave = { text ->
                    onSaveTimeCapsuleReflection(reflectionCapsule.id, text)
                    reflectionCapsuleId = null
                }
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = com.productivitystreak.ui.theme.Spacing.xxs)
        )
        content()
    }
}
