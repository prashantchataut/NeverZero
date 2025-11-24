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
import kotlin.math.roundToInt

data class StreakUiState(
    val streaks: List<Streak> = emptyList(),
    val selectedStreakId: String? = null,
    val todayTasks: List<DashboardTask> = emptyList(),
    val oneOffTasks: List<com.productivitystreak.data.model.Task> = emptyList(),
    val statsState: StatsState = StatsState(),
    val skillPathsState: SkillPathsState = SkillPathsState(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val buddhaInsight: String? = null
)

class StreakViewModel(
    private val streakRepository: StreakRepository,
    private val preferencesManager: com.productivitystreak.data.local.PreferencesManager,
    private val moshi: com.squareup.moshi.Moshi,
    private val geminiClient: com.productivitystreak.data.gemini.GeminiClient,
    private val socialRepository: com.productivitystreak.data.repository.SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    private val taskListAdapter = moshi.adapter<List<com.productivitystreak.data.model.Task>>(
        com.squareup.moshi.Types.newParameterizedType(List::class.java, com.productivitystreak.data.model.Task::class.java)
    )

    init {
        observeStreaks()
        observeTopStreakLeaderboard()
        observeGlobalLeaderboard()
        observeOneOffTasks()
        fetchBuddhaInsight()
    }

    private fun fetchBuddhaInsight() {
        viewModelScope.launch {
            // In a real app, check cache first. For now, fetch fresh to demonstrate AI.
            val insight = geminiClient.generateBuddhaInsight()
            _uiState.update { it.copy(buddhaInsight = insight) }
        }
    }

    // ... (rest of the file)

    private fun observeOneOffTasks() {
        viewModelScope.launch {
            preferencesManager.oneOffTasks.collectLatest { json ->
                val tasks = try {
                    taskListAdapter.fromJson(json) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
                _uiState.update { it.copy(oneOffTasks = tasks) }
            }
        }
    }

    private fun saveOneOffTasks(tasks: List<com.productivitystreak.data.model.Task>) {
        viewModelScope.launch {
            val json = taskListAdapter.toJson(tasks)
            preferencesManager.setOneOffTasks(json)
        }
    }

    // ... (existing methods)

    fun addOneOffTask(title: String) {
        if (title.isBlank()) return
        val newTask = com.productivitystreak.data.model.Task(title = title)
        val currentTasks = _uiState.value.oneOffTasks
        saveOneOffTasks(currentTasks + newTask)
    }

    fun toggleOneOffTask(taskId: String) {
        val currentTasks = _uiState.value.oneOffTasks
        val updatedTasks = currentTasks.map { 
            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it 
        }
        saveOneOffTasks(updatedTasks)
    }

    fun deleteOneOffTask(taskId: String) {
        val currentTasks = _uiState.value.oneOffTasks
        val updatedTasks = currentTasks.filter { it.id != taskId }
        saveOneOffTasks(updatedTasks)
    }

    // ... (rest of the file)


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
        val windowSize = 7
        val groupedByDate = streaks
            .flatMap { streak -> streak.history.takeLast(windowSize) }
            .groupBy { it.date }
            .toSortedMap()

        val points = groupedByDate.map { (date, records) ->
            val percent = if (records.isEmpty()) 0 else {
                val average = records.sumOf { it.completionFraction.toDouble() } / records.size
                (average * 100).roundToInt()
            }
            com.productivitystreak.ui.state.stats.TrendPoint(date = date, percent = percent)
        }.takeLast(windowSize)

        return com.productivitystreak.ui.state.stats.AverageDailyTrend(
            windowSize = windowSize,
            points = points
        )
    }

    private fun computeStreakConsistency(streaks: List<Streak>): List<com.productivitystreak.ui.state.stats.ConsistencyScore> {
        if (streaks.isEmpty()) return emptyList()

        return streaks.map { streak ->
            val recentFractions = streak.history.takeLast(14).map { it.completionFraction }
            val average = if (recentFractions.isEmpty()) 0f else recentFractions.average().toFloat()
            val score = (average * 100).roundToInt()
            val variance = if (recentFractions.size <= 1) 0f else {
                val diffSum = recentFractions.fold(0f) { acc, value ->
                    val diff = value - average
                    acc + diff * diff
                }
                diffSum / (recentFractions.size - 1)
            }
            val level = when {
                score >= 80 -> com.productivitystreak.ui.state.stats.ConsistencyLevel.High
                score >= 50 -> com.productivitystreak.ui.state.stats.ConsistencyLevel.Medium
                else -> com.productivitystreak.ui.state.stats.ConsistencyLevel.NeedsAttention
            }

            com.productivitystreak.ui.state.stats.ConsistencyScore(
                streakId = streak.id,
                streakName = streak.name,
                score = score,
                completionRate = score,
                variance = variance,
                level = level
            )
        }
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

    private fun observeGlobalLeaderboard() {
        viewModelScope.launch {
            try {
                socialRepository.getGlobalLeaderboard().collectLatest { globalEntries ->
                    _uiState.update { state ->
                        state.copy(
                            statsState = state.statsState.copy(
                                globalLeaderboard = globalEntries
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleLeaderboardType(type: com.productivitystreak.ui.state.stats.LeaderboardType) {
        _uiState.update { state ->
            state.copy(
                statsState = state.statsState.copy(leaderboardType = type)
            )
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
