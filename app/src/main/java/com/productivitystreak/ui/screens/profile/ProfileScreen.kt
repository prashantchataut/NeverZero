package com.productivitystreak.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.profile.LegalItem
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ProfileTheme
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Shapes
import com.productivitystreak.ui.theme.Spacing

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
    val scrollState = rememberScrollState()
    val gradient = Brush.verticalGradient(
        listOf(
            NeverZeroTheme.gradientColors.PremiumStart.copy(alpha = 0.08f),
            NeverZeroTheme.gradientColors.PremiumEnd.copy(alpha = 0.08f)
        )
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            ProfileHeader()
            GreetingCard(userName = userName, quote = quote?.text, onRefreshQuote = onRefreshQuote)
            QuickLinksCard(onNavigateToSettings = onNavigateToSettings)
            NotificationPreferences(
                notificationEnabled = state.notificationEnabled,
                frequency = state.reminderFrequency,
                hasWeeklySummary = state.hasWeeklySummary,
                activeCategories = state.activeCategories,
                onToggleNotifications = onToggleNotifications,
                onChangeFrequency = onChangeReminderFrequency,
                onToggleWeeklySummary = onToggleWeeklySummary
            )
            ThemeSection(theme = state.theme, onChangeTheme = onChangeTheme)
            HapticsPreference(
                enabled = state.hapticsEnabled,
                onToggleHaptics = onToggleHaptics
            )
            LegalLinks(items = state.legalLinks)
            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

@Composable
private fun ProfileHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = "Never Zero",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GreetingCard(userName: String, quote: String?, onRefreshQuote: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = Shapes.extraLarge,
        tonalElevation = 8.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = NeverZeroTheme.gradientColors.PremiumStart.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = NeverZeroTheme.gradientColors.PremiumStart,
                    modifier = Modifier.padding(Spacing.lg)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Joined January 2024",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onRefreshQuote) {
                Text(text = quote ?: "Build habits that last.")
            }
        }
    }
}

@Composable
private fun QuickLinksCard(onNavigateToSettings: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.Person, contentDescription = null)
                Spacer(modifier = Modifier.size(Spacing.sm))
                Text(text = "Account", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.ColorLens, contentDescription = null)
                Spacer(modifier = Modifier.size(Spacing.sm))
                Text(text = "Appearance", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = null)
                Spacer(modifier = Modifier.size(Spacing.sm))
                Text(text = "Privacy", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onNavigateToSettings, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Open Settings")
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Notifications, contentDescription = null, modifier = Modifier.size(Spacing.xl))
                Spacer(Modifier.size(Spacing.sm))
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
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Column(Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Palette, contentDescription = null, modifier = Modifier.size(Spacing.xl))
                Spacer(Modifier.size(Spacing.sm))
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Column(Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Vibration, contentDescription = null, modifier = Modifier.size(Spacing.xl))
                Spacer(Modifier.size(Spacing.sm))
                Text(text = "Haptics", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = if (enabled) "Subtle taps keep your focus on track" else "Haptics are disabled",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Column(Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.Help, contentDescription = null)
                Spacer(modifier = Modifier.size(Spacing.sm))
                Text(text = "Help & Support", style = MaterialTheme.typography.titleMedium)
            }
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.label, style = MaterialTheme.typography.bodyMedium)
                    Icon(imageVector = Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
