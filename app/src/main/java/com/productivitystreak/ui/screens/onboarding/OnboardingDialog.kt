package com.productivitystreak.ui.screens.onboarding

// Onboarding UI removed during architectural sanitization.

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.onboarding.OnboardingCategory
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.utils.PermissionManager
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
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
            Canvas(modifier = Modifier.matchParentSize()) {
                val premiumStart = NeverZeroTheme.gradientColors.PremiumStart
                val premiumEnd = NeverZeroTheme.gradientColors.PremiumEnd
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

@Composable
private fun StepHeader(currentStep: Int, totalSteps: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Never Zero",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = when (currentStep) {
                0 -> "Build habits that last"
                1 -> "What matters to you?"
                2 -> "Start with one small win"
                3 -> "Let us nudge you gently"
                else -> "Set your first habit"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(totalSteps) { index ->
                val isActive = index == currentStep
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        )
                )
            }
        }
    }
}

@Composable
private fun OnboardingWelcomeStep() {
    val infiniteTransition = rememberInfiniteTransition(label = "sunrise-pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunrise-radius"
    )

    val drift by infiniteTransition.animateFloat(
        initialValue = -0.04f,
        targetValue = 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunrise-drift"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(48.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val baseCenter = Offset(size.width / 2, size.height * 0.65f)
                val center = baseCenter.copy(x = baseCenter.x + drift * size.width * 0.2f)
                val sunriseStart = NeverZeroTheme.gradientColors.SunriseStart
                val sunriseEnd = NeverZeroTheme.gradientColors.SunriseEnd
                val minDim = kotlin.math.min(size.width, size.height)
                val radius = (minDim / 3f) * pulse
                drawCircle(
                    brush = Brush.verticalGradient(
                        listOf(
                            sunriseStart,
                            sunriseEnd
                        )
                    ),
                    radius = radius,
                    center = center
                )
                drawCircle(
                    color = MaterialTheme.colorScheme.surface,
                    radius = radius * 1.15f,
                    center = center.copy(y = center.y + radius * 0.9f)
                )
            }
        }

        Text(
            text = "You donâ€™t need a perfect day â€” just never hit zero.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OnboardingIdentityStep(
    categories: List<OnboardingCategory>,
    selected: Set<String>,
    onToggleCategory: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Choose a few areas you want to protect from going to zero.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            categories.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        val isSelected = selected.contains(item.id)
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            tonalElevation = if (isSelected) 4.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surface,
                            onClick = { onToggleCategory(item.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = item.emoji, fontSize = 18.sp)
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingLeadHabitConceptStep() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            val surfaceColor = MaterialTheme.colorScheme.surface
            val successColor = NeverZeroTheme.semanticColors.Success
            Canvas(modifier = Modifier.fillMaxSize()) {
                val start = Offset(size.width * 0.15f, size.height * 0.65f)
                val end = Offset(size.width * 0.85f, size.height * 0.35f)
                val oceanStart = NeverZeroTheme.gradientColors.OceanStart
                val oceanEnd = NeverZeroTheme.gradientColors.OceanEnd
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(
                            oceanStart,
                            oceanEnd
                        )
                    ),
                    start = start,
                    end = end,
                    strokeWidth = 8.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                val dotPositions = listOf(0.15f, 0.35f, 0.55f, 0.75f, 0.9f)
                dotPositions.forEachIndexed { index, fraction ->
                    val t = fraction
                    val x = size.width * t
                    val y = size.height * (0.65f - 0.3f * t)
                    drawCircle(
                        color = if (index == 0)
                            surfaceColor
                        else successColor,
                        radius = if (index == 0) 9.dp.toPx() else 6.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }

        Text(
            text = "Weâ€™ll start with one small habit â€” your lead habit.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Nail this first habit and everything else becomes easier. No overwhelm, just daily momentum.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun OnboardingPermissionStep(
    allowNotifications: Boolean,
    onToggleNotificationsAllowed: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    reminderTime: String,
    onSetReminderTime: (String) -> Unit
) {
    val context = LocalContext.current
    val notificationsGranted = !PermissionManager.shouldRequestNotificationPermission(context)
    val exactAlarmsGranted = PermissionManager.canScheduleExactAlarms(context)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Never Zero works best when we can gently remind you at the right moments.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
        )

        PermissionCard(
            title = "Notifications",
            description = "Daily nudges so your streak never silently breaks.",
            enabled = notificationsGranted && allowNotifications,
            actionLabel = if (notificationsGranted) "Enabled" else "Enable",
            onClick = {
                if (!notificationsGranted) onRequestNotificationPermission()
                onToggleNotificationsAllowed(true)
            }
        )

        PermissionCard(
            title = "Exact alarms",
            description = "Guaranteed reminders for high-priority habits.",
            enabled = exactAlarmsGranted,
            actionLabel = if (exactAlarmsGranted) "Enabled" else "Enable",
            onClick = {
                if (!exactAlarmsGranted) onRequestExactAlarmPermission()
            }
        )

        Text(
            text = "When should we nudge you?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val options = listOf("08:30 AM", "01:00 PM", "08:30 PM")
            options.forEach { option ->
                val selected = option == reminderTime
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    else Color.Transparent,
                    tonalElevation = if (selected) 2.dp else 0.dp,
                    onClick = { onSetReminderTime(option) }
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Text(
            text = "If youâ€™d rather skip this for now, you can always turn reminders on later from Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    enabled: Boolean,
    actionLabel: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled) NeverZeroTheme.semanticColors.Success.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (enabled) NeverZeroTheme.semanticColors.Success
                            else MaterialTheme.colorScheme.primary
                        )
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) NeverZeroTheme.semanticColors.Success
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun OnboardingFirstHabitStep(
    goal: String,
    commitmentMinutes: Int,
    frequencyPerWeek: Int,
    onSetGoal: (String) -> Unit,
    onSetCommitment: (Int, Int) -> Unit
) {
    var localGoal by remember { mutableStateOf(goal) }
    var minutes by remember { mutableStateOf(commitmentMinutes.coerceIn(1, 60)) }
    var frequency by remember { mutableStateOf(frequencyPerWeek.coerceIn(1, 7)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pick one lead habit to start.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
        )

        val quickPicks = listOf(
            "Drink a glass of water",
            "Read 5 pages",
            "Walk for 5 minutes",
            "Write one sentence"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPicks.take(2).forEach { title ->
                QuickPickPill(
                    label = title,
                    selected = localGoal == title,
                    onClick = {
                        localGoal = title
                        onSetGoal(title)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPicks.drop(2).forEach { title ->
                QuickPickPill(
                    label = title,
                    selected = localGoal == title,
                    onClick = {
                        localGoal = title
                        onSetGoal(title)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        androidx.compose.material3.OutlinedTextField(
            value = localGoal,
            onValueChange = {
                localGoal = it
                onSetGoal(it)
            },
            label = { Text("Or describe your own habit") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        CommitmentRow(
            minutes = minutes,
            frequencyPerWeek = frequency,
            onMinutesChanged = {
                minutes = it
                onSetCommitment(minutes, frequency)
            },
            onFrequencyChanged = {
                frequency = it
                onSetCommitment(minutes, frequency)
            }
        )
    }
}

@Composable
private fun QuickPickPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = if (selected) 4.dp else 0.dp,
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        else MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CommitmentRow(
    minutes: Int,
    frequencyPerWeek: Int,
    onMinutesChanged: (Int) -> Unit,
    onFrequencyChanged: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "How small should we keep it?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CommitmentChip(
                label = "$minutes min/day",
                onMinus = { if (minutes > 1) onMinutesChanged(minutes - 1) },
                onPlus = { if (minutes < 60) onMinutesChanged(minutes + 1) },
                modifier = Modifier.weight(1f)
            )
            CommitmentChip(
                label = "$frequencyPerWeek days/week",
                onMinus = { if (frequencyPerWeek > 1) onFrequencyChanged(frequencyPerWeek - 1) },
                onPlus = { if (frequencyPerWeek < 7) onFrequencyChanged(frequencyPerWeek + 1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CommitmentChip(
    label: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MiniIconButton(text = "-") { onMinus() }
                MiniIconButton(text = "+") { onPlus() }
            }
        }
    }
}

@Composable
private fun MiniIconButton(text: String, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OnboardingFooter(
    currentStep: Int,
    totalSteps: Int,
    canGoBack: Boolean,
    isFinalStep: Boolean,
    onBack: () -> Unit,
    onPrimaryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canGoBack) {
            Text(
                text = "Back",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.clickable(onClick = onBack)
            )
        } else {
            Spacer(modifier = Modifier.width(40.dp))
        }

        val primaryLabel = if (isFinalStep) "Finish" else "Continue"
        PrimaryOnboardingButton(text = primaryLabel, onClick = onPrimaryClick)
    }
}

@Composable
private fun PrimaryOnboardingButton(
    text: String,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    Button(
        onClick = {
            pressed = true
            onClick()
            pressed = false
        },
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .height(48.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

