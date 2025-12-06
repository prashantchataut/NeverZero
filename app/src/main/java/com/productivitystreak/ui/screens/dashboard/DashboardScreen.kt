package com.productivitystreak.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.components.BentoGrid
import com.productivitystreak.ui.components.CompletionCelebration
import com.productivitystreak.ui.components.PrimaryButton
import com.productivitystreak.ui.components.fullWidthItem
import com.productivitystreak.ui.screens.dashboard.components.AddTaskDialog
import com.productivitystreak.ui.screens.dashboard.components.FocusModeWidget
import com.productivitystreak.ui.screens.dashboard.components.OneOffTaskRow
import com.productivitystreak.ui.screens.dashboard.components.RadarChartPreview
import com.productivitystreak.ui.screens.dashboard.components.VoiceControlCard
import com.productivitystreak.ui.screens.home.HomeHeader
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.state.AddEntryType
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.PlayfairFontFamily
import com.productivitystreak.ui.theme.Spacing
import com.productivitystreak.ui.interaction.rememberHapticManager
import com.productivitystreak.ui.interaction.HapticFeedback
import com.productivitystreak.ui.interaction.HapticPattern
import com.productivitystreak.ui.interaction.taskSwipeGestures
import com.productivitystreak.ui.interaction.quickActionGestures
import com.productivitystreak.ui.components.VoiceButton
import com.productivitystreak.ui.voice.rememberVoiceManager
import com.productivitystreak.ui.voice.VoiceCommand
import com.productivitystreak.ui.animation.rememberPhysicsState
import com.productivitystreak.ui.animation.elasticStretchAnimation
import com.productivitystreak.ui.animation.bounceAnimation

