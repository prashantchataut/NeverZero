package com.productivitystreak.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.GradientButton
import com.productivitystreak.ui.state.onboarding.OnboardingCategory
import com.productivitystreak.ui.state.onboarding.OnboardingState
import com.productivitystreak.R
import com.productivitystreak.ui.theme.Shapes
import com.productivitystreak.ui.theme.Spacing
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingDialog(
    state: OnboardingState,
    onDismiss: () -> Unit,
    onToggleCategory: (String) -> Unit,
    onGoalSelected: (String) -> Unit,
    onCommitmentChanged: (Int, Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onReminderTimeSelected: (String) -> Unit,
    onComplete: () -> Unit
) {
    val isLastStep = state.currentStep >= state.totalSteps - 1
    val background = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant
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
                            fadeIn(animationSpec = tween(250)) with fadeOut(animationSpec = tween(250))
                        }, label = "onboarding-step"
                    ) { step ->
                        when (step) {
                            0 -> GoalStep(goal = state.goalHabit, onGoalSelected = onGoalSelected)
                            1 -> CommitmentStep(
                                durationMinutes = state.commitmentDurationMinutes,
                                frequencyPerWeek = state.commitmentFrequencyPerWeek,
                                onCommitmentChanged = onCommitmentChanged
                            )
                            2 -> CategoriesStep(state, onToggleCategory)
                            else -> PermissionStep(state, onToggleNotifications, onReminderTimeSelected)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    val primaryEnabled = when (state.currentStep) {
                        0 -> state.goalHabit.isNotBlank()
                        2 -> state.selectedCategories.size >= 2
                        else -> true
                    }

                    GradientButton(
                        text = when {
                            isLastStep -> stringResource(id = R.string.onboarding_primary_cta_last)
                            else -> stringResource(id = R.string.onboarding_primary_cta)
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
                                Text(stringResource(id = R.string.onboarding_back), color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(Spacing.xl))
                        }

                        TextButton(onClick = if (isLastStep) onComplete else onDismiss) {
                            Text(
                                text = if (isLastStep) stringResource(id = R.string.onboarding_skip_for_now) else stringResource(id = R.string.onboarding_secondary_cta),
                                color = MaterialTheme.colorScheme.primary
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
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cd_close_dialog), color = MaterialTheme.colorScheme.primary)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(((currentStep + 1f) / totalSteps).coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Text(
            text = stringResource(id = R.string.onboarding_step_progress, currentStep + 1, totalSteps),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            Box(
                modifier = Modifier
                    .width(if (isActive) 24.dp else 8.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

@Composable
private fun GoalStep(goal: String, onGoalSelected: (String) -> Unit) {
    var localGoal by remember(goal) { mutableStateOf(goal) }
    val suggestions = listOf("Read 5 minutes", "Meditate 10 minutes", "Walk 1 km", "Journal nightly")

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "Choose your first habit",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E36)
        )
        Text(
            text = "Start with something small and specific. We'll build momentum together.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5F647C)
        )
        OutlinedTextField(
            value = localGoal,
            onValueChange = {
                localGoal = it
                onGoalSelected(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. Read for 5 minutes") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(text = "Quick picks", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6E70A4))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                suggestions.forEach { suggestion ->
                    AssistChip(
                        onClick = {
                            localGoal = suggestion
                            onGoalSelected(suggestion)
                        },
                        label = { Text(suggestion) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (suggestion == localGoal) Color(0xFFE8E5FF) else Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CommitmentStep(
    durationMinutes: Int,
    frequencyPerWeek: Int,
    onCommitmentChanged: (Int, Int) -> Unit
) {
    var minutes by remember(durationMinutes) { mutableStateOf(durationMinutes) }
    var frequency by remember(frequencyPerWeek) { mutableStateOf(frequencyPerWeek) }

    fun emit() = onCommitmentChanged(minutes, frequency)

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "Set your micro-commitment",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E36)
        )
        Text(
            text = "Small wins add up. Pick a duration and how many days per week you'll show up.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5F647C)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = Shapes.large,
            color = Color.White,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Text(text = "Daily duration", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(text = "$minutes minutes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Slider(
                    value = minutes.toFloat(),
                    onValueChange = {
                        minutes = it.roundToInt().coerceIn(1, 60)
                        emit()
                    },
                    valueRange = 1f..60f,
                    steps = 58,
                    colors = SliderDefaults.colors(activeTrackColor = Color(0xFF5B6BFF))
                )
                Text(text = "Days per week", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    val options = listOf(3, 4, 5, 7)
                    options.forEach { option ->
                        val selected = option == frequency
                        Surface(
                            shape = Shapes.full,
                            color = if (selected) Color(0xFFE8E5FF) else Color.Transparent,
                            border = BorderStroke(1.dp, if (selected) Color(0xFF5B6BFF) else Color(0xFFE2E4F0)),
                            modifier = Modifier.clickable {
                                frequency = option
                                emit()
                            }
                        ) {
                            Text(
                                text = "$option x/week",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                color = if (selected) Color(0xFF5B6BFF) else Color(0xFF5F647C)
                            )
                        }
                    }
                }
            }
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
private fun PermissionStep(
    state: OnboardingState,
    onToggleNotifications: (Boolean) -> Unit,
    onReminderTimeSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "Stay on track with reminders",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E36)
        )
        Text(
            text = "Never Zero sends gentle nudges before the day ends so you can keep the streak alive.",
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
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val hourOptions = (1..12).map { it.toString().padStart(2, '0') }
        val minuteOptions = listOf("00", "15", "30", "45")
        val periodOptions = listOf("AM", "PM")

        TimeColumn(stringResource(id = R.string.time_picker_label_hour), selectedHour, hourOptions) {
            selectedHour = it
            emit()
        }
        TimeColumn(stringResource(id = R.string.time_picker_label_minute), selectedMinute, minuteOptions) {
            selectedMinute = it
            emit()
        }
        TimeColumn(stringResource(id = R.string.time_picker_label_period), selectedPeriod, periodOptions) {
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
