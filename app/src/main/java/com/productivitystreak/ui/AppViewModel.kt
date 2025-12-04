package com.productivitystreak.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.model.UserContext
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.UiMessage
import com.productivitystreak.ui.state.UiMessageType
import com.productivitystreak.ui.state.AddUiState
import com.productivitystreak.ui.state.AddEntryType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

import com.productivitystreak.data.repository.GeminiRepository

class AppViewModel(
    application: Application,
    private val quoteRepository: QuoteRepository,
    private val preferencesManager: PreferencesManager,
    private val streakRepository: com.productivitystreak.data.repository.StreakRepository,
    private val templateRepository: com.productivitystreak.data.repository.TemplateRepository,
    private val geminiRepository: GeminiRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var quoteRefreshJob: Job? = null

    init {
        loadUserPreferences()
        refreshQuote()
    }

    // ... (loadUserPreferences remains unchanged)

    fun refreshQuote() {
        quoteRefreshJob?.cancel()
        quoteRefreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isQuoteLoading = true, uiMessage = null) }
            try {
                // Fetch user stats (placeholder for now, ideally from repository)
                val userStats = com.productivitystreak.data.local.entity.UserStats.default()
                
                when (val result = geminiRepository.getDailyWisdom(userStats)) {
                    is com.productivitystreak.data.repository.RepositoryResult.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                quote = com.productivitystreak.data.model.Quote(
                                    text = result.data,
                                    author = "The Digital Ascetic",
                                    category = "Wisdom"
                                ),
                                isQuoteLoading = false
                            )
                        }
                    }
                    is com.productivitystreak.data.repository.RepositoryResult.PermissionError -> {
                        _uiState.update { state ->
                            state.copy(
                                isQuoteLoading = false,
                                uiMessage = UiMessage(
                                    text = "Gemini API Key missing. Please configure it in local.properties.",
                                    type = UiMessageType.ERROR,
                                    isBlocking = false,
                                    actionLabel = "Retry"
                                )
                            )
                        }
                    }
                    is com.productivitystreak.data.repository.RepositoryResult.NetworkError,
                    is com.productivitystreak.data.repository.RepositoryResult.UnknownError -> {
                        _uiState.update { state ->
                            state.copy(
                                isQuoteLoading = false,
                                uiMessage = UiMessage(
                                    text = "Connection weak. Meditating...",
                                    type = UiMessageType.ERROR,
                                    isBlocking = false,
                                    actionLabel = "Retry"
                                )
                            )
                        }
                    }
                    else -> { /* Handle other cases if needed */ }
                }
            } catch (error: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isQuoteLoading = false,
                        uiMessage = UiMessage(
                            text = "Connection weak. Meditating...",
                            isBlocking = false,
                            actionLabel = "Retry"
                        )
                    )
                }
            }
        }
    }

    fun onDismissUiMessage() {
        _uiState.update { it.copy(uiMessage = null) }
    }

    fun onAddButtonTapped() {
        updateAddState { it.copy(isMenuOpen = true) }
    }

    fun onDismissAddMenu() {
        updateAddState { it.copy(isMenuOpen = false) }
    }

    fun onAddEntrySelected(type: AddEntryType) {
        updateAddState { it.copy(activeForm = type, isMenuOpen = false) }
    }

    fun onDismissAddForm() {
        completeAddFlow()
    }
    
    fun setAddSubmitting(isSubmitting: Boolean) {
        updateAddState { it.copy(isSubmitting = isSubmitting) }
    }
    
    fun completeAddFlow() {
        _uiState.update { it.copy(addUiState = AddUiState()) }
    }

    private fun updateAddState(transform: (AddUiState) -> AddUiState) {
        _uiState.update { state -> state.copy(addUiState = transform(state.addUiState)) }
    }

    // Permission Dialogs
    fun onShowNotificationPermissionDialog() {
        _uiState.update { state ->
            state.copy(permissionState = state.permissionState.copy(showNotificationDialog = true))
        }
    }

    fun onDismissNotificationPermissionDialog() {
        _uiState.update { state ->
            state.copy(permissionState = state.permissionState.copy(showNotificationDialog = false))
        }
    }

    fun onShowAlarmPermissionDialog() {
        _uiState.update { state ->
            state.copy(permissionState = state.permissionState.copy(showAlarmDialog = true))
        }
    }

    fun onDismissAlarmPermissionDialog() {
        _uiState.update { state ->
            state.copy(permissionState = state.permissionState.copy(showAlarmDialog = false))
        }
    }

    // Template Library
    fun getTemplates() = templateRepository.getCuratedTemplates()

    fun getTemplatesByCategory(category: String) = templateRepository.getTemplatesByCategory(category)

    fun importTemplate(template: com.productivitystreak.data.model.StreakTemplate) {
        viewModelScope.launch {
            val result = streakRepository.createStreakFromTemplate(template)
            if (result is com.productivitystreak.data.repository.RepositoryResult.Success) {
                _uiState.update { 
                    it.copy(uiMessage = UiMessage(text = "Habit imported: ${template.name}", type = UiMessageType.SUCCESS)) 
                }
            } else {
                _uiState.update { 
                    it.copy(uiMessage = UiMessage(text = "Failed to import habit.", type = UiMessageType.ERROR)) 
                }
            }
        }
    }
}
