package com.productivitystreak.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.model.Streak
import com.productivitystreak.data.repository.RepositoryResult
import com.productivitystreak.data.repository.StreakRepository
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.state.stats.LeaderboardEntry
import com.productivitystreak.ui.state.stats.StatsState
import com.productivitystreak.ui.state.skills.SkillPathsState
import com.productivitystreak.ui.utils.SkillPathsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StreakUiState(
    val streaks: List<Streak> = emptyList(),
    val selectedStreakId: String? = null,
    val todayTasks: List<DashboardTask> = emptyList(),
    val oneOffTasks: List<com.productivitystreak.data.model.Task> = emptyList(),
    val statsState: StatsState = StatsState(),
    val skillPathsState: SkillPathsState = SkillPathsState(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class StreakViewModel(
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    init {
        observeStreaks()
        observeTopStreakLeaderboard()
    }

    private fun observeStreaks() {
        viewModelScope.launch {
            try {
                streakRepository.observeStreaks().collectLatest { streaks ->
                    val stats = withContext(Dispatchers.Default) {
                        buildStatsStateFromStreaks(streaks)
                    }
                    val skillPaths = withContext(Dispatchers.Default) {
                        SkillPathsHelper.computeSkillPathsState(streaks)
                    }

                    _uiState.update { state ->
                        val selectedId = state.selectedStreakId ?: streaks.firstOrNull()?.id
                        state.copy(
                            streaks = streaks,
                            selectedStreakId = selectedId,
                            todayTasks = buildTasksForStreaks(streaks),
                            statsState = stats,
                            skillPathsState = skillPaths
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun buildStatsStateFromStreaks(streaks: List<Streak>): StatsState {
        if (streaks.isEmpty()) return StatsState()

        val longestStreak = streaks.maxByOrNull { it.currentCount }
        val avgProgress = streaks.map { it.progress }.average() * 100
        val habitBreakdown = streaks.map { streak ->
            com.productivitystreak.ui.state.stats.HabitBreakdown(
                name = streak.name,
                completionPercent = (streak.progress * 100).toInt(),
                accentHex = streak.color
            )
        }

        val dailyTrend = computeAverageDailyTrend(streaks)
        val consistency = computeStreakConsistency(streaks)
        val heatMap = computeCalendarHeatMap(streaks)

        return StatsState(
            currentLongestStreak = longestStreak?.currentCount ?: 0,
            currentLongestStreakName = longestStreak?.name ?: "",
            averageDailyProgressPercent = avgProgress.toInt(),
            averageDailyTrend = dailyTrend,
            streakConsistency = consistency,
            habitBreakdown = habitBreakdown,
            leaderboard = emptyList(), // Handled separately by observeTopStreaks
            calendarHeatMap = heatMap
        )
    }

    private fun computeAverageDailyTrend(streaks: List<Streak>): com.productivitystreak.ui.state.stats.AverageDailyTrend {
        // Placeholder implementation
        return com.productivitystreak.ui.state.stats.AverageDailyTrend(
            points = emptyList(),
            trendDirection = "stable",
            changePercent = 0
        )
    }

    private fun computeStreakConsistency(streaks: List<Streak>): com.productivitystreak.ui.state.stats.ConsistencyScore {
        // Placeholder implementation
        return com.productivitystreak.ui.state.stats.ConsistencyScore(
            score = 85,
            level = com.productivitystreak.ui.state.stats.ConsistencyLevel.GOOD,
            description = "You are doing great!"
        )
    }

    private fun computeCalendarHeatMap(
        streaks: List<Streak>,
        totalWeeks: Int = 6
    ): com.productivitystreak.ui.state.stats.CalendarHeatMap? {
        if (streaks.isEmpty()) return null

        val daysPerWeek = 7
        val horizonDays = totalWeeks * daysPerWeek
        val today = java.time.LocalDate.now()
        val startDate = today.minusDays((horizonDays - 1).toLong())

        val aggregates = mutableMapOf<java.time.LocalDate, MutableList<Float>>()
        streaks.forEach { streak ->
            streak.history.forEach { record ->
                val date = try { java.time.LocalDate.parse(record.date) } catch (e: Exception) { null } ?: return@forEach
                val fraction = record.completionFraction.coerceIn(0f, 1f)
                aggregates.getOrPut(date) { mutableListOf() }.add(fraction)
            }
        }

        var activeDayCount = 0
        val days = (0 until horizonDays).map { offset ->
            val date = startDate.plusDays(offset.toLong())
            val values = aggregates[date]
            val intensity = values?.let { list ->
                (list.sum() / list.size).coerceIn(0f, 1f)
            } ?: 0f
            if (intensity > 0f) activeDayCount++
            com.productivitystreak.ui.state.stats.HeatMapDay(
                date = date.toString(),
                intensity = intensity,
                isToday = date == today
            )
        }

        if (days.all { it.intensity == 0f }) {
            return null
        }

        val weeks = days.chunked(daysPerWeek).map { chunk ->
            com.productivitystreak.ui.state.stats.HeatMapWeek(days = chunk)
        }

        return com.productivitystreak.ui.state.stats.CalendarHeatMap(
            weeks = weeks,
            completedDays = activeDayCount,
            totalDays = horizonDays
        )
    }

    private fun buildTasksForStreaks(streaks: List<Streak>): List<DashboardTask> {
        if (streaks.isEmpty()) return emptyList()
        return streaks.map { streak ->
            DashboardTask(
                id = "task-${streak.id}",
                title = "Log ${streak.goalPerDay} ${streak.unit}",
                category = streak.category,
                streakId = streak.id,
                isCompleted = streak.history.lastOrNull()?.metGoal == true,
                accentHex = streak.color
            )
        }
    }

    private fun observeTopStreakLeaderboard(limit: Int = 5) {
        viewModelScope.launch {
            try {
                streakRepository.observeTopStreaks(limit).collectLatest { topStreaks ->
                    _uiState.update { state ->
                        state.copy(
                            statsState = state.statsState.copy(
                                leaderboard = topStreaks.mapIndexed { index, streak ->
                                    LeaderboardEntry(
                                        position = index + 1,
                                        name = streak.name,
                                        streakDays = streak.currentCount
                                    )
                                }
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun onSelectStreak(streakId: String) {
        _uiState.update { it.copy(selectedStreakId = streakId) }
    }

    fun onToggleTask(taskId: String) {
        val snapshot = _uiState.value
        val task = snapshot.todayTasks.find { it.id == taskId } ?: return
        if (task.isCompleted) return

        val streak = snapshot.streaks.find { it.id == task.streakId }
        if (streak == null) {
            return
        }

        // Optimistic update
        _uiState.update { state ->
            state.copy(
                todayTasks = state.todayTasks.map { current ->
                    if (current.id == taskId) current.copy(isCompleted = true) else current
                }
            )
        }

        viewModelScope.launch {
            try {
                streakRepository.logProgress(task.streakId, streak.goalPerDay)
            } catch (e: Exception) {
                // Revert on failure
                _uiState.update { state ->
                    state.copy(todayTasks = buildTasksForStreaks(state.streaks))
                }
            }
        }
    }

    fun addOneOffTask(title: String) {
        // TODO: Implement one-off tasks in repository
    }

    fun toggleOneOffTask(taskId: String) {
        // TODO: Implement one-off tasks
    }

    fun deleteOneOffTask(taskId: String) {
        // TODO: Implement one-off tasks
    }

    fun simulateTaskCompletion(streakId: String, count: Int) {
         viewModelScope.launch {
            streakRepository.logProgress(streakId, count)
        }
    }

    fun createStreak(
        name: String,
        goalPerDay: Int,
        unit: String,
        category: String,
        colorHex: String?,
        iconName: String?
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Streak name can't be empty.") }
            return
        }
        val safeUnit = unit.trim().ifBlank { "count" }
        val safeCategory = category.trim().ifBlank { "Focus" }
        val goal = goalPerDay.coerceAtLeast(1)
        val tint = colorHex?.ifBlank { null } ?: "#6366F1"
        val icon = iconName?.ifBlank { null } ?: "flag"

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            when (
                val result = streakRepository.createStreak(
                    name = trimmedName,
                    goalPerDay = goal,
                    unit = safeUnit,
                    category = safeCategory,
                    color = tint,
                    icon = icon
                )
            ) {
                is RepositoryResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSubmitting = false,
                            successMessage = "Streak added to your dashboard"
                        )
                    }
                }
                else -> {
                    _uiState.update { 
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "Couldn't create streak. Please try again."
                        )
                    }
                }
            }
        }
    }
}
