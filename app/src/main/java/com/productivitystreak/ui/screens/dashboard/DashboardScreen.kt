package com.productivitystreak.ui.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.theme.Border
import com.productivitystreak.ui.theme.Elevation
import com.productivitystreak.ui.theme.Motion
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Shapes
import com.productivitystreak.ui.theme.Size
import com.productivitystreak.ui.theme.Spacing
import java.time.LocalTime
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    state: AppUiState,
    onRefreshQuote: () -> Unit,
    onSelectStreak: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNavigateToReading: () -> Unit,
    onNavigateToVocabulary: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToDiscover: () -> Unit
) {
    val scrollState = rememberScrollState()
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFF0F3FF), Color(0xFFEEF1FF), Color(0xFFF9F5FF))
    )
    val selectedStreak = state.streaks.firstOrNull { it.id == state.selectedStreakId } ?: state.streaks.firstOrNull()
    val headlineQuote = state.quote?.text ?: "The secret of getting ahead is getting started."
    val completedTasks = state.todayTasks.count { it.isCompleted }
    val pendingTask = state.todayTasks.firstOrNull { !it.isCompleted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DashboardTopBar(userName = state.userName)

        HeroStreakCard(
            streak = selectedStreak,
            totalStreaks = state.streaks.size,
            onRefreshQuote = onRefreshQuote
        )

        QuickActionsRow(
            onNavigateToReading = onNavigateToReading,
            onNavigateToVocabulary = onNavigateToVocabulary,
            onNavigateToStats = onNavigateToStats
        )

        StreakGridSection(
            streaks = state.streaks,
            selectedId = state.selectedStreakId,
            onSelectStreak = onSelectStreak
        )

        PrimaryActionButton(
            label = if (pendingTask == null) "All streaks logged" else "+  Log Streak",
            enabled = pendingTask != null,
            onClick = { pendingTask?.let { onToggleTask(it.id) } }
        )

        DailyInspirationPanel(
            quote = headlineQuote,
            author = state.quote?.author,
            isLoading = state.isQuoteLoading,
            onRefresh = onRefreshQuote
        )

        TaskListSection(
            tasks = state.todayTasks,
            completedTasks = completedTasks,
            onToggleTask = onToggleTask
        )

        CommunityTeaser(
            onNavigateToDiscover = onNavigateToDiscover
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DashboardTopBar(userName: String) {
    val greeting = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Never Zero", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6A6F85))
            Text(text = "$greeting, $userName", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Surface(shape = CircleShape, tonalElevation = 6.dp, color = Color.White) {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Rounded.Person, contentDescription = null, tint = Color(0xFF7B61FF))
            }
        }
    }
}

@Composable
private fun HeroStreakCard(
    streak: Streak?,
    totalStreaks: Int,
    onRefreshQuote: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        tonalElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Your Streaks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "$totalStreaks active", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
                }
                IconButton(onClick = onRefreshQuote) {
                    Icon(imageVector = Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color(0xFF7B61FF))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color(0xFFE8EAFE)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Color(0xFF7B61FF), modifier = Modifier.size(36.dp))
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = streak?.name ?: "Stay inspired", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (streak == null) "Create your first habit" else "${streak.currentCount} day streak",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7C819C)
                    )
                }
            }

            if (streak != null) {
                val progressPercent = (streak.progress * 100).roundToInt().coerceIn(0, 100)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Today's Progress", style = MaterialTheme.typography.labelMedium, color = Color(0xFF7C819C))
                    LinearProgressIndicator(
                        progress = { streak.progress.coerceIn(0f, 1f) },
                        color = Color(0xFF7B61FF),
                        trackColor = Color(0xFFECEEFF)
                    )
                    Text(text = "$progressPercent% complete", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onNavigateToReading: () -> Unit,
    onNavigateToVocabulary: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionCard(title = "Reading Log", subtitle = "Track chapters", icon = Icons.AutoMirrored.Rounded.MenuBook, onClick = onNavigateToReading)
        QuickActionCard(title = "New Word", subtitle = "Expand vocab", icon = Icons.Rounded.Add, onClick = onNavigateToVocabulary)
        QuickActionCard(title = "Stats", subtitle = "View insights", icon = Icons.Rounded.Bolt, onClick = onNavigateToStats)
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = Shapes.large,
        color = Color.White,
        tonalElevation = 6.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(shape = CircleShape, color = Color(0xFFEDF0FF)) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6757FF), modifier = Modifier.padding(10.dp))
            }
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
        }
    }
}

