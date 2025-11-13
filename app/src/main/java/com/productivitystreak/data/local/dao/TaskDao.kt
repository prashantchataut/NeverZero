package com.productivitystreak.data.local.dao

import androidx.room.*
import com.productivitystreak.data.local.entity.HabitCompletionEntity
import com.productivitystreak.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY priority DESC, createdAt ASC")
    fun observeActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC LIMIT :limit")
    fun observeCompletedTasks(limit: Int = 50): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    fun observeTasksByCategory(category: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    suspend fun getActiveTaskCount(): Int

    @Insert
    suspend fun insertHabitCompletion(completion: HabitCompletionEntity)

    @Query("SELECT * FROM habit_completions WHERE taskId = :taskId ORDER BY completedAt DESC LIMIT :limit")
    suspend fun getHabitCompletions(taskId: String, limit: Int = 30): List<HabitCompletionEntity>

    @Query("""
        SELECT * FROM habit_completions
        WHERE completedAt >= :startDate AND completedAt < :endDate
        ORDER BY completedAt DESC
    """)
    suspend fun getCompletionsInRange(startDate: Long, endDate: Long): List<HabitCompletionEntity>
}
