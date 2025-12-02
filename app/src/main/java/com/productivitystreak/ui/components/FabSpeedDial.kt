package com.productivitystreak.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.AddEntryType
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing

data class FabAction(
    val icon: ImageVector,
    val label: String,
    val type: AddEntryType,
    val color: Color
)

/**
 * FAB Speed Dial
 * Floating Action Button that expands into a menu of contextual actions
 */
@Composable
fun FabSpeedDial(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onActionSelected: (AddEntryType) -> Unit,
    modifier: Modifier = Modifier
) {
    val designColors = NeverZeroTheme.designColors
    
    val actions = remember {
        listOf(
            FabAction(
                icon = com.productivitystreak.ui.icons.AppIcons.AddHabit,
                label = "Habit",
                type = AddEntryType.HABIT,
                color = Color(0xFF6C63FF)
            ),
            FabAction(
                icon = com.productivitystreak.ui.icons.AppIcons.AddJournal,
                label = "Journal",
                type = AddEntryType.JOURNAL,
                color = Color(0xFFFF6B9D)
            ),
            FabAction(
                icon = com.productivitystreak.ui.icons.AppIcons.AddWord,
                label = "Word",
                type = AddEntryType.WORD,
                color = Color(0xFF4ECDC4)
            ),
            FabAction(
                icon = com.productivitystreak.ui.icons.AppIcons.TeachWord,
                label = "Teach",
                type = AddEntryType.TEACH,
                color = Color(0xFFFFA726)
            )
        )
    }
    
    // Rotation animation for FAB icon
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab-rotation"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Action Items (appear above FAB)
        if (isExpanded) {
            Column(
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                horizontalAlignment = Alignment.End
            ) {
                actions.forEachIndexed { index, action ->
                    FabMenuItem(
                        action = action,
                        index = index,
                        isExpanded = isExpanded,
                        onClick = {
                            onActionSelected(action.type)
                            onToggle() // Close menu after selection
                        }
                    )
                }
            }
        }
        
        // Main FAB
        FloatingActionButton(
            onClick = onToggle,
            containerColor = designColors.primary,
            contentColor = Color.White,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Rounded.Close else Icons.Rounded.Add,
                contentDescription = if (isExpanded) "Close menu" else "Add",
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotation)
            )
        }
    }
}

@Composable
private fun FabMenuItem(
    action: FabAction,
    index: Int,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    // Staggered entrance animation
    val delay = index * 50
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            kotlinx.coroutines.delay(delay.toLong())
            visible = true
        } else {
            visible = false
        }
    }
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "menu-item-scale-$index"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(200),
        label = "menu-item-alpha-$index"
    )
    
    Row(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Icon Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(action.color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
