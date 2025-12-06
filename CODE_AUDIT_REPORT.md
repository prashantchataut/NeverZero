# Code Audit Report - Never Zero Android App
**Date:** December 6, 2025  
**Auditor:** Lead Developer

## Executive Summary

This audit analyzed the Never Zero Android productivity app for:
1. Unused resources (images, layouts, strings)
2. ViewModel consolidation opportunities
3. AndroidManifest.xml optimization
4. Stale documentation and build files

### Key Findings
- **28 unused string resources** identified by Android Lint
- **9 ViewModels** with significant code duplication
- **Manifest issues**: Redundant label, missing queries declaration
- **Multiple stale files**: Build logs, backup scripts, improvement docs

---

## 1. Android Lint - Unused Resources Analysis

### How to Use Android Lint for Unused Resources

```bash
# Run lint analysis
./gradlew lint --no-daemon

# View HTML report
app/build/reports/lint-results-debug.html

# View XML report (programmatic access)
app/build/reports/lint-results-debug.xml
```

### Unused String Resources (28 Total)

All unused resources are in `app/src/main/res/values/strings.xml`:

| Line | Resource Name | Value |
|------|---------------|-------|
| 11 | `action_view_all` | "View all" |
| 12 | `action_change` | "Change" |
| 13 | `dashboard_section_quick_actions` | "Quick actions" |
| 14 | `dashboard_section_inspiration` | "Daily inspiration" |
| 15 | `dashboard_button_log_streak` | "+  Log streak" |
| 16 | `dashboard_label_all_streaks_logged` | "All streaks logged" |
| 17 | `dashboard_action_get_motivated` | "Get motivated" |
| 18 | `discover_section_title` | "Discover" |
| 19 | `discover_section_community` | "Community" |
| 20 | `stats_section_title` | "Your stats" |
| 21 | `stats_section_leaderboard` | "Leaderboard" |
| 28 | `profile_section_preferences` | "Preferences" |
| 29 | `profile_action_logout` | "Logout" |
| 32 | `onboarding_primary_cta` | "Continue" |
| 33 | `onboarding_primary_cta_last` | "Confirm" |
| 34 | `onboarding_secondary_cta` | "Skip" |
| 35 | `onboarding_back` | "Back" |
| 36 | `onboarding_step_progress` | "Step %1$d of %2$d" |
| 37 | `onboarding_welcome_subtitle` | "Build habits that last..." |
| 38 | `onboarding_skip_for_now` | "Skip for now" |
| 39 | `time_picker_label_hour` | "Hour" |
| 40 | `time_picker_label_minute` | "Minute" |
| 41 | `time_picker_label_period` | "Period" |
| 44 | `cd_profile_avatar` | "Open profile" |
| 45 | `cd_refresh_quote` | "Refresh inspiration quote" |
| 46 | `cd_navigate_discover` | "Go to discover" |
| 47 | `cd_close_dialog` | "Close dialog" |
| 48 | `cd_leaderboard_badge` | (truncated) |

**Recommendation:** Remove all 28 unused string resources to reduce APK size and improve build performance.

### Empty Localization Folders

The app has **93 empty localization folders** (values-af, values-am, values-ar, etc.) that serve no purpose.

**Recommendation:** Delete all empty localization folders.

---

## 2. ViewModel Consolidation Analysis

### Current ViewModels (9 Total)

| ViewModel | Location | Lines | Dependencies |
|-----------|----------|-------|--------------|
| `BuddhaChatViewModel` | `ui/screens/ai/` | ~130 | BuddhaRepository, GeminiClient |
| `DiscoverViewModel` | `ui/screens/discover/` | ~50 | AssetRepository, PreferencesManager |
| `JournalViewModel` | `ui/screens/journal/` | ~180 | ReflectionRepository, JournalRepository, GeminiClient |
| `LeaderboardViewModel` | `ui/screens/leaderboard/` | ~50 | None (mock data) |
| `OnboardingViewModel` | `ui/screens/onboarding/` | ~200 | PreferencesManager, StreakRepository, GeminiClient |
| `ProfileViewModel` | `ui/screens/profile/` | ~350 | PreferencesManager, StreakRepository, TimeCapsuleRepository |
| `ReadingViewModel` | `ui/screens/reading/` | ~120 | PreferencesManager, Moshi |
| `StreakViewModel` | `ui/screens/stats/` | ~450 | StreakRepository, PreferencesManager, GeminiClient, SocialRepository, AICoach |
| `VocabularyViewModel` | `ui/screens/vocabulary/` | ~200 | PreferencesManager, Moshi, GeminiClient, GeminiRepository |

