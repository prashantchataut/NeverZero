package com.productivitystreak.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.productivitystreak.data.local.dao.*
import com.productivitystreak.data.local.entity.*

@Database(
    entities = [
        StreakEntity::class,
        QuoteEntity::class,
        VocabularyWordEntity::class,
        BookEntity::class,
        ReadingSessionEntity::class,
        DailyReflectionEntity::class,
        AchievementEntity::class,
        TimeCapsuleEntity::class,
        // New Protocol-based entities
        ProtocolEntity::class,
        DailyLogEntity::class,
        UserStatsEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // Existing DAOs
    abstract fun streakDao(): StreakDao
    abstract fun quoteDao(): QuoteDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun bookDao(): BookDao
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun dailyReflectionDao(): DailyReflectionDao
    abstract fun achievementDao(): AchievementDao
    abstract fun timeCapsuleDao(): TimeCapsuleDao

    // Protocol-based DAOs
    abstract fun protocolDao(): ProtocolDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "productivity_streak_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

