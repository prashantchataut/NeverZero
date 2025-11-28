package com.productivitystreak.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.staggeredgrid.items
import com.productivitystreak.ui.components.BentoGrid
import com.productivitystreak.ui.components.StaggeredEntryAnimation
import com.productivitystreak.ui.components.fullWidthItem
import com.productivitystreak.ui.screens.dashboard.components.*
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing

@Composable
fun DashboardScreen(
    streakUiState: com.productivitystreak.ui.screens.stats.StreakUiState,
    uiState: AppUiState,
    onToggleTask: (String) -> Unit,
    onRefreshQuote: () -> Unit,
    onAddHabitClick: () -> Unit,
    onSelectStreak: (String) -> Unit,
    onAddOneOffTask: (String) -> Unit,
    onToggleOneOffTask: (String) -> Unit,
    onDeleteOneOffTask: (String) -> Unit,
    onAssetSelected: (String) -> Unit,
    onOpenJournal: () -> Unit = {},
    onOpenTimeCapsule: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val maxStreak = streakUiState.streaks.maxOfOrNull { it.currentCount } ?: 0
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

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NeverZeroTheme.designColors.background)
            .padding(horizontal = Spacing.md),
        contentPadding = PaddingValues(vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // 1. Header
        item {
            DashboardHeader(userName = uiState.userName)
        }

        // 2. Journal & Time Capsule
        item {
            com.productivitystreak.ui.screens.home.JournalPromptCard(onClick = onOpenJournal)
        }
        item {
            com.productivitystreak.ui.screens.home.TimeCapsuleCard(onClick = onOpenTimeCapsule)
        }

        // 3. Buddha Insight
        item {
            streakUiState.buddhaInsight?.let { insight ->
                BuddhaInsightCard(
                    insight = insight,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 4. Today's Focus
        item {
            Text(
                text = "TODAY'S FOCUS",
                style = MaterialTheme.typography.labelMedium,
                color = NeverZeroTheme.designColors.textSecondary
            )
        }

        if (streakUiState.todayTasks.isEmpty()) {
            item {
                DashboardEmptyState(onAddHabitClick = onAddHabitClick)
            }
        } else {
            items(streakUiState.todayTasks.size) { index ->
                val task = streakUiState.todayTasks[index]
                com.productivitystreak.ui.screens.home.ImprovedHabitRow(
                    task = task,
                    onToggle = { onToggleTask(task.id) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
        }

        // 5. Quick Tasks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUICK TASKS",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeverZeroTheme.designColors.textSecondary
                )
                IconButton(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Task",
                        tint = NeverZeroTheme.designColors.primary
                    )
                }
            }
        }

        if (streakUiState.oneOffTasks.isNotEmpty()) {
            items(streakUiState.oneOffTasks.size) { index ->
                val task = streakUiState.oneOffTasks[index]
                OneOffTaskRow(
                    task = task,
                    onToggle = { onToggleOneOffTask(task.id) },
                    onDelete = { onDeleteOneOffTask(task.id) }
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DashboardHeader(userName: String) {
    Column(modifier = Modifier.padding(bottom = Spacing.sm)) {
        Text(
            text = "Welcome back,",
            style = MaterialTheme.typography.bodyLarge,
            color = NeverZeroTheme.designColors.textSecondary
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.displaySmall, // Bold from Type.kt update
            color = NeverZeroTheme.designColors.textPrimary
        )
    }
}
