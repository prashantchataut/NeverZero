package com.productivitystreak.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.stats.LeaderboardEntry
import com.productivitystreak.ui.state.stats.StatsState
import com.productivitystreak.ui.state.stats.TrendPoint
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun StatsScreen(state: StatsState) {
    val gradient = Brush.verticalGradient(listOf(Color(0xFFF0F4FF), Color(0xFFEAE5FF)))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StatsHeader()
        StatsHeroCard(current = state.currentLongestStreak)
        MetricsRow(
            current = state.currentLongestStreak,
            longestName = state.currentLongestStreakName,
            average = state.averageDailyProgressPercent
        )
        TrendSection(points = state.averageDailyTrend?.points.orEmpty())
        LeaderboardSection(entries = state.leaderboard)
        Spacer(modifier = Modifier.height(8.dp))
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
private fun StatsHeroCard(current: Int) {
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
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "You're on a roll!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "${max(current, 0)} day streak and counting.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7C8095))
                }
                Surface(shape = CircleShape, color = Color(0xFFE6E9FF)) {
                    Icon(
                        imageVector = Icons.Rounded.AutoGraph,
                        contentDescription = null,
                        tint = Color(0xFF6A63FF),
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            MomentumHighlights(
                highlights = listOf(
                    HighlightCard(title = "Momentum", detail = "Daily progress is on track", action = "Keep logging")
                )
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun MetricsRow(current: Int, longestName: String, average: Int) {
    Spacer(modifier = Modifier.height(16.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 3
    ) {
        MetricCard(
            title = "Current streak",
            value = if (current > 0) "$current days" else "No streak yet",
            helper = if (current > 0) "Logged today" else "Log a habit to start"
        )
        MetricCard(
            title = "Focus habit",
            value = longestName.ifBlank { "Add a priority" },
            helper = if (longestName.isBlank()) "Pick a habit to spotlight" else "Your longest winning habit"
        )
        MetricCard(
            title = "Success rate",
            value = "$average%",
            helper = if (average >= 80) "Great consistency" else "Aim for 80%+"
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowScope.MetricCard(title: String, value: String, helper: String) {
    Surface(
        modifier = Modifier
            .weight(1f, fill = true)
            .fillMaxWidth()
            .width(200.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = Color(0xFF7C819C))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )
            Text(text = helper, style = MaterialTheme.typography.bodySmall, color = Color(0xFF9A9EB8))
        }
    }
}

@Composable
private fun MomentumHighlights(highlights: List<HighlightCard>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        highlights.distinctBy { it.title }.forEach { highlight ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF7F5FF)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = highlight.title, style = MaterialTheme.typography.labelLarge, color = Color(0xFF7C819C))
                        Text(text = highlight.detail, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
                    }
                    Text(
                        text = highlight.action,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6A63FF)
                    )
                }
            }
        }
    }
}

private data class HighlightCard(
    val title: String,
    val detail: String,
    val action: String
)

@Composable
private fun TrendSection(points: List<TrendPoint>) {
    val latestPercent = points.lastOrNull()?.percent ?: 0
    val trendDelta = if (points.size >= 2) latestPercent - points.first().percent else 0
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
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
                    Text(text = "Streak Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = if (points.size >= 2) {
                            val direction = if (trendDelta >= 0) "up" else "down"
                            "Consistency $direction ${abs(trendDelta)}% this week"
                        } else "Latest performance",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7C8095)
                    )
                }
                TextButton(onClick = { }) {
                    Text(text = "View habit insights", color = Color(0xFF6A63FF))
                }
            }
            TrendChart(points = points)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Latest streak completion: $latestPercent%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF585C74)
                )
                Text(
                    text = if (trendDelta >= 0) "You're improving. Keep the streak alive." else "Momentum dipped—log today to recover.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7C8095)
                )
            }
        }
    }
}

@Composable
private fun TrendChart(points: List<TrendPoint>) {
    if (points.size < 2) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF5F2FF)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "Not enough data yet", color = Color(0xFF7C8095))
            }
        }
        return
    }

    val minValue = points.minOf { it.percent }.coerceAtMost(0)
    val maxValue = points.maxOf { it.percent }.coerceAtLeast(100)
    val span = max(1, maxValue - minValue)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF5F2FF)
    ) {
        Canvas(modifier = Modifier.padding(12.dp)) {
            val widthStep = size.width / (points.size - 1)
            val path = Path()
            points.forEachIndexed { index, point ->
                val normalized = (point.percent - minValue) / span.toFloat()
                val y = size.height - (normalized * size.height)
                val x = widthStep * index
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            // baseline
            val baselineY = size.height - ((50 - minValue) / span.toFloat()).coerceIn(0f, 1f) * size.height
            drawLine(
                color = Color(0xFFD4CEFF),
                start = Offset(0f, baselineY),
                end = Offset(size.width, baselineY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f))
            )

            drawPath(
                path = path,
                color = Color(0xFF6A63FF),
                style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            points.forEachIndexed { index, point ->
                val normalized = (point.percent - minValue) / span.toFloat()
                val y = size.height - (normalized * size.height)
                val x = widthStep * index
                drawCircle(
                    color = Color.White,
                    radius = 10f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color(0xFF6A63FF),
                    radius = 6f,
                    center = Offset(x, y)
                )
            }
        }
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
