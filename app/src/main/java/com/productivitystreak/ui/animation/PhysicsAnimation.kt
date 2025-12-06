package com.productivitystreak.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * Physics-Based Animation System
 * Provides natural, realistic motion for NeverZero UI elements
 */

data class PhysicsConfig(
    val damping: Float = 0.8f,
    val stiffness: Float = 300f,
    val mass: Float = 1f,
    val gravity: Float = 9.8f,
    val friction: Float = 0.95f,
    val restitution: Float = 0.6f // Bounciness
)

class PhysicsState {
    var position by mutableStateOf(Offset.Zero)
    var velocity by mutableStateOf(Offset.Zero)
    var acceleration by mutableStateOf(Offset.Zero)
    var rotation by mutableStateOf(0f)
    var scale by mutableStateOf(1f)
    var alpha by mutableStateOf(1f)
    
    fun applyForce(force: Offset) {
        acceleration += force / PhysicsConfig().mass
    }
    
    fun update(deltaTime: Float, config: PhysicsConfig) {
        // Update velocity with acceleration
        velocity += acceleration * deltaTime
        
        // Apply friction
        velocity *= config.friction
        
        // Update position with velocity
        position += velocity * deltaTime
        
        // Apply gravity
        acceleration = Offset(0f, config.gravity)
        
        // Apply damping to rotation
        rotation *= config.damping
        
        // Apply damping to scale
        scale = 1f + (scale - 1f) * config.damping
        
        // Apply damping to alpha
        alpha = 1f + (alpha - 1f) * config.damping
    }
    
    fun reset() {
        position = Offset.Zero
        velocity = Offset.Zero
        acceleration = Offset.Zero
        rotation = 0f
        scale = 1f
        alpha = 1f
    }
}

@Composable
fun rememberPhysicsState(): PhysicsState = remember { PhysicsState() }

/**
 * Spring physics animation
 */
@Composable
fun springPhysicsAnimation(
    targetValue: Float,
    config: PhysicsConfig = PhysicsConfig(),
    onValue: (Float) -> Unit = {}
): Float {
    val state = remember { PhysicsState() }
    val animatable = remember { Animatable(0f) }
    
    LaunchedEffect(targetValue) {
        val springSpec = SpringSpec<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = config.stiffiness,
            visibilityThreshold = 0.01f
        )
        
        animatable.animateTo(targetValue, springSpec)
    }
    
    return animatable.value
}

/**
 * Momentum scrolling with friction
 */
fun Modifier.momentumScroll(
    state: PhysicsState,
    config: PhysicsConfig = PhysicsConfig(),
    onScroll: (Offset) -> Unit = {}
): Modifier = this.pointerInput(Unit) {
    var isDragging = false
    var lastPosition = Offset.Zero
    var velocity = Offset.Zero
    
    detectDragGestures(
        onDragStart = { offset ->
            isDragging = true
            lastPosition = offset
            state.velocity = Offset.Zero
        },
        onDragEnd = {
            isDragging = false
            state.velocity = velocity
        },
        onDragCancel = {
            isDragging = false
        },
        onDrag = { change ->
            val currentPosition = change.position
            val delta = currentPosition - lastPosition
            
            // Calculate velocity
            velocity = delta / 16f // Assuming 60fps
            
            // Update physics state
            state.position += delta
            state.velocity = velocity
            
            lastPosition = currentPosition
            onScroll(delta)
        }
    )
}

/**
 * Bounce animation for drops
 */
@Composable
fun bounceAnimation(
    isActive: Boolean,
    config: PhysicsConfig = PhysicsConfig(),
    onComplete: () -> Unit = {}
): Float {
    val animatable = remember { Animatable(0f) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = {
                        // Custom bounce easing
                        val t = it
                        if (t < 0.5f) {
                            2f * t * t
                        } else {
                            1f - 2f * (1f - t) * (1f - t)
                        }
                    }
                )
            )
            onComplete()
        }
    }
    
    return animatable.value
}

/**
 * Collision detection for drag operations
 */
data class CollisionBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

class CollisionSystem {
    fun checkCollision(
        pos1: Offset,
        size1: Offset,
        pos2: Offset,
        size2: Offset
    ): Boolean {
        return pos1.x < pos2.x + size2.x &&
               pos1.x + size1.x > pos2.x &&
               pos1.y < pos2.y + size2.y &&
               pos1.y + size1.y > pos2.y
    }
    
    fun resolveCollision(
        state1: PhysicsState,
        state2: PhysicsState,
        config: PhysicsConfig
    ) {
        // Calculate relative velocity
        val relativeVelocity = state1.velocity - state2.velocity
        
        // Calculate collision normal
        val collisionNormal = (state1.position - state2.position).normalize()
        
        // Calculate relative velocity along collision normal
        val velocityAlongNormal = relativeVelocity.dot(collisionNormal)
        
        // Don't resolve if velocities are separating
        if (velocityAlongNormal > 0) return
        
        // Calculate restitution
        val e = config.restitution
        
        // Calculate impulse scalar
        val j = -(1 + e) * velocityAlongNormal
        val impulse = collisionNormal * j
        
        // Apply impulse
        state1.velocity += impulse
        state2.velocity -= impulse
    }
}

