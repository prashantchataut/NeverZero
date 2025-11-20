package com.productivitystreak.ui.screens.stats

// Stats UI removed during architectural sanitization.

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.icons.AppIcons
import com.productivitystreak.ui.state.stats.AverageDailyTrend
import com.productivitystreak.ui.state.stats.CalendarHeatMap
import com.productivitystreak.ui.state.stats.ConsistencyLevel
import com.productivitystreak.ui.state.stats.ConsistencyScore
import com.productivitystreak.ui.state.stats.HabitBreakdown
import com.productivitystreak.ui.state.stats.LeaderboardEntry
import com.productivitystreak.ui.state.stats.StatsState
import com.productivitystreak.ui.theme.NeverZeroTheme
import kotlin.math.absoluteValue

@Composable
fun StatsScreen(
    statsState: StatsState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Your streak story",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        SummaryRow(statsState = statsState)

        statsState.averageDailyTrend?.let {
            StreakTrendCard(trend = it)
        }

        if (statsState.habitBreakdown.isNotEmpty()) {
            BreakdownRow(breakdown = statsState.habitBreakdown)
        }

        if (statsState.leaderboard.isNotEmpty()) {
            LeaderboardSection(entries = statsState.leaderboard)
        }

        statsState.calendarHeatMap?.let {
            HeatMapCard(heatMap = it)
        }
    }
}

