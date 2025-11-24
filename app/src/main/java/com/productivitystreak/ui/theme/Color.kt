package com.productivitystreak.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== DEEP SPACE PALETTE ====================

// Backgrounds
val Void = Color(0xFF000000) // True Black
val DeepSpace = Color(0xFF050508) // Slightly off-black for depth
val Glass = Color(0xFF1E1E2E) // Blue-tinted grey for glass cards
val SurfaceDark = Color(0xFF121212) // Standard dark surface

// Primary Accents (Neons)
val ElectricBlue = Color(0xFF2E86DE)
val NeonCyan = Color(0xFF00D2D3)
val HotPink = Color(0xFFFF00FF) // More vibrant pink
val NeonPurple = Color(0xFFBC13FE) // Cyberpunk purple
val NeonLime = Color(0xFFC4F000) // Acid green/lime for high contrast

// Text
val White = Color(0xFFFFFFFF)
val Silver = Color(0xFFE0E0E0)
val Grey = Color(0xFF9E9E9E)

// ==================== MATERIAL 3 MAPPING ====================

// We are enforcing Dark Mode, so these map primarily to Dark Theme slots

val Primary = ElectricBlue
val OnPrimary = White
val PrimaryContainer = ElectricBlue.copy(alpha = 0.2f)
val OnPrimaryContainer = Color(0xFFD1E4FF)

val Secondary = NeonPurple
val OnSecondary = White
val SecondaryContainer = NeonPurple.copy(alpha = 0.2f)
val OnSecondaryContainer = Color(0xFFEADDFF)

val Tertiary = NeonCyan
val OnTertiary = Void
val TertiaryContainer = NeonCyan.copy(alpha = 0.2f)
val OnTertiaryContainer = Color(0xFF9CF8F8)

val Error = Color(0xFFFF4444)
val OnError = Void
val ErrorContainer = Color(0xFF93000A)
val OnErrorContainer = Color(0xFFFFDAD6)

val Background = Void
val OnBackground = Silver
val Surface = SurfaceDark
val OnSurface = Silver
val SurfaceVariant = Glass
val OnSurfaceVariant = Grey

val Outline = Color(0xFF444444)
val OutlineVariant = Color(0xFF222222)
val Scrim = Color(0xFF000000)

// ==================== STREAK / CATEGORY COLORS ====================

object StreakColors {
    val Reading = Color(0xFF48DBFB) // Cyan
    val ReadingContainer = Reading.copy(alpha = 0.15f)
    val OnReadingContainer = Reading

    val Learning = Color(0xFFFFD32A) // Vibrant Yellow
    val LearningContainer = Learning.copy(alpha = 0.15f)
    val OnLearningContainer = Learning

    val Vocabulary = Color(0xFFBE2EDD) // Purple
    val VocabularyContainer = Vocabulary.copy(alpha = 0.15f)
    val OnVocabularyContainer = Vocabulary

    val Creative = Color(0xFFFF7979) // Salmon/Pink
    val CreativeContainer = Creative.copy(alpha = 0.15f)
    val OnCreativeContainer = Creative

    val Exercise = Color(0xFFFF4757) // Red
    val ExerciseContainer = Exercise.copy(alpha = 0.15f)
    val OnExerciseContainer = Exercise

    val Wellness = Color(0xFF2ED573) // Green
    val WellnessContainer = Wellness.copy(alpha = 0.15f)
    val OnWellnessContainer = Wellness

    val Meditation = Color(0xFF70A1FF) // Blue
    val MeditationContainer = Meditation.copy(alpha = 0.15f)
    val OnMeditationContainer = Meditation

    val Productivity = Color(0xFF1E90FF) // Dodger Blue
    val ProductivityContainer = Productivity.copy(alpha = 0.15f)
    val OnProductivityContainer = Productivity
}

// ==================== GRADIENTS ====================

object GradientColors {
    // Deep Space Gradients
    val PremiumStart = Color(0xFF2E86DE)
    val PremiumEnd = Color(0xFFBC13FE)

    val CyberStart = Color(0xFF00D2D3)
    val CyberEnd = Color(0xFF2E86DE)

    val FireStart = Color(0xFFFF9F43)
    val FireEnd = Color(0xFFFF4757)

    val VoidStart = Color(0xFF000000)
    val VoidEnd = Color(0xFF121212)
}

// ==================== SEMANTIC COLORS ====================

object SemanticColors {
    val Success = Color(0xFF2ED573)
    val OnSuccess = Void
    val SuccessContainer = Success.copy(alpha = 0.2f)
    
    val Warning = Color(0xFFFFA502)
    val OnWarning = Void
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

object NeverZeroDesignPalettes {
    val DeepSpace = NeverZeroDesignColors(
        isDark = true,
        background = Void,
        backgroundAlt = DeepSpace,
        surface = SurfaceDark,
        surfaceElevated = Glass,
        border = Color(0xFF333333),
        glow = ElectricBlue.copy(alpha = 0.5f),
        primary = ElectricBlue,
        onPrimary = White,
        primaryMuted = ElectricBlue.copy(alpha = 0.5f),
        secondary = NeonPurple,
        onSecondary = White,
        textPrimary = White,
        textSecondary = Silver,
        disabled = Color(0xFF444444),
        success = SemanticColors.Success,
        warning = SemanticColors.Warning,
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

