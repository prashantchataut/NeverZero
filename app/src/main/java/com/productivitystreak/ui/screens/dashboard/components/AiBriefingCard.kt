package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.GlassCard
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing
import kotlinx.coroutines.delay

/**
 * AI Briefing Card - Chat-style interface
 * Features typing animation for daily briefing with "Reply" button
 */
@Composable
fun AiBriefingCard(
    briefing: String?,
    isLoading: Boolean,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val designColors = NeverZeroTheme.designColors
    
    // Typing animation state
    var displayedText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    
    // Trigger typing animation when briefing changes
    LaunchedEffect(briefing) {
        if (briefing != null && briefing.isNotEmpty()) {
            isTyping = true
            displayedText = ""
            
            // Type out the text character by character
            briefing.forEachIndexed { index, char ->
                displayedText = briefing.substring(0, index + 1)
                delay(15) // Typing speed
            }
            
            isTyping = false
        } else {
            displayedText = ""
        }
    }
    
    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor-blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor-alpha"
    )
    
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = com.productivitystreak.ui.icons.AppIcons.Mentor,
                        contentDescription = null,
                        tint = designColors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Buddha's Insight",
                        style = MaterialTheme.typography.labelLarge,
                        color = designColors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Status indicator
                if (isLoading || isTyping) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            val delay = index * 150
                            val dotAlpha = remember {
                                Animatable(0.3f)
                            }
                            
                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(delay.toLong())
                                    dotAlpha.animateTo(
                                        1f,
                                        animationSpec = tween(300)
                                    )
                                    dotAlpha.animateTo(
                                        0.3f,
                                        animationSpec = tween(300)
                                    )
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .alpha(dotAlpha.value)
                                    .background(
                                        color = designColors.primary,
                                        shape = RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                }
            }
            
            // Chat Bubble
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                designColors.primary.copy(alpha = 0.08f),
                                designColors.primary.copy(alpha = 0.04f)
                            )
                        )
                    )
                    .padding(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isLoading) {
                            "Contemplating your journey..."
                        } else if (displayedText.isNotEmpty()) {
                            displayedText
                        } else {
                            "The path reveals itself to those who persist."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = designColors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Blinking cursor during typing
                    if (isTyping) {
                        Text(
                            text = "▌",
                            style = MaterialTheme.typography.bodyMedium,
                            color = designColors.primary,
                            modifier = Modifier.alpha(cursorAlpha)
                        )
                    }
                }
            }
            
            // Reply Button
            TextButton(
                onClick = onReplyClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Chat,
                    contentDescription = null,
                    tint = designColors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xxs))
                Text(
                    text = "Seek Wisdom",
                    style = MaterialTheme.typography.labelMedium,
                    color = designColors.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
