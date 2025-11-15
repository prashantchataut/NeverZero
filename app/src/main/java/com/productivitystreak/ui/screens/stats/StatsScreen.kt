package com.productivitystreak.ui.screens.stats

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivitystreak.R
import com.productivitystreak.ui.state.stats.LeaderboardEntry
import com.productivitystreak.ui.state.stats.StatsState
import com.productivitystreak.ui.state.stats.TrendPoint

@Composable
fun StatsScreen(state: StatsState) {
    val gradient = Brush.verticalGradient(listOf(Color(0xFFF0F4FF), Color(0xFFEAE5FF)))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StatsHeader()
        StatsHeroCard()
        MetricsRow(
            current = state.currentLongestStreak,
            longestName = state.currentLongestStreakName,
            average = state.averageDailyProgressPercent
        )
        TrendSection(points = state.averageDailyTrend?.points.orEmpty())
        LeaderboardSection(entries = state.leaderboard)
    }
}

@Composable
private fun StatsHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Never Zero", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6C70A3))
        Text(text = "Your Stats", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = "Track your progress and stay motivated.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6F748C))
    }
}

@Composable
private fun StatsHeroCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        tonalElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(shape = CircleShape, color = Color(0xFFE6E9FF)) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_stats),
                    contentDescription = null,
                    tint = Color(0xFF6A63FF),
                    modifier = Modifier.padding(20.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "You're on a roll!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "Keep building your momentum.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7C8095))
            }
        }
    }
}

@Composable
private fun MetricsRow(current: Int, longestName: String, average: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MetricCard(title = "Current", value = "$current days")
        MetricCard(title = "Longest", value = longestName.ifBlank { "Keep going" })
        MetricCard(title = "Success", value = "$average%")
    }
}

@Composable
private fun MetricCard(title: String, value: String) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = Color(0xFF7C819C))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TrendSection(points: List<TrendPoint>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Streak Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "Rolling performance", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C8095))
                }
                IconButton(onClick = { }) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right), contentDescription = null, tint = Color(0xFF6A63FF))
                }
            }
            TrendGraph(points = points)
        }
    }
}

@Composable
private fun TrendGraph(points: List<TrendPoint>) {
    val displayPoints = if (points.isEmpty()) listOf(TrendPoint("", 10), TrendPoint("", 30), TrendPoint("", 60)) else points
    val path = remember(displayPoints) { android.graphics.Path() }
    val hasAnimated = remember { mutableStateOf(false) }
    LaunchedEffect(displayPoints) {
        hasAnimated.value = false
        kotlinx.coroutines.delay(100)
        hasAnimated.value = true
    }
    val progress by animateFloatAsState(
        targetValue = if (hasAnimated.value) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "trend-progress"
    )
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        if (displayPoints.size < 2) return@Canvas
        val max = displayPoints.maxOf { it.percent }
        val min = displayPoints.minOf { it.percent }
        val range = (max - min).coerceAtLeast(1)
        val stepX = size.width / (displayPoints.size - 1)
        val animatedCount = (displayPoints.size * progress).coerceAtLeast(2f)
        val drawPath = Path()
        displayPoints.forEachIndexed { index, trend ->
            if (index > animatedCount) return@forEachIndexed
            val yRatio = (trend.percent - min) / range.toFloat()
            val x = stepX * index
            val y = size.height - (yRatio * size.height)
            if (index == 0) drawPath.moveTo(x, y) else drawPath.lineTo(x, y)
        }
        drawPath(
            path = drawPath,
            color = Color(0xFF6A63FF),
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun LeaderboardSection(entries: List<LeaderboardEntry>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Leaderboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "View All", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6A63FF))
            }
            if (entries.isEmpty()) {
                Text(text = "No entries yet", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7C8095))
            } else {
                entries.take(3).forEach { entry ->
                    LeaderboardRow(entry)
                    if (entry != entries.take(3).last()) Divider(color = Color(0xFFF0F0FF))
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = CircleShape, color = Color(0xFFEEF0FF)) {
                Text(
                    text = entry.position.toString(),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A63FF)
                )
            }
            Column {
                Text(text = entry.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = "${entry.streakDays} day streak", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C8095))
            }
        }
        Text(text = "🔥", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun TrendCard(trend: AverageDailyTrend) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Rolling ${trend.windowSize}-Day Trend", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Tracks average completion across all streaks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text(text = "${trend.points.size} days") }
                )
            }
            TrendChart(points = trend.points)
        }
    }
}

