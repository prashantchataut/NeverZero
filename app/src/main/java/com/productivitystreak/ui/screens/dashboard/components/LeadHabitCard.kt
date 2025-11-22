package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun LeadHabitCard(
    streak: Streak,
    progress: Float,
    onClick: () -> Unit
) {
    val designColors = NeverZeroTheme.designColors
    val gradient = remember(designColors) {
        Brush.linearGradient(
            colors = listOf(
                designColors.primary.copy(alpha = 0.34f),
                designColors.secondary.copy(alpha = 0.40f)
            )
        )
    }

    val highlightBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.18f),
                Color.Transparent
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, designColors.border.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = gradient
                )
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(highlightBrush)
            )

            Column(
                modifier = Modifier.align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Lead habit",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                    text = streak.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Goal • ${streak.goalPerDay} ${streak.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "${streak.currentCount} day streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.82f)
                )
            }

            CircularProgressRing(
                modifier = Modifier.align(Alignment.CenterEnd),
                progress = progress.coerceIn(0f, 1f)
            )
        }
    }
}

@Composable
private fun CircularProgressRing(
    modifier: Modifier = Modifier,
    progress: Float
) {
    val ringSize = 80.dp
    val strokeWidth = 10.dp

    Canvas(modifier = modifier.size(ringSize)) {
        val sweep = 360f * progress
        val stroke = strokeWidth.toPx()
        val radius = size.minDimension / 2f - stroke

        drawCircle(
            color = Color.White.copy(alpha = 0.25f),
            radius = radius,
            style = Stroke(width = stroke)
        )

        drawArc(
            color = Color.White,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Round
            )
        )
    }
}
