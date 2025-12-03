package com.productivitystreak.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.productivitystreak.ui.components.NeverZeroBottomBar
import com.productivitystreak.ui.components.PrimaryButton
import com.productivitystreak.ui.screens.add.AddEntryMenuSheet
import com.productivitystreak.ui.screens.add.HabitFormSheet
import com.productivitystreak.ui.screens.add.JournalFormSheet
import com.productivitystreak.ui.screens.add.TeachWordSheet
import com.productivitystreak.ui.screens.add.TimeCapsuleFormSheet
import com.productivitystreak.ui.screens.add.VocabularyFormSheet
import com.productivitystreak.ui.screens.dashboard.DashboardScreen
import com.productivitystreak.ui.screens.discover.AssetDetailScreen
import com.productivitystreak.ui.screens.onboarding.OnboardingFlow
import com.productivitystreak.ui.screens.profile.ProfileScreen
import com.productivitystreak.ui.screens.stats.StatsScreen
import com.productivitystreak.ui.state.AddEntryType
import com.productivitystreak.ui.state.UiMessageType
import kotlinx.coroutines.launch

enum class MainDestination(val route: String) {
    HOME("home"),
    STATS("stats"),
    MENTOR("mentor"),
    PROFILE("profile"),
    FOCUS("focus"),
    CHALLENGES("challenges")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeverZeroApp(
    appViewModel: com.productivitystreak.ui.AppViewModel,
    viewModelFactory: androidx.lifecycle.ViewModelProvider.Factory
) {
    val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    
    // Feature ViewModels
    val streakViewModel: com.productivitystreak.ui.screens.stats.StreakViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
    val profileViewModel: com.productivitystreak.ui.screens.profile.ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
    val discoverViewModel: com.productivitystreak.ui.screens.discover.DiscoverViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
    val onboardingViewModel: com.productivitystreak.ui.screens.onboarding.OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
    
    // We need these for the Add Menu
    val vocabularyViewModel: com.productivitystreak.ui.screens.vocabulary.VocabularyViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
    val journalViewModel: com.productivitystreak.ui.screens.journal.JournalViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
    val readingViewModel: com.productivitystreak.ui.screens.reading.ReadingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)

    // Event handlers
    val onDismissUiMessage = appViewModel::onDismissUiMessage
    val onAssetConsumed = discoverViewModel::onAssetConsumed
    val onAssetTestPassed = discoverViewModel::onAssetTestPassed
    val onAddEntrySelected = appViewModel::onAddEntrySelected
    val onDismissAddMenu = appViewModel::onDismissAddMenu
    val onDismissAddForm = appViewModel::onDismissAddForm
    val onAddButtonTapped = appViewModel::onAddButtonTapped

    val haptics = LocalHapticFeedback.current

    // FTUE: Immersive onboarding flow
    val showOnboarding by onboardingViewModel.showOnboarding.collectAsStateWithLifecycle()
    val isAuthLoading by onboardingViewModel.isLoading.collectAsStateWithLifecycle()

