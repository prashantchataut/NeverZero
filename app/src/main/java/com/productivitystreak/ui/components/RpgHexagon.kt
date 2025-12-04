package com.productivitystreak.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.data.model.HabitAttribute
import com.productivitystreak.data.model.RpgStats
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RpgHexagon(
    stats: RpgStats,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    lineColor: Color = MaterialTheme.colorScheme.outlineVariant,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
    strokeColor: Color = MaterialTheme.colorScheme.primary,
    glowColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold
    )

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(size.toPx() / 2, size.toPx() / 2)
        val radius = (size.toPx() / 2) * 0.72f // Leave room for labels
        val maxStat = 10f // Normalize stats to 10 for now

        // Draw background webs (3 levels)
        for (i in 1..3) {
            val levelRadius = radius * (i / 3f)
            drawHexagonPath(center, levelRadius, lineColor.copy(alpha = 0.35f), strokeWidth = 1.5f)
        }

        // Draw stat polygon
        val attributes = listOf(
            HabitAttribute.STRENGTH,
            HabitAttribute.INTELLIGENCE,
            HabitAttribute.CHARISMA,
            HabitAttribute.WISDOM,
            HabitAttribute.DISCIPLINE
        )
        // Hexagon has 6 points, but we have 5 stats. 
        // Let's use a Pentagon for 5 stats or map them to 6 points (maybe split one or add Luck?)
        // Let's stick to 5 points (Pentagon) for the stats we defined.
        
        val angleStep = (2 * Math.PI / attributes.size).toFloat()
        val statPath = Path()

        attributes.forEachIndexed { index, attribute ->
            val statValue = stats.getStat(attribute).coerceAtMost(maxStat.toInt())
            val normalizedValue = statValue / maxStat
            val angle = -Math.PI.toFloat() / 2 + index * angleStep // Start from top
            
            val pointRadius = radius * normalizedValue
            val x = center.x + pointRadius * cos(angle)
            val y = center.y + pointRadius * sin(angle)

            if (index == 0) {
                statPath.moveTo(x, y)
            } else {
                statPath.lineTo(x, y)
            }

            // Draw Labels
            val labelRadius = radius * 1.25f
            val labelX = center.x + labelRadius * cos(angle)
            val labelY = center.y + labelRadius * sin(angle)
            
            val labelText = attribute.displayName.take(3).uppercase()
            val measuredText = textMeasurer.measure(AnnotatedString(labelText), style = labelStyle)
            
            drawText(
                textLayoutResult = measuredText,
                topLeft = Offset(labelX - measuredText.size.width / 2, labelY - measuredText.size.height / 2)
            )
        }
        statPath.close()

        drawPath(
            path = statPath,
            color = fillColor
        )
        // Glow layer
        drawPath(
            path = statPath,
            color = glowColor,
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = statPath,
            color = strokeColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHexagonPath(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float = 1f
) {
    val path = Path()
    val sides = 5 // Matching the 5 attributes
    val angleStep = (2 * Math.PI / sides).toFloat()
    
    for (i in 0 until sides) {
        val angle = -Math.PI.toFloat() / 2 + i * angleStep
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)
        
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth.dp.toPx())
    )
}
