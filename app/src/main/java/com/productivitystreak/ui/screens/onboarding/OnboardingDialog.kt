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
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingFlow(
    uiState: AppUiState,
    onToggleOnboardingCategory: (String) -> Unit,
    onSetOnboardingGoal: (String) -> Unit,
    onSetOnboardingCommitment: (Int, Int) -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onToggleNotificationsAllowed: (Boolean) -> Unit,
    onSetReminderTime: (String) -> Unit,
    onCompleteOnboarding: () -> Unit,
    onDismissOnboarding: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {
    val onboarding = uiState.onboardingState
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
                        3 -> OnboardingPermissionStep(
                            allowNotifications = onboarding.allowNotifications,
                            onToggleNotificationsAllowed = onToggleNotificationsAllowed,
                            onRequestNotificationPermission = onRequestNotificationPermission,
                            onRequestExactAlarmPermission = onRequestExactAlarmPermission,
                            reminderTime = onboarding.reminderTime,
                            onSetReminderTime = onSetReminderTime
                        )
                        else -> OnboardingFirstHabitStep(
                            goal = onboarding.goalHabit,
                            commitmentMinutes = onboarding.commitmentDurationMinutes,
                            frequencyPerWeek = onboarding.commitmentFrequencyPerWeek,
                            onSetGoal = onSetOnboardingGoal,
                            onSetCommitment = onSetOnboardingCommitment
                        )
                    }
                }
            }

            val isFinalStep = onboarding.currentStep == onboarding.totalSteps - 1
            OnboardingFooter(
                currentStep = onboarding.currentStep,
                totalSteps = onboarding.totalSteps,
                canGoBack = onboarding.currentStep > 0,
                isFinalStep = isFinalStep,
                onBack = {
                    if (onboarding.currentStep == 0) onDismissOnboarding() else onPreviousStep()
                },
                onPrimaryClick = {
                    if (isFinalStep) {
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
                    } else {
                        onNextStep()
                    }
                }
            )
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
