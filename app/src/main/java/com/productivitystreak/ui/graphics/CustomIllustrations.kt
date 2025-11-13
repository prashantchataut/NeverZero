package com.productivitystreak.ui.graphics

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.Motion
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom Empty State Illustration - No Streaks
 * Animated graphic showing a flame waiting to be lit
 */
@Composable
fun EmptyStreaksIllustration(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF0061FE),
    secondaryColor: Color = Color(0xFF6C5CE7)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emptyStreakAnim")

    val flameGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameGlow"
    )

    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleRotation"
    )

    Canvas(modifier = modifier.size(200.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Animated glow circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.3f * flameGlow),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = size.minDimension / 2
            ),
            center = Offset(centerX, centerY),
            radius = size.minDimension / 2
        )

        // Draw unlit flame base (match shape)
        val flameBasePath = Path().apply {
            moveTo(centerX - 40f, centerY + 60f)
            lineTo(centerX + 40f, centerY + 60f)
            lineTo(centerX + 30f, centerY + 80f)
            lineTo(centerX - 30f, centerY + 80f)
            close()
        }

        drawPath(
            path = flameBasePath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF4A5568),
                    Color(0xFF2D3748)
                )
            )
        )

        // Draw dotted flame outline (waiting to be lit)
        val flamePath = Path().apply {
            moveTo(centerX, centerY - 60f)
            cubicTo(
                centerX - 50f, centerY - 40f,
                centerX - 60f, centerY + 20f,
                centerX, centerY + 60f
            )
            cubicTo(
                centerX + 60f, centerY + 20f,
                centerX + 50f, centerY - 40f,
                centerX, centerY - 60f
            )
        }

        drawPath(
            path = flamePath,
            color = primaryColor.copy(alpha = 0.3f),
            style = Stroke(
                width = 3.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        )

        // Animated sparkles
        rotate(degrees = sparkleRotation, pivot = Offset(centerX, centerY)) {
            drawSparkles(
                center = Offset(centerX, centerY),
                radius = 100f,
                color = secondaryColor.copy(alpha = 0.6f)
            )
        }

        // Plus icon in center
        val plusSize = 40f
        val plusStroke = 6f
        drawLine(
            color = primaryColor.copy(alpha = 0.8f),
            start = Offset(centerX, centerY - plusSize / 2),
            end = Offset(centerX, centerY + plusSize / 2),
            strokeWidth = plusStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = primaryColor.copy(alpha = 0.8f),
            start = Offset(centerX - plusSize / 2, centerY),
            end = Offset(centerX + plusSize / 2, centerY),
            strokeWidth = plusStroke,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Custom Empty State Illustration - No Tasks
 * Animated graphic showing a checkmark list
 */
@Composable
fun EmptyTasksIllustration(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF0061FE),
    accentColor: Color = Color(0xFF00D9A5)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emptyTaskAnim")

    val checkAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(500)
        ),
        label = "checkAnimation"
    )

    val listFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "listFloat"
    )

    Canvas(modifier = modifier.size(180.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Draw clipboard background
        val clipboardPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = centerX - 70f,
                    top = centerY - 80f + listFloat,
                    right = centerX + 70f,
                    bottom = centerY + 80f + listFloat,
                    radiusX = 16f,
                    radiusY = 16f
                )
            )
        }

        drawPath(
            path = clipboardPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.1f),
                    primaryColor.copy(alpha = 0.05f)
                ),
                start = Offset(centerX, centerY - 80f),
                end = Offset(centerX, centerY + 80f)
            )
        )

        drawPath(
            path = clipboardPath,
            color = primaryColor.copy(alpha = 0.3f),
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw checkmark items (3 items)
        val itemStartY = centerY - 40f + listFloat
        val itemSpacing = 45f

        for (i in 0..2) {
            val itemY = itemStartY + (i * itemSpacing)
            val checkProgress = if (checkAnimation > i * 0.3f) {
                ((checkAnimation - i * 0.3f) / 0.7f).coerceIn(0f, 1f)
            } else 0f

            // Checkbox circle
            drawCircle(
                color = if (checkProgress > 0.8f) accentColor else primaryColor.copy(alpha = 0.3f),
                radius = 12f,
                center = Offset(centerX - 45f, itemY),
                style = if (checkProgress > 0.8f) androidx.compose.ui.graphics.drawscope.Fill else Stroke(
                    width = 2.dp.toPx()
                )
            )

            // Animated checkmark
            if (checkProgress > 0f) {
                val checkPath = Path().apply {
                    moveTo(centerX - 50f, itemY)
                    lineTo(centerX - 45f, itemY + 5f * checkProgress)
                    lineTo(centerX - 35f, itemY - 5f * checkProgress)
                }
                drawPath(
                    path = checkPath,
                    color = if (checkProgress > 0.8f) Color.White else accentColor,
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )
            }

            // Task line
            val lineAlpha = if (checkProgress > 0.8f) 0.3f else 0.5f
            drawLine(
                color = primaryColor.copy(alpha = lineAlpha),
                start = Offset(centerX - 20f, itemY),
                end = Offset(centerX + 50f, itemY),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Animated Fire Streak Illustration
 * Shows an active, burning flame with particles
 */
@Composable
fun ActiveStreakFlame(
    modifier: Modifier = Modifier,
    streakColor: Color,
    intensity: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flameAnim")

    val flameFlicker by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameFlicker"
    )

    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )

    Canvas(modifier = modifier.size(48.dp)) {
        val centerX = size.width / 2
        val baseY = size.height * 0.8f

        scale(scale = flameFlicker * intensity, pivot = Offset(centerX, baseY)) {
            // Draw outer flame
            val outerFlamePath = Path().apply {
                moveTo(centerX, size.height * 0.2f)
                cubicTo(
                    centerX - size.width * 0.35f, size.height * 0.4f,
                    centerX - size.width * 0.4f, size.height * 0.7f,
                    centerX, baseY
                )
                cubicTo(
                    centerX + size.width * 0.4f, size.height * 0.7f,
                    centerX + size.width * 0.35f, size.height * 0.4f,
                    centerX, size.height * 0.2f
                )
            }

            drawPath(
                path = outerFlamePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        streakColor.copy(alpha = 0.9f),
                        streakColor.copy(alpha = 0.6f),
                        streakColor.copy(alpha = 0.3f)
                    ),
                    startY = size.height * 0.2f,
                    endY = baseY
                )
            )

            // Draw inner flame
            val innerFlamePath = Path().apply {
                moveTo(centerX, size.height * 0.35f)
                cubicTo(
                    centerX - size.width * 0.2f, size.height * 0.5f,
                    centerX - size.width * 0.22f, size.height * 0.7f,
                    centerX, baseY - 5f
                )
                cubicTo(
                    centerX + size.width * 0.22f, size.height * 0.7f,
                    centerX + size.width * 0.2f, size.height * 0.5f,
                    centerX, size.height * 0.35f
                )
            }

            drawPath(
                path = innerFlamePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFDD00).copy(alpha = 0.9f),
                        streakColor.copy(alpha = 0.7f)
                    )
                )
            )

            // Draw particles
            val particleAlpha = 1f - (particleOffset / 40f)
            for (i in 0..2) {
                val xOffset = (i - 1) * 8f
                drawCircle(
                    color = streakColor.copy(alpha = particleAlpha * 0.6f),
                    radius = 2f,
                    center = Offset(centerX + xOffset, baseY - particleOffset - (i * 10f))
                )
            }
        }
    }
}

