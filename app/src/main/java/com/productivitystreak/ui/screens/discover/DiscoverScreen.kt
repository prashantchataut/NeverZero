package com.productivitystreak.ui.screens.discover

// Discover UI removed during architectural sanitization.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.productivitystreak.data.model.Asset
import com.productivitystreak.data.model.AssetCategory
import com.productivitystreak.ui.state.discover.ArticleItem
import com.productivitystreak.ui.state.discover.CategoryItem
import com.productivitystreak.ui.state.discover.CommunityStory
import com.productivitystreak.ui.state.discover.DiscoverState
import com.productivitystreak.ui.state.discover.FeaturedContent
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun DiscoverScreen(
    state: DiscoverState,
    onAssetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Discover",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (state.communityStories.isEmpty() && 
            state.featuredContent.title.isBlank() && 
            state.categories.isEmpty() && 
            state.assets.isEmpty() && 
            state.articles.isEmpty()) {
            item {
                com.productivitystreak.ui.components.EmptyState(
                    icon = com.productivitystreak.ui.icons.AppIcons.Search,
                    message = "Nothing to discover yet. Check back later for new content!",
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        }

        if (state.communityStories.isNotEmpty()) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Community stories",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        itemsIndexed(state.communityStories, key = { _, story -> story.id }) { index, story ->
                            CommunityStoryAvatar(story = story, isOnline = index == 0)
                        }
                    }
                }
            }
        }

        if (state.featuredContent.title.isNotBlank()) {
            item {
                FeaturedCard(content = state.featuredContent)
            }
        }

        if (state.categories.isNotEmpty()) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Focus areas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(state.categories, key = { it.id }) { category ->
                            FocusAreaChip(category)
                        }
                    }
                }
            }
        }

        if (state.assets.isNotEmpty()) {
            item {
                AssetLibrarySection(
                    assets = state.assets,
                    onAssetClick = { asset -> onAssetSelected(asset.id) }
                )
            }
        }

        if (state.articles.isNotEmpty()) {
            item {
                Text(
                    text = "Resource feed",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            items(state.articles, key = { it.id }) { article ->
                ArticleCard(
                    article = article,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AssetLibrarySection(
    assets: List<Asset>,
    onAssetClick: (Asset) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "High-value asset library",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        val grouped = assets.groupBy { it.category }
        grouped.forEach { (category, categoryAssets) ->
            val label = when (category) {
                AssetCategory.PSYCHOLOGY_TRICKS -> "Psychology tricks"
                AssetCategory.MEMORY_TECHNIQUES -> "Memory techniques"
                AssetCategory.NEGOTIATION_SCRIPTS -> "Negotiation scripts"
                AssetCategory.MARKETING_MENTAL_MODELS -> "Marketing mental models"
                AssetCategory.BOOK_SUMMARIES -> "Book summaries"
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val rows = categoryAssets.chunked(2)
                rows.forEach { rowAssets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowAssets.forEach { asset ->
                            AssetCard(
                                asset = asset,
                                modifier = Modifier.weight(1f),
                                onClick = { onAssetClick(asset) }
                            )
                        }
                        if (rowAssets.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AssetCard(
    asset: Asset,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accent = assetCategoryColor(asset.category)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = asset.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "60–90 second read",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (asset.certified) {
                Text(
                    text = "Certified",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
        }
    }
}

private fun assetCategoryColor(category: AssetCategory): Color {
    return when (category) {
        AssetCategory.PSYCHOLOGY_TRICKS -> Color(0xFF80CBC4)
        AssetCategory.MEMORY_TECHNIQUES -> Color(0xFF81D4FA)
        AssetCategory.NEGOTIATION_SCRIPTS -> Color(0xFFFFB74D)
        AssetCategory.MARKETING_MENTAL_MODELS -> Color(0xFFBA68C8)
        AssetCategory.BOOK_SUMMARIES -> Color(0xFFAED581)
    }
}

@Composable
fun ArticleCard(
    article: ArticleItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${article.readTimeMinutes} min read",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CommunityStoryAvatar(story: CommunityStory, isOnline: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                model = story.avatarUrl,
                contentDescription = "${story.author} avatar",
                contentScale = ContentScale.Crop
            )
            if (isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.inversePrimary)
                        .align(Alignment.BottomEnd)
                )
            }
        }
        Text(
            modifier = Modifier.widthIn(max = 72.dp),
            text = story.author,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FocusAreaChip(item: CategoryItem) {
    val primary = MaterialTheme.colorScheme.primary
    val accent = remember(item.accentHex) {
        runCatching { Color(android.graphics.Color.parseColor(item.accentHex)) }
            .getOrElse { primary }
    }

    FilterChip(
        selected = false,
        onClick = {},
        label = {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.surface,
            selectedLabelColor = MaterialTheme.colorScheme.onSurface,
            selectedLeadingIconColor = accent,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = false,
            borderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun FeaturedCard(content: FeaturedContent) {
    val defaultColor = NeverZeroTheme.gradientColors.PremiumStart
    val accent = remember(content.accentHex) {
        runCatching { Color(android.graphics.Color.parseColor(content.accentHex)) }
            .getOrElse { defaultColor }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent,
                            NeverZeroTheme.gradientColors.PremiumEnd
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Spotlight",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = content.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
