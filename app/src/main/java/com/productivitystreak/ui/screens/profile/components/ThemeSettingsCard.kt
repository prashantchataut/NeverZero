package com.productivitystreak.ui.screens.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.settings.ThemeMode

private data class ThemeOption(val label: String, val mode: ThemeMode)

@Composable
fun ThemeSettingsCard(
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

@OptIn(ExperimentalMaterial3Api::class)
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
