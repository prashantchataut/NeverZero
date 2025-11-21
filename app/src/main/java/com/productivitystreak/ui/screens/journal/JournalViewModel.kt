package com.productivitystreak.ui.screens.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.repository.ReflectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JournalViewModel(
    private val reflectionRepository: ReflectionRepository
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()
    
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    fun onSubmitJournalEntry(
        mood: Int,
        notes: String,
        highlights: String?,
        gratitude: String?,
        tomorrowGoals: String?
    ) {
        val trimmedNotes = notes.trim()
        if (trimmedNotes.isBlank()) {
            _uiMessage.value = "Journal entry can’t be empty."
            return
        }
        val safeMood = mood.coerceIn(1, 5)
        val cleanedHighlights = highlights?.trim()?.takeIf { it.isNotBlank() }
        val cleanedGratitude = gratitude?.trim()?.takeIf { it.isNotBlank() }
        val cleanedTomorrow = tomorrowGoals?.trim()?.takeIf { it.isNotBlank() }

        _isSubmitting.value = true
        viewModelScope.launch {
            try {
                reflectionRepository.saveReflection(
                    mood = safeMood,
                    notes = trimmedNotes,
                    highlights = cleanedHighlights,
                    gratitude = cleanedGratitude,
                    tomorrowGoals = cleanedTomorrow
                )
                _uiMessage.value = "Journal entry saved"
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error saving journal entry", e)
                _uiMessage.value = "Couldn’t save journal entry. Please retry."
            } finally {
                _isSubmitting.value = false
            }
        }
    }
    
    fun clearMessage() {
        _uiMessage.value = null
    }
}
