package com.productivitystreak.ui.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask

@Composable
fun DashboardScreen(
    state: AppUiState,
    onRefreshQuote: () -> Unit,
    onSelectStreak: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNavigateToReading: () -> Unit,
    onNavigateToVocabulary: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        GreetingCard(
            userName = state.userName,
            quote = state.quote?.text ?: "Steady beats sudden. Keep your streak warm today!",
            author = state.quote?.author,
            isLoading = state.isQuoteLoading,
            onRefreshClick = onRefreshQuote
        )

        StreakCarousel(
            streaks = state.streaks,
            selectedId = state.selectedStreakId,
            onSelectStreak = onSelectStreak
        )

        TodayTasksSection(
            tasks = state.todayTasks,
            onToggleTask = onToggleTask,
            onNavigateToReading = onNavigateToReading,
            onNavigateToVocabulary = onNavigateToVocabulary
        )
    }
}

@Composable
private fun GreetingCard(
    userName: String,
    quote: String,
    author: String?,
    isLoading: Boolean,
    onRefreshClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "glow")
    val glow by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow-anim"
    )

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = glow),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Good Morning, $userName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Never hit zero today.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedIconButton(onClick = onRefreshClick, enabled = !isLoading) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Refresh quote")
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            AnimatedContent(targetState = isLoading, label = "quote-state") { loading ->
                if (loading) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "\"$quote\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        if (!author.isNullOrBlank()) {
                            Text(
                                text = author,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakCarousel(
    streaks: List<com.productivitystreak.data.model.Streak>,
    selectedId: String?,
    onSelectStreak: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Your Streaks", style = MaterialTheme.typography.titleMedium)
        if (streaks.isEmpty()) {
            Text(
                text = "Tap the + button to add your first habit and start a streak!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(streaks, key = { it.id }) { streak ->
                    StreakCard(
                        streak = streak,
                        selected = streak.id == selectedId,
                        onClick = { onSelectStreak(streak.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakCard(
    streak: com.productivitystreak.data.model.Streak,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = hexToColor(
        when (streak.category.lowercase()) {
            "reading" -> "#6C63FF"
            "vocabulary" -> "#FF6584"
            "wellness" -> "#4CD964"
            else -> "#F7B500"
        }
    )
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) accent.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = streak.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Current streak: ${streak.currentCount} days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Sparkline(values = streak.history, lineColor = accent)
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = accent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Longest: ${streak.longestCount} days",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun Sparkline(values: List<Int>, lineColor: Color) {
    val points = if (values.isEmpty()) listOf(0f, 0f, 0f) else values.map { it.toFloat() }
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)) {
        if (points.isEmpty()) return@Canvas
        val maxVal = points.maxOrNull() ?: 1f
        val minVal = points.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(1f)
        val stepX = size.width / (points.size - 1).coerceAtLeast(1)
        val path = androidx.compose.ui.graphics.Path().apply {
            points.forEachIndexed { index, value ->
                val x = stepX * index
                val normalized = (value - minVal) / range
                val y = size.height - normalized * size.height
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = lineColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 6f,
                pathEffect = PathEffect.cornerPathEffect(16f)
            )
        )
    }
}

@Composable
private fun TodayTasksSection(
    tasks: List<DashboardTask>,
    onToggleTask: (String) -> Unit,
    onNavigateToReading: () -> Unit,
    onNavigateToVocabulary: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Today’s Focus", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = onNavigateToReading) {
                Text("Log Reading")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onNavigateToVocabulary) {
                Text("Add Word")
            }
        }

        if (tasks.isEmpty()) {
            Text(
                text = "Create habits to see them here. Your streaks will appear as quick actions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                tasks.forEach { task ->
                    TaskRow(task = task, onToggleTask = onToggleTask)
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: DashboardTask, onToggleTask: (String) -> Unit) {
    val accent = hexToColor(task.accentHex)
    val transition = rememberInfiniteTransition(label = "task-glow")
    val alpha by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "task-alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleTask(task.id) },
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) accent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis
                )
            }
            AnimatedVisibility(visible = task.isCompleted) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = accent
                )
            }
        }
    }
}

@Composable
private fun hexToColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: IllegalArgumentException) {
        MaterialTheme.colorScheme.primary
    }
}
