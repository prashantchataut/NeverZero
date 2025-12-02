package com.productivitystreak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.icons.AppIcons
import com.productivitystreak.ui.theme.Spacing

/**
 * Standard XP Button Component
 * Displays XP amount with claim action
 */
@Composable
fun XpButton(
    xpAmount: Int,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = if (enabled) 0.2f else 0.1f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = AppIcons.Star,
                contentDescription = null,
                tint = accentColor.copy(alpha = if (enabled) 1f else 0.5f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "+$xpAmount XP",
                style = MaterialTheme.typography.labelMedium,
                color = accentColor.copy(alpha = if (enabled) 1f else 0.5f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
