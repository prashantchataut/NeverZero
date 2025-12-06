package com.productivitystreak.ui.interaction

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Advanced Haptic Feedback System
 * Provides contextual, rich haptic experiences for NeverZero
 */

enum class HapticPattern {
    // Task interactions
    TASK_COMPLETE,
    TASK_DELETE,
    TASK_EDIT,
    TASK_SWIPE,
    
    // Achievement feedback
    LEVEL_UP,
    STREAK_MILESTONE,
    ACHIEVEMENT_UNLOCK,
    BADGE_EARNED,
    
    // Navigation feedback
    NAVIGATION_TAP,
    NAVIGATION_SWIPE,
    BACK_PRESS,
    
    // RPG elements
    XP_GAIN,
    STAT_INCREASE,
    CRITICAL_HIT,
    
    // UI feedback
    SUCCESS,
    WARNING,
    ERROR,
    REFRESH,
    LOADING_COMPLETE,
    
    // Special patterns
    CELEBRATION,
    FOCUS_MODE_START,
    FOCUS_MODE_END,
    WISDOM_REVEAL
}

data class HapticConfig(
    val type: HapticFeedbackType,
    val intensity: Float = 1.0f,
    val pattern: List<Long> = emptyList(),
    val description: String
)

class HapticManager {
    private val hapticPatterns = mapOf(
        // Task interactions
        HapticPattern.TASK_COMPLETE to HapticConfig(
            type = HapticFeedbackType.LongPress,
            intensity = 1.0f,
            pattern = listOf(0, 50, 100, 50),
            description = "Satisfying completion feedback"
        ),
        HapticPattern.TASK_DELETE to HapticConfig(
            type = HapticFeedbackType.TextHandleMove,
            intensity = 0.7f,
            pattern = listOf(0, 30, 30),
            description = "Gentle deletion confirmation"
        ),
        HapticPattern.TASK_EDIT to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.8f,
            pattern = listOf(0, 25),
            description = "Quick edit initiation"
        ),
        HapticPattern.TASK_SWIPE to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.6f,
            pattern = listOf(0, 15),
            description = "Smooth swipe feedback"
        ),
        
        // Achievement feedback
        HapticPattern.LEVEL_UP to HapticConfig(
            type = HapticFeedbackType.LongPress,
            intensity = 1.0f,
            pattern = listOf(0, 100, 50, 100, 50, 200),
            description = "Epic level up celebration"
        ),
        HapticPattern.STREAK_MILESTONE to HapticConfig(
            type = HapticFeedbackType.LongPress,
            intensity = 0.9f,
            pattern = listOf(0, 80, 40, 80, 40),
            description = "Milestone achievement"
        ),
        HapticPattern.ACHIEVEMENT_UNLOCK to HapticConfig(
            type = HapticFeedbackType.LongPress,
            intensity = 1.0f,
            pattern = listOf(0, 150, 50, 150),
            description = "Achievement unlocked"
        ),
        HapticPattern.BADGE_EARNED to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.8f,
            pattern = listOf(0, 60, 30),
            description = "Badge earned"
        ),
        
        // Navigation feedback
        HapticPattern.NAVIGATION_TAP to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.5f,
            pattern = listOf(0, 20),
            description = "Soft navigation tap"
        ),
        HapticPattern.NAVIGATION_SWIPE to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.4f,
            pattern = listOf(0, 15),
            description = "Gentle swipe feedback"
        ),
        HapticPattern.BACK_PRESS to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.6f,
            pattern = listOf(0, 25),
            description = "Back confirmation"
        ),
        
        // RPG elements
        HapticPattern.XP_GAIN to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.7f,
            pattern = listOf(0, 30, 20),
            description = "XP gained"
        ),
        HapticPattern.STAT_INCREASE to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.8f,
            pattern = listOf(0, 40),
            description = "Stat increased"
        ),
        HapticPattern.CRITICAL_HIT to HapticConfig(
            type = HapticFeedbackType.LongPress,
            intensity = 1.0f,
            pattern = listOf(0, 200),
            description = "Critical success"
        ),
        
        // UI feedback
        HapticPattern.SUCCESS to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.8f,
            pattern = listOf(0, 50),
            description = "Success confirmation"
        ),
        HapticPattern.WARNING to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.6f,
            pattern = listOf(0, 40, 40),
            description = "Warning alert"
        ),
        HapticPattern.ERROR to HapticConfig(
            type = HapticFeedbackType.LongPress,
            intensity = 0.9f,
            pattern = listOf(0, 100, 50),
            description = "Error notification"
        ),
        HapticPattern.REFRESH to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.5f,
            pattern = listOf(0, 25),
            description = "Refresh action"
        ),
        HapticPattern.LOADING_COMPLETE to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.7f,
            pattern = listOf(0, 30, 20),
            description = "Loading finished"
        ),
        
        // Special patterns
        HapticPattern.CELEBRATION to HapticConfig(
            type = HapticFeedbackType.LongPress,
            intensity = 1.0f,
            pattern = listOf(0, 100, 50, 100, 50, 100, 50, 200),
            description = "Full celebration"
        ),
        HapticPattern.FOCUS_MODE_START to HapticConfig(
            type = HapticFeedbackType.LongPress,
            intensity = 0.8f,
            pattern = listOf(0, 150, 100),
            description = "Focus mode activation"
        ),
        HapticPattern.FOCUS_MODE_END to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.6f,
            pattern = listOf(0, 80, 40),
            description = "Focus mode completion"
        ),
        HapticPattern.WISDOM_REVEAL to HapticConfig(
            type = HapticFeedbackType.GestureTap,
            intensity = 0.7f,
            pattern = listOf(0, 60, 30, 60),
            description = "Wisdom revealed"
        )
    )
    
    private var hapticEnabled = mutableStateOf(true)
    private var hapticIntensity = mutableStateOf(1.0f)
    
    fun setEnabled(enabled: Boolean) {
        hapticEnabled.value = enabled
    }
    
    fun setIntensity(intensity: Float) {
        hapticIntensity.value = intensity.coerceIn(0.0f, 1.0f)
    }
    
    fun shouldPlay(): Boolean = hapticEnabled.value && hapticIntensity.value > 0.0f
    
    fun getConfig(pattern: HapticPattern): HapticConfig? = hapticPatterns[pattern]
}

