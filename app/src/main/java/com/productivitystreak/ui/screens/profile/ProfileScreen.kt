package com.productivitystreak.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.profile.LegalItem
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ProfileTheme
import com.productivitystreak.ui.state.profile.ReminderFrequency

@Composable
fun ProfileScreen(
    userName: String,
    state: ProfileState,
    quote: com.productivitystreak.data.model.Quote?,
    onRefreshQuote: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onChangeReminderFrequency: (ReminderFrequency) -> Unit,
    onToggleWeeklySummary: (Boolean) -> Unit,
    onChangeTheme: (ProfileTheme) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        GreetingCard(userName = userName, quote = quote?.text, onRefreshQuote = onRefreshQuote)
        
        // Settings Navigation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Settings", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Manage app preferences and data",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                TextButton(onClick = onNavigateToSettings) {
                    Text(text = "Open")
                }
            }
        }
        
        NotificationPreferences(
            notificationEnabled = state.notificationEnabled,
            frequency = state.reminderFrequency,
            hasWeeklySummary = state.hasWeeklySummary,
            activeCategories = state.activeCategories,
            onToggleNotifications = onToggleNotifications,
            onChangeFrequency = onChangeReminderFrequency,
            onToggleWeeklySummary = onToggleWeeklySummary
        )
        HapticsPreference(
            enabled = state.hapticsEnabled,
            onToggleHaptics = onToggleHaptics
        )
        ThemeSection(theme = state.theme, onChangeTheme = onChangeTheme)
        LegalLinks(items = state.legalLinks)
    }
}

@Composable
private fun GreetingCard(userName: String, quote: String?, onRefreshQuote: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Hi $userName", style = MaterialTheme.typography.titleLarge)
            Text(
                text = quote ?: "Stay consistent and your future self will thank you.",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onRefreshQuote) {
                Text(text = "Refresh quote")
            }
        }
    }
}

@Composable
private fun NotificationPreferences(
    notificationEnabled: Boolean,
    frequency: ReminderFrequency,
    hasWeeklySummary: Boolean,
    activeCategories: Set<String>,
    onToggleNotifications: (Boolean) -> Unit,
    onChangeFrequency: (ReminderFrequency) -> Unit,
    onToggleWeeklySummary: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Notifications, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Reminders", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (notificationEnabled) "Stay nudged with friendly reminders" else "Notifications are off",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(checked = notificationEnabled, onCheckedChange = onToggleNotifications)
            }
            Text(text = "Frequency: ${frequency.name}")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReminderFrequency.values().forEach { target ->
                    TextButton(onClick = { onChangeFrequency(target) }) {
                        Text(text = target.name)
                    }
                }
            }
            SwitchPreference(
                label = "Weekly summary",
                checked = hasWeeklySummary,
                onCheckedChange = onToggleWeeklySummary
            )
            if (activeCategories.isNotEmpty()) {
                Text(
                    text = "Active categories: ${activeCategories.joinToString()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SwitchPreference(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ThemeSection(theme: ProfileTheme, onChangeTheme: (ProfileTheme) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Palette, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.size(12.dp))
                Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
            }
            ProfileTheme.values().forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(text = option.name, modifier = Modifier.weight(1f))
                    Switch(checked = option == theme, onCheckedChange = { onChangeTheme(option) })
                }
            }
        }
    }
}

@Composable
private fun HapticsPreference(enabled: Boolean, onToggleHaptics: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Vibration, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.size(12.dp))
                Text(text = "Haptics", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = if (enabled) "Subtle taps keep your focus on track" else "Haptics are disabled",
                style = MaterialTheme.typography.bodySmall
            )
            SwitchPreference(
                label = "Enable haptic feedback",
                checked = enabled,
                onCheckedChange = onToggleHaptics
            )
        }
    }
}

@Composable
private fun LegalLinks(items: List<LegalItem>) {
    if (items.isEmpty()) return
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Legal", style = MaterialTheme.typography.titleMedium)
            items.forEach { item ->
                Text(text = item.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
