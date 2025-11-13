package com.productivitystreak.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.Motion
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom pulsing circle loader
 */
@Composable
fun PulsingLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsingLoader")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loaderScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loaderAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = this.size.minDimension / 2 * scale

            drawCircle(
                color = color.copy(alpha = alpha * 0.3f),
                radius = radius,
                center = center
            )

            drawCircle(
                color = color.copy(alpha = alpha),
                radius = radius * 0.6f,
                center = center
            )
        }
    }
}

/**
 * Orbit loader - Multiple dots orbiting around center
 */
@Composable
fun OrbitLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbitLoader")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitRotation"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .progressSemantics()
    ) {
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2
        val orbitRadius = this.size.minDimension / 3
        val dotRadius = this.size.minDimension / 12

        val dotCount = 3
        val angleStep = 360f / dotCount

        for (i in 0 until dotCount) {
            val angle = Math.toRadians((rotation + i * angleStep).toDouble())
            val x = centerX + (orbitRadius * cos(angle)).toFloat()
            val y = centerY + (orbitRadius * sin(angle)).toFloat()

            val dotAlpha = 1f - (i * 0.25f)

            drawCircle(
                color = color.copy(alpha = dotAlpha),
                radius = dotRadius,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Wave loader - Animated wave effect
 */
@Composable
fun WaveLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    barCount: Int = 5
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveLoader")

    val animations = (0 until barCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing, delayMillis = index * 100),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animations.forEach { animatedValue ->
            val height by animatedValue

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp * height)
                    .graphicsLayer {
                        this.alpha = height
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = color,
                        size = this.size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                    )
                }
            }
        }
    }
}

/**
 * Circular progress indicator with gradient
 */
@Composable
fun GradientCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary
    ),
    strokeWidth: Dp = 8.dp,
    size: Dp = 120.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "circularProgress"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .progressSemantics(progress)
    ) {
        val sweep = animatedProgress * 360f
        val strokeWidthPx = strokeWidth.toPx()

        // Background circle
        drawCircle(
            color = gradientColors.first().copy(alpha = 0.1f),
            radius = (this.size.minDimension - strokeWidthPx) / 2,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Progress arc with gradient
        val brush = Brush.sweepGradient(
            colors = gradientColors,
            center = center
        )

        drawArc(
            brush = brush,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            size = Size(
                this.size.width - strokeWidthPx,
                this.size.height - strokeWidthPx
            ),
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
        )
    }
}

/**
 * Linear progress with gradient and shimmer effect
 */
@Composable
fun GradientLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary
    ),
    height: Dp = 8.dp,
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (animated) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        } else {
            snap()
        },
        label = "linearProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .progressSemantics(progress)
    ) {
        // Background
        drawRoundRect(
            color = gradientColors.first().copy(alpha = 0.1f),
            size = this.size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(height.toPx() / 2)
        )

        if (animatedProgress > 0f) {
            // Progress with gradient
            val progressBrush = Brush.linearGradient(
                colors = gradientColors,
                start = Offset(0f, 0f),
                end = Offset(this.size.width * animatedProgress, 0f)
            )

            drawRoundRect(
                brush = progressBrush,
                size = Size(this.size.width * animatedProgress, this.size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(height.toPx() / 2)
            )

            // Shimmer highlight
            val shimmerX = this.size.width * animatedProgress * shimmerOffset
            if (shimmerX > 0 && shimmerX < this.size.width * animatedProgress) {
                drawLine(
                    color = Color.White.copy(alpha = 0.4f),
                    start = Offset(shimmerX, 0f),
                    end = Offset(shimmerX, this.size.height),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Dots loader - Three bouncing dots
 */
@Composable
fun DotsLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 12.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dotsLoader")

    val animations = (0..2).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing, delayMillis = index * 150),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animations.forEach { animatedValue ->
            val scale by animatedValue

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        scaleX = 0.5f + (scale * 0.5f)
                        scaleY = 0.5f + (scale * 0.5f)
                        this.alpha = 0.5f + (scale * 0.5f)
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = color,
                        radius = this.size.minDimension / 2
                    )
                }
            }
        }
    }
}

/**
 * Spinning arc loader
 */
@Composable
fun SpinningArcLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinningArc")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arcRotation"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .progressSemantics()
    ) {
        rotate(degrees = rotation) {
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                size = Size(
                    this.size.width - strokeWidth.toPx(),
                    this.size.height - strokeWidth.toPx()
                ),
                topLeft = Offset(strokeWidth.toPx() / 2, strokeWidth.toPx() / 2)
            )
        }
    }
}
