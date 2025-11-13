package com.productivitystreak.ui.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.Motion
import kotlinx.coroutines.launch

/**
 * Advanced Animation Utilities
 * Physics-based and creative animations
 */

/**
 * Press and release animation with haptic feedback
 */
fun Modifier.pressAnimation(
    pressScale: Float = 0.95f,
    hapticFeedback: HapticFeedbackManager? = null
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressScale"
    )

    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    hapticFeedback?.lightTap()
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = {
                    hapticFeedback?.mediumImpact()
                }
            )
        }
}

/**
 * Bouncy scale animation on appearance
 */
fun Modifier.bouncyAppearance(
    delayMillis: Int = 0,
    durationMillis: Int = Motion.durationLong
): Modifier = composed {
    var isVisible by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bouncyScale"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        isVisible = true
    }

    this.scale(scale)
}

/**
 * Ripple effect animation
 */
fun Modifier.rippleEffect(
    color: Color,
    radius: Float = 100f
): Modifier = composed {
    var ripples by remember { mutableStateOf(listOf<RippleData>()) }
    val coroutineScope = rememberCoroutineScope()

    this
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                val ripple = RippleData(
                    offset = offset,
                    startTime = System.currentTimeMillis()
                )
                ripples = ripples + ripple

                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    ripples = ripples.filter { it != ripple }
                }
            }
        }
        .drawBehind {
            ripples.forEach { ripple ->
                val progress = ((System.currentTimeMillis() - ripple.startTime) / 500f).coerceIn(
                    0f,
                    1f
                )
                val currentRadius = radius * progress
                val alpha = 1f - progress

                drawCircle(
                    color = color.copy(alpha = alpha * 0.3f),
                    radius = currentRadius,
                    center = ripple.offset
                )
            }
        }
}

private data class RippleData(
    val offset: Offset,
    val startTime: Long
)

/**
 * Shimmer effect for loading states
 */
fun Modifier.shimmerEffect(
    colors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.3f)
    ),
    durationMillis: Int = 1200
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val xShimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "xShimmer"
    )

    this.drawBehind {
        drawShimmer(xShimmer, colors)
    }
}

private fun DrawScope.drawShimmer(offset: Float, colors: List<Color>) {
    val brush = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = colors,
        start = Offset(offset - 500f, 0f),
        end = Offset(offset, size.height)
    )

    drawRect(brush = brush)
}

/**
 * Shake animation for errors
 */
fun Modifier.shakeAnimation(trigger: Boolean): Modifier = composed {
    var isShaking by remember { mutableStateOf(false) }
    val offsetX by animateFloatAsState(
        targetValue = if (isShaking) 0f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "shakeOffset"
    )

    LaunchedEffect(trigger) {
        if (trigger) {
            isShaking = true
            kotlinx.coroutines.delay(50)
            isShaking = false
        }
    }

    this.scale(scaleX = if (isShaking) 1.02f else 1f, scaleY = 1f)
}

/**
 * Pulsing animation for attention
 */
@Composable
fun rememberPulseAnimation(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    durationMillis: Int = 1000
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    return scale
}

/**
 * Rotation animation
 */
@Composable
fun rememberRotationAnimation(
    durationMillis: Int = 2000
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    return rotation
}

/**
 * Float up and fade animation
 */
fun Modifier.floatUpAnimation(
    visible: Boolean,
    durationMillis: Int = Motion.durationMedium
): Modifier = composed {
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "floatY"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis, easing = LinearEasing),
        label = "floatAlpha"
    )

    this
        .graphicsLayer {
            translationY = offsetY
            this.alpha = alpha
        }
}

/**
 * Spring animation constants for consistency
 */
object SpringAnimations {
    val Gentle = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val Bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val Snappy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )

    val Smooth = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}