### Identified Code Duplication

#### 1. **RPG Stats Calculation** (Duplicated in ProfileViewModel & StreakViewModel)
- **Lines:** ~80 lines of identical logic
- **Function:** `computeRpgStatsFromStreaks()`
- **Logic:** Maps streak categories to attributes, calculates XP, converts to stats

#### 2. **Category to Attribute Mapping** (Duplicated in ProfileViewModel & StreakViewModel)
- **Lines:** ~15 lines
- **Function:** `mapCategoryToAttribute()`
- **Logic:** Maps habit categories to RPG attributes (Strength, Intelligence, etc.)

#### 3. **Preferences Management** (Used in 7 ViewModels)
- **Pattern:** Direct PreferencesManager calls scattered across ViewModels
- **Duplication:** Similar patterns for loading/saving user preferences

#### 4. **Gemini AI Integration** (Used in 5 ViewModels)
- **ViewModels:** BuddhaChatViewModel, JournalViewModel, OnboardingViewModel, StreakViewModel, VocabularyViewModel
- **Pattern:** Similar error handling, loading states, API calls

#### 5. **Moshi JSON Serialization** (Used in 3 ViewModels)
- **ViewModels:** ReadingViewModel, StreakViewModel, VocabularyViewModel
- **Pattern:** Identical adapter creation and JSON parsing logic

#### 6. **UI State Management** (All ViewModels)
- **Pattern:** MutableStateFlow → StateFlow pattern repeated
- **Duplication:** Similar loading, error, success state handling

### Proposed Refactoring: Shared Use Cases

#### Create `domain/usecase/` Package

```
app/src/main/java/com/productivitystreak/domain/usecase/
├── RpgStatsUseCase.kt          # RPG stats calculation
├── PreferencesUseCase.kt       # Centralized preferences logic
├── GeminiAIUseCase.kt          # AI integration wrapper
├── JsonSerializationUseCase.kt # Moshi serialization helper
└── StateManagementUseCase.kt   # Common state patterns
```

#### 1. **RpgStatsUseCase.kt**
```kotlin
class RpgStatsUseCase {
    fun computeRpgStats(streaks: List<Streak>): RpgStats
    fun mapCategoryToAttribute(category: String): HabitAttribute
    private fun xpToStat(xp: Int): Int
}
```

**Impact:** Eliminates ~95 lines of duplication between ProfileViewModel and StreakViewModel

#### 2. **GeminiAIUseCase.kt**
```kotlin
class GeminiAIUseCase(private val geminiClient: GeminiClient) {
    suspend fun generateWithErrorHandling(
        operation: suspend () -> String
    ): Result<String>
    
    fun handleAIError(error: Throwable): String
}
```

**Impact:** Standardizes AI error handling across 5 ViewModels

#### 3. **PreferencesUseCase.kt**
```kotlin
class PreferencesUseCase(private val preferencesManager: PreferencesManager) {
    suspend fun getUserProfile(): UserProfile
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun getAppSettings(): AppSettings
}
```

**Impact:** Reduces direct PreferencesManager coupling in 7 ViewModels

#### 4. **JsonSerializationUseCase.kt**
```kotlin
class JsonSerializationUseCase(private val moshi: Moshi) {
    inline fun <reified T> serializeList(list: List<T>): String
    inline fun <reified T> deserializeList(json: String): List<T>?
}
```

**Impact:** Eliminates repeated Moshi adapter creation in 3 ViewModels

### Consolidation Benefits

- **Code Reduction:** ~300-400 lines of duplicated code eliminated
- **Maintainability:** Single source of truth for shared logic
- **Testability:** Use cases can be unit tested independently
- **Consistency:** Standardized error handling and state management
- **SOLID Principles:** Better separation of concerns

---

## 3. AndroidManifest.xml Analysis

### Current Manifest Issues

#### Issue 1: Redundant Label (Line 23)
```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:label="@string/app_name"  <!-- REDUNDANT -->
    android:theme="@style/Theme.ProductivityStreak">
```

**Problem:** Activity label duplicates application label  
**Fix:** Remove `android:label` attribute from MainActivity

#### Issue 2: Missing Queries Declaration (PermissionManager.kt:42)
```kotlin
if (intent.resolveActivity(activity.packageManager) != null) {
```

**Problem:** Android 11+ requires `<queries>` declaration for package visibility  
**Fix:** Add queries declaration to manifest

