# ViewModel Refactoring Guide
**Never Zero Android App - Code Consolidation**

## Overview

This guide provides step-by-step instructions for refactoring ViewModels to use the new shared use cases, eliminating ~300-400 lines of duplicated code.

## New Use Cases Created

### 1. RpgStatsUseCase
**Location:** `app/src/main/java/com/productivitystreak/domain/usecase/RpgStatsUseCase.kt`

**Purpose:** Centralized RPG stats calculation logic

**Methods:**
- `computeRpgStats(streaks: List<Streak>): RpgStats`
- `mapCategoryToAttribute(category: String): HabitAttribute`

**Usage Example:**
```kotlin
// Before (in ViewModel)
private fun computeRpgStatsFromStreaks(streaks: List<Streak>): RpgStats {
    // 80+ lines of duplicated code...
}

// After (in ViewModel)
val rpgStats = rpgStatsUseCase.computeRpgStats(streaks)
```

### 2. GeminiAIUseCase
**Location:** `app/src/main/java/com/productivitystreak/domain/usecase/GeminiAIUseCase.kt`

**Purpose:** Standardized AI error handling and operations

**Methods:**
- `generateWithErrorHandling(operation: suspend () -> String): Result<String>`
- `handleAIError(error: Throwable): String`
- `generateBuddhaInsight(forceRefresh: Boolean): String`
- `generateJournalFeedback(text: String): String`
- `generateHabitSuggestions(categories: String): List<String>`

**Usage Example:**
```kotlin
// Before (in ViewModel)
try {
    val response = geminiClient.generateJournalFeedback(text)
    _buddhaResponse.value = response
} catch (e: Exception) {
    Log.e("ViewModel", "Error", e)
    val errorMessage = when {
        e.message?.contains("API key") == true -> "Invalid API key"
        // ... more error handling
    }
    _uiMessage.value = errorMessage
}

// After (in ViewModel)
val feedback = geminiAIUseCase.generateJournalFeedback(text)
_buddhaResponse.value = feedback
```

### 3. JsonSerializationUseCase
**Location:** `app/src/main/java/com/productivitystreak/domain/usecase/JsonSerializationUseCase.kt`

**Purpose:** Simplified JSON serialization with Moshi

**Methods:**
- `serializeList<T>(list: List<T>): String`
- `deserializeList<T>(json: String): List<T>?`
- `serialize<T>(item: T): String`
- `deserialize<T>(json: String): T?`

**Usage Example:**
```kotlin
// Before (in ViewModel)
private val taskListAdapter = moshi.adapter<List<Task>>(
    Types.newParameterizedType(List::class.java, Task::class.java)
)
val json = taskListAdapter.toJson(tasks)

// After (in ViewModel)
val json = jsonSerializationUseCase.serializeList(tasks)
```

---

## Refactoring Steps by ViewModel

### Priority 1: StreakViewModel (Highest Impact)

**File:** `app/src/main/java/com/productivitystreak/ui/screens/stats/StreakViewModel.kt`

**Changes Required:**

1. **Add use case dependencies to constructor:**
```kotlin
class StreakViewModel(
    private val streakRepository: StreakRepository,
    private val preferencesManager: PreferencesManager,
    private val moshi: Moshi,
    private val geminiClient: GeminiClient,
    private val socialRepository: SocialRepository,
    private val aiCoach: AICoach,
    // ADD THESE:
    private val rpgStatsUseCase: RpgStatsUseCase,
    private val geminiAIUseCase: GeminiAIUseCase,
    private val jsonSerializationUseCase: JsonSerializationUseCase
) : ViewModel() {
```

2. **Replace `computeRpgStatsFromStreaks()` calls:**
```kotlin
// FIND (line ~200):
val rpg = computeRpgStatsFromStreaks(streaks)

// REPLACE WITH:
val rpg = rpgStatsUseCase.computeRpgStats(streaks)
```

3. **Delete the entire `computeRpgStatsFromStreaks()` function (~80 lines)**

4. **Delete the `mapCategoryToAttribute()` function (~15 lines)**

5. **Replace JSON serialization:**
```kotlin
// FIND:
private val taskListAdapter = moshi.adapter<List<Task>>(
    Types.newParameterizedType(List::class.java, Task::class.java)
)

// DELETE this field

// FIND all usages like:
val json = taskListAdapter.toJson(tasks)
val tasks = taskListAdapter.fromJson(json) ?: emptyList()

// REPLACE WITH:
val json = jsonSerializationUseCase.serializeList(tasks)
val tasks = jsonSerializationUseCase.deserializeList<Task>(json) ?: emptyList()
```

