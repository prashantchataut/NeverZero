package com.productivitystreak.ui.voice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Voice Integration System for NeverZero
 * Enables hands-free task management and navigation
 */

sealed class VoiceCommand {
    data class CreateTask(val title: String, val category: String? = null) : VoiceCommand()
    data class CompleteTask(val taskTitle: String) : VoiceCommand()
    data class DeleteTask(val taskTitle: String) : VoiceCommand()
    data class EditTask(val taskTitle: String) : VoiceCommand()
    object ViewStats : VoiceCommand()
    object ViewDashboard : VoiceCommand()
    object StartFocusMode : VoiceCommand()
    object StopFocusMode : VoiceCommand()
    object RefreshWisdom : VoiceCommand()
    data class SearchTasks(val query: String) : VoiceCommand()
    data class NavigateTo(val screen: String) : VoiceCommand()
    object Help : VoiceCommand()
    data class UnknownCommand(val text: String) : VoiceCommand()
}

data class VoiceState(
    val isListening: Boolean = false,
    val isProcessing: Boolean = false,
    val lastCommand: VoiceCommand? = null,
    val recognizedText: String = "",
    val error: String? = null,
    val hasPermission: Boolean = false
)

class VoiceManager {
    private val _state = MutableStateFlow(VoiceState())
    val state: StateFlow<VoiceState> = _state.asStateFlow()
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var context: android.content.Context? = null
    
    fun initialize(context: android.content.Context) {
        this.context = context
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        setupSpeechRecognizer()
    }
    
    private fun setupSpeechRecognizer() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _state.value = _state.value.copy(isListening = true, error = null)
            }
            
            override fun onBeginningOfSpeech() {
                _state.value = _state.value.copy(isProcessing = false)
            }
            
            override fun onRmsChanged(rmsdB: Float) {}
            
            override fun onBufferReceived(buffer: ByteArray?) {}
            
            override fun onEndOfSpeech() {
                _state.value = _state.value.copy(isListening = false, isProcessing = true)
            }
            
            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    else -> "Unknown error"
                }
                _state.value = _state.value.copy(
                    isListening = false,
                    isProcessing = false,
                    error = errorMessage
                )
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                
                _state.value = _state.value.copy(
                    isListening = false,
                    isProcessing = false,
                    recognizedText = text,
                    lastCommand = parseVoiceCommand(text)
                )
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                _state.value = _state.value.copy(recognizedText = text)
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }
    
    fun startListening() {
        if (!hasPermission()) {
            _state.value = _state.value.copy(error = "Microphone permission required")
            return
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context?.packageName)
        }
        
        speechRecognizer?.startListening(intent)
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
        _state.value = _state.value.copy(isListening = false, isProcessing = false)
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        context = null
    }
    
    fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context ?: return false,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun parseVoiceCommand(text: String): VoiceCommand {
        val lowerText = text.lowercase().trim()
        
        return when {
            // Task creation
            lowerText.startsWith("create") || lowerText.startsWith("add") || lowerText.startsWith("new") -> {
                val title = extractTaskTitle(lowerText)
                val category = extractCategory(lowerText)
                VoiceCommand.CreateTask(title, category)
            }
            
            // Task completion
            lowerText.startsWith("complete") || lowerText.startsWith("done") || lowerText.startsWith("finish") -> {
                val taskTitle = extractTaskTitle(lowerText)
                VoiceCommand.CompleteTask(taskTitle)
            }
            
            // Task deletion
            lowerText.startsWith("delete") || lowerText.startsWith("remove") -> {
                val taskTitle = extractTaskTitle(lowerText)
                VoiceCommand.DeleteTask(taskTitle)
            }
            
            // Task editing
            lowerText.startsWith("edit") || lowerText.startsWith("modify") || lowerText.startsWith("change") -> {
                val taskTitle = extractTaskTitle(lowerText)
                VoiceCommand.EditTask(taskTitle)
            }
            
            // Navigation
            lowerText.contains("dashboard") || lowerText.contains("home") -> VoiceCommand.ViewDashboard
            lowerText.contains("stats") || lowerText.contains("statistics") -> VoiceCommand.ViewStats
            lowerText.contains("focus") && lowerText.contains("start") -> VoiceCommand.StartFocusMode
            lowerText.contains("focus") && lowerText.contains("stop") -> VoiceCommand.StopFocusMode
            lowerText.contains("wisdom") || lowerText.contains("quote") || lowerText.contains("refresh") -> VoiceCommand.RefreshWisdom
            
            // Search
            lowerText.startsWith("search") || lowerText.startsWith("find") -> {
                val query = lowerText.removePrefix("search").removePrefix("find").trim()
                VoiceCommand.SearchTasks(query)
            }
            
            // Help
            lowerText.contains("help") || lowerText.contains("commands") -> VoiceCommand.Help
            
            else -> VoiceCommand.UnknownCommand(text)
        }
    }
    
    private fun extractTaskTitle(text: String): String {
        val patterns = listOf(
            "create task", "add task", "new task", "complete task", "done task",
            "finish task", "delete task", "remove task", "edit task", "modify task"
        )
        
        var result = text
        patterns.forEach { pattern ->
            if (result.contains(pattern)) {
                result =alk = result.replace(pattern, "").trim()
                result = result.replaceFirst(" called ", " ").trim()
                result = result.replaceFirst(" named ", " ").trim()
            }
        }
        
        return result.ifEmpty { "Untitled Task" }
    }
    
    private fun extractCategory(text: String): String? {
        val categories = listOf("reading", "exercise", "meditation", "learning", "work", "health", "creative")
        
        return categories.firstOrNull { category ->
            text.contains(category, ignoreCase = true)
        }
    }
    
    fun getCommandHelp(): List<String> {
        return listOf(
            "• \"Create task [task name]\" - Add a new task",
            "• \"Complete task [task name]\" - Mark task as done",
            "• \"Delete task [task name]\" - Remove a task",
            "• \"Edit task [task name]\" - Modify a task",
            "• \"Show dashboard\" or \"Home\" - Go to dashboard",
            "• \"Show stats\" - View statistics",
            "• \"Start focus mode\" - Begin focus session",
            "• \"Stop focus mode\" - End focus session",
            "• \"Refresh wisdom\" - Get new wisdom quote",
            "• \"Search [query]\" - Find tasks",
            "• \"Help\" - Show all commands"
        )
    rep    }
}

