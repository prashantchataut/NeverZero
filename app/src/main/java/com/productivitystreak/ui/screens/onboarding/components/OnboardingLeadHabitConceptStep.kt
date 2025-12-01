package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
fun OnboardingLeadHabitConceptStep() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant), // Light Gray
            contentAlignment = Alignment.Center
        ) {
            val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
            val successColor = MaterialTheme.colorScheme.primary // Vibrant Green
            val oceanStart = MaterialTheme.colorScheme.primary
            val oceanEnd = MaterialTheme.colorScheme.primaryContainer
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val start = Offset(size.width * 0.15f, size.height * 0.65f)
                val end = Offset(size.width * 0.85f, size.height * 0.35f)
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(
                            oceanStart,
                            oceanEnd
                        )
                    ),
                    start = start,
                    end = end,
                    strokeWidth = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                val dotPositions = listOf(0.15f, 0.35f, 0.55f, 0.75f, 0.9f)
                dotPositions.forEachIndexed { index, fraction ->
                    val t = fraction
                    val x = size.width * t
                    val y = size.height * (0.65f - 0.3f * t)
                    drawCircle(
                        color = if (index == 0)
                            surfaceColor
                        else successColor,
                        radius = if (index == 0) 9.dp.toPx() else 6.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }

        Text(
            text = "The Keystone Protocol",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Don't build 10 habits. Build one protocol that pulls everything else with it.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
