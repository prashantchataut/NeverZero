package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.GradientPrimaryButton
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing

@Composable
fun OnboardingPersonalizationStep(
    userName: String,
    onUserNameChange: (String) -> Unit,
    habitName: String,
    onHabitNameChange: (String) -> Unit,
    selectedIcon: String,
    onIconSelected: (String) -> Unit,
    dailyReminderEnabled: Boolean,
    onDailyReminderToggle: (Boolean) -> Unit,
    onComplete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    // Available icons for selection
    val icons = listOf(
        "book" to Icons.Default.MenuBook,
        "fitness" to Icons.Default.FitnessCenter,
        "run" to Icons.Default.DirectionsRun,
        "water" to Icons.Default.LocalDrink,
        "edit" to Icons.Default.Edit
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = "One Last Step",
            style = MaterialTheme.typography.titleMedium,
            color = NeverZeroTheme.designColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Profile Photo Placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(NeverZeroTheme.designColors.surfaceElevated)
                .border(1.dp, NeverZeroTheme.designColors.border, CircleShape)
                .clickable { /* TODO: Implement photo picker */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Add Photo",
                tint = NeverZeroTheme.designColors.textSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        Text(
            text = "Set Up Your Profile",
            style = MaterialTheme.typography.titleMedium,
            color = NeverZeroTheme.designColors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Add a Photo (Optional)",
            style = MaterialTheme.typography.bodySmall,
            color = NeverZeroTheme.designColors.textSecondary
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Name Input
        OutlinedTextField(
            value = userName,
            onValueChange = onUserNameChange,
            label = { Text("Your Name") },
            placeholder = { Text("What should we call you?") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeverZeroTheme.designColors.primary,
                unfocusedBorderColor = NeverZeroTheme.designColors.border,
                focusedLabelColor = NeverZeroTheme.designColors.primary,
                unfocusedLabelColor = NeverZeroTheme.designColors.textSecondary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Habit Setup Section
        Text(
            text = "What's Your First Goal?",
            style = MaterialTheme.typography.titleMedium,
            color = NeverZeroTheme.designColors.textPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Start with something small. The goal is to never have a zero day.",
            style = MaterialTheme.typography.bodySmall,
            color = NeverZeroTheme.designColors.textSecondary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        OutlinedTextField(
            value = habitName,
            onValueChange = onHabitNameChange,
            label = { Text("Habit Name") },
            placeholder = { Text("e.g., Read for 10 minutes") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeverZeroTheme.designColors.primary,
                unfocusedBorderColor = NeverZeroTheme.designColors.border,
                focusedLabelColor = NeverZeroTheme.designColors.primary,
                unfocusedLabelColor = NeverZeroTheme.designColors.textSecondary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Choose an Icon",
            style = MaterialTheme.typography.bodyMedium,
            color = NeverZeroTheme.designColors.textPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            icons.forEach { (id, icon) ->
                val isSelected = selectedIcon == id
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) NeverZeroTheme.designColors.primary else NeverZeroTheme.designColors.surfaceElevated,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onIconSelected(id) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) NeverZeroTheme.designColors.onPrimary else NeverZeroTheme.designColors.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Daily Reminder Toggle
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = NeverZeroTheme.designColors.surfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeverZeroTheme.designColors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Set a Daily Reminder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeverZeroTheme.designColors.textPrimary
                )
                Switch(
                    checked = dailyReminderEnabled,
                    onCheckedChange = onDailyReminderToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeverZeroTheme.designColors.onPrimary,
                        checkedTrackColor = NeverZeroTheme.designColors.primary,
                        uncheckedThumbColor = NeverZeroTheme.designColors.textSecondary,
                        uncheckedTrackColor = NeverZeroTheme.designColors.surface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        GradientPrimaryButton(
            text = "Complete Setup & Start",
            onClick = onComplete,
            enabled = userName.isNotBlank() && habitName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing.lg))
    }
}
