package com.productivitystreak.ui.navigation

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    onNextOnboardingStep: () -> Unit,
    onPreviousOnboardingStep: () -> Unit,
    onToggleOnboardingNotifications: (Boolean) -> Unit,
    onSetOnboardingReminderTime: (String) -> Unit,
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
    onSettingsRestoreFileSelected: (Uri) -> Unit = {},
    onSettingsDismissRestoreDialog: () -> Unit = {},
    onSettingsDismissMessage: () -> Unit = {},
    onPrimaryAction: () -> Unit = {}
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (shouldShowBottomBar(currentDestination?.route)) {
                NeverZeroNavigationBar(
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
                    },
                    onPrimaryAction = onPrimaryAction
                )
            }
        }
    ) { innerPadding ->
        if (uiState.showOnboarding) {
            OnboardingDialog(
                state = uiState.onboardingState,
                onDismiss = onDismissOnboarding,
                onToggleCategory = onToggleOnboardingCategory,
                onNext = onNextOnboardingStep,
                onPrevious = onPreviousOnboardingStep,
                onToggleNotifications = onToggleOnboardingNotifications,
                onReminderTimeSelected = onSetOnboardingReminderTime,
                onComplete = onCompleteOnboarding
            )
        }

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
                    },
                    onNavigateToStats = {
                        navController.navigate(NeverZeroDestination.Stats.route)
                    },
                    onNavigateToDiscover = {
                        navController.navigate(NeverZeroDestination.Discover.route)
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
                    onRestoreFileSelected = onSettingsRestoreFileSelected,
                    onDismissRestoreDialog = onSettingsDismissRestoreDialog,
                    onDismissMessage = onSettingsDismissMessage
                )
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
private fun NeverZeroNavigationBar(
    destinations: List<NeverZeroDestination>,
    currentRoute: String?,
    onNavigate: (NeverZeroDestination) -> Unit,
    onPrimaryAction: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surface
        )
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        shadowElevation = Elevation.level3
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundGradient)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEachIndexed { index, destination ->
                    NavigationPill(
                        destination = destination,
                        selected = currentRoute == destination.route,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(destination) }
                    )

                    if (index < destinations.lastIndex) {
                        Spacer(modifier = Modifier.width(12.dp))
                        PrimaryActionDivider(onClick = onPrimaryAction)
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationPill(
    destination: NeverZeroDestination,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val targetColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val iconTint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier.height(56.dp),
        shape = CircleShape,
        tonalElevation = if (selected) Elevation.level2 else Elevation.level1,
        color = targetColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            destination.icon?.let { icon ->
                Icon(
                    painter = rememberVectorPainter(icon),
                    contentDescription = destination.label,
                    tint = iconTint
                )
            }
            Text(
                text = destination.label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PrimaryActionDivider(onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .height(60.dp)
                .clip(CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = Elevation.level3,
            onClick = onClick
        ) {
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Primary Action",
                    tint = MaterialTheme.colorScheme.onPrimary
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
