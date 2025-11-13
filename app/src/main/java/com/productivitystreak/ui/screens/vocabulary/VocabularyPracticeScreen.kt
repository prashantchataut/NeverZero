package com.productivitystreak.ui.screens.vocabulary

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.local.entity.VocabularyWordEntity
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun VocabularyPracticeScreen(
    words: List<VocabularyWordEntity>,
    onComplete: (correct: Int, total: Int) -> Unit,
    onReviewWord: (Long, Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    val currentWord = words.getOrNull(currentIndex)
    val totalWords = words.size

    // Generate multiple choice options
    val options = remember(currentWord) {
        currentWord?.let { word ->
            val otherDefinitions = words
                .filter { it.id != word.id }
                .shuffled()
                .take(3)
                .map { it.definition }

            (otherDefinitions + word.definition).shuffled()
        } ?: emptyList()
    }

    if (currentWord == null || currentIndex >= totalWords) {
        // Practice completed
        PracticeCompleteScreen(
            correct = correctCount,
            total = totalWords,
            onComplete = { onComplete(correctCount, totalWords) },
            onNavigateBack = onNavigateBack
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Practice Mode",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Question ${currentIndex + 1} of $totalWords",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Progress
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / totalWords },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScoreChip(
                        icon = Icons.Default.CheckCircle,
                        label = "Correct",
                        count = correctCount,
                        color = MaterialTheme.colorScheme.primary
                    )
                    ScoreChip(
                        icon = Icons.Default.Cancel,
                        label = "Incorrect",
                        count = currentIndex - correctCount,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Word Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "What is the definition of:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentWord.word,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Answer Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                options.forEachIndexed { index, option ->
                    AnswerOption(
                        text = option,
                        isSelected = selectedAnswer == option,
                        isCorrect = showFeedback && option == currentWord.definition,
                        isIncorrect = showFeedback && selectedAnswer == option && option != currentWord.definition,
                        onClick = {
                            if (!showFeedback) {
                                selectedAnswer = option
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (!showFeedback) {
                        if (selectedAnswer != null) {
                            showFeedback = true
                            val isCorrect = selectedAnswer == currentWord.definition
                            if (isCorrect) correctCount++
                            onReviewWord(currentWord.id, isCorrect)
                        }
                    } else {
                        // Move to next word
                        currentIndex++
                        selectedAnswer = null
                        showFeedback = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedAnswer != null,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (!showFeedback) "Submit Answer" else "Next Question",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isIncorrect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect -> MaterialTheme.colorScheme.primaryContainer
        isIncorrect -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isCorrect -> MaterialTheme.colorScheme.primary
        isIncorrect -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (isCorrect) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            } else if (isIncorrect) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun ScoreChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = color
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun PracticeCompleteScreen(
    correct: Int,
    total: Int,
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val percentage = (correct.toFloat() / total * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Practice Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You got $correct out of $total correct",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = {
                onComplete()
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Done", style = MaterialTheme.typography.titleMedium)
        }
    }
}
