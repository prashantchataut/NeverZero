package com.productivitystreak.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.screens.add.AddEntryMenuSheet
import com.productivitystreak.ui.screens.add.HabitFormSheet
import com.productivitystreak.ui.screens.add.JournalFormSheet
import com.productivitystreak.ui.screens.add.VocabularyFormSheet
import com.productivitystreak.ui.screens.add.TeachWordSheet
import com.productivitystreak.ui.screens.dashboard.DashboardScreen
import com.productivitystreak.ui.screens.discover.AssetDetailScreen
import com.productivitystreak.ui.screens.discover.DiscoverScreen
import com.productivitystreak.ui.screens.onboarding.OnboardingFlow
import com.productivitystreak.ui.screens.profile.ProfileScreen
import com.productivitystreak.ui.screens.stats.StatsScreen
import com.productivitystreak.ui.state.AddEntryType
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.UiMessageType
import com.productivitystreak.ui.theme.NeverZeroTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

enum class MainDestination { HOME, STATS, MENTOR, PROFILE, FOCUS, CHALLENGES }

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun NeverZeroApp(
    appViewModel: com.productivitystreak.ui.AppViewModel,
    viewModelFactory: androidx.lifecycle.ViewModelProvider.Factory
) {
    val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
    
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
        // Show a blank screen or a splash logo while loading preference
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

    var currentDestination by rememberSaveable { mutableStateOf(MainDestination.HOME) }
    var selectedAssetId by rememberSaveable { mutableStateOf<String?>(null) }
    var showSkillPaths by rememberSaveable { mutableStateOf(false) }
    var showTemplates by rememberSaveable { mutableStateOf(false) }
    var showBuddhaChat by rememberSaveable { mutableStateOf(false) }
    var showLeaderboard by rememberSaveable { mutableStateOf(false) }

    val addUi = uiState.addUiState
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isSheetVisible = addUi.isMenuOpen || addUi.activeForm != null

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
            NeverZeroBottomBar(
                current = currentDestination,
                onDestinationSelected = { destination ->
                    if (destination == MainDestination.HOME && currentDestination == MainDestination.HOME) return@NeverZeroBottomBar
                    currentDestination = destination
                    if (uiState.profileState.hapticsEnabled) {
                        haptics.performHapticFeedback(com.productivitystreak.ui.theme.HapticTokens.Selection)
                    }
                },
                onAddTapped = {
                    if (uiState.profileState.hapticsEnabled) {
                        haptics.performHapticFeedback(com.productivitystreak.ui.theme.HapticTokens.Impact)
                    }
                    onAddButtonTapped()
                },
                isMenuOpen = isSheetVisible
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = {
                    (androidx.compose.animation.fadeIn(com.productivitystreak.ui.theme.AnimationPresets.screenTransition()) +
                            androidx.compose.animation.scaleIn(initialScale = 0.95f, animationSpec = com.productivitystreak.ui.theme.AnimationPresets.screenTransition())) with
                            androidx.compose.animation.fadeOut(com.productivitystreak.ui.theme.AnimationPresets.screenTransition())
                },
                label = "main-nav"
            ) { destination ->
                when (destination) {
                    MainDestination.HOME -> {
                        val streakState by streakViewModel.uiState.collectAsStateWithLifecycle()
                        DashboardScreen(
                            streakUiState = streakState,
                            uiState = uiState,
                            onToggleTask = streakViewModel::onToggleTask,
                            onRefreshQuote = streakViewModel::fetchBuddhaInsight,
                            onAddHabitClick = appViewModel::onAddButtonTapped,
                            onSelectStreak = streakViewModel::onSelectStreak,
                            onAddOneOffTask = streakViewModel::addOneOffTask,
                            onToggleOneOffTask = streakViewModel::toggleOneOffTask,
                            onDeleteOneOffTask = streakViewModel::deleteOneOffTask,
                            onAssetSelected = { assetId -> selectedAssetId = assetId },
                            onOpenBuddhaChat = { showBuddhaChat = true },
                            onOpenJournal = { appViewModel.onAddEntrySelected(AddEntryType.JOURNAL) },
                            onOpenTimeCapsule = { appViewModel.onAddEntrySelected(AddEntryType.TIME_CAPSULE) },
                            onOpenLeaderboard = { showLeaderboard = true },
                            onOpenMonkMode = { currentDestination = MainDestination.FOCUS },
                            onOpenChallenges = { currentDestination = MainDestination.CHALLENGES }
                        )
                    }
                    MainDestination.PROFILE -> {
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
                    MainDestination.STATS -> {
                        val streakState by streakViewModel.uiState.collectAsStateWithLifecycle()
                        StatsScreen(
                            statsState = streakState.statsState,
                            onLeaderboardTypeSelected = streakViewModel::toggleLeaderboardType,
                            onOpenLeaderboard = { showLeaderboard = true }
                        )
                    }
                    MainDestination.MENTOR -> {
                        val app = appViewModel.getApplication<com.productivitystreak.NeverZeroApplication>()
                        com.productivitystreak.ui.screens.ai.BuddhaChatScreen(
                            userName = uiState.userName,
                            onBackClick = { currentDestination = MainDestination.HOME },
                            repository = app.buddhaRepository,
                            hapticsEnabled = uiState.profileState.hapticsEnabled,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    MainDestination.FOCUS -> {
                        com.productivitystreak.ui.screens.focus.FocusScreen(
                            onBackClick = { currentDestination = MainDestination.HOME }
                        )
                    }
                    MainDestination.CHALLENGES -> {
                        com.productivitystreak.ui.screens.challenges.ChallengesScreen(
                            onBackClick = { currentDestination = MainDestination.HOME }
                        )
                    }
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
                        // Use a coroutine to avoid blocking the UI thread during dismissal
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
                            // Close sheet and navigate to templates
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
                            com.productivitystreak.ui.screens.add.TimeCapsuleFormSheet(
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
                            com.productivitystreak.ui.components.PrimaryButton(
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

@Composable
private fun NeverZeroBottomBar(
    current: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit,
    onAddTapped: () -> Unit,
    isMenuOpen: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = com.productivitystreak.ui.theme.Spacing.md, vertical = com.productivitystreak.ui.theme.Spacing.sm),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .blur(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            tonalElevation = com.productivitystreak.ui.theme.Elevation.level2,
            shape = com.productivitystreak.ui.theme.Shapes.extraLarge
        ) {}

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shape = com.productivitystreak.ui.theme.Shapes.extraLarge
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = com.productivitystreak.ui.theme.Spacing.lg, vertical = com.productivitystreak.ui.theme.Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavItem(
                    icon = Icons.Outlined.Home,
                    label = "Home",
                    selected = current == MainDestination.HOME,
                    onClick = { onDestinationSelected(MainDestination.HOME) }
                )
                NavItem(
                    icon = Icons.Outlined.BarChart,
                    label = "Stats",
                    selected = current == MainDestination.STATS,
                    onClick = { onDestinationSelected(MainDestination.STATS) }
                )

                com.productivitystreak.ui.components.StyledFAB(
                    icon = Icons.Outlined.Add,
                    onClick = onAddTapped,
                    containerColor = NeverZeroTheme.gradientColors.PremiumStart,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    isExpanded = isMenuOpen
                )

                NavItem(
                    icon = Icons.Rounded.Spa,
                    label = "Mentor",
                    selected = current == MainDestination.MENTOR,
                    onClick = { onDestinationSelected(MainDestination.MENTOR) }
                )
                NavItem(
                    icon = Icons.Outlined.Person,
                    label = "Profile",
                    selected = current == MainDestination.PROFILE,
                    onClick = { onDestinationSelected(MainDestination.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        label = "scale"
    )
    
    val color by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp)) // Less aggressive rounding
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp) // More padding
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1,
            softWrap = false,
            fontSize = 10.sp, // Slightly smaller to fit
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}


