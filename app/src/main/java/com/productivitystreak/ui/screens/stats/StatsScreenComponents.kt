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

// Add this composable after the StatsScreen function

@Composable
private fun ConsistencyScoreCard(score: ConsistencyScore) {
    val levelColor = when (score.level) {
        ConsistencyLevel.BEGINNER -> Color(0xFF9E9E9E)
        ConsistencyLevel.BUILDING -> Color(0xFF2196F3)
        ConsistencyLevel.CONSISTENT -> Color(0xFF4CAF50)
        ConsistencyLevel.UNSTOPPABLE -> Color(0xFFFFD700)
    }

    val levelIcon = when (score.level) {
        ConsistencyLevel.BEGINNER -> AppIcons.Seedling
        ConsistencyLevel.BUILDING -> AppIcons.FireStreak
        ConsistencyLevel.CONSISTENT -> AppIcons.Lightning
        ConsistencyLevel.UNSTOPPABLE -> AppIcons.Crown
    }

    val levelText = when (score.level) {
        ConsistencyLevel.BEGINNER -> "Beginner"
        ConsistencyLevel.BUILDING -> "Building Momentum"
        ConsistencyLevel.CONSISTENT -> "Consistent Performer"
        ConsistencyLevel.UNSTOPPABLE -> "Unstoppable Force"
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
                        text = "${score.percentToNextLevel}% to next",
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
                            .fillMaxWidth(score.percentToNextLevel / 100f)
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
                    ConsistencyLevel.BEGINNER -> "Every journey starts with a single step. Keep showing up."
                    ConsistencyLevel.BUILDING -> "You're building real momentum. Don't stop now."
                    ConsistencyLevel.CONSISTENT -> "You've proven you're the person who shows up. Keep going."
                    ConsistencyLevel.UNSTOPPABLE -> "You're unstoppable. This is who you are now."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
