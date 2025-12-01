package com.productivitystreak.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart
import com.productivitystreak.data.ai.BuddhaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuddhaChatViewModel(
    private val repository: BuddhaRepository
) : ViewModel() {

    private var chatSession: Chat? = null
    
    private val _uiState = MutableStateFlow(BuddhaChatUiState())
    val uiState: StateFlow<BuddhaChatUiState> = _uiState.asStateFlow()

    init {
        startChat()
    }

    fun startChat() {
        chatSession = repository.createChatSession()
        _uiState.value = BuddhaChatUiState(
            messages = chatSession?.history?.map { content ->
                BuddhaChatMessage(
                    text = (content.parts.firstOrNull() as? TextPart)?.text ?: "",
                    isUser = content.role == "user"
                )
            } ?: emptyList()
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(BuddhaChatMessage(text, isUser = true))
        _uiState.value = _uiState.value.copy(messages = currentMessages, isLoading = true)

        viewModelScope.launch {
            try {
                val response = chatSession?.sendMessage(text)
                val responseText = response?.text

                if (responseText != null) {
                    val updatedMessages = _uiState.value.messages.toMutableList()
                    updatedMessages.add(BuddhaChatMessage(responseText, isUser = false))
                    _uiState.value = _uiState.value.copy(messages = updatedMessages, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false) // Handle error/empty
                }
            } catch (e: Exception) {
                // Log the actual error for debugging
                android.util.Log.e("BuddhaChat", "Error generating response", e)
                
                val updatedMessages = _uiState.value.messages.toMutableList()
                // Show a mystical, thematic error message instead of raw technical details
                updatedMessages.add(BuddhaChatMessage("The cosmos is silent right now. Please try again later.", isUser = false))
                _uiState.value = _uiState.value.copy(messages = updatedMessages, isLoading = false)
            }
        }
    }
}

data class BuddhaChatUiState(
    val messages: List<BuddhaChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

data class BuddhaChatMessage(
    val text: String,
    val isUser: Boolean
)

class BuddhaChatViewModelFactory(private val repository: BuddhaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuddhaChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BuddhaChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
