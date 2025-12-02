package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.utils.PermissionManager

@Composable
fun OnboardingPermissionStep(
    allowNotifications: Boolean,
    onToggleNotificationsAllowed: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    reminderTime: String,
    onSetReminderTime: (String) -> Unit
) {
    val context = LocalContext.current
    val notificationsGranted = !PermissionManager.shouldRequestNotificationPermission(context)
    val exactAlarmsGranted = PermissionManager.canScheduleExactAlarms(context)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Never Zero works best when we can gently remind you at the right moments.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
        )

        PermissionCard(
            title = "Notifications",
            description = "Daily nudges so your streak never silently breaks.",
            enabled = allowNotifications && notificationsGranted,
            actionLabel = if (allowNotifications && notificationsGranted) "On" else "Enable",
            onClick = {
                if (!notificationsGranted) {
                    onRequestNotificationPermission()
                }
                onToggleNotificationsAllowed(!allowNotifications)
            }
        )

        PermissionCard(
            title = "Exact alarms",
            description = "Guaranteed reminders for high-priority protocols.",
            enabled = exactAlarmsGranted,
            actionLabel = if (exactAlarmsGranted) "Enabled" else "Enable",
            onClick = {
                if (!exactAlarmsGranted) onRequestExactAlarmPermission()
            }
        )

        Text(
            text = "When should we nudge you?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val options = listOf("08:30 AM", "01:00 PM", "08:30 PM")
            options.forEach { option ->
                val selected = option == reminderTime
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    else Color.Transparent,
                    tonalElevation = if (selected) 2.dp else 0.dp,
                    onClick = { onSetReminderTime(option) }
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Text(
            text = "If you’d rather skip this for now, you can always turn reminders on later from Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    enabled: Boolean,
    actionLabel: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled) NeverZeroTheme.semanticColors.Success.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (enabled) NeverZeroTheme.semanticColors.Success
                            else MaterialTheme.colorScheme.primary
                        )
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) NeverZeroTheme.semanticColors.Success
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}
