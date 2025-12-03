package com.productivitystreak.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.GradientColors
import com.productivitystreak.ui.theme.MotionSpec
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing

object GlassDefaults {
    val DefaultBorderGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.05f)
        )
    )
    
    val InteractiveBorderGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.05f)
        )
    )
    
    val PremiumBorderGradient = Brush.linearGradient(
        colors = listOf(
            GradientColors.PremiumStart.copy(alpha = 0.5f),
            GradientColors.PremiumEnd.copy(alpha = 0.2f)
        )
    )
    
    val PremiumBackgroundGradient = Brush.linearGradient(
        colors = listOf(
            GradientColors.PremiumStart.copy(alpha = 0.15f),
            GradientColors.PremiumEnd.copy(alpha = 0.05f)
        )
    )
}

/**
 * Simplified Glass Card Component
 * Clean, professional design with standard radius and solid colors
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp), // Standard 16dp
    containerColor: Color = NeverZeroTheme.designColors.surface,
    contentPadding: PaddingValues = PaddingValues(16.dp), // Standard padding
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = shape,
        border = BorderStroke(1.dp, NeverZeroTheme.designColors.border), // Solid border
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * Glow Card
 * A card with a colored glow behind it, used for high-emphasis items.
 */
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = NeverZeroTheme.designColors.primary,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = glowColor,
                spotColor = glowColor
            )
    ) {
        val borderGradient = remember(glowColor) {
            Brush.verticalGradient(
                listOf(
                    glowColor.copy(alpha = 0.5f),
                    Color.Transparent
                )
            )
        }

        if (onClick != null) {
            Surface(
                onClick = onClick,
                modifier = Modifier
                    .clip(shape)
                    .border(
                        BorderStroke(1.dp, borderGradient),
                        shape
                    ),
                color = NeverZeroTheme.designColors.surfaceElevated,
                shape = shape
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    content()
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .clip(shape)
                    .border(
                        BorderStroke(1.dp, borderGradient),
                        shape
                    ),
                color = NeverZeroTheme.designColors.surfaceElevated,
                shape = shape
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    content()
                }
            }
        }
    }
}

/**
 * Premium Glass Card - Simplified
 * Accent border for emphasis, clean background
 */
@Composable
fun PremiumGlassCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = MotionSpec.quickScale(),
        label = "premium-card-scale"
    )
    
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Surface(
        onClick = {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        color = NeverZeroTheme.designColors.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, NeverZeroTheme.designColors.primary.copy(alpha = 0.3f)),
        tonalElevation = 0.dp,
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * Interactive Glass Card - Simplified with press animation
 */
@Composable
fun InteractiveGlassCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = NeverZeroTheme.designColors.surface,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = MotionSpec.quickScale(),
        label = "glass-card-scale"
    )
    
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    
    Surface(
        onClick = {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        color = containerColor,
        shape = shape,
        border = BorderStroke(1.dp, NeverZeroTheme.designColors.border),
        tonalElevation = 0.dp,
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}
