package com.productivitystreak.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.productivitystreak.ui.state.AddEntryType

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
    onOpenBuddhaChat: () -> Unit = {},
    onOpenLeaderboard: () -> Unit = {},
    onAddEntrySelected: (AddEntryType) -> Unit = {},
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

    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val hapticsEnabled = uiState.profileState.hapticsEnabled

    fun performHaptic(type: androidx.compose.ui.hapticfeedback.HapticFeedbackType = androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress) {
        if (hapticsEnabled) {
            haptics.performHapticFeedback(type)
        }
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

        // 2. Today's Focus (Top Priority)
        item {
            Text(
                text = "TODAY'S FOCUS",
                style = MaterialTheme.typography.labelMedium,
                color = NeverZeroTheme.designColors.textSecondary
            )
        }

        if (streakUiState.isLoading) {
            items(3) {
                com.productivitystreak.ui.components.GlassCard(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    content = {}
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
        } else if (streakUiState.todayTasks.isEmpty()) {
            item {
                DashboardEmptyState(onAddHabitClick = {
                    performHaptic()
                    onAddHabitClick()
                })
            }
        } else {
            items(streakUiState.todayTasks.size) { index ->
                val task = streakUiState.todayTasks[index]
                com.productivitystreak.ui.screens.home.ImprovedHabitRow(
                    task = task,
                    onToggle = { 
                        performHaptic(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onToggleTask(task.id) 
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
        }

        // 3. Tasks (To-Do List Focus)
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TASKS",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeverZeroTheme.designColors.textSecondary
                )
                IconButton(
                    onClick = { 
                        performHaptic()
                        showAddTaskDialog = true 
                    },
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
                    onToggle = { 
                        performHaptic()
                        onToggleOneOffTask(task.id) 
                    },
                    onDelete = { 
                        performHaptic()
                        onDeleteOneOffTask(task.id) 
                    }
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
        } else {
            item {
                // Empty state for tasks to encourage usage
                com.productivitystreak.ui.components.GlassCard(
                    modifier = Modifier.fillMaxWidth().clickable { showAddTaskDialog = true },
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add a task for today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Teach Me a Word (AI Highlight)
        item {
            com.productivitystreak.ui.screens.dashboard.components.TeachWordWidget(
                onClick = { 
                    performHaptic()
                    onAddEntrySelected(AddEntryType.TEACH) 
                }
            )
        }

        // 5. Buddha's Wisdom
        item {
            streakUiState.buddhaInsight?.let { insight ->
                BuddhaInsightCard(
                    insight = insight,
                    onRefresh = {
                        performHaptic(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        onRefreshQuote()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            performHaptic()
                            onOpenBuddhaChat() 
                        }
                )
            }
        }

        // 6. Habits List (Renamed to Disciplines)
        item {
            Text(
                text = "DAILY DISCIPLINES",
                style = MaterialTheme.typography.labelMedium,
                color = NeverZeroTheme.designColors.textSecondary,
                modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.sm)
            )
        }

        if (streakUiState.streaks.isEmpty()) {
            item {
                com.productivitystreak.ui.components.EmptyStateCard(
                    message = "No disciplines set. Begin your protocol.",
                    buttonText = "Define Protocol",
                    onClick = { onAddHabitClick() }
                )
            }
        } else {
            items(streakUiState.streaks.size) { index ->
                val streak = streakUiState.streaks[index]
                com.productivitystreak.ui.screens.home.ImprovedHabitRow(
                    streak = streak,
                    onToggle = { onSelectStreak(streak) },
                    onClick = { onSelectStreak(streak) }
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
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
