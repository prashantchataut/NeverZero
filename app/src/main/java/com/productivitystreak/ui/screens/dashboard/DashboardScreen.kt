package com.productivitystreak.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.productivitystreak.ui.components.EmptyState
import com.productivitystreak.ui.components.PrimaryButton
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.screens.profile.components.RpgStatsCard

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
    onOpenMonkMode: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
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

    fun performHaptic(type: androidx.compose.ui.hapticfeedback.HapticFeedbackType = com.productivitystreak.ui.theme.HapticTokens.Impact) {
        if (hapticsEnabled) {
            haptics.performHapticFeedback(type)
        }
    }

    val scrollState = androidx.compose.foundation.lazy.rememberLazyListState()

    androidx.compose.foundation.lazy.LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
            .background(NeverZeroTheme.designColors.background)
            .padding(horizontal = Spacing.md),
        contentPadding = PaddingValues(vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // 1. Header
        item {
            com.productivitystreak.ui.screens.home.HomeHeader(
                userName = uiState.userName,
                quote = uiState.quote,
                level = streakUiState.rpgStats.level,
                currentXp = streakUiState.rpgStats.currentXp,
                xpToNextLevel = streakUiState.rpgStats.xpToNextLevel
            )
        }



        // 2. Focus Section (Hero Card)
        item {
            val activeProtocol = streakUiState.todayTasks.firstOrNull { !it.isCompleted }
            val activeQuest = streakUiState.oneOffTasks.firstOrNull { !it.isCompleted }

            if (activeProtocol != null) {
                com.productivitystreak.ui.screens.home.FocusHeroCard(
                    title = activeProtocol.title,
                    subtitle = "Active Protocol",
                    accentHex = activeProtocol.accentHex,
                    streakCount = activeProtocol.streakCount,
                    onStart = {
                        performHaptic()
                        onToggleTask(activeProtocol.id)
                    }
                )
            } else if (activeQuest != null) {
                com.productivitystreak.ui.screens.home.FocusHeroCard(
                    title = activeQuest.title,
                    subtitle = "Quest of the Day",
                    accentHex = "#FF5722", // Default orange for quests
                    streakCount = null,
                    onStart = {
                        performHaptic()
                        onToggleOneOffTask(activeQuest.id)
                    }
                )
            } else {
                // No active tasks - show empty state or "All Clear"
                 com.productivitystreak.ui.components.GlassCard(
                    modifier = Modifier.fillMaxWidth().clickable { onAddHabitClick() },
                    contentPadding = PaddingValues(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ALL SYSTEMS GO",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeverZeroTheme.designColors.primary,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active protocols",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeverZeroTheme.designColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            text = "Initialize New Protocol",
                            onClick = { onAddHabitClick() }
                        )
                    }
                }
            }
        }

        // 3. Quests (To-Do List Focus)
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUESTS",
                    style = MaterialTheme.typography.labelLarge,
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
                        contentDescription = "Add Quest",
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
                            text = "Add a quest for today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 3. Widget Grid (2x2)
        item {
            com.productivitystreak.ui.screens.dashboard.components.DashboardWidgetGrid(
                wordOfTheDay = uiState.vocabularyState.wordOfTheDay, // Need to ensure this is exposed
                buddhaInsight = streakUiState.buddhaInsight,
                onTeachWordClick = {
                    performHaptic()
                    onAddEntrySelected(AddEntryType.TEACH)
                },
                onBuddhaInsightRefresh = {
                    performHaptic()
                    // Trigger refresh logic if available in VM
                },
                onMonkModeClick = {
                    performHaptic()
                    onOpenMonkMode()
                },
                onChallengesClick = {
                    performHaptic()
                    onOpenChallenges()
                }
            )
        }

        // 7. Habits List (Renamed to Disciplines)
        item {
            Text(
                text = "DAILY DISCIPLINES",
                style = MaterialTheme.typography.labelLarge,
                color = NeverZeroTheme.designColors.textSecondary,
                modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.sm)
            )
        }

        if (streakUiState.streaks.isEmpty()) {
            item {
                com.productivitystreak.ui.components.EmptyState(
                    icon = com.productivitystreak.ui.icons.AppIcons.AddHabit,
                    message = "No disciplines set. Begin your protocol.",
                    action = {
                        PrimaryButton(
                            text = "Define Protocol",
                            onClick = { onAddHabitClick() }
                        )
                    }
                )
            }
        } else {

            items(streakUiState.streaks.size) { index ->
                val streak = streakUiState.streaks[index]
                val task = DashboardTask(
                    id = streak.id,
                    title = streak.name,
                    category = streak.category,
                    streakId = streak.id,
                    isCompleted = streak.currentCount > 0 && streak.lastUpdated >= System.currentTimeMillis() - 86400000, // Rough check, logic should be in VM
                    accentHex = streak.color,
                    streakCount = streak.currentCount
                )
                
                com.productivitystreak.ui.screens.home.ImprovedHabitRow(
                    task = task,
                    onToggle = { onSelectStreak(streak.id) },
                    modifier = Modifier.clickable { onSelectStreak(streak.id) }
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
        }
    }
}


