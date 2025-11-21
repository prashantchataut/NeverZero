package com.productivitystreak.ui.screens.reading

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.ui.state.reading.ReadingLog
import com.productivitystreak.ui.state.reading.ReadingTrackerState
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReadingViewModel(
    private val preferencesManager: PreferencesManager,
    private val moshi: Moshi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingTrackerState())
    val uiState: StateFlow<ReadingTrackerState> = _uiState.asStateFlow()

    init {
        loadReadingTrackerData()
    }

    private fun loadReadingTrackerData() {
        viewModelScope.launch {
            try {
                preferencesManager.readingStreakDays.collect { streakDays ->
                    _uiState.update { it.copy(currentStreakDays = streakDays) }
                }
            } catch (e: Exception) {
                Log.e("ReadingViewModel", "Error loading reading streak days", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.readingLastDate.collect { lastDate ->
                    val today = LocalDate.now().toString()
                    if (lastDate != today) {
                        preferencesManager.setPagesReadToday(0)
                        preferencesManager.setReadingLastDate(today)
                    }
                }
            } catch (e: Exception) {
                Log.e("ReadingViewModel", "Error checking reading date", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.pagesReadToday.collect { pages ->
                    _uiState.update { it.copy(pagesReadToday = pages) }
                }
            } catch (e: Exception) {
                Log.e("ReadingViewModel", "Error loading pages read today", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.readingGoalPages.collect { goal ->
                    _uiState.update { it.copy(goalPagesPerDay = goal) }
                }
            } catch (e: Exception) {
                Log.e("ReadingViewModel", "Error loading reading goal", e)
            }
        }

        viewModelScope.launch {
            try {
                preferencesManager.readingActivity.collect { activityJson ->
                    val type = Types.newParameterizedType(List::class.java, ReadingLog::class.java)
                    val adapter = moshi.adapter<List<ReadingLog>>(type)
                    val activity = try {
                        adapter.fromJson(activityJson) ?: emptyList()
                    } catch (e: Exception) {
                        Log.e("ReadingViewModel", "Error parsing reading activity", e)
                        emptyList()
                    }
                    _uiState.update { it.copy(recentActivity = activity) }
                }
            } catch (e: Exception) {
                Log.e("ReadingViewModel", "Error loading reading activity", e)
            }
        }
    }

    fun onLogReadingProgress(pages: Int) {
        if (pages <= 0) return
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        val today = LocalDate.now()

        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val updatedPages = currentState.pagesReadToday + pages
                val newLog = ReadingLog(
                    dateLabel = today.format(formatter),
                    pages = pages
                )
                val updatedActivity = (listOf(newLog) + currentState.recentActivity).take(7)

                val shouldIncrementStreak = updatedPages >= currentState.goalPagesPerDay
                val updatedStreakDays = if (shouldIncrementStreak) {
                    currentState.currentStreakDays + 1
                } else {
                    currentState.currentStreakDays
                }

                _uiState.update { state ->
                    state.copy(
                        pagesReadToday = updatedPages,
                        recentActivity = updatedActivity,
                        currentStreakDays = updatedStreakDays
                    )
                }

                preferencesManager.setPagesReadToday(updatedPages)
                preferencesManager.setReadingLastDate(today.toString())
                if (shouldIncrementStreak) {
                    preferencesManager.setReadingStreakDays(updatedStreakDays)
                }

                val type = Types.newParameterizedType(List::class.java, ReadingLog::class.java)
                val adapter = moshi.adapter<List<ReadingLog>>(type)
                val activityJson = adapter.toJson(updatedActivity)
                preferencesManager.setReadingActivity(activityJson)

                // checkReadingAchievements(updatedPages, updatedStreakDays) // TODO: Implement achievements logic
            } catch (e: Exception) {
                Log.e("ReadingViewModel", "Error saving reading progress", e)
            }
        }
    }
}
