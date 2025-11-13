package com.productivitystreak.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design Tokens for Never Zero App
 * Centralized design system values for consistent UI
 */

// ==================== SPACING TOKENS ====================
/**
 * Spacing Scale - 4dp grid system
 * Use these tokens for consistent spacing throughout the app
 */
object Spacing {
    val none: Dp = 0.dp
    val xxxs: Dp = 2.dp      // Micro spacing
    val xxs: Dp = 4.dp       // Extra extra small
    val xs: Dp = 8.dp        // Extra small
    val sm: Dp = 12.dp       // Small
    val md: Dp = 16.dp       // Medium (most common)
    val lg: Dp = 20.dp       // Large
    val xl: Dp = 24.dp       // Extra large
    val xxl: Dp = 32.dp      // Extra extra large
    val xxxl: Dp = 40.dp     // Huge
    val xxxxl: Dp = 48.dp    // Massive
    val xxxxxl: Dp = 64.dp   // Gigantic
}

// ==================== SHAPE TOKENS ====================
/**
 * Shape Scale - Material 3 shape system
 * Consistent corner radius values
 */
object Shapes {
    // Extra Small - Chips, small buttons
    val extraSmall = RoundedCornerShape(8.dp)

    // Small - Buttons, input fields
    val small = RoundedCornerShape(12.dp)

    // Medium - Cards, dialogs
    val medium = RoundedCornerShape(16.dp)

    // Large - Large cards, bottom sheets
    val large = RoundedCornerShape(20.dp)

    // Extra Large - Hero cards, modals
    val extraLarge = RoundedCornerShape(28.dp)

    // Full - Pills, circular elements
    val full = RoundedCornerShape(50)

    // Custom shapes for specific components
    val topRounded = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val bottomRounded = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
}

// ==================== ELEVATION TOKENS ====================
/**
 * Elevation Scale - Z-axis depth
 * Material 3 elevation system
 */
object Elevation {
    val none: Dp = 0.dp
    val level0: Dp = 0.dp     // Surface level
    val level1: Dp = 1.dp     // Slightly raised (buttons)
    val level2: Dp = 3.dp     // Cards
    val level3: Dp = 6.dp     // FAB, app bar
    val level4: Dp = 8.dp     // Navigation drawer
    val level5: Dp = 12.dp    // Modal bottom sheet
    val level6: Dp = 16.dp    // Dialogs
    val level7: Dp = 24.dp    // Menu
}

// ==================== SIZE TOKENS ====================
/**
 * Component Size Tokens
 * Standard sizes for common UI elements
 */
object Size {
    // Icon sizes
    val iconSmall: Dp = 16.dp
    val iconMedium: Dp = 24.dp
    val iconLarge: Dp = 32.dp
    val iconExtraLarge: Dp = 48.dp

    // Button heights
    val buttonSmall: Dp = 32.dp
    val buttonMedium: Dp = 40.dp
    val buttonLarge: Dp = 56.dp

    // Card sizes
    val cardMinHeight: Dp = 72.dp
    val cardImageHeight: Dp = 200.dp

    // Bottom navigation
    val bottomNavHeight: Dp = 80.dp

    // Avatar sizes
    val avatarSmall: Dp = 32.dp
    val avatarMedium: Dp = 40.dp
    val avatarLarge: Dp = 56.dp
    val avatarExtraLarge: Dp = 96.dp

    // Divider
    val dividerThickness: Dp = 1.dp

    // Progress indicators
    val progressIndicatorSmall: Dp = 16.dp
    val progressIndicatorMedium: Dp = 24.dp
    val progressIndicatorLarge: Dp = 48.dp
}

// ==================== ANIMATION TOKENS ====================
/**
 * Motion Tokens - Animation durations
 * Material 3 motion system
 */
object Motion {
    // Duration in milliseconds
    const val durationInstant: Int = 50
    const val durationQuick: Int = 100
    const val durationShort: Int = 200
    const val durationMedium: Int = 300
    const val durationLong: Int = 400
    const val durationExtraLong: Int = 500
    const val durationExtended: Int = 700

    // Easing curves (for use with tween animations)
    // Standard: Most common, entering and exiting
    // Emphasized: More dramatic, important actions
    // Decelerate: Entering elements
    // Accelerate: Exiting elements
}

// ==================== OPACITY TOKENS ====================
/**
 * Opacity levels for overlays and disabled states
 */
object Opacity {
    const val disabled: Float = 0.38f
    const val medium: Float = 0.6f
    const val high: Float = 0.87f
    const val overlay: Float = 0.12f
    const val scrim: Float = 0.32f
}

// ==================== BORDER TOKENS ====================
/**
 * Border width tokens
 */
object Border {
    val none: Dp = 0.dp
    val thin: Dp = 1.dp
    val medium: Dp = 2.dp
    val thick: Dp = 4.dp
}
