package com.productivitystreak.ui.accessibility

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.semantics.semantics
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AI-Powered Accessibility System for NeverZero
 * Provides intelligent content descriptions and adaptive UI
 */

data class AccessibilityConfig(
    val highContrastMode: Boolean = false,
    val largeTextMode: Boolean = false,
    val reducedMotion: Boolean = false,
    val screenReaderEnabled: Boolean = false,
    val voiceNavigationEnabled: Boolean = false,
    val aiDescriptionsEnabled: Boolean = true
)

data class AIContentDescription(
    val primaryDescription: String,
    val secondaryDescription: String? = null,
    val actions: List<String> = emptyList(),
    val context: String = "",
    val importance: AccessibilityImportance = AccessibilityImportance.DEFAULT
)

enum class AccessibilityImportance {
    CRITICAL, HIGH, MEDIUM, LOW, DEFAULT
}

class AIAccessibilityManager {
    private val _config = MutableStateFlow(AccessibilityConfig())
    val config: StateFlow<AccessibilityConfig> = _config.asStateFlow()
    
    private val _descriptions = MutableStateFlow<Map<String, AIContentDescription>>(emptyMap())
    val descriptions: StateFlow<Map<String, AIContentDescription>> = _descriptions.asStateFlow()
    
    fun updateConfig(newConfig: AccessibilityConfig) {
        _config.value = newConfig
    }
    
    fun generateDescription(
        elementType: String,
        content: String,
        context: String = "",
        actions: List<String> = emptyList()
    ): AIContentDescription {
        return when (elementType.lowercase()) {
            "task" -> generateTaskDescription(content, context, actions)
            "button" -> generateButtonDescription(content, actions)
            "card" -> generateCardDescription(content, context)
            "chart" -> generateChartDescription(content, context)
            "wisdom" -> generateWisdomDescription(content)
            "achievement" -> generateAchievementDescription(content, context)
            "stat" -> generateStatDescription(content, context)
            else -> generateGenericDescription(content, context)
        }
    }
    
    private fun generateTaskDescription(
        content: String,
        context: String,
        actions: List<String>
    ): AIContentDescription {
        val primary = "Task: $content"
        val secondary = if (context.isNotEmpty()) "Part of: $context" else null
        val availableActions = if (actions.isNotEmpty()) {
            "Available actions: ${actions.joinToString(", ")}"
        } else {
            "Tap to complete, long press to edit"
        }
        
        return AIContentDescription(
            primaryDescription = primary,
            secondaryDescription = secondary,
            actions = listOf(availableActions),
            context = context,
            importance = if (content.contains("urgent", ignoreCase = true)) {
                AccessibilityImportance.HIGH
            } else {
                AccessibilityImportance.MEDIUM
            }
        )
    }
    
    private fun generateButtonDescription(
        content: String,
        actions: List<String>
    ): AIContentDescription {
        val primary = "Button: $content"
        val actionText = if (actions.isNotEmpty()) {
            actions.first()
        } else {
            "Double tap to activate"
        }
        
        return AIContentDescription(
            primaryDescription = primary,
            actions = listOf(actionText),
            importance = when {
                content.contains("save", ignoreCase = true) || 
                content.contains("submit", ignoreCase = true) -> AccessibilityImportance.HIGH
                content.contains("cancel", ignoreCase = true) -> AccessibilityImportance.MEDIUM
                else -> AccessibilityImportance.DEFAULT
            }
        )
    }
    
    private fun generateCardDescription(
        content: String,
        context: String
    ): AIContentDescription {
        val primary = "Card: $content"
        val secondary = if (context.isNotEmpty()) {
            "Contains: $context"
        } else {
            "Tap to expand"
        }
        
        return AIContentDescription(
            primaryDescription = primary,
            secondaryDescription = secondary,
            actions = listOf("Double tap to view details"),
            importance = AccessibilityImportance.MEDIUM
        )
    }
    
    private fun generateChartDescription(
        content: String,
        context: String
    ): AIContentDescription {
        val primary = "Chart showing $content"
        val secondary = if (context.isNotEmpty()) {
            "Data represents: $context"
        } else {
            "Visual data representation"
        }
        
        return AIContentDescription(
            primaryDescription = primary,
            secondaryDescription = secondary,
            actions = listOf("Double tap for detailed data"),
            importance = AccessibilityImportance.MEDIUM
        )
    }
    
