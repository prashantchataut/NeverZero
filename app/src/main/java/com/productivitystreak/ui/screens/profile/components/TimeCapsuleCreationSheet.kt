package com.productivitystreak.ui.screens.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun TimeCapsuleCreationSheet(
    onDismiss: () -> Unit,
    onCreate: (message: String, goal: String, daysFromNow: Int) -> Unit
) {
    var goal by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var daysText by rememberSaveable { mutableStateOf("30") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Write to your future self",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Describe the person you intend to be, and when you want to be reminded.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("Promise / goal to revisit") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Letter to future self") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            maxLines = 6
        )

        OutlinedTextField(
            value = daysText,
            onValueChange = { daysText = it.filter { ch -> ch.isDigit() }.take(3) },
            label = { Text("Deliver in (days)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Schedule capsule",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable {
                        val days = daysText.toIntOrNull() ?: 30
                        onCreate(message, goal, days)
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}
