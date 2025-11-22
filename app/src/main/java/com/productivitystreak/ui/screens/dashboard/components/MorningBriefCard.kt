package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.Quote
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.theme.NeverZeroTheme
import kotlinx.coroutines.delay

@Composable
fun MorningBriefCard(
    quote: Quote?,
    isQuoteLoading: Boolean,
    todayTasks: List<DashboardTask>,
    onRefreshQuote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val designColors = NeverZeroTheme.designColors
    val completed = todayTasks.count { it.isCompleted }
    val total = todayTasks.size
    val backgroundBrush = remember(designColors) {
        Brush.verticalGradient(
            listOf(
                designColors.surface.copy(alpha = 0.96f),
                designColors.backgroundAlt.copy(alpha = 0.94f)
            )
        )
    }

    val progressBarBrush = remember(designColors) {
        Brush.horizontalGradient(
            listOf(
                designColors.primary.copy(alpha = 0.9f),
                designColors.secondary.copy(alpha = 0.9f)
            )
        )
    }

    var tapped by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (tapped) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "buddha-tap-scale"
    )

    LaunchedEffect(tapped) {
        if (tapped) {
            delay(120)
            tapped = false
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer(
                scaleX = tapScale,
                scaleY = tapScale
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = {
                tapped = true
                onRefreshQuote()
            }),
        colors = CardDefaults.cardColors(
            containerColor = designColors.surface
        ),
        border = BorderStroke(1.dp, designColors.border.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(brush = backgroundBrush)
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Morning brief",
                            style = MaterialTheme.typography.labelMedium,
                            color = designColors.textSecondary
                        )
                        Text(
                            text = "Keep the streak breathing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = designColors.textSecondary
                        )
                    }

                    if (isQuoteLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = designColors.primary
                        )
                    } else {
                        Text(
                            text = "Refresh",
                            style = MaterialTheme.typography.labelMedium,
                            color = designColors.primary
                        )
                    }
                }

                quote?.let {
                    Text(
                        text = "\"${it.text}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = designColors.textPrimary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = it.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = designColors.textSecondary
                    )
                } ?: run {
                    Text(
                        text = "Today is a clean slate. Take one decisive action.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = designColors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (total > 0) {
                    val progressText = "$completed / $total habits logged"
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.labelSmall,
                        color = designColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val fraction = (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(designColors.border.copy(alpha = 0.5f))
                    ) {
                        if (fraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(4.dp)
                                    .background(progressBarBrush)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No habits scheduled. Set one tiny target.",
                        style = MaterialTheme.typography.labelSmall,
                        color = designColors.textSecondary
                    )
                }
            }
        }
    }
}
