package com.productivitystreak.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.stats.HabitBreakdown
import com.productivitystreak.ui.state.stats.LeaderboardEntry
import com.productivitystreak.ui.state.stats.StatsState

@Composable
fun StatsScreen(state: StatsState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            SummaryCard(
                title = "Current Longest Streak",
                primaryValue = "${state.currentLongestStreak} days",
                secondary = state.currentLongestStreakName.ifBlank { "Keep climbing" },
                accent = MaterialTheme.colorScheme.primary
            )
        }
        item {
            SummaryCard(
                title = "Average Daily Progress",
                primaryValue = "${state.averageDailyProgressPercent}%",
                secondary = "Consistency is built one check-in at a time.",
                accent = MaterialTheme.colorScheme.secondary
            )
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
    accent: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = accent.copy(alpha = 0.25f)
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
