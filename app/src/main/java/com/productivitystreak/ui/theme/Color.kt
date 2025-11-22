package com.productivitystreak.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== CORE BRAND COLORS ====================

private val Ion = Color(0xFF4BF2FF)
private val Plasma = Color(0xFF8A7CFF)
private val Laser = Color(0xFF12E09F)
private val Ember = Color(0xFFFF5C4D)

private val Obsidian = Color(0xFF000000)
private val Stealth = Color(0xFF06080F)
private val MatteGlass = Color(0xFF0B111C)
private val BorderGraphite = Color(0xFF1D2738)
private val Silver = Color(0xFFC6D2E4)
private val Graphite = Color(0xFF7E8AA3)
private val Ink = Color(0xFF05070D)
private val Frost = Color(0xFFE8F6FF)

// ==================== LIGHT THEME COLORS ====================

val Primary = Ion
val OnPrimary = Ink
val PrimaryContainer = Ion.copy(alpha = 0.14f)
val OnPrimaryContainer = Ion

val Secondary = Plasma
val OnSecondary = Ink
val SecondaryContainer = Plasma.copy(alpha = 0.18f)
val OnSecondaryContainer = Plasma

val Tertiary = Laser
val OnTertiary = Ink
val TertiaryContainer = Laser.copy(alpha = 0.2f)
val OnTertiaryContainer = Laser

val Error = Ember
val OnError = Color(0xFF1A0A07)
val ErrorContainer = Ember.copy(alpha = 0.18f)
val OnErrorContainer = Ember

val Background = Color(0xFFF4F6FB)
val OnBackground = Ink
val Surface = Color(0xFFF7F9FF)
val OnSurface = Ink
val SurfaceVariant = Color(0xFFE5E9F5)
val OnSurfaceVariant = Graphite

val Outline = BorderGraphite.copy(alpha = 0.6f)
val OutlineVariant = BorderGraphite.copy(alpha = 0.35f)

// ==================== DARK THEME COLORS ====================

val DarkPrimary = Ion
val DarkOnPrimary = Obsidian
val DarkPrimaryContainer = Ion.copy(alpha = 0.22f)
val DarkOnPrimaryContainer = Frost

val DarkSecondary = Plasma
val DarkOnSecondary = Obsidian
val DarkSecondaryContainer = Plasma.copy(alpha = 0.24f)
val DarkOnSecondaryContainer = Frost

val DarkTertiary = Laser
val DarkOnTertiary = Obsidian
val DarkTertiaryContainer = Laser.copy(alpha = 0.24f)
val DarkOnTertiaryContainer = Frost

val DarkError = Ember
val DarkOnError = Obsidian
val DarkErrorContainer = Ember.copy(alpha = 0.28f)
val DarkOnErrorContainer = Frost

val DarkBackground = Obsidian
val DarkOnBackground = Silver
val DarkSurface = MatteGlass
val DarkOnSurface = Silver
val DarkSurfaceVariant = Stealth
val DarkOnSurfaceVariant = Graphite

val DarkOutline = BorderGraphite
val DarkOutlineVariant = BorderGraphite.copy(alpha = 0.5f)

// ==================== CATEGORY/STREAK ACCENT COLORS ====================
// Vibrant, modern colors for different streak categories

object StreakColors {
    // Focus Domain (Reading, Learning) -> Ion (Cyan)
    val Reading = Ion
    val ReadingContainer = Ion.copy(alpha = 0.15f)
    val OnReadingContainer = Ion.copy(alpha = 0.9f) // Darker for contrast if needed, or keep Ion for glow

    val Learning = Ion
    val LearningContainer = Ion.copy(alpha = 0.15f)
    val OnLearningContainer = Ion.copy(alpha = 0.9f)

    // Create Domain (Creative, Vocabulary) -> Plasma (Purple)
    val Vocabulary = Plasma
    val VocabularyContainer = Plasma.copy(alpha = 0.15f)
    val OnVocabularyContainer = Plasma.copy(alpha = 0.9f)

    val Creative = Plasma
    val CreativeContainer = Plasma.copy(alpha = 0.15f)
    val OnCreativeContainer = Plasma.copy(alpha = 0.9f)

    // Move Domain (Exercise, Wellness) -> Ember (Red)
    val Exercise = Ember
    val ExerciseContainer = Ember.copy(alpha = 0.15f)
    val OnExerciseContainer = Ember.copy(alpha = 0.9f)

    val Wellness = Ember
    val WellnessContainer = Ember.copy(alpha = 0.15f)
    val OnWellnessContainer = Ember.copy(alpha = 0.9f)

    // Reflect Domain (Meditation, Journal) -> Laser (Green)
    val Meditation = Laser
    val MeditationContainer = Laser.copy(alpha = 0.15f)
    val OnMeditationContainer = Laser.copy(alpha = 0.9f)

