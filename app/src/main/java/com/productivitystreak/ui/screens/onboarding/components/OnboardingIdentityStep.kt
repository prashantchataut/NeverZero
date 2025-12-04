package com.productivitystreak.ui.screens.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.icons.AppIcons
import com.productivitystreak.ui.state.onboarding.OnboardingCategory

@Composable
fun OnboardingIdentityStep(
    categories: List<OnboardingCategory>,
    selected: Set<String>,
    onToggleCategory: (String) -> Unit
) {
    val deepForest = Color(0xFF1A2C24)
    val creamWhite = Color(0xFFF5F5DC)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What is your mission?",
            style = MaterialTheme.typography.headlineSmall,
            color = deepForest
        )
        Text(
            text = "Choose focus areas to personalize your rituals.",
            style = MaterialTheme.typography.bodyMedium,
            color = deepForest.copy(alpha = 0.7f)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(categories, key = { it.id }) { item ->
                val isSelected = selected.contains(item.id)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) deepForest else creamWhite,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) deepForest else deepForest.copy(alpha = 0.2f)
                    ),
                    onClick = { onToggleCategory(item.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.forCategory(item.id),
                            contentDescription = item.label,
                            tint = if (isSelected) creamWhite else deepForest
                        )
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isSelected) creamWhite else deepForest,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
