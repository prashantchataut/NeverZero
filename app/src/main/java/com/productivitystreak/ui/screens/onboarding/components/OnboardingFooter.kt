package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.PrimaryButton
import com.productivitystreak.ui.components.StyledTextButton
import com.productivitystreak.ui.theme.Spacing

@Composable
fun OnboardingFooter(
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
            .padding(top = Spacing.xl),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canGoBack) {
            StyledTextButton(
                text = "Back",
                onClick = onBack
            )
        } else {
            Spacer(modifier = Modifier.width(Spacing.xxl))
        }

        val primaryLabel = if (isFinalStep) "Finish" else "Continue"
        PrimaryButton(
            text = primaryLabel,
            onClick = onPrimaryClick,
            modifier = Modifier.width(140.dp)
        )
    }
}