    // Productivity -> Mapped to Focus (Ion) or Create (Plasma)? Let's go with Focus (Ion)
    val Productivity = Ion
    val ProductivityContainer = Ion.copy(alpha = 0.15f)
    val OnProductivityContainer = Ion.copy(alpha = 0.9f)
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

// ==================== MATERIAL 3 TONAL SURFACES ====================
// Enhanced surface colors for Material You design

// Light theme tonal surfaces
val SurfaceDim = Color(0xFFDDD8E1)
val SurfaceBright = Color(0xFFFEF7FF)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFF7F2FA)
val SurfaceContainer = Color(0xFFF1ECF4)
val SurfaceContainerHigh = Color(0xFFEBE6EE)
val SurfaceContainerHighest = Color(0xFFE6E0E9)

// Dark theme tonal surfaces
val DarkSurfaceDim = Color(0xFF141218)
val DarkSurfaceBright = Color(0xFF3B383E)
val DarkSurfaceContainerLowest = Color(0xFF0F0D13)
val DarkSurfaceContainerLow = Color(0xFF1D1B20)
val DarkSurfaceContainer = Color(0xFF211F26)
val DarkSurfaceContainerHigh = Color(0xFF2B2930)
val DarkSurfaceContainerHighest = Color(0xFF36343B)

// ==================== INTERACTION STATE COLORS ====================
// State layer colors for interactive components

object StateLayerOpacity {
    const val hover = 0.08f
    const val focus = 0.12f
    const val pressed = 0.12f
    const val dragged = 0.16f
}

// ==================== DESIGN COLOR TOKENS ====================

data class NeverZeroDesignColors(
    val isDark: Boolean,
    val background: Color,
    val backgroundAlt: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val glow: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryMuted: Color,
    val secondary: Color,
    val onSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val disabled: Color,
    val success: Color,
    val warning: Color,
    val error: Color
)

object NeverZeroDesignPalettes {
    val Dark = NeverZeroDesignColors(
        isDark = true,
        background = Obsidian,
        backgroundAlt = Stealth,
        surface = MatteGlass,
        surfaceElevated = Color(0xFF111929),
        border = BorderGraphite,
        glow = Ion.copy(alpha = 0.35f),
        primary = Ion,
        onPrimary = Obsidian,
        primaryMuted = Ion.copy(alpha = 0.45f),
        secondary = Plasma,
        onSecondary = Obsidian,
        textPrimary = Silver,
        textSecondary = Graphite,
        disabled = Graphite.copy(alpha = 0.4f),
        success = Laser,
        warning = Color(0xFFFFC857),
        error = Ember
    )

    val Light = NeverZeroDesignColors(
        isDark = false,
        background = Background,
        backgroundAlt = Color(0xFFE8ECF6),
        surface = Color(0xFFFDFEFF),
        surfaceElevated = Color(0xFFF0F4FF),
        border = Outline,
        glow = Ion.copy(alpha = 0.2f),
        primary = Ion,
        onPrimary = Ink,
        primaryMuted = Ion.copy(alpha = 0.6f),
        secondary = Plasma,
        onSecondary = Ink,
        textPrimary = Color(0xFF0F1726),
        textSecondary = Graphite,
        disabled = Graphite.copy(alpha = 0.48f),
        success = Laser,
        warning = Color(0xFFFF9F43),
        error = Ember
    )
}

// ==================== EXTENDED SEMANTIC COLORS ====================
// Additional semantic colors for specific UI states

object ExtendedSemanticColors {
    // Focus indicator
    val Focus = Color(0xFF0061FE)
    val DarkFocus = Color(0xFFAAC7FF)
    
    // Disabled states
    val DisabledContent = Color(0xFF1C1B1F).copy(alpha = 0.38f)
    val DisabledContainer = Color(0xFF1C1B1F).copy(alpha = 0.12f)
    val DarkDisabledContent = Color(0xFFE6E1E5).copy(alpha = 0.38f)
    val DarkDisabledContainer = Color(0xFFE6E1E5).copy(alpha = 0.12f)
    
    // Loading state
    val LoadingShimmer = Color(0xFFE1E2EC)
    val DarkLoadingShimmer = Color(0xFF44474F)
}

// ==================== CUSTOM ACCENT COLORS ====================
// Additional accent colors for special features

object AccentColors {
    // Achievement gold
    val Achievement = Color(0xFFFFB300)
    val AchievementContainer = Color(0xFFFFECB3)
    
    // Premium purple
    val Premium = Color(0xFF6C5CE7)
    val PremiumContainer = Color(0xFFEAE6FF)
    
    // Streak fire
    val StreakFire = Color(0xFFFF6B35)
    val StreakFireContainer = Color(0xFFFFE8E0)
    
    // Focus mode
    val Focus = Color(0xFF00BFA5)
    val FocusContainer = Color(0xFFB2DFDB)
}

