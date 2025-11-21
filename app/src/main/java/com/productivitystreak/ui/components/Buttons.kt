package com.productivitystreak.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.Border
import com.productivitystreak.ui.theme.Elevation
import com.productivitystreak.ui.theme.IconSize
import com.productivitystreak.ui.theme.MotionSpec
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Shapes
import com.productivitystreak.ui.theme.Spacing
import com.productivitystreak.ui.theme.TouchTarget

/**
 * Never Zero button kit
 * Zen-cyberpunk inspired controls with matte glass surfaces, 1px borders, gradients, and haptic cues
 */

private object NeverZeroButtonDefaults {
    val DefaultShape: Shape = Shapes.small
    val LargeShape: Shape = Shapes.large

    val ContentPadding = PaddingValues(
        horizontal = Spacing.lg,
        vertical = Spacing.sm
    )

    fun glowModifier(glowAlpha: Float, shape: Shape): Modifier = Modifier.shadow(
        elevation = 18.dp,
        shape = shape,
        clip = false,
        ambientColor = NeverZeroTheme.designColors.glow.copy(alpha = glowAlpha),
        spotColor = NeverZeroTheme.designColors.glow.copy(alpha = glowAlpha)
    )
}

private fun Modifier.neverZeroButtonBackground(
    brush: Brush,
    borderColor: Color,
    shape: Shape
): Modifier {
    return this
        .clip(shape)
        .background(brush = brush, shape = shape)
        .border(BorderStroke(Border.thin, borderColor), shape)
}

/**
 * Primary Button - Filled button with press animation
 * Best for primary actions
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    hapticEnabled: Boolean = true,
    shape: Shape = NeverZeroButtonDefaults.DefaultShape
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val designColors = NeverZeroTheme.designColors
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = MotionSpec.quickScale(),
        label = "button-scale"
    )
    val glow by animateFloatAsState(
        targetValue = if (isPressed) 0.55f else 0.35f,
        animationSpec = MotionSpec.quickFade(),
        label = "button-glow"
    )

    val gradient = remember(designColors) {
        Brush.linearGradient(listOf(designColors.primary, designColors.secondary))
    }
    val disabledGradient = remember(designColors) {
        Brush.linearGradient(listOf(designColors.primaryMuted, designColors.primaryMuted))
    }

    Button(
        onClick = {
            if (hapticEnabled) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                NeverZeroButtonDefaults.glowModifier(glowAlpha = glow, shape = shape)
            )
            .neverZeroButtonBackground(
                brush = if (enabled) gradient else disabledGradient,
                borderColor = if (enabled) designColors.border else designColors.border.copy(alpha = 0.4f),
                shape = shape
            ),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = designColors.onPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = designColors.disabled
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp),
        interactionSource = interactionSource,
        contentPadding = NeverZeroButtonDefaults.ContentPadding
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.medium)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Secondary Button - Tonal button variant
 * Best for secondary actions
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    hapticEnabled: Boolean = true,
    shape: Shape = NeverZeroButtonDefaults.DefaultShape
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val designColors = NeverZeroTheme.designColors
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = MotionSpec.quickScale(),
        label = "button-scale"
    )
    val glassBrush = remember(designColors) {
        Brush.verticalGradient(
            listOf(
                designColors.surface.copy(alpha = 0.92f),
                designColors.backgroundAlt.copy(alpha = 0.88f)
            )
        )
    }

    Button(
        onClick = {
            if (hapticEnabled) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onClick()
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neverZeroButtonBackground(
                brush = glassBrush,
                borderColor = designColors.border,
                shape = shape
            ),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = designColors.textPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = designColors.disabled
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp),
        interactionSource = interactionSource,
        contentPadding = NeverZeroButtonDefaults.ContentPadding
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.medium)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Outlined Button - Outlined style for tertiary actions
 * Best for alternative or cancel actions
 */
@Composable
fun StyledOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    shape: Shape = NeverZeroButtonDefaults.DefaultShape
) {
    val designColors = NeverZeroTheme.designColors

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.clip(shape),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = designColors.textPrimary,
            disabledContentColor = designColors.disabled
        ),
        border = BorderStroke(
            width = Border.thin,
            color = designColors.border.copy(alpha = if (enabled) 1f else 0.4f)
        ),
        contentPadding = NeverZeroButtonDefaults.ContentPadding
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.medium)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Text Button - Minimal text-only button
 * Best for low-priority actions or dialog buttons
 */
@Composable
fun StyledTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val designColors = NeverZeroTheme.designColors

    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (enabled) designColors.primary else designColors.disabled
        ),
        contentPadding = PaddingValues(
            horizontal = Spacing.md,
            vertical = Spacing.xs
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.medium)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * FAB Button - Floating action button with Material You theming
 * Best for primary floating actions
 */
@Composable
fun StyledFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    text: String? = null,
    hapticEnabled: Boolean = true,
    containerColor: Color = NeverZeroTheme.designColors.surface,
    contentColor: Color = NeverZeroTheme.designColors.textPrimary,
    isExpanded: Boolean = false
) {
    val haptics = LocalHapticFeedback.current
    
    // Rotation animation for Command Center (0° -> 45°)
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "fab-rotation"
    )
    
    if (text != null) {
        ExtendedFloatingActionButton(
            onClick = {
                if (hapticEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
            modifier = modifier
                .graphicsLayer { rotationZ = rotation },
            containerColor = containerColor,
            contentColor = contentColor,
            elevation = FloatingActionButtonDefaults.elevation()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.medium)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    } else {
        FloatingActionButton(
            onClick = {
                if (hapticEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
            modifier = modifier
                .graphicsLayer { rotationZ = rotation },
            containerColor = containerColor,
            contentColor = contentColor,
            elevation = FloatingActionButtonDefaults.elevation()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.medium)
            )
        }
    }
}

/**
 * Icon Button - Icon-only button with state layers
 * Best for toolbars and compact actions
 */
@Composable
fun StyledIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    hapticEnabled: Boolean = false
) {
    val haptics = LocalHapticFeedback.current
    
    IconButton(
        onClick = {
            if (hapticEnabled) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onClick()
        },
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(IconSize.medium)
        )
    }
}

/**
 * Filled Icon Button - Icon button with filled background
 * Best for emphasized icon actions
 */
@Composable
fun FilledIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    containerColor: Color = NeverZeroTheme.designColors.primary,
    contentColor: Color = NeverZeroTheme.designColors.onPrimary
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(IconSize.medium)
        )
    }
}

/**
 * Pill Button - Full-width button with pill shape
 * Best for bottom sheets and full-width actions
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    containerColor: Color = NeverZeroTheme.designColors.primary,
    contentColor: Color = NeverZeroTheme.designColors.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val designColors = NeverZeroTheme.designColors
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = MotionSpec.quickScale(),
        label = "pill-scale"
    )
    val gradient = remember(containerColor, designColors) {
        Brush.horizontalGradient(
            listOf(containerColor, designColors.secondary.copy(alpha = 0.9f))
        )
    }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(TouchTarget.recommended)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neverZeroButtonBackground(
                brush = gradient,
                borderColor = designColors.border,
                shape = NeverZeroButtonDefaults.LargeShape
            ),
        enabled = enabled,
        shape = NeverZeroButtonDefaults.LargeShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = designColors.disabled
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp),
        interactionSource = interactionSource
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.medium)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