@OptIn(ExperimentalAnimationApi::class)
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
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var celebratingTaskId by remember { mutableStateOf<String?>(null) }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title ->
                onAddOneOffTask(title)
                showAddTaskDialog = false
            }
        )
    }

    val haptics = LocalHapticFeedback.current
    val hapticsEnabled = uiState.profileState.hapticsEnabled
    val hapticManager = rememberHapticManager()
    val voiceManager = rememberVoiceManager()

    fun performHaptic(type: HapticFeedbackType = com.productivitystreak.ui.theme.HapticTokens.Impact) {
        if (hapticsEnabled) {
            haptics.performHapticFeedback(type)
        }
    }

    val deepForest = Color(0xFF1A2C24)
    val creamWhite = Color(0xFFF5F5DC)

    // Celebration overlay
    Box(modifier = modifier.fillMaxSize()) {
        BentoGrid(
            modifier = Modifier
                .fillMaxSize()
                .background(NeverZeroTheme.designColors.background),
            columns = 2,
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.lg),
            verticalItemSpacing = Spacing.md
        ) {
            // 1. Header with greeting and XP bar (full width)
            fullWidthItem(key = "header") {
                HomeHeader(
                    userName = uiState.userName,
                    quote = uiState.quote,
                    level = streakUiState.rpgStats.level,
                    currentXp = streakUiState.rpgStats.currentXp,
                    xpToNextLevel = streakUiState.rpgStats.xpToNextLevel
                )
            }

            // 2. Focus Mode Widget (full width, large)
            fullWidthItem(key = "focus-mode") {
                FocusModeWidget(
                    onClick = {
                        performHaptic()
                        onOpenMonkMode()
                    }
                )
            }

            // 3. Daily Wisdom Card (square)
            item(key = "daily-wisdom") {
                DailyWisdomCard(
                    insight = streakUiState.buddhaInsight ?: "Meditate on your goals.",
                    onRefresh = {
                        HapticFeedback(pattern = HapticPattern.REFRESH, manager = hapticManager)
                        onRefreshQuote()
                    },
                    deepForest = deepForest,
                    creamWhite = creamWhite
                ).quickActionGestures(
                    onRefresh = {
                        HapticFeedback(pattern = HapticPattern.WISDOM_REVEAL, manager = hapticManager)
                        onRefreshQuote()
                    }
                )
            }

            // 4. Voice Control (square)
            item(key = "voice-control") {
                VoiceControlCard(
                    voiceManager = voiceManager,
                    hapticManager = hapticManager,
                    deepForest = deepForest,
                    creamWhite = creamWhite
                )
            }

            // 5. Radar Chart Preview (square)
            item(key = "radar-chart") {
                RadarChartPreview(
                    stats = streakUiState.rpgStats,
                    onClick = {
                        performHaptic()
                        // Could navigate to stats screen
                    }
                )
            }

            // 6. Quest Log Section (full width)
            fullWidthItem(key = "quest-log") {
                QuestLogSection(
                    tasks = streakUiState.oneOffTasks,
                    deepForest = deepForest,
                    creamWhite = creamWhite,
                    onAddQuest = {
                        performHaptic()
                        showAddTaskDialog = true
                    },
                    onToggle = { taskId ->
                        performHaptic(HapticFeedbackType.LongPress)
                        onToggleOneOffTask(taskId)
                    },
                    onDelete = { taskId ->
                        performHaptic()
                        onDeleteOneOffTask(taskId)
                    }
                )
            }

            // 7. Daily Disciplines Section Header (full width)
            if (streakUiState.streaks.isNotEmpty()) {
                fullWidthItem(key = "disciplines-header") {
                    Text(
                        text = "DAILY DISCIPLINES",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeverZeroTheme.designColors.textSecondary,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.xs)
                    )
                }

                // 8. Protocol list items (full width each)
                streakUiState.streaks.forEachIndexed { index, streak ->
                    fullWidthItem(key = "streak-${streak.id}") {
                        val task = DashboardTask(
                            id = streak.id,
                            title = streak.name,
                            category = streak.category,
                            streakId = streak.id,
                            isCompleted = streak.currentCount > 0 && streak.lastUpdated >= System.currentTimeMillis() - 86400000,
                            accentHex = streak.color,
                            streakCount = streak.currentCount
                        )

                        ProtocolRow(
                            task = task,
                            onToggle = {
                                performHaptic(HapticFeedbackType.LongPress)
                                celebratingTaskId = task.id
                                onToggleTask(task.id)
                            },
                            onSelect = { onSelectStreak(streak.id) },
                            hapticManager = hapticManager
                        )
                    }
                }
            } else {
                fullWidthItem(key = "empty-state") {
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
            }
        }

        // Celebration overlay
        celebratingTaskId?.let { taskId ->
            CompletionCelebration(
                trigger = true,
                onComplete = { celebratingTaskId = null },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Protocol row with animated checkbox that triggers celebration.
 */
@Composable
private fun ProtocolRow(
    task: DashboardTask,
    onToggle: () -> Unit,
    onSelect: () -> Unit,
    hapticManager: com.productivitystreak.ui.interaction.HapticManager,
    modifier: Modifier = Modifier
) {
    val checkScale = remember { Animatable(1f) }
    val physicsState = rememberPhysicsState()
    val elasticScale = elasticStretchAnimation(stretched = false)
    
    LaunchedEffect(task.isCompleted) {
        if (task.isCompleted) {
            checkScale.animateTo(
                targetValue = 1.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            checkScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .taskSwipeGestures(
                onComplete = {
                    HapticFeedback(pattern = HapticPattern.TASK_COMPLETE, manager = hapticManager)
                    onToggle()
                },
                onDelete = {
                    HapticFeedback(pattern = HapticPattern.TASK_DELETE, manager = hapticManager)
                    // Handle delete if needed
                },
                onEdit = {
                    HapticFeedback(pattern = HapticPattern.TASK_EDIT, manager = hapticManager)
                    onSelect()
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated checkbox
            IconButton(
                onClick = { if (!task.isCompleted) onToggle() },
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        scaleX = checkScale.value
                        scaleY = checkScale.value
                    }
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = if (task.isCompleted) "Completed" else "Mark complete",
                    tint = if (task.isCompleted) accentColor else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Task info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (task.isCompleted) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${task.category} • Streak ${task.streakCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Accent indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accentColor, shape = RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
private fun DailyWisdomCard(
    insight: String,
    onRefresh: () -> Unit,
    deepForest: Color,
    creamWhite: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            creamWhite,
                            creamWhite.copy(alpha = 0.9f),
                            Color(0xFFF0E6D3)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Daily Wisdom",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = PlayfairFontFamily
                        ),
                        color = deepForest,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = deepForest.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = insight,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    ),
                    color = deepForest.copy(alpha = 0.85f),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun QuestLogSection(
    tasks: List<com.productivitystreak.data.model.Task>,
    deepForest: Color,
    creamWhite: Color,
    onAddQuest: () -> Unit,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = creamWhite,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quest Log",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = deepForest,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TextButton(onClick = onAddQuest) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = deepForest,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", color = deepForest)
                }
            }

            if (tasks.isEmpty()) {
                Text(
                    text = "Log quick quests for momentum.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = deepForest.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Spacing.sm),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    tasks.forEach { task ->
                        OneOffTaskRow(
                            task = task,
                            onToggle = { onToggle(task.id) },
                            onDelete = { onDelete(task.id) }
                        )
                    }
                }
            }
        }
    }
}