    private fun generateWisdomDescription(
        content: String
    ): AIContentDescription {
        return AIContentDescription(
            primaryDescription = "Daily wisdom: $content",
            secondaryDescription = "Inspirational quote for motivation",
            actions = listOf("Double tap to refresh"),
            importance = AccessibilityImportance.LOW
        )
    }
    
    private fun generateAchievementDescription(
        content: String,
        context: String
    ): AIContentDescription {
        val primary = "Achievement: $content"
        val secondary = if (context.isNotEmpty()) {
            "Earned through: $context"
        } else {
            "Milestone accomplishment"
        }
        
        return AIContentDescription(
            primaryDescription = primary,
            secondaryDescription = secondary,
            actions = listOf("Double tap to view details"),
            importance = AccessibilityImportance.HIGH
        )
    }
    
    private fun generateStatDescription(
        content: String,
        context: String
    ): AIContentDescription {
        val primary = "Statistic: $content"
        val secondary = if (context.isNotEmpty()) {
            "Represents: $context"
        } else {
            "Performance metric"
        }
        
        return AIContentDescription(
            primaryDescription = primary,
            secondaryDescription = secondary,
            importance = AccessibilityImportance.MEDIUM
        )
    }
    
    private fun generateGenericDescription(
        content: String,
        context: String
    ): AIContentDescription {
        val primary = content
        val secondary = if (context.isNotEmpty()) {
            "Context: $context"
        } else {
            null
        }
        
        return AIContentDescription(
            primaryDescription = primary,
            secondaryDescription = secondary,
            importance = AccessibilityImportance.DEFAULT
        )
    }
    
    fun addDescription(key: String, description: AIContentDescription) {
        val current = _descriptions.value.toMutableMap()
        current[key] = description
        _descriptions.value = current
    }
    
    fun getDescription(key: String): AIContentDescription? {
        return _descriptions.value[key]
    }
    
    fun shouldProvideEnhancedDescription(): Boolean {
        return _config.value.aiDescriptionsEnabled && _config.value.screenReaderEnabled
    }
}

@Composable
fun rememberAIAccessibilityManager(): AIAccessibilityManager = remember { AIAccessibilityManager() }

/**
 * AI-powered accessibility modifier
 */
fun Modifier.aiAccessibility(
    manager: AIAccessibilityManager,
    elementType: String,
    content: String,
    context: String = "",
    actions: List<String> = emptyList(),
    key: String = ""
): Modifier {
    val description = remember(elementType, content, context, actions) {
        manager.generateDescription(elementType, content, context, actions)
    }
    
    val config by manager.config.collectAsState()
    
    LaunchedEffect(key, description) {
        if (key.isNotEmpty()) {
            manager.addDescription(key, description)
        }
    }
    
    return this.semantics(mergeDescendants = true) {
        this.contentDescription = buildString {
            append(description.primaryDescription)
            description.secondaryDescription?.let {
                append(". ")
                append(it)
            }
            if (config.aiDescriptionsEnabled && description.actions.isNotEmpty()) {
                append(". ")
                append(description.actions.joinToString(". "))
            }
        }
        
        // Set importance based on AI analysis
        when (description.importance) {
            AccessibilityImportance.CRITICAL -> {
                this.accessibilityRole = Role.Button
                this.stateDescription = "Critical action required"
            }
            AccessibilityImportance.HIGH -> {
                this.accessibilityRole = Role.Button
            }
            AccessibilityImportance.MEDIUM -> {
                this.accessibilityRole = Role.Button
            }
            AccessibilityImportance.LOW -> {
                this.accessibilityRole = Role.Image
            }
            AccessibilityImportance.DEFAULT -> {
                // Use default role
            }
        }
        
        // Add custom actions if available
        if (description.actions.isNotEmpty()) {
            actions = description.actions.mapIndexed { index, action ->
                AccessibilityAction(
                    label = action,
                    action = null
                )
            }
        }
    }
}

/**
 * Adaptive UI based on accessibility needs
 */
