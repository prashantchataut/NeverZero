package com.productivitystreak.ui.screens.profile.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.utils.PermissionManager

data class ReminderCadenceOption(val label: String, val frequency: ReminderFrequency)

private fun ReminderFrequency.toDropdownLabel(): String = when (this) {
    ReminderFrequency.Daily -> "Daily"
    ReminderFrequency.Weekly -> "Weekly"
    ReminderFrequency.None -> "Daily"
}

@Composable
fun NotificationSettingsCard(
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

    com.productivitystreak.ui.components.GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Reminders",
                style = MaterialTheme.typography.titleMedium
            )

            PreferenceRow(
                title = "Enable notifications",
                subtitle = "Allow Never Zero to send protocol nudges.",
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
fun PreferenceRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
