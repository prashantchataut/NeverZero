package com.productivitystreak.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.*

/**
 * Material 3 Card Components
 * Standardized card variants following Material Design guidelines
 */

/**
 * Elevated Card - Subtle elevation with background tint
 * Best for content that needs slight emphasis
 */
/**
 * Standard padding for all cards
 */
private val CardPadding = PaddingValues(Spacing.lg)

/**
 * Elevated Card - Subtle elevation with background tint
 * Best for content that needs slight emphasis
 */
@Composable
fun ElevatedCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Shapes.medium, // Standardized to Medium (16dp)
    elevation: Dp = Elevation.level2,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.97f else 1f,
            animationSpec = MotionSpec.quickScale(),
            label = "scale"
        )

        Card(
            onClick = onClick,
            modifier = modifier.graphicsLayer { 
                scaleX = scale
                scaleY = scale
            },
            enabled = enabled,
            shape = shape,
            colors = CardDefaults.elevatedCardColors(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
            interactionSource = interactionSource,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.elevatedCardColors(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
            content = content
        )
    }
}

/**
 * Filled Card - Filled variant with surface container color
 * Best for grouping related content
 */
@Composable
fun FilledCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Shapes.medium, // Standardized to Medium (16dp)
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.97f else 1f,
            animationSpec = MotionSpec.quickScale(),
            label = "scale"
        )

        Card(
            onClick = onClick,
            modifier = modifier.graphicsLayer { 
                scaleX = scale
                scaleY = scale
            },
            enabled = enabled,
            shape = shape,
            colors = CardDefaults.cardColors(),
            elevation = CardDefaults.cardElevation(),
            interactionSource = interactionSource,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(),
            elevation = CardDefaults.cardElevation(),
            content = content
        )
    }
}

/**
 * Outlined Card - Minimal card with outline border
 * Best for secondary content or lists
 */
@Composable
fun OutlinedCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Shapes.medium, // Standardized to Medium (16dp)
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.97f else 1f,
            animationSpec = MotionSpec.quickScale(),
            label = "scale"
        )

        Card(
            onClick = onClick,
            modifier = modifier.graphicsLayer { 
                scaleX = scale
                scaleY = scale
            },
            enabled = enabled,
            shape = shape,
            colors = CardDefaults.outlinedCardColors(),
            elevation = CardDefaults.outlinedCardElevation(),
            border = BorderStroke(Border.thin, borderColor),
            interactionSource = interactionSource,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.outlinedCardColors(),
            elevation = CardDefaults.outlinedCardElevation(),
            border = BorderStroke(Border.thin, borderColor),
            content = content
        )
    }
}

/**
 * Interactive Card - Pressable card with scale animation and state feedback
 * Best for clickable items that need clear interaction feedback
 */
@Composable
fun InteractiveCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Shapes.medium, // Standardized to Medium (16dp)
    elevation: Dp = Elevation.level2,
    pressScale: Float = 0.97f,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = MotionSpec.quickScale(),
        label = "card-press-scale"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        enabled = enabled,
        shape = shape,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = elevation,
            pressedElevation = elevation + 2.dp
        ),
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * Gradient Card - Card with gradient background
 * Best for hero content or promotional materials
 */
@Composable
fun GradientCard(
    gradient: Brush,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Shapes.medium, // Standard for hero cards
    elevation: Dp = Elevation.level3,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1f,
        animationSpec = MotionSpec.quickScale(),
        label = "scale"
    )

    Surface(
        onClick = onClick ?: {},
        modifier = modifier.graphicsLayer { 
            scaleX = scale
            scaleY = scale
        },
        enabled = onClick != null && enabled,
        shape = shape,
        color = Color.Transparent,
        tonalElevation = elevation,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawRect(gradient)
                    drawContent()
                }
        ) {
            content()
        }
    }
}

/**
 * Stat Card - Card optimized for displaying statistics
 * Includes icon, title, value, and optional trend indicator
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    trend: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    if (onClick != null) {
        InteractiveCard(
            onClick = onClick,
            modifier = modifier,
            elevation = Elevation.level1
        ) {
            StatCardContent(
                title = title,
                value = value,
                subtitle = subtitle,
                icon = icon,
                trend = trend
            )
        }
    } else {
        ElevatedCard(
            modifier = modifier,
            elevation = Elevation.level1
        ) {
            StatCardContent(
                title = title,
                value = value,
                subtitle = subtitle,
                icon = icon,
                trend = trend
            )
        }
    }
}

@Composable
private fun ColumnScope.StatCardContent(
    title: String,
    value: String,
    subtitle: String?,
    icon: @Composable (() -> Unit)?,
    trend: @Composable (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(CardPadding), // Use standardized padding
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            icon?.invoke()
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.Bottom
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            trend?.invoke()
        }
        
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
