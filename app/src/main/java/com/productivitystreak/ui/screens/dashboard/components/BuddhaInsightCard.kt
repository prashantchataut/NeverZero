package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.components.GlassCard
import com.productivitystreak.ui.components.PremiumGlassCard
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun BuddhaInsightCard(
    insight: String,
    modifier: Modifier = Modifier
) {
    PremiumGlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = {} // Optional: Expand or share
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "DAILY INSIGHT",
                style = MaterialTheme.typography.labelSmall,
                color = NeverZeroTheme.designColors.textSecondary,
                letterSpacing = 2.sp
            )
            
            Text(
                text = insight,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = NeverZeroTheme.designColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