#### Issue 3: DebugActivity Entry Point
```xml
<activity
    android:name=".ui.debug.DebugActivity"
    android:exported="false" />
```

**Status:** ✅ Correct - Not exported, only for internal debugging

### Permissions Audit

| Permission | Usage | Necessary? |
|------------|-------|------------|
| `INTERNET` | Gemini AI API calls | ✅ Yes |
| `VIBRATE` | Haptic feedback | ✅ Yes |
| `POST_NOTIFICATIONS` | Streak reminders | ✅ Yes |
| `SCHEDULE_EXACT_ALARM` | Precise notification timing | ✅ Yes |
| `USE_BIOMETRIC` | Profile security | ✅ Yes |

**Verdict:** All permissions are necessary and properly justified.

### Single Activity Architecture

✅ **Confirmed:** MainActivity is the only exported activity (Single Activity Architecture with Compose Navigation)

---

## 4. Stale Files to Delete

### Build Logs and Outputs (12 files)
```
bento_build.txt
bento_build2.txt
build_errors.txt
build_log.txt
build_log_final.txt
build_output.txt
clean_log.txt
clean_log_250.txt
clean_log_buildscript.txt
clean_log_legacy.txt
final_bento_build.txt
final_build.txt
final_build2.txt
test_log.txt
test_output.txt
kotlin_errors.txt
```

**Reason:** Temporary build artifacts, not version controlled

### Backup Scripts (2 files)
```
backup_overrides.ps1
cache_scan.ps1
```

**Reason:** Ad-hoc scripts, should be in `scripts/` folder if needed

### Documentation Files (2 files)
```
IMPROVEMENT_PLAN.md
UI_UX_OVERHAUL.md
```

**Reason:** Outdated planning docs, superseded by `.kiro/specs/ux-overhaul/`

### Total Files to Delete: 19

---

## 5. Additional Lint Warnings

### Obsolete Dependencies (20 warnings)
Major updates available:
- `androidx.core:core-ktx`: 1.13.1 → 1.17.0
- `androidx.lifecycle:*`: 2.8.6 → 2.10.0
- `androidx.compose.material3:material3`: 1.2.1 → 1.4.0
- `androidx.room:*`: 2.6.1 → 2.8.4
- `androidx.navigation:navigation-compose`: 2.8.0 → 2.9.6

**Recommendation:** Update dependencies in a separate PR with thorough testing

### Modifier Parameter Warnings (13 warnings)
Composable functions have `modifier` parameter not as first optional parameter.

**Recommendation:** Refactor Composable signatures to follow Compose guidelines

### Target SDK Warning
```kotlin
targetSdk = 34  // Latest is 35
```

**Recommendation:** Update to SDK 35 after testing

---

## Action Plan

### Phase 1: Cleanup (Immediate)
1. ✅ Delete 19 stale files
2. ✅ Remove 28 unused string resources
3. ✅ Delete 93 empty localization folders
4. ✅ Fix AndroidManifest.xml redundant label
5. ✅ Add `<queries>` declaration to manifest

### Phase 2: Refactoring (Next Sprint)
1. Create `domain/usecase/` package structure
2. Implement `RpgStatsUseCase` (highest impact)
3. Implement `GeminiAIUseCase`
4. Implement `PreferencesUseCase`
5. Implement `JsonSerializationUseCase`
6. Refactor ViewModels to use new use cases
7. Add unit tests for use cases

### Phase 3: Optimization (Future)
1. Update dependencies to latest versions
2. Fix Modifier parameter warnings
3. Update targetSdk to 35
4. Consider ProGuard/R8 optimization

---

## Estimated Impact

### APK Size Reduction
- Unused resources: ~5-10 KB
- Empty folders: ~1 KB
- **Total:** ~6-11 KB

### Code Reduction
- Duplicated ViewModel logic: ~300-400 lines
- Improved maintainability: High
- Test coverage improvement: +15-20%

### Build Performance
- Fewer resources to process: ~2-3% faster builds
- Cleaner project structure: Better IDE performance

---

## Conclusion

The Never Zero app is well-structured with proper Single Activity Architecture and MVVM pattern. However, significant code duplication exists in ViewModels, particularly around RPG stats calculation and AI integration. Implementing shared use cases will dramatically improve maintainability and testability.

The unused resources and stale files are minor issues but should be cleaned up for professional code hygiene.

**Priority:** High for ViewModel refactoring, Medium for resource cleanup, Low for dependency updates.
