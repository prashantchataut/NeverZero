package com.productivitystreak.ui.screens.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.HabitTemplate
import com.productivitystreak.data.model.HabitTemplates
import com.productivitystreak.ui.theme.Spacing

/**
 * Phase 4: Habit Templates Browser
 * Browse 21 pre-built templates and create streaks with one tap
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesBrowserScreen(
    onCreateFromTemplate: (HabitTemplate) -> Unit,
    onClose: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("all") }
    
    val categories = listOf(
        "All" to "all",
        "Reading" to "reading",
        "Vocabulary" to "vocabulary", 
        "Wellness" to "wellness",
        "Learning" to "learning",
        "Creativity" to "creativity",
        "Productivity" to "productivity",
        "Social" to "social"
    )
    
    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory == "all") {
            HabitTemplates.templates
        } else {
            HabitTemplates.templates.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habit Templates") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(categories) { (label, value) ->
                    FilterChip(
                        selected = selectedCategory == value,
                        onClick = { selectedCategory = value },
                        label = { Text(label) }
                    )
                }
            }

            HorizontalDivider()

            // Templates List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(filteredTemplates, key = { it.id }) { template ->
                    TemplateCard(
                        template = template,
                        onClick = { onCreateFromTemplate(template) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: HabitTemplate,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = Color(android.graphics.Color.parseColor(template.color)).copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = template.icon,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${template.goalPerDay} ${template.unit} daily",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                template.tips.firstOrNull()?.let { tip ->
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Add button
            IconButton(onClick = onClick) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add template",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
