package com.productivitystreak.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                    imageVector = Icons.Rounded.AutoGraph,
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
    val latestPercent = points.lastOrNull()?.percent ?: 0
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
                    Text(text = "Latest performance", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C8095))
                }
                TextButton(onClick = { }) {
                    Text(text = "View details", color = Color(0xFF6A63FF))
                }
            }
            LinearProgressIndicator(
                progress = { latestPercent.coerceIn(0, 100) / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF6A63FF)
            )
            Text(
                text = "Latest streak completion: $latestPercent%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7C8095)
            )
            if (points.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    points.takeLast(4).forEach { point ->
                        Text(
                            text = "${point.date.ifBlank { "Recent" }} • ${point.percent}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF585C74)
                        )
                    }
                }
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