@Composable
private fun StreakGridSection(
    streaks: List<Streak>,
    selectedId: String?,
    onSelectStreak: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Your Streaks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

        if (streaks.isEmpty()) {
            EmptyStateCard(message = "Add a habit to begin logging streaks.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                streaks.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { streak ->
                            StreakCard(
                                streak = streak,
                                selected = streak.id == selectedId,
                                modifier = Modifier.weight(1f)
                            ) { onSelectStreak(streak.id) }
                        }
                        if (row.size == 1) {
                            AddHabitCard(modifier = Modifier.weight(1f), onClick = {})
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakCard(
    streak: Streak,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accent = getCategoryColors(streak.category)
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "streak-scale"
    )

    Surface(
        modifier = modifier
            .height(170.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        shape = Shapes.large,
        color = Color.White,
        tonalElevation = if (selected) 10.dp else 4.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = streak.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = streak.category, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { streak.progress.coerceIn(0f, 1f) },
                color = accent.first,
                trackColor = accent.second.copy(alpha = 0.4f)
            )
            Text(text = "${streak.currentCount} days", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "Goal: ${streak.goalPerDay} ${streak.unit}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
        }
    }
}

@Composable
private fun AddHabitCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(170.dp)
            .clip(Shapes.large)
            .border(BorderStroke(Border.thin, Color(0xFFCBD3FF)), Shapes.large)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = CircleShape, color = Color(0xFFECEFFF)) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color(0xFF5F6BFF), modifier = Modifier.padding(12.dp))
            }
            Text(text = "New Habit", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PrimaryActionButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = if (enabled) Color(0xFF6B63FF) else Color(0xFFCBCFEF),
        tonalElevation = 8.dp,
        onClick = if (enabled) onClick else null
    ) {
        Box(modifier = Modifier.padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
            Text(text = label, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DailyInspirationPanel(
    quote: String,
    author: String?,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Daily Inspiration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onRefresh, enabled = !isLoading) {
                    Text("Refresh")
                }
            }

            AnimatedContent(targetState = isLoading, label = "quote-state") { loading ->
                if (loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "\"$quote\"", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        if (!author.isNullOrBlank()) {
                            Text(text = author, style = MaterialTheme.typography.labelLarge, color = Color(0xFF7C819C))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskListSection(
    tasks: List<DashboardTask>,
    completedTasks: Int,
    onToggleTask: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Today's Focus", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "$completedTasks/${tasks.size} completed", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
                }
                TextButton(onClick = { /* future expand */ }) {
                    Text("View All")
                }
            }

            if (tasks.isEmpty()) {
                EmptyStateCard(message = "No tasks scheduled. Add a habit to begin.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tasks.forEach { task ->
                        TaskRow(task = task, onToggleTask = onToggleTask)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: DashboardTask, onToggleTask: (String) -> Unit) {
    val categoryColors = getCategoryColors(task.category)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.large,
        color = Color(0xFFF6F7FF)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) categoryColors.first else categoryColors.second),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = task.isCompleted, label = "task-icon") { completed ->
                    Icon(
                        imageVector = if (completed) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (completed) Color.White else categoryColors.first
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(text = task.category, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
            }
            TextButton(onClick = { if (!task.isCompleted) onToggleTask(task.id) }, enabled = !task.isCompleted) {
                Text(if (task.isCompleted) "Done" else "Log")
            }
        }
    }
}

@Composable
private fun CommunityTeaser(onNavigateToDiscover: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 6.dp,
        onClick = onNavigateToDiscover
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Community", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "Join groups and discussions", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
            }
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color(0xFF7B61FF))
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.large,
        color = Color(0xFFF6F7FF)
    ) {
        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.CenterStart) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7C819C))
        }
    }
}

