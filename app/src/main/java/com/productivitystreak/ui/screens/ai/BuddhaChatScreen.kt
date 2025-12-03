package com.productivitystreak.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.productivitystreak.data.ai.BuddhaRepository
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.Spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuddhaChatScreen(
    userName: String,
    onBackClick: () -> Unit,
    repository: BuddhaRepository,
    hapticsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val viewModel: BuddhaChatViewModel = viewModel(
        factory = BuddhaChatViewModelFactory(repository)
    )
    
    LaunchedEffect(userName) {
        viewModel.startChat(userName)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty() || uiState.isLoading) {
            val count = uiState.messages.size + if (uiState.isLoading) 1 else 0
            if (count > 0) {
                listState.animateScrollToItem(count - 1)
            }
        }
    }

    // Handle Error Events
    LaunchedEffect(uiState.errorEvent) {
        uiState.errorEvent?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Buddha",
                        style = MaterialTheme.typography.headlineSmall,
                        color = NeverZeroTheme.designColors.textPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            tint = NeverZeroTheme.designColors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeverZeroTheme.designColors.background,
                    scrolledContainerColor = NeverZeroTheme.designColors.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NeverZeroTheme.designColors.background,
        modifier = modifier.imePadding() // Pin to keyboard
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chat Messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(uiState.messages) { message ->
                    ChatMessageItem(
                        message = message,
                        onRetry = { viewModel.retryMessage(message.id) }
                    )
                }
                
                if (uiState.isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Input Area
            Surface(
                color = NeverZeroTheme.designColors.surface,
                tonalElevation = 2.dp,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                        .navigationBarsPadding(), // Handle bottom nav bar overlap
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Meditate on this...") },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(28.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = NeverZeroTheme.designColors.background,
                            unfocusedContainerColor = NeverZeroTheme.designColors.background,
                            focusedIndicatorColor = NeverZeroTheme.designColors.primary,
                            unfocusedIndicatorColor = NeverZeroTheme.designColors.outline,
                            focusedTextColor = NeverZeroTheme.designColors.textPrimary,
                            unfocusedTextColor = NeverZeroTheme.designColors.textPrimary
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (!uiState.isLoading && inputText.isNotBlank()) {
                                    if (hapticsEnabled) {
                                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                    }
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                    keyboardController?.hide()
                                }
                            }
                        ),
                        maxLines = 4
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    
                    val isSendEnabled = !uiState.isLoading && inputText.isNotBlank()
                    
                    IconButton(
                        onClick = {
                            if (isSendEnabled) {
                                if (hapticsEnabled) {
                                    haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                }
                                viewModel.sendMessage(inputText)
                                inputText = ""
                                keyboardController?.hide()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (isSendEnabled) NeverZeroTheme.designColors.primary else NeverZeroTheme.designColors.surfaceVariant,
                                shape = CircleShape
                            )
                            .alpha(if (isSendEnabled) 1f else 0.5f),
                        enabled = isSendEnabled
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (isSendEnabled) NeverZeroTheme.designColors.onPrimary else NeverZeroTheme.designColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: BuddhaChatMessage,
    onRetry: () -> Unit
) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    
    // Modern Stoic Colors
    val backgroundColor = when {
        message.status == MessageStatus.ERROR -> MaterialTheme.colorScheme.errorContainer
        message.isUser -> NeverZeroTheme.designColors.primary
        else -> NeverZeroTheme.designColors.surface
    }
    
    val textColor = when {
        message.status == MessageStatus.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        message.isUser -> NeverZeroTheme.designColors.onPrimary
        else -> NeverZeroTheme.designColors.textPrimary
    }

    val shape = if (message.isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!message.isUser) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(NeverZeroTheme.designColors.surface, CircleShape)
                        .border(1.dp, NeverZeroTheme.designColors.primary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SelfImprovement,
                        contentDescription = "Buddha",
                        tint = NeverZeroTheme.designColors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .background(backgroundColor, shape)
                        .border(
                            width = 1.dp,
                            color = if (message.isUser) Color.Transparent else NeverZeroTheme.designColors.outline.copy(alpha = 0.5f),
                            shape = shape
                        )
                        .padding(12.dp)
                        .clickable(enabled = message.status == MessageStatus.ERROR, onClick = onRetry)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (message.status == MessageStatus.ERROR) {
                    Text(
                        text = "Tap to retry",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val dotSize = 8.dp
    val delayUnit = 300
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(start = 40.dp, top = 8.dp) // Align with mentor bubbles
            .background(NeverZeroTheme.designColors.surface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        TypingDot(delayUnit * 0)
        TypingDot(delayUnit * 1)
        TypingDot(delayUnit * 2)
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = "Buddha is thinking...",
            style = MaterialTheme.typography.labelSmall,
            color = NeverZeroTheme.designColors.textSecondary
        )
    }
}

@Composable
fun TypingDot(delayMillis: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = delayMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = Modifier
            .size(8.dp)
            .alpha(alpha)
            .background(NeverZeroTheme.designColors.primary, CircleShape)
    )
}

