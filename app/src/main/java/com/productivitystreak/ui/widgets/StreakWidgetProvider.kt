package com.productivitystreak.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.productivitystreak.MainActivity
import com.productivitystreak.R
import com.productivitystreak.NeverZeroApplication
import com.productivitystreak.data.repository.StreakRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/**
 * Phase 4: Home Screen Widget
 * Quick glance at streaks without opening app
 */
class StreakWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val app = context.applicationContext as NeverZeroApplication
                    val streakRepository = app.streakRepository
                    
                    // Get active streaks
                    val streaks = streakRepository.observeStreaks().first()
                    val activeStreaks = streaks.filter { !it.isArchived }
                    
                    // Get top 3 streaks by current count
                    val topStreaks = activeStreaks.sortedByDescending { it.currentCount }.take(3)
                    
                    // Create an Intent to launch MainActivity
                    val intent = Intent(context, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    // Construct the RemoteViews object
                    val views = RemoteViews(context.packageName, R.layout.widget_streak_medium)
                    views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
                    
                    // Update widget content
                    if (topStreaks.isNotEmpty()) {
                        views.setTextViewText(R.id.widget_title, "Your Top Streaks")
                        
                        // First streak
                        if (topStreaks.size > 0) {
                            val streak1 = topStreaks[0]
                            views.setTextViewText(R.id.streak_1_name, streak1.name)
                            views.setTextViewText(R.id.streak_1_count, "${streak1.currentCount} days")
                            val progress1 = ((streak1.todayProgress.toFloat() / streak1.goalPerDay) * 100).toInt()
                            views.setProgressBar(R.id.streak_1_progress, 100, progress1, false)
                        }
                        
                        // Second streak
                        if (topStreaks.size > 1) {
                            val streak2 = topStreaks[1]
                            views.setTextViewText(R.id.streak_2_name, streak2.name)
                            views.setTextViewText(R.id.streak_2_count, "${streak2.currentCount} days")
                            val progress2 = ((streak2.todayProgress.toFloat() / streak2.goalPerDay) * 100).toInt()
                            views.setProgressBar(R.id.streak_2_progress, 100, progress2, false)
                        }
                        
                        // Third streak
                        if (topStreaks.size > 2) {
                            val streak3 = topStreaks[2]
                            views.setTextViewText(R.id.streak_3_name, streak3.name)
                            views.setTextViewText(R.id.streak_3_count, "${streak3.currentCount} days")
                            val progress3 = ((streak3.todayProgress.toFloat() / streak3.goalPerDay) * 100).toInt()
                            views.setProgressBar(R.id.streak_3_progress, 100, progress3, false)
                        }
                    } else {
                        views.setTextViewText(R.id.widget_title, "No Active Streaks")
                    }

                    // Instruct the widget manager to update the widget
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    // Fallback to empty state
                    val views = RemoteViews(context.packageName, R.layout.widget_streak_medium)
                    views.setTextViewText(R.id.widget_title, "Tap to open")
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}
