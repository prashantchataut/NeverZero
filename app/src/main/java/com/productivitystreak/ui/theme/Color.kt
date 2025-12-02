package com.productivitystreak.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== CALM ZEN RPG PALETTE ====================

// Basic Colors
val White = Color(0xFFFFFFFF)
val NeonCyan = Color(0xFF00F5FF) // Bright cyan for accents
val NeonPurple = Color(0xFFBF40BF) // Bright purple for accents

// Backgrounds (Deep Muted Blue-Greys)
val Background = Color(0xFF0B0F14) // Very dark desaturated blue
val Surface = Color(0xFF151A21) // Slightly lighter
val SurfaceVariant = Color(0xFF1E242C) // Elevated surface
val BorderColor = Color(0xFF2A323C) // Muted border (renamed to avoid conflict with Border object)

// Primary Colors (Muted Blues & Desaturated Accents)
val PrimaryBlue = Color(0xFF6482AD) // Muted Steel Blue
val PrimaryPurple = Color(0xFF9485C2) // Desaturated Purple
val AccentCyan = Color(0xFF5FA8D3) // Muted Cyan

// RPG Stat Colors (strategic use only)
val StatRed = Color(0xFFEF4444) // Strength
val StatBlue = Color(0xFF3B82F6) // Intelligence  
val StatPurple = Color(0xFF8B5CF6) // Charisma
val StatGreen = Color(0xFF10B981) // Wisdom
val StatOrange = Color(0xFFF59E0B) // Discipline

// Semantic (Muted)
// Semantic (Muted & Professional)
val SuccessGreen = Color(0xFF5DAA86) // Sage Green
val DangerRed = Color(0xFFC25B5B) // Muted Red
val WarningYellow = Color(0xFFD4A758) // Muted Gold

// Text Hierarchy
// Text Hierarchy
val TextPrimary = Color(0xFFE2E8F0) // Off-white/Slate-200
val TextSecondary = Color(0xFF94A3B8) // Slate-400
val TextTertiary = Color(0xFF64748B) // Slate-500

// ==================== MATERIAL 3 MAPPING ====================

// Dark theme mappings using Calm Zen RPG palette
val Primary = PrimaryBlue
val OnPrimary = TextPrimary
val PrimaryContainer = PrimaryBlue.copy(alpha = 0.2f)
val OnPrimaryContainer = PrimaryBlue

val Secondary = PrimaryPurple
val OnSecondary = TextPrimary
val SecondaryContainer = PrimaryPurple.copy(alpha = 0.2f)
val OnSecondaryContainer = PrimaryPurple

val Tertiary = AccentCyan
val OnTertiary = Background
val TertiaryContainer = AccentCyan.copy(alpha = 0.2f)
val OnTertiaryContainer = AccentCyan

val Error = DangerRed
val OnError = Background
val ErrorContainer = DangerRed.copy(alpha = 0.2f)
val OnErrorContainer = DangerRed

val BackgroundColor = Background
val OnBackground = TextSecondary
val SurfaceColor = Surface
val OnSurface = TextSecondary
val SurfaceVariantColor = SurfaceVariant
val OnSurfaceVariant = TextTertiary

val Outline = BorderColor
val OutlineVariant = Color(0xFF1A1A1A)
val Scrim = Color(0xFF000000)

// ==================== STREAK / CATEGORY COLORS ====================

object StreakColors {
    val Reading = Color(0xFF5FA8D3) // Muted Cyan
    val ReadingContainer = Reading.copy(alpha = 0.15f)
    val OnReadingContainer = Reading

    val Learning = Color(0xFFD4A758) // Muted Yellow
    val LearningContainer = Learning.copy(alpha = 0.15f)
    val OnLearningContainer = Learning

    val Vocabulary = Color(0xFF9485C2) // Muted Purple
    val VocabularyContainer = Vocabulary.copy(alpha = 0.15f)
    val OnVocabularyContainer = Vocabulary

    val Creative = Color(0xFFC27D8B) // Muted Pink/Salmon
    val CreativeContainer = Creative.copy(alpha = 0.15f)
    val OnCreativeContainer = Creative

    val Exercise = Color(0xFFC25B5B) // Muted Red
    val ExerciseContainer = Exercise.copy(alpha = 0.15f)
    val OnExerciseContainer = Exercise

    val Wellness = Color(0xFF5DAA86) // Sage Green
    val WellnessContainer = Wellness.copy(alpha = 0.15f)
    val OnWellnessContainer = Wellness

