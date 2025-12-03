package com.productivitystreak.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== MODERN STOIC RPG PALETTE ====================

// Base Colors
val StoicWhite = Color(0xFFFAFAFA) // Off-white background
val StoicBlack = Color(0xFF1A1C1E) // Deep text
val StoicGray = Color(0xFFE0E0E0) // Subtle borders

// Primary - Deep Forest Green (Growth/Wisdom)
val ForestGreen = Color(0xFF2E5C55)
val ForestGreenLight = Color(0xFF4A8075)
val ForestGreenDark = Color(0xFF1B3B35)

// Secondary - Slate Blue (Tech/Stats)
val SlateBlue = Color(0xFF5C7C8A)
val SlateBlueLight = Color(0xFF829FA8)
val SlateBlueDark = Color(0xFF3A525E)

// Accents (Stoic/Ancient Feel)
val AncientGold = Color(0xFFC5A059) // Wisdom/Achievements
val ClayRed = Color(0xFFA65D57) // Danger/Action
val SageGreen = Color(0xFF7CA982) // Success/Health

// Backgrounds
val BackgroundLight = StoicWhite
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF0F2F4)

// Borders
val BorderLight = Color(0xFFE5E7EB) // Subtle 1px border
val BorderStrong = Color(0xFFD1D5DB)

// Text
val TextPrimaryLight = Color(0xFF1F2937) // Gray-900
val TextSecondaryLight = Color(0xFF4B5563) // Gray-600
val TextTertiaryLight = Color(0xFF9CA3AF) // Gray-400

// ==================== RPG STAT COLORS ====================
val StatStrength = Color(0xFFE57373) // Red
val StatIntelligence = SlateBlue // Blue
val StatCharisma = Color(0xFF9575CD) // Purple
val StatWisdom = ForestGreen // Green
val StatDiscipline = AncientGold // Gold

// ==================== MATERIAL 3 MAPPING ====================

// Light Theme (Default for Modern Stoic)
val Primary = ForestGreen
val OnPrimary = Color.White
val PrimaryContainer = ForestGreen.copy(alpha = 0.1f)
val OnPrimaryContainer = ForestGreenDark

val Secondary = SlateBlue
val OnSecondary = Color.White
val SecondaryContainer = SlateBlue.copy(alpha = 0.1f)
val OnSecondaryContainer = SlateBlueDark

val Tertiary = AncientGold
val OnTertiary = Color.White
val TertiaryContainer = AncientGold.copy(alpha = 0.1f)
val OnTertiaryContainer = Color(0xFF3E2E0B)

val Error = ClayRed
val OnError = Color.White
val ErrorContainer = ClayRed.copy(alpha = 0.1f)
val OnErrorContainer = Color(0xFF411B19)

val Background = BackgroundLight
val OnBackground = TextPrimaryLight
val Surface = SurfaceLight
val OnSurface = TextPrimaryLight
val SurfaceVariant = SurfaceVariantLight
val OnSurfaceVariant = TextSecondaryLight

val Outline = BorderLight
val OutlineVariant = BorderStrong
val Scrim = Color.Black

// ==================== STREAK / CATEGORY COLORS ====================

object StreakColors {
    val Reading = SlateBlue
    val ReadingContainer = Reading.copy(alpha = 0.1f)
    val OnReadingContainer = Reading

    val Learning = AncientGold
    val LearningContainer = Learning.copy(alpha = 0.1f)
    val OnLearningContainer = Learning

    val Vocabulary = StatCharisma
    val VocabularyContainer = Vocabulary.copy(alpha = 0.1f)
    val OnVocabularyContainer = Vocabulary

    val Creative = Color(0xFFD88C9A) // Muted Rose
    val CreativeContainer = Creative.copy(alpha = 0.1f)
    val OnCreativeContainer = Creative

    val Exercise = ClayRed
    val ExerciseContainer = Exercise.copy(alpha = 0.1f)
    val OnExerciseContainer = Exercise

    val Wellness = SageGreen
    val WellnessContainer = Wellness.copy(alpha = 0.1f)
    val OnWellnessContainer = Wellness

    val Meditation = ForestGreen
    val MeditationContainer = Meditation.copy(alpha = 0.1f)
    val OnMeditationContainer = Meditation

    val Productivity = Color(0xFF546E7A) // Blue Gray
    val ProductivityContainer = Productivity.copy(alpha = 0.1f)
    val OnProductivityContainer = Productivity
}

// ==================== GRADIENTS ====================

object GradientColors {
    // Subtle, natural gradients
    val PremiumStart = ForestGreen
    val PremiumEnd = SlateBlue

    val CyberStart = SlateBlue
    val CyberEnd = Color(0xFF4A8075) // ForestGreenLight

    val FireStart = AncientGold
    val FireEnd = ClayRed

    val VoidStart = Color(0xFF2C3E50)
    val VoidEnd = Color(0xFF34495E)

    val OceanStart = SlateBlue
    val OceanEnd = ForestGreen

    val SunriseStart = AncientGold
    val SunriseEnd = Color(0xFFE6B89C)
}

// ==================== SEMANTIC COLORS ====================

object SemanticColors {
    val Success = SageGreen
    val OnSuccess = Color.White
    val SuccessContainer = Success.copy(alpha = 0.1f)
    
    val Warning = AncientGold
    val OnWarning = Color.White
    val WarningContainer = Warning.copy(alpha = 0.1f)

    val Info = SlateBlue
    val OnInfo = Color.White
    val InfoContainer = Info.copy(alpha = 0.1f)
}

// ==================== DESIGN TOKENS ====================

data class NeverZeroDesignColors(
    val isDark: Boolean = false,
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
    val ModernStoic = NeverZeroDesignColors(
        isDark = false,
        background = Background,
        backgroundAlt = SurfaceVariant,
        surface = Surface,
        surfaceElevated = Surface, // Flat design, use border instead of elevation
        border = BorderLight,
        glow = Color.Transparent, // No glow in stoic theme
        primary = Primary,
        onPrimary = OnPrimary,
        primaryMuted = Primary.copy(alpha = 0.6f),
        secondary = Secondary,
        onSecondary = OnSecondary,
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight,
        disabled = Color(0xFF9E9E9E),
        success = SemanticColors.Success,
        warning = SemanticColors.Warning,
        error = Error
    )
}

object ExtendedSemanticColors {
    val Focus = ForestGreen
    val DarkFocus = ForestGreenDark
    
    val DisabledContent = Color.Black.copy(alpha = 0.38f)
    val DisabledContainer = Color.Black.copy(alpha = 0.12f)
    val DarkDisabledContent = Color.White.copy(alpha = 0.38f)
    val DarkDisabledContainer = Color.White.copy(alpha = 0.12f)
    
    val LoadingShimmer = Color(0xFFEEEEEE)
    val DarkLoadingShimmer = Color(0xFF333333)
}

object AccentColors {
    val Achievement = AncientGold
    val AchievementContainer = AncientGold.copy(alpha = 0.1f)
    
    val Premium = ForestGreen
    val PremiumContainer = ForestGreen.copy(alpha = 0.1f)
    
    val StreakFire = ClayRed
    val StreakFireContainer = ClayRed.copy(alpha = 0.1f)
    
    val Focus = ForestGreen
    val FocusContainer = ForestGreen.copy(alpha = 0.1f)
}

