package com.productivitystreak.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.*

/**
 * Modern elevated card with Material 3 design
 * Primary reusable card component
 */
@Composable
fun NeverZeroCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = Elevation.level2),
    content: @Composable ColumnScope.() -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 0.97f else 1f,
        animationSpec = tween(durationMillis = Motion.durationShort),
        label = "cardScale"
    )

    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            colors.containerColor
        },
        animationSpec = tween(durationMillis = Motion.durationMedium),
        label = "cardColor"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .then(
                if (onClick != null) {
                    Modifier.clickable(enabled = enabled) { onClick() }
                } else Modifier
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = if (selected) {
            CardDefaults.cardElevation(defaultElevation = Elevation.level3)
        } else elevation,
        shape = Shapes.medium,
        content = content
    )
}

/**
 * Gradient card with modern visual appeal
 * Perfect for hero sections and featured content
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(Shapes.large)
            .background(
                brush = Brush.linearGradient(colors = gradientColors)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        content = content
    )
}

/**
 * Outlined card with subtle border
 * Perfect for secondary content
 */
@Composable
fun OutlinedNeverZeroCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(
                width = Border.thin,
                color = borderColor,
                shape = Shapes.medium
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = Shapes.medium,
        content = content
    )
}

/**
 * Streak card with category-specific colors
 * Displays streak progress with visual appeal
 */
@Composable
fun StreakCard(
    title: String,
    currentStreak: Int,
    longestStreak: Int,
    streakColor: Color,
    containerColor: Color,
    onContainerColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    selected: Boolean = false,
    sparklineData: List<Int> = emptyList()
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = tween(durationMillis = Motion.durationShort),
        label = "streakCardScale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) Elevation.level3 else Elevation.level2
        ),
        shape = Shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            // Category badge
            Surface(
                color = streakColor,
                shape = Shapes.small,
                modifier = Modifier.padding(bottom = Spacing.xs)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Current streak
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$currentStreak",
                    style = MaterialTheme.typography.displaySmall,
                    color = onContainerColor
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = "days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onContainerColor.copy(alpha = Opacity.medium)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxs))

            // Longest streak
            Text(
                text = "Longest: $longestStreak days",
                style = MaterialTheme.typography.bodySmall,
                color = onContainerColor.copy(alpha = Opacity.high)
            )

            // Sparkline chart (if data available)
            if (sparklineData.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                // Sparkline visualization would go here
                // This would be a custom Canvas drawing or chart library
            }
        }
    }
}

/**
 * Info card with icon and content
 * Perfect for tips, warnings, and informational messages
 */
@Composable
fun InfoCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = Shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(Size.iconLarge)
                    .clip(Shapes.small)
                    .background(contentColor.copy(alpha = Opacity.overlay)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = Opacity.high)
                )
            }
        }
    }
}

/**
 * Stat card for displaying metrics
 * Clean, focused presentation of numbers
 */
@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    icon: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = Shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(Size.iconLarge)
                        .clip(Shapes.small)
                        .background(accentColor.copy(alpha = Opacity.overlay)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
