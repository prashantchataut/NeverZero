package com.productivitystreak.data.repository

import com.productivitystreak.data.local.dao.AchievementDao
import com.productivitystreak.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val achievementDao: AchievementDao) {

    fun observeAllAchievements(): Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    fun observeUnlockedAchievements(): Flow<List<AchievementEntity>> = achievementDao.getUnlockedAchievements()

    fun observeLockedAchievements(): Flow<List<AchievementEntity>> = achievementDao.getLockedAchievements()

    fun observeAchievementsByCategory(category: String): Flow<List<AchievementEntity>> =
        achievementDao.getAchievementsByCategory(category)

    suspend fun getAchievementById(id: String): AchievementEntity? =
        achievementDao.getAchievementById(id)

    suspend fun updateProgress(achievementId: String, progress: Int): Boolean {
        val achievement = achievementDao.getAchievementById(achievementId) ?: return false

        val newProgress = minOf(progress, achievement.requirement)
        achievementDao.updateProgress(achievementId, newProgress)

        // Unlock if requirement met
        if (newProgress >= achievement.requirement && !achievement.isUnlocked) {
            achievementDao.unlockAchievement(achievementId, System.currentTimeMillis())
            return true // Achievement just unlocked
        }

        return false
    }

    suspend fun incrementProgress(achievementId: String, amount: Int = 1): Boolean {
        val achievement = achievementDao.getAchievementById(achievementId) ?: return false
        return updateProgress(achievementId, achievement.progress + amount)
    }

    suspend fun getUnlockedCount(): Int = achievementDao.getUnlockedCount()

    suspend fun getTotalPoints(): Int = achievementDao.getTotalPoints() ?: 0

    suspend fun initializeAchievements() {
        val existing = achievementDao.getUnlockedCount()
        if (existing == 0) {
            achievementDao.insertAchievements(defaultAchievements())
        }
    }

    private fun defaultAchievements(): List<AchievementEntity> = listOf(
        // Streak Achievements
        AchievementEntity(
            id = "streak_7",
            title = "Week Warrior",
            description = "Maintain a 7-day streak",
            icon = "local_fire_department",
            category = "Streaks",
            requirement = 7,
            tier = "bronze",
            points = 10
        ),
        AchievementEntity(
            id = "streak_30",
            title = "Monthly Master",
            description = "Maintain a 30-day streak",
            icon = "local_fire_department",
            category = "Streaks",
            requirement = 30,
            tier = "silver",
            points = 50
        ),
        AchievementEntity(
            id = "streak_100",
            title = "Century Club",
            description = "Maintain a 100-day streak",
            icon = "local_fire_department",
            category = "Streaks",
            requirement = 100,
            tier = "gold",
            points = 200
        ),
        AchievementEntity(
            id = "streak_365",
            title = "Year of Excellence",
            description = "Maintain a 365-day streak",
            icon = "emoji_events",
            category = "Streaks",
            requirement = 365,
            tier = "platinum",
            points = 1000
        ),

        // Reading Achievements
        AchievementEntity(
            id = "books_1",
            title = "First Book",
            description = "Finish your first book",
            icon = "menu_book",
            category = "Reading",
            requirement = 1,
            tier = "bronze",
            points = 10
        ),
        AchievementEntity(
            id = "books_10",
            title = "Bookworm",
            description = "Finish 10 books",
            icon = "menu_book",
            category = "Reading",
            requirement = 10,
            tier = "silver",
            points = 50
        ),
        AchievementEntity(
            id = "pages_1000",
            title = "Page Turner",
            description = "Read 1000 pages",
            icon = "auto_stories",
            category = "Reading",
            requirement = 1000,
            tier = "silver",
            points = 40
        ),

        // Vocabulary Achievements
        AchievementEntity(
            id = "words_50",
            title = "Word Collector",
            description = "Learn 50 new words",
            icon = "school",
            category = "Vocabulary",
            requirement = 50,
            tier = "bronze",
            points = 20
        ),
        AchievementEntity(
            id = "words_200",
            title = "Vocabulary Master",
            description = "Learn 200 new words",
            icon = "school",
            category = "Vocabulary",
            requirement = 200,
            tier = "silver",
            points = 80
        ),
        AchievementEntity(
            id = "words_500",
            title = "Lexicon Legend",
            description = "Learn 500 new words",
            icon = "emoji_events",
            category = "Vocabulary",
            requirement = 500,
            tier = "gold",
            points = 200
        ),

        // Reflection Achievements
        AchievementEntity(
            id = "reflections_7",
            title = "Thoughtful Week",
            description = "Write reflections for 7 days",
            icon = "edit_note",
            category = "Reflections",
            requirement = 7,
            tier = "bronze",
            points = 15
        ),
        AchievementEntity(
            id = "reflections_30",
            title = "Mindful Month",
            description = "Write reflections for 30 days",
            icon = "edit_note",
            category = "Reflections",
            requirement = 30,
            tier = "silver",
            points = 60
        )
    )
}
