package com.productivitystreak.ui.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.PrimaryButton
import com.productivitystreak.ui.components.SecondaryButton
import com.productivitystreak.ui.screens.dashboard.components.*
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing
import kotlinx.coroutines.delay
import java.time.LocalTime

@Composable
fun DashboardScreen(
    streakUiState: com.productivitystreak.ui.screens.stats.StreakUiState,
    appUiState: AppUiState,
    onToggleTask: (String) -> Unit,
    onRefreshQuote: () -> Unit,
    onAddHabitClick: () -> Unit,
    onSelectStreak: (String) -> Unit,
    onAddOneOffTask: (String) -> Unit,
    onToggleOneOffTask: (String) -> Unit,
    onDeleteOneOffTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val greetingPrefix = remember {
        val hour = LocalTime.now().hour
        when {
            hour in 5..11 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            hour in 17..21 -> "Good Evening"
            else -> "Hello"
        }
    }

    val leadStreak = streakUiState.streaks.find { it.id == streakUiState.selectedStreakId }
        ?: streakUiState.streaks.firstOrNull()

    val progress by animateFloatAsState(
        targetValue = leadStreak?.progress ?: 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "lead-progress"
    )

    val confettiState = remember { mutableStateMapOf<String, Boolean>() }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title ->
                onAddOneOffTask(title)
                showAddTaskDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        // 1. Greeting & Focus Ring
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = appUiState.userName,
                        label = "dashboard-greeting"
                    ) { name ->
                        Text(
                            text = "$greetingPrefix, $name",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "Let's make today count.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Focus Ring (Placeholder for now, can be elaborated)
                CircularProgressIndicator(
                    progress = { 
                        val total = streakUiState.todayTasks.size
                        val completed = streakUiState.todayTasks.count { it.isCompleted }
                        if (total > 0) completed.toFloat() / total else 0f
                    },
                    modifier = Modifier.size(48.dp),
                    color = NeverZeroTheme.designColors.primary,
                    trackColor = NeverZeroTheme.designColors.primary.copy(alpha = 0.2f),
                    strokeWidth = 4.dp
                )
            }
        }

        // 2. Morning Brief
        item {
            MorningBriefCard(
                quote = appUiState.quote,
                isQuoteLoading = appUiState.isQuoteLoading,
                todayTasks = streakUiState.todayTasks,
                onRefreshQuote = onRefreshQuote,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 3. Lead Habit
        if (leadStreak != null) {
            item {
                LeadHabitCard(
                    streak = leadStreak,
                    progress = progress,
                    onClick = { onSelectStreak(leadStreak.id) }
                )
            }
        }

        // 4. Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                PrimaryButton(
                    text = "Add Habit",
                    onClick = onAddHabitClick,
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = "View Stats",
                    onClick = { /* Navigate to stats - handled by bottom nav usually, but this is a shortcut */ },
                    icon = Icons.Default.BarChart,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. Today's Habits
        item {
            Text(
                text = "Today's Focus",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = Spacing.sm)
            )
        }

        if (streakUiState.todayTasks.isEmpty()) {
            item { DashboardEmptyState(onAddHabitClick = onAddHabitClick) }
        } else {
            items(streakUiState.todayTasks, key = { it.id }) { task ->
                val showConfetti = confettiState[task.id] == true

                DashboardTaskRow(
                    task = task,
                    onToggle = {
                        if (!task.isCompleted) {
                            if (appUiState.profileState.hapticsEnabled) { // Using appUiState.profileState assuming it's available
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            confettiState[task.id] = true
                            onToggleTask(task.id)
                        }
                    },
                    showConfetti = showConfetti
                )

                LaunchedEffect(showConfetti) {
                    if (showConfetti) {
                        delay(450)
                        confettiState[task.id] = false
                    }
                }
            }
        }

        // 6. One-Off Tasks
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (streakUiState.oneOffTasks.isEmpty()) {
            item {
                Text(
                    text = "No quick tasks. Clear mind.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = Spacing.xs)
                )
            }
        } else {
            items(streakUiState.oneOffTasks, key = { it.id }) { task ->
                OneOffTaskRow(
                    task = task,
                    onToggle = { onToggleOneOffTask(task.id) },
                    onDelete = { onDeleteOneOffTask(task.id) }
                )
            }
        }
        
        // Bottom spacer for navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
