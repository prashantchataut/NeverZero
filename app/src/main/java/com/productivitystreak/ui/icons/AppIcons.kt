package com.productivitystreak.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Centralized set of icons used across the app to keep the visual language consistent.
 */
object AppIcons {
    // Friendly heart for Health, matching our optimistic, habit-first tone
    val Health: ImageVector = Icons.Outlined.FavoriteBorder
    val Fitness: ImageVector = Icons.Outlined.FitnessCenter
    val Mindfulness: ImageVector = Icons.Outlined.SelfImprovement
    // School cap for general learning/skills
    val Learning: ImageVector = Icons.Outlined.School
    val Career: ImageVector = Icons.Outlined.Work
    val Finance: ImageVector = Icons.Outlined.Savings
    val Productivity: ImageVector = Icons.Outlined.AutoGraph
    val Analytics: ImageVector = Icons.Outlined.BarChart
    val BarChart: ImageVector = Icons.Outlined.BarChart
    val Search: ImageVector = Icons.Outlined.Search
    // Softer, continuous care vibe for wellness
    val Wellness: ImageVector = Icons.Outlined.MonitorHeart
    val Celebration: ImageVector = Icons.Outlined.Celebration
    // Vibration icon better represents haptic feedback than headphones
    val Haptics: ImageVector = Icons.Outlined.Vibration
    val Default: ImageVector = Icons.Outlined.CheckCircle
    
    // Stats & Leaderboard Icons
    val Crown: ImageVector = Icons.Rounded.EmojiEvents // Trophy/Crown for #1
    val FireStreak: ImageVector = Icons.Rounded.LocalFireDepartment // Flame for streaks
    val Lightning: ImageVector = Icons.Rounded.Bolt // Lightning for XP/energy
    val Seedling: ImageVector = Icons.Rounded.Spa // Zen/growth for beginner
    val TrendUp: ImageVector = Icons.AutoMirrored.Rounded.TrendingUp // Upward trend
    
    // Command Center Menu Icons (Thin-line/Outlined style)
    val AddHabit: ImageVector = Icons.Outlined.AddTask // Add new habit
    val AddWord: ImageVector = Icons.AutoMirrored.Outlined.MenuBook // Log vocabulary word
    val AddJournal: ImageVector = Icons.Outlined.EditNote // Journal entry
    val TeachWord: ImageVector = Icons.Outlined.QuestionAnswer // Teach/coach with AI

    private val categoryMap: Map<String, ImageVector> = mapOf(
        "health" to Health,
        "fitness" to Fitness,
        "mindfulness" to Mindfulness,
        "learning" to Learning,
        "career" to Career,
        "finance" to Finance,
        "productivity" to Productivity,
        "wellness" to Wellness,
        "celebration" to Celebration
    )

    fun forCategory(categoryId: String): ImageVector =
        categoryMap[categoryId.lowercase()] ?: Default
}
