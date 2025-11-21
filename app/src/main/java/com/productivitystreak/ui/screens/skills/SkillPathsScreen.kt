package com.productivitystreak.ui.screens.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(bottom = Spacing.xl)
        ) {
            item {
                Text(
                    text = "Master your habits",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = Spacing.sm)
                )
            }

            items(paths) { progress ->
                SkillPathCard(
                    progress = progress,
                    onClick = { /* TODO: Navigate to detail view */ }
                )
            }
        }
    }
}

@Composable
private fun rememberDummySkillPaths(): List<SkillPathProgress> {
    // This would normally come from a ViewModel
    val scholarPath = SkillPath(
        id = "scholar",
        name = "The Scholar",
        description = "For those who seek knowledge through daily reading.",
        category = "Reading",
        colorHex = "#FFD700",
        levels = listOf(
            Badge("b1", "Novice Reader", "Read for 7 days", "book", BadgeRequirementType.TOTAL_DAYS, 7),
            Badge("b2", "Bookworm", "Read for 30 days", "library_books", BadgeRequirementType.TOTAL_DAYS, 30),
            Badge("b3", "Sage", "Read for 100 days", "school", BadgeRequirementType.TOTAL_DAYS, 100)
        )
    )

    val zenPath = SkillPath(
        id = "zen",
        name = "Zen Master",
        description = "Cultivate inner peace through mindfulness.",
        category = "Mindfulness",
        colorHex = "#4CAF50",
        levels = listOf(
            Badge("z1", "Seeker", "Meditate for 7 days", "self_improvement", BadgeRequirementType.TOTAL_DAYS, 7),
            Badge("z2", "Monk", "Meditate for 30 days", "spa", BadgeRequirementType.TOTAL_DAYS, 30)
        )
    )

    return listOf(
        SkillPathProgress(
            path = scholarPath,
            currentLevelIndex = 0,
            nextBadge = scholarPath.levels[1],
            progressToNext = 0.45f,
            earnedBadges = listOf(UserBadge("b1", System.currentTimeMillis()))
        ),
        SkillPathProgress(
            path = zenPath,
            currentLevelIndex = -1,
            nextBadge = zenPath.levels[0],
            progressToNext = 0.1f,
            earnedBadges = emptyList()
        )
    )
}
