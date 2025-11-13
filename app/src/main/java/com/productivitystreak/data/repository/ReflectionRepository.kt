package com.productivitystreak.data.repository

import com.productivitystreak.data.local.dao.DailyReflectionDao
import com.productivitystreak.data.local.entity.DailyReflectionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class ReflectionRepository(private val reflectionDao: DailyReflectionDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun observeAllReflections(): Flow<List<DailyReflectionEntity>> = reflectionDao.getAllReflections()

    fun observeRecentReflections(limit: Int = 7): Flow<List<DailyReflectionEntity>> =
        reflectionDao.getRecentReflections(limit)

    suspend fun getTodayReflection(): DailyReflectionEntity? {
        val today = dateFormat.format(Date())
        return reflectionDao.getReflectionByDate(today)
    }

    suspend fun getReflectionByDate(date: String): DailyReflectionEntity? =
        reflectionDao.getReflectionByDate(date)

    suspend fun saveReflection(
        date: String = dateFormat.format(Date()),
        mood: Int,
        notes: String,
        highlights: String? = null,
        challenges: String? = null,
        gratitude: String? = null,
        tomorrowGoals: String? = null
    ) {
        val reflection = DailyReflectionEntity(
            date = date,
            mood = mood,
            notes = notes,
            highlights = highlights,
            challenges = challenges,
            gratitude = gratitude,
            tomorrowGoals = tomorrowGoals,
            lastUpdated = System.currentTimeMillis()
        )
        reflectionDao.insertReflection(reflection)
    }

    suspend fun updateReflection(reflection: DailyReflectionEntity) {
        val updated = reflection.copy(lastUpdated = System.currentTimeMillis())
        reflectionDao.updateReflection(updated)
    }

    suspend fun deleteReflection(reflection: DailyReflectionEntity) {
        reflectionDao.deleteReflection(reflection)
    }

    suspend fun getReflectionCount(): Int = reflectionDao.getReflectionCount()

    suspend fun getAverageMood(daysBack: Int = 30): Float? {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysBack)
        val startDate = dateFormat.format(calendar.time)
        return reflectionDao.getAverageMoodSince(startDate)
    }
}