6. **Replace Gemini AI calls:**
```kotlin
// FIND:
val insight = geminiClient.generateBuddhaInsight(forceRefresh = forceRefresh)

// REPLACE WITH:
val insight = geminiAIUseCase.generateBuddhaInsight(forceRefresh)
```

**Lines Saved:** ~120 lines

---

### Priority 2: ProfileViewModel

**File:** `app/src/main/java/com/productivitystreak/ui/screens/profile/ProfileViewModel.kt`

**Changes Required:**

1. **Add use case to constructor:**
```kotlin
class ProfileViewModel(
    application: Application,
    private val preferencesManager: PreferencesManager,
    private val streakRepository: StreakRepository,
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val reminderScheduler: StreakReminderScheduler,
    // ADD THIS:
    private val rpgStatsUseCase: RpgStatsUseCase
) : AndroidViewModel(application) {
```

2. **Replace RPG stats calculation:**
```kotlin
// FIND (in observeRpgStats() function):
val stats = computeRpgStatsFromStreaks(streaks)

// REPLACE WITH:
val stats = rpgStatsUseCase.computeRpgStats(streaks)
```

3. **Delete `computeRpgStatsFromStreaks()` function (~80 lines)**

4. **Delete `mapCategoryToAttribute()` function (~15 lines)**

**Lines Saved:** ~95 lines

---

### Priority 3: VocabularyViewModel

**File:** `app/src/main/java/com/productivitystreak/ui/screens/vocabulary/VocabularyViewModel.kt`

**Changes Required:**

1. **Add use cases to constructor:**
```kotlin
class VocabularyViewModel(
    private val preferencesManager: PreferencesManager,
    private val moshi: Moshi,
    private val geminiClient: GeminiClient,
    private val geminiRepository: GeminiRepository,
    // ADD THESE:
    private val geminiAIUseCase: GeminiAIUseCase,
    private val jsonSerializationUseCase: JsonSerializationUseCase
) : ViewModel() {
```

2. **Replace JSON serialization for vocabulary words:**
```kotlin
// FIND (in loadVocabularyData()):
val type = Types.newParameterizedType(List::class.java, VocabularyWord::class.java)
val adapter = moshi.adapter<List<VocabularyWord>>(type)
val words = adapter.fromJson(wordsJson) ?: emptyList()

// REPLACE WITH:
val words = jsonSerializationUseCase.deserializeList<VocabularyWord>(wordsJson) ?: emptyList()

// FIND (in onAddVocabularyWord()):
val adapter = moshi.adapter<List<VocabularyWord>>(type)
val wordsJson = adapter.toJson(updatedWords)

// REPLACE WITH:
val wordsJson = jsonSerializationUseCase.serializeList(updatedWords)
```

3. **Replace Gemini AI calls:**
```kotlin
// FIND (in suggestNewWord()):
val result = runCatching { geminiClient.generateWordOfTheDay(topic) }

// REPLACE WITH:
val word = geminiAIUseCase.generateWithErrorHandling {
    geminiClient.generateWordOfTheDay(topic)
}.getOrNull()

// FIND (in onGenerateTeachingLesson()):
val result = runCatching { geminiClient.generateTeachingLesson(word, context) }

// REPLACE WITH:
val lesson = geminiAIUseCase.generateWithErrorHandling {
    geminiClient.generateTeachingLesson(word, context)
}.getOrNull()
```

**Lines Saved:** ~40 lines

---

### Priority 4: ReadingViewModel

**File:** `app/src/main/java/com/productivitystreak/ui/screens/reading/ReadingViewModel.kt`

**Changes Required:**

1. **Add use case to constructor:**
```kotlin
class ReadingViewModel(
    private val preferencesManager: PreferencesManager,
    private val moshi: Moshi,
    // ADD THIS:
    private val jsonSerializationUseCase: JsonSerializationUseCase
) : ViewModel() {
```

