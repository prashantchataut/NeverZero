package com.productivitystreak.ui.screens.profile

// Profile UI removed during architectural sanitization.

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.TimeCapsule
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.state.settings.ThemeMode
import com.productivitystreak.ui.utils.PermissionManager

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
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showTimeCapsuleSheet by rememberSaveable { mutableStateOf(false) }
    var reflectionCapsuleId by rememberSaveable { mutableStateOf<String?>(null) }
    val reflectionCapsule = reflectionCapsuleId?.let { id -> timeCapsules.firstOrNull { it.id == id } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Profile & Settings",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        AccountCard(
            userName = userName,
            email = profileState.email
        )

        Text(
            text = "Cognitive XP: $totalPoints",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        TimeCapsuleOverviewCard(
            capsules = timeCapsules,
            onWriteNew = { showTimeCapsuleSheet = true },
            onReflect = { capsule -> reflectionCapsuleId = capsule.id }
        )

        NotificationPreferencesCard(
            profileState = profileState,
            settingsState = settingsState,
            onToggleNotifications = onToggleNotifications,
            onSettingsDailyRemindersToggle = onSettingsDailyRemindersToggle,
            onChangeReminderFrequency = onChangeReminderFrequency,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRequestExactAlarmPermission = onRequestExactAlarmPermission
        )

        ThemeCard(
            selectedTheme = settingsState.themeMode,
            onSettingsThemeChange = onSettingsThemeChange
        )

        HapticsCard(
            enabled = settingsState.hapticFeedbackEnabled,
            onSettingsHapticFeedbackToggle = onSettingsHapticFeedbackToggle,
            onToggleHaptics = onToggleHaptics
        )

        BackupCard(
            settingsState = settingsState,
            onCreateBackup = onSettingsCreateBackup,
            onRestoreBackup = onSettingsRestoreBackup,
            onSettingsRestoreFileSelected = onSettingsRestoreFileSelected
        )

        LegalLinks(profileState)
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

    if (settingsState.errorMessage != null || settingsState.showBackupSuccessMessage || settingsState.showRestoreSuccessMessage || settingsState.showTimePickerDialog) {
        // Let the central snackbar represent messages; here we just clear flags when invoked externally
        // via onSettingsDismissMessage from NeverZeroApp when needed.
        // No additional dialogs to avoid duplication.
    }
}

