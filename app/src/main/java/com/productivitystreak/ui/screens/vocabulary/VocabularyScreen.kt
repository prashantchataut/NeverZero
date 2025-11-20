package com.productivitystreak.ui.screens.vocabulary

import androidx.compose.foundation.Canvas
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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.state.vocabulary.VocabularyState
import com.productivitystreak.ui.state.vocabulary.VocabularyWord

@Composable
fun VocabularyScreen(
    state: VocabularyState,
    onAddWord: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Your Vocabulary Journey",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Expanding your mind, one word at a time.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
            )
        }

        VocabularySummaryCard(
            streakDays = state.currentStreakDays,
            wordsAddedToday = state.wordsAddedToday
        )

        if (state.words.isEmpty()) {
            VocabularyEmptyState(onAddWord = onAddWord)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.words, key = { it.word }) { word ->
                    VocabularyWordRow(word = word)
                }
            }
        }
    }
}

@Composable
private fun VocabularySummaryCard(streakDays: Int, wordsAddedToday: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryPill(title = "Streak", value = "$streakDays days")
            SummaryPill(title = "Words today", value = "$wordsAddedToday")
        }
    }
}

@Composable
private fun SummaryPill(title: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun VocabularyWordRow(word: VocabularyWord) {
    val indicatorColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    val partOfSpeech = remember(word.word, word.definition) {
        extractPartOfSpeech(word.definition)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = indicatorColor)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = partOfSpeech ?: "(learning)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = word.definition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun VocabularyEmptyState(onAddWord: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .clip(RoundedCornerShape(28.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
        val primary = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.size(160.dp)) {
            val bookWidth = size.width * 0.7f
            val bookHeight = size.height * 0.45f
            val leftPage = Offset((size.width - bookWidth) / 2f, size.height * 0.2f)
            val rightPage = Offset(leftPage.x + bookWidth / 2f, leftPage.y)

            val minDim = kotlin.math.min(size.width, size.height)
            
            drawRoundRect(
                color = surfaceVariant.copy(alpha = 0.6f),
                topLeft = leftPage,
                size = androidx.compose.ui.geometry.Size(bookWidth / 2f - 8f, bookHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
            )
            drawRoundRect(
                color = surfaceVariant.copy(alpha = 0.6f),
                topLeft = rightPage,
                size = androidx.compose.ui.geometry.Size(bookWidth / 2f - 8f, bookHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
            )
            drawLine(
                color = primary.copy(alpha = 0.4f),
                start = Offset(size.width / 2f, leftPage.y),
                end = Offset(size.width / 2f, leftPage.y + bookHeight),
                strokeWidth = 6f
            )

            drawCircle(
                color = primary.copy(alpha = 0.15f),
                radius = minDim / 5f,
                center = Offset(size.width / 2f, leftPage.y - 30f),
                style = Stroke(width = 10f)
            )
            drawCircle(
                color = primary.copy(alpha = 0.6f),
                radius = 8f,
                center = Offset(size.width / 2f, leftPage.y - 30f)
            )
        }

        Text(
            text = "No words yet? Start expanding your vocabulary today!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(
            onClick = onAddWord,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Add your first word")
        }
    }
}

private fun extractPartOfSpeech(definition: String): String? {
    val regex = Regex("\\(([^)]+)\\)")
    val match = regex.find(definition)
    return match?.groupValues?.getOrNull(1)?.lowercase()?.let { "($it)" }
}