@Composable
fun AdaptiveLayout(
    manager: AIAccessibilityManager,
    standardLayout: @Composable () -> Unit,
    accessibleLayout: @Composable () -> Unit
) {
    val config by manager.config.collectAsState()
    
    if (config.largeTextMode || config.highContrastMode || config.screenReaderEnabled) {
        accessibleLayout()
    } else {
        standardLayout()
    }
}

/**
 * Voice navigation system
 */
class VoiceNavigationManager {
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()
    
    private val _currentCommand = MutableStateFlow<String?>(null)
    val currentCommand: StateFlow<String?> = _currentCommand.asStateFlow()
    
    fun enable() {
        _isEnabled.value = true
    }
    
    fun disable() {
        _isEnabled.value = false
    }
    
    fun processCommand(command: String) {
        _currentCommand.value = command
    }
    
    fun getNavigationHints(): List<String> {
        return listOf(
            "Say 'next screen' to navigate forward",
            "Say 'previous screen' to go back",
            "Say 'dashboard' to go home",
            "Say 'stats' to view statistics",
            "Say 'help' for available commands"
        )
    }
}

@Composable
fun rememberVoiceNavigationManager(): VoiceNavigationManager = remember { VoiceNavigationManager() }

/**
 * High contrast mode modifier
 */
fun Modifier.highContrast(
    manager: AIAccessibilityManager
): Modifier {
    val config by manager.config.collectAsState()
    
    return if (config.highContrastMode) {
        this.semantics {
            // Add high contrast indicators
            stateDescription = "High contrast mode enabled"
        }
    } else {
        this
    }
}

/**
 * Large text mode modifier
 */
fun Modifier.largeText(
    manager: AIAccessibilityManager
): Modifier {
    val config by manager.config.collectAsState()
    
    return if (config.largeTextMode) {
        this.semantics {
            // Add large text indicators
            stateDescription = "Large text mode enabled"
        }
    } else {
        this
    }
}

/**
 * Reduced motion modifier
 */
fun Modifier.reducedMotion(
    manager: AIAccessibilityManager
): Modifier {
    val config by manager.config.collectAsState()
    
    return if (config.reducedMotion) {
        this.semantics {
            // Add reduced motion indicators
            stateDescription = "Reduced motion mode enabled"
        }
    } else {
        this
    }
}

/**
 * AI-powered content validator
 */
class ContentValidator {
    fun validateAccessibility(content: String): List<String> {
        val issues = mutableListOf<String>()
        
        // Check for common accessibility issues
        if (content.length < 3) {
            issues.add("Content description too short")
        }
        
        if (content.contains("click here", ignoreCase = true)) {
            issues.add("Use descriptive action text instead of 'click here'")
        }
        
        if (content.matches(Regex("\\d+"))) {
            issues.add("Numbers should be spelled out for screen readers")
        }
        
        if (content.contains("image", ignoreCase = true) && content.length < 10) {
            issues.add("Image descriptions should be more descriptive")
        }
        
        return issues
    }
    
    fun suggestImprovements(content: String): List<String> {
        val suggestions = mutableListOf<String>()
        
        if (content.length < 10) {
            suggestions.add("Consider adding more context to this description")
        }
        
        if (!content.contains("button", ignoreCase = true) && 
            !content.contains("link", ignoreCase = true) && 
            !content.contains("field", ignoreCase = true)) {
            suggestions.add("Consider indicating the element type")
        }
        
        if (content.split(" ").size < 3) {
            suggestions.add("Add more descriptive words for better understanding")
        }
        
        return suggestions
    }
}

/**
 * Accessibility testing helper
 */
@Composable
fun AccessibilityTestOverlay(
    manager: AIAccessibilityManager,
    isVisible: Boolean
) {
    if (isVisible) {
        val config by manager.config.collectAsState()
        
        // This would show an overlay with accessibility information
        // For development and testing purposes
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Accessibility Mode: ${if (config.screenReaderEnabled) "ON" else "OFF"}")
            Text("High Contrast: ${if (config.highContrastMode) "ON" else "OFF"}")
            Text("Large Text: ${if (config.largeTextMode) "ON" else "OFF"}")
            Text("Reduced Motion: ${if (config.reducedMotion) "ON" else "OFF"}")
            Text("AI Descriptions: ${if (config.aiDescriptionsEnabled) "ON" else "OFF"}")
        }
    }
}