@Composable
private fun LeaderboardSection(entries: List<LeaderboardEntry>) {
    if (entries.isEmpty()) return

    var selectedPosition by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Consistency leaderboard",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Top streaks over the last 30 days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                entries
                    .sortedBy { it.position }
                    .take(5)
                    .forEach { entry ->
                        val isSelected = selectedPosition == entry.position
                        LeaderboardRow(
                            entry = entry,
                            highlight = entry.position == 1,
                            selected = isSelected,
                            onClick = { selectedPosition = entry.position }
                        )
                    }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    entry: LeaderboardEntry,
    highlight: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        Color.Transparent
    }

    val avatarColors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.surfaceVariant
    )
    val avatarColor = remember(entry.name) {
        val index = entry.name.hashCode().absoluteValue % avatarColors.size
        avatarColors[index]
    }

    val initial = remember(entry.name) {
        entry.name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: ""
    }

    val badgeBackground = when (entry.position) {
        1 -> MaterialTheme.colorScheme.primaryContainer
        2 -> MaterialTheme.colorScheme.secondaryContainer
        3 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val badgeTextColor = when (entry.position) {
        1, 2, 3 -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = avatarColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = entry.name,
                    style = if (highlight) {
                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    } else {
                        MaterialTheme.typography.bodyMedium
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${entry.streakDays} days streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (entry.position == 1) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Celebration,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Box(
                modifier = Modifier
                    .background(badgeBackground, shape = RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "#${entry.position}",
                    style = MaterialTheme.typography.labelMedium,
                    color = badgeTextColor
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(statsState: StatsState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticCard(
            title = "Current streak",
            value = "${statsState.currentLongestStreak} days",
            subtitle = statsState.currentLongestStreakName.ifBlank { "Stay on the path" },
            modifier = Modifier.weight(1f)
        )
        StatisticCard(
            title = "Completion rate",
            value = "${statsState.averageDailyProgressPercent}%",
            subtitle = "Daily average",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StreakTrendCard(trend: AverageDailyTrend) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Consistency over time",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Rolling ${trend.windowSize}-day average completion",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            TrendChart(trend = trend)
        }
    }
}

@Composable
private fun TrendChart(trend: AverageDailyTrend) {
    val points = trend.points
    if (points.isEmpty()) return

    val maxPercent = 100f
    val minPercent = 0f
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    val oceanStart = NeverZeroTheme.gradientColors.OceanStart
    val oceanEnd = NeverZeroTheme.gradientColors.OceanEnd
    val surfaceColor = MaterialTheme.colorScheme.surface

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        val chartWidth = size.width
        val chartHeight = size.height
        val horizontalPadding = 16.dp.toPx()
        val verticalPadding = 16.dp.toPx()

        val usableWidth = chartWidth - 2 * horizontalPadding
        val usableHeight = chartHeight - 2 * verticalPadding

        // Baseline grid
        // Baseline grid
        repeat(4) { index ->
            val y = verticalPadding + (usableHeight / 3f) * index
            drawLine(
                color = gridColor,
                start = Offset(horizontalPadding, y),
                end = Offset(chartWidth - horizontalPadding, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val stepX = if (points.size == 1) 0f else usableWidth / (points.size - 1).coerceAtLeast(1)

        val path = Path()
        val fillPath = Path()

        points.forEachIndexed { index, point ->
            val percent = point.percent.toFloat().coerceIn(minPercent, maxPercent)
            val normalized = (percent - minPercent) / (maxPercent - minPercent)
            val x = horizontalPadding + stepX * index
            val y = verticalPadding + usableHeight * (1f - normalized)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, chartHeight - verticalPadding)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        val lastX = horizontalPadding + stepX * (points.size - 1)
        fillPath.lineTo(lastX, chartHeight - verticalPadding)
        fillPath.close()

        val gradient = Brush.verticalGradient(
            colors = listOf(
                oceanStart.copy(alpha = 0.35f),
                Color.Transparent
            ),
            startY = verticalPadding,
            endY = chartHeight - verticalPadding
        )

        drawPath(path = fillPath, brush = gradient)

        drawPath(
            path = path,
            color = oceanEnd,
            style = Stroke(width = 3.dp.toPx())
        )

        points.forEachIndexed { index, point ->
            val percent = point.percent.toFloat().coerceIn(minPercent, maxPercent)
            val normalized = (percent - minPercent) / (maxPercent - minPercent)
            val x = horizontalPadding + stepX * index
            val y = verticalPadding + usableHeight * (1f - normalized)
            drawCircle(
                color = surfaceColor,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = oceanEnd,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun BreakdownRow(breakdown: List<HabitBreakdown>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Habits at a glance",
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(breakdown) { item ->
                HabitBreakdownCard(item)
            }
        }
    }
}

@Composable
private fun HabitBreakdownCard(item: HabitBreakdown) {
    val primary = MaterialTheme.colorScheme.primary
    val accent = remember(item.accentHex) {
        runCatching { Color(android.graphics.Color.parseColor(item.accentHex)) }.getOrElse {
            primary
        }
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .size(width = 180.dp, height = 120.dp)
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 6.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(accent, accent.copy(alpha = 0.3f))
                            ),
                            shape = RoundedCornerShape(999.dp)
                        )
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "${item.completionPercent}%",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun HeatMapCard(heatMap: CalendarHeatMap) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Streak calendar",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${heatMap.completedDays} of ${heatMap.totalDays} days had activity.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            HeatMapGrid(heatMap)
        }
    }
}

@Composable
private fun HeatMapGrid(heatMap: CalendarHeatMap) {
    val weeks = heatMap.weeks
    if (weeks.isEmpty()) return

    val sunriseStart = NeverZeroTheme.gradientColors.SunriseStart
    val sunriseEnd = NeverZeroTheme.gradientColors.SunriseEnd
    val onSurface = MaterialTheme.colorScheme.onSurface

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val columns = weeks.size
        val rows = weeks.maxOf { it.days.size }.coerceAtLeast(1)

        val cellWidth = size.width / columns
        val cellHeight = size.height / rows

        weeks.forEachIndexed { xIndex, week ->
            week.days.forEachIndexed { yIndex, day ->
                val intensity = day.intensity.coerceIn(0f, 1f)

                val brush = if (intensity <= 0f) {
                    SolidColor(onSurface.copy(alpha = 0.05f))
                } else {
                    Brush.verticalGradient(
                        listOf(
                            sunriseStart.copy(alpha = 0.3f + 0.5f * intensity),
                            sunriseEnd.copy(alpha = 0.3f + 0.5f * intensity)
                        )
                    )
                }
                val left = xIndex * cellWidth + 4f
                val top = yIndex * cellHeight + 4f
                val cellSize = Size(cellWidth - 8f, cellHeight - 8f)

                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(left, top),
                    size = cellSize,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }
    }
}

