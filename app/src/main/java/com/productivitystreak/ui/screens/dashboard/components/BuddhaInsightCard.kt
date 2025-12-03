package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.components.InteractiveGlassCard
import com.productivitystreak.ui.theme.NeverZeroTheme

@Composable
fun BuddhaInsightCard(
    insight: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    InteractiveGlassCard(
        onClick = onRefresh,
        modifier = modifier.fillMaxWidth().height(160.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Paper texture background (simulated with color)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFDFBF7)) // Off-white paper color
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DAILY WISDOM",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8D6E63), // Brownish
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Text(
                    text = insight,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    ),
                    color = Color(0xFF3E2723), // Dark Brown
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "Tap for insight",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8D6E63).copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
