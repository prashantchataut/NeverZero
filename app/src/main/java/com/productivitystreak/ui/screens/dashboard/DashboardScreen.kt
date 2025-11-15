package com.productivitystreak.ui.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.theme.Shapes
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
        QuickActionCard(
            title = "Reading Log",
            subtitle = "Track chapters",
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            onClick = onNavigateToReading,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "New Word",
            subtitle = "Expand vocab",
            icon = Icons.Rounded.Add,
            onClick = onNavigateToVocabulary,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Stats",
            subtitle = "View insights",
            icon = Icons.Rounded.Bolt,
            onClick = onNavigateToStats,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
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
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6B63FF),
            disabledContainerColor = Color(0xFFCBCFEF),
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        )
    ) {
        Box(modifier = Modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
