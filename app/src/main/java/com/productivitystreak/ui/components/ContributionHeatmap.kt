package com.productivitystreak.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

/**
 * GitHub-style contribution heatmap
 * Shows last 365 days in a grid format with intensity-based coloring
 */
@Composable
fun ContributionHeatmap(
    contributions: Map<LocalDate, Float>, // Date to intensity (0.0 to 1.0)
    modifier: Modifier = Modifier,
    weeksToShow: Int = 52 // ~1 year
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.labelSmall,
                color = onSurface.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            
            // Legend squares
            for (i in 0..4) {
                val intensity = i / 4f
                val color = if (intensity == 0f) {
                    surfaceVariant
                } else {
                    primaryColor.copy(alpha = 0.2f + 0.8f * intensity)
                }
                
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(12.dp)
                        .padding(1.dp)
                ) {
                    drawRoundRect(
                        color = color,
                        size = this.size,
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "More",
                style = MaterialTheme.typography.labelSmall,
                color = onSurface.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
        
        // Heatmap grid
        HeatmapGrid(
            contributions = contributions,
            weeksToShow = weeksToShow,
            primaryColor = primaryColor,
            surfaceVariant = surfaceVariant,
            onSurface = onSurface
        )
    }
}

@Composable
private fun HeatmapGrid(
    contributions: Map<LocalDate, Float>,
    weeksToShow: Int,
    primaryColor: Color,
    surfaceVariant: Color,
    onSurface: Color
) {
    val today = LocalDate.now()
    val startDate = today.minusDays((weeksToShow * 7 - 1).toLong())
    
    // Organize data by week
    val weeks = mutableListOf<List<Pair<LocalDate, Float>>>()
    var currentWeek = mutableListOf<Pair<LocalDate, Float>>()
    
    var date = startDate
    while (!date.isAfter(today)) {
        val intensity = contributions[date] ?: 0f
        currentWeek.add(date to intensity)
        
        // Start new week on Sunday
        if (date.dayOfWeek.value == 7 || date == today) {
            weeks.add(currentWeek.toList())
            currentWeek = mutableListOf()
        }
        
        date = date.plusDays(1)
    }
    
    // Add remaining days if any
    if (currentWeek.isNotEmpty()) {
        weeks.add(currentWeek.toList())
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Month labels
        MonthLabels(
            weeks = weeks,
            onSurface = onSurface
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Grid
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val cellSize = 14.dp.toPx()
            val cellGap = 3.dp.toPx()
            val totalCellWidth = cellSize + cellGap
            
            weeks.forEachIndexed { weekIndex, week ->
                week.forEachIndexed { dayIndex, (_, intensity) ->
                    val x = weekIndex * totalCellWidth
                    val y = dayIndex * totalCellWidth
                    
                    val color = if (intensity <= 0f) {
                        surfaceVariant
                    } else {
                        primaryColor.copy(alpha = 0.2f + 0.8f * intensity)
                    }
                    
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = Size(cellSize, cellSize),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Day labels (Mon, Wed, Fri)
        DayLabels(onSurface = onSurface)
    }
}

@Composable
private fun MonthLabels(
    weeks: List<List<Pair<LocalDate, Float>>>,
    onSurface: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        var lastMonth: Int? = null
        
        weeks.forEachIndexed { index, week ->
            val firstDay = week.firstOrNull()?.first
            if (firstDay != null) {
                val month = firstDay.monthValue
                
                // Show month label at the start of each month
                if (month != lastMonth && (index == 0 || firstDay.dayOfMonth <= 7)) {
                    Text(
                        text = firstDay.month.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurface.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        modifier = Modifier.width(40.dp)
                    )
                    lastMonth = month
                } else {
                    Spacer(modifier = Modifier.width(17.dp))
                }
            }
        }
    }
}

@Composable
private fun DayLabels(onSurface: Color) {
    Column {
        listOf("Mon", "Wed", "Fri").forEachIndexed { index, day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelSmall,
                color = onSurface.copy(alpha = 0.6f),
                fontSize = 9.sp,
                modifier = Modifier.height(17.dp)
            )
            if (index < 2) {
                Spacer(modifier = Modifier.height(17.dp))
            }
        }
    }
}

/**
 * Helper function to generate sample contribution data
 */
fun generateSampleContributions(days: Int = 365): Map<LocalDate, Float> {
    val today = LocalDate.now()
    val contributions = mutableMapOf<LocalDate, Float>()
    
    for (i in 0 until days) {
        val date = today.minusDays(i.toLong())
        // Random intensity with some pattern (more recent = more likely to have data)
        val baseIntensity = if (i < 30) 0.7f else 0.4f
        val intensity = if (Math.random() > 0.3) {
            (baseIntensity + Math.random().toFloat() * 0.3f).coerceIn(0f, 1f)
        } else {
            0f
        }
        contributions[date] = intensity
    }
    
    return contributions
}
