package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.Spacing
import com.productivitystreak.ui.theme.AnimationTokens
import java.time.LocalTime
import java.time.LocalDate
import java.time.DayOfWeek
import kotlin.math.cos
import kotlin.math.sin

/**
 * Dashboard Hero Section
 * Time-aware greeting with animated gradient and progress visualization
 */
@Composable
fun HeroSection(
    userName: String,
    currentStreak: Int,
    habitsCompleted: Int,
    totalHabits: Int,
    modifier: Modifier = Modifier,
    scrollOffset: Int = 0
) {
    val currentTime = remember { LocalTime.now() }
    val currentDay = remember { LocalDate.now().dayOfWeek }
    
    // Time-aware gradient colors
    val (gradientStart, gradientEnd) = remember(currentTime) {
        getTimeAwareGradient(currentTime)
    }
    
    // Time-aware greeting
    val greeting = remember(currentTime, currentDay) {
        getTimeAwareGreeting(currentTime, currentDay)
    }
    
    // Animated gradient shift
    val infiniteTransition = rememberInfiniteTransition(label = "gradient-shift")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient-offset"
    )
    
    // Progress animation
    val progress = if (totalHabits > 0) habitsCompleted.toFloat() / totalHabits else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = AnimationTokens.AnimationCurves.SpringyEnter,
        label = "progress"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        gradientStart.copy(alpha = 0.3f + gradientOffset * 0.2f),
                        gradientEnd.copy(alpha = 0.5f - gradientOffset * 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(Spacing.xl)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (scrollOffset * 0.5f).dp), // Parallax effect
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Greeting Section
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (currentStreak > 0) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.material3.Icon(
                            imageVector = com.productivitystreak.ui.icons.AppIcons.FireStreak,
                            contentDescription = "Streak",
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = "$currentStreak day streak",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Progress Ring
            Box(
                modifier = Modifier.align(Alignment.End),
                contentAlignment = Alignment.Center
            ) {
                ProgressRing(
                    progress = animatedProgress,
                    size = 100.dp,
                    modifier = Modifier
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$habitsCompleted",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "of $totalHabits",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressRing(
    progress: Float,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = 8.dp.toPx()
        val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)
        val radius = (this.size.minDimension - strokeWidth) / 2f
        
        // Background circle
        drawCircle(
            color = surfaceColor,
            radius = radius,
            center = centerOffset,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Progress arc
        val sweepAngle = 360f * progress
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(
                centerOffset.x - radius,
                centerOffset.y - radius
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}

/**
 * Get time-aware gradient based on current hour
 */
private fun getTimeAwareGradient(time: LocalTime): Pair<Color, Color> {
    return when (time.hour) {
        in 0..5 -> {
            // Night: Deep space
            Color(0xFF1a0033) to Color(0xFF0d001a)
        }
        in 6..11 -> {
            // Morning: Sunrise
            Color(0xFFff9966) to Color(0xFFff5e62)
        }
        in 12..17 -> {
            // Afternoon: Bright sky
            Color(0xFF56ccf2) to Color(0xFF2f80ed)
        }
        in 18..21 -> {
            // Evening: Dusk
            Color(0xFF667eea) to Color(0xFF764ba2)
        }
        else -> {
            // Late night
            Color(0xFF2c1654) to Color(0xFF1a0033)
        }
    }
}

/**
 * Get time and day-aware greeting message
 */
private fun getTimeAwareGreeting(time: LocalTime, day: DayOfWeek): String {
    val timeGreeting = when (time.hour) {
        in 0..5 -> "Still up"
        in 6..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        in 18..21 -> "Good evening"
        else -> "Good night"
    }
    
    val dayModifier = when (day) {
        DayOfWeek.MONDAY -> "Fresh week, fresh start"
        DayOfWeek.FRIDAY -> "Almost there"
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> "Enjoying the weekend"
        else -> timeGreeting
    }
    
    return if (day == DayOfWeek.MONDAY || day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
        dayModifier
    } else {
        timeGreeting
    }
}