    if (isAuthLoading) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        return
    }

    if (showOnboarding) {
        val onboardingState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
        OnboardingFlow(
            onboardingState = onboardingState,
            onToggleOnboardingCategory = onboardingViewModel::onToggleOnboardingCategory,
            onSetOnboardingGoal = onboardingViewModel::onSetOnboardingGoal,
            onSetOnboardingCommitment = onboardingViewModel::onSetOnboardingCommitment,
            onNextStep = onboardingViewModel::onNextOnboardingStep,
            onPreviousStep = onboardingViewModel::onPreviousOnboardingStep,
            onToggleNotificationsAllowed = onboardingViewModel::onToggleOnboardingNotifications,
            onSetReminderTime = onboardingViewModel::onSetOnboardingReminderTime,
            onUserNameChange = onboardingViewModel::onUserNameChange,
            onHabitNameChange = onboardingViewModel::onHabitNameChange,
            onIconSelected = onboardingViewModel::onIconSelected,
            onCompleteOnboarding = { onboardingViewModel.onCompleteOnboarding() },
            onDismissOnboarding = { /* handled by state */ },
            onRequestNotificationPermission = appViewModel::onShowNotificationPermissionDialog,
            onRequestExactAlarmPermission = appViewModel::onShowAlarmPermissionDialog
        )
        return
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val uiMessage = uiState.uiMessage
    val scope = rememberCoroutineScope()
    var snackbarType by remember { mutableStateOf(UiMessageType.INFO) }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarType = message.type
            scope.launch {
                snackbarHostState.showSnackbar(message.text)
                onDismissUiMessage()
            }
        }
    }

    // State for overlays
    var selectedAssetId by rememberSaveable { mutableStateOf<String?>(null) }
    var showTemplates by rememberSaveable { mutableStateOf(false) }
    var showBuddhaChat by rememberSaveable { mutableStateOf(false) }
    var showLeaderboard by rememberSaveable { mutableStateOf(false) }

    val addUi = uiState.addUiState
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isSheetVisible = addUi.isMenuOpen || addUi.activeForm != null

    // Navigation State
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = when (currentRoute) {
        MainDestination.HOME.route -> MainDestination.HOME
        MainDestination.STATS.route -> MainDestination.STATS
        MainDestination.MENTOR.route -> MainDestination.MENTOR
        MainDestination.PROFILE.route -> MainDestination.PROFILE
        MainDestination.FOCUS.route -> MainDestination.FOCUS
        MainDestination.CHALLENGES.route -> MainDestination.CHALLENGES
        else -> MainDestination.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                val (icon, iconTint) = when (snackbarType) {
                    UiMessageType.SUCCESS -> Icons.Filled.CheckCircle to MaterialTheme.colorScheme.primary
                    UiMessageType.ERROR -> Icons.Filled.Warning to MaterialTheme.colorScheme.error
                    UiMessageType.INFO -> Icons.Filled.Info to MaterialTheme.colorScheme.secondary
                }
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    tonalElevation = 6.dp,
                    shadowElevation = 10.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedContent(targetState = iconTint, label = "snackbar-icon") { tint ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = tint
                            )
                        }
                        Text(
                            text = snackbarData.visuals.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Hide bottom bar on Focus and Challenges screens if desired, or keep it.
            // For now, we keep it for main tabs.
            if (currentRoute in listOf(MainDestination.HOME.route, MainDestination.STATS.route, MainDestination.MENTOR.route, MainDestination.PROFILE.route)) {
                NeverZeroBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { destination ->
                        navController.navigate(destination.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    onQuickAction = onAddButtonTapped
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = MainDestination.HOME.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                composable(MainDestination.HOME.route) {
                    val streakState by streakViewModel.uiState.collectAsStateWithLifecycle()
                    val vocabularyState by vocabularyViewModel.uiState.collectAsStateWithLifecycle()
                    DashboardScreen(
                        streakUiState = streakState,
                        uiState = uiState.copy(vocabularyState = vocabularyState),
                        onToggleTask = streakViewModel::onToggleTask,
                        onRefreshQuote = streakViewModel::fetchBuddhaInsight,
                        onAddHabitClick = appViewModel::onAddButtonTapped,
                        onSelectStreak = streakViewModel::onSelectStreak,
                        onAddOneOffTask = streakViewModel::addOneOffTask,
                        onToggleOneOffTask = streakViewModel::toggleOneOffTask,
                        onDeleteOneOffTask = streakViewModel::deleteOneOffTask,
                        onAssetSelected = { assetId -> selectedAssetId = assetId },
                        onOpenBuddhaChat = { navController.navigate(MainDestination.MENTOR.route) },
                        onOpenJournal = { appViewModel.onAddEntrySelected(AddEntryType.JOURNAL) },
                        onOpenTimeCapsule = { appViewModel.onAddEntrySelected(AddEntryType.TIME_CAPSULE) },
                        onOpenLeaderboard = { showLeaderboard = true },
                        onOpenMonkMode = { navController.navigate(MainDestination.FOCUS.route) },
                        onOpenChallenges = { navController.navigate(MainDestination.CHALLENGES.route) },
                        onAddEntrySelected = appViewModel::onAddEntrySelected
                    )
                }

                composable(MainDestination.STATS.route) {
                    val streakState by streakViewModel.uiState.collectAsStateWithLifecycle()
                    StatsScreen(
                        statsState = streakState.statsState,
                        onLeaderboardTypeSelected = streakViewModel::toggleLeaderboardType,
                        onOpenLeaderboard = { showLeaderboard = true }
                    )
                }

                composable(MainDestination.MENTOR.route) {
                    val app = appViewModel.getApplication<com.productivitystreak.NeverZeroApplication>()
                    com.productivitystreak.ui.screens.ai.BuddhaChatScreen(
                        userName = uiState.userName,
                        onBackClick = { navController.navigateUp() },
                        repository = app.buddhaRepository,
                        hapticsEnabled = uiState.profileState.hapticsEnabled,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                composable(MainDestination.PROFILE.route) {
                    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
                    ProfileScreen(
                        userName = uiState.userName,
                        profileState = profileState.profileState,
                        settingsState = profileState.settingsState,
                        totalPoints = uiState.totalPoints,
                        timeCapsules = profileState.timeCapsules,
                        onSettingsThemeChange = profileViewModel::onSettingsThemeChange,
                        onSettingsDailyRemindersToggle = profileViewModel::onSettingsDailyRemindersToggle,
                        onSettingsWeeklyBackupsToggle = profileViewModel::onSettingsWeeklyBackupsToggle,
                        onSettingsReminderTimeChange = profileViewModel::onSettingsReminderTimeChange,
                        onSettingsHapticFeedbackToggle = profileViewModel::onSettingsHapticFeedbackToggle,
                        onSettingsCreateBackup = profileViewModel::onSettingsCreateBackup,
                        onSettingsRestoreBackup = profileViewModel::onSettingsRestoreBackup,
                        onSettingsRestoreFileSelected = profileViewModel::onSettingsRestoreFromFile,
                        onSettingsDismissRestoreDialog = profileViewModel::onSettingsDismissRestoreDialog,
                        onSettingsDismissMessage = profileViewModel::onSettingsDismissMessage,
                        onToggleNotifications = profileViewModel::onToggleNotifications,
                        onChangeReminderFrequency = profileViewModel::onChangeReminderFrequency,
                        onToggleWeeklySummary = profileViewModel::onToggleWeeklySummary,
                        onToggleHaptics = profileViewModel::onToggleHaptics,
                        onRequestNotificationPermission = appViewModel::onShowNotificationPermissionDialog,
                        onRequestExactAlarmPermission = appViewModel::onShowAlarmPermissionDialog,
                        onCreateTimeCapsule = profileViewModel::onCreateTimeCapsule,
                        onSaveTimeCapsuleReflection = profileViewModel::onSaveTimeCapsuleReflection
                    )
                }

                composable(MainDestination.FOCUS.route) {
                    com.productivitystreak.ui.screens.focus.FocusScreen(
                        onBackClick = { navController.navigateUp() }
                    )
                }

                composable(MainDestination.CHALLENGES.route) {
                    com.productivitystreak.ui.screens.challenges.ChallengesScreen(
                        onBackClick = { navController.navigateUp() }
                    )
                }
            }

            // Leaderboard Overlay
            if (showLeaderboard) {
                val leaderboardViewModel: com.productivitystreak.ui.screens.leaderboard.LeaderboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
                com.productivitystreak.ui.screens.leaderboard.LeaderboardScreen(
                    viewModel = leaderboardViewModel,
                    onBackClick = { showLeaderboard = false }
                )
            }

            if (isSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        scope.launch {
                            if (addUi.activeForm != null) {
                                onDismissAddForm()
                            } else {
                                onDismissAddMenu()
                            }
                        }
                    },
                    sheetState = sheetState,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    when (addUi.activeForm) {
                        null -> AddEntryMenuSheet(onEntrySelected = appViewModel::onAddEntrySelected)
                        AddEntryType.HABIT -> {
                            HabitFormSheet(
                                isSubmitting = addUi.isSubmitting,
                                onSubmit = { name, goal, unit, category, color, icon ->
                                    streakViewModel.createStreak(name, goal, unit, category, color, icon)
                                }
                            )
                        }
                        AddEntryType.WORD -> {
                            VocabularyFormSheet(
                                isSubmitting = addUi.isSubmitting,
                                onSubmit = { word, definition, example ->
                                    vocabularyViewModel.onSubmitVocabularyEntry(word, definition, example)
                                }
                            )
                        }
                        AddEntryType.JOURNAL -> {
                            JournalFormSheet(
                                isSubmitting = addUi.isSubmitting,
                                onSubmit = { mood, notes, highlights, gratitude, tomorrowGoals ->
                                    journalViewModel.onSubmitJournalEntry(mood, notes, highlights, gratitude, tomorrowGoals)
                                }
                            )
                        }
                        AddEntryType.TEMPLATE -> {
                            LaunchedEffect(Unit) {
                                onDismissAddMenu()
                                showTemplates = true
                            }
                        }
                        AddEntryType.TEACH -> {
                            val teachState by vocabularyViewModel.teachUiState.collectAsStateWithLifecycle()
                            TeachWordSheet(
                                uiState = teachState,
                                onWordChange = vocabularyViewModel::onTeachWordChanged,
                                onContextChange = vocabularyViewModel::onTeachContextChanged,
                                onGenerateLesson = vocabularyViewModel::onGenerateTeachingLesson,
                                onLogLesson = { lesson ->
                                    vocabularyViewModel.logLessonWord(lesson)
                                    appViewModel.setAddSubmitting(true)
                                    appViewModel.completeAddFlow()
                                },
                                onDismissLesson = vocabularyViewModel::resetTeachUiState,
                                onNextWord = vocabularyViewModel::suggestNewWord
                            )
                        }
                        AddEntryType.TIME_CAPSULE -> {
                            TimeCapsuleFormSheet(
                                isSubmitting = addUi.isSubmitting,
                                onSubmit = { message, goal, days ->
                                    profileViewModel.onCreateTimeCapsule(message, goal, days)
                                    appViewModel.setAddSubmitting(true)
                                    appViewModel.completeAddFlow()
                                }
                            )
                        }
                    }
                }
            }

            // Templates Screen Overlay
            if (showTemplates) {
                com.productivitystreak.ui.screens.templates.TemplatesScreen(
                    viewModel = appViewModel,
                    onNavigateBack = { showTemplates = false },
                    onImportTemplate = { template ->
                        appViewModel.importTemplate(template)
                        showTemplates = false
                    }
                )
            }

            // Asset Detail Overlay
            selectedAssetId?.let { assetId ->
                val discoverState by discoverViewModel.uiState.collectAsStateWithLifecycle()
                val asset = discoverState.assets.find { it.id == assetId }
                asset?.let {
                    AssetDetailScreen(
                        asset = it,
                        onDismiss = { selectedAssetId = null },
                        onComplete = { 
                            onAssetConsumed(assetId)
                            selectedAssetId = null
                        },
                        onTestPassed = { 
                            onAssetTestPassed(assetId)
                            selectedAssetId = null
                        }
                    )
                }
            }

            // Buddha Response Dialog
            val buddhaResponse by journalViewModel.buddhaResponse.collectAsStateWithLifecycle()
            buddhaResponse?.let { response ->
                androidx.compose.ui.window.Dialog(
                    onDismissRequest = {
                        journalViewModel.clearBuddhaResponse()
                        appViewModel.completeAddFlow()
                    }
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Spa,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "A Moment of Reflection",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = response,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            PrimaryButton(
                                text = "Continue",
                                onClick = {
                                    journalViewModel.clearBuddhaResponse()
                                    appViewModel.completeAddFlow()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