private fun getCategoryColors(category: String): Triple<Color, Color, Color> {
    return when (category.lowercase()) {
        "fitness" -> Triple(Color(0xFF6B63FF), Color(0xFFECEBFF), Color(0xFF352C79))
        "health" -> Triple(Color(0xFF3DD598), Color(0xFFDFF9ED), Color(0xFF0B5C3A))
        "mindfulness" -> Triple(Color(0xFF6FD6FF), Color(0xFFE3F6FF), Color(0xFF0B3D52))
        "learning" -> Triple(Color(0xFFFFC542), Color(0xFFFFF3D6), Color(0xFF4A2E00))
        "career" -> Triple(Color(0xFFFF8A65), Color(0xFFFFE5DC), Color(0xFF5F230B))
        else -> Triple(Color(0xFF7B61FF), Color(0xFFEAE5FF), Color(0xFF2E1F66))
    }
}

@Composable
private fun ProgressOverviewCard(
    completedTasks: Int,
    totalTasks: Int,
    activeStreaks: Int,
    longestStreak: Int,
    onNavigateToStats: () -> Unit,
    onNavigateToDiscover: () -> Unit
) {
    val completionFraction = if (totalTasks > 0) completedTasks / totalTasks.toFloat() else 0f
    val completionPercent = (completionFraction * 100).roundToInt()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = Shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Text(
                text = "Today's Momentum",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$completionPercent% of daily goals",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$completedTasks/$totalTasks tasks",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LinearProgressIndicator(
                    progress = { completionFraction.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = NeverZeroTheme.gradientColors.PremiumStart
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                StatMetric(
                    title = "Active Streaks",
                    value = activeStreaks.toString(),
                    icon = Icons.Rounded.AutoAwesome,
                    accent = NeverZeroTheme.streakColors.Productivity,
                    modifier = Modifier.weight(1f)
                )
                StatMetric(
                    title = "Longest Run",
                    value = "$longestStreak d",
                    icon = Icons.Rounded.LocalFireDepartment,
                    accent = NeverZeroTheme.streakColors.Wellness,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                AssistChip(
                    onClick = onNavigateToStats,
                    label = {
                        Text("View Stats")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.BarChart,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    )
                )

                AssistChip(
                    onClick = onNavigateToDiscover,
                    label = {
                        Text("Discover Ideas")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Explore,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}

@Composable
private fun StatMetric(
    title: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = accent.copy(alpha = 0.08f),
        shape = Shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Surface(
                modifier = Modifier.size(Size.iconLarge),
                shape = Shapes.medium,
                color = accent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(Size.iconMedium)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
/**
 * Welcome header with time-based greeting
 */
@Composable
private fun DashboardHeader(userName: String) {
    val greeting = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            hour < 21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
    ) {
        Text(
            text = "Never Zero",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = Shapes.full,
                tonalElevation = Elevation.level2,
                color = Color.White,
                shadowElevation = Elevation.level3
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
                        tint = NeverZeroTheme.gradientColors.PremiumStart
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakSummaryCard(
    streakDays: Int,
    onRefreshQuote: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 8.dp,
        shape = Shapes.extraLarge,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                    Text(
                        text = "Your Current Streak",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$streakDays Days",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onRefreshQuote) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = NeverZeroTheme.gradientColors.PremiumStart
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyInspirationCard(
    quote: String,
    author: String?,
    isLoading: Boolean,
    onRefreshClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = Shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Inspiration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = onRefreshClick, enabled = !isLoading) {
                    Text("Refresh")
                }
            }

            AnimatedContent(
                targetState = isLoading,
                transitionSpec = {
                    fadeIn(animationSpec = tween(Motion.durationMedium)) togetherWith
                            fadeOut(animationSpec = tween(Motion.durationMedium))
                },
                label = "dailyQuoteState"
            ) { loading ->
                if (loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = "\"$quote\"",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (!author.isNullOrBlank()) {
                            Text(
                                text = author,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Streaks section with horizontal carousel
 */
@Composable
private fun StreaksSection(
    streaks: List<Streak>,
    selectedId: String?,
    onSelectStreak: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = "Your Streaks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (streaks.isEmpty()) {
            EmptyStreaksState()
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.xxs)
            ) {
                items(streaks, key = { it.id }) { streak ->
                    ModernStreakCard(
                        streak = streak,
                        selected = streak.id == selectedId,
                        onClick = { onSelectStreak(streak.id) }
                    )
                }
            }
        }
    }
}

/**
 * Modern streak card with category colors
 */
@Composable
private fun ModernStreakCard(
    streak: Streak,
    selected: Boolean,
    onClick: () -> Unit
) {
    val categoryColors = getCategoryColors(streak.category)

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .width(200.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onClick),
        shape = Shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = categoryColors.second
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) Elevation.level3 else Elevation.level2
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Category badge
            Surface(
                color = categoryColors.first,
                shape = Shapes.small
            ) {
                Text(
                    text = streak.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxs))

            // Streak name
            Text(
                text = streak.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = categoryColors.third,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Current streak with fire icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = categoryColors.first,
                    modifier = Modifier.size(Size.iconMedium)
                )
                Text(
                    text = "${streak.currentCount}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = categoryColors.third
                )
                Text(
                    text = "days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = categoryColors.third.copy(alpha = Opacity.high)
                )
            }

            // Sparkline chart
            if (streak.history.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                ModernSparkline(
                    values = streak.history.map { it.completed.toFloat() },
                    lineColor = categoryColors.first
                )
            }

            // Longest streak
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = categoryColors.first.copy(alpha = Opacity.high),
                    modifier = Modifier.size(Size.iconSmall)
                )
                Text(
                    text = "Best: ${streak.longestCount} days",
                    style = MaterialTheme.typography.labelMedium,
                    color = categoryColors.third.copy(alpha = Opacity.high)
                )
            }
        }
    }
}

/**
 * Modern sparkline chart with smooth curves
 */
@Composable
private fun ModernSparkline(
    values: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    val points = if (values.isEmpty()) listOf(0f, 0f, 0f) else values

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .semantics {
                contentDescription = "Streak trend sparkline"
            }
    ) {
        if (points.isEmpty() || points.size < 2) return@Canvas

        val maxVal = points.maxOrNull() ?: 1f
        val minVal = points.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(1f)
        val stepX = size.width / (points.size - 1).coerceAtLeast(1)

        val path = Path().apply {
            points.forEachIndexed { index, value ->
                val x = stepX * index
                val normalized = (value - minVal) / range
                val y = size.height - (normalized * size.height * 0.8f + size.height * 0.1f)
                if (index == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
        }

        // Draw the line with rounded corners
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.cornerPathEffect(8.dp.toPx())
            )
        )
    }
}

/**
 * Empty state for streaks
 */
@Composable
private fun EmptyStreaksState() {
    OutlinedNeverZeroCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Surface(
                modifier = Modifier.size(Size.iconExtraLarge),
                shape = Shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.AddCircleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(Size.iconLarge)
                    )
                }
            }

            Text(
                text = "Start Your First Streak",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Create a habit and track your progress daily. Never let your streak hit zero!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Today's focus section with tasks
 */
@Composable
private fun TodayTasksCard(
    tasks: List<DashboardTask>,
    onToggleTask: (String) -> Unit,
    onNavigateToReading: () -> Unit,
    onNavigateToVocabulary: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 8.dp,
        shape = Shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                SecondaryButton(
                    text = "Log Reading",
                    onClick = onNavigateToReading,
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                PrimaryButton(
                    text = "Add Word",
                    onClick = onNavigateToVocabulary,
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (tasks.isEmpty()) {
                EmptyTasksState()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    tasks.forEach { task ->
                        ModernTaskCard(
                            task = task,
                            onToggleTask = onToggleTask
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modern task card with completion state
 */
@Composable
private fun ModernTaskCard(
    task: DashboardTask,
    onToggleTask: (String) -> Unit
) {
    val categoryColors = getCategoryColors(task.category)
    val context = LocalContext.current
    val haptics = remember(context) { context.hapticFeedbackManager() }

    val backgroundColor by animateColorAsState(
        targetValue = if (task.isCompleted) {
            categoryColors.second
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = Motion.durationMedium),
        label = "taskBackground"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val willComplete = !task.isCompleted
                if (willComplete) {
                    haptics.success()
                } else {
                    haptics.selection()
                }
                onToggleTask(task.id)
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = Shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) Elevation.level2 else Elevation.level1
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(Size.iconLarge)
                    .clip(Shapes.small)
                    .background(
                        if (task.isCompleted) categoryColors.first else categoryColors.first.copy(
                            alpha = Opacity.overlay
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = task.isCompleted,
                    transitionSpec = {
                        scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
                    },
                    label = "taskIcon"
                ) { completed ->
                    Icon(
                        imageVector = if (completed) {
                            Icons.Rounded.CheckCircle
                        } else {
                            Icons.Rounded.RadioButtonUnchecked
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(Size.iconMedium)
                    )
                }
            }

            // Task content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (task.isCompleted) {
                        categoryColors.third
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = categoryColors.first.copy(alpha = 0.2f),
                        shape = Shapes.extraSmall
                    ) {
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColors.third,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(
                                horizontal = Spacing.xs,
                                vertical = Spacing.xxxs
                            )
                        )
                    }
                }
            }

            // Completion badge
            if (task.isCompleted) {
                Surface(
                    color = NeverZeroTheme.semanticColors.Success.copy(alpha = 0.15f),
                    shape = Shapes.full
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = Spacing.sm,
                            vertical = Spacing.xxs
                        ),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Done,
                            contentDescription = null,
                            tint = NeverZeroTheme.semanticColors.Success,
                            modifier = Modifier.size(Size.iconSmall)
                        )
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeverZeroTheme.semanticColors.Success,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Empty state for tasks
 */
@Composable
private fun EmptyTasksState() {
    OutlinedNeverZeroCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(Size.iconLarge),
                shape = Shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Task,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(Size.iconMedium)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "No tasks yet",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Create habits to see them here",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Helper function to get category-specific colors
 */
@Composable
private fun getCategoryColors(category: String): Triple<Color, Color, Color> {
    return when (category.lowercase()) {
        "reading" -> Triple(
            NeverZeroTheme.streakColors.Reading,
            NeverZeroTheme.streakColors.ReadingContainer,
            NeverZeroTheme.streakColors.OnReadingContainer
        )
        "vocabulary" -> Triple(
            NeverZeroTheme.streakColors.Vocabulary,
            NeverZeroTheme.streakColors.VocabularyContainer,
            NeverZeroTheme.streakColors.OnVocabularyContainer
        )
        "wellness" -> Triple(
            NeverZeroTheme.streakColors.Wellness,
            NeverZeroTheme.streakColors.WellnessContainer,
            NeverZeroTheme.streakColors.OnWellnessContainer
        )
        "productivity" -> Triple(
            NeverZeroTheme.streakColors.Productivity,
            NeverZeroTheme.streakColors.ProductivityContainer,
            NeverZeroTheme.streakColors.OnProductivityContainer
        )
        "learning" -> Triple(
            NeverZeroTheme.streakColors.Learning,
            NeverZeroTheme.streakColors.LearningContainer,
            NeverZeroTheme.streakColors.OnLearningContainer
        )
        "exercise" -> Triple(
            NeverZeroTheme.streakColors.Exercise,
            NeverZeroTheme.streakColors.ExerciseContainer,
            NeverZeroTheme.streakColors.OnExerciseContainer
        )
        "meditation" -> Triple(
            NeverZeroTheme.streakColors.Meditation,
            NeverZeroTheme.streakColors.MeditationContainer,
            NeverZeroTheme.streakColors.OnMeditationContainer
        )
        "creative" -> Triple(
            NeverZeroTheme.streakColors.Creative,
            NeverZeroTheme.streakColors.CreativeContainer,
            NeverZeroTheme.streakColors.OnCreativeContainer
        )
        else -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
