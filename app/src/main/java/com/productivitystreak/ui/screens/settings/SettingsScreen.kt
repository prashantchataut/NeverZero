package com.productivitystreak.ui.screens.settings

import android.app.TimePickerDialog
import android.net.Uri
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.state.settings.ThemeMode
import com.productivitystreak.ui.theme.Elevation
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Shapes
import com.productivitystreak.ui.theme.Spacing
import com.productivitystreak.ui.theme.Size
import java.util.Calendar

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
    onRestoreFileSelected: (Uri) -> Unit,
    onDismissRestoreDialog: () -> Unit,
    onDismissMessage: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    var showThemeMenu by remember { mutableStateOf(false) }

    val backupPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onRestoreFileSelected(uri)
        }
    }

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

    if (state.showRestoreDialog) {
        AlertDialog(
            onDismissRequest = onDismissRestoreDialog,
            title = {
                Text(text = "Restore backup")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        text = "Select the JSON backup file you previously exported to restore your streaks, reading sessions, reflections, and more.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (state.isRestoreInProgress) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Text(text = "Restoring data...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !state.isRestoreInProgress,
                    onClick = {
                        backupPickerLauncher.launch(arrayOf("application/json"))
                    }
                ) {
                    Text(text = "Choose file")
                }
            },
            dismissButton = {
                Text(
                    text = "Cancel",
                    modifier = Modifier
                        .clip(Shapes.full)
                        .clickable(
                            enabled = !state.isRestoreInProgress,
                            onClick = onDismissRestoreDialog
                        )
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )
    }

    val gradient = Brush.verticalGradient(
        listOf(
            NeverZeroTheme.gradientColors.PremiumStart.copy(alpha = 0.08f),
            NeverZeroTheme.gradientColors.PremiumEnd.copy(alpha = 0.08f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            SettingsCard(title = "Appearance") {
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

            SettingsCard(title = "Notifications") {
                SettingsSwitchItem(
                    icon = Icons.Rounded.Notifications,
                    title = "Daily Reminders",
                    subtitle = "Get reminded to maintain your streaks",
                    checked = state.dailyRemindersEnabled,
                    onCheckedChange = onDailyRemindersToggle
                )

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

                SettingsSwitchItem(
                    icon = Icons.Rounded.CalendarToday,
                    title = "Weekly Backups",
                    subtitle = "Automatic weekly backup notifications",
                    checked = state.weeklyBackupsEnabled,
                    onCheckedChange = onWeeklyBackupsToggle
                )
            }

            SettingsCard(title = "Feedback") {
                SettingsSwitchItem(
                    icon = Icons.Rounded.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on interactions",
                    checked = state.hapticFeedbackEnabled,
                    onCheckedChange = onHapticFeedbackToggle
                )
            }

            SettingsCard(title = "Data Management") {
                SettingsActionItem(
                    icon = Icons.Rounded.Backup,
                    title = "Create Backup",
                    subtitle = "Save your streak data securely",
                    onClick = onCreateBackup
                )
                SettingsActionItem(
                    icon = Icons.Rounded.Restore,
                    title = "Restore Backup",
                    subtitle = "Recover your saved streaks",
                    onClick = onRestoreBackup
                )
            }

            SettingsCard(title = "Account") {
                SettingsClickableItem(
                    icon = Icons.Rounded.Security,
                    title = "Privacy",
                    subtitle = "Manage permissions and safeguards",
                    onClick = {}
                )
                SettingsClickableItem(
                    icon = Icons.Rounded.Info,
                    title = "About",
                    subtitle = "App version and credits",
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = Shapes.extraLarge,
            color = Color.White,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp
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
