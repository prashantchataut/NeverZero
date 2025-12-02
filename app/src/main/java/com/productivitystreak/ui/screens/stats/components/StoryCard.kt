package com.productivitystreak.ui.screens.stats.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing
import kotlinx.coroutines.delay

/**
 * Story Card - Instagram Stories-style insight card
 * Features: Swipeable, auto-advance, progress indicators
 */
@Composable
fun StoryCard(
    title: String,
    content: @Composable () -> Unit,
    onShare: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    currentIndex: Int,
    totalStories: Int,
    modifier: Modifier = Modifier
) {
    val designColors = NeverZeroTheme.designColors
    var isPaused by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    
    // Auto-advance timer
    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused) {
            while (progress < 1f) {
                delay(50) // Update every 50ms
                progress += 0.01f // 5 seconds total (50ms * 100 steps)
            }
            if (progress >= 1f) {
                progress = 0f
                onNext()
            }
        }
    }
    
    // Reset progress when story changes
    LaunchedEffect(currentIndex) {
        progress = 0f
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        designColors.surface,
                        designColors.backgroundAlt
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        // Tap left third = previous, right third = next
                        if (offset.x < size.width / 3) {
                            onPrevious()
                        } else if (offset.x > size.width * 2 / 3) {
                            onNext()
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Progress Indicators
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(totalStories) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(
                                    when {
                                        index < currentIndex -> designColors.primary
                                        index == currentIndex -> Color.Transparent
                                        else -> designColors.border.copy(alpha = 0.3f)
                                    }
                                )
                        ) {
                            // Animated progress for current story
                            if (index == currentIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .fillMaxHeight()
                                        .background(designColors.primary)
                                )
                            }
                        }
                    }
                }
                
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = designColors.textPrimary
                    )
                    
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = designColors.primary
                        )
                    }
                }
            }
            
            // Middle: Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
            
            // Bottom: Hint
            Text(
                text = "Tap left/right to navigate • Hold to pause",
                style = MaterialTheme.typography.labelSmall,
                color = designColors.textSecondary.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * Story data class
 */
data class StoryInsight(
    val id: String,
    val title: String,
    val type: StoryType,
    val content: @Composable () -> Unit
)

enum class StoryType {
    WEEKLY_SUMMARY,
    MILESTONE,
    CONSISTENCY_SCORE,
    STREAK_TREND
}
