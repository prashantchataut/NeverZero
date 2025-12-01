package com.productivitystreak.ui.screens.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.data.gemini.TeachingLesson
import com.productivitystreak.ui.state.AddEntryType
import com.productivitystreak.ui.state.vocabulary.TeachWordUiState
import com.productivitystreak.ui.state.vocabulary.VocabularyWord

@Composable
fun AddEntryMenuSheet(
    onEntrySelected: (AddEntryType) -> Unit
) {
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Command Center",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "What would you like to do?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Actions List
        CommandCenterItem(
            icon = com.productivitystreak.ui.icons.AppIcons.AddHabit,
            title = "New Protocol",
            subtitle = "Define a new discipline",
            color = Color(0xFF4ADE80),
            onClick = { 
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onEntrySelected(AddEntryType.HABIT) 
            }
        )
        CommandCenterItem(
            icon = com.productivitystreak.ui.icons.AppIcons.TeachWord,
            title = "Teach Word",
            subtitle = "Expand your vocabulary",
            color = Color(0xFF22D3EE),
            onClick = { 
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onEntrySelected(AddEntryType.TEACH) 
            }
        )
        CommandCenterItem(
            icon = com.productivitystreak.ui.icons.AppIcons.AddJournal,
            title = "Journal",
            subtitle = "Reflect on your day",
            color = Color(0xFFFB7185),
            onClick = { 
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onEntrySelected(AddEntryType.JOURNAL) 
            }
        )
    }
}