@Composable
fun rememberVoiceManager(): VoiceManager = remember { VoiceManager() }

@Composable
fun VoicePermissionHandler(
    voiceManager: VoiceManager,
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        voiceManager.initialize(context)
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    )
    
    LaunchedEffect(voiceManager.hasPermission丛Permission()) {
        if (!voiceManager.hasPermission()) {
            permissionLauncher.launch(Manifest.RECORD_AUDIO)
        }
    }
}

/**
 * Voice command handler for the app
 */
@Composable
fun VoiceCommandHandler(
    voiceManager: VoiceManager,
    onCommand: (VoiceCommand) -> Unit
) {
    val state by voiceManager.state.collectAsState()
    
    LaunchedEffect(state.lastCommand) {
        state.lastCommand?.let { command ->
            onCommand(command)
        }
    }
}

/**
 * Voice feedback system
 */
object VoiceFeedback {
    fun getFeedback(command: VoiceCommand): String {
        return when (command) {
            is VoiceCommand.CreateTask -> "Creating task: ${command.title}"
            is VoiceCommand.CompleteTask -> "Completing task: ${command.taskTitle}"
            is VoiceCommand.DeleteTask -> "Deleting task: ${command.taskTitle}"
            is VoiceCommand.EditTask -> "Editing task: ${command.taskTitle}"
            is VoiceCommand.ViewStats -> "Opening statistics"
            is VoiceCommand.ViewDashboard -> "Going to dashboard"
            is VoiceCommand.StartFocusMode -> "Starting focus mode"
            is VoiceCommand.StopFocusMode -> "Stopping focus mode"
            is VoiceCommand.RefreshWisdom -> "Refreshing wisdom"
            is VoiceCommand.SearchTasks -> "Searching for: ${command.query}"
            is VoiceCommand.NavigateTo -> "Navigating to ${command.screen}"
            is VoiceCommand.Help -> "Showing voice commands"
            is VoiceCommand.UnknownCommand -> "I didn't understand: ${command.text}"
        }
    }
    
    fun getConfirmation(command: VoiceCommand): String {
        return when (command) {
            is VoiceCommand.CreateTask -> "Task created successfully"
            is VoiceCommand.CompleteTask -> "Task completed"
            is VoiceCommand.DeleteTask -> "Task deleted"
            is VoiceCommand.EditTask -> "Task updated"
            is VoiceCommand.ViewStats -> "Statistics opened"
            is VoiceCommand.ViewDashboard -> "Dashboard opened"
            is VoiceCommand.StartFocusMode -> "Focus mode started"
            is VoiceCommand.StopFocusMode -> "Focus mode stopped"
            is VoiceCommand.RefreshWisdom -> "Wisdom refreshed"
            is VoiceCommand.SearchTasks -> "Search completed"
            is VoiceCommand.NavigateTo -> "Navigated to ${command.screen}"
            is VoiceCommand.Help -> "Voice commands displayed"
            is VoiceCommand.UnknownCommand -> "Please try again"
        }
    }
}
