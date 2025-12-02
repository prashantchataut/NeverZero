package com.productivitystreak.ui.screens.stats.components

import androidx.compose.foundation.layout.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import com.productivitystreak.ui.state.stats.StatsState

@Composable
fun StoryCardsSection(statsState: StatsState) {
    var currentStoryIndex by remember { mutableStateOf(0) }
    
    val stories = remember(statsState) {
        buildList {
            // Weekly Summary Story
            add(
                StoryInsight(
                    id = "weekly_summary",
                    title = "This Week",
                    type = StoryType.WEEKLY_SUMMARY,
                    content = {
                        WeeklySummaryContent(
                            completionRate = statsState.averageDailyProgressPercent,
                            streak = statsState.currentLongestStreak
                        )
                    }
                )
            )
            
            // Consistency Score Story
            val topConsistency = statsState.streakConsistency.maxByOrNull { it.score }
            if (topConsistency != null) {
                add(
                    StoryInsight(
                        id = "consistency",
                        title = "Consistency",
                        type = StoryType.CONSISTENCY_SCORE,
                        content = {
                            ConsistencyStoryContent(score = topConsistency)
                        }
                    )
                )
            }
            
            // Streak Trend Story
            statsState.averageDailyTrend?.let { trend ->
                add(
                    StoryInsight(
                        id = "trend",
                        title = "Your Progress",
                        type = StoryType.STREAK_TREND,
                        content = {
                            TrendStoryContent(trend = trend)
                        }
                    )
                )
            }
        }
    }
    
    if (stories.isNotEmpty()) {
        StoryCard(
            title = stories[currentStoryIndex].title,
            content = stories[currentStoryIndex].content,
            onShare = {
                // TODO: Implement share functionality
            },
            onNext = {
                if (currentStoryIndex < stories.size - 1) {
                    currentStoryIndex++
                }
            },
            onPrevious = {
                if (currentStoryIndex > 0) {
                    currentStoryIndex--
                }
            },
            currentIndex = currentStoryIndex,
            totalStories = stories.size,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
