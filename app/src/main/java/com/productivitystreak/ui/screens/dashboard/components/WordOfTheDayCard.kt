package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.components.GlassCard
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun WordOfTheDayCard(
    wordOfTheDay: com.productivitystreak.ui.state.vocabulary.VocabularyWord?,
    modifier: Modifier = Modifier
) {
    if (wordOfTheDay == null) return

    val word = wordOfTheDay.word
    val pronunciation = "" // API might not return this yet, or we can add it later
    val type = "Word of the Day" // Or extract from definition if possible
    val definition = wordOfTheDay.definition
    val example = wordOfTheDay.example ?: "Use this word in a sentence today."

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = NeverZeroTheme.designColors.glassSurface.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORD OF THE DAY",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeverZeroTheme.designColors.secondary,
                    letterSpacing = androidx.compose.ui.unit.sp(1.5)
                )
                Text(
                    text = "AI Curated",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = pronunciation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            
            Text(
                text = type,
                style = MaterialTheme.typography.labelMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Text(
                text = definition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "\"$example\"",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
