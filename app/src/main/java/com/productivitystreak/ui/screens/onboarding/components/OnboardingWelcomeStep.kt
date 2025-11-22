package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun OnboardingWelcomeStep() {
    val infiniteTransition = rememberInfiniteTransition(label = "sunrise-pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunrise-radius"
    )

    val drift by infiniteTransition.animateFloat(
        initialValue = -0.04f,
        targetValue = 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunrise-drift"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(48.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            val sunriseStart = NeverZeroTheme.gradientColors.SunriseStart
            val sunriseEnd = NeverZeroTheme.gradientColors.SunriseEnd
            val surfaceColor = MaterialTheme.colorScheme.surface
            Canvas(modifier = Modifier.fillMaxSize()) {
                val baseCenter = Offset(size.width / 2, size.height * 0.65f)
                val center = baseCenter.copy(x = baseCenter.x + drift * size.width * 0.2f)
                val minDim = kotlin.math.min(size.width, size.height)
                val radius = (minDim / 3f) * pulse
                drawCircle(
                    brush = Brush.verticalGradient(
                        listOf(
                            sunriseStart,
                            sunriseEnd
                        )
                    ),
                    radius = radius,
                    center = center
                )
                drawCircle(
                    color = surfaceColor,
                    radius = radius * 1.15f,
                    center = center.copy(y = center.y + radius * 0.9f)
                )
            }
        }

        Text(
            text = "You don’t need a perfect day — just never hit zero.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}
