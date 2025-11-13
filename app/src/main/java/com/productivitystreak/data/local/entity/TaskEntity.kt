package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String? = null,
    val category: String,
    val streakId: String? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val dueDate: Long? = null,
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH
    val createdAt: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val recurringDays: String? = null // Comma-separated days: "MON,WED,FRI"
)

@Entity(tableName = "habit_completions")
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: String,
    val completedAt: Long = System.currentTimeMillis(),
    val value: Int = 1,
    val notes: String? = null
)