/**
 * Helper function to draw sparkles
 */
private fun DrawScope.drawSparkles(center: Offset, radius: Float, color: Color) {
    val sparkleCount = 4
    val angleStep = 360f / sparkleCount

    for (i in 0 until sparkleCount) {
        val angle = Math.toRadians((i * angleStep).toDouble())
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()

        // Draw star shape
        drawLine(
            color = color,
            start = Offset(x - 6f, y),
            end = Offset(x + 6f, y),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(x, y - 6f),
            end = Offset(x, y + 6f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Celebration Confetti Animation
 * Shows particles bursting outward
 */
@Composable
fun CelebrationConfetti(
    modifier: Modifier = Modifier,
    trigger: Boolean,
    onAnimationEnd: () -> Unit = {}
) {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(trigger) {
        if (trigger) {
            isAnimating = true
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        finishedListener = {
            if (it == 1f) {
                isAnimating = false
                onAnimationEnd()
            }
        },
        label = "confettiProgress"
    )

    if (progress > 0f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            val particleColors = listOf(
                Color(0xFFFF6B6B),
                Color(0xFF6C5CE7),
                Color(0xFF00D9A5),
                Color(0xFFFF9F43),
                Color(0xFF0095FF),
                Color(0xFFFFC043)
            )

            // Draw 20 particles
            for (i in 0 until 20) {
                val angle = (i * 18f) * (Math.PI / 180f)
                val distance = 150f * progress
                val x = centerX + (distance * cos(angle)).toFloat()
                val y = centerY + (distance * sin(angle)).toFloat() + (100f * progress * progress)

                val alpha = 1f - progress
                val size = 8f + (i % 3) * 4f

                drawCircle(
                    color = particleColors[i % particleColors.size].copy(alpha = alpha),
                    radius = size,
                    center = Offset(x, y)
                )
            }
        }
    }
}
