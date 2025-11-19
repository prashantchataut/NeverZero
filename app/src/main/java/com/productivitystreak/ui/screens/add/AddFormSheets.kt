package com.productivitystreak.ui.screens.add

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.state.AddEntryType

@Composable
fun AddEntryMenuSheet(
    onEntrySelected: (AddEntryType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What would you like to add?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Capture new habits, keep words alive, or reflect on the day.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        AddMenuCard(
            title = "New habit",
            subtitle = "Track a fresh routine with gentle nudges.",
            onClick = { onEntrySelected(AddEntryType.HABIT) }
        )
        AddMenuCard(
            title = "Log word",
            subtitle = "Save vocabulary for your streak.",
            onClick = { onEntrySelected(AddEntryType.WORD) }
        )
        AddMenuCard(
            title = "Journal",
            subtitle = "Reflect, reset, and stay intentional.",
            onClick = { onEntrySelected(AddEntryType.JOURNAL) }
        )
    }
}

@Composable
private fun AddMenuCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(24.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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

    AddSheetContainer(title = "New habit", subtitle = "Craft a clear target and give it a friendly face.") {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Habit name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = goalText,
                onValueChange = { value -> if (value.length <= 3 && value.all { it.isDigit() }) goalText = value },
                label = { Text("Goal") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
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
        Button(
            onClick = {
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(if (isSubmitting) "Saving…" else "Save habit")
        }
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
        OutlinedTextField(
            value = word,
            onValueChange = { word = it },
            label = { Text("Word") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = definition,
            onValueChange = { definition = it },
            label = { Text("Definition") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        OutlinedTextField(
            value = example,
            onValueChange = { example = it },
            label = { Text("Example sentence (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Button(
            onClick = { onSubmit(word.trim(), definition.trim(), example.trim().ifBlank { null }) },
            enabled = !isSubmitting && word.isNotBlank() && definition.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSubmitting) "Saving…" else "Save word")
        }
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
            Text(text = moodEmoji(mood), fontSize = 32.sp)
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

        Button(
            onClick = {
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
        ) {
            Text(if (isSubmitting) "Saving…" else "Save entry")
        }
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
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        minLines = minLines,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

private fun moodEmoji(value: Float): String = when (value.toInt()) {
    1 -> "😔"
    2 -> "😐"
    3 -> "🙂"
    4 -> "😊"
    else -> "🤩"
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
