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
 * Calm Zen RPG Color Scheme
 * Professional Dark Mode with Muted Colors
 */
private val CalmZenRPGColorScheme = darkColorScheme(
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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00C853),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF00C853).copy(alpha = 0.2f),
    onPrimaryContainer = Color(0xFF003300),
    secondary = Color(0xFF1B1B1F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF21005D),
    tertiary = Color(0xFF2BB8FF), // Accent.Info
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2BB8FF).copy(alpha = 0.2f),
    onTertiaryContainer = Color(0xFF004F4F),
    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFFE2E4EA),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color.Black
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
val LocalDesignColors = staticCompositionLocalOf { NeverZeroDesignPalettes.CalmZenRPG }

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
    val colorScheme = if (darkTheme) CalmZenRPGColorScheme else LightColorScheme
    val designColors = if (darkTheme) NeverZeroDesignPalettes.CalmZenRPG else NeverZeroDesignPalettes.Light
    
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
