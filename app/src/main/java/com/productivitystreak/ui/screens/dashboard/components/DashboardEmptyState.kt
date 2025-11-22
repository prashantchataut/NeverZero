package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.PrimaryButton
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun DashboardEmptyState(onAddHabitClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty-state-breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing-scale"
    )
    
    val designColors = NeverZeroTheme.designColors
    val cardBackgroundBrush = remember(designColors) {
        Brush.verticalGradient(
            listOf(
                designColors.surface.copy(alpha = 0.98f),
                designColors.backgroundAlt.copy(alpha = 0.96f)
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = designColors.textPrimary
        ),
        border = BorderStroke(1.dp, designColors.border.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackgroundBrush)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f))
                        .graphicsLayer {
                            scaleX = breathingScale
                            scaleY = breathingScale
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val primaryAlpha = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                    val oceanStart = NeverZeroTheme.gradientColors.OceanStart.copy(alpha = 0.9f)
                    val oceanEnd = NeverZeroTheme.gradientColors.OceanEnd.copy(alpha = 0.9f)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val minDim = kotlin.math.min(size.width, size.height)

                        drawCircle(
                            color = primaryAlpha,
                            radius = minDim / 3
                        )
                        drawCircle(
                            color = oceanStart,
                            radius = minDim / 6,
                            center = center
                        )
                        drawCircle(
                            color = oceanEnd,
                            radius = minDim / 9,
                            center = center + Offset(minDim / 9, -minDim / 9)
                        )
                    }
                }

                Text(
                    text = "No habits for today",
                    style = MaterialTheme.typography.titleLarge,
                    color = designColors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Enjoy your free time or add a new habit to keep the streak alive.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = designColors.textSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                PrimaryButton(
                    text = "Add a habit",
                    onClick = onAddHabitClick,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
