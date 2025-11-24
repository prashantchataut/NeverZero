package com.productivitystreak.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.productivitystreak.ui.screens.onboarding.components.*
import com.productivitystreak.ui.state.onboarding.OnboardingState
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingFlow(
    onboardingState: OnboardingState,
    onToggleOnboardingCategory: (String) -> Unit,
    onSetOnboardingGoal: (String) -> Unit,
    onSetOnboardingCommitment: (Int, Int) -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onToggleNotificationsAllowed: (Boolean) -> Unit,
    onSetReminderTime: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onHabitNameChange: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    onCompleteOnboarding: () -> Unit,
    onDismissOnboarding: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {
    val onboarding = onboardingState
    val coroutineScope = rememberCoroutineScope()
    var showFinishRipple by remember { mutableStateOf(false) }
    val finishRipple = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NeverZeroTheme.gradientColors.PremiumStart.copy(alpha = 0.18f),
                        NeverZeroTheme.gradientColors.PremiumEnd.copy(alpha = 0.08f)
                    )
                )
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                StepHeader(currentStep = onboarding.currentStep, totalSteps = onboarding.totalSteps)

                AnimatedContent(
                    targetState = onboarding.currentStep,
                    transitionSpec = {
                        fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) togetherWith
                            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow))
                    },
                    label = "onboarding-steps"
                ) { step ->
                    when (step) {
                        0 -> OnboardingWelcomeStep()
                        1 -> OnboardingIdentityStep(
                            categories = onboarding.categories,
                            selected = onboarding.selectedCategories,
                            onToggleCategory = onToggleOnboardingCategory
                        )
                        2 -> OnboardingLeadHabitConceptStep()
                        3 -> OnboardingNotificationStep(
                            onEnableNotifications = {
                                onToggleNotificationsAllowed(true)
                                onRequestNotificationPermission()
                                onNextStep()
                            },
                            onSkip = {
                                onToggleNotificationsAllowed(false)
                                onNextStep()
                            }
                        )
                        else -> OnboardingPersonalizationStep(
                            userName = onboarding.userName,
                            onUserNameChange = onUserNameChange,
                            habitName = onboarding.goalHabit,
                            onHabitNameChange = onHabitNameChange,
                            selectedIcon = onboarding.selectedIcon,
                            onIconSelected = onIconSelected,
                            dailyReminderEnabled = onboarding.allowNotifications,
                            onDailyReminderToggle = onToggleNotificationsAllowed,
                            onComplete = {
                                if (!showFinishRipple) {
                                    coroutineScope.launch {
                                        showFinishRipple = true
                                        finishRipple.snapTo(0f)
                                        finishRipple.animateTo(
                                            1f,
                                            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                                        )
                                        showFinishRipple = false
                                        onCompleteOnboarding()
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Only show default footer for first 3 steps (0, 1, 2)
            // Steps 3 and 4 have their own action buttons
            if (onboarding.currentStep < 3) {
                OnboardingFooter(
                    currentStep = onboarding.currentStep,
                    totalSteps = onboarding.totalSteps,
                    canGoBack = onboarding.currentStep > 0,
                    isFinalStep = false,
                    onBack = {
                        if (onboarding.currentStep == 0) onDismissOnboarding() else onPreviousStep()
                    },
                    onPrimaryClick = onNextStep
                )
            }
        }

        if (showFinishRipple) {
            val premiumStart = NeverZeroTheme.gradientColors.PremiumStart
            val premiumEnd = NeverZeroTheme.gradientColors.PremiumEnd
            Canvas(modifier = Modifier.matchParentSize()) {
                val radius = size.maxDimension * finishRipple.value
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            premiumStart,
                            premiumEnd
                        )
                    ),
                    radius = radius,
                    center = center,
                    alpha = (1f - finishRipple.value).coerceAtLeast(0f)
                )
            }
        }
    }
}
