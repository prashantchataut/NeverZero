package com.productivitystreak.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.voice.VoiceManager
import com.productivitystreak.ui.voice.VoiceCommand
import kotlinx.coroutines.delay

/**
 * Voice Input Button Component
 * Provides visual feedback for voice interactions
 */

@Composable
fun VoiceButton(
    voiceManager: VoiceManager,
    modifier: Modifier = Modifier,
    onVoiceCommand: (VoiceCommand) -> Unit = {}
) {
    val state by voiceManager.state.collectAsState()
    var showHelp by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        // Main voice button
        VoiceButtonCore(
            isListening = state.isListening,
            isProcessing = state.isProcessing,
            hasPermission = state.hasPermission,
            error = state.error,
            onClick = {
                if (state.hasPermission) {
                    if (state.isListening) {
                        voiceManager.stopListening()
                    } else {
                        voiceManager.startListening()
                    }
                }
            },
            onLongPress = { showHelp = true }
        )
        
        // Help dialog
        if (showHelp) {
            VoiceHelpDialog(
                commands = voiceManager.getCommandHelp(),
                onDismiss = { showHelp = false }
            )
        }
    }
}

@Composable
private fun VoiceButtonCore(
    isListening: Boolean,
    isProcessing: Boolean,
    hasPermission: Boolean,
    error: String?,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    val pulseScale = remember { Animatable(1f) }
    
    // Pulse animation when listening
    LaunchedEffect(isListening) {
        if (isListening) {
            while (true) {
                pulseScale.animateTo(
                    targetValue = 1.1f,
                    animationSpec = tween(1000, easing = EaseInOutCubic)
                )
                pulseScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(1000, easing = EaseInOutCubic)
                )
            }
        } else {
            pulseScale.animateTo(1f)
        }
    }
    
    // Press animation
    LaunchedEffect(isListening) {
        if (isListening) {
            scale.animateTo(0.95f, animationSpec = tween(100))
        } else {
            scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }
    
    val backgroundColor = when {
        error != null -> NeverZeroTheme.designColors.error
        isListening -> NeverZeroTheme.designColors.primary
        isProcessing -> NeverZeroTheme.designColors.secondary
        !hasPermission -> NeverZeroTheme.designColors.disabled
        else -> NeverZeroTheme.designColors.surface
    }
    
    val iconColor = when {
        error != null || isListening -> Color.White
        !hasPermission -> NeverZeroTheme.designColors.disabled
        else -> NeverZeroTheme.designColors.primary
    }
    
    val icon = when {
        isListening -> Icons.Default.Mic
        else -> Icons.Default.MicOff
    }
    
    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale.value * pulseScale.value)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .border(
                width = if (isListening) 2.dp else 1.dp,
                color = if (isListening) backgroundColor.copy(alpha = 0.5f) else NeverZeroTheme.designColors.border,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = if (isListening) "Stop listening" else "Start voice input",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    
    // Sound waves animation when listening
    if (isListening) {
        SoundWavesAnimation()
    }
}

@Composable
private fun SoundWavesAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_waves")
    
    val wave1Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )
    
    val wave2Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseOutCubic),
            initialOffset = 500,
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )
    
    val wave3Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseOutCubic),
            initialOffset = 1000,
            repeatMode = RepeatMode.Restart
        ),
        label = "wave3"
    )
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Wave 1
        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(wave1Scale)
                .background(
                    color = NeverZeroTheme.designColors.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )
        
        // Wave 2
        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(wave2Scale)
                .background(
                    color = NeverZeroTheme.designColors.primary.copy(alpha = 0.05f),
                    shape = CircleShape
                )
        )
        
        // Wave 3
        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(wave3Scale)
                .background(
                    color = NeverZeroTheme.designColors.primary.copy(alpha = 0.02f),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun VoiceHelpDialog(
    commands: List<String>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Voice Commands",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Try these voice commands:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                commands.forEach { command ->
                    Text(
                        text = command,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        },
        modifier = modifier
    )
}

/**
 * Voice feedback toast/snackbar
 */
@Composable
fun VoiceFeedback(
    command: VoiceCommand,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val feedback = remember(command) { com.productivitystreak.ui.voice.VoiceFeedback.getFeedback(command) }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = NeverZeroTheme.designColors.primary
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Voice Command",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = "Dismiss",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Voice status indicator
 */
@Composable
fun VoiceStatusIndicator(
    voiceManager: VoiceManager,
    modifier: Modifier = Modifier
) {
    val state by voiceManager.state.collectAsState()
    
    if (state.isListening || state.isProcessing) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.isListening) {
                VoiceListeningIndicator()
            }
            
            Text(
                text = when {
                    state.isListening -> "Listening..."
                    state.isProcessing -> "Processing..."
                    else -> ""
                },
                style = MaterialTheme.typography.labelMedium,
                color = NeverZeroTheme.designColors.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun VoiceListeningIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "listening_indicator")
    
    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    
    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            initialOffset = 200,
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    
    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            initialOffset = 400,
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .scale(dot1Scale)
                .background(
                    color = NeverZeroTheme.designColors.primary,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .scale(dot2Scale)
                .background(
                    color = NeverZeroTheme.designColors.primary,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .scale(dot3Scale)
                .background(
                    color = NeverZeroTheme.designColors.primary,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
    }
}
