package com.productivitystreak.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    private var chatSession: BuddhaRepository.ChatSessionWrapper? = null
    
    private val _uiState = MutableStateFlow(BuddhaChatUiState())
    val uiState: StateFlow<BuddhaChatUiState> = _uiState.asStateFlow()

    // Removed init block to allow lazy start with userName

    fun startChat(userName: String) {
        if (chatSession != null) return // Already started
        
        chatSession = repository.createChatSession(userName)
        _uiState.value = BuddhaChatUiState(
            messages = chatSession?.history?.map { content ->
                BuddhaChatMessage(
                    text = (content.parts.firstOrNull() as? TextPart)?.text ?: "",
                    isUser = content.role == "user",
                    status = MessageStatus.SENT
                )
            } ?: emptyList()
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val currentMessages = _uiState.value.messages.toMutableList()
        val userMessage = BuddhaChatMessage(
            id = java.util.UUID.randomUUID().toString(),
            text = text,
            isUser = true,
            status = MessageStatus.SENDING
        )
        currentMessages.add(userMessage)
        _uiState.value = _uiState.value.copy(messages = currentMessages, isLoading = true)

        viewModelScope.launch {
            try {
                val responseText = chatSession?.sendMessage(text)

                val updatedMessages = _uiState.value.messages.toMutableList()
                // Update user message to SENT
                val index = updatedMessages.indexOfFirst { it.id == userMessage.id }
                if (index != -1) {
                    updatedMessages[index] = userMessage.copy(status = MessageStatus.SENT)
                }

                if (!responseText.isNullOrBlank()) {
                    updatedMessages.add(
                        BuddhaChatMessage(
                            id = java.util.UUID.randomUUID().toString(),
                            text = responseText,
                            isUser = false,
                            status = MessageStatus.SENT
                        )
                    )
                }
                _uiState.value = _uiState.value.copy(messages = updatedMessages, isLoading = false)
            } catch (e: Exception) {
                android.util.Log.e("BuddhaChat", "Error generating response", e)
                
                val updatedMessages = _uiState.value.messages.toMutableList()
                val index = updatedMessages.indexOfFirst { it.id == userMessage.id }
                if (index != -1) {
                    updatedMessages[index] = userMessage.copy(status = MessageStatus.ERROR)
                }
                
                _uiState.value = _uiState.value.copy(
                    messages = updatedMessages, 
                    isLoading = false,
                    errorEvent = "Connection weak, meditating..."
                )
            }
        }
    }

    fun retryMessage(messageId: String) {
        val message = _uiState.value.messages.find { it.id == messageId } ?: return
        if (message.status != MessageStatus.ERROR) return

        // Remove the failed message and re-send its text
        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.remove(message)
        _uiState.value = _uiState.value.copy(messages = currentMessages)
        
        sendMessage(message.text)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorEvent = null)
    }
}

data class BuddhaChatUiState(
    val messages: List<BuddhaChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorEvent: String? = null
)

data class BuddhaChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENDING, SENT, ERROR
}

class BuddhaChatViewModelFactory(private val repository: BuddhaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuddhaChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BuddhaChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
