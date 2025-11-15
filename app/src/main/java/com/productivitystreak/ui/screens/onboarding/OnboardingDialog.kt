package com.productivitystreak.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.GradientButton
import com.productivitystreak.ui.state.onboarding.OnboardingCategory
import com.productivitystreak.ui.state.onboarding.OnboardingState
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Shapes
import com.productivitystreak.ui.theme.Spacing

@Composable
fun OnboardingDialog(
    state: OnboardingState,
    onDismiss: () -> Unit,
    onToggleCategory: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onReminderTimeSelected: (String) -> Unit,
    onComplete: () -> Unit
) {
    val isLastStep = state.currentStep >= state.totalSteps - 1
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            NeverZeroTheme.gradientColors.PremiumStart.copy(alpha = 0.15f),
            NeverZeroTheme.gradientColors.PremiumEnd.copy(alpha = 0.15f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(horizontal = Spacing.xl, vertical = Spacing.xl)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = Color.White.copy(alpha = 0.92f),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.xl),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                    OnboardingHeader(
                        currentStep = state.currentStep,
                        totalSteps = state.totalSteps,
                        onDismiss = onDismiss
                    )

                    when (state.currentStep) {
                        0 -> WelcomeStep()
                        1 -> CategoriesStep(state, onToggleCategory)
                        else -> ReminderStep(state, onToggleNotifications, onReminderTimeSelected)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    val primaryEnabled = when (state.currentStep) {
                        1 -> state.selectedCategories.size >= 3
                        else -> true
                    }

                    GradientButton(
                        text = if (isLastStep) "Continue" else if (state.currentStep == 0) "Let's Begin" else "Continue",
                        onClick = { if (isLastStep) onComplete() else onNext() },
                        gradientColors = listOf(
                            NeverZeroTheme.gradientColors.PremiumStart,
                            NeverZeroTheme.gradientColors.PremiumEnd
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = primaryEnabled
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (state.currentStep > 0) {
                            TextButton(onClick = onPrevious) {
                                Text("Back", color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(Spacing.lg))
                        }

                        val secondaryLabel = when {
                            isLastStep -> "Skip for now"
                            else -> "Maybe later"
                        }
                        TextButton(onClick = if (isLastStep) onComplete else onDismiss) {
                            Text(secondaryLabel, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingHeader(
    currentStep: Int,
    totalSteps: Int,
    onDismiss: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Never Zero",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = onDismiss) {
                Text("Skip", color = MaterialTheme.colorScheme.primary)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = "Step ${currentStep + 1} of $totalSteps",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LinearProgressIndicator(
                progress = (currentStep + 1f) / totalSteps,
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = NeverZeroTheme.gradientColors.PremiumStart,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = "Never Zero",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Build habits that last. Start your journey today.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoriesStep(
    state: OnboardingState,
    onToggleCategory: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "What do you want to improve?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Select at least 3 categories to start your journey.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CategoryGrid(
            categories = state.categories,
            selected = state.selectedCategories,
            onToggle = onToggleCategory
        )
    }
}

@Composable
private fun CategoryGrid(
    categories: List<OnboardingCategory>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        categories.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                rowItems.forEach { category ->
                    CategoryCard(
                        category = category,
                        selected = selected.contains(category.label),
                        onToggle = { onToggle(category.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: OnboardingCategory,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val border = if (selected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.large),
        shape = Shapes.large,
        color = Color.White,
        tonalElevation = 6.dp,
        border = border,
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = Spacing.lg)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                text = category.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = category.label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReminderStep(
    state: OnboardingState,
    onToggleNotifications: (Boolean) -> Unit,
    onReminderTimeSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        Text(
            text = "Set Reminders",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Get gentle nudges to keep your streak going.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = Shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = "Daily Reminders",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Receive one notification per day.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.allowNotifications,
                        onCheckedChange = onToggleNotifications,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                ReminderTimeSelector(
                    selectedTime = state.reminderTime,
                    onSelect = onReminderTimeSelected
                )
            }
        }
    }
}

@Composable
private fun ReminderTimeSelector(
    selectedTime: String,
    onSelect: (String) -> Unit
) {
    val timeOptions = listOf("07:30 AM", "08:30 PM", "09:30 PM")
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = "Reminder Time",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            timeOptions.forEach { time ->
                val selected = selectedTime == time
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = Shapes.full,
                    color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = { onSelect(time) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = Spacing.sm)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = null,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
