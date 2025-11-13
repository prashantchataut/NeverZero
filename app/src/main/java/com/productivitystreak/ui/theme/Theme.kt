package com.productivitystreak.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * Light Color Scheme
 * Clean, modern colors optimized for light backgrounds
 */
private val LightColorScheme = lightColorScheme(
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
 * Dark Color Scheme
 * Deep, rich colors optimized for dark backgrounds with OLED support
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
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

/**
 * Never Zero Theme
 * Complete Material 3 theme with Poppins typography and custom color system
 *
 * @param darkTheme Whether to use dark theme colors
 * @param dynamicColor Whether to use dynamic colors (Android 12+)
 * @param content The composable content to theme
 */
@Composable
fun ProductivityStreakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalStreakColors provides StreakColors,
        LocalGradientColors provides GradientColors,
        LocalSemanticColors provides SemanticColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NeverZeroTypography,
            shapes = AppShapes,
            content = content
        )
    }
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
}
