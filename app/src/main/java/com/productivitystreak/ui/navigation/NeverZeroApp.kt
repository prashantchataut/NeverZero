package com.productivitystreak.ui.navigation

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.productivitystreak.ui.components.add.CenterAddMenu
import com.productivitystreak.ui.screens.add.AddFormSheets
import com.productivitystreak.ui.screens.dashboard.DashboardScreen
import com.productivitystreak.ui.screens.discover.DiscoverScreen
import com.productivitystreak.ui.screens.onboarding.OnboardingDialog
import com.productivitystreak.ui.screens.profile.ProfileScreen
import com.productivitystreak.ui.screens.reading.ReadingTrackerScreen
import com.productivitystreak.ui.screens.settings.SettingsScreen
import com.productivitystreak.ui.screens.stats.StatsScreen
import com.productivitystreak.ui.screens.vocabulary.VocabularyScreen
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.AddEntryType
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
    onSetOnboardingGoal: (String) -> Unit,
    onSetOnboardingCommitment: (Int, Int) -> Unit,
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
    onAddButtonTapped: () -> Unit,
    onDismissAddMenu: () -> Unit,
    onAddEntrySelected: (AddEntryType) -> Unit,
    onDismissAddForm: () -> Unit,
    onSubmitHabit: (name: String, goal: Int, unit: String, category: String, color: String?, icon: String?) -> Unit,
    onSubmitWord: (word: String, definition: String, example: String?) -> Unit,
    onSubmitJournal: (mood: Int, notes: String, highlights: String?, gratitude: String?, tomorrowGoals: String?) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (shouldShowBottomBar(currentDestination?.route)) {
                NeverZeroBottomBar(
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
                    onAddTapped = onAddButtonTapped
                )
            }
        }
    ) { innerPadding ->
        if (uiState.showOnboarding) {
            OnboardingDialog(
                state = uiState.onboardingState,
                onDismiss = onDismissOnboarding,
                onToggleCategory = onToggleOnboardingCategory,
                onGoalSelected = onSetOnboardingGoal,
                onCommitmentChanged = onSetOnboardingCommitment,
                onNext = onNextOnboardingStep,
                onPrevious = onPreviousOnboardingStep,
                onToggleNotifications = onToggleOnboardingNotifications,
                onReminderTimeSelected = onSetOnboardingReminderTime,
                onComplete = onCompleteOnboarding
            )
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = NeverZeroDestination.Dashboard.route
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
                        },
                        onEnableNotifications = { onToggleNotifications(true) }
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

            CenterAddMenu(
                visible = uiState.addUiState.isMenuOpen,
                onDismiss = onDismissAddMenu,
                onSelect = onAddEntrySelected
            )

            AddFormSheets(
                activeForm = uiState.addUiState.activeForm,
                isSubmitting = uiState.addUiState.isSubmitting,
                onDismiss = onDismissAddForm,
                onSubmitHabit = onSubmitHabit,
                onSubmitWord = onSubmitWord,
                onSubmitJournal = onSubmitJournal
            )
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
private fun NeverZeroBottomBar(
    destinations: List<NeverZeroDestination>,
    currentRoute: String?,
    onNavigate: (NeverZeroDestination) -> Unit,
    onAddTapped: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = Elevation.level2,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val leading = destinations.take(2)
            val trailing = destinations.drop(2)

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leading.forEach { destination ->
                    NavBarItem(
                        destination = destination,
                        selected = currentRoute == destination.route,
                        onClick = { onNavigate(destination) }
                    )
                }
            }

            AddCenterButton(onClick = onAddTapped)

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                trailing.forEach { destination ->
                    NavBarItem(
                        destination = destination,
                        selected = currentRoute == destination.route,
                        onClick = { onNavigate(destination) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBarItem(
    destination: NeverZeroDestination,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        destination.icon?.let { icon ->
            Icon(
                painter = rememberVectorPainter(icon),
                contentDescription = destination.label,
                tint = contentColor
            )
        }
        Text(
            text = destination.label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AddCenterButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF5F7BFF), Color(0xFF8C6AFF))
                    )
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
        Text(
            text = "Add",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
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
