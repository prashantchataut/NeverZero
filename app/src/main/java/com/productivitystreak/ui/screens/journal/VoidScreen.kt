package com.productivitystreak.ui.screens.journal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay



@Composable
fun VoidScreen(
    onNavigateBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showBuddhaResponse by remember { mutableStateOf(false) }
    var buddhaMessage by remember { mutableStateOf("") }
    
    val focusManager = LocalFocusManager.current
    val haptics = LocalHapticFeedback.current
    
    // Pulsing background effect
    val pulseAnim = remember { Animatable(0f) }
    
    LaunchedEffect(text.length) {
        if (text.isNotEmpty() && !isSaving) {
            // Subtle pulse when typing
            pulseAnim.animateTo(
                targetValue = 0.1f,
                animationSpec = tween(100)
            )
            pulseAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(500)
            )
        }
    }

    // Animation Logic
    LaunchedEffect(isSaving) {
        if (isSaving) {
            // Wait for text to dissolve (faster)
            delay(1200)
            showBuddhaResponse = true
            
            // Wait for response to be read (shorter, but allow manual exit)
            delay(2500)
            
            // We don't auto-reset immediately, we let the user linger or exit.
            // But if they want to write again:
            delay(2000)
            showBuddhaResponse = false
            text = ""
            isSaving = false
        }
    }
    
    // Smoke/Dissolve animation state
    val textAlpha by animateFloatAsState(
        targetValue = if (isSaving) 0f else 1f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "textAlpha"
    )
    
    val textTranslationY by animateFloatAsState(
        targetValue = if (isSaving) -50f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "textTranslation"
    )
    
    val responseAlpha by animateFloatAsState(
        targetValue = if (showBuddhaResponse) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "responseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Subtle background pulse
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(pulseAnim.value)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E1E2E), // Dark blue-ish grey
                            Color.Black
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header (minimalist)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                TextButton(
                    onClick = onNavigateBack,
                    // ALWAYS ENABLED so user is never stuck
                    enabled = true 
                ) {
                    Text(
                        text = "exit void",
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                if (text.isNotEmpty() && !isSaving) {
                    TextButton(
                        onClick = {
                            isSaving = true
                            focusManager.clearFocus()
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            
                            // Mock Buddha analysis
                            buddhaMessage = mockBuddhaAnalysis(text)
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            text = "release",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // The Void Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopStart
            ) {
                if (!showBuddhaResponse) {
                    BasicTextField(
                        value = text,
                        onValueChange = { if (!isSaving) text = it },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 28.sp
                        ),
                        cursorBrush = SolidColor(Color.White),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = textAlpha
                                translationY = textTranslationY
                            }
                    )
                    
                    if (text.isEmpty()) {
                        Text(
                            text = "what weighs on you?",
                            color = Color.DarkGray,
                            fontSize = 18.sp,
                            modifier = Modifier.alpha(0.5f)
                        )
                    }
                }
                
                // Buddha's Response
                if (showBuddhaResponse) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Text(
                                text = buddhaMessage,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier
                                    .alpha(responseAlpha)
                                    .padding(horizontal = 32.dp)
                            )
                            
                            // Explicit Done button to clear state faster if desired
                            TextButton(
                                onClick = {
                                    showBuddhaResponse = false
                                    text = ""
                                    isSaving = false
                                },
                                modifier = Modifier.alpha(responseAlpha)
                            ) {
                                Text("clear", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

private fun mockBuddhaAnalysis(input: String): String {
    val length = input.length
    return when {
        length < 50 -> "silence is also an answer."
        length < 200 -> "the thought is released. the mind is lighter."
        else -> "chaos is merely order waiting to be understood."
    }
}
