package com.productivitystreak.ui.interaction

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Advanced Gesture System for NeverZero
 * Provides intuitive interactions for task management and navigation
 */

data class GestureConfig(
    val swipeThreshold: Float = 100f,
    val longPressDelay: Long = 500L,
    val doubleTapTimeout: Long = 300L,
    val dragSensitivity: Float = 1.0f
)

class GestureState {
    var isDragging by mutableStateOf(false)
    var isLongPressing by mutableStateOf(false)
    var swipeDirection by mutableStateOf<SwipeDirection?>(null)
    var dragOffset by mutableStateOf(Offset.Zero)
    var velocity by mutableStateOf(Offset.Zero)
    
    fun reset() {
        isDragging = false
        isLongPressing = false
        swipeDirection = null
        dragOffset = Offset.Zero
        velocity = Offset.Zero
    }
}

enum class SwipeDirection {
    LEFT, RIGHT, UP, DOWN
}

sealed class GestureEvent {
    data class Swipe(val direction: SwipeDirection, val velocity: Float) : GestureEvent()
    data class LongPress(val position: Offset) : GestureEvent()
    data class DoubleTap(val position: Offset) : GestureEvent()
    data class DragStart(val position: Offset) : GestureEvent()
    data class DragUpdate(val offset: Offset, val velocity: Offset) : GestureEvent()
    data class DragEnd(val velocity: Offset) : GestureEvent()
    object Tap : GestureEvent()
}

@Composable
fun rememberGestureState(): GestureState = remember { GestureState() }

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.advancedGestures(
    state: GestureState,
    config: GestureConfig = GestureConfig(),
    onGestureEvent: (GestureEvent) -> Unit
): Modifier = this.pointerInput(Unit) {
    var dragStarted = false
    var longPressJob by mutableStateOf<kotlinx.coroutines.Job?>(null)
        @Composable { remember { mutableStateOf<kotlinx.coroutines.Job?>(null) } }.value
    
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            
            when (event.type) {
                PointerEventType.Press -> {
                    val position = event.changes.first().position
                    
                    // Start long press timer
                    longPressJob = kotlinx.coroutines.GlobalScope.launch {
                        delay(config.longPressDelay)
                        if (!dragStarted) {
                            state.isLongPressing = true
                            onGestureEvent(GestureEvent.LongPress(position))
                        }
                    }
                    
                    // Handle double tap
                    event.changes.first().consume()
                }
                
                PointerEventType.Move -> {
                    if (event.changes.isNotEmpty()) {
                        val change = event.changes.first()
                        val currentPosition = change.position
                        
                        if (!dragStarted) {
                            // Cancel long press if moved
                            longPressJob?.cancel()
                            
                            // Check if movement exceeds threshold to start drag
                            val dragDistance = currentPosition - change.previousPosition
                            if (dragDistance.getDistance() > 20f) {
                                dragStarted = true
                                state.isDragging = true
                                onGestureEvent(GestureEvent.DragStart(currentPosition))
                            }
                        } else {
                            // Update drag
                            state.dragOffset = currentPosition
                            state.velocity = change.velocity
                            onGestureEvent(GestureEvent.DragUpdate(currentPosition, change.velocity))
                        }
                        
                        change.consume()
                    }
                }
                
                PointerEventType.Release -> {
                    longPressJob?.cancel()
                    
                    if (dragStarted) {
                        // Handle drag end and potential swipe
                        val velocity = event.changes.firstOrNull()?.velocity ?: Offset.Zero
                        state.velocity = velocity
                        
                        // Determine swipe direction
                        val absVelocityX = kotlin.math.abs(velocity.x)
                        val absVelocityY = kotlin.math.abs(velocity.y)
                        
                        if (absVelocityX > config.swipeThreshold || absVelocityY > config.swipeThreshold) {
                            val direction = when {
                                absVelocityX > absVelocityY -> {
                                    if (velocity.x > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
                                }
                                else -> {
                                    if (velocity.y > 0) SwipeDirection.DOWN else SwipeDirection.UP
                                }
                            }
                            state.swipeDirection = direction
                            onGestureEvent(GestureEvent.Swipe(direction, kotlin.math.sqrt(absVelocityX * absVelocityX + absVelocityY * absVelocityY)))
                        }
                        
                        onGestureEvent(GestureEvent.DragEnd(velocity))
                    } else if (!state.isLongPressing) {
                        // Simple tap
                        onGestureEvent(GestureEvent.Tap)
                    }
                    
                    // Reset state
                    dragStarted = false
                    state.reset()
                }
            }
        }
    }
}

/**
 * Task-specific gesture modifiers
 */
fun Modifier.taskSwipeGestures(
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
): Modifier = this.advancedGestures(
    state = rememberGestureState(),
    config = GestureConfig(swipeThreshold = 150f)
) { event ->
    when (event) {
        is GestureEvent.Swipe -> {
            when (event.direction) {
                SwipeDirection.RIGHT -> onComplete()
                SwipeDirection.LEFT -> onDelete()
                SwipeDirection.UP -> onEdit()
                SwipeDirection.DOWN -> {} // Could be used for snooze
            }
        }
        is GestureEvent.LongPress -> {
            onEdit()
        }
        else -> {}
    }
}

/**
 * Navigation gestures for bottom bar and screens
 */
fun Modifier.navigationGestures(
    onBack: () -> Unit = {},
    onHome: () -> Unit = {},
    onNext: () -> Unit = {}
): Modifier = this.advancedGestures(
    state = rememberGestureState(),
    config = GestureConfig(swipeThreshold = 100f)
) { event ->
    when (event) {
        is GestureEvent.Swipe -> {
            when (event.direction) {
                SwipeDirection.RIGHT -> onBack()
                SwipeDirection.LEFT -> onNext()
                SwipeDirection.UP -> onHome()
                SwipeDirection.DOWN -> {} // Could open quick actions
            }
        }
        else -> {}
    }
}

/**
 * Quick action gestures for dashboard widgets
 */
fun Modifier.quickActionGestures(
    onRefresh: () -> Unit = {},
    onExpand: () -> Unit = {},
    onSettings: () -> Unit = {}
): Modifier = this.advancedGestures(
    state = rememberGestureState(),
    config = GestureConfig(longPressDelay = 300L)
) { event ->
    when (event) {
        is GestureEvent.DoubleTap -> onRefresh()
        is GestureEvent.LongPress -> onSettings()
        is GestureEvent.Swipe -> {
            when (event.direction) {
                SwipeDirection.UP -> onExpand()
                else -> {}
            }
        }
        else -> {}
    }
}

/**
 * Streak-specific gestures for RPG elements
 */
fun Modifier.streakGestures(
    onLevelUp: () -> Unit = {},
    onViewStats: () -> Unit = {},
    onShare: () -> Unit = {}
): Modifier = this.advancedGestures(
    state = rememberGestureState(),
    config = GestureConfig(swipeThreshold = 120f)
) { event ->
    when (event) {
        is GestureEvent.Swipe -> {
            when (event.direction) {
                SwipeDirection.UP -> onLevelUp()
                SwipeDirection.RIGHT -> onShare()
                SwipeDirection.LEFT -> onViewStats()
                SwipeDirection.DOWN -> {} // Could show history
            }
        }
        is GestureEvent.DoubleTap -> onLevelUp()
        else -> {}
    }
}
