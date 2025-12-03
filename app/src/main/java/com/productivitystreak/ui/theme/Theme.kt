package com.productivitystreak.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Modern Stoic RPG Color Scheme
 * Clean, professional, and wisdom-focused
 */
private val ModernStoicColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    scrim = Scrim
)

// Keep Dark Scheme for users who really want it, but map to Stoic Dark equivalents if needed
// For now, we enforce the Light "Stoic" theme as requested, but provide a dark fallback that matches
private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenLight,
    onPrimary = Color.White,
    primaryContainer = ForestGreenDark,
    onPrimaryContainer = ForestGreenLight,
    secondary = SlateBlueLight,
    onSecondary = Color.White,
    secondaryContainer = SlateBlueDark,
    onSecondaryContainer = SlateBlueLight,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0)
)

/**
 * Material 3 Shape System
 * Consistent corner radius for all components
 */
private val AppShapes = Shapes(
    extraSmall = com.productivitystreak.ui.theme.Shapes.extraSmall,
    small = com.productivitystreak.ui.theme.Shapes.small,
    medium = com.productivitystreak.ui.theme.Shapes.medium,
    large = com.productivitystreak.ui.theme.Shapes.large,
    extraLarge = com.productivitystreak.ui.theme.Shapes.extraLarge
)

/**
 * Composition Local for accessing streak colors throughout the app
 */
val LocalStreakColors = staticCompositionLocalOf { StreakColors }
val LocalGradientColors = staticCompositionLocalOf { GradientColors }
val LocalSemanticColors = staticCompositionLocalOf { SemanticColors }
val LocalDesignColors = staticCompositionLocalOf { NeverZeroDesignPalettes.ModernStoic }

/**
 * Never Zero Theme
 * Supports Dark and Light modes
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color
    content: @Composable () -> Unit
) {
    // User requested "Modern Stoic RPG" with off-white background. 
    // We prioritize the Light (Stoic) scheme.
    // If the user explicitly wants dark mode, we can support it, but the default "Stoic" look is light.
    val colorScheme = if (darkTheme) DarkColorScheme else ModernStoicColorScheme
    val designColors = NeverZeroDesignPalettes.ModernStoic // Always use Stoic palette structure
    
    CompositionLocalProvider(
        LocalStreakColors provides StreakColors,
        LocalGradientColors provides GradientColors,
        LocalSemanticColors provides SemanticColors,
        LocalDesignColors provides designColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NeverZeroTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

@Composable
fun ProductivityStreakTheme(
    themeMode: com.productivitystreak.ui.state.profile.ProfileTheme = com.productivitystreak.ui.state.profile.ProfileTheme.Auto,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        com.productivitystreak.ui.state.profile.ProfileTheme.Light -> false
        com.productivitystreak.ui.state.profile.ProfileTheme.Dark -> true
        com.productivitystreak.ui.state.profile.ProfileTheme.Auto -> isSystemInDarkTheme()
    }

    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = false,
        content = content
    )
}

/**
 * Extension property to access streak colors from MaterialTheme
 */
object NeverZeroTheme {
    val streakColors: StreakColors
        @Composable
        get() = LocalStreakColors.current

    val gradientColors: GradientColors
        @Composable
        get() = LocalGradientColors.current

    val semanticColors: SemanticColors
        @Composable
        get() = LocalSemanticColors.current

    val designColors: NeverZeroDesignColors
        @Composable
        get() = LocalDesignColors.current
}
