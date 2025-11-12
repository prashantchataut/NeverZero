package com.productivitystreak.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CompassCalibration
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.productivitystreak.ui.screens.stats.StatsScreen
import com.productivitystreak.ui.screens.vocabulary.VocabularyScreen
import com.productivitystreak.ui.state.AppUiState

private val bottomDestinations = listOf(
    NeverZeroDestination.Dashboard,
    NeverZeroDestination.Stats,
    NeverZeroDestination.Discover,
    NeverZeroDestination.Profile
)

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
    onChangeTheme: (com.productivitystreak.ui.state.profile.ProfileTheme) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentDestination?.route)) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { destination.icon?.let { Icon -> androidx.compose.material3.Icon(Icon, contentDescription = destination.label) } },
                            label = { Text(destination.label) },
                            colors = NavigationBarItemDefaults.colors()
                        )
                    }
                }
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
            transitionSpec = { fadeIn() with fadeOut() }
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
                        onChangeTheme = onChangeTheme
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
            }
        }
    }
}

private fun shouldShowBottomBar(route: String?): Boolean {
    return bottomDestinations.any { it.route == route }
}

private sealed class NeverZeroDestination(
    val route: String,
    val label: String,
    val icon: ImageVector?
) {
    object Dashboard : NeverZeroDestination("dashboard", "Dashboard", Icons.Rounded.Home)
    object Stats : NeverZeroDestination("stats", "Stats", Icons.Rounded.BarChart)
    object Discover : NeverZeroDestination("discover", "Discover", Icons.Rounded.CompassCalibration)
    object Profile : NeverZeroDestination("profile", "Profile", Icons.Rounded.Person)
    object Reading : NeverZeroDestination("reading", "Reading", null)
    object Vocabulary : NeverZeroDestination("vocabulary", "Vocabulary", null)
}
