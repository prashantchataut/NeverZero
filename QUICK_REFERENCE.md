# Quick Reference - Code Audit Results

## ✅ What Was Completed

### Files Deleted (20)
```
✓ All build logs (.txt files)
✓ Backup scripts (backup_overrides.ps1, cache_scan.ps1)
✓ Outdated docs (IMPROVEMENT_PLAN.md, UI_UX_OVERHAUL.md)
```

### Resources Cleaned
```
✓ 28 unused string resources removed from strings.xml
✓ AndroidManifest.xml: Removed redundant label
✓ AndroidManifest.xml: Added <queries> declaration
```

### New Code Created
```
✓ domain/usecase/RpgStatsUseCase.kt
✓ domain/usecase/GeminiAIUseCase.kt
✓ domain/usecase/JsonSerializationUseCase.kt
✓ Updated NeverZeroApplication.kt with use case initialization
```

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `CODE_AUDIT_REPORT.md` | Full audit report with detailed analysis |
| `VIEWMODEL_REFACTORING_GUIDE.md` | Step-by-step refactoring instructions |
| `AUDIT_SUMMARY.md` | Executive summary of changes |
| `QUICK_REFERENCE.md` | This file - quick lookup |

---

## 🎯 Use Cases - Quick Usage

### RpgStatsUseCase
```kotlin
// Get from Application
val app = context.applicationContext as NeverZeroApplication
val rpgStatsUseCase = app.rpgStatsUseCase

// Use in ViewModel
val rpgStats = rpgStatsUseCase.computeRpgStats(streaks)
val attribute = rpgStatsUseCase.mapCategoryToAttribute("Fitness")
```

### GeminiAIUseCase
```kotlin
// Get from Application
val geminiAIUseCase = app.geminiAIUseCase

// Use in ViewModel
val insight = geminiAIUseCase.generateBuddhaInsight(forceRefresh = false)
val feedback = geminiAIUseCase.generateJournalFeedback(text)
val errorMsg = geminiAIUseCase.handleAIError(exception)
```

### JsonSerializationUseCase
```kotlin
// Get from Application
val jsonUseCase = app.jsonSerializationUseCase

// Use in ViewModel
val json = jsonUseCase.serializeList(tasks)
val tasks = jsonUseCase.deserializeList<Task>(json) ?: emptyList()
```

---

## 🔧 Android Lint Commands

```bash
# Run lint analysis
./gradlew lint --no-daemon

# View HTML report
start app/build/reports/lint-results-debug.html

# View XML report (programmatic)
cat app/build/reports/lint-results-debug.xml
```

---

## 📊 Impact Summary

| Metric | Result |
|--------|--------|
| Stale files deleted | 20 |
| Unused resources removed | 28 |
| Manifest issues fixed | 2 |
| Use cases created | 3 |
| Lines to be saved (Phase 2) | ~308 |
| APK size reduction | ~5-10 KB |

---

## 🚀 Next Steps (Phase 2)

### Priority Order
1. StreakViewModel (~120 lines saved)
2. ProfileViewModel (~95 lines saved)
3. VocabularyViewModel (~40 lines saved)
4. ReadingViewModel (~20 lines saved)
5. JournalViewModel (~15 lines saved)
6. OnboardingViewModel (~10 lines saved)
7. BuddhaChatViewModel (~8 lines saved)

### For Each ViewModel
1. Add use case dependencies to constructor
2. Replace duplicated logic with use case calls
3. Delete old functions
4. Update factory/instantiation code
5. Test thoroughly
6. Commit

---

## ✅ Verification Checklist

### Phase 1 (Completed)
- [x] Lint analysis run
- [x] Unused resources identified
- [x] Stale files deleted
- [x] Manifest fixed
- [x] Use cases created
- [x] Application updated
- [x] Documentation written
- [x] No compilation errors

### Phase 2 (To Do)
- [ ] Refactor 7 ViewModels
- [ ] Write use case unit tests
- [ ] Run integration tests
- [ ] Manual UI testing
- [ ] Code review
- [ ] Merge to main

---

## 🐛 Troubleshooting

### If Build Fails
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

### If Use Cases Not Found
Check `NeverZeroApplication.kt` has:
```kotlin
lateinit var rpgStatsUseCase: RpgStatsUseCase
lateinit var geminiAIUseCase: GeminiAIUseCase
lateinit var jsonSerializationUseCase: JsonSerializationUseCase
```

### If ViewModel Crashes
Ensure factory passes use cases:
```kotlin
factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return XxxViewModel(
            // ... other dependencies
            rpgStatsUseCase = app.rpgStatsUseCase,
            geminiAIUseCase = app.geminiAIUseCase,
            jsonSerializationUseCase = app.jsonSerializationUseCase
        ) as T
    }
}
```

---

## 📞 Support

For detailed information, refer to:
- **Full analysis:** CODE_AUDIT_REPORT.md
- **Refactoring steps:** VIEWMODEL_REFACTORING_GUIDE.md
- **Summary:** AUDIT_SUMMARY.md

---

**Last Updated:** December 6, 2025  
**Status:** Phase 1 Complete ✅
