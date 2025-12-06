package com.productivitystreak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.RpgStats
import com.productivitystreak.ui.theme.NeverZeroTheme
import java.time.LocalDate

@Preview(name = "Spider Chart - Low Stats", showBackground = true)
@Composable
private fun SpiderChartPreviewLow() {
    NeverZeroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Beginner Character",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                SpiderChart(
                    rpgStats = RpgStats(
                        strength = 2,
                        intelligence = 3,
                        charisma = 2,
                        wisdom = 1,
                        discipline = 2,
                        level = 1,
                        currentXp = 25,
                        xpToNextLevel = 75
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Spider Chart - Balanced Stats", showBackground = true)
@Composable
private fun SpiderChartPreviewBalanced() {
    NeverZeroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Intermediate Character",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                SpiderChart(
                    rpgStats = RpgStats(
                        strength = 5,
                        intelligence = 6,
                        charisma = 5,
                        wisdom = 5,
                        discipline = 6,
                        level = 5,
                        currentXp = 450,
                        xpToNextLevel = 50
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Spider Chart - Max Stats", showBackground = true)
@Composable
private fun SpiderChartPreviewMax() {
    NeverZeroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Master Character",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                SpiderChart(
                    rpgStats = RpgStats(
                        strength = 10,
                        intelligence = 9,
                        charisma = 8,
                        wisdom = 10,
                        discipline = 10,
                        level = 15,
                        currentXp = 1450,
                        xpToNextLevel = 50
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Spider Chart - Specialized (STR Focus)", showBackground = true)
@Composable
private fun SpiderChartPreviewSpecialized() {
    NeverZeroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Strength Specialist",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                SpiderChart(
                    rpgStats = RpgStats(
                        strength = 10,
                        intelligence = 3,
                        charisma = 2,
                        wisdom = 4,
                        discipline = 7,
                        level = 8,
                        currentXp = 750,
                        xpToNextLevel = 50
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Contribution Heatmap - Active User", showBackground = true)
@Composable
private fun ContributionHeatmapPreviewActive() {
    NeverZeroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Active User - 365 Days",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "High consistency with occasional breaks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                ContributionHeatmap(
                    contributions = generateActiveUserContributions(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Contribution Heatmap - Sporadic User", showBackground = true)
@Composable
private fun ContributionHeatmapPreviewSporadic() {
    NeverZeroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sporadic User - 365 Days",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Inconsistent activity with long gaps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                ContributionHeatmap(
                    contributions = generateSporadicUserContributions(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Contribution Heatmap - Perfect Streak", showBackground = true)
@Composable
private fun ContributionHeatmapPreviewPerfect() {
    NeverZeroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Perfect Streak - 365 Days",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Never missed a single day!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                ContributionHeatmap(
                    contributions = generatePerfectStreakContributions(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Contribution Heatmap - New User", showBackground = true)
@Composable
private fun ContributionHeatmapPreviewNew() {
    NeverZeroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "New User - 30 Days",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Just getting started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                ContributionHeatmap(
                    contributions = generateNewUserContributions(),
                    modifier = Modifier.fillMaxWidth(),
                    weeksToShow = 8
                )
            }
        }
    }
}

// Helper functions for preview data
private fun generateActiveUserContributions(): Map<LocalDate, Float> {
    val today = LocalDate.now()
    val contributions = mutableMapOf<LocalDate, Float>()
    
    for (i in 0 until 365) {
        val date = today.minusDays(i.toLong())
        // 80% chance of activity with varying intensity
        val intensity = if (Math.random() > 0.2) {
            (0.5f + Math.random().toFloat() * 0.5f).coerceIn(0f, 1f)
        } else {
            0f
        }
        contributions[date] = intensity
    }
    
    return contributions
}

private fun generateSporadicUserContributions(): Map<LocalDate, Float> {
    val today = LocalDate.now()
    val contributions = mutableMapOf<LocalDate, Float>()
    
    for (i in 0 until 365) {
        val date = today.minusDays(i.toLong())
        // 40% chance of activity
        val intensity = if (Math.random() > 0.6) {
            (0.3f + Math.random().toFloat() * 0.7f).coerceIn(0f, 1f)
        } else {
            0f
        }
        contributions[date] = intensity
    }
    
    return contributions
}

private fun generatePerfectStreakContributions(): Map<LocalDate, Float> {
    val today = LocalDate.now()
    val contributions = mutableMapOf<LocalDate, Float>()
    
    for (i in 0 until 365) {
        val date = today.minusDays(i.toLong())
        // Always active with high intensity
        val intensity = (0.7f + Math.random().toFloat() * 0.3f).coerceIn(0f, 1f)
        contributions[date] = intensity
    }
    
    return contributions
}

private fun generateNewUserContributions(): Map<LocalDate, Float> {
    val today = LocalDate.now()
    val contributions = mutableMapOf<LocalDate, Float>()
    
    for (i in 0 until 30) {
        val date = today.minusDays(i.toLong())
        // Recent activity only, high enthusiasm
        val intensity = if (Math.random() > 0.15) {
            (0.6f + Math.random().toFloat() * 0.4f).coerceIn(0f, 1f)
        } else {
            0f
        }
        contributions[date] = intensity
    }
    
    return contributions
}
