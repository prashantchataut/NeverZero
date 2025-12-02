package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.GlassCard
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.theme.Spacing
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun SwipeableHabitCard(
    task: DashboardTask,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    
    // Thresholds for actions
    val completeThreshold = with(density) { -100.dp.toPx() } // Swipe UP
    val skipThreshold = with(density) { 100.dp.toPx() }     // Swipe DOWN
    
    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            offsetY.snapTo(offsetY.value + delta)
        }
    }

    val accentColor = try {
        Color(android.graphics.Color.parseColor(task.accentHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .height(160.dp)
            .fillMaxWidth()
    ) {
        // Background Actions (Visible when swiping)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    when {
                        offsetY.value < 0 -> accentColor.copy(alpha = 0.2f)
                        offsetY.value > 0 -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        else -> Color.Transparent
                    }
                )
                .padding(Spacing.md),
            contentAlignment = Alignment.Center
        ) {
            if (offsetY.value < 0) {
                // Complete Action (Top)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = Spacing.md)
                        .alpha((-offsetY.value / -completeThreshold).coerceIn(0f, 1f)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Complete",
                        tint = accentColor
                    )
                    Text(
                        text = "+50 XP",
                        style = MaterialTheme.typography.titleMedium,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (offsetY.value > 0) {
                // Skip Action (Bottom)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = Spacing.md)
                        .alpha((offsetY.value / skipThreshold).coerceIn(0f, 1f)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Skip",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "SKIP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Foreground Card
        GlassCard(
            modifier = Modifier
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Vertical,
                    onDragStopped = { velocity ->
                        scope.launch {
                            if (offsetY.value < completeThreshold) {
                                // Trigger Complete
                                onComplete()
                                offsetY.animateTo(-1000f) // Fly off screen
                            } else if (offsetY.value > skipThreshold) {
                                // Trigger Skip
                                onSkip()
                                offsetY.animateTo(1000f) // Fly off screen
                            } else {
                                // Snap back
                                offsetY.animateTo(0f)
                            }
                        }
                    }
                )
                .fillMaxSize(),
            contentPadding = PaddingValues(Spacing.md)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Streak Badge
                    if (task.streakCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = accentColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = com.productivitystreak.ui.icons.AppIcons.FireStreak,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${task.streakCount}",
                                style = MaterialTheme.typography.labelMedium,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                // Bottom Section (Timer/Status)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Swipe up to Claim XP",
                        style = MaterialTheme.typography.bodySmall,
                        color = accentColor
                    )
                }
            }
        }
    }
}