2. **Replace JSON serialization:**
```kotlin
// FIND (in loadReadingTrackerData()):
val type = Types.newParameterizedType(List::class.java, ReadingLog::class.java)
val adapter = moshi.adapter<List<ReadingLog>>(type)
val activity = adapter.fromJson(activityJson) ?: emptyList()

// REPLACE WITH:
val activity = jsonSerializationUseCase.deserializeList<ReadingLog>(activityJson) ?: emptyList()

// FIND (in onLogReadingProgress()):
val adapter = moshi.adapter<List<ReadingLog>>(type)
val activityJson = adapter.toJson(updatedActivity)

// REPLACE WITH:
val activityJson = jsonSerializationUseCase.serializeList(updatedActivity)
```

**Lines Saved:** ~20 lines

---

### Priority 5: JournalViewModel

**File:** `app/src/main/java/com/productivitystreak/ui/screens/journal/JournalViewModel.kt`

**Changes Required:**

1. **Add use case to constructor:**
```kotlin
class JournalViewModel(
    private val reflectionRepository: ReflectionRepository,
    private val journalRepository: JournalRepository,
    private val geminiClient: GeminiClient,
    // ADD THIS:
    private val geminiAIUseCase: GeminiAIUseCase
) : ViewModel() {
```

2. **Replace Gemini AI calls:**
```kotlin
// FIND (in onSubmitJournalEntry()):
val response = geminiClient.generateJournalFeedback(trimmedNotes)
_buddhaResponse.value = response

// REPLACE WITH:
val response = geminiAIUseCase.generateJournalFeedback(trimmedNotes)
_buddhaResponse.value = response

// FIND (in onJournalTextChanged()):
val feedback = geminiClient.generateJournalFeedback(text.trim())
_realtimeFeedback.value = feedback

// REPLACE WITH:
val feedback = geminiAIUseCase.generateJournalFeedback(text.trim())
_realtimeFeedback.value = feedback
```

**Lines Saved:** ~15 lines

---

### Priority 6: OnboardingViewModel

**File:** `app/src/main/java/com/productivitystreak/ui/screens/onboarding/OnboardingViewModel.kt`

**Changes Required:**

1. **Add use case to constructor:**
```kotlin
class OnboardingViewModel(
    private val preferencesManager: PreferencesManager,
    private val streakRepository: StreakRepository,
    private val reminderScheduler: StreakReminderScheduler,
    private val geminiClient: GeminiClient,
    // ADD THIS:
    private val geminiAIUseCase: GeminiAIUseCase
) : ViewModel() {
```

2. **Replace Gemini AI calls:**
```kotlin
// FIND (in generateHabitSuggestions()):
val suggestions = geminiClient.generateHabitSuggestions(categories)
_uiState.update { it.copy(isGeneratingSuggestions = false, habitSuggestions = suggestions) }

// REPLACE WITH:
val suggestions = geminiAIUseCase.generateHabitSuggestions(categories)
_uiState.update { it.copy(isGeneratingSuggestions = false, habitSuggestions = suggestions) }
```

**Lines Saved:** ~10 lines

---

### Priority 7: BuddhaChatViewModel

**File:** `app/src/main/java/com/productivitystreak/ui/screens/ai/BuddhaChatViewModel.kt`

**Changes Required:**

1. **Add use case to constructor:**
```kotlin
class BuddhaChatViewModel(
    private val repository: BuddhaRepository,
    // ADD THIS:
    private val geminiAIUseCase: GeminiAIUseCase
) : ViewModel() {
```

2. **Replace error handling in sendMessage():**
```kotlin
// FIND (in catch block):
val errorMessage = when {
    e.message?.contains("API key", ignoreCase = true) == true -> "Invalid API key"
    e.message?.contains("quota", ignoreCase = true) == true -> "API quota exceeded"
    e.message?.contains("network", ignoreCase = true) == true -> "Network error"
    e.message?.contains("timeout", ignoreCase = true) == true -> "Request timeout"
    else -> "Connection weak, meditating..."
}

// REPLACE WITH:
val errorMessage = geminiAIUseCase.handleAIError(e)
```

**Lines Saved:** ~8 lines

---

## Updating ViewModelFactory Classes

After refactoring ViewModels, update their factory classes to pass use cases:

### Example: StreakViewModel Factory

