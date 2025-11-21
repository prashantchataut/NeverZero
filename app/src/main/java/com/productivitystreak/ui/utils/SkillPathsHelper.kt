package com.productivitystreak.ui.utils

import com.productivitystreak.data.model.*
import com.productivitystreak.ui.state.skills.SkillPathsState

object SkillPathsHelper {
    
    // Predefined skill paths
    private fun getAvailablePaths(): List<SkillPath> = listOf(
        SkillPath(
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
        ),
        SkillPath(
            id = "zen",
            name = "Zen Master",
            description = "Cultivate inner peace through mindfulness.",
            category = "Mindfulness",
            colorHex = "#4CAF50",
            levels = listOf(
                Badge("z1", "Seeker", "Meditate for 7 days", "self_improvement", BadgeRequirementType.TOTAL_DAYS, 7),
                Badge("z2", "Monk", "Meditate for 30 days", "spa", BadgeRequirementType.TOTAL_DAYS, 30),
                Badge("z3","Master", "Meditate for 100 days", "psychology", BadgeRequirementType.TOTAL_DAYS, 100)
            )
        ),
        SkillPath(
            id = "athlete",
            name = "The Athlete",
            description = "Build physical strength through consistent habits.",
            category = "Fitness",
            colorHex = "#FF5722",
            levels = listOf(
                Badge("a1", "Starter", "Exercise for 7 days", "fitness_center", BadgeRequirementType.TOTAL_DAYS, 7),
                Badge("a2", "Regular", "Exercise for 30 days", "directions_run", BadgeRequirementType.TOTAL_DAYS, 30),
                Badge("a3", "Champion", "Exercise for 100 days", "emoji_events", BadgeRequirementType.TOTAL_DAYS, 100)
            )
        )
    )
    
    fun computeSkillPathsState(streaks: List<Streak>): SkillPathsState {
        val paths = getAvailablePaths()
        val pathsProgress = paths.map { path ->
            computePathProgress(path, streaks)
        }
        return SkillPathsState(pathsProgress = pathsProgress)
    }
    
    private fun computePathProgress(path: SkillPath, streaks: List<Streak>): SkillPathProgress {
        // Find streaks matching this path's category
        val relevantStreaks = streaks.filter { it.category.equals(path.category, ignoreCase = true) }
        
        // Calculate total days across all relevant streaks
        val totalDays = relevantStreaks.sumOf { it.history.size }
        
        // Find current level based on total days
        var currentLevelIndex = -1
        val earnedBadges = mutableListOf<UserBadge>()
        
        path.levels.forEachIndexed { index, badge ->
            if (totalDays >= badge.requirementValue) {
                currentLevelIndex = index
                earnedBadges.add(UserBadge(badge.id, System.currentTimeMillis()))
            }
        }
        
        // Determine next badge and progress
        val nextBadge = path.levels.getOrNull(currentLevelIndex + 1)
        val progressToNext = if (nextBadge != null) {
            (totalDays.toFloat() / nextBadge.requirementValue.toFloat()).coerceAtMost(1f)
        } else {
            1f // Completed all badges
        }
        
        return SkillPathProgress(
            path = path,
            currentLevelIndex = currentLevelIndex,
            nextBadge = nextBadge,
            progressToNext = progressToNext,
            earnedBadges = earnedBadges
        )
    }
}
