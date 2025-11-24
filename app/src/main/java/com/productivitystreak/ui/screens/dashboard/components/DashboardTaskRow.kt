package com.productivitystreak.ui.screens.dashboard.components

import android.graphics.Color.parseColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun DashboardTaskRow(
    task: DashboardTask,
    onToggle: () -> Unit,
    showConfetti: Boolean
) {
    val soundManager = com.productivitystreak.ui.utils.rememberSoundManager()
    val primaryColor = MaterialTheme.colorScheme.primary
    val accent = hexToColor(task.accentHex, primaryColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = !task.isCompleted) {
                soundManager.playClick()
                onToggle()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = accent
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(accent)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = if (task.isCompleted) "Done" else "Tap to log",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (task.isCompleted) NeverZeroTheme.semanticColors.Success else MaterialTheme.colorScheme.primary
                )
            }

            if (showConfetti) {
                EnergySurgeAnimation(color = accent)
            }
        }
    }
}

@Composable
private fun EnergySurgeAnimation(color: Color) {
    val designColors = NeverZeroTheme.designColors
    val progress = remember { Animatable(0f) }

    val circuitBrush = remember(color, designColors) {
        Brush.horizontalGradient(
            listOf(color, designColors.primary, designColors.secondary)
        )
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val p = progress.value
        val centerY = size.height * 0.5f
        val startX = size.width * 0.1f
        val endX = size.width * 0.9f
        val currentX = startX + (endX - startX) * p
        val stroke = 3.dp.toPx()

        drawLine(
            color = designColors.border.copy(alpha = 0.7f),
            start = Offset(startX, centerY),
            end = Offset(endX, centerY),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            brush = circuitBrush,
            start = Offset(startX, centerY),
            end = Offset(currentX, centerY),
            strokeWidth = stroke * 1.5f,
            cap = StrokeCap.Round
        )

        val rippleRadius = 18.dp.toPx()
        val origin = Offset(startX, centerY)
        drawCircle(
            color = color.copy(alpha = (1f - p) * 0.8f),
            radius = rippleRadius * p,
            center = origin
        )
        drawCircle(
            color = designColors.glow.copy(alpha = (1f - p) * 0.6f),
            radius = rippleRadius * (0.4f + 0.6f * p),
            center = origin
        )
    }
}

private fun hexToColor(hex: String, fallback: Color): Color {
    return try {
        Color(parseColor(hex))
    } catch (_: IllegalArgumentException) {
        fallback
    }
}
