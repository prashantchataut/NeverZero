package com.productivitystreak.ui.screens.achievements

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.local.entity.AchievementEntity
import com.productivitystreak.ui.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    achievements: List<AchievementEntity>,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPoints = achievements.filter { it.isUnlocked }.sumOf { it.points }
    val unlockedCount = achievements.count { it.isUnlocked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Achievements",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "$unlockedCount/${achievements.size} Unlocked · $totalPoints Points",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(achievements, key = { it.id }) { achievement ->
                AchievementCard(achievement)
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: AchievementEntity) {
    val tierColor = when (achievement.tier) {
        "platinum" -> Color(0xFFE5E7EB)
        "gold" -> Color(0xFFFFD700)
        "silver" -> Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32) // bronze
    }

    val icon = when (achievement.icon) {
        "local_fire_department" -> Icons.Default.LocalFireDepartment
        "emoji_events" -> Icons.Default.EmojiEvents
        "menu_book" -> Icons.Default.MenuBook
        "auto_stories" -> Icons.Default.AutoStories
        "school" -> Icons.Default.School
        "edit_note" -> Icons.Default.EditNote
        else -> Icons.Default.Star
    }

    val progress = (achievement.progress.toFloat() / achievement.requirement.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .aspectRatio(0.85f)
            .alpha(if (achievement.isUnlocked) 1f else 0.6f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Tier Badge
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(tierColor)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) {
                            Brush.radialGradient(
                                listOf(
                                    tierColor.copy(alpha = 0.3f),
                                    tierColor.copy(alpha = 0.1f)
                                )
                            )
                        } else {
                            Brush.radialGradient(
                                listOf(
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (achievement.isUnlocked) {
                        tierColor
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = if (achievement.isUnlocked) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = if (achievement.isUnlocked) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress or Points
            if (achievement.isUnlocked) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = tierColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${achievement.points} pts",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = tierColor
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${achievement.progress}/${achievement.requirement}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
