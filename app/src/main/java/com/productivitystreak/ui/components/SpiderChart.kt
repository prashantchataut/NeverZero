package com.productivitystreak.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.data.model.RpgStats
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Spider/Radar Chart for RPG Stats visualization
 * Displays 5 attributes: STR, INT, CHA, WIS, DIS in a pentagon shape
 */
@Composable
fun SpiderChart(
    rpgStats: RpgStats,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    maxValue: Int = 10
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val textMeasurer = rememberTextMeasurer()
    
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val centerX = this.size.width / 2f
            val centerY = this.size.height / 2f
            val radius = (this.size.minDimension / 2f) * 0.7f
            
            // 5 stats in pentagon formation
            val stats = listOf(
                "STR" to rpgStats.strength,
                "INT" to rpgStats.intelligence,
                "CHA" to rpgStats.charisma,
                "WIS" to rpgStats.wisdom,
                "DIS" to rpgStats.discipline
            )
            
            // Draw background grid (5 levels)
            drawSpiderGrid(
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                levels = 5,
                sides = 5,
                color = outlineVariant.copy(alpha = 0.3f)
            )
            
            // Draw the data polygon
            drawDataPolygon(
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                stats = stats,
                maxValue = maxValue,
                fillColor = Color.Green.copy(alpha = 0.3f),
                strokeColor = primaryColor
            )
            
            // Draw dots at vertices
            drawDataPoints(
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                stats = stats,
                maxValue = maxValue,
                color = primaryColor
            )
            
            // Draw labels
            drawLabels(
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                stats = stats,
                textMeasurer = textMeasurer,
                textStyle = TextStyle(
                    color = onSurface,
                    fontSize = 12.sp
                )
            )
        }
    }
}

private fun DrawScope.drawSpiderGrid(
    centerX: Float,
    centerY: Float,
    radius: Float,
    levels: Int,
    sides: Int,
    color: Color
) {
    // Draw concentric polygons
    for (level in 1..levels) {
        val levelRadius = radius * (level.toFloat() / levels)
        val path = Path()
        
        for (i in 0 until sides) {
            val angle = (PI / 2) - (2 * PI * i / sides)
            val x = centerX + levelRadius * cos(angle).toFloat()
            val y = centerY - levelRadius * sin(angle).toFloat()
            
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.dp.toPx())
        )
    }
    
    // Draw radial lines from center to vertices
    for (i in 0 until sides) {
        val angle = (PI / 2) - (2 * PI * i / sides)
        val x = centerX + radius * cos(angle).toFloat()
        val y = centerY - radius * sin(angle).toFloat()
        
        drawLine(
            color = color,
            start = Offset(centerX, centerY),
            end = Offset(x, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawDataPolygon(
    centerX: Float,
    centerY: Float,
    radius: Float,
    stats: List<Pair<String, Int>>,
    maxValue: Int,
    fillColor: Color,
    strokeColor: Color
) {
    val path = Path()
    
    stats.forEachIndexed { index, (_, value) ->
        val normalizedValue = (value.toFloat() / maxValue).coerceIn(0f, 1f)
        val angle = (PI / 2) - (2 * PI * index / stats.size)
        val distance = radius * normalizedValue
        val x = centerX + distance * cos(angle).toFloat()
        val y = centerY - distance * sin(angle).toFloat()
        
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    
    // Fill the polygon
    drawPath(
        path = path,
        color = fillColor
    )
    
    // Stroke the polygon
    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun DrawScope.drawDataPoints(
    centerX: Float,
    centerY: Float,
    radius: Float,
    stats: List<Pair<String, Int>>,
    maxValue: Int,
    color: Color
) {
    stats.forEachIndexed { index, (_, value) ->
        val normalizedValue = (value.toFloat() / maxValue).coerceIn(0f, 1f)
        val angle = (PI / 2) - (2 * PI * index / stats.size)
        val distance = radius * normalizedValue
        val x = centerX + distance * cos(angle).toFloat()
        val y = centerY - distance * sin(angle).toFloat()
        
        // Draw outer circle (white background)
        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = Offset(x, y)
        )
        
        // Draw inner circle (colored)
        drawCircle(
            color = color,
            radius = 4.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawLabels(
    centerX: Float,
    centerY: Float,
    radius: Float,
    stats: List<Pair<String, Int>>,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle
) {
    val labelDistance = radius + 30.dp.toPx()
    
    stats.forEachIndexed { index, (label, value) ->
        val angle = (PI / 2) - (2 * PI * index / stats.size)
        val x = centerX + labelDistance * cos(angle).toFloat()
        val y = centerY - labelDistance * sin(angle).toFloat()
        
        val text = "$label: $value"
        val textLayoutResult = textMeasurer.measure(text, textStyle)
        
        // Center the text around the calculated position
        val textX = x - textLayoutResult.size.width / 2f
        val textY = y - textLayoutResult.size.height / 2f
        
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(textX, textY)
        )
    }
}
