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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.icons.AppIcons
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing

/**
 * Standard Quest Row Component
 * Displays a time-bound task/quest
 */
@Composable
fun QuestRow(
    title: String,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NeverZeroTheme.designColors.surface)
            .clickable(onClick = onToggle)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isCompleted) 
                            NeverZeroTheme.designColors.primary 
                        else 
                            NeverZeroTheme.designColors.border
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = AppIcons.Check,
                        contentDescription = null,
                        tint = NeverZeroTheme.designColors.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCompleted) 
                    NeverZeroTheme.designColors.textSecondary 
                else 
                    NeverZeroTheme.designColors.textPrimary,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
        }
        
        // Delete Button
        Icon(
            imageVector = AppIcons.Delete,
            contentDescription = "Delete quest",
            tint = NeverZeroTheme.designColors.textSecondary.copy(alpha = 0.5f),
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onDelete)
        )
    }
}
