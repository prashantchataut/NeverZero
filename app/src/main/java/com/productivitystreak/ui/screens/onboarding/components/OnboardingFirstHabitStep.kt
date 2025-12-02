package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.productivitystreak.ui.theme.Spacing

@Composable
fun OnboardingFirstHabitStep(
    goal: String,
    commitmentMinutes: Int,
    frequencyPerWeek: Int,
    onSetGoal: (String) -> Unit,
    onSetCommitment: (Int, Int) -> Unit
) {
    var localGoal by remember { mutableStateOf(goal) }
    var minutes by remember { mutableStateOf(commitmentMinutes.coerceIn(1, 60)) }
    var frequency by remember { mutableStateOf(frequencyPerWeek.coerceIn(1, 7)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pick one lead protocol to start.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
        )

        val quickPicks = listOf(
            "Drink a glass of water",
            "Read 5 pages",
            "Walk for 5 minutes",
            "Write one sentence"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPicks.take(2).forEach { title ->
                QuickPickPill(
                    label = title,
                    selected = localGoal == title,
                    onClick = {
                        localGoal = title
                        onSetGoal(title)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPicks.drop(2).forEach { title ->
                QuickPickPill(
                    label = title,
                    selected = localGoal == title,
                    onClick = {
                        localGoal = title
                        onSetGoal(title)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        OutlinedTextField(
            value = localGoal,
            onValueChange = {
                localGoal = it
                onSetGoal(it)
            },
            label = { Text("Or describe your own protocol") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        CommitmentRow(
            minutes = minutes,
            frequencyPerWeek = frequency,
            onMinutesChanged = {
                minutes = it
                onSetCommitment(minutes, frequency)
            },
            onFrequencyChanged = {
                frequency = it
                onSetCommitment(minutes, frequency)
            }
        )
    }
}

@Composable
private fun QuickPickPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = if (selected) 4.dp else 0.dp,
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        else MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CommitmentRow(
    minutes: Int,
    frequencyPerWeek: Int,
    onMinutesChanged: (Int) -> Unit,
    onFrequencyChanged: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "How small should we keep it?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Minutes per day",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                QuantitySelector(
                    value = minutes,
                    onValueChange = onMinutesChanged,
                    min = 1,
                    max = 60,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Days per week",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                QuantitySelector(
                    value = frequencyPerWeek,
                    onValueChange = onFrequencyChanged,
                    min = 1,
                    max = 7,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = 1,
    max: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.FilledIconButton(
            onClick = {
                val newValue = (value - 1).coerceAtLeast(min)
                if (newValue != value) onValueChange(newValue)
            },
            enabled = value > min,
            modifier = Modifier.size(40.dp)
        ) {
            androidx.compose.material3.Icon(Icons.Outlined.Remove, contentDescription = "Decrease")
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium
        )

        androidx.compose.material3.FilledIconButton(
            onClick = {
                val upper = max ?: Int.MAX_VALUE
                val newValue = (value + 1).coerceAtMost(upper)
                if (newValue != value) onValueChange(newValue)
            },
            enabled = max?.let { value < it } ?: true,
            modifier = Modifier.size(40.dp)
        ) {
            androidx.compose.material3.Icon(Icons.Outlined.Add, contentDescription = "Increase")
        }
    }
}
