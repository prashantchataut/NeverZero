package com.productivitystreak.ui.screens.stats.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HighlightOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.icons.AppIcons
import com.productivitystreak.ui.state.stats.AverageDailyTrend
import com.productivitystreak.ui.state.stats.ConsistencyLevel
import com.productivitystreak.ui.state.stats.ConsistencyScore
import com.productivitystreak.ui.theme.Spacing

@Composable
fun WeeklySummaryContent(completionRate: Int, streak: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        com.productivitystreak.ui.components.AnimatedProgressRing(
            progress = completionRate / 100f,
            size = 180.dp,
            ringColor = Color(0xFF6C63FF)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "$completionRate%",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Completion Rate",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (streak > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.FireStreak,
                        contentDescription = null,
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "$streak day streak",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFF5722),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ConsistencyStoryContent(score: ConsistencyScore) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Text(
            text = score.streakName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        com.productivitystreak.ui.components.AnimatedProgressRing(
            progress = score.score / 100f,
            size = 160.dp,
            ringColor = when (score.level) {
                ConsistencyLevel.High -> Color(0xFF4CAF50)
                ConsistencyLevel.Medium -> Color(0xFFFFA726)
                ConsistencyLevel.NeedsAttention -> Color(0xFFEF5350)
            }
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "${score.score}%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Consistency Score",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TrendStoryContent(trend: AverageDailyTrend) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Text(
            text = "${trend.windowSize}-Day Trend",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        com.productivitystreak.ui.components.AnimatedLineChart(
            dataPoints = trend.points.map { it.percent.toFloat() },
            lineColor = Color(0xFF4ECDC4),
            fillGradient = listOf(
                Color(0xFF4ECDC4).copy(alpha = 0.3f),
                Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        val avgProgress = trend.points.map { it.percent }.average().toInt()
        Text(
            text = "Average: $avgProgress%",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
