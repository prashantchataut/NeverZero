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
    modifier: Modifier = Modifier
) {
    
    // Calculate total streak (sum of all current streaks or max streak - simplified to max for now)
    val maxStreak = streakUiState.streaks.maxOfOrNull { it.currentCount } ?: 0

    var showAddTaskDialog by remember { mutableStateOf(false) }
    val showConfetti by remember { mutableStateOf(false) }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title ->
                onAddOneOffTask(title)
                showAddTaskDialog = false
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeverZeroTheme.designColors.background)
    ) {
        BentoGrid(
            contentPadding = PaddingValues(
                start = Spacing.md,
                end = Spacing.md,
                top = Spacing.lg,
                bottom = 100.dp // Space for bottom nav
            )
        ) {
            // 1. Greeting (Full Width)
            fullWidthItem {
                StaggeredEntryAnimation(index = 0) {
                    DashboardHeader(userName = uiState.userName)
                }
            }

            // 2. Hero Streak Widget (Full Width for impact)
            fullWidthItem {
                StaggeredEntryAnimation(index = 1) {
                    HeroStreakWidget(streakCount = maxStreak)
                }
            }

            // 3. Quick Actions (Full Width)
            fullWidthItem {
                StaggeredEntryAnimation(index = 2) {
                    QuickActionsWidget(
                        onAddHabit = onAddHabitClick,
                        onViewStats = { /* Navigate to stats */ },
                        onSettings = { /* Navigate to settings */ }
                    )
                }
            }

            // 4. Buddha Insight
            item {
                streakUiState.buddhaInsight?.let { insight ->
                    StaggeredEntryAnimation(index = 4) {
                        BuddhaInsightCard(
                            insight = insight,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 5. Today's Habits Header (Full Width)
            fullWidthItem {
                StaggeredEntryAnimation(index = 5) {
                    Text(
                        text = "TODAY'S FOCUS",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeverZeroTheme.designColors.textSecondary,
                        modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.sm)
                    )
                }
            }

            // 6. Habits List (Full Width items for now, could be grid)
            if (streakUiState.todayTasks.isEmpty()) {
                fullWidthItem {
                    StaggeredEntryAnimation(index = 6) {
                        DashboardEmptyState(onAddHabitClick = onAddHabitClick)
                    }
                }
            } else {
                items(streakUiState.todayTasks.size) { index ->
                    val task = streakUiState.todayTasks[index]
                    StaggeredEntryAnimation(index = 6 + index) {
                        DashboardTaskRow(
                            task = task,
                            onToggle = { onToggleTask(task.id) },
                            showConfetti = showConfetti
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }
            }
            
            // 7. Quick Tasks Header
             fullWidthItem {
                 StaggeredEntryAnimation(index = 10) { // Arbitrary index offset
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.lg, bottom = Spacing.sm),
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
            }

            // 8. Quick Tasks List
             if (streakUiState.oneOffTasks.isNotEmpty()) {
                items(streakUiState.oneOffTasks.size) { index ->
                    val task = streakUiState.oneOffTasks[index]
                    StaggeredEntryAnimation(index = 11 + index) {
                        OneOffTaskRow(
                            task = task,
                            onToggle = { onToggleOneOffTask(task.id) },
                            onDelete = { onDeleteOneOffTask(task.id) }
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }
            }
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