@Composable
private fun CommandCenterItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    com.productivitystreak.ui.components.InteractiveGlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun CommandCenterCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "card-scale"
    )
    
    // Glassmorphic card
    androidx.compose.material3.Surface(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Large thin-line icon
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                
                // Subtitle
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AddMenuCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    com.productivitystreak.ui.components.InteractiveCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(com.productivitystreak.ui.theme.Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(com.productivitystreak.ui.theme.Spacing.sm)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormSheet(
    isSubmitting: Boolean,
    onSubmit: (name: String, goal: Int, unit: String, category: String, color: String?, icon: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var goalText by remember { mutableStateOf("10") }
    var unit by remember { mutableStateOf("minutes") }
    var category by remember { mutableStateOf("Focus") }
    var selectedIcon by remember { mutableStateOf(habitIcons.first()) }
    var frequency by remember { mutableStateOf("Daily") }

    AddSheetContainer(title = "New Protocol", subtitle = "Craft a clear target and give it a friendly face.") {
        com.productivitystreak.ui.components.StyledTextField(
            value = name,
            onValueChange = { name = it },
            label = "Protocol Name",
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            com.productivitystreak.ui.components.StyledTextField(
                value = goalText,
                onValueChange = { value -> if (value.length <= 3 && value.all { it.isDigit() }) goalText = value },
                label = "Goal",
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            com.productivitystreak.ui.components.StyledTextField(
                value = unit,
                onValueChange = { unit = it },
                label = "Unit",
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        com.productivitystreak.ui.components.StyledTextField(
            value = category,
            onValueChange = { category = it },
            label = "Category",
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Choose an icon",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HabitIconGrid(selected = selectedIcon) { selectedIcon = it }

        Text(
            text = "Days per week",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FrequencyRow(selected = frequency, onSelect = { frequency = it })

        val goal = goalText.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
        com.productivitystreak.ui.components.PrimaryButton(
            text = if (isSubmitting) "Saving…" else "Initiate Protocol",
            onClick = {
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onSubmit(
                    name.trim(),
                    goal,
                    unit.trim().ifBlank { "count" },
                    category.trim().ifBlank { "Focus" },
                    selectedIcon.accent.toHex(),
                    selectedIcon.iconName
                )
            },
            enabled = !isSubmitting && name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyFormSheet(
    isSubmitting: Boolean,
    onSubmit: (word: String, definition: String, example: String?) -> Unit
) {
    var word by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }

    AddSheetContainer(title = "Log word", subtitle = "Store the word, context, and meaning so it sticks.") {
        com.productivitystreak.ui.components.StyledTextField(
            value = word,
            onValueChange = { word = it },
            label = "Word",
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        com.productivitystreak.ui.components.MultilineTextField(
            value = definition,
            onValueChange = { definition = it },
            label = "Definition",
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        com.productivitystreak.ui.components.MultilineTextField(
            value = example,
            onValueChange = { example = it },
            label = "Example sentence (optional)",
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
        com.productivitystreak.ui.components.PrimaryButton(
            text = if (isSubmitting) "Saving…" else "Save word",
            onClick = { 
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onSubmit(word.trim(), definition.trim(), example.trim().ifBlank { null }) 
            },
            enabled = !isSubmitting && word.isNotBlank() && definition.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalFormSheet(
    isSubmitting: Boolean,
    onSubmit: (mood: Int, notes: String, highlights: String?, gratitude: String?, tomorrowGoals: String?) -> Unit
) {
    var mood by remember { mutableStateOf(3f) }
    var notes by remember { mutableStateOf("") }
    var highlights by remember { mutableStateOf("") }
    var gratitude by remember { mutableStateOf("") }
    var tomorrowGoals by remember { mutableStateOf("") }

    AddSheetContainer(title = "Journal entry", subtitle = "Capture emotions, anchors, and intentions.") {
        val moodColor by animateColorAsState(targetValue = moodColorForValue(mood), label = "mood-color")

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MoodIndicator(value = mood, color = moodColor)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Mood", style = MaterialTheme.typography.labelMedium)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(vertical = 6.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFB7185),
                                    Color(0xFFFACC15),
                                    Color(0xFF4ADE80),
                                    Color(0xFF22D3EE)
                                )
                            ),
                            shape = RoundedCornerShape(999.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Slider(
                        value = mood,
                        onValueChange = { mood = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = moodColor,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        JournalField(value = notes, onValueChange = { notes = it }, label = "What stood out today?", minLines = 3)
        JournalField(value = highlights, onValueChange = { highlights = it }, label = "Highlights (optional)")
        JournalField(value = gratitude, onValueChange = { gratitude = it }, label = "Gratitude (optional)")
        JournalField(value = tomorrowGoals, onValueChange = { tomorrowGoals = it }, label = "Tomorrow’s focus (optional)")

        val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
        com.productivitystreak.ui.components.PrimaryButton(
            text = if (isSubmitting) "Saving…" else "Save entry",
            onClick = {
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onSubmit(
                    mood.toInt(),
                    notes.trim(),
                    highlights.trim().ifBlank { null },
                    gratitude.trim().ifBlank { null },
                    tomorrowGoals.trim().ifBlank { null }
                )
            },
            enabled = !isSubmitting && notes.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AddSheetContainer(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        content()
    }
}

@Composable
private fun HabitIconGrid(selected: HabitIconOption, onSelect: (HabitIconOption) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        habitIcons.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { option ->
                    val isSelected = option == selected
                    val scale by animateFloatAsState(targetValue = if (isSelected) 1.1f else 1f, label = "icon-scale")
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        label = "icon-border"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .clip(CircleShape)
                            .background(option.accent.copy(alpha = 0.12f))
                            .border(width = 2.dp, color = borderColor, shape = CircleShape)
                            .clickable { onSelect(option) }
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(option.icon, contentDescription = option.iconName, tint = option.accent)
                    }
                }
            }
        }
    }
}

@Composable
private fun FrequencyRow(selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        frequencyOptions.forEach { option ->
            val background by animateColorAsState(
                if (selected == option) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface,
                label = "freq-bg"
            )
            val contentColor by animateColorAsState(
                if (selected == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "freq-color"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(background)
                    .border(1.dp, contentColor.copy(alpha = 0.3f), RoundedCornerShape(999.dp))
                    .clickable { onSelect(option) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(option, style = MaterialTheme.typography.labelMedium, color = contentColor)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLines: Int = 2
) {
    com.productivitystreak.ui.components.MultilineTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines
    )
}

@Composable
private fun MoodIndicator(value: Float, color: Color) {
    val icon = when (value.toInt()) {
        1 -> Icons.Outlined.SentimentVeryDissatisfied
        2 -> Icons.Outlined.SentimentDissatisfied
        3 -> Icons.Outlined.SentimentNeutral
        4 -> Icons.Outlined.SentimentSatisfied
        else -> Icons.Outlined.SentimentVerySatisfied
    }
    
    Icon(
        imageVector = icon,
        contentDescription = "Mood indicator",
        tint = color,
        modifier = Modifier.size(36.dp)
    )
}

private fun moodColorForValue(value: Float): Color = when (value.toInt()) {
    1 -> Color(0xFFFB7185)
    2 -> Color(0xFFFACC15)
    3 -> Color(0xFF4ADE80)
    4 -> Color(0xFF2DD4BF)
    else -> Color(0xFF22D3EE)
}

private val habitIcons = listOf(
    HabitIconOption(Icons.Outlined.WaterDrop, "water", Color(0xFF38BDF8)),
    HabitIconOption(Icons.Outlined.FitnessCenter, "fitness", Color(0xFFF97316)),
    HabitIconOption(Icons.Outlined.Lightbulb, "lightbulb", Color(0xFFEAB308)),
    HabitIconOption(Icons.Outlined.SelfImprovement, "calm", Color(0xFF8B5CF6)),
    HabitIconOption(Icons.Outlined.AutoGraph, "progress", Color(0xFF22C55E)),
    HabitIconOption(Icons.Outlined.FavoriteBorder, "heart", Color(0xFFFB7185))
)

private val frequencyOptions = listOf("Daily", "5x/week", "3x/week")

private data class HabitIconOption(val icon: ImageVector, val iconName: String, val accent: Color)

private fun Color.toHex(): String = "#%06X".format(this.toArgb() and 0xFFFFFF)

@Composable
fun TeachWordSheet(
    uiState: TeachWordUiState,
    onWordChange: (String) -> Unit,
    onContextChange: (String) -> Unit,
    onGenerateLesson: () -> Unit,
    onLogLesson: (TeachingLesson) -> Unit,
    onDismissLesson: () -> Unit,
    onNextWord: () -> Unit = {} // New callback for skipping
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Discover & Learn",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "AI-curated vocabulary to elevate your thinking.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.isGenerating && uiState.lesson == null && uiState.suggestedWord == null) {
             Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator()
             }
        } else if (uiState.suggestedWord != null) {
            // AI Suggested Word Card
            WordDiscoveryCard(
                word = uiState.suggestedWord,
                onSkip = onNextWord,
                onLearn = onGenerateLesson
            )
        } else {
            // Fallback or Initial State (shouldn't happen often if auto-suggest works)
             Button(onClick = onNextWord, modifier = Modifier.fillMaxWidth()) {
                 Text("Find a word")
             }
        }

        // Show Lesson if generated
        TeachLessonCTA(
            isGenerating = uiState.isGenerating && uiState.lesson == null && uiState.suggestedWord != null, // Only show spinner here if generating lesson
            lesson = uiState.lesson,
            error = uiState.errorMessage,
            onGenerateLesson = onGenerateLesson,
            onDismissLesson = onDismissLesson,
            onLogLesson = onLogLesson
        )
    }
}

@Composable
fun WordDiscoveryCard(
    word: VocabularyWord,
    onSkip: () -> Unit,
    onLearn: () -> Unit
) {
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = word.word,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = word.definition,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        onSkip()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Skip")
                }
                Button(
                    onClick = {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onLearn()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Learn")
                }
            }
        }
    }
}

@Composable
private fun TeachLessonCTA(
    isGenerating: Boolean,
    lesson: TeachingLesson?,
    error: String?,
    onGenerateLesson: () -> Unit,
    onDismissLesson: () -> Unit,
    onLogLesson: (TeachingLesson) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (lesson == null) {
             // Only show this button if we are NOT in the suggested word flow (or if we want a manual override, but let's hide it for now to clean UI)
             // Actually, the 'Learn' button in WordDiscoveryCard calls onGenerateLesson.
             // So we don't need this button here if a suggested word is present.
        }

        error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        AnimatedVisibility(visible = lesson != null) {
            lesson?.let {
                TeachingLessonCard(
                    lesson = it,
                    onDismiss = onDismissLesson,
                    onLog = onLogLesson
                )
            }
        }
    }
}

@Composable
private fun TeachingLessonCard(
    lesson: TeachingLesson,
    onDismiss: () -> Unit,
    onLog: (TeachingLesson) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(lesson.word, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = lesson.definition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(onClick = onDismiss) {
                    Text("Clear")
                }
            }

            LessonSection("Analogy", lesson.analogy)
            LessonSection("Mnemonic", lesson.mnemonic)
            LessonSection("Example", lesson.example)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Micro practice",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                lesson.practicePrompts.forEach { prompt ->
                    LessonPromptChip(prompt)
                }
            }

            Button(
                onClick = { onLog(lesson) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log to vocabulary")
            }
        }
    }
}

@Composable
private fun LessonSection(title: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun LessonPromptChip(prompt: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = prompt,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
