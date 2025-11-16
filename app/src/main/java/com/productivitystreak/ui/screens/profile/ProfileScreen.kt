package com.productivitystreak.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.profile.LegalItem
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ProfileTheme
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.theme.Shapes
import com.productivitystreak.ui.theme.Spacing

@Composable
fun ProfileScreen(
    userName: String,
    state: ProfileState,
    onToggleNotifications: (Boolean) -> Unit,
    onChangeReminderFrequency: (ReminderFrequency) -> Unit,
    onToggleWeeklySummary: (Boolean) -> Unit,
    onChangeTheme: (ProfileTheme) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val background = Brush.verticalGradient(listOf(Color(0xFFF3F6FF), Color(0xFFE8EDFF)))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ProfileHeader()
        ProfileHeroCard(
            userName = userName,
            email = state.email,
            activeCategories = state.activeCategories,
            onManageAccount = onNavigateToSettings
        )
        if (!state.notificationEnabled) {
            NotificationPermissionCard(onEnable = { onToggleNotifications(true) })
        }
        SettingsShortcutCard(onNavigateToSettings = onNavigateToSettings)
        PreferencesCard(
            notificationEnabled = state.notificationEnabled,
            reminderFrequency = state.reminderFrequency,
            hasWeeklySummary = state.hasWeeklySummary,
            hapticsEnabled = state.hapticsEnabled,
            onToggleNotifications = onToggleNotifications,
            onChangeReminderFrequency = onChangeReminderFrequency,
            onToggleWeeklySummary = onToggleWeeklySummary,
            onToggleHaptics = onToggleHaptics
        )
        SupportCard(items = state.legalLinks)
        Text(
            text = "Logout",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF6A63FF),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Never Zero", style = MaterialTheme.typography.labelLarge, color = Color(0xFF7277A9))
        Text(text = "Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = "Manage your account and preferences.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7C8095))
    }
}

@Composable
private fun ProfileHeroCard(
    userName: String,
    email: String,
    activeCategories: Set<String>,
    onManageAccount: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = Color(0xFFE7E9FF)) {
                Icon(imageVector = Icons.Rounded.Person, contentDescription = null, tint = Color(0xFF6A63FF), modifier = Modifier.padding(24.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = email, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7C8095))
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.large,
                color = Color(0xFFF3F4FF)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Focus areas", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6E70A4))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (activeCategories.isEmpty()) {
                            Surface(shape = Shapes.medium, color = Color.White) {
                                Text(
                                    text = "Select interests in onboarding",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF7C8095)
                                )
                            }
                        } else {
                            activeCategories.forEach { category ->
                                Surface(shape = Shapes.medium, color = Color.White) {
                                    Text(
                                        text = category,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsShortcutCard(onNavigateToSettings: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Settings shortcuts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            ShortcutRow(icon = Icons.Rounded.Person, title = "Account", subtitle = "Personal info & login", onClick = onNavigateToSettings)
            Divider(color = Color(0xFFF0F0FF))
            ShortcutRow(icon = Icons.Rounded.Palette, title = "Appearance", subtitle = "Theme & color mode", onClick = onNavigateToSettings)
            Divider(color = Color(0xFFF0F0FF))
            ShortcutRow(icon = Icons.Rounded.Lock, title = "Privacy", subtitle = "Permissions & exports", onClick = onNavigateToSettings)
        }
    }
}

@Composable
private fun ShortcutRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(shape = CircleShape, color = Color(0xFFEEF0FF)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6A63FF), modifier = Modifier.padding(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C8095))
        }
        Icon(imageVector = Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = Color(0xFFB0B3CC))
    }
}

@Composable
private fun NotificationPermissionCard(onEnable: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color(0xFFFFF7ED)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Reminders are off", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF7A4D00))
            Text(
                text = "Turn on notifications so Never Zero can ping you when it's time to log a habit.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8A5A10)
            )
        }
    }
}

@Composable
private fun PreferencesCard(
    notificationEnabled: Boolean,
    reminderFrequency: ReminderFrequency,
    hasWeeklySummary: Boolean,
    hapticsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onChangeReminderFrequency: (ReminderFrequency) -> Unit,
    onToggleWeeklySummary: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text = "Fine-tune how reminders, summaries, and haptics support your streaks.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C8095))
            PreferenceRow(
                icon = Icons.Rounded.Notifications,
                title = "Daily Reminder",
                subtitle = if (notificationEnabled) "On" else "Off",
                trailing = {
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = onToggleNotifications
                    )
                }
            )
            PreferenceRow(
                icon = Icons.Rounded.Palette,
                title = "Reminder Time",
                subtitle = reminderFrequency.name,
                trailing = {
                    TextButton(onClick = {
                        val values = ReminderFrequency.values()
                        val currentIndex = values.indexOf(reminderFrequency)
                        val next = values[(currentIndex + 1) % values.size]
                        onChangeReminderFrequency(next)
                    }) { Text("Change") }
                }
            )
            PreferenceRow(
                icon = Icons.Rounded.Settings,
                title = "Weekly Summary",
                subtitle = if (hasWeeklySummary) "Enabled" else "Disabled",
                trailing = {
                    Switch(checked = hasWeeklySummary, onCheckedChange = onToggleWeeklySummary)
                }
            )
            PreferenceRow(
                icon = Icons.Rounded.Vibration,
                title = "Haptics",
                subtitle = if (hapticsEnabled) "Gentle feedback" else "Off",
                trailing = {
                    Switch(checked = hapticsEnabled, onCheckedChange = onToggleHaptics)
                }
            )
        }
    }
}

@Composable
private fun PreferenceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(shape = CircleShape, color = Color(0xFFEEF0FF)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6A63FF), modifier = Modifier.padding(10.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C8095))
        }
        trailing()
    }
}

@Composable
private fun SupportCard(items: List<LegalItem>) {
    if (items.isEmpty()) return
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Support", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            SupportRow(icon = Icons.Rounded.Help, label = "Help & Support", value = items.firstOrNull()?.label ?: "Browse FAQs")
            SupportRow(icon = Icons.Rounded.Settings, label = "Guides", value = items.getOrNull(1)?.label ?: "Learn about Never Zero")
        }
    }
}

@Composable
private fun SupportRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = Color(0xFFEEF0FF)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6A63FF), modifier = Modifier.padding(10.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(text = value, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C8095))
        }
        Icon(imageVector = Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = Color(0xFFB0B3CC))
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
                    Icon(imageVector = Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
