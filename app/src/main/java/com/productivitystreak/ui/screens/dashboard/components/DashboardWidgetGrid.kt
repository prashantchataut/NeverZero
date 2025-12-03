package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.vocabulary.VocabularyWord

@Composable
fun DashboardWidgetGrid(
    wordOfTheDay: VocabularyWord?,
    buddhaInsight: String?,
    onTeachWordClick: () -> Unit,
    onBuddhaInsightRefresh: () -> Unit,
    onMonkModeClick: () -> Unit,
    onChallengesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row 1: Teach Word & Buddha Insight
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TeachWordWidget(
                    word = wordOfTheDay,
                    onClick = onTeachWordClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                BuddhaInsightCard(
                    insight = buddhaInsight ?: "Meditate on your goals.",
                    onRefresh = onBuddhaInsightRefresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Row 2: Monk Mode & Challenges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                MonkModeWidget(
                    onClick = onMonkModeClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                ChallengesWidget(
                    onClick = onChallengesClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
