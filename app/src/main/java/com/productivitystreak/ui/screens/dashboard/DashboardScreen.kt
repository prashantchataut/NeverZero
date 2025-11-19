package com.productivitystreak.ui.screens.dashboard

// Dashboard UI removed during architectural sanitization.

import android.graphics.Color.parseColor
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.theme.NeverZeroTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import java.time.LocalTime
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    uiState: AppUiState,
    onToggleTask: (String) -> Unit,
    onRefreshQuote: () -> Unit,
    onAddHabitClick: () -> Unit,
    onSelectStreak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val greetingPrefix = remember {
        val hour = LocalTime.now().hour
        when {
            hour in 5..11 -> "Hello"
            hour in 12..16 -> "Hello"
            hour in 17..21 -> "Hello"
            else -> "Hello"
        }
    }

    val leadStreak = uiState.streaks.find { it.id == uiState.selectedStreakId }
        ?: uiState.streaks.firstOrNull()

    val progress by animateFloatAsState(
        targetValue = leadStreak?.progress ?: 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "lead-progress"
    )

    val confettiState = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedContent(
            targetState = uiState.userName,
            label = "dashboard-greeting"
        ) { name ->
            Text(
                text = "$greetingPrefix, $name! Let’s get to work.",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        leadStreak?.let { streak ->
            LeadHabitCard(
                streak = streak,
                progress = progress,
                onClick = { onSelectStreak(streak.id) }
            )
        }

        Text(
            text = "Today",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp)
        )

        if (uiState.todayTasks.isEmpty()) {
            DashboardEmptyState(onAddHabitClick = onAddHabitClick)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.todayTasks, key = { it.id }) { task ->
                    val showConfetti = confettiState[task.id] == true

                    DashboardTaskRow(
                        task = task,
                        onToggle = {
                            if (!task.isCompleted) {
                                if (uiState.profileState.hapticsEnabled) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                confettiState[task.id] = true
                                onToggleTask(task.id)
                            }
                        },
                        showConfetti = showConfetti
                    )

                    LaunchedEffect(showConfetti) {
                        if (showConfetti) {
                            delay(450)
                            confettiState[task.id] = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeadHabitCard(
    streak: Streak,
    progress: Float,
    onClick: () -> Unit
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            NeverZeroTheme.gradientColors.PremiumStart,
            NeverZeroTheme.gradientColors.PremiumEnd
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize()
                .padding(20.dp)
        ) {
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
    val size = 80.dp
    val strokeWidth = 10.dp

    Canvas(modifier = modifier.size(size)) {
        val sweep = 360f * progress
        val stroke = strokeWidth.toPx()
        val radius = size.minDimension / 2f - stroke

        drawCircle(
            color = Color.White.copy(alpha = 0.25f),
            radius = radius,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )

        drawArc(
            color = Color.White,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = stroke,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}

@Composable
private fun DashboardTaskRow(
    task: DashboardTask,
    onToggle: () -> Unit,
    showConfetti: Boolean
) {
    val accent = remember(task.accentHex) {
        hexToColor(task.accentHex, MaterialTheme.colorScheme.primary)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = !task.isCompleted, onClick = onToggle),
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

            AnimatedVisibility(
                visible = showConfetti,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.matchParentSize()
            ) {
                ConfettiOverlay(color = accent)
            }
        }
    }
}

@Composable
private fun ConfettiOverlay(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val random = kotlin.random.Random
        val particleCount = 24
        repeat(particleCount) { index ->
            val startX = size.width * random.nextFloat()
            val startY = size.height * random.nextFloat()
            val velocity = (40 + random.nextInt(80)).toFloat()
            val end = Offset(
                x = startX + random.nextFloat() * 12f - 6f,
                y = (startY + velocity).coerceAtMost(size.height)
            )
            val tint = if (index % 3 == 0) MaterialTheme.colorScheme.primary else color
            drawLine(
                color = tint.copy(alpha = 0.55f),
                start = Offset(startX, startY),
                end = end,
                strokeWidth = 3.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
private fun DashboardEmptyState(onAddHabitClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    drawCircle(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        radius = size.minDimension / 3
                    )
                    drawCircle(
                        color = NeverZeroTheme.gradientColors.OceanStart.copy(alpha = 0.9f),
                        radius = size.minDimension / 6,
                        center = center
                    )
                    drawCircle(
                        color = NeverZeroTheme.gradientColors.OceanEnd.copy(alpha = 0.9f),
                        radius = size.minDimension / 9,
                        center = center + Offset(size.minDimension / 9, -size.minDimension / 9)
                    )
                }
            }

            Text(
                text = "No habits for today",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Enjoy your free time or add a new habit to keep the streak alive.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            SoftPrimaryButton(
                text = "Add a habit",
                onClick = onAddHabitClick
            )
        }
    }
}

@Composable
private fun SoftPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> pressed = true
                is androidx.compose.foundation.interaction.PressInteraction.Release,
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> pressed = false
            }
        }
    }

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "button-scale"
    )

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(999.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp
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

