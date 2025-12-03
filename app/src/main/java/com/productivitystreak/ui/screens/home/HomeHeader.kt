package com.productivitystreak.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.Quote
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun HomeHeader(
    userName: String,
    quote: Quote?,
    level: Int,
    currentXp: Int,
    xpToNextLevel: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Greeting & Quote
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Good Morning, $userName",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NeverZeroTheme.designColors.textPrimary
            )
            if (quote != null) {
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeverZeroTheme.designColors.textSecondary,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Right: Level Badge
        LevelBadge(
            level = level,
            currentXp = currentXp,
            xpToNextLevel = xpToNextLevel
        )
    }
}

@Composable
fun LevelBadge(
    level: Int,
    currentXp: Int,
    xpToNextLevel: Int
) {
    val totalLevelXp = currentXp + xpToNextLevel
    val progress = if (totalLevelXp > 0) currentXp.toFloat() / totalLevelXp else 0f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(NeverZeroTheme.designColors.cardBackground)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "LVL $level",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = NeverZeroTheme.designColors.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(CircleShape),
            color = NeverZeroTheme.designColors.primary,
            trackColor = NeverZeroTheme.designColors.primary.copy(alpha = 0.2f),
        )
    }
}