```kotlin
// In the file where StreakViewModel is instantiated
val app = LocalContext.current.applicationContext as NeverZeroApplication

val streakViewModel: StreakViewModel = viewModel(
    factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return StreakViewModel(
                streakRepository = app.streakRepository,
                preferencesManager = app.preferencesManager,
                moshi = /* moshi instance */,
                geminiClient = app.geminiClient,
                socialRepository = app.socialRepository,
                aiCoach = app.aiCoach,
                // ADD THESE:
                rpgStatsUseCase = app.rpgStatsUseCase,
                geminiAIUseCase = app.geminiAIUseCase,
                jsonSerializationUseCase = app.jsonSerializationUseCase
            ) as T
        }
    }
)
```

---

## Testing Strategy

### 1. Unit Tests for Use Cases

Create test files in `app/src/test/java/com/productivitystreak/domain/usecase/`:

**RpgStatsUseCaseTest.kt:**
```kotlin
class RpgStatsUseCaseTest {
    private lateinit var useCase: RpgStatsUseCase

    @Before
    fun setup() {
        useCase = RpgStatsUseCase()
    }

    @Test
    fun `computeRpgStats returns default stats for empty list`() {
        val result = useCase.computeRpgStats(emptyList())
        assertEquals(RpgStats(), result)
    }

    @Test
    fun `mapCategoryToAttribute maps fitness to strength`() {
        val result = useCase.mapCategoryToAttribute("Fitness")
        assertEquals(HabitAttribute.STRENGTH, result)
    }

    // Add more tests...
}
```

### 2. Integration Tests

After refactoring each ViewModel:
1. Run existing ViewModel tests
2. Verify UI screens still function correctly
3. Test error scenarios with AI calls

### 3. Manual Testing Checklist

- [ ] Stats screen displays RPG stats correctly
- [ ] Profile screen shows correct RPG stats
- [ ] Journal AI feedback works
- [ ] Onboarding habit suggestions work
- [ ] Vocabulary word suggestions work
- [ ] Buddha chat error handling works
- [ ] Reading activity serialization works
- [ ] Vocabulary words serialization works

---

## Migration Checklist

### Phase 1: Setup (Completed ✅)
- [x] Create `RpgStatsUseCase.kt`
- [x] Create `GeminiAIUseCase.kt`
- [x] Create `JsonSerializationUseCase.kt`
- [x] Update `NeverZeroApplication.kt` to initialize use cases
- [x] Remove unused string resources
- [x] Fix AndroidManifest.xml issues
- [x] Delete stale files

### Phase 2: Refactor ViewModels (To Do)
- [ ] Refactor `StreakViewModel` (Priority 1)
- [ ] Refactor `ProfileViewModel` (Priority 2)
- [ ] Refactor `VocabularyViewModel` (Priority 3)
- [ ] Refactor `ReadingViewModel` (Priority 4)
- [ ] Refactor `JournalViewModel` (Priority 5)
- [ ] Refactor `OnboardingViewModel` (Priority 6)
- [ ] Refactor `BuddhaChatViewModel` (Priority 7)

### Phase 3: Testing (To Do)
- [ ] Write unit tests for `RpgStatsUseCase`
- [ ] Write unit tests for `GeminiAIUseCase`
- [ ] Write unit tests for `JsonSerializationUseCase`
- [ ] Run all existing ViewModel tests
- [ ] Perform manual UI testing
- [ ] Test error scenarios

### Phase 4: Cleanup (To Do)
- [ ] Remove unused imports from ViewModels
- [ ] Update documentation
- [ ] Code review
- [ ] Merge to main branch

---

## Expected Benefits

### Code Metrics
- **Lines of code removed:** ~300-400 lines
- **Duplication eliminated:** 95%
- **Test coverage increase:** +15-20%

### Maintainability
- Single source of truth for RPG stats calculation
- Consistent AI error handling across all features
- Simplified JSON serialization
- Easier to add new features using shared logic

### Performance
- No performance impact (same logic, better organization)
- Slightly faster builds due to fewer lines to compile

---

## Rollback Plan

If issues arise during refactoring:

1. **Per-ViewModel rollback:** Git revert individual ViewModel changes
2. **Full rollback:** Revert to commit before use case creation
3. **Partial adoption:** Keep use cases but don't delete old code initially (mark as deprecated)

---

## Questions & Support

For questions about this refactoring:
1. Review the `CODE_AUDIT_REPORT.md` for context
2. Check use case implementations in `domain/usecase/` package
3. Refer to this guide for step-by-step instructions

---

**Last Updated:** December 6, 2025  
**Status:** Phase 1 Complete, Phase 2 Ready to Start
