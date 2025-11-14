package com.productivitystreak.ui.screens.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.HabitTemplate
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
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
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
    
    val filteredTemplates = if (selectedCategory == null || selectedCategory == "all") {
        HabitTemplate.defaultTemplates
    } else {
        HabitTemplate.defaultTemplates.filter { it.category == selectedCategory }
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
                        selected = (selectedCategory ?: "all") == value,
                        onClick = { selectedCategory = value },
                        label = { Text(label) }
                    )
                }
            }

            Divider()

            // Templates List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(filteredTemplates) { template ->
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
            // Icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = Color(android.graphics.Color.parseColor(template.color)).copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = template.icon,
                        style = MaterialTheme.typography.headlineMedium
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
                    text = "${template.goalValue} ${template.unit} ${template.frequency}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (template.tip.isNotEmpty()) {
                    Text(
                        text = template.tip,
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
