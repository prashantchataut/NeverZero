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
    val xxxs: Dp = 2.dp      // Micro spacing (Sub-grid)
    val xxs: Dp = 4.dp       // Extra extra small (Half-grid)
    val xs: Dp = 8.dp        // Extra small (1x Grid)
    val sm: Dp = 8.dp        // Small (Aliased to 1x Grid to avoid 12dp)
    val md: Dp = 16.dp       // Medium (2x Grid)
    val lg: Dp = 24.dp       // Large (3x Grid)
    val xl: Dp = 32.dp       // Extra large (4x Grid)
    val xxl: Dp = 40.dp      // Extra extra large (5x Grid)
    val xxxl: Dp = 48.dp     // Huge (6x Grid)
    val xxxxl: Dp = 64.dp    // Massive (8x Grid)
    val xxxxxl: Dp = 80.dp   // Gigantic (10x Grid)
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

// ==================== TOUCH TARGET TOKENS ====================
/**
 * Minimum touch target sizes for accessibility
 * Following Material 3 and WCAG guidelines
 */
object TouchTarget {
    val minimum: Dp = 48.dp        // Minimum accessible touch target
    val recommended: Dp = 56.dp    // Recommended comfortable size
    val large: Dp = 64.dp          // Large, easy-to-hit targets
}

// ==================== CONTAINER TOKENS ====================
/**
 * Standard container dimensions for consistent layouts
 */
object Container {
    // Card dimensions
    val cardMinWidth: Dp = 280.dp
    val cardMaxWidth: Dp = 560.dp
    val cardMinHeight: Dp = 80.dp
    
    // Dialog dimensions
    val dialogMinWidth: Dp = 280.dp
    val dialogMaxWidth: Dp = 560.dp
    val dialogPadding: Dp = 24.dp
    
    // Bottom sheet dimensions
    val bottomSheetMaxWidth: Dp = 640.dp
    val bottomSheetHandleWidth: Dp = 32.dp
    val bottomSheetHandleHeight: Dp = 4.dp
    
    // List item heights
    val listItemMinHeight: Dp = 56.dp
    val listItemMediumHeight: Dp = 72.dp
    val listItemLargeHeight: Dp = 88.dp
}
object Insets {
    val screenHorizontal: Dp = 16.dp
    val screenVertical: Dp = 16.dp
    val screenTop: Dp = 8.dp
    val screenBottom: Dp = 16.dp
    val contentHorizontal: Dp = 20.dp
}

// ==================== TYPOGRAPHY SPACING ====================
/**
 * Spacing specifically for text content
 */
object TextSpacing {
    val paragraphSpacing: Dp = 16.dp
    val lineSpacing: Dp = 8.dp
    val wordSpacing: Dp = 4.dp
    val captionSpacing: Dp = 4.dp
}

// ==================== GRID SYSTEM ====================
/**
 * Grid layout specifications
 */
object Grid {
    val columns: Int = 4           // Mobile columns
    val tabletColumns: Int = 8     // Tablet columns
    val desktopColumns: Int = 12   // Desktop columns
    val gutter: Dp = 16.dp         // Space between columns
    val margin: Dp = 16.dp         // Screen edge margins
}

// ==================== Z-INDEX TOKENS ====================
/**
 * Z-index layering for component stacking
 */
object ZIndex {
    const val background = 0f
    const val content = 1f
    const val surface = 2f
    const val elevated = 3f
    const val overlay = 4f
    const val modal = 5f
    const val popover = 6f
    const val tooltip = 7f
    const val notification = 8f
    const val maximum = 9f
}

