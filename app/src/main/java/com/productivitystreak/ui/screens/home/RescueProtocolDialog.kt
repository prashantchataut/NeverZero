package com.productivitystreak.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RescueProtocolDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Rounded.Timer, contentDescription = null)
        },
        title = {
            Text(text = "Rescue Protocol")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Momentum is better than perfection.",
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Commit to just 1 minute of focused work right now to save your streak.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I'll Do 1 Minute")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
