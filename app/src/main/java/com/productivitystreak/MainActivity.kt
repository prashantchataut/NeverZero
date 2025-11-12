package com.productivitystreak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.productivitystreak.ui.AppViewModel
import com.productivitystreak.ui.navigation.NeverZeroApp
import com.productivitystreak.ui.theme.ProductivityStreakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: AppViewModel by viewModels()
        setContent {
            ProductivityStreakTheme {
                val state = viewModel.uiState.collectAsStateWithLifecycle()
                Surface {
                    NeverZeroApp(
                        uiState = state.value,
                        onRefreshQuote = viewModel::refreshQuote,
                        onSelectStreak = viewModel::onSelectStreak,
                        onToggleTask = viewModel::onToggleTask,
                        onSimulateTaskCompletion = viewModel::simulateTaskCompletion,
                        onLogReadingProgress = viewModel::onLogReadingProgress,
                        onAddVocabularyWord = viewModel::onAddVocabularyWord,
                        onToggleOnboardingCategory = viewModel::onToggleOnboardingCategory,
                        onCompleteOnboarding = viewModel::onCompleteOnboarding,
                        onDismissOnboarding = viewModel::onDismissOnboarding,
                        onToggleNotifications = viewModel::onToggleNotifications,
                        onChangeReminderFrequency = viewModel::onChangeReminderFrequency,
                        onToggleWeeklySummary = viewModel::onToggleWeeklySummary,
                        onChangeTheme = viewModel::onChangeTheme
                    )
                }
            }
        }
    }
}