@Composable
fun rememberHapticManager(): HapticManager = remember { HapticManager() }

@Composable
fun HapticFeedback(
    pattern: HapticPattern,
    manager: HapticManager = rememberHapticManager(),
    hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback = LocalHapticFeedback.current
) {
    LaunchedEffect(pattern) {
        if (manager.shouldPlay()) {
            val config = manager.getConfig(pattern)
            config?.let {
                // Apply intensity scaling
                val adjustedIntensity = it.intensity * manager.hapticIntensity.value
                
                // For now, use the basic haptic types
                // In a full implementation, we'd create custom vibration patterns
                when {
                    adjustedIntensity > 0.8f -> {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    adjustedIntensity > 0.5f -> {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureTap)
                    }
                    else -> {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                }
            }
        }
    }
}

/**
 * Contextual haptic feedback composable
 */
@Composable
fun ContextualHapticFeedback(
    eventType: String,
    manager: HapticManager = rememberHapticManager()
) {
    val pattern = when (eventType.lowercase()) {
        "task_complete", "complete", "done" -> HapticPattern.TASK_COMPLETE
        "task_delete", "delete", "remove" -> HapticPattern.TASK_DELETE
        "task_edit", "edit", "modify" -> HapticPattern.TASK_EDIT
        "level_up", "levelup", "level" -> HapticPattern.LEVEL_UP
        "achievement", "unlock", "badge" -> HapticPattern.ACHIEVEMENT_UNLOCK
        "success", "completed", "finished" -> HapticPattern.SUCCESS
        "error", "failed", "mistake" -> HapticPattern.ERROR
        "warning", "alert", "caution" -> HapticPattern.WARNING
        "refresh", "reload", "update" -> HapticPattern.REFRESH
        "focus_start", "focus_on", "monk_mode" -> HapticPattern.FOCUS_MODE_START
        "focus_end", "focus_off", "monk_complete" -> HapticPattern.FOCUS_MODE_END
        "wisdom", "quote", "insight" -> HapticPattern.WISDOM_REVEAL
        "celebration", "milestone", "victory" -> HapticPattern.CELEBRATION
        else -> null
    }
    
    pattern?.let {
        HapticFeedback(pattern = it, manager = manager)
    }
}

/**
 * Advanced haptic feedback with custom patterns
 */
@Composable
fun AdvancedHapticFeedback(
    patterns: List<HapticPattern>,
    manager: HapticManager = rememberHapticManager(),
    delayBetween: Long = 100L
) {
    patterns.forEachIndexed { index, pattern ->
        LaunchedEffect(index) {
            if (index > 0) {
                delay(delayBetween)
            }
            HapticFeedback(pattern = pattern, manager = manager)
        }
    }
}