@Composable
private fun TimeCapsuleOverviewCard(
    capsules: List<TimeCapsule>,
    onWriteNew: () -> Unit,
    onReflect: (TimeCapsule) -> Unit
) {
    val now = System.currentTimeMillis()
    val upcoming = capsules.filter { !it.opened && it.deliveryDateMillis > now }.minByOrNull { it.deliveryDateMillis }
    val pendingReflection = capsules.filter { !it.opened && it.deliveryDateMillis <= now }.minByOrNull { it.deliveryDateMillis }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Time Capsule Protocol",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Write a serious letter to your future self and revisit whether you kept your promises.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            upcoming?.let {
                Text(
                    text = "Next delivery scheduled",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = it.goalDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            pendingReflection?.let {
                Text(
                    text = "Awaiting reflection",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = it.goalDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Capture reflection",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = { onReflect(it) })
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Write to future self",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onWriteNew)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TimeCapsuleReflectionSheet(
    capsule: TimeCapsule,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var reflection by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Reflection",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Past self’s promise",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = capsule.goalDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Current reality",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = reflection,
            onValueChange = { reflection = it },
            label = { Text("Were you the person you promised to be?") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            maxLines = 6
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Save reflection",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { onSave(reflection) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TimeCapsuleCreationSheet(
    onDismiss: () -> Unit,
    onCreate: (message: String, goal: String, daysFromNow: Int) -> Unit
) {
    var goal by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var daysText by rememberSaveable { mutableStateOf("30") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Write to your future self",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Describe the person you intend to be, and when you want to be reminded.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("Promise / goal to revisit") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Letter to future self") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            maxLines = 6
        )

        OutlinedTextField(
            value = daysText,
            onValueChange = { daysText = it.filter { ch -> ch.isDigit() }.take(3) },
            label = { Text("Deliver in (days)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Schedule capsule",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable {
                        val days = daysText.toIntOrNull() ?: 30
                        onCreate(message, goal, days)
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun AccountCard(userName: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "N",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class ReminderCadenceOption(val label: String, val frequency: ReminderFrequency)

private data class ThemeOption(val label: String, val mode: ThemeMode)

private fun ReminderFrequency.toDropdownLabel(): String = when (this) {
    ReminderFrequency.Daily -> "Daily"
    ReminderFrequency.Weekly -> "Weekly"
    ReminderFrequency.None -> "Daily"
}

@Composable
private fun NotificationPreferencesCard(
    profileState: ProfileState,
    settingsState: SettingsState,
    onToggleNotifications: (Boolean) -> Unit,
    onSettingsDailyRemindersToggle: (Boolean) -> Unit,
    onChangeReminderFrequency: (ReminderFrequency) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {
    val context = LocalContext.current
    val shouldRequestNotification = PermissionManager.shouldRequestNotificationPermission(context)
    val reminderOptions = remember {
        listOf(
            ReminderCadenceOption("Daily", ReminderFrequency.Daily),
            ReminderCadenceOption("Weekly", ReminderFrequency.Weekly),
            ReminderCadenceOption("Weekdays", ReminderFrequency.Weekly)
        )
    }
    var selectedCadenceLabel by rememberSaveable {
        mutableStateOf(profileState.reminderFrequency.toDropdownLabel())
    }

    LaunchedEffect(profileState.reminderFrequency) {
        selectedCadenceLabel = when (profileState.reminderFrequency) {
            ReminderFrequency.Daily -> "Daily"
            ReminderFrequency.Weekly ->
                if (selectedCadenceLabel == "Weekdays") "Weekdays" else "Weekly"
            ReminderFrequency.None -> "Daily"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Reminders",
                style = MaterialTheme.typography.titleMedium
            )

            PreferenceRow(
                title = "Enable notifications",
                subtitle = "Allow Never Zero to send habit nudges.",
                checked = profileState.notificationEnabled,
                onCheckedChange = {
                    if (it) {
                        onRequestNotificationPermission()
                    }
                    onToggleNotifications(it)
                }
            )

            PreferenceRow(
                title = "Daily reminders",
                subtitle = "Receive a summary at your preferred time.",
                checked = settingsState.dailyRemindersEnabled,
                onCheckedChange = onSettingsDailyRemindersToggle
            )

            ReminderCadenceDropdown(
                selectedLabel = selectedCadenceLabel,
                options = reminderOptions,
                onSelect = { option ->
                    selectedCadenceLabel = option.label
                    onChangeReminderFrequency(option.frequency)
                }
            )

            if (shouldRequestNotification) {
                Spacer(modifier = Modifier.height(8.dp))
                PermissionNudgeCard(onRequestNotificationPermission, onRequestExactAlarmPermission)
            }
        }
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderCadenceDropdown(
    selectedLabel: String,
    options: List<ReminderCadenceOption>,
    onSelect: (ReminderCadenceOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Reminder cadence", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            expanded = false
                            onSelect(option)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionNudgeCard(
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Enable Notifications for better tracking",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Turn notifications and alarms back on from system settings.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Open app settings",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
private fun ThemeCard(
    selectedTheme: ThemeMode,
    onSettingsThemeChange: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Appearance", style = MaterialTheme.typography.titleMedium)
            ThemeSegmentedControl(
                selectedTheme = selectedTheme,
                onThemeSelected = onSettingsThemeChange
            )
        }
    }
}

@Composable
private fun ThemeSegmentedControl(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    val options = listOf(
        ThemeOption("Light", ThemeMode.LIGHT),
        ThemeOption("Dark", ThemeMode.DARK),
        ThemeOption("System", ThemeMode.SYSTEM)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selectedTheme == option.mode,
                onClick = { onThemeSelected(option.mode) },
                label = { Text(option.label) },
                shape = RoundedCornerShape(50)
            )
        }
    }
}

@Composable
private fun HapticsCard(
    enabled: Boolean,
    onSettingsHapticFeedbackToggle: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Haptics", style = MaterialTheme.typography.titleMedium)
            PreferenceRow(
                title = "Haptic feedback",
                subtitle = "Subtle vibrations on key actions.",
                checked = enabled,
                onCheckedChange = {
                    onSettingsHapticFeedbackToggle(it)
                    onToggleHaptics(it)
                }
            )
        }
    }
}

@Composable
private fun BackupCard(
    settingsState: SettingsState,
    onCreateBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onSettingsRestoreFileSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val restoreLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                onSettingsRestoreFileSelected(uri)
            }
        }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Backups", style = MaterialTheme.typography.titleMedium)
            Text(
                text = settingsState.lastBackupTime?.let { "Last backup: $it" } ?: "No backups yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Create backup",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(enabled = !settingsState.isBackupInProgress) { onCreateBackup() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
                Text(
                    text = "Restore from file",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(enabled = !settingsState.isRestoreInProgress) {
                            restoreLauncher.launch(arrayOf("application/json", "*/*"))
                            onRestoreBackup()
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun LegalLinks(profileState: ProfileState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Legal",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        profileState.legalLinks.forEach { item ->
            val context = LocalContext.current
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                    context.startActivity(intent)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

