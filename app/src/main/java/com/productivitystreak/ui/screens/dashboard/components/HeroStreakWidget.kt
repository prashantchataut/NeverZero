package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.components.GlowCard
import com.productivitystreak.ui.theme.AccentColors
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun HeroStreakWidget(
    streakCount: Int,
    modifier: Modifier = Modifier
) {
    GlowCard(
        modifier = modifier.fillMaxWidth(),
        glowColor = AccentColors.StreakFire,
        onClick = {} // Could navigate to stats
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = AccentColors.StreakFire,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "CURRENT STREAK",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentColors.StreakFire,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$streakCount",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black
                ),
                color = NeverZeroTheme.designColors.textPrimary
            )
            
            Text(
                text = "DAYS ON FIRE",
                style = MaterialTheme.typography.bodyMedium,
                color = NeverZeroTheme.designColors.textSecondary
            )
        }
    }
}
