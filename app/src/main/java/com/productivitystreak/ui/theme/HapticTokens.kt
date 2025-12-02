package com.productivitystreak.ui.theme

import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Haptic Feedback Tokens
 * Standardized haptic patterns for consistent tactile feedback
 */
object HapticTokens {
    // Standard interactions
    val Click = HapticFeedbackType.TextHandleMove // Light tick
    val LongPress = HapticFeedbackType.LongPress // Heavy click
    
    // Semantic interactions (mapped to available types)
    val Success = HapticFeedbackType.LongPress
    val Error = HapticFeedbackType.LongPress
    val Selection = HapticFeedbackType.TextHandleMove
    val Impact = HapticFeedbackType.LongPress
}
