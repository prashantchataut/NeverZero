package com.productivitystreak.ui.screens.reflection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReflectionScreen(
    onSave: (mood: Int, notes: String, highlights: String?, challenges: String?, gratitude: String?) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableIntStateOf(3) }
    var notes by remember { mutableStateOf("") }
    var highlights by remember { mutableStateOf("") }
    var challenges by remember { mutableStateOf("") }
    var gratitude by remember { mutableStateOf("") }

    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }
    val todayDate = remember { dateFormatter.format(Date()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Daily Reflection",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            todayDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onSave(
                                selectedMood,
                                notes,
                                highlights.takeIf { it.isNotBlank() },
                                challenges.takeIf { it.isNotBlank() },
                                gratitude.takeIf { it.isNotBlank() }
                            )
                            onNavigateBack()
                        },
                        enabled = notes.isNotBlank()
                    ) {
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Mood Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "How are you feeling today?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (mood in 1..5) {
                            MoodButton(
                                mood = mood,
                                isSelected = selectedMood == mood,
                                onClick = { selectedMood = mood }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Terrible",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Amazing",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Notes (Required)
            ReflectionTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Today's Reflection",
                placeholder = "How was your day? What did you accomplish?",
                required = true,
                minLines = 4
            )

            // Highlights
            ReflectionTextField(
                value = highlights,
                onValueChange = { highlights = it },
                label = "Highlights",
                placeholder = "What went well today?",
                icon = Icons.Default.Star,
                minLines = 2
            )

            // Challenges
            ReflectionTextField(
                value = challenges,
                onValueChange = { challenges = it },
                label = "Challenges",
                placeholder = "What was difficult? What did you learn?",
                icon = Icons.Default.TrendingDown,
                minLines = 2
            )

            // Gratitude
            ReflectionTextField(
                value = gratitude,
                onValueChange = { gratitude = it },
                label = "Gratitude",
                placeholder = "What are you grateful for today?",
                icon = Icons.Default.Favorite,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MoodButton(
    mood: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val emoji = when (mood) {
        1 -> "😞"
        2 -> "😕"
        3 -> "😐"
        4 -> "🙂"
        5 -> "😄"
        else -> "😐"
    }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Surface(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = backgroundColor,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun ReflectionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    required: Boolean = false,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    minLines: Int = 1
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = label + if (required) " *" else "",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            minLines = minLines,
            maxLines = minLines + 2,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
    }
}
