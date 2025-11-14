package com.productivitystreak.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.productivitystreak.ui.screens.dashboard.DashboardScreen
import com.productivitystreak.ui.screens.discover.DiscoverScreen
import com.productivitystreak.ui.screens.onboarding.OnboardingDialog
import com.productivitystreak.ui.screens.profile.ProfileScreen
import com.productivitystreak.ui.screens.reading.ReadingTrackerScreen
import com.productivitystreak.ui.screens.settings.SettingsScreen
import com.productivitystreak.ui.screens.stats.StatsScreen
import com.productivitystreak.ui.screens.vocabulary.VocabularyScreen
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.theme.*

/**
 * Bottom navigation destinations
 */
private val bottomDestinations = listOf(
    NeverZeroDestination.Dashboard,
    NeverZeroDestination.Stats,
    NeverZeroDestination.Discover,
    NeverZeroDestination.Profile
)

/**
 * Main app navigation with modern Material 3 bottom bar
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NeverZeroApp(
    uiState: AppUiState,
    onRefreshQuote: () -> Unit,
    onSelectStreak: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onSimulateTaskCompletion: (String, Int) -> Unit,
    onLogReadingProgress: (Int) -> Unit,
    onAddVocabularyWord: (String, String, String?) -> Unit,
    onToggleOnboardingCategory: (String) -> Unit,
    onCompleteOnboarding: () -> Unit,
    onDismissOnboarding: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onChangeReminderFrequency: (com.productivitystreak.ui.state.profile.ReminderFrequency) -> Unit,
    onToggleWeeklySummary: (Boolean) -> Unit,
    onChangeTheme: (com.productivitystreak.ui.state.profile.ProfileTheme) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onSettingsThemeChange: (com.productivitystreak.ui.state.settings.ThemeMode) -> Unit = {},
    onSettingsDailyRemindersToggle: (Boolean) -> Unit = {},
    onSettingsWeeklyBackupsToggle: (Boolean) -> Unit = {},
    onSettingsReminderTimeChange: (String) -> Unit = {},
    onSettingsHapticFeedbackToggle: (Boolean) -> Unit = {},
    onSettingsCreateBackup: () -> Unit = {},
    onSettingsRestoreBackup: () -> Unit = {},
    onSettingsDismissMessage: () -> Unit = {}
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (shouldShowBottomBar(currentDestination?.route)) {
                ModernNavigationBar(
                    destinations = bottomDestinations,
                    currentRoute = currentDestination?.route,
                    onNavigate = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        if (uiState.showOnboarding) {
            OnboardingDialog(
                state = uiState.onboardingState,
                onDismiss = onDismissOnboarding,
                onToggleCategory = onToggleOnboardingCategory,
                onComplete = onCompleteOnboarding
            )
        }

        AnimatedContent(
            targetState = currentDestination?.route ?: NeverZeroDestination.Dashboard.route,
            label = "nav-content",
            transitionSpec = { fadeIn().togetherWith(fadeOut()) }
        ) { _ ->
            NavHost(
                navController = navController,
                startDestination = NeverZeroDestination.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(NeverZeroDestination.Dashboard.route) {
                    DashboardScreen(
                        state = uiState,
                        onRefreshQuote = onRefreshQuote,
                        onSelectStreak = onSelectStreak,
                        onToggleTask = onToggleTask,
                        onNavigateToReading = {
                            navController.navigate(NeverZeroDestination.Reading.route)
                        },
                        onNavigateToVocabulary = {
                            navController.navigate(NeverZeroDestination.Vocabulary.route)
                        }
                    )
                }
                composable(NeverZeroDestination.Stats.route) {
                    StatsScreen(state = uiState.statsState)
                }
                composable(NeverZeroDestination.Discover.route) {
                    DiscoverScreen(state = uiState.discoverState)
                }
                composable(NeverZeroDestination.Profile.route) {
                    ProfileScreen(
                        userName = uiState.userName,
                        state = uiState.profileState,
                        quote = uiState.quote,
                        onRefreshQuote = onRefreshQuote,
                        onToggleNotifications = onToggleNotifications,
                        onChangeReminderFrequency = onChangeReminderFrequency,
                        onToggleWeeklySummary = onToggleWeeklySummary,
                        onChangeTheme = onChangeTheme,
                        onToggleHaptics = onToggleHaptics,
                        onNavigateToSettings = {
                            navController.navigate(NeverZeroDestination.Settings.route)
                        }
                    )
                }
                composable(NeverZeroDestination.Reading.route) {
                    ReadingTrackerScreen(
                        state = uiState.readingTrackerState,
                        onAddProgress = onLogReadingProgress
                    )
                }
                composable(NeverZeroDestination.Vocabulary.route) {
                    VocabularyScreen(
                        state = uiState.vocabularyState,
                        onAddWord = onAddVocabularyWord
                    )
                }
                composable(NeverZeroDestination.Settings.route) {
                    SettingsScreen(
                        state = uiState.settingsState,
                        onThemeChange = onSettingsThemeChange,
                        onDailyRemindersToggle = onSettingsDailyRemindersToggle,
                        onWeeklyBackupsToggle = onSettingsWeeklyBackupsToggle,
                        onReminderTimeChange = onSettingsReminderTimeChange,
                        onHapticFeedbackToggle = onSettingsHapticFeedbackToggle,
                        onCreateBackup = onSettingsCreateBackup,
                        onRestoreBackup = onSettingsRestoreBackup,
                        onDismissMessage = onSettingsDismissMessage
                    )
                }
            }
        }
    }
}

/**
 * Check if bottom bar should be visible for the given route
 */
private fun shouldShowBottomBar(route: String?): Boolean {
    return bottomDestinations.any { it.route == route }
}

/**
 * Modern Navigation Bar with Material 3 design
 */
@Composable
private fun ModernNavigationBar(
    destinations: List<NeverZeroDestination>,
    currentRoute: String?,
    onNavigate: (NeverZeroDestination) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = Elevation.level2,
        shadowElevation = Elevation.level3
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(Size.bottomNavHeight),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp
        ) {
            destinations.forEach { destination ->
                val selected = currentRoute == destination.route

                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.1f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "iconScale"
                )

                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(destination) },
                    icon = {
                        destination.icon?.let { icon ->
                            Box(
                                modifier = Modifier
                                    .size(Size.iconLarge)
                                    .animateContentSize()
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = destination.label,
                                    modifier = Modifier
                                        .size(Size.iconMedium)
                                        .graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

/**
 * Navigation destinations
 */
private sealed class NeverZeroDestination(
    val route: String,
    val label: String,
    val icon: ImageVector?
) {
    object Dashboard : NeverZeroDestination("dashboard", "Home", Icons.Rounded.Home)
    object Stats : NeverZeroDestination("stats", "Stats", Icons.Rounded.BarChart)
    object Discover : NeverZeroDestination("discover", "Discover", Icons.Rounded.Explore)
    object Profile : NeverZeroDestination("profile", "Profile", Icons.Rounded.Person)
    object Reading : NeverZeroDestination("reading", "Reading", null)
    object Vocabulary : NeverZeroDestination("vocabulary", "Vocabulary", null)
    object Settings : NeverZeroDestination("settings", "Settings", Icons.Rounded.Settings)
}
