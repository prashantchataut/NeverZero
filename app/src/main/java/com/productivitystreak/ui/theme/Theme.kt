package com.productivitystreak.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Deep Space Color Scheme
 * Enforced Dark Mode for Premium Aesthetic
 */
private val DeepSpaceColorScheme = darkColorScheme(
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
val LocalDesignColors = staticCompositionLocalOf { NeverZeroDesignPalettes.DeepSpace }

/**
 * Never Zero Theme
 * Enforces Deep Space Dark Mode
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = true, // Force Dark Mode
    dynamicColor: Boolean = false, // Disable dynamic color
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Void,
            darkIcons = false
        )
    }

    CompositionLocalProvider(
        LocalStreakColors provides StreakColors,
        LocalGradientColors provides GradientColors,
        LocalSemanticColors provides SemanticColors,
        LocalDesignColors provides NeverZeroDesignPalettes.DeepSpace
    ) {
        MaterialTheme(
            colorScheme = DeepSpaceColorScheme,
            typography = NeverZeroTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

@Composable
fun ProductivityStreakTheme(
    themeMode: com.productivitystreak.ui.state.profile.ProfileTheme = com.productivitystreak.ui.state.profile.ProfileTheme.Dark,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Ignore user preference for now to enforce the redesign aesthetic
    AppTheme(
        darkTheme = true,
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
