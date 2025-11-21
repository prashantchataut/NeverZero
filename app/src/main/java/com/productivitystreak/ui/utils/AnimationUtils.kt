package com.productivitystreak.ui.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.productivitystreak.ui.theme.Duration
import com.productivitystreak.ui.theme.Easing
import com.productivitystreak.ui.theme.MotionSpec
import kotlinx.coroutines.delay

/**
 * Shared Animation Utilities
 * Reusable animation effects for consistent motion throughout the app
 */

/**
 * Shimmer effect for loading states
 * Returns an animated brush for shimmer effect
 */
@Composable
fun shimmerBrush(
    targetValue: Float = 1000f,
    showShimmer: Boolean = true,
    shimmerColors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
): Brush {
    if (!showShimmer) {
        return Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = Easing.standard
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer-transition"
    )
    
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - targetValue / 2, y = 0f),
        end = Offset(x = translateAnimation.value, y = targetValue)
    )
}

/**
 * Pulsating scale animation
 * Modifier that applies a pulsating effect to a composable
 */
fun Modifier.pulseAnimation(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    durationMillis: Int = Duration.extraLong2,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = Easing.emphasized
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-scale"
    )
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Breathing animation - slower, more subtle pulsation
 * Good for highlighting important elements
 */
fun Modifier.breathingAnimation(
    minAlpha: Float = 0.6f,
    maxAlpha: Float = 1.0f,
    durationMillis: Int = Duration.extraLong4,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this
    
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = Easing.emphasized
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing-alpha"
    )
    
    this.graphicsLayer {
        this.alpha = alpha
    }
}

/**
 * Bounce animation using spring physics
 * Best for success states and celebrations
 */
@Composable
fun rememberBounceAnimation(
    trigger: Boolean,
    targetScale: Float = 1.2f
): Float {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            scale.animateTo(
                targetValue = targetScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }
    
    return scale.value
}

/**
 * Elastic bounce for emphasis
 * Returns a scale value that bounces with elastic physics
 */
@Composable
fun rememberElasticBounce(
    trigger: Any?
): Float {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(trigger) {
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(durationMillis = Duration.short2)
        )
        scale.animateTo(
            targetValue = 1.15f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }
    
    return scale.value
}

/**
 * Slide in from bottom animation
 * Returns an offset value for sliding content in from bottom
 */
@Composable
fun rememberSlideInAnimation(
    trigger: Boolean,
    offsetDistance: Float = 100f
): Float {
    val offset = remember { Animatable(offsetDistance) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            offset.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = Duration.medium3,
                    easing = Easing.emphasizedDecelerate
                )
            )
        }
    }
    
    return offset.value
}

/**
 * Fade in animation
 * Returns an alpha value for fading content in
 */
@Composable
fun rememberFadeInAnimation(
    trigger: Boolean,
    delayMillis: Int = 0
): Float {
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            delay(delayMillis.toLong())
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = Duration.medium2,
                    easing = Easing.standard
                )
            )
        }
    }
    
    return alpha.value
}

/**
 * Stagger animation for list items
 * Returns a delay for staggered animations
 */
fun calculateStaggerDelay(
    index: Int,
    staggerDelayMillis: Int = Duration.short1
): Int {
    return index * staggerDelayMillis
}

/**
 * Rotation animation
 * Returns continuous rotation angle
 */
@Composable
fun rememberRotationAnimation(
    durationMillis: Int = Duration.extraLong4,
    enabled: Boolean = true
): Float {
    if (!enabled) return 0f
    
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation-angle"
    )
    
    return rotation
}

/**
 * Shake animation for error states
 * Returns a horizontal offset for shaking effect
 */
@Composable
fun rememberShakeAnimation(
    trigger: Boolean
): Float {
    val offset = remember { Animatable(0f) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            // Shake left and right
            repeat(3) {
                offset.animateTo(
                    targetValue = -10f,
                    animationSpec = tween(durationMillis = 50)
                )
                offset.animateTo(
                    targetValue = 10f,
                    animationSpec = tween(durationMillis = 50)
                )
            }
            offset.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        }
    }
    
    return offset.value
}

/**
 * Scale in animation with overshoot
 * Returns a scale value that overshoots then settles
 */
@Composable
fun rememberScaleInAnimation(
    trigger: Boolean,
    delayMillis: Int = 0
): Float {
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            delay(delayMillis.toLong())
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }
    
    return scale.value
}

/**
 * Glow effect animation
 * Returns a glow intensity value
 */
@Composable
fun rememberGlowAnimation(
    enabled: Boolean = true,
    minIntensity: Float = 0.3f,
    maxIntensity: Float = 1.0f,
    durationMillis: Int = Duration.long2
): Float {
    if (!enabled) return 0f
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val intensity by infiniteTransition.animateFloat(
        initialValue = minIntensity,
        targetValue = maxIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = Easing.emphasized
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow-intensity"
    )
    
    return intensity
}
