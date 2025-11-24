package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivitystreak.R
import com.productivitystreak.ui.components.GradientPrimaryButton
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing

@Composable
fun OnboardingNotificationStep(
    onEnableNotifications: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(Spacing.xl))

        // Illustration Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for the calendar/notification illustration
            // Using a large icon for now if no specific asset is available, 
            // or a composition of icons to mimic the design
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = NeverZeroTheme.designColors.primary
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Text(
                text = "Stay on Track with\nReminders",
                style = MaterialTheme.typography.headlineMedium,
                color = NeverZeroTheme.designColors.textPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Enable notifications to build consistency and never miss a day on your journey.",
                style = MaterialTheme.typography.bodyMedium,
                color = NeverZeroTheme.designColors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Spacing.md)
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Benefit Items
            NotificationBenefitItem(
                icon = Icons.Default.Notifications,
                title = "Daily Reminders",
                description = "Gentle nudges to help you complete your goals."
            )
            
            // Using placeholder icons that would likely exist or be standard
            NotificationBenefitItem(
                icon = Icons.Default.Notifications, // Replace with Fire/Warning icon if available
                title = "Streak Warnings",
                description = "Stay motivated with alerts when your streak is at risk."
            )
            
            NotificationBenefitItem(
                icon = Icons.Default.Notifications, // Replace with Celebration/Star icon
                title = "Milestone Celebrations",
                description = "Celebrate your achievements and progress."
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            GradientPrimaryButton(
                text = "Enable Notifications",
                onClick = onEnableNotifications,
                modifier = Modifier.fillMaxWidth()
            )
            
            TextButton(onClick = onSkip) {
                Text(
                    text = "Maybe Later",
                    color = NeverZeroTheme.designColors.textSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}

@Composable
private fun NotificationBenefitItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = NeverZeroTheme.designColors.surfaceElevated,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeverZeroTheme.designColors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = NeverZeroTheme.designColors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = NeverZeroTheme.designColors.textSecondary
            )
        }
    }
}
