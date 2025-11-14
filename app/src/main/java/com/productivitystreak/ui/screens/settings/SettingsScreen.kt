package com.productivitystreak.ui.screens.settings

import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.state.settings.ThemeMode
import com.productivitystreak.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onThemeChange: (ThemeMode) -> Unit,
    onDailyRemindersToggle: (Boolean) -> Unit,
    onWeeklyBackupsToggle: (Boolean) -> Unit,
    onReminderTimeChange: (String) -> Unit,
    onHapticFeedbackToggle: (Boolean) -> Unit,
    onCreateBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onDismissMessage: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    var showThemeMenu by remember { mutableStateOf(false) }

    // Time Picker Dialog
    if (state.showTimePickerDialog) {
        val calendar = Calendar.getInstance()
        val timeParts = state.reminderTime.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                onReminderTimeChange(timeString)
            },
            hour,
            minute,
            true
        ).apply {
            setOnDismissListener { onDismissMessage() }
            show()
        }
    }

    // Success/Error Snackbar
    if (state.showBackupSuccessMessage || state.showRestoreSuccessMessage || state.errorMessage != null) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(3000)
            onDismissMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = {
            if (state.showBackupSuccessMessage || state.showRestoreSuccessMessage || state.errorMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(Spacing.md),
                    containerColor = if (state.errorMessage != null) 
                        MaterialTheme.colorScheme.errorContainer 
                    else 
                        MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (state.errorMessage != null) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = state.errorMessage ?: when {
                            state.showBackupSuccessMessage -> "✓ Backup created successfully"
                            state.showRestoreSuccessMessage -> "✓ Data restored successfully"
                            else -> ""
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.md)
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))

            // Appearance Section
            SettingsSection(title = "Appearance") {
                SettingsDropdownItem(
                    icon = Icons.Rounded.Palette,
                    title = "Theme",
                    subtitle = state.themeMode.displayName,
                    expanded = showThemeMenu,
                    onExpandChange = { showThemeMenu = it },
                    options = ThemeMode.values().toList(),
                    selectedOption = state.themeMode,
                    onOptionSelected = { theme ->
                        onThemeChange(theme)
                        showThemeMenu = false
                    },
                    optionLabel = { it.displayName }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Notifications Section
            SettingsSection(title = "Notifications") {
                SettingsSwitchItem(
                    icon = Icons.Rounded.Notifications,
                    title = "Daily Reminders",
                    subtitle = "Get reminded to maintain your streaks",
                    checked = state.dailyRemindersEnabled,
                    onCheckedChange = onDailyRemindersToggle
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                AnimatedVisibility(
                    visible = state.dailyRemindersEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SettingsClickableItem(
                        icon = Icons.Rounded.AccessTime,
                        title = "Reminder Time",
                        subtitle = state.reminderTime,
                        onClick = { onReminderTimeChange(state.reminderTime) }
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                SettingsSwitchItem(
                    icon = Icons.Rounded.CalendarToday,
                    title = "Weekly Backups",
                    subtitle = "Automatic weekly backup notifications",
                    checked = state.weeklyBackupsEnabled,
                    onCheckedChange = onWeeklyBackupsToggle
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Feedback Section
            SettingsSection(title = "Feedback") {
                SettingsSwitchItem(
                    icon = Icons.Rounded.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on interactions",
                    checked = state.hapticFeedbackEnabled,
                    onCheckedChange = onHapticFeedbackToggle
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Data Management Section
            SettingsSection(title = "Data Management") {
                SettingsActionItem(
                    icon = Icons.Rounded.Backup,
                    title = "Create Backup",
                    subtitle = state.lastBackupTime?.let { "Last backup: $it" } ?: "Backup your data to file",
                    isLoading = state.isBackupInProgress,
                    onClick = onCreateBackup
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                SettingsActionItem(
                    icon = Icons.Rounded.CloudDownload,
                    title = "Restore Backup",
                    subtitle = "Restore data from backup file",
                    isLoading = state.isRestoreInProgress,
                    onClick = onRestoreBackup
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // About Section
            SettingsSection(title = "About") {
                SettingsInfoItem(
                    icon = Icons.Rounded.Info,
                    title = "App Version",
                    subtitle = state.appVersion
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = Spacing.xs, bottom = Spacing.sm)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = Shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = Elevation.level1
        ) {
            Column(
                modifier = Modifier.padding(vertical = Spacing.xs)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Size.iconMedium)
        )
        
        Spacer(modifier = Modifier.width(Spacing.md))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(Size.iconMedium)
        )
        
        Spacer(modifier = Modifier.width(Spacing.md))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Size.iconMedium)
        )
    }
}

@Composable
private fun <T> SettingsDropdownItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionLabel: (T) -> String
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandChange(!expanded) }
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Size.iconMedium)
            )
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Size.iconMedium)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Spacing.xxxl, end = Spacing.md, bottom = Spacing.sm)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Shapes.small)
                            .clickable { onOptionSelected(option) }
                            .background(
                                if (option == selectedOption) 
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                else 
                                    MaterialTheme.colorScheme.surface
                            )
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = { onOptionSelected(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (option == selectedOption)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (option != options.last()) {
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(Size.iconMedium)
        )
        
        Spacer(modifier = Modifier.width(Spacing.md))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Size.iconMedium),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Size.iconMedium)
            )
        }
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Size.iconMedium)
        )
        
        Spacer(modifier = Modifier.width(Spacing.md))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
