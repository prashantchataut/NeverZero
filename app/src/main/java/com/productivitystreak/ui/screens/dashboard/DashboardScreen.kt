package com.productivitystreak.ui.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.productivitystreak.ui.components.RpgHexagon
import com.productivitystreak.ui.screens.dashboard.components.AddTaskDialog
import com.productivitystreak.ui.screens.dashboard.components.OneOffTaskRow
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.PlayfairFontFamily
import com.productivitystreak.ui.theme.Spacing
import com.productivitystreak.ui.state.AddEntryType
import com.productivitystreak.ui.components.PrimaryButton

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

    val deepForest = Color(0xFF1A2C24)
    val creamWhite = Color(0xFFF5F5DC)
    val activeProtocol = streakUiState.todayTasks.firstOrNull { !it.isCompleted }

    val heroScale = remember { Animatable(0.95f) }
    val heroAlpha = remember { Animatable(0f) }
    val heroTranslation = remember { Animatable(20f) }
    LaunchedEffect(activeProtocol?.id) {
        heroAlpha.snapTo(0f)
        heroScale.snapTo(0.95f)
        heroTranslation.snapTo(20f)
        heroAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        )
        heroScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        heroTranslation.animateTo(
            targetValue = 0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    val middleAlpha = remember { Animatable(0f) }
    LaunchedEffect(streakUiState.buddhaInsight, streakUiState.rpgStats) {
        middleAlpha.animateTo(1f, animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing))
    }

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

        // 2. Bento Grid — Top hero card
        item {
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = heroScale.value
                    scaleY = heroScale.value
                    translationY = heroTranslation.value
                    alpha = heroAlpha.value
                }
            ) {
                ActiveProtocolCard(
                    activeTask = activeProtocol,
                    deepForest = deepForest,
                    creamWhite = creamWhite,
                    onStartProtocol = activeProtocol?.let {
                        {
                            performHaptic(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onToggleTask(it.id)
                        }
                    },
                    onCreateProtocol = {
                        performHaptic()
                        onAddHabitClick()
                    }
                )
            }
        }

        // 3. Bento Grid — Middle row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = middleAlpha.value
                        translationY = (1f - middleAlpha.value) * 30f
                    },
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                DailyWisdomCard(
                    insight = streakUiState.buddhaInsight ?: "Meditate on your goals.",
                    onRefresh = {
                        performHaptic()
                        onRefreshQuote()
                    },
                    deepForest = deepForest,
                    creamWhite = creamWhite,
                    modifier = Modifier.weight(1f)
                )
                RpgSummaryCard(
                    stats = streakUiState.rpgStats,
                    deepForest = deepForest,
                    creamWhite = creamWhite,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Quest Log
        item {
            AnimatedContent(
                targetState = streakUiState.oneOffTasks,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) + slideInVertically(animationSpec = tween(280, easing = FastOutSlowInEasing)) { it / 3 }) with
                        (fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)) + slideOutVertically(animationSpec = tween(220, easing = FastOutSlowInEasing)) { it / 3 })
                },
                label = "quest-log"
            ) { quests ->
                QuestLogSection(
                    tasks = quests,
                    deepForest = deepForest,
                    creamWhite = creamWhite,
                    onAddQuest = {
                        performHaptic()
                        showAddTaskDialog = true
                    },
                    onToggle = { taskId ->
                        performHaptic()
                        onToggleOneOffTask(taskId)
                    },
                    onDelete = { taskId ->
                        performHaptic()
                        onDeleteOneOffTask(taskId)
                    }
                )
            }
        }

        // 5. Daily Disciplines
        item {
            AnimatedVisibility(
                visible = streakUiState.streaks.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically()
            ) {
                Text(
                    text = "DAILY DISCIPLINES",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeverZeroTheme.designColors.textSecondary,
                    modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.sm)
                )
            }
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
                    isCompleted = streak.currentCount > 0 && streak.lastUpdated >= System.currentTimeMillis() - 86400000,
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
private fun ActiveProtocolCard(
    activeTask: DashboardTask?,
    deepForest: Color,
    creamWhite: Color,
    onStartProtocol: (() -> Unit)?,
    onCreateProtocol: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val serifHeading = MaterialTheme.typography.labelLarge.copy(
        fontFamily = PlayfairFontFamily,
        letterSpacing = 2.sp,
        color = creamWhite.copy(alpha = 0.8f)
    )

    Surface(
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.fillMaxWidth(),
        color = if (activeTask != null) deepForest else creamWhite,
        contentColor = if (activeTask != null) creamWhite else deepForest
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Mission Control", style = serifHeading)

            if (activeTask != null) {
                Text(
                    text = activeTask.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = PlayfairFontFamily),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = activeTask.category.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = creamWhite.copy(alpha = 0.7f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Streak ${activeTask.streakCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .width(1.dp)
                            .background(creamWhite.copy(alpha = 0.2f))
                    )
                    Text(
                        text = "Honor the ritual",
                        style = MaterialTheme.typography.bodyMedium,
                        color = creamWhite.copy(alpha = 0.7f)
                    )
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onStartProtocol?.invoke()
                    },
                    enabled = onStartProtocol != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = creamWhite,
                        contentColor = deepForest
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Commence Protocol", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            } else {
                Text(
                    text = "No protocol is active. Craft one to anchor today's intent.",
                    style = MaterialTheme.typography.titleMedium,
                    color = deepForest
                )
                Button(
                    onClick = onCreateProtocol,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = deepForest,
                        contentColor = creamWhite
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Create Protocol", style = MaterialTheme.typography.labelLarge)
                }
            }
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
        modifier = modifier.heightIn(min = 220.dp),
        shape = RoundedCornerShape(28.dp),
        color = creamWhite,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Daily Wisdom",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = PlayfairFontFamily),
                    color = deepForest
                )
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh wisdom",
                        tint = deepForest
                    )
                }
            }

            Text(
                text = insight,
                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = PlayfairFontFamily, fontWeight = FontWeight.Medium),
                color = deepForest.copy(alpha = 0.85f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Summon another verse whenever you need fresh perspective.",
                style = MaterialTheme.typography.labelSmall,
                color = deepForest.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun RpgSummaryCard(
    stats: com.productivitystreak.data.model.RpgStats,
    deepForest: Color,
    creamWhite: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.heightIn(min = 220.dp),
        shape = RoundedCornerShape(28.dp),
        color = deepForest,
        contentColor = creamWhite
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "RPG Stats",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = PlayfairFontFamily)
            )
            Text(
                text = "Level ${stats.level} → ${stats.currentXp}/${stats.xpToNextLevel + stats.currentXp} XP",
                style = MaterialTheme.typography.bodyMedium,
                color = creamWhite.copy(alpha = 0.8f)
            )
            RpgHexagon(
                stats = stats,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                size = 220.dp,
                lineColor = creamWhite.copy(alpha = 0.25f),
                strokeColor = creamWhite,
                fillColor = creamWhite.copy(alpha = 0.2f)
            )
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
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = PlayfairFontFamily),
                    color = deepForest
                )
                TextButton(onClick = onAddQuest) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = deepForest)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", color = deepForest)
                }
            }

            if (tasks.isEmpty()) {
                Text(
                    text = "Log quick quests to keep momentum on days when life improvises.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = deepForest.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Spacing.sm)
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


