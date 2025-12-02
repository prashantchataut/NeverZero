package com.productivitystreak.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.productivitystreak.data.model.Challenge
import com.productivitystreak.data.repository.ChallengeRepository
import com.productivitystreak.ui.components.GlassCard
import com.productivitystreak.ui.components.PrimaryButton
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing

@Composable
fun ChallengesScreen(
    onBackClick: () -> Unit,
    repository: ChallengeRepository = remember { ChallengeRepository() } // Simple DI for now
) {
    val challenges by repository.getAvailableChallenges().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeverZeroTheme.designColors.background)
            .padding(Spacing.md)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = Spacing.lg)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NeverZeroTheme.designColors.textPrimary
                )
            }
            Text(
                text = "Protocol Challenges",
                style = MaterialTheme.typography.displaySmall,
                color = NeverZeroTheme.designColors.textPrimary,
                modifier = Modifier.padding(start = Spacing.sm)
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            items(challenges) { challenge ->
                ChallengeCard(challenge = challenge)
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${challenge.durationDays} Days • ${challenge.difficulty}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(android.graphics.Color.parseColor(challenge.colorHex))
                    )
                }
                // Professional icon instead of emoji
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color(android.graphics.Color.parseColor(challenge.colorHex)).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (challenge.iconId) {
                            "brain" -> com.productivitystreak.ui.icons.AppIcons.ChallengeBrain
                            "sword" -> com.productivitystreak.ui.icons.AppIcons.ChallengeSword
                            "phone" -> com.productivitystreak.ui.icons.AppIcons.ChallengePhone
                            else -> com.productivitystreak.ui.icons.AppIcons.ChallengeTrophy
                        },
                        contentDescription = challenge.title,
                        tint = Color(android.graphics.Color.parseColor(challenge.colorHex)),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            PrimaryButton(
                text = "Accept Quest",
                onClick = { /* TODO: Join logic */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