private fun Offset.dot(other: Offset): Float = this.x * other.x + this.y * other.y

private fun Offset.normalize(): Offset {
    val length = sqrt(x * x + y * y)
    return if (length > 0) Offset(x / length, y / length) else Offset.Zero
}

/**
 * Gravity simulation for falling elements
 */
@Composable
fun gravityAnimation(
    isActive: Boolean,
    config: PhysicsConfig = PhysicsConfig(),
    onPositionUpdate: (Offset) -> Unit = {}
): Offset {
    val state = rememberPhysicsState()
    val density = LocalDensity.current
    
    LaunchedEffect(isActive) {
        if (isActive) {
            val startTime = System.nanoTime()
            var lastTime = startTime
            
            while (true) {
                val currentTime = System.nanoTime()
                val deltaTime = (currentTime - lastTime) / 1_000_000_000f // Convert to seconds
                
                // Apply gravity
                state.applyForce(Offset(0f, config.gravity))
                
                // Update physics
                state.update(deltaTime, config)
                
                // Convert to dp
                val positionDp = with(density) {
                    Offset(state.position.x.toDp().value, state.position.y.toDp().value)
                }
                
                onPositionUpdate(positionDp)
                
                lastTime = currentTime
                
                // Break if velocity is very small and position is stable
                if (state.velocity.getDistance() < 0.1f && state.position.y > 1000f) {
                    break
                }
                
                kotlinx.coroutines.delay(16) // ~60fps
            }
        }
    }
    
    return state.position
}

/**
 * Elastic stretch animation for interactive elements
 */
@Composable
fun elasticStretchAnimation(
    stretched: Boolean,
    config: PhysicsConfig = PhysicsConfig()
): Float {
    val animatable = remember { Animatable(1f) }
    
    LaunchedEffect(stretched) {
        val target = if (stretched) 1.2f else 1f
        val springSpec = SpringSpec<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = config.stiffness * 0.5f, // Less stiff for stretch
            visibilityThreshold = 0.001f
        )
        
        animatable.animateTo(target, springSpec)
    }
    
    return animatable.value
}

/**
 * Physics-based card flip animation
 */
@Composable
fun physicsFlipAnimation(
    isFlipped: Boolean,
    config: PhysicsConfig = PhysicsConfig()
): Float {
    val animatable = remember { Animatable(0f) }
    
    LaunchedEffect(isFlipped) {
        val target = if (isFlipped) 180f else 0f
        val springSpec = SpringSpec<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = config.stiffiness * 1.5f, // Stiffer for flip
            visibilityThreshold = 0.1f
        )
        
        animatable.animateTo(target, springSpec)
    }
    
    return animatable.value
}

/**
 * Liquid-like fill animation
 */
@Composable
fun liquidFillAnimation(
    progress: Float,
    config: PhysicsConfig = PhysicsConfig()
): Float {
    val animatable = remember { Animatable(0f) }
    
    LaunchedEffect(progress) {
        // Create wave-like animation
        val waveSpec = keyframes {
            0f at 0 using LinearEasing
            0.1f at 100 using LinearEasing
            -0.05f at 200 using LinearEasing
            0.05f at 300 using LinearEasing
            0f at 400 using LinearEasing
        }
        
        animatable.animateTo(progress, animationSpec = waveSpec)
    }
    
    return animatable.value
}

/**
 * Magnetic attraction for nearby elements
 */
class MagneticSystem {
    fun calculateMagneticForce(
        pos1: Offset,
        pos2: Offset,
        strength: Float = 100f,
        range: Float = 200f
    ): Offset {
        val distance = (pos2 - pos1)
        val distanceMagnitude = distance.getDistance()
        
        if (distanceMagnitude > range || distanceMagnitude < 1f) {
            return Offset.Zero
        }
        
        // Inverse square law for magnetic force
        val forceMagnitude = strength / (distanceMagnitude * distanceMagnitude)
        val forceDirection = distance.normalize()
        
        return forceDirection * forceMagnitude
    }
}

/**
 * Chain reaction animation for cascading effects
 */
@Composable
fun chainReactionAnimation(
    trigger: Boolean,
    delay: Long = 100,
    onReaction: (Int) -> Unit = {}
) {
    LaunchedEffect(trigger) {
        if (trigger) {
            for (i in 0..5) {
                delay(delay)
                onReaction(i)
            }
        }
    }
}

/**
 * Pendulum swing animation
 */
@Composable
fun pendulumAnimation(
    isActive: Boolean,
    config: PhysicsConfig = PhysicsConfig()
): Float {
    val animatable = remember { Animatable(0f) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            // Simulate pendulum motion
            val pendulumSpec = repeatable(
                iterations = AnimationConstants.Infinite,
                animation = tween(2000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            )
            
            animatable.animateTo(45f, pendulumSpec)
        }
    }
    
    return animatable.value
}

private fun Offset.getDistance(): Float = sqrt(x * x + y * y)
