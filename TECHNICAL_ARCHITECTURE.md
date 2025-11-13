# Technical Architecture Documentation

## 📋 Table of Contents
1. [Overview](#overview)
2. [Architecture Layers](#architecture-layers)
3. [Database Schema](#database-schema)
4. [Data Flow](#data-flow)
5. [Key Components](#key-components)
6. [Dependencies](#dependencies)
7. [Code Structure](#code-structure)

---

## Overview

### Architecture Pattern
**MVVM (Model-View-ViewModel) with Clean Architecture principles**

### Technology Stack
- **Language**: Kotlin 100%
- **UI**: Jetpack Compose with Material 3
- **Database**: Room (SQLite wrapper)
- **Preferences**: DataStore (Preferences)
- **Async**: Kotlin Coroutines + Flow
- **DI**: Manual Factory Pattern (ready for Hilt)
- **Networking**: Retrofit 2 + OkHttp + Moshi
- **Background Work**: WorkManager
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

---

## Architecture Layers

### 1. Presentation Layer (UI)
```
├── ui/
│   ├── screens/
│   │   ├── dashboard/      # Main dashboard
│   │   ├── stats/          # Statistics screen
│   │   ├── discover/       # Discovery feed
│   │   ├── profile/        # User profile & settings
│   │   ├── reading/        # Reading tracker (modal)
│   │   └── vocabulary/     # Vocabulary builder (modal)
│   ├── components/         # Reusable UI components
│   ├── navigation/         # Navigation setup
│   ├── theme/              # Material 3 theme
│   ├── AppViewModel.kt     # Main ViewModel
│   └── AppViewModelFactory.kt
```

**Key Features**:
- Jetpack Compose for declarative UI
- StateFlow for reactive state management
- Single source of truth (AppUiState)
- Material 3 components
- Dark/Light theme support

### 2. Domain Layer (Business Logic)
```
├── data/
│   ├── repository/
│   │   ├── StreakRepository.kt
│   │   ├── VocabularyRepository.kt
│   │   ├── BookRepository.kt
│   │   ├── ReflectionRepository.kt
│   │   ├── AchievementRepository.kt
│   │   └── QuoteRepository.kt
│   └── model/
│       ├── Streak.kt
│       ├── Quote.kt
│       └── HabitTemplate.kt
```

**Key Features**:
- Repository pattern for data abstraction
- Suspend functions for async operations
- Flow for reactive data streams
- Business logic encapsulation

### 3. Data Layer (Storage)
```
├── data/
│   ├── local/
│   │   ├── entity/          # Room entities
│   │   ├── dao/             # Data Access Objects
│   │   ├── AppDatabase.kt   # Room database
│   │   └── PreferencesManager.kt  # DataStore
│   ├── remote/
│   │   ├── QuoteApi.kt      # Retrofit interface
│   │   └── QuoteService.kt  # API implementation
│   └── backup/
│       └── BackupManager.kt # Export/Import
```

**Key Features**:
- Room for local database
- DataStore for preferences
- Type converters for complex types
- Singleton database instance
- Flow-based queries

### 4. Utilities & Helpers
```
├── notifications/
│   ├── NotificationHelper.kt
│   ├── StreakReminderScheduler.kt
│   └── StreakReminderWorker.kt
├── debug/
│   └── GlobalExceptionHandler.kt
└── data/
    └── gemini/
        └── GeminiClient.kt
```

---

## Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐
│  StreakEntity   │
├─────────────────┤
│ id (PK)         │
│ name            │
│ currentCount    │
│ longestCount    │
│ goalPerDay      │
│ unit            │
│ category        │
│ history         │
│ color           │
│ icon            │
│ isArchived      │
│ freezeDaysUsed  │
│ freezeDaysAvail │
│ createdAt       │
│ lastUpdated     │
└─────────────────┘

┌─────────────────┐
│   BookEntity    │
├─────────────────┤
│ id (PK)         │
│ title           │
│ author          │
│ totalPages      │
│ currentPage     │
│ coverImageUrl   │
│ startedAt       │
│ finishedAt      │
│ rating          │
│ notes           │
│ genre           │
│ isbn            │
│ isArchived      │
│ createdAt       │
└─────────────────┘
        ↓ 1:N
┌─────────────────┐
│ ReadingSession  │
├─────────────────┤
│ id (PK)         │
│ bookId (FK)     │
│ pagesRead       │
│ startPage       │
│ endPage         │
│ durationMinutes │
│ notes           │
│ date            │
└─────────────────┘

┌─────────────────┐
│ VocabularyWord  │
├─────────────────┤
│ id (PK)         │
│ word            │
│ definition      │
│ example         │
│ synonyms        │
│ partOfSpeech    │
│ pronunciation   │
│ masteryLevel    │
│ timesReviewed   │
│ lastReviewedAt  │
│ createdAt       │
│ tags            │
└─────────────────┘

┌─────────────────┐
│ DailyReflection │
├─────────────────┤
│ date (PK)       │
│ mood            │
│ notes           │
│ highlights      │
│ challenges      │
│ gratitude       │
│ tomorrowGoals   │
│ createdAt       │
│ lastUpdated     │
└─────────────────┘

┌─────────────────┐
│ AchievementEnt  │
├─────────────────┤
│ id (PK)         │
│ title           │
│ description     │
│ icon            │
│ category        │
│ requirement     │
│ progress        │
│ isUnlocked      │
│ unlockedAt      │
│ tier            │
│ points          │
└─────────────────┘

┌─────────────────┐
│   QuoteEntity   │
├─────────────────┤
│ id (PK)         │
│ text            │
│ author          │
│ isFavorite      │
│ createdAt       │
└─────────────────┘
```

### Indexes
```sql
-- Performance optimization
CREATE INDEX idx_streaks_category ON streaks(category)
CREATE INDEX idx_streaks_archived ON streaks(isArchived)
CREATE INDEX idx_books_archived ON books(isArchived)
CREATE INDEX idx_reading_sessions_book ON reading_sessions(bookId)
CREATE INDEX idx_vocabulary_mastery ON vocabulary_words(masteryLevel)
CREATE INDEX idx_reflections_date ON daily_reflections(date)
CREATE INDEX idx_achievements_unlocked ON achievements(isUnlocked)
```

---

## Data Flow

### Typical Data Flow (MVVM)
```
┌──────────┐
│   User   │
│ Interacts│
└────┬─────┘
     │
     ↓
┌────────────────┐
│  UI (Compose)  │  ← StateFlow.collectAsState()
└────┬───────────┘
     │ User Action
     ↓
┌────────────────┐
│   ViewModel    │  ← Holds StateFlow<UiState>
└────┬───────────┘
     │ Call suspend fun
     ↓
┌────────────────┐
│   Repository   │  ← Business logic
└────┬───────────┘
     │ Call DAO methods
     ↓
┌────────────────┐
│   DAO (Room)   │  ← Returns Flow<T>
└────┬───────────┘
     │ SQL Query
     ↓
┌────────────────┐
│    Database    │  ← SQLite file
└────────────────┘
```

### Example: Adding a Streak
```kotlin
// 1. User taps "Add Streak" button in UI
Button(onClick = { viewModel.showAddStreakDialog() })

// 2. ViewModel shows dialog
fun showAddStreakDialog() {
    _uiState.update { it.copy(showAddDialog = true) }
}

// 3. User fills form and submits
fun createStreak(name: String, goal: Int, unit: String, category: String) {
    viewModelScope.launch {
        // 4. Repository creates streak
        val id = streakRepository.createStreak(
            name = name,
            goalPerDay = goal,
            unit = unit,
            category = category
        )

        // 5. DAO inserts into database
        // streakDao.insertStreak(entity)

        // 6. Flow automatically updates UI
        // Repository's observeStreaks() emits new list
    }
}

// 7. UI recomposes with new data
val streaks by viewModel.streaks.collectAsState()
LazyColumn {
    items(streaks) { streak ->
        StreakCard(streak)
    }
}
```

---

## Key Components

### 1. AppViewModel
**Responsibility**: Central state management
```kotlin
class AppViewModel(
    private val application: Application,
    private val quoteRepository: QuoteRepository,
    private val streakRepository: StreakRepository,
    private val reminderScheduler: StreakReminderScheduler
) : AndroidViewModel(application) {

    // State
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    // Data streams
    val streaks = streakRepository.observeStreaks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Actions
    fun logProgress(streakId: String, value: Int) {
        viewModelScope.launch {
            streakRepository.logProgress(streakId, value)
        }
    }
}
```

### 2. AppDatabase
**Responsibility**: Database configuration
```kotlin
@Database(
    entities = [
        StreakEntity::class,
        QuoteEntity::class,
        VocabularyWordEntity::class,
        BookEntity::class,
        ReadingSessionEntity::class,
        DailyReflectionEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun streakDao(): StreakDao
    abstract fun quoteDao(): QuoteDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun bookDao(): BookDao
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun dailyReflectionDao(): DailyReflectionDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "productivity_streak_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
```

### 3. Repository Pattern
**Responsibility**: Abstract data sources
```kotlin
class StreakRepository(private val streakDao: StreakDao) {

    // Observe (reactive)
    fun observeStreaks(): Flow<List<Streak>> =
        streakDao.getAllStreaks().map { entities ->
            entities.map { it.toStreak() }
        }

    // Single query (one-shot)
    suspend fun getStreakById(id: String): Streak? =
        streakDao.getStreakById(id)?.toStreak()

    // Modify
    suspend fun logProgress(streakId: String, value: Int) {
        val streak = streakDao.getStreakById(streakId) ?: return
        // Business logic here
        streakDao.updateStreak(updatedStreak)
    }
}
```

### 4. PreferencesManager
**Responsibility**: User settings persistence
```kotlin
class PreferencesManager(context: Context) {
    private val dataStore = context.dataStore

    val themeMode: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_MODE] ?: "system"
        }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }
}
```

### 5. NotificationHelper
**Responsibility**: System notifications
```kotlin
class NotificationHelper(private val context: Context) {
    fun showDailyReminder(userName: String, activeStreakCount: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Time to maintain your streaks!")
            .setContentText("You have $activeStreakCount active streaks")
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
```

---

## Dependencies

### Gradle Dependencies (app/build.gradle.kts)
```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Biometric
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // AI (Optional)
    implementation("com.google.ai.client.generativeai:generativeai:0.8.0")
}
```

### Plugins
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}
```

---

## Code Structure

### Project Structure
```
app/src/main/
├── AndroidManifest.xml
├── java/com/productivitystreak/
│   ├── MainActivity.kt
│   ├── NeverZeroApplication.kt
│   │
│   ├── data/
│   │   ├── backup/
│   │   │   └── BackupManager.kt
│   │   ├── gemini/
│   │   │   └── GeminiClient.kt
│   │   ├── local/
│   │   │   ├── dao/
│   │   │   │   ├── AchievementDao.kt
│   │   │   │   ├── BookDao.kt
│   │   │   │   ├── DailyReflectionDao.kt
│   │   │   │   ├── QuoteDao.kt
│   │   │   │   ├── ReadingSessionDao.kt
│   │   │   │   ├── StreakDao.kt
│   │   │   │   └── VocabularyDao.kt
│   │   │   ├── entity/
│   │   │   │   ├── AchievementEntity.kt
│   │   │   │   ├── BookEntity.kt
│   │   │   │   ├── Converters.kt
│   │   │   │   ├── DailyReflectionEntity.kt
│   │   │   │   ├── QuoteEntity.kt
│   │   │   │   ├── ReadingSessionEntity.kt
│   │   │   │   ├── StreakEntity.kt
│   │   │   │   └── VocabularyWordEntity.kt
│   │   │   ├── AppDatabase.kt
│   │   │   └── PreferencesManager.kt
│   │   ├── model/
│   │   │   ├── HabitTemplate.kt
│   │   │   ├── Quote.kt
│   │   │   └── Streak.kt
│   │   ├── remote/
│   │   │   ├── model/
│   │   │   │   └── QuoteResponse.kt
│   │   │   ├── QuoteApi.kt
│   │   │   └── QuoteService.kt
│   │   ├── repository/
│   │   │   ├── AchievementRepository.kt
│   │   │   ├── BookRepository.kt
│   │   │   ├── ReflectionRepository.kt
│   │   │   ├── StreakRepository.kt
│   │   │   └── VocabularyRepository.kt
│   │   └── QuoteRepository.kt
│   │
│   ├── debug/
│   │   └── GlobalExceptionHandler.kt
│   │
│   ├── notifications/
│   │   ├── NotificationHelper.kt
│   │   ├── StreakReminderScheduler.kt
│   │   └── StreakReminderWorker.kt
│   │
│   └── ui/
│       ├── components/
│       │   ├── Buttons.kt
│       │   └── Cards.kt
│       ├── navigation/
│       │   └── NeverZeroApp.kt
│       ├── screens/
│       │   ├── dashboard/
│       │   │   └── DashboardScreen.kt
│       │   ├── discover/
│       │   │   └── DiscoverScreen.kt
│       │   ├── profile/
│       │   │   └── ProfileScreen.kt
│       │   ├── reading/
│       │   │   └── ReadingTrackerScreen.kt
│       │   ├── stats/
│       │   │   └── StatsScreen.kt
│       │   └── vocabulary/
│       │       └── VocabularyScreen.kt
│       ├── theme/
│       │   ├── Color.kt
│       │   ├── DesignTokens.kt
│       │   ├── Shape.kt
│       │   └── Theme.kt
│       ├── AppUiState.kt
│       ├── AppViewModel.kt
│       └── AppViewModelFactory.kt
│
└── res/
    ├── drawable/
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml
    └── xml/
        ├── backup_rules.xml
        └── data_extraction_rules.xml
```

### File Count Summary
- **Total Kotlin files**: ~40
- **Data layer**: 28 files
- **UI layer**: 12 files
- **Total lines of code**: ~6,000+

---

## Architectural Decisions

### Why MVVM?
- **Separation of concerns**: UI, business logic, data clearly separated
- **Testability**: ViewModels can be unit tested without Android dependencies
- **Lifecycle awareness**: ViewModels survive configuration changes
- **Reactive**: Flow/StateFlow enable reactive UI updates

### Why Room over raw SQLite?
- **Compile-time SQL verification**: Catch errors at compile time
- **Type-safe queries**: No string-based queries
- **Coroutines support**: Suspend functions out of the box
- **Flow support**: Reactive queries automatically
- **Less boilerplate**: Auto-generated implementations

### Why DataStore over SharedPreferences?
- **Type-safe**: Preferences are strongly typed
- **Async-first**: Built on Coroutines and Flow
- **Data consistency**: Transactional updates
- **Modern API**: Replaces deprecated SharedPreferences

### Why Manual DI over Dagger/Hilt?
- **Simplicity**: Small project doesn't need complex DI
- **Learning curve**: Easier for new developers
- **Migration ready**: Can easily migrate to Hilt later
- **Explicit**: Dependencies are clearly visible

### Why Compose over XML?
- **Declarative**: UI as a function of state
- **Less boilerplate**: No findViewById, view binding
- **Interoperability**: Works with existing Android Views
- **Modern**: Google's recommended UI toolkit
- **Performance**: Efficient recomposition

---

## Performance Considerations

### Database
- **Indices** on frequently queried columns
- **Pagination** for large datasets (ReadingSessions)
- **Transactions** for bulk operations
- **Background thread** for all database operations (enforced by Room)

### UI
- **Lazy loading** with LazyColumn/LazyRow
- **State hoisting** to minimize recompositions
- **Remember** for expensive computations
- **Keys** in lazy lists for efficient updates

### Memory
- **Flow** for reactive data (no memory leaks)
- **ViewModel scope** for coroutines (auto-cancellation)
- **Singleton database** (one instance)
- **Image loading** (not yet implemented, but use Coil when needed)

---

## Security Considerations

### Data Protection
- **Local only**: No cloud, no network except quotes API
- **Encrypted preferences**: Can enable with EncryptedSharedPreferences
- **Database encryption**: Can use SQLCipher if needed
- **Biometric auth**: Optional app lock

### Permissions
- **Minimal permissions**: Only INTERNET, VIBRATE, POST_NOTIFICATIONS, USE_BIOMETRIC
- **Runtime permissions**: POST_NOTIFICATIONS requires Android 13+ runtime request
- **No location**: Privacy-first approach
- **No contacts**: No social features requiring contacts

---

## Testing Strategy

### Unit Tests (Repositories & ViewModels)
```kotlin
@Test
fun `logProgress increases streak count when goal met`() = runTest {
    // Given
    val repository = StreakRepository(fakeDao)

    // When
    repository.logProgress("test-id", 30)

    // Then
    val streak = repository.getStreakById("test-id")
    assertEquals(1, streak.currentCount)
}
```

### Integration Tests (Database)
```kotlin
@Test
fun `inserting and retrieving streak works`() = runTest {
    // Given
    val streak = StreakEntity(...)
    database.streakDao().insertStreak(streak)

    // When
    val retrieved = database.streakDao().getStreakById(streak.id)

    // Then
    assertEquals(streak, retrieved)
}
```

### UI Tests (Compose)
```kotlin
@Test
fun `clicking add button shows dialog`() {
    composeTestRule.setContent {
        DashboardScreen(viewModel)
    }

    composeTestRule
        .onNodeWithText("Add Streak")
        .performClick()

    composeTestRule
        .onNodeWithText("Create New Streak")
        .assertIsDisplayed()
}
```

---

## Migration Strategy (If Database Schema Changes)

### Example: Adding new column to StreakEntity
```kotlin
@Database(version = 2)  // Increment version
abstract class AppDatabase : RoomDatabase() {

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE streaks ADD COLUMN lastAccessedAt INTEGER DEFAULT 0 NOT NULL"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(...)
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}
```

---

## Conclusion

This architecture provides:
- ✅ **Scalability**: Easy to add new features
- ✅ **Maintainability**: Clean separation of concerns
- ✅ **Testability**: All layers can be tested independently
- ✅ **Performance**: Optimized database queries and UI rendering
- ✅ **Modern**: Uses latest Android best practices
- ✅ **Offline-first**: Works completely offline
- ✅ **Privacy-focused**: All data stays local

**Ready for production** with local storage! 🚀
