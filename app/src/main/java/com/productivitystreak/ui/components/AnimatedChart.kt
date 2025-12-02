package com.productivitystreak.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Animated Chart Component
 * Supports line charts, bar charts, and progress rings with reveal animations
 */

@Composable
fun AnimatedLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF6C63FF),
    fillGradient: List<Color> = listOf(
        Color(0xFF6C63FF).copy(alpha = 0.3f),
        Color.Transparent
    )
) {
    var animationProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            animationProgress = value
        }
    }
    
    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        if (dataPoints.isEmpty()) return@Canvas
        
        val maxValue = dataPoints.maxOrNull() ?: 1f
        val minValue = dataPoints.minOrNull() ?: 0f
        val range = maxValue - minValue
        
        val width = size.width
        val height = size.height
        val padding = 32.dp.toPx()
        
        val usableWidth = width - 2 * padding
        val usableHeight = height - 2 * padding
        
        val stepX = if (dataPoints.size > 1) usableWidth / (dataPoints.size - 1) else 0f
        
        // Draw animated line
        val linePath = Path()
        val fillPath = Path()
        
        val visiblePoints = (dataPoints.size * animationProgress).toInt().coerceAtLeast(1)
        
        dataPoints.take(visiblePoints).forEachIndexed { index, value ->
            val normalizedValue = if (range > 0) (value - minValue) / range else 0f
            val x = padding + stepX * index
            val y = padding + usableHeight * (1f - normalizedValue)
            
            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, height - padding)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        
        // Close fill path
        if (visiblePoints > 0) {
            val lastX = padding + stepX * (visiblePoints - 1)
            fillPath.lineTo(lastX, height - padding)
            fillPath.close()
        }
        
        // Draw gradient fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = fillGradient,
                startY = padding,
                endY = height - padding
            )
        )
        
        // Draw line
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw points
        dataPoints.take(visiblePoints).forEachIndexed { index, value ->
            val normalizedValue = if (range > 0) (value - minValue) / range else 0f
            val x = padding + stepX * index
            val y = padding + usableHeight * (1f - normalizedValue)
            
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun AnimatedBarChart(
    dataPoints: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF4ECDC4)
) {
    var animationProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            animationProgress = value
        }
    }
    
    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        if (dataPoints.isEmpty()) return@Canvas
        
        val maxValue = dataPoints.maxOfOrNull { it.second } ?: 1f
        
        val width = size.width
        val height = size.height
        val padding = 32.dp.toPx()
        
        val usableWidth = width - 2 * padding
        val usableHeight = height - 2 * padding
        
        val barWidth = usableWidth / dataPoints.size * 0.6f
        val spacing = usableWidth / dataPoints.size
        
        dataPoints.forEachIndexed { index, (_, value) ->
            val normalizedValue = value / maxValue
            val animatedHeight = usableHeight * normalizedValue * animationProgress
            
            val x = padding + spacing * index + (spacing - barWidth) / 2
            val y = height - padding - animatedHeight
            
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, animatedHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
        }
    }
}

@Composable
fun AnimatedProgressRing(
    progress: Float,
    size: androidx.compose.ui.unit.Dp = 120.dp,
    modifier: Modifier = Modifier,
    ringColor: Color = Color(0xFFFF6B9D),
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.2f)
) {
    var animationProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(progress) {
        animate(
            initialValue = 0f,
            targetValue = progress,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            animationProgress = value
        }
    }
    
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = 12.dp.toPx()
        val radius = (this.size.minDimension - strokeWidth) / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        
        // Background circle
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Progress arc
        val sweepAngle = 360f * animationProgress
        drawArc(
            color = ringColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
    }
}
