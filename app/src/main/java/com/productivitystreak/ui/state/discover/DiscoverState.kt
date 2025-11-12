package com.productivitystreak.ui.state.discover

data class DiscoverState(
    val featuredContent: FeaturedContent = FeaturedContent(),
    val categories: List<CategoryItem> = emptyList(),
    val suggestions: List<SuggestionItem> = emptyList(),
    val communityChallenges: List<ChallengeItem> = emptyList()
)

data class FeaturedContent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val accentHex: String = "#6C63FF"
)

data class CategoryItem(
    val id: String,
    val title: String,
    val accentHex: String
)

data class SuggestionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val accentHex: String
)

data class ChallengeItem(
    val id: String,
    val title: String,
    val durationLabel: String,
    val participantCount: Int,
    val accentHex: String
)