    val Meditation = Color(0xFF6482AD) // Steel Blue
    val MeditationContainer = Meditation.copy(alpha = 0.15f)
    val OnMeditationContainer = Meditation

    val Productivity = Color(0xFF4B6E91) // Deep Steel Blue
    val ProductivityContainer = Productivity.copy(alpha = 0.15f)
    val OnProductivityContainer = Productivity
}

// ==================== GRADIENTS ====================

object GradientColors {
    // Muted Deep Space Gradients
    val PremiumStart = Color(0xFF4B6E91)
    val PremiumEnd = Color(0xFF9485C2)

    val CyberStart = Color(0xFF5FA8D3)
    val CyberEnd = Color(0xFF4B6E91)

    val FireStart = Color(0xFFD4A758)
    val FireEnd = Color(0xFFC25B5B)

    val VoidStart = Color(0xFF0B0F14)
    val VoidEnd = Color(0xFF151A21)

    val OceanStart = Color(0xFF4B6E91)
    val OceanEnd = Color(0xFF5FA8D3)

    val SunriseStart = Color(0xFFD4A758)
    val SunriseEnd = Color(0xFFC27D8B)
}

// ==================== SEMANTIC COLORS ====================

object SemanticColors {
    val Success = Color(0xFF2ED573)
    val OnSuccess = Background
    val SuccessContainer = Success.copy(alpha = 0.2f)
    
    val Warning = Color(0xFFFFA502)
    val OnWarning = Background
    val WarningContainer = Warning.copy(alpha = 0.2f)

    val Info = Color(0xFF1E90FF)
    val OnInfo = White
    val InfoContainer = Info.copy(alpha = 0.2f)
}

// ==================== DESIGN TOKENS ====================

data class NeverZeroDesignColors(
    val isDark: Boolean = true,
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

// NeonGreen is already defined in Primary Accents section

// ... (existing code)

object NeverZeroDesignPalettes {
    val CalmZenRPG = NeverZeroDesignColors(
        isDark = true,
        background = Background,
        backgroundAlt = Surface,
        surface = Surface,
        surfaceElevated = SurfaceVariant,
        border = Border,
        glow = PrimaryBlue.copy(alpha = 0.3f),
        primary = PrimaryBlue,
        onPrimary = TextPrimary,
        primaryMuted = PrimaryBlue.copy(alpha = 0.5f),
        secondary = PrimaryPurple,
        onSecondary = TextPrimary,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        disabled = Color(0xFF444444),
        success = SuccessGreen,
        warning = WarningYellow,
        error = DangerRed
    )

    val Light = NeverZeroDesignColors(
        isDark = false,
        background = Color(0xFFF7F8FA), // Surface.Base
        backgroundAlt = White,
        surface = White, // Surface.Card
        surfaceElevated = White,
        border = Color(0xFFE2E4EA), // Border.Muted
        glow = Color.Transparent,
        primary = Color(0xFF00C853), // Brand.Primary (Emerald)
        onPrimary = White,
        primaryMuted = Color(0xFF00C853).copy(alpha = 0.5f),
        secondary = Color(0xFF1B1B1F), // Brand.Secondary
        onSecondary = White,
        textPrimary = Color(0xFF1B1B1F),
        textSecondary = Color(0xFF757575),
        disabled = Color(0xFFE0E0E0),
        success = Color(0xFF00C853), // Accent.Success
        warning = Color(0xFFFFB74D), // Accent.Warning
        error = Error
    )
}

object ExtendedSemanticColors {
    val Focus = NeonCyan
    val DarkFocus = NeonCyan
    
    val DisabledContent = Color(0xFFFFFFFF).copy(alpha = 0.38f)
    val DisabledContainer = Color(0xFFFFFFFF).copy(alpha = 0.12f)
    val DarkDisabledContent = Color(0xFFFFFFFF).copy(alpha = 0.38f)
    val DarkDisabledContainer = Color(0xFFFFFFFF).copy(alpha = 0.12f)
    
    val LoadingShimmer = Color(0xFF333333)
    val DarkLoadingShimmer = Color(0xFF333333)
}

object AccentColors {
    val Achievement = Color(0xFFFFD700) // Gold
    val AchievementContainer = Color(0xFF332F00)
    
    val Premium = NeonPurple
    val PremiumContainer = NeonPurple.copy(alpha = 0.2f)
    
    val StreakFire = Color(0xFFFF4757)
    val StreakFireContainer = StreakFire.copy(alpha = 0.2f)
    
    val Focus = NeonCyan
    val FocusContainer = NeonCyan.copy(alpha = 0.2f)
}

