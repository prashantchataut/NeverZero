package com.productivitystreak.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.model.UserContext
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.UiMessage
import com.productivitystreak.ui.state.AddUiState
import com.productivitystreak.ui.state.AddEntryType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class AppViewModel(
    application: Application,
    private val quoteRepository: QuoteRepository,
    private val preferencesManager: PreferencesManager,
    private val streakRepository: com.productivitystreak.data.repository.StreakRepository,
    private val templateRepository: com.productivitystreak.data.repository.TemplateRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var quoteRefreshJob: Job? = null

    init {
        loadUserPreferences()
        refreshQuote()
    }

    private fun loadUserPreferences() {
        viewModelScope.launch {
            preferencesManager.userName.collect { name ->
                if (name.isNotEmpty()) {
                    _uiState.update { it.copy(userName = name) }
                }
            }
        }
        
        viewModelScope.launch {
            preferencesManager.totalPoints.collect { points ->
                _uiState.update { it.copy(totalPoints = points) }
            }
        }
    }

    fun refreshQuote() {
        quoteRefreshJob?.cancel()
        quoteRefreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isQuoteLoading = true, uiMessage = null) }
            try {
                val snapshot = _uiState.value
                
                // Simplified context for now - specific ViewModels handle their own data
                // We use placeholders here or rely on PreferencesManager if needed
                val userContext = UserContext(
                    userName = snapshot.userName,
                    currentStreakDays = 0, // Placeholder, real data in StreakViewModel
                    totalTasksToday = 0,
                    completedTasksToday = 0,
                    timeOfDay = LocalTime.now(),
                    lastActivityDate = null,
                    totalPoints = snapshot.totalPoints
                )
                
                val quote = quoteRepository.getPersonalizedQuote(userContext)
                _uiState.update { state ->
                    state.copy(
                        quote = quote,
                        isQuoteLoading = false
                    )
                }
            } catch (error: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isQuoteLoading = false,
                        uiMessage = UiMessage(
                            text = error.message ?: "Unable to load quote",
                            isBlocking = false,
                            actionLabel = "Retry"
                        )
                    )
                }
            }
        }
    }

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
