package com.productivitystreak.ui.transition

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Advanced Transition System for NeverZero
 * Provides sophisticated screen and element transitions
 */

enum class TransitionType {
    SLIDE, FADE, SCALE, ROTATE, MORPH, EXPLODE, LIQUID, CARD_FLIP
}

data class TransitionConfig(
    val type: TransitionType,
    val duration: Int = 300,
    val easing: Easing = FastOutSlowInEasing,
    val delay: Int = 0,
    val direction: TransitionDirection = TransitionDirection.FORWARD
)

enum class TransitionDirection {
    FORWARD, BACKWARD, UP, DOWN, LEFT, RIGHT
}

/**
 * Shared element transition between screens
 */
@Composable
fun SharedElementTransition(
    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            ),
            transformOrigin = TransformOrigin.Center
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 200,
                easing = LinearEasing
            )
        ) + scaleOut(
            targetScale = 1.1f,
            animationSpec = tween(
                durationMillis = 200,
                easing = LinearEasing
            ),
            transformOrigin = TransformOrigin.Center
        )
    ) {
        content()
    }
}

/**
 * Morphing animation for state changes
 */
@Composable
fun MorphTransition(
    fromState: Any,
    toState: Any,
    content: @Composable (progress: Float) -> Unit
) {
    val transition = updateTransition(fromState, label = "morph")
    
    val progress by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 600,
                easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
            )
        },
        label = "progress"
    ) { state ->
        if (state == toState) 1f else 0f
    }
    
    content(progress)
}

/**
 * Card flip animation
 */
@Composable
fun CardFlipTransition(
    isFlipped: Boolean,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    val rotation = animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "card_flip"
    )
    
    Box(
        modifier = Modifier.graphicsLayer {
            rotationY = rotation.value
            if (rotation.value > 90f) {
                alpha = (rotation.value - 90f) / 90f
            } else {
                alpha = 1f - (rotation.value / 90f)
            }
        }
    ) {
        if (rotation.value <= 90f) {
            front()
        } else {
            Box(
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f
                }
            ) {
                back()
            }
        }
    }
}

/**
 * Liquid transition for smooth flow
 */
@Composable
fun LiquidTransition(
    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = 800,
                easing = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f)
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 600,
                easing = LinearEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(
                durationMillis = 800,
                easing = CubicBezierEasing(0.55f, 0.055f, 0.675f, 0.19f)
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 400,
                easing = LinearEasing
            )
        )
    ) {
        content()
    }
}

/**
 * Explosion transition for dramatic effects
 */
@Composable
fun ExplosionTransition(
    trigger: Boolean,
    content: @Composable () -> Unit
) {
    val scale = animateFloatAsState(
        targetValue = if (trigger) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
            visibilityThreshold = 0.01f
        ),
        label = "explosion_scale"
    )
    
    val alpha = animateFloatAsState(
        targetValue = if (trigger) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutLinearInEasing
        ),
        label = "explosion_alpha"
    )
    
    Box(
        modifier = Modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            alpha = alpha.value
        }
    ) {
        content()
    }
}

/**
 * Staggered list animation
 */
@Composable
fun StaggeredListTransition(
    items: List<Any>,
    itemContent: @Composable (item: Any, index: Int) -> Unit
) {
    items.forEachIndexed { index, item ->
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(
                    durationMillis = 400,
                    delayMillis = index * 50,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = index * 50,
                    easing = LinearEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = index * 30,
                    easing = FastOutLinearInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 200,
                    delayMillis = index * 30,
                    easing = LinearEasing
                )
            )
        ) {
            itemContent(item, index)
        }
    }
}

/**
 * Hero transition for focused elements
 */
@Composable
fun HeroTransition(
    isExpanded: Boolean,
    collapsed: @Composable () -> Unit,
    expanded: @Composable () -> Unit
) {
    val transition = updateTransition(isExpanded, label = "hero")
    
    val scale by transition.animateFloat(
        transitionSpec = {
            if (isExpanded) {
                tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            } else {
                tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing
                )
            }
        },
        label = "hero_scale"
    ) { expanded ->
        if (expanded) 2f else 1f
    }
    
    val cornerRadius by transition.animateDp(
        transitionSpec = {
            tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        },
        label = "hero_corner"
    ) { expanded ->
        if (expanded) 0.dp else 16.dp
    }
    
    Box(
        modifier = Modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
    ) {
        if (isExpanded) {
            expanded()
        } else {
            collapsed()
        }
    }
}

/**
 * Ripple transition for touch feedback
 */
