package com.productivitystreak.ui.screens.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.productivitystreak.ui.state.AddEntryType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddFormSheets(
    activeForm: AddEntryType?,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmitHabit: (name: String, goal: Int, unit: String, category: String, color: String?, icon: String?) -> Unit,
    onSubmitWord: (word: String, definition: String, example: String?) -> Unit,
    onSubmitJournal: (mood: Int, notes: String, highlights: String?, gratitude: String?, tomorrowGoals: String?) -> Unit
) {
    if (activeForm == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (activeForm) {
                AddEntryType.HABIT -> HabitForm(isSubmitting, onDismiss, onSubmitHabit)
                AddEntryType.WORD -> WordForm(isSubmitting, onDismiss, onSubmitWord)
                AddEntryType.JOURNAL -> JournalForm(isSubmitting, onDismiss, onSubmitJournal)
            }
        }
    }
}

@Composable
private fun MoodSelector(mood: Int, onSelect: (Int) -> Unit) {
    val options = listOf(
        1 to "😔",
        2 to "😐",
        3 to "🙂",
        4 to "😄",
        5 to "🔥"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (value, label) ->
            val selected = value == mood
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .clickable { onSelect(value) },
                color = if (selected) Color(0xFFE8E5FF) else Color(0xFFF4F4FA)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = label, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    ElevatedAssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                color = if (selected) Color(0xFF4C52FF) else MaterialTheme.colorScheme.onSurface
            )
        },
        shape = CircleShape,
        modifier = Modifier.height(40.dp),
        colors = androidx.compose.material3.AssistChipDefaults.elevatedAssistChipColors(
            containerColor = if (selected) Color(0xFFE8E5FF) else MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun ColorChip(colorHex: String, selected: Boolean, onSelect: () -> Unit) {
    val color = runCatching { Color(android.graphics.Color.parseColor(colorHex)) }.getOrElse { Color(0xFF5F7BFF) }
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onSelect),
        color = color,
        tonalElevation = if (selected) 8.dp else 2.dp,
        border = if (selected) BorderStroke(2.dp, Color.White) else null
    ) {
        if (selected) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HabitForm(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmitHabit: (String, Int, String, String, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("minutes") }
    var goal by remember { mutableStateOf(10) }
    var category by remember { mutableStateOf("Focus") }
    var icon by remember { mutableStateOf("flame") }
    var color by remember { mutableStateOf("#5F7BFF") }

    SheetHeader(title = "Add New Habit", subtitle = "Define the habit you want to track.")

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Habit name") },
        placeholder = { Text("e.g. Read 10 pages") },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = Modifier.fillMaxWidth()
    )

    FrequencyPicker(goal) { goal = it }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("minutes", "pages", "times", "words").forEach { option ->
            Chip(
                label = option,
                selected = unit == option,
                onClick = { unit = option }
            )
        }
    }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Focus", "Wellness", "Learning", "Creativity").forEach { option ->
            Chip(label = option, selected = category == option, onClick = { category = option })
        }
    }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val colors = listOf("#5F7BFF", "#10B981", "#F97316", "#EC4899")
        colors.forEach { value ->
            ColorChip(colorHex = value, selected = color == value, onSelect = { color = value })
        }
    }

    PrimaryActions(
        isSubmitting = isSubmitting,
        primaryLabel = "Save habit",
        onPrimary = { onSubmitHabit(name, goal, unit, category, color, icon) },
        onDismiss = onDismiss
    )
}

@Composable
private fun FrequencyPicker(goal: Int, onGoalChanged: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Daily target", style = MaterialTheme.typography.titleMedium)
        Text(text = "$goal units per day", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Slider(
            value = goal.toFloat(),
            onValueChange = { onGoalChanged(it.toInt().coerceIn(1, 120)) },
            valueRange = 1f..120f
        )
    }
}

@Composable
private fun WordForm(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmitWord: (String, String, String?) -> Unit
) {
    var word by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }

    SheetHeader(title = "Add New Word", subtitle = "Capture vocab the moment it appears.")

    OutlinedTextField(
        value = word,
        onValueChange = { word = it },
        label = { Text("Word") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = definition,
        onValueChange = { definition = it },
        label = { Text("Definition") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = example,
        onValueChange = { example = it },
        label = { Text("Context sentence (optional)") },
        modifier = Modifier.fillMaxWidth()
    )

    PrimaryActions(
        isSubmitting = isSubmitting,
        primaryLabel = "Log word",
        onPrimary = { onSubmitWord(word, definition, example.ifBlank { null }) },
        onSecondary = {
            onSubmitWord(word, definition, example.ifBlank { null })
            word = ""
            definition = ""
            example = ""
        },
        secondaryLabel = "Save & add another",
        onDismiss = onDismiss
    )
}

@Composable
private fun JournalForm(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmitJournal: (Int, String, String?, String?, String?) -> Unit
) {
    var mood by remember { mutableStateOf(3) }
    var notes by remember { mutableStateOf("") }
    var highlights by remember { mutableStateOf("") }
    var gratitude by remember { mutableStateOf("") }
    var tomorrow by remember { mutableStateOf("") }

    SheetHeader(title = "Add Journal Entry", subtitle = "Reflect on momentum, gratitude, and what’s next.")

    MoodSelector(mood = mood, onSelect = { selected -> mood = selected })

    OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text("Main entry") },
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    )

    OutlinedTextField(
        value = highlights,
        onValueChange = { highlights = it },
        label = { Text("Highlights") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = gratitude,
        onValueChange = { gratitude = it },
        label = { Text("Gratitude") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = tomorrow,
        onValueChange = { tomorrow = it },
        label = { Text("Tomorrow’s focus") },
        modifier = Modifier.fillMaxWidth()
    )

    PrimaryActions(
        isSubmitting = isSubmitting,
        primaryLabel = "Save entry",
        onPrimary = {
            onSubmitJournal(
                mood,
                notes,
                highlights.ifBlank { null },
                gratitude.ifBlank { null },
                tomorrow.ifBlank { null }
            )
        },
        onDismiss = onDismiss
    )
}

@Composable
private fun SheetHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .align(Alignment.CenterHorizontally)
        )
        Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PrimaryActions(
    isSubmitting: Boolean,
    primaryLabel: String,
    onPrimary: () -> Unit,
    onDismiss: () -> Unit,
    onSecondary: (() -> Unit)? = null,
    secondaryLabel: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onPrimary,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text(primaryLabel)
            }
        }

        AnimatedVisibility(visible = secondaryLabel != null && onSecondary != null) {
            TextButton(
                onClick = { onSecondary?.invoke() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting
            ) {
                Text(secondaryLabel ?: "")
            }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
