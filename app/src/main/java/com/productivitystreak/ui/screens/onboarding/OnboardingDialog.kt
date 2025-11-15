package com.productivitystreak.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.GradientButton
import com.productivitystreak.ui.state.onboarding.OnboardingCategory
import com.productivitystreak.ui.state.onboarding.OnboardingState
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
    val background = Brush.verticalGradient(
        listOf(
            Color(0xFFF2F6FF),
            Color(0xFFE1E6FF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = Spacing.xl, vertical = Spacing.xl)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(32.dp),
            color = Color.White.copy(alpha = 0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.xl),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                    OnboardingHeader(
                        currentStep = state.currentStep,
                        totalSteps = state.totalSteps,
                        onDismiss = onDismiss
                    )

                    AnimatedContent(
                        targetState = state.currentStep,
                        transitionSpec = {
                            tween<IntSize>(250) with tween(250)
                        }, label = "onboarding-step"
                    ) { step ->
                        when (step) {
                            0 -> WelcomeStep()
                            1 -> CategoriesStep(state, onToggleCategory)
                            else -> GentleNudgeStep(state, onToggleNotifications, onReminderTimeSelected)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    val primaryEnabled = when (state.currentStep) {
                        1 -> state.selectedCategories.size >= 3
                        else -> true
                    }

                    GradientButton(
                        text = when {
                            state.currentStep == 0 -> "Let's Begin"
                            isLastStep -> "Confirm"
                            else -> "Continue"
                        },
                        onClick = {
                            if (isLastStep) onComplete() else onNext()
                        },
                        gradientColors = listOf(
                            Color(0xFF7C4DFF),
                            Color(0xFF5B6BFF)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = primaryEnabled
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.currentStep > 0) {
                            TextButton(onClick = onPrevious) {
                                Text("Back", color = Color(0xFF5B6BFF))
                            }
                        } else {
                            Spacer(modifier = Modifier.width(Spacing.xl))
                        }

                        TextButton(onClick = if (isLastStep) onComplete else onDismiss) {
                            Text(
                                text = if (isLastStep) "Skip for now" else "Skip",
                                color = Color(0xFF5B6BFF)
                            )
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
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Never Zero",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF5B6BFF))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE0E5FF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(((currentStep + 1f) / totalSteps).coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF5B6BFF))
            )
        }

        Text(
            text = "Step ${currentStep + 1} of $totalSteps",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF667085)
        )
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
            modifier = Modifier.size(160.dp),
            shape = CircleShape,
            color = Color.White,
            tonalElevation = 8.dp,
            border = BorderStroke(2.dp, Color(0xFFE8EAFF))
        ) {
            Box(contentAlignment = Alignment.Center) {
                PulsingRing()
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Never Zero",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E36)
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "Build habits that last. Start your journey today.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF5F647C),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PulsingRing() {
    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color(0xFFF2F5FF)
        ) {}
        Surface(
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
            color = Color(0xFF7C4DFF)
        ) {}
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
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E36)
        )
        Text(
            text = "Select at least 3 categories to start your journey.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5F647C)
        )

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            state.categories.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    row.forEach { category ->
                        val isSelected = state.selectedCategories.contains(category.label)
                        CategoryCard(
                            label = category.label,
                            accent = category.emoji,
                            selected = isSelected,
                            modifier = Modifier.weight(1f)
                        ) {
                            onToggleCategory(category.label)
                        }
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    label: String,
    accent: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = Shapes.large,
        color = Color.White,
        tonalElevation = 4.dp,
        border = BorderStroke(2.dp, if (selected) Color(0xFF5B6BFF) else Color(0xFFE7E9F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(text = accent, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E1E36)
            )
            Icon(
                imageVector = if (selected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (selected) Color(0xFF5B6BFF) else Color(0xFFD7DAF3)
            )
        }
    }
}

@Composable
private fun GentleNudgeStep(
    state: OnboardingState,
    onToggleNotifications: (Boolean) -> Unit,
    onReminderTimeSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "Set a Gentle Nudge",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E36)
        )
        Text(
            text = "A little reminder to help you build momentum and never have a zero day.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5F647C)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = Shapes.large,
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEEF0FF)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Alarm,
                                contentDescription = null,
                                tint = Color(0xFF5B6BFF),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.md))
                        Column {
                            Text("Daily Reminder", fontWeight = FontWeight.Medium)
                            Text(
                                text = "Keeps your streak in motion",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7C8095)
                            )
                        }
                    }
                    Switch(
                        checked = state.allowNotifications,
                        onCheckedChange = onToggleNotifications,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFF5B6BFF),
                            checkedThumbColor = Color.White
                        )
                    )
                }

                Text(
                    text = "Reminder Time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5F647C)
                )
                TimePickerRow(
                    selectedTime = state.reminderTime,
                    onValueChange = onReminderTimeSelected
                )
            }
        }
    }
}

@Composable
private fun TimePickerRow(
    selectedTime: String,
    onValueChange: (String) -> Unit
) {
    val parts = selectedTime.trim().split(" ")
    val (hour, minute, period) = when {
        parts.size == 3 -> Triple(parts[0], parts[1].removeSuffix(":"), parts[2])
        else -> Triple("08", "30", "PM")
    }
    var selectedHour by remember(hour) { mutableStateOf(hour) }
    var selectedMinute by remember(minute) { mutableStateOf(minute) }
    var selectedPeriod by remember(period) { mutableStateOf(period) }

    fun emit() {
        onValueChange("$selectedHour:$selectedMinute $selectedPeriod")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFF5F6FF))
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeColumn("Hour", selectedHour, listOf("07", "08", "09")) {
            selectedHour = it
            emit()
        }
        TimeColumn("Minute", selectedMinute, listOf("29", "30", "31")) {
            selectedMinute = it
            emit()
        }
        TimeColumn("Period", selectedPeriod, listOf("AM", "PM")) {
            selectedPeriod = it
            emit()
        }
    }
}

@Composable
private fun TimeColumn(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        options.forEach { option ->
            val isSelected = option == value
            Surface(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onSelected(option) },
                color = if (isSelected) Color.White else Color.Transparent
            ) {
                Text(
                    text = option,
                    modifier = Modifier
                        .width(48.dp)
                        .padding(vertical = 6.dp),
                    color = if (isSelected) Color(0xFF5B6BFF) else Color(0xFF7C8095),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
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
