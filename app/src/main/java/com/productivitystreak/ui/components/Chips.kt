package com.productivitystreak.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.*

/**
 * Material 3 Chip Components
 * Filter, suggestion, input, and assist chips following M3 design
 */

/**
 * Filter Chip - Toggle chips for filtering with checkmark
 * Best for filtering options
 */
@Composable
fun StyledFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.0f else 0.95f,
        animationSpec = MotionSpec.quickScale(),
        label = "chip-scale"
    )
    
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        enabled = enabled,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(IconSize.small)
                )
            }
        } else if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.small)
                )
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(),
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected
        )
    )
}

/**
 * Suggestion Chip - Action chips for suggestions
 * Best for quick actions or suggestions
 */
@Composable
fun SuggestionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.small)
                )
            }
        },
        colors = SuggestionChipDefaults.suggestionChipColors(),
        border = SuggestionChipDefaults.suggestionChipBorder(enabled = enabled)
    )
}

/**
 * Input Chip - Chips with avatar and remove button
 * Best for tag input or multi-select
 */
@Composable
fun StyledInputChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    avatar: ImageVector? = null,
    onRemove: (() -> Unit)? = null
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        avatar = avatar?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.small)
                )
            }
        },
        trailingIcon = if (onRemove != null) {
            {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(IconSize.medium)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(IconSize.small)
                    )
                }
            }
        } else null,
        colors = InputChipDefaults.inputChipColors(),
        border = InputChipDefaults.inputChipBorder(
            enabled = enabled,
            selected = selected
        )
    )
}

/**
 * Assist Chip - Helper chips for quick actions
 * Best for contextual actions
 */
@Composable
fun AssistChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.small)
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.small)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(),
        border = AssistChipDefaults.assistChipBorder(enabled = enabled)
    )
}

/**
 * Color Chip - Chip with custom color indicator
 * Best for color selection
 */
@Composable
fun ColorChip(
    selected: Boolean,
    onClick: () -> Unit,
    color: Color,
    label: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        animationSpec = MotionSpec.fade(),
        label = "chip-color"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (selected) color else MaterialTheme.colorScheme.outline,
        animationSpec = MotionSpec.fade(),
        label = "chip-border"
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        enabled = enabled,
        shape = Shapes.small,
        color = containerColor,
        border = BorderStroke(Border.medium, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // Color indicator
            Surface(
                modifier = Modifier.size(16.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = color,
                border = BorderStroke(Border.thin, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {}
            
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(IconSize.small),
                    tint = color
                )
            }
        }
    }
}

/**
 * Category Chip - Chip with category-specific styling
 * Best for displaying categories or tags
 */
@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    categoryColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            categoryColor.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        animationSpec = MotionSpec.fade(),
        label = "category-color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (selected) categoryColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = MotionSpec.fade(),
        label = "category-content"
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        enabled = enabled,
        shape = Shapes.full,
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xxs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // Category indicator dot
            if (selected) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = categoryColor
                ) {}
            }
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}
