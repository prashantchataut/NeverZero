package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.PlayfairFontFamily
import com.productivitystreak.ui.theme.Spacing
import com.productivitystreak.ui.components.VoiceButton
import com.productivitystreak.ui.components.VoiceStatusIndicator
import com.productivitystreak.ui.voice.VoiceManager
import com.productivitystreak.ui.voice.VoiceCommand
import com.productivitystreak.ui.interaction.HapticManager
import com.productivitystreak.ui.interaction.HapticPattern
import com.productivitystreak.ui.interaction.HapticFeedback

@Composable
fun VoiceControlCard(
    voiceManager: VoiceManager,
    hapticManager: HapticManager,
    deepForest: Color,
    creamWhite: Color,
    modifier: Modifier = Modifier
) {
    val state by voiceManager.state.collectAsState()
    var showHelp by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            deepForest.copy(alpha = 0.9f),
                            deepForest.copy(alpha = 0.8f),
                            deepForest.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voice Control",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = PlayfairFontFamily
                        ),
                        color = creamWhite,
                        maxLines = 1
                    )
                    
                    // Help icon
                    IconButton(
                        onClick = { showHelp = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MicOff,
                            contentDescription = "Voice commands help",
                            tint = creamWhite.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Voice Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    VoiceButton(
                        voiceManager = voiceManager,
                        modifier = Modifier.size(64.dp),
                        onVoiceCommand = { command ->
                            HapticFeedback(pattern = HapticPattern.SUCCESS, manager = hapticManager)
                            // Handle voice command here
                        }
                    )
                }

                // Status and instructions
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    VoiceStatusIndicator(
                        voiceManager = voiceManager,
                        modifier = Modifier
                    )
                    
                    Text(
                        text = when {
                            state.isListening -> "Listening..."
                            state.isProcessing -> "Processing..."
                            !state.hasPermission -> "Enable microphone"
                            else -> "Tap to speak"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = creamWhite.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Try: \"Create task meditation\"",
                        style = MaterialTheme.typography.labelSmall,
                        color = creamWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    // Help dialog
    if (showHelp) {
        VoiceHelpDialog(
            commands = voiceManager.getCommandHelp(),
            onDismiss = { showHelp = false },
            deepForest = deepForest,
            creamWhite = creamWhite
        )
    }
}

@Composable
private fun VoiceHelpDialog(
    commands: List<String>,
    onDismiss: () -> Unit,
    deepForest: Color,
    creamWhite: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Voice Commands",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = deepForest
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Control Never Zero with your voice:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = deepForest.copy(alpha = 0.8f)
                )
                
                commands.forEach { command ->
                    Text(
                        text = command,
                        style = MaterialTheme.typography.bodySmall,
                        color = deepForest,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Note: Microphone permission required for voice control.",
                    style = MaterialTheme.typography.labelSmall,
                    color = deepForest.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Got it",
                    color = deepForest
                )
            }
        },
        containerColor = creamWhite
    )
}
