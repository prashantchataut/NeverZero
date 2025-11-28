package com.productivitystreak.ui.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.components.PrimaryButton

@Composable
fun TimeCapsuleFormSheet(
    isSubmitting: Boolean,
    onSubmit: (message: String, goal: String, days: Int) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("30") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Create Time Capsule",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message to future self") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("A promise/goal to achieve") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        OutlinedTextField(
            value = days,
            onValueChange = { if (it.all { char -> char.isDigit() }) days = it },
            label = { Text("Deliver in (days)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        PrimaryButton(
            text = if (isSubmitting) "Sealing..." else "Seal Capsule",
            onClick = {
                val daysInt = days.toIntOrNull() ?: 30
                onSubmit(message, goal, daysInt)
            },
            enabled = message.isNotBlank() && goal.isNotBlank() && !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