@Composable
fun RippleTransition(
    trigger: Boolean,
    content: @Composable () -> Unit
) {
    val rippleScale = animateFloatAsState(
        targetValue = if (trigger) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "ripple_scale"
    )
    
    val rippleAlpha = animateFloatAsState(
        targetValue = if (trigger) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = LinearEasing
        ),
        label = "ripple_alpha"
    )
    
    Box {
        content()
        
        // Ripple effect overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = rippleScale.value
                    scaleY = rippleScale.value
                    alpha = rippleAlpha.value * 0.3f
                }
        )
    }
}

/**
 * Slide transition with direction
 */
@Composable
fun SlideTransition(
    isVisible: Boolean,
    direction: TransitionDirection = TransitionDirection.LEFT,
    content: @Composable () -> Unit
) {
    val slideOffset = when (direction) {
        TransitionDirection.LEFT -> IntOffset(-1, 0)
        TransitionDirection.RIGHT -> IntOffset(1, 0)
        TransitionDirection.UP -> IntOffset(0, -1)
        TransitionDirection.DOWN -> IntOffset(0, 1)
        else -> IntOffset(-1, 0)
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it * slideOffset.x },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + slideInVertically(
            initialOffsetY = { it * slideOffset.y },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 200,
                easing = LinearEasing
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it * slideOffset.x },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        ) + slideOutVertically(
            targetOffsetY = { it * slideOffset.y },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 200,
                easing = LinearEasing
            )
        )
    ) {
        content()
    }
}

/**
 * Reveal transition for content
 */
@Composable
fun RevealTransition(
    isVisible: Boolean,
    direction: TransitionDirection = TransitionDirection.UP,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(isVisible, label = "reveal")
    
    val clipPath by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        },
        label = "reveal_clip"
    ) { visible ->
        if (visible) 1f else 0f
    }
    
    Box(
        modifier = Modifier.graphicsLayer {
            clipPath?.let { path ->
                // Create clipping based on direction
                when (direction) {
                    TransitionDirection.UP -> {
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        scaleY = clipPath
                    }
                    TransitionDirection.DOWN -> {
                        transformOrigin = TransformOrigin(0.5f, 0f)
                        scaleY = clipPath
                    }
                    TransitionDirection.LEFT -> {
                        transformOrigin = TransformOrigin(1f, 0.5f)
                        scaleX = clipPath
                    }
                    TransitionDirection.RIGHT -> {
                        transformOrigin = TransformOrigin(0f, 0.5f)
                        scaleX = clipPath
                    }
                    else -> {
                        transformOrigin = TransformOrigin(0.5f, 0f)
                        scaleY = clipPath
                    }
                }
            }
        }
    ) {
        content()
    }
}

/**
 * Parallax transition for layered content
 */
@Composable
fun ParallaxTransition(
    scrollOffset: Float,
    layers: List<@Composable () -> Unit>
) {
    Box {
        layers.forEachIndexed { index, layer ->
            Box(
                modifier = Modifier.graphicsLayer {
                    translationY = scrollOffset * (index + 1) * 0.5f
                    alpha = 1f - (scrollOffset * 0.001f).coerceIn(0f, 0.5f)
                }
            ) {
                layer()
            }
        }
    }
}

/**
 * Crossfade with custom timing
 */
@Composable
fun AdvancedCrossfade(
    targetState: Any,
    modifier: Modifier = Modifier,
    content: @Composable (state: Any) -> Unit
) {
    val transition = updateTransition(targetState, label = "crossfade")
    
    transition.Crossfade(
        modifier = modifier,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    ) { state ->
        content(state)
    }
}

/**
 * Staggered fade for multiple elements
 */
@Composable
fun StaggeredFadeTransition(
    isVisible: Boolean,
    count: Int,
    content: @Composable (index: Int) -> Unit
) {
    if (isVisible) {
        repeat(count) { index ->
            val alpha by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 600,
                    delayMillis = index * 100,
                    easing = FastOutSlowInEasing
                ),
                label = "staggered_fade_$index"
            )
            
            Box(
                modifier = Modifier.graphicsLayer {
                    this.alpha = alpha
                }
            ) {
                content(index)
            }
        }
    }
}

/**
 * Elastic scale transition
 */
@Composable
fun ElasticScaleTransition(
    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
            visibilityThreshold = 0.01f
        ),
        label = "elastic_scale"
    )
    
    Box(
        modifier = Modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
    ) {
        content()
    }
}

/**
 * Circular reveal transition
 */
@Composable
fun CircularRevealTransition(
    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(isVisible, label = "circular_reveal")
    
    val revealRadius by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        },
        label = "reveal_radius"
    ) { visible ->
        if (visible) 1f else 0f
    }
    
    Box(
        modifier = Modifier.graphicsLayer {
            // This would need custom clipping implementation
            // For now, using scale as approximation
            val scale = revealRadius * 2f
            scaleX = scale.coerceIn(0f, 1f)
            scaleY = scale.coerceIn(0f, 1f)
            alpha = revealRadius
        }
    ) {
        content()
    }
}
