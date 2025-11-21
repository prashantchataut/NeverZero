package com.productivitystreak.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.icons.AppIcons
import com.productivitystreak.ui.state.stats.ConsistencyLevel
import com.productivitystreak.ui.state.stats.ConsistencyScore
import kotlin.math.roundToInt

// Add this composable after the StatsScreen function

@Composable
fun ConsistencyScoreCard(score: ConsistencyScore) {
    // Map underlying 3-level score into 4 narrative tiers
    val tier = when {
        score.score >= 90 -> "UNSTOPPABLE"
        score.level == ConsistencyLevel.High -> "CONSISTENT"
        score.level == ConsistencyLevel.Medium -> "BUILDING"
        else -> "BEGINNER"
    }

    val levelColor = when (tier) {
        "BEGINNER" -> Color(0xFF9E9E9E)
        "BUILDING" -> Color(0xFF2196F3)
        "CONSISTENT" -> Color(0xFF4CAF50)
        "UNSTOPPABLE" -> Color(0xFFFFD700)
        else -> MaterialTheme.colorScheme.primary
    }

    val levelIcon = when (tier) {
        "BEGINNER" -> AppIcons.Seedling
        "BUILDING" -> AppIcons.FireStreak
        "CONSISTENT" -> AppIcons.Lightning
        "UNSTOPPABLE" -> AppIcons.Crown
        else -> AppIcons.Default
    }

    val levelText = when (tier) {
        "BEGINNER" -> "Beginner"
        "BUILDING" -> "Building Momentum"
        "CONSISTENT" -> "Consistent Performer"
        "UNSTOPPABLE" -> "Unstoppable Force"
        else -> "Consistency"
    }

    // Approximate progress toward the next tier based on score (0–100)
    val percentToNextLevel = when (tier) {
        "BEGINNER" -> (score.score * 100f / 50f).coerceIn(0f, 100f).roundToInt()
        "BUILDING" -> ((score.score - 50) * 100f / 30f).coerceIn(0f, 100f).roundToInt()
        "CONSISTENT" -> ((score.score - 80) * 100f / 10f).coerceIn(0f, 100f).roundToInt()
        "UNSTOPPABLE" -> 100
        else -> 0
    }

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
                        text = "Consistency Level",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your momentum score",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = levelIcon,
                        contentDescription = null,
                        tint = levelColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${score.score}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = levelColor
                    )
                }
            }

            // Level progress bar
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = levelText,
                        style = MaterialTheme.typography.labelLarge,
                        color = levelColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${percentToNextLevel}% to next",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(999.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentToNextLevel / 100f)
                            .height(12.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(levelColor, levelColor.copy(alpha = 0.7f))
                                ),
                                shape = RoundedCornerShape(999.dp)
                            )
                    )
                }
            }

            // Motivational message
            Text(
                text = when (score.level) {
                    ConsistencyLevel.NeedsAttention -> "Every journey starts with a single step. Keep showing up."
                    ConsistencyLevel.Medium -> "You're building real momentum. Don't stop now."
                    ConsistencyLevel.High -> if (score.score >= 90) {
                        "You're unstoppable. This is who you are now."
                    } else {
                        "You've proven you're the person who shows up. Keep going."
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
