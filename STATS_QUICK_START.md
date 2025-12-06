# Stats Visualization - Quick Start

## 🎯 What Was Built

Two custom Canvas-based visualizations for the stats screen:

### 1. Spider/Radar Chart
- **Pentagon shape** showing 5 RPG stats
- **Translucent green fill** (Color.Green @ 30% alpha)
- **Primary color stroke** outline
- **White-bordered dots** at each vertex
- **Labels** with stat names and values

### 2. GitHub-Style Heatmap
- **365-day grid** of activity
- **Intensity-based coloring** (grey → green)
- **Month and day labels**
- **Legend** showing intensity scale
- **Stats summary** (active days, avg intensity, streak)

---

## 📁 Files Created

```
app/src/main/java/com/productivitystreak/ui/components/
├── SpiderChart.kt                    # Radar chart component
├── ContributionHeatmap.kt            # GitHub-style heatmap
└── StatsVisualizationPreviews.kt     # 8 preview variations

app/src/main/java/com/productivitystreak/ui/screens/stats/
└── StatsScreen.kt                    # Updated with new visualizations

Documentation:
├── STATS_VISUALIZATION_GUIDE.md      # Complete guide
└── STATS_QUICK_START.md              # This file
```

---

## 🚀 Usage

### Spider Chart
```kotlin
import com.productivitystreak.ui.components.SpiderChart
import com.productivitystreak.data.model.RpgStats

SpiderChart(
    rpgStats = RpgStats(
        strength = 7,
        intelligence = 8,
        charisma = 5,
        wisdom = 6,
        discipline = 9
    )
)
```

### Contribution Heatmap
```kotlin
import com.productivitystreak.ui.components.ContributionHeatmap
import java.time.LocalDate

val contributions = mapOf(
    LocalDate.now() to 0.9f,
    LocalDate.now().minusDays(1) to 0.7f,
    LocalDate.now().minusDays(2) to 0.0f
)

ContributionHeatmap(
    contributions = contributions,
    weeksToShow = 52
)
```

### Updated Stats Screen
```kotlin
StatsScreen(
    statsState = statsState,
    rpgStats = rpgStats,  // ← New parameter
    // ... other params
)
```

---

## 🎨 Visual Design

### Spider Chart
- **Size:** 240dp x 240dp
- **Grid:** 5 concentric levels
- **Fill:** Green @ 30% alpha
- **Stroke:** Primary color @ 2dp
- **Dots:** 6dp white + 4dp colored
- **Labels:** 12sp outside chart

### Heatmap
- **Cell Size:** 14dp x 14dp
- **Gap:** 3dp between cells
- **Weeks:** 52 (1 year)
- **Height:** ~120dp
- **Colors:** Grey (0%) → Green (100%)

---

## 📊 Data Integration

### Get RPG Stats
```kotlin
// In ViewModel
val rpgStats = rpgStatsUseCase.computeRpgStats(streaks)
_rpgStats.value = rpgStats
```

### Generate Contributions
```kotlin
fun generateContributions(streaks: List<Streak>): Map<LocalDate, Float> {
    val contributions = mutableMapOf<LocalDate, Float>()
    
    streaks.forEach { streak ->
        streak.history.forEach { record ->
            val date = LocalDate.parse(record.date)
            contributions[date] = record.completionFraction
        }
    }
    
    return contributions
}
```

---

## 🔍 Preview in Android Studio

1. Open `StatsVisualizationPreviews.kt`
2. Click **Split** or **Design** view
3. See 8 preview variations:
   - Spider Chart: Low, Balanced, Max, Specialized
   - Heatmap: Active, Sporadic, Perfect, New User

---

## ✅ Key Features

### Spider Chart
✅ Custom Canvas drawing  
✅ Pentagon shape (5 stats)  
✅ Translucent fill  
✅ Vertex dots  
✅ Stat labels  
✅ Grid reference lines  

### Heatmap
✅ 365-day grid  
✅ Intensity coloring  
✅ Month labels  
✅ Day labels  
✅ Legend  
✅ Stats summary  

### Stats Screen
✅ Scrollable layout  
✅ RPG stats card  
✅ Heatmap card  
✅ Level badge  
✅ XP progress bar  

---

## 🎯 Next Steps

1. **Test in app:**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

2. **View previews:**
   - Open `StatsVisualizationPreviews.kt`
   - Check all 8 variations

3. **Integrate with ViewModel:**
   - Pass `rpgStats` to StatsScreen
   - Generate contribution data from streaks

4. **Customize:**
   - Adjust colors in theme
   - Modify chart sizes
   - Add animations (optional)

---

## 📖 Full Documentation

See **STATS_VISUALIZATION_GUIDE.md** for:
- Detailed component documentation
- Integration examples
- Customization options
- Performance tips
- Troubleshooting
- Future enhancements

---

## 🐛 Common Issues

**Spider chart labels overlap?**
→ Increase chart size or reduce font size

**Heatmap cells too small?**
→ Adjust `cellSize` in Canvas drawing

**Stats screen not scrolling?**
→ Verify `verticalScroll(rememberScrollState())` is applied

**Colors don't match theme?**
→ Use `MaterialTheme.colorScheme` colors

---

## 💡 Tips

- Use `remember` for expensive calculations
- Cache contribution data in ViewModel
- Limit heatmap to 52 weeks for performance
- Test on different screen sizes
- Check dark mode appearance

---

**Created:** December 6, 2025  
**Status:** ✅ Ready to Use
