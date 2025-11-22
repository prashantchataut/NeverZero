package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.productivitystreak.R

@Composable
fun StepHeader(currentStep: Int, totalSteps: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = "Never Zero logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .scale(0.8f)
                .padding(bottom = 4.dp)
        )

        Text(
            text = when (currentStep) {
                0 -> "Build habits that last"
                1 -> "What matters to you?"
                2 -> "Start with one small win"
                3 -> "Let us nudge you gently"
                else -> "Set your first habit"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(totalSteps) { index ->
                val isActive = index == currentStep
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        )
                )
            }
        }
    }
}
