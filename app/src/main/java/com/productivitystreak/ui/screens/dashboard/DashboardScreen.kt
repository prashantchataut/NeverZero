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
            val habitsCompleted = uiState.todayTasks.count { it.isCompleted }
            val totalHabits = uiState.todayTasks.size
            val currentStreak = uiState.statsState.currentLongestStreak

            DashboardHeader(
                userName = uiState.userName,
                currentStreak = currentStreak,
                habitsCompleted = habitsCompleted,
                totalHabits = totalHabits,
                scrollOffset = scrollState.firstVisibleItemScrollOffset
            )
        }

        // 1.5 Character Stats (RPG)
        item {
            RpgStatsCard(
                stats = streakUiState.rpgStats,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 2. Today's Focus (Horizontal Swipeable Cards)
        item {
            Text(
                text = "ACTIVE PROTOCOLS",
                style = MaterialTheme.typography.labelLarge,
                color = NeverZeroTheme.designColors.textSecondary,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
        }

        if (streakUiState.isLoading) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    repeat(2) {
                        com.productivitystreak.ui.components.GlassCard(
                            modifier = Modifier.width(280.dp).height(160.dp),
                            content = {}
                        )
                    }
                }
            }
        } else if (streakUiState.todayTasks.isEmpty()) {
            item {
                DashboardEmptyState(onAddHabitClick = {
                    performHaptic()
                    onAddHabitClick()
                })
            }
        } else {
            item {
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(end = Spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        count = streakUiState.todayTasks.size,
                        key = { index -> streakUiState.todayTasks[index].id }
                    ) { index ->
                        val task = streakUiState.todayTasks[index]
                        com.productivitystreak.ui.screens.dashboard.components.SwipeableHabitCard(
                            task = task,
                            onComplete = {
                                performHaptic(com.productivitystreak.ui.theme.HapticTokens.Success)
                                onToggleTask(task.id)
                            },
                            onSkip = {
                                performHaptic(com.productivitystreak.ui.theme.HapticTokens.Impact)
                                // TODO: Implement skip logic in ViewModel
                            },
                            modifier = Modifier.width(300.dp)
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

        // 4. AI Briefing as Chat
        item {
            com.productivitystreak.ui.screens.dashboard.components.AiBriefingCard(
                briefing = streakUiState.dailyBriefing,
                isLoading = streakUiState.isLoading,
                onReplyClick = {
                    performHaptic()
                    onOpenBuddhaChat()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // 5. Teach Me a Word & Monk Mode

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                com.productivitystreak.ui.screens.dashboard.components.TeachWordWidget(
                    onClick = { 
                        performHaptic()
                        onAddEntrySelected(AddEntryType.TEACH) 
                    },
                    modifier = Modifier.weight(1f)
                )
                com.productivitystreak.ui.screens.dashboard.components.MonkModeWidget(
                    onClick = {
                        performHaptic()
                        onOpenMonkMode()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 6. Challenges
        item {
            com.productivitystreak.ui.screens.dashboard.components.ChallengesWidget(
                onClick = {
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

@Composable
private fun DashboardHeader(
    userName: String,
    currentStreak: Int,
    habitsCompleted: Int,
    totalHabits: Int,
    scrollOffset: Int = 0
) {
    // We need to update HeroSection usage here, but wait, DashboardHeader currently just shows Text.
    // The previous HeroSection was likely used inside DashboardHeader or replaced it.
    // Let's check the file content again. Ah, DashboardHeader in the file I read (Step 154) ONLY has Text.
    // It seems I need to REPLACE the Text with the HeroSection component!
    
    // Wait, the plan says "Integrate HeroSection".
    // So I should replace the simple Text header with the HeroSection component.
    
    com.productivitystreak.ui.screens.dashboard.components.HeroSection(
        userName = userName,
        currentStreak = currentStreak,
        habitsCompleted = habitsCompleted,
        totalHabits = totalHabits,
        scrollOffset = scrollOffset,
        modifier = Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(24.dp))
    )
}
