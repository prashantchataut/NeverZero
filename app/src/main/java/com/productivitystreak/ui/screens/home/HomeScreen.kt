package com.productivitystreak.ui.screens.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.icons.AppIcons
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.home.DailyContent
import com.productivitystreak.ui.theme.NeverZeroTheme
import java.time.LocalTime
import kotlinx.coroutines.delay

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@Composable
fun HomeScreen(
    uiState: AppUiState,
    viewModel: HomeViewModel,
    onHabitToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    onOpenNotificationsSettings: () -> Unit = {},
    onOpenVocabulary: () -> Unit = {},
    onOpenJournal: () -> Unit = {},
    onOpenTimeCapsule: () -> Unit = {},
    onRescueQuickAction: (streakId: String, value: Int) -> Unit = { _, _ -> }
) {
    val dailyContentState = viewModel.dailyContent.collectAsStateWithLifecycle()
    val dailyContent = dailyContentState.value
    val greeting = remember { getGreeting() }
    val streakDays = uiState.vocabularyState.currentStreakDays

    // Rescue Mode Logic
    val currentHour = LocalTime.now().hour
    val hasIncompleteTasks = uiState.todayTasks.any { !it.isCompleted }
    val showRescue = currentHour >= 17 && hasIncompleteTasks
    var showRescueDialog by remember { mutableStateOf(false) }

    if (showRescueDialog) {
        // Find the first incomplete streak for rescue
        val endangeredStreak = uiState.streaks.firstOrNull { streak ->
            streak.history.lastOrNull()?.metGoal == false
        }
        
        RescueProtocolDialog(
            endangeredStreakName = endangeredStreak?.name ?: "your habit",
            currentStreak = endangeredStreak?.currentCount ?: 0,
            onDismiss = { showRescueDialog = false },
            onQuickAction = {
                showRescueDialog = false
                // Mark today's record as "rescued" for the first incomplete streak
                endangeredStreak?.let { streak ->
                    onRescueQuickAction(streak.id, 1) // Log minimal progress
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (showRescue) {
                    RescueButton(
                        onClick = { showRescueDialog = true }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$greeting, ${uiState.userName}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Let's make today count.",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val streakColor = Color(0xFFFF5722)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Animated Fire Icon
                        val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "fire-pulse")
                        val fireScale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.2f,
                            animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                animation = androidx.compose.animation.core.tween(1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                            ),
                            label = "fire-scale"
                        )
                        val fireAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1f,
                            animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                animation = androidx.compose.animation.core.tween(1000, easing = androidx.compose.animation.core.LinearEasing),
                                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                            ),
                            label = "fire-alpha"
                        )

                        Icon(
                            imageVector = Icons.Outlined.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = streakColor.copy(alpha = fireAlpha),
                            modifier = Modifier.scale(fireScale)
                        )
                        Text(
                            text = streakDays.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = streakColor
                        )
                    }
                }
                
                // Journal Prompt
                JournalPromptCard(onClick = onOpenJournal)

                // Time Capsule
                TimeCapsuleCard(onClick = onOpenTimeCapsule)

                val buddhaState = viewModel.buddhaInsightState.collectAsStateWithLifecycle().value
                
                LaunchedEffect(uiState.streaks) {
                    // Always try to load, even if empty (handled in ViewModel now)
                    viewModel.loadBuddhaInsight(uiState.streaks)
                }
                
                com.productivitystreak.ui.components.BuddhaInsightCard(
                    state = buddhaState,
                    onRetry = { viewModel.retryBuddhaInsight(uiState.streaks) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                val sidequestState = viewModel.sidequest.collectAsStateWithLifecycle()
                sidequestState.value?.let { quest ->
                    com.productivitystreak.ui.components.SidequestCard(
                        quest = quest,
                        onAccept = { /* TODO: Handle quest acceptance */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = "Today’s Focus",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Habits List
        items(
            items = uiState.todayTasks,
            key = { it.id }
        ) { task ->
            ImprovedHabitRow(
                task = task,
                onToggle = { onHabitToggle(task.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Bottom Spacer
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Returns a friendly greeting based on the current local time.
 */
fun getGreeting(now: LocalTime = LocalTime.now()): String {
    val hour = now.hour
    return when (hour) {
        in 5..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