@Composable
private fun TrendChart(points: List<TrendPoint>, modifier: Modifier = Modifier) {
    if (points.isEmpty()) {
        Text(text = "Not enough data yet", style = MaterialTheme.typography.bodyMedium)
        return
    }
    val animatePath = remember { mutableStateOf(false) }
    LaunchedEffect(points) {
        animatePath.value = false
        // Give composition time to update before animating again
        delay(50)
        animatePath.value = true
    }
    val progress by animateFloatAsState(
        targetValue = if (animatePath.value) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "trend-progress"
    )
    val fillGradientColors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
        Color.Transparent
    )
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .graphicsLayer { alpha = 0.9f }
    ) {
        if (points.size < 2) return@Canvas
        val maxPercent = max(points.maxOf { it.percent }, 100)
        val minPercent = min(points.minOf { it.percent }, 0)
        val range = (maxPercent - minPercent).coerceAtLeast(1)
        val stepX = size.width / (points.size - 1)
        val visiblePoints = (points.size * progress).coerceAtLeast(1f)
        val path = Path()
        val areaPath = Path()
        points.forEachIndexed { index, point ->
            if (index > visiblePoints) return@forEachIndexed
            val clampedIndex = index.coerceAtMost(points.lastIndex)
            val percentProgress = points[clampedIndex].percent
            val normalized = (percentProgress - minPercent) / range.toFloat()
            val x = stepX * clampedIndex
            val y = size.height - normalized * size.height
            if (clampedIndex == 0) {
                path.moveTo(x, y)
                areaPath.moveTo(x, size.height)
                areaPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                areaPath.lineTo(x, y)
            }
            if (clampedIndex == visiblePoints.toInt()) {
                areaPath.lineTo(x, size.height)
                areaPath.close()
            }
        }
        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(colors = fillGradientColors)
        )
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun ConsistencySection(scores: List<ConsistencyScore>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Consistency Insights", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            scores.forEach { score ->
                ConsistencyCard(score)
            }
        }
    }
}

@Composable
private fun ConsistencyCard(score: ConsistencyScore) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConsistencyGauge(score = score.score, level = score.level)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = score.streakName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${score.completionRate}% completion • σ² ${"%.2f".format(score.variance)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text(text = score.level.name) },
                colors = AssistChipDefaults.assistChipColors(
                    disabledContainerColor = when (score.level) {
                        ConsistencyLevel.High -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ConsistencyLevel.Medium -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ConsistencyLevel.NeedsAttention -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    }
                )
            )
        }
    }
}

@Composable
private fun ConsistencyGauge(score: Int, level: ConsistencyLevel) {
    val normalized = score.coerceIn(0, 100) / 100f
    val animatedValue by animateFloatAsState(
        targetValue = normalized,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "gauge"
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val accent = when (level) {
        ConsistencyLevel.High -> MaterialTheme.colorScheme.primary
        ConsistencyLevel.Medium -> MaterialTheme.colorScheme.secondary
        ConsistencyLevel.NeedsAttention -> MaterialTheme.colorScheme.error
    }
    Canvas(modifier = Modifier.size(72.dp)) {
        val strokeWidth = 10.dp.toPx()
        drawArc(
            color = trackColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            brush = Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.6f))),
            startAngle = 180f,
            sweepAngle = 180f * animatedValue,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }
    Text(
        text = "$score",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp),
        textAlign = TextAlign.Center
    )
}
