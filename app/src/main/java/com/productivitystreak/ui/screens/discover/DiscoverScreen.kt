package com.productivitystreak.ui.screens.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.discover.CategoryItem
import com.productivitystreak.ui.state.discover.ChallengeItem
import com.productivitystreak.ui.state.discover.DiscoverState
import com.productivitystreak.ui.state.discover.FeaturedContent
import com.productivitystreak.ui.state.discover.SuggestionItem

@Composable
fun DiscoverScreen(state: DiscoverState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FeaturedCard(content = state.featuredContent)
        CategoryRow(categories = state.categories)
        SuggestionsSection(items = state.suggestions)
        ChallengesSection(challenges = state.communityChallenges)
    }
}

@Composable
private fun FeaturedCard(content: FeaturedContent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(content.accentHex)).copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = content.title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryRow(categories: List<CategoryItem>) {
    if (categories.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Topics to Explore", style = MaterialTheme.typography.titleMedium)
        LazyRow(contentPadding = PaddingValues(horizontal = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(categories) { category ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(android.graphics.Color.parseColor(category.accentHex)).copy(alpha = 0.16f)
                ) {
                    Text(
                        text = category.title,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionsSection(items: List<SuggestionItem>) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Suggested for You", style = MaterialTheme.typography.titleMedium)
        items.forEach { suggestion ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = suggestion.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = suggestion.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengesSection(challenges: List<ChallengeItem>) {
    if (challenges.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Community Challenges", style = MaterialTheme.typography.titleMedium)
        challenges.forEach { challenge ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = challenge.title, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = challenge.durationLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(text = "${challenge.participantCount} joined", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Preview
@Composable
private fun DiscoverScreenPreview() {
    DiscoverScreen(
        state = DiscoverState(
            featuredContent = FeaturedContent(
                id = "focus",
                title = "Reset your routines",
                description = "Use evidence-based streak tactics to stay on track.",
                accentHex = "#6C63FF"
            ),
            categories = listOf(
                CategoryItem("mindfulness", "Mindfulness", "#FF6584"),
                CategoryItem("reading", "Reading", "#6C63FF")
            ),
            suggestions = listOf(
                SuggestionItem("micro", "Try micro-habits", "Start with 2-minute wins", "#6C63FF")
            ),
            communityChallenges = listOf(
                ChallengeItem("journaling", "Weekly Journaling", "2 days left", 1372, "#6C63FF")
            )
        )
    )
}
