package com.productivitystreak.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.productivitystreak.ui.state.onboarding.OnboardingState
import com.productivitystreak.ui.utils.hapticFeedbackManager

@Composable
fun OnboardingDialog(
    state: OnboardingState,
    onDismiss: () -> Unit,
    onToggleCategory: (String) -> Unit,
    onComplete: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = (state.currentStep + 1f) / state.totalSteps,
        label = "onboarding-progress"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onComplete) {
                Text("Start Building Streaks")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        },
        title = { Text(text = "Welcome to Never Zero", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Never miss a day across the habits that matter. Pick a couple to focus on first.",
                    style = MaterialTheme.typography.bodyMedium
                )
                ProgressBar(progress = progress)
                CategorySelection(
                    categories = listOf("Reading", "Vocabulary", "Languages", "Journaling", "Wellness"),
                    selected = state.selectedCategories,
                    onToggle = onToggleCategory
                )
                ReminderRow(enabled = state.allowNotifications)
            }
        }
    )
}

@Composable
private fun ProgressBar(progress: Float) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(12.dp),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(24.dp)
        ) {}
    }
}

@Composable
private fun CategorySelection(
    categories: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    val context = LocalContext.current
    val haptics = remember(context) { context.hapticFeedbackManager() }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        categories.forEach { category ->
            val isSelected = selected.contains(category)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .toggleable(value = isSelected, onValueChange = {
                        if (isSelected) {
                            haptics.selection()
                        } else {
                            haptics.light()
                        }
                        onToggle(category)
                    }),
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = category, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    AnimatedVisibility(visible = isSelected) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderRow(enabled: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsActive,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("Daily Reminders", style = MaterialTheme.typography.titleSmall)
                Text(
                    "We’ll nudge you with friendly notes—no guilt trips.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "You can change reminders anytime in Profile > Notifications.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
