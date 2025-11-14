package com.productivitystreak.ui.screens.stats

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.state.stats.AverageDailyTrend
import com.productivitystreak.ui.state.stats.CalendarHeatMap
import com.productivitystreak.ui.state.stats.ConsistencyLevel
import com.productivitystreak.ui.state.stats.ConsistencyScore
import com.productivitystreak.ui.state.stats.HabitBreakdown
import com.productivitystreak.ui.state.stats.HeatMapDay
import com.productivitystreak.ui.state.stats.LeaderboardEntry
import com.productivitystreak.ui.state.stats.StatsState
import com.productivitystreak.ui.state.stats.TrendPoint
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
fun StatsScreen(state: StatsState) {
    val listState = rememberLazyListState()
    val cardSpacing = 18.dp
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(cardSpacing)
    ) {
        item {
            SummaryCard(
                title = "Current Longest Streak",
                primaryValue = "${state.currentLongestStreak} days",
                secondary = state.currentLongestStreakName.ifBlank { "Keep climbing" },
                accent = MaterialTheme.colorScheme.primary,
                index = 0
            )
        }
        item {
            SummaryCard(
                title = "Average Daily Progress",
                primaryValue = "${state.averageDailyProgressPercent}%",
                secondary = "Consistency is built one check-in at a time.",
                accent = MaterialTheme.colorScheme.secondary,
                index = 1
            )
        }
        state.averageDailyTrend?.let { trend ->
            item {
                TrendCard(trend)
            }
        }
        state.calendarHeatMap?.let { heatMap ->
            item {
                CalendarHeatMapCard(heatMap)
            }
        }
        if (state.streakConsistency.isNotEmpty()) {
            item {
                ConsistencySection(scores = state.streakConsistency)
            }
        }
        if (state.habitBreakdown.isNotEmpty()) {
            item {
                Text(
                    text = "Habit Breakdown",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(state.habitBreakdown) { habit ->
                HabitBreakdownRow(habit)
            }
        }
        if (state.leaderboard.isNotEmpty()) {
            item {
                Text(
                    text = "Community Leaderboard",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(state.leaderboard) { entry ->
                LeaderboardRow(entry)
            }
        }
    }
}

@Composable
private fun CalendarHeatMapCard(heatMap: CalendarHeatMap) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Momentum Map",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${heatMap.completedDays}/${heatMap.totalDays} active days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text("Last ${heatMap.totalDays} days") },
                    colors = AssistChipDefaults.assistChipColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                heatMap.weeks.forEach { week ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        week.days.forEach { day ->
                            HeatMapCell(day)
                        }
                    }
                }
            }

            HeatMapLegend()
        }
    }
}

@Composable
private fun HeatMapCell(day: HeatMapDay) {
    val baseColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant
    val intensityColor = lerp(inactiveColor, baseColor, day.intensity)
    val animatedScale by animateFloatAsState(
        targetValue = if (day.isToday) 1.15f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "heat-map-scale"
    )

    Box(
        modifier = Modifier
            .size(24.dp)
            .semantics {
                contentDescription = "${day.date}: ${(day.intensity * 100).roundToInt()}%"
            }
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .background(intensityColor, RoundedCornerShape(6.dp))
            .border(
                width = if (day.isToday) 2.dp else 0.dp,
                color = if (day.isToday) baseColor else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
    )
}

@Composable
private fun HeatMapLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Less",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(0f, 0.33f, 0.66f, 1f).forEach { value ->
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 8.dp)
                        .background(
                            color = lerp(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.primary,
                                value
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
        Text(
            text = "More",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HabitBreakdownRow(habit: HabitBreakdown) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = habit.name, style = MaterialTheme.typography.titleSmall)
            GradientBar(
                value = habit.completionPercent / 100f,
                accent = Color(android.graphics.Color.parseColor(habit.accentHex))
            )
            Text(
                text = "${habit.completionPercent}% complete",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${entry.position}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entry.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${entry.streakDays} day streak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "🔥",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    primaryValue: String,
    secondary: String,
    accent: Color,
    index: Int
) {
    val hasAnimated = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100L * index)
        hasAnimated.value = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (hasAnimated.value) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "summary-alpha"
    )
    val translation by animateFloatAsState(
        targetValue = if (hasAnimated.value) 0f else 30f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "summary-translation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = translation
            },
        colors = CardDefaults.cardColors(
            containerColor = accent.copy(alpha = 0.22f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = primaryValue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = secondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun GradientBar(value: Float, accent: Color) {
    val safeValue = value.coerceIn(0f, 1f)
    val gradient = Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.4f)))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(60.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(safeValue)
                .height(12.dp)
                .background(gradient, RoundedCornerShape(60.dp))
        ) {}
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
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    Color.Transparent
                )
            )
        )
        drawPath(
            path = path,
            color = MaterialTheme.colorScheme.primary,
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
