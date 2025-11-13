package com.productivitystreak.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== LIGHT THEME COLORS ====================

// Primary - Main brand color (vibrant blue)
val Primary = Color(0xFF0061FE)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFD6E3FF)
val OnPrimaryContainer = Color(0xFF001A41)

// Secondary - Supporting color (modern slate)
val Secondary = Color(0xFF535F70)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFD7E3F8)
val OnSecondaryContainer = Color(0xFF101C2B)

// Tertiary - Accent color (warm purple)
val Tertiary = Color(0xFF6B5778)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFF2DAFF)
val OnTertiaryContainer = Color(0xFF251431)

// Error colors
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF410002)

// Surface colors - Clean, modern backgrounds
val Background = Color(0xFFFCFCFF)
val OnBackground = Color(0xFF1A1C1E)
val Surface = Color(0xFFFCFCFF)
val OnSurface = Color(0xFF1A1C1E)
val SurfaceVariant = Color(0xFFE1E2EC)
val OnSurfaceVariant = Color(0xFF44474F)

// Outline colors
val Outline = Color(0xFF74777F)
val OutlineVariant = Color(0xFFC4C6D0)

// ==================== DARK THEME COLORS ====================

// Primary - Lighter for dark backgrounds
val DarkPrimary = Color(0xFFAAC7FF)
val DarkOnPrimary = Color(0xFF002F65)
val DarkPrimaryContainer = Color(0xFF00468F)
val DarkOnPrimaryContainer = Color(0xFFD6E3FF)

// Secondary - Adapted for dark mode
val DarkSecondary = Color(0xFFBBC7DB)
val DarkOnSecondary = Color(0xFF253140)
val DarkSecondaryContainer = Color(0xFF3C4858)
val DarkOnSecondaryContainer = Color(0xFFD7E3F8)

// Tertiary - Warmer for dark mode
val DarkTertiary = Color(0xFFD7BEE4)
val DarkOnTertiary = Color(0xFF3B2948)
val DarkTertiaryContainer = Color(0xFF53405F)
val DarkOnTertiaryContainer = Color(0xFFF2DAFF)

// Error colors for dark mode
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

// Surface colors - Deep, rich backgrounds
val DarkBackground = Color(0xFF1A1C1E)
val DarkOnBackground = Color(0xFFE2E2E6)
val DarkSurface = Color(0xFF1A1C1E)
val DarkOnSurface = Color(0xFFE2E2E6)
val DarkSurfaceVariant = Color(0xFF44474F)
val DarkOnSurfaceVariant = Color(0xFFC4C6D0)

// Outline colors for dark mode
val DarkOutline = Color(0xFF8E9099)
val DarkOutlineVariant = Color(0xFF44474F)

// ==================== CATEGORY/STREAK ACCENT COLORS ====================
// Vibrant, modern colors for different streak categories

object StreakColors {
    // Reading - Warm coral/salmon
    val Reading = Color(0xFFFF6B6B)
    val ReadingContainer = Color(0xFFFFE5E5)
    val OnReadingContainer = Color(0xFF5C0000)

    // Vocabulary - Vibrant purple
    val Vocabulary = Color(0xFF6C5CE7)
    val VocabularyContainer = Color(0xFFEAE6FF)
    val OnVocabularyContainer = Color(0xFF1A0052)

    // Wellness - Fresh green
    val Wellness = Color(0xFF00D9A5)
    val WellnessContainer = Color(0xFFD6FFF3)
    val OnWellnessContainer = Color(0xFF003829)

    // Productivity - Energetic orange
    val Productivity = Color(0xFFFF9F43)
    val ProductivityContainer = Color(0xFFFFEFDD)
    val OnProductivityContainer = Color(0xFF4D2800)

    // Learning - Bright blue
    val Learning = Color(0xFF0095FF)
    val LearningContainer = Color(0xFFD4EBFF)
    val OnLearningContainer = Color(0xFF001D33)

    // Exercise - Dynamic magenta
    val Exercise = Color(0xFFE84393)
    val ExerciseContainer = Color(0xFFFFDDF0)
    val OnExerciseContainer = Color(0xFF470025)

    // Meditation - Calm indigo
    val Meditation = Color(0xFF5F27CD)
    val MeditationContainer = Color(0xFFE8DDFF)
    val OnMeditationContainer = Color(0xFF1F003D)

    // Creative - Sunny yellow
    val Creative = Color(0xFFFFC043)
    val CreativeContainer = Color(0xFFFFF4D6)
    val OnCreativeContainer = Color(0xFF3D2800)
}

// ==================== GRADIENT COLORS ====================
// Modern gradient combinations for visual interest

object GradientColors {
    // Sunrise gradient
    val SunriseStart = Color(0xFFFF6B6B)
    val SunriseEnd = Color(0xFFFFD93D)

    // Ocean gradient
    val OceanStart = Color(0xFF0061FE)
    val OceanEnd = Color(0xFF00D9A5)

    // Twilight gradient
    val TwilightStart = Color(0xFF6C5CE7)
    val TwilightEnd = Color(0xFFE84393)

    // Success gradient
    val SuccessStart = Color(0xFF00D9A5)
    val SuccessEnd = Color(0xFF00B894)

    // Premium gradient
    val PremiumStart = Color(0xFF0061FE)
    val PremiumEnd = Color(0xFF6C5CE7)
}

// ==================== SEMANTIC COLORS ====================
// Contextual colors for specific UI states

object SemanticColors {
    // Success
    val Success = Color(0xFF00D9A5)
    val OnSuccess = Color(0xFFFFFFFF)
    val SuccessContainer = Color(0xFFD6FFF3)
    val OnSuccessContainer = Color(0xFF003829)

    // Warning
    val Warning = Color(0xFFFF9F43)
    val OnWarning = Color(0xFFFFFFFF)
    val WarningContainer = Color(0xFFFFEFDD)
    val OnWarningContainer = Color(0xFF4D2800)

    // Info
    val Info = Color(0xFF0095FF)
    val OnInfo = Color(0xFFFFFFFF)
    val InfoContainer = Color(0xFFD4EBFF)
    val OnInfoContainer = Color(0xFF001D33)
}

// ==================== UTILITY COLORS ====================

// Overlay and scrim colors
val Scrim = Color(0xFF000000)
val ScrimTransparent = Color(0x00000000)

// Divider colors
val DividerLight = Color(0xFFE1E2EC)
val DividerDark = Color(0xFF44474F)
