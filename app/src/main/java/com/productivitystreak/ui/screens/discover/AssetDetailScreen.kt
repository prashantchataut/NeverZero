package com.productivitystreak.ui.screens.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.Asset
import com.productivitystreak.data.model.AssetCategory
import com.productivitystreak.data.model.AssetTestQuestion

@Composable
fun AssetDetailScreen(
    asset: Asset,
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    onTestPassed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var completed by remember { mutableStateOf(false) }
    var hasPassedTest by remember { mutableStateOf(asset.certified) }
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.98f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CategoryPill(category = asset.category)
                }
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .background(Color.Transparent)
                        .padding(4.dp)
                        .clickable { onDismiss() }
                )
            }

            if (hasPassedTest || asset.certified) {
                CertifiedBadge()
            }

            Text(
                text = asset.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!completed) {
                        completed = true
                        onComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !completed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (completed) "Marked as completed" else "Mark as completed")
            }

            asset.test?.let { test ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Retention check",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                var answers by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
                var feedback by remember { mutableStateOf<String?>(null) }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    test.questions.forEach { question ->
                        QuestionCard(
                            question = question,
                            selectedIndex = answers[question.id],
                            onOptionSelected = { index ->
                                answers = answers.toMutableMap().apply { put(question.id, index) }
                            }
                        )
                    }

                    Button(
                        onClick = {
                            val correct = test.questions.count { q ->
                                answers[q.id] == q.correctIndex
                            }
                            val passed = correct >= test.passingScore
                            feedback = if (passed) {
                                if (!hasPassedTest) {
                                    hasPassedTest = true
                                    onTestPassed()
                                }
                                "Certified: $correct / ${test.questions.size} correct."
                            } else {
                                "Score: $correct / ${test.questions.size}. Review the idea and try again." 
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Submit answers")
                    }

                    feedback?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(category: AssetCategory) {
    val label = when (category) {
        AssetCategory.PSYCHOLOGY_TRICKS -> "Psychology"
        AssetCategory.MEMORY_TECHNIQUES -> "Memory"
        AssetCategory.NEGOTIATION_SCRIPTS -> "Negotiation"
        AssetCategory.MARKETING_MENTAL_MODELS -> "Marketing Mental Models"
        AssetCategory.BOOK_SUMMARIES -> "Book Summary"
    }
    val color = when (category) {
        AssetCategory.PSYCHOLOGY_TRICKS -> Color(0xFF80CBC4)
        AssetCategory.MEMORY_TECHNIQUES -> Color(0xFF81D4FA)
        AssetCategory.NEGOTIATION_SCRIPTS -> Color(0xFFFFB74D)
        AssetCategory.MARKETING_MENTAL_MODELS -> Color(0xFFBA68C8)
        AssetCategory.BOOK_SUMMARIES -> Color(0xFFAED581)
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.18f), shape = MaterialTheme.shapes.extraLarge)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun CertifiedBadge() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width(10.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
            )
            Column {
                Text(
                    text = "Certified", 
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "You’ve passed the retention check for this asset.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: AssetTestQuestion,
    selectedIndex: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = question.prompt,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        )
                        .clickable { onOptionSelected(index) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
